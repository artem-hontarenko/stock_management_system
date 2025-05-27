package org.stockmanager.service;


import org.stockmanager.model.*;
import org.stockmanager.storage.DataStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Manages a user's portfolio, transactions, and interactions with stock market data.
 * Provides functionality to deposit funds, buy and sell stocks, and maintain transaction history.
 */
public class PortfolioManager {
    private User user;
    private Portfolio portfolio;
    private List<Transaction> transactions; // User's transaction history
    private StockApiService apiService;
    private DataStorage dataStorage; // To persist changes

    /**
     * Constructs a new PortfolioManager with the given user, portfolio, transaction history,
     * stock API service, and data storage for persistence.
     *
     * @param user         The user whose portfolio is managed.
     * @param portfolio    The portfolio to manage.
     * @param transactions The user's transaction history.
     * @param apiService   The service for fetching stock market data.
     * @param dataStorage  The storage system for persisting changes.
     */
    public PortfolioManager(User user, Portfolio portfolio, List<Transaction> transactions, StockApiService apiService, DataStorage dataStorage) {
        this.user = Objects.requireNonNull(user, "User cannot be null");
        this.portfolio = Objects.requireNonNull(portfolio, "Portfolio cannot be null");
        // Ensure transactions list is mutable and not null
        this.transactions = (transactions != null) ? new ArrayList<>(transactions) : new ArrayList<>();
        this.apiService = Objects.requireNonNull(apiService, "StockApiService cannot be null");
        this.dataStorage = Objects.requireNonNull(dataStorage, "DataStorage cannot be null");
    }

    // --- Getters needed by ReportGenerator and App ---
    /**
     * Retrieves the user associated with this portfolio manager.
     *
     * @return The user being managed.
     */
    public User getUser() {
        return user;
    }

    /**
     * Retrieves the portfolio being managed.
     *
     * @return The user's portfolio.
     */
    public Portfolio getPortfolio() {
        return portfolio;
    }

    /**
     * Retrieves the transaction history of the user.
     *
     * @return A list of transactions associated with the user.
     */
    public List<Transaction> getTransactions() {
        // Return an unmodifiable list to prevent external modification
        return Collections.unmodifiableList(transactions);
    }
    // --- Core Actions ---

    /**
     * Deposits funds into the user's account.
     * @param amount The amount to deposit. Must be positive.
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            System.err.println("Deposit amount must be positive.");
            return;
        }
        user.deposit(amount);
        Transaction depositTx = new Transaction(TransactionType.DEPOSIT, LocalDateTime.now(), amount);
        transactions.add(depositTx);

        // Persist changes
        saveUserData();
        System.out.printf("Successfully deposited $%.2f. New balance: $%.2f%n", amount, user.getBalance());
    }

    /**
     * Calculates the maximum number of whole shares of a stock that can be bought with a given amount.
     *
     * @param symbol The stock symbol.
     * @param amount The amount of money available to spend.
     * @return The maximum number of whole shares, or -1 if the price cannot be fetched or is zero.
     */
    public int calculateMaxShares(String symbol, double amount) {
        if (amount <= 0) {
            return 0; // Cannot buy with zero or negative amount
        }
        Stock stock = apiService.getStockInfo(symbol);
        if (stock == null) {
            System.err.println("Could not fetch stock info for " + symbol + " to calculate max shares.");
            return -1; // Indicate error
        }
        double currentPrice = stock.getCurrentPrice();
        if (currentPrice <= 0) {
            System.err.println("Stock price for " + symbol + " is zero or negative. Cannot calculate max shares.");
            return -1; // Avoid division by zero or negative price issues
        }
        return (int) Math.floor(amount / currentPrice); // Only whole shares
    }


    /**
     * Buys a specified quantity of stock if funds are sufficient.
     *
     * @param symbol   The stock symbol to buy.
     * @param quantity The number of shares to buy.
     * @return true if the purchase was successful, false otherwise.
     */
    public boolean buyStock(String symbol, int quantity) {
        if (quantity <= 0) {
            System.err.println("Quantity to buy must be positive.");
            return false;
        }

        Stock stock = apiService.getStockInfo(symbol);
        if (stock == null) {
            System.err.println("Failed to buy stock. Could not fetch current price for " + symbol);
            return false;
        }

        double currentPrice = stock.getCurrentPrice();
        if (currentPrice <= 0) {
            System.err.println("Failed to buy stock. Invalid price ("+ String.format("%.2f", currentPrice) +") for " + symbol);
            return false;
        }

        double totalCost = quantity * currentPrice;

        if (user.getBalance() < totalCost) {
            System.err.printf("Failed to buy stock. Insufficient funds. Required: $%.2f, Available: $%.2f%n",
                    totalCost, user.getBalance());
            return false;
        }

        // Attempt withdrawal first
        if (user.withdraw(totalCost)) {
            // Withdrawal successful, now update portfolio
            portfolio.addOrUpdatePosition(symbol, quantity, currentPrice);

            // Record transaction
            Transaction buyTx = new Transaction(TransactionType.BUY, LocalDateTime.now(), symbol, quantity, currentPrice);
            transactions.add(buyTx);

            // Persist changes
            saveUserData();

            System.out.printf("Successfully bought %d shares of %s at $%.2f each. Total cost: $%.2f%n",
                    quantity, symbol.toUpperCase(), currentPrice, totalCost);
            System.out.printf("Remaining balance: $%.2f%n", user.getBalance());
            return true;
        } else {
            // Should not happen if initial check passed, but good for safety
            System.err.println("Failed to buy stock. Withdrawal failed unexpectedly.");
            return false;
        }
    }


    /**
     * Sells a specified quantity of stock if the user owns enough shares.
     *
     * @param symbol   The stock symbol to sell.
     * @param quantity The number of shares to sell.
     * @return true if the sale was successful, false otherwise.
     */
    public boolean sellStock(String symbol, int quantity) {
        if (quantity <= 0) {
            System.err.println("Quantity to sell must be positive.");
            return false;
        }

        String upperSymbol = symbol.toUpperCase();
        Position position = portfolio.getPosition(upperSymbol);

        if (position == null) {
            System.err.println("Failed to sell stock. You do not own any shares of " + upperSymbol);
            return false;
        }

        if (position.getQuantity() < quantity) {
            System.err.printf("Failed to sell stock. You only own %d shares of %s, but tried to sell %d.%n",
                    position.getQuantity(), upperSymbol, quantity);
            return false;
        }

        // Fetch current price to calculate proceeds
        Stock stock = apiService.getStockInfo(upperSymbol);
        if (stock == null) {
            System.err.println("Failed to sell stock. Could not fetch current price for " + upperSymbol);
            // Critical decision: Should we allow selling without a current price?
            // For now, let's disallow it to avoid selling at an unknown/stale price.
            return false;
        }
        double currentPrice = stock.getCurrentPrice();
        if (currentPrice <= 0) {
            System.err.println("Failed to sell stock. Invalid current market price ("+ String.format("%.2f", currentPrice) +") for " + upperSymbol);
            // Maybe allow selling even if price is 0 (e.g., worthless stock)? For now, disallow.
            return false;
        }

        // Update portfolio first (removes shares/position)
        boolean portfolioUpdated = portfolio.sellShares(upperSymbol, quantity);

        if (portfolioUpdated) {
            // Calculate proceeds and deposit funds
            double totalProceeds = quantity * currentPrice;
            user.deposit(totalProceeds); // Add proceeds to balance

            // Record transaction
            Transaction sellTx = new Transaction(TransactionType.SELL, LocalDateTime.now(), upperSymbol, quantity, currentPrice);
            transactions.add(sellTx);

            // Persist changes
            saveUserData();

            System.out.printf("Successfully sold %d shares of %s at $%.2f each. Total proceeds: $%.2f%n",
                    quantity, upperSymbol, currentPrice, totalProceeds);
            System.out.printf("New balance: $%.2f%n", user.getBalance());
            return true;
        } else {
            // Should not happen if initial checks passed, but good for safety
            System.err.println("Failed to sell stock. Portfolio update failed unexpectedly.");
            // Potentially need rollback logic here in a more complex system
            return false;
        }
    }


    // --- Helper Methods ---

    /** Saves the current user's state (user data, portfolio, transactions) */
    private void saveUserData() {
        dataStorage.saveUser(user);
        dataStorage.savePortfolio(user.getUsername(), portfolio);
        dataStorage.saveTransactions(user.getUsername(), transactions);
        // System.out.println("DEBUG: User data saved for " + user.getUsername()); // Optional debug log
    }
}

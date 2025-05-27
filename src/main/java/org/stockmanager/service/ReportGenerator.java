package org.stockmanager.service;

import org.stockmanager.model.Portfolio;
import org.stockmanager.model.Position;
import org.stockmanager.model.Stock;
import org.stockmanager.model.Transaction;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Responsible for generating and displaying various reports related to the user's portfolio,
 * transaction history, and profit/loss analysis. Utilizes portfolio and market data to provide insights.
 */

public class ReportGenerator {

    private final PortfolioManager portfolioManager;
    private final StockApiService apiService;

    /**
     * Constructs a new ReportGenerator with the specified PortfolioManager and StockApiService.
     *
     * @param portfolioManager The portfolio manager providing access to the user's data.
     * @param apiService       The service for fetching current stock market data.
     */
    public ReportGenerator(PortfolioManager portfolioManager, StockApiService apiService) {
        this.portfolioManager = portfolioManager;
        this.apiService = apiService;
    }

    /**
     * Displays a summary of the user's current portfolio holdings and cash balance.
     * Fetches current market prices for valuation.
     */
    public void displayPortfolioSummary() {
        System.out.println("\n--- Portfolio Summary for " + portfolioManager.getUser().getUsername() + " ---");
        Portfolio portfolio = portfolioManager.getPortfolio();
        Map<String, Position> positions = portfolio.getPositions();

        if (positions.isEmpty()) {
            System.out.println("No stock positions held.");
        } else {
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.printf("%-10s | %-10s | %-15s | %-15s | %-15s%n",
                    "Symbol", "Quantity", "Avg Buy Price", "Current Price", "Total Value");
            System.out.println("-------------------------------------------------------------------------------------");

            double totalPortfolioValue = 0.0;

            // Sort symbols alphabetically for consistent display
            List<String> sortedSymbols = positions.keySet().stream().sorted().toList();

            for (String symbol : sortedSymbols) {
                Position position = positions.get(symbol);
                Stock currentStock = apiService.getStockInfo(symbol); // Fetch latest price
                double currentPrice = (currentStock != null) ? currentStock.getCurrentPrice() : -1.0; // Indicate if fetch failed
                double positionValue = (currentPrice >= 0) ? position.getQuantity() * currentPrice : 0.0;
                totalPortfolioValue += positionValue;

                System.out.printf("%-10s | %-10d | $%-14.2f | %-15s | $%-14.2f%n",
                        position.getStockSymbol(),
                        position.getQuantity(),
                        position.getAverageBuyPrice(),
                        (currentPrice >= 0) ? String.format("$%.2f", currentPrice) : "N/A", // Show N/A if price unavailable
                        positionValue);
            }
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.printf("Total Stock Holdings Value: $%.2f%n", totalPortfolioValue);
        }

        System.out.printf("Cash Balance: $%.2f%n", portfolioManager.getUser().getBalance());
        System.out.println("-------------------------------------------------------------------------------------\n");
    }

    /**
     * Displays the user's transaction history, sorted by time (most recent first).
     */
    public void displayTransactionHistory() {
        System.out.println("\n--- Transaction History for " + portfolioManager.getUser().getUsername() + " ---");
        List<Transaction> transactions = portfolioManager.getTransactions();

        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded.");
        } else {
            // Sort transactions by timestamp, descending (most recent first)
            transactions.stream()
                    .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                    .forEach(System.out::println); // Uses Transaction.toString()
        }
        System.out.println("--------------------------------------------------\n");
    }

    /**
     * Displays a profit/loss report for each stock holding based on current market prices.
     */
    public void displayProfitLossReport() {
        System.out.println("\n--- Profit/Loss Report for " + portfolioManager.getUser().getUsername() + " ---");
        Portfolio portfolio = portfolioManager.getPortfolio();
        Map<String, Position> positions = portfolio.getPositions();

        if (positions.isEmpty()) {
            System.out.println("No stock positions held to calculate profit/loss.");
        } else {
            System.out.println("-----------------------------------------------------------------------------------------------------------");
            System.out.printf("%-10s | %-10s | %-15s | %-15s | %-15s | %-15s | %-10s%n",
                    "Symbol", "Quantity", "Avg Buy Price", "Current Price", "Total Cost", "Current Value", "P/L ($)", "P/L (%)");
            System.out.println("-----------------------------------------------------------------------------------------------------------");

            double totalPortfolioCost = 0.0;
            double totalPortfolioValue = 0.0;

            // Sort symbols alphabetically
            List<String> sortedSymbols = positions.keySet().stream().sorted().toList();

            for (String symbol : sortedSymbols) {
                Position position = positions.get(symbol);
                Stock currentStock = apiService.getStockInfo(symbol);
                double currentPrice = (currentStock != null) ? currentStock.getCurrentPrice() : -1.0;

                double avgBuyPrice = position.getAverageBuyPrice();
                int quantity = position.getQuantity();
                double totalCost = quantity * avgBuyPrice;
                double currentValue = (currentPrice >= 0) ? quantity * currentPrice : 0.0;
                double profitLoss = (currentPrice >= 0) ? currentValue - totalCost : -totalCost; // If price N/A, P/L is negative total cost
                double profitLossPercent = (totalCost > 0 && currentPrice >= 0) ? (profitLoss / totalCost) * 100.0 : (currentPrice < 0 ? -100.0 : 0.0);

                totalPortfolioCost += totalCost;
                totalPortfolioValue += currentValue;

                System.out.printf("%-10s | %-10d | $%-14.2f | %-15s | $%-14.2f | $%-14.2f | %s%-14.2f | %s%.2f%%%n",
                        position.getStockSymbol(),
                        quantity,
                        avgBuyPrice,
                        (currentPrice >= 0) ? String.format("$%.2f", currentPrice) : "N/A",
                        totalCost,
                        currentValue,
                        (profitLoss >= 0 ? "+" : ""), profitLoss, // Add '+' sign for positive P/L
                        (currentPrice >= 0 ? String.format("%s", profitLossPercent >= 0 ? "+" : "") : ""), // Sign for %
                        (currentPrice >= 0 ? profitLossPercent : (totalCost > 0 ? -100.0 : 0.0)) // Show -100% if price N/A and cost > 0
                );
            }
            System.out.println("-----------------------------------------------------------------------------------------------------------");
            double totalProfitLoss = totalPortfolioValue - totalPortfolioCost;
            double totalProfitLossPercent = (totalPortfolioCost > 0) ? (totalProfitLoss / totalPortfolioCost) * 100.0 : 0.0;
            System.out.printf("Overall Portfolio P/L: %s$%.2f (%s%.2f%%)%n",
                    (totalProfitLoss >= 0 ? "+" : ""), totalProfitLoss,
                    (totalProfitLossPercent >= 0 ? "+" : ""), totalProfitLossPercent);
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------\n");
    }
}
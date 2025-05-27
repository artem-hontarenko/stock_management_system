package org.stockmanager.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import java.io.Serializable;

/**
 * Represents a financial transaction, which can be a stock purchase, sale, or deposit.
 * Each transaction is uniquely identified and includes relevant details such as type,
 * timestamp, stock symbol, quantity, price, and the total amount.
 */
public class Transaction implements Serializable{
    private static final long serialVersionUID = 2L;
    private final String id;
    private final TransactionType type;
    private final LocalDateTime timestamp;
    private final String stockSymbol; // Null for deposits
    private final int quantity;      // 0 for deposits
    private final double price;       // Price per share for BUY/SELL, deposit amount for DEPOSIT
    private final double totalAmount; // Total cost for BUY, total proceeds for SELL, deposit amount for DEPOSIT

    /**
     * Constructs a new Transaction for a BUY or SELL operation.
     *
     * @param type          The transaction type (BUY or SELL).
     * @param timestamp     The date and time of the transaction.
     * @param stockSymbol   The stock symbol involved in the transaction.
     * @param quantity      The number of shares in the transaction.
     * @param pricePerShare The price per share for the transaction.
     */
    public Transaction(TransactionType type, LocalDateTime timestamp, String stockSymbol, int quantity, double pricePerShare) {
        if (type == TransactionType.DEPOSIT) {
            throw new IllegalArgumentException("Use the deposit constructor for DEPOSIT transactions.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive for BUY/SELL.");
        }
        if (pricePerShare < 0) {
            throw new IllegalArgumentException("Price per share cannot be negative.");
        }
        this.id = UUID.randomUUID().toString();
        this.type = Objects.requireNonNull(type, "Transaction type cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.stockSymbol = Objects.requireNonNull(stockSymbol, "Stock symbol cannot be null for BUY/SELL").toUpperCase();
        this.quantity = quantity;
        this.price = pricePerShare;
        this.totalAmount = quantity * pricePerShare;
    }

    /**
     * Constructs a new Transaction for a DEPOSIT.
     *
     * @param type          The transaction type (DEPOSIT).
     * @param timestamp     The date and time of the transaction.
     * @param depositAmount The amount deposited.
     */
    public Transaction(TransactionType type, LocalDateTime timestamp, double depositAmount) {
        if (type != TransactionType.DEPOSIT) {
            throw new IllegalArgumentException("Use the BUY/SELL constructor for non-DEPOSIT transactions.");
        }
        if (depositAmount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.stockSymbol = null;
        this.quantity = 0;
        this.price = depositAmount; // Store the deposit amount here
        this.totalAmount = depositAmount;
    }

    /**
     * Retrieves the unique identifier for this transaction.
     *
     * @return The transaction ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the type of this transaction (BUY, SELL, or DEPOSIT).
     *
     * @return The transaction type.
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Retrieves the timestamp of this transaction.
     *
     * @return The transaction timestamp.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Retrieves the stock symbol involved in this transaction (null for DEPOSIT).
     *
     * @return The stock symbol, or {@code null} if the transaction is a deposit.
     */
    public String getStockSymbol() {
        return stockSymbol;
    }

    /**
     * Retrieves the quantity of shares involved in this transaction (0 for DEPOSIT).
     *
     * @return The number of shares, or 0 if the transaction is a deposit.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Retrieves the price per share for this transaction (used for BUY or SELL, 0 for DEPOSIT).
     *
     * @return The price per share, or 0 if the transaction is a deposit.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Retrieves the total amount for this transaction.
     * For BUY, this is the total cost; for SELL, this is the total proceeds;
     * for DEPOSIT, this is the deposit amount.
     *
     * @return The total amount involved in the transaction.
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    /**
     * Returns a string representation of the transaction, summarizing its details.
     *
     * @return A string containing information about the transaction type,
     *         timestamp, stock symbol, quantity, price, and total amount.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = timestamp.format(formatter);
        switch (type) {
            case BUY:
                return String.format("[%s] BUY  %d %s @ $%.2f (Total: $%.2f) ID: %s",
                        formattedTimestamp, quantity, stockSymbol, price, totalAmount, id);
            case SELL:
                return String.format("[%s] SELL %d %s @ $%.2f (Total: $%.2f) ID: %s",
                        formattedTimestamp, quantity, stockSymbol, price, totalAmount, id);
            case DEPOSIT:
                return String.format("[%s] DEPOSIT $%.2f ID: %s",
                        formattedTimestamp, totalAmount, id);
            default:
                return "Unknown Transaction";
        }
    }
}

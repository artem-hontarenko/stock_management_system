package org.stockmanager.model;

import java.util.Objects;
import java.io.Serializable;

/**
 * Represents a stock position in a portfolio, including stock symbol, quantity,
 * and the average purchase price per share. Provides functionality to manage
 * and update the position.
 */
public class Position implements Serializable{
    private static final long serialVersionUID = 5L;
    private final String stockSymbol;
    private int quantity;
    private double averageBuyPrice; // Weighted average purchase price per share

    /**
     * Constructs a new Position with the specified stock symbol, initial quantity,
     * and initial purchase price.
     *
     * @param stockSymbol   The stock symbol for this position.
     * @param quantity      The initial quantity of shares.
     * @param purchasePrice The initial purchase price per share.
     */
    public Position(String stockSymbol, int quantity, double purchasePrice) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Initial quantity must be positive.");
        }
        if (purchasePrice < 0) {
            throw new IllegalArgumentException("Purchase price cannot be negative.");
        }
        this.stockSymbol = Objects.requireNonNull(stockSymbol, "Stock symbol cannot be null").toUpperCase();
        this.quantity = quantity;
        this.averageBuyPrice = purchasePrice;
    }

    /**
     * Retrieves the stock symbol of this position.
     *
     * @return The stock symbol associated with this position.
     */
    public String getStockSymbol() {
        return stockSymbol;
    }

    /**
     * Retrieves the total quantity of shares in this position.
     *
     * @return The quantity of shares.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Retrieves the weighted average purchase price per share for this position.
     *
     * @return The average purchase price per share.
     */
    public double getAverageBuyPrice() {
        return averageBuyPrice;
    }

    /**
     * Adds shares to this position and updates the average purchase price.
     *
     * @param sharesToAdd   The number of shares to add.
     * @param pricePerShare The price per share for the added shares.
     */
    public void addShares(int sharesToAdd, double pricePerShare) {
        if (sharesToAdd <= 0) {
            throw new IllegalArgumentException("Shares to add must be positive.");
        }
        if (pricePerShare < 0) {
            throw new IllegalArgumentException("Price per share cannot be negative.");
        }
        // Calculate new weighted average buy price
        double currentTotalValue = this.quantity * this.averageBuyPrice;
        double addedValue = sharesToAdd * pricePerShare;
        this.quantity += sharesToAdd;
        this.averageBuyPrice = (currentTotalValue + addedValue) / this.quantity;
    }

    /**
     * Removes a specified number of shares from this position.
     *
     * @param sharesToRemove The number of shares to remove.
     * @return {@code true} if the shares were successfully removed,
     *         or {@code false} if the position does not have enough shares.
     */
    public boolean removeShares(int sharesToRemove) {
        if (sharesToRemove <= 0) {
            throw new IllegalArgumentException("Shares to remove must be positive.");
        }
        if (this.quantity >= sharesToRemove) {
            this.quantity -= sharesToRemove;
            // Average buy price remains the same when selling
            return true; // Removal successful
        }
        return false; // Not enough shares to remove
    }

    /**
     * Returns a string representation of this position, summarizing its details.
     *
     * @return A string containing information about the stock symbol, quantity,
     *         and average purchase price.
     */
    @Override
    public String toString() {
        return "Position{" +
                "stockSymbol='" + stockSymbol + '\'' +
                ", quantity=" + quantity +
                ", averageBuyPrice=" + String.format("%.2f", averageBuyPrice) +
                '}';
    }

    /**
     * Compares this position to another object for equality.
     * Two positions are considered equal if their stock symbol, quantity,
     * and average buy price match.
     *
     * @param o The object to compare with this position.
     * @return {@code true} if the objects are equal, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Objects.equals(stockSymbol, position.stockSymbol);
    }

    /**
     * Computes the hash code for this position.
     *
     * @return The hash code based on the stock symbol, quantity,
     *         and average buy price.
     */
    @Override
    public int hashCode() {
        return Objects.hash(stockSymbol);
    }
}
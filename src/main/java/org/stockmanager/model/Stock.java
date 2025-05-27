package org.stockmanager.model;

import java.util.Objects;
import java.io.Serializable;

/**
 * Represents a stock with a symbol, optional name, and current price.
 * Provides methods to manage and update stock information.
 */
public class Stock implements Serializable{
    private static final long serialVersionUID = 3L;
    private String symbol;
    private String name; // Optional, might not always be available easily from price APIs
    private double currentPrice;

    /**
     * Constructs a new Stock with the specified symbol, name, and initial price.
     *
     * @param symbol       The stock symbol.
     * @param name         The name of the stock (optional, can be null).
     * @param currentPrice The current price of the stock.
     */
    public Stock(String symbol, String name, double currentPrice) {
        this.symbol = Objects.requireNonNull(symbol, "Symbol cannot be null").toUpperCase();
        this.name = (name != null && !name.trim().isEmpty()) ? name : "N/A"; // Handle potential null/empty name
        if (currentPrice < 0) {
            throw new IllegalArgumentException("Current price cannot be negative.");
        }
        this.currentPrice = currentPrice;
    }

    /**
     * Constructs a new Stock with only the specified symbol.
     * Other attributes may be set at a later time.
     *
     * @param symbol The stock symbol.
     */
    public Stock(String symbol) {
        this(symbol, "N/A", 0.0); // Default price to 0 until fetched
    }

    /**
     * Retrieves the stock symbol.
     *
     * @return The symbol of the stock.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Retrieves the name of the stock.
     *
     * @return The name of the stock, or null if not set.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the current price of the stock.
     *
     * @return The current price of the stock.
     */
    public double getCurrentPrice() {
        return currentPrice;
    }

    /**
     * Updates the current price of the stock.
     *
     * @param currentPrice The new price of the stock.
     */
    public void setCurrentPrice(double currentPrice) {
        if (currentPrice < 0) {
            // Log a warning or handle appropriately, but maybe don't throw exception
            // if API temporarily glitches. Setting to 0 might be safer.
            System.err.println("Warning: Attempted to set negative price for " + symbol + ". Setting to 0.");
            this.currentPrice = 0.0;
        } else {
            this.currentPrice = currentPrice;
        }
    }

    /**
     * Updates the name of the stock.
     *
     * @param name The new name of the stock.
     */
    public void setName(String name) {
        this.name = (name != null && !name.trim().isEmpty()) ? name : "N/A";
    }

    /**
     * Returns a string representation of the stock, containing details
     * such as the symbol, name, and current price.
     *
     * @return A string summarizing the stock's information.
     */
    @Override
    public String toString() {
        return "Stock{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", currentPrice=" + String.format("%.2f", currentPrice) +
                '}';
    }

    /**
     * Compares this stock to another object for equality.
     * Two stocks are considered equal if their symbols, names,
     * and prices are the same.
     *
     * @param o The object to compare with this stock.
     * @return {@code true} if the objects are equal, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Objects.equals(symbol, stock.symbol);
    }

    /**
     * Computes the hash code for this stock.
     *
     * @return The hash code based on the stock's symbol, name,
     *         and current price.
     */
    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}
package org.stockmanager.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.io.Serializable;

/**
 * Represents a portfolio of stock positions, where each position is mapped to a stock symbol.
 * Allows adding, updating, and selling positions in the portfolio.
 */
public class Portfolio implements Serializable{
    private static final long serialVersionUID = 4L;
    // Map: Stock Symbol (String) -> Position object
    private final Map<String, Position> positions;

    /**
     * Constructs a new Portfolio object. Initializes an empty collection of stock positions.
     */
    public Portfolio() {
        this.positions = new HashMap<>();
    }

    /**
     * Retrieves an immutable view of the current stock positions.
     *
     * @return A map of stock symbols to their respective positions.
     */
    public Map<String, Position> getPositions() {
        return Collections.unmodifiableMap(positions);
    }

    /**
     * Fetches the position for a specific stock symbol.
     *
     * @param symbol The stock symbol to retrieve the position for.
     * @return The {@code Position} object corresponding to the given stock symbol,
     *         or {@code null} if no such position exists.
     */
    public Position getPosition(String symbol) {
        return positions.get(symbol.toUpperCase());
    }

    /**
     * Adds or updates a position when buying shares.
     * If the position exists, it updates the quantity and average buy price.
     * If it doesn't exist, it creates a new position.
     *
     * @param symbol Stock symbol
     * @param quantity Number of shares bought
     * @param pricePerShare Price paid per share
     */
    public void addOrUpdatePosition(String symbol, int quantity, double pricePerShare) {
        Objects.requireNonNull(symbol, "Symbol cannot be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        if (pricePerShare < 0) {
            throw new IllegalArgumentException("Price per share cannot be negative.");
        }

        String upperSymbol = symbol.toUpperCase();
        Position existingPosition = positions.get(upperSymbol);

        if (existingPosition != null) {
            existingPosition.addShares(quantity, pricePerShare);
        } else {
            positions.put(upperSymbol, new Position(upperSymbol, quantity, pricePerShare));
        }
    }

    /**
     * Reduces the quantity of shares in a position when selling.
     * If the quantity reaches zero, the position is removed.
     *
     * @param symbol Stock symbol
     * @param quantity Number of shares sold
     * @return true if the sale was successful (enough shares existed), false otherwise.
     */
    public boolean sellShares(String symbol, int quantity) {
        Objects.requireNonNull(symbol, "Symbol cannot be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        String upperSymbol = symbol.toUpperCase();
        Position position = positions.get(upperSymbol);

        if (position == null) {
            System.err.println("Error: Cannot sell stock '" + upperSymbol + "'. Position not found.");
            return false; // No position to sell from
        }

        boolean removed = position.removeShares(quantity);
        if (removed) {
            if (position.getQuantity() == 0) {
                positions.remove(upperSymbol); // Remove position if empty
            }
            return true;
        } else {
            System.err.println("Error: Cannot sell " + quantity + " shares of '" + upperSymbol + "'. Only " + position.getQuantity() + " owned.");
            return false; // Not enough shares
        }
    }

    /**
     * Returns a string representation of the portfolio, including details of all positions.
     *
     * @return A string summarizing the portfolio and its stock positions.
     */
    @Override
    public String toString() {
        return "Portfolio{" +
                "positions=" + positions +
                '}';
    }
}
package org.stockmanager.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Stock class.
 */
public class StockTest {

    @Test
    void testConstructorWithAllFields() {
        // Arrange & Act
        Stock stock = new Stock("AAPL", "Apple Inc.", 150.0);

        // Assert
        assertEquals("AAPL", stock.getSymbol());
        assertEquals("Apple Inc.", stock.getName());
        assertEquals(150.0, stock.getCurrentPrice());
    }

    @Test
    void testConstructorWithSymbolOnly() {
        // Arrange & Act
        Stock stock = new Stock("TSLA");

        // Assert
        assertEquals("TSLA", stock.getSymbol());
        assertEquals("N/A", stock.getName());
        assertEquals(0.0, stock.getCurrentPrice()); // Assuming default price is 0.0
    }

    @Test
    void testSetName() {
        // Arrange
        Stock stock = new Stock("MSFT");

        // Act
        stock.setName("Microsoft Corporation");

        // Assert
        assertEquals("Microsoft Corporation", stock.getName());
    }

    @Test
    void testSetCurrentPrice() {
        // Arrange
        Stock stock = new Stock("GOOGL");

        // Act
        stock.setCurrentPrice(2800.0);

        // Assert
        assertEquals(2800.0, stock.getCurrentPrice());
    }

    @Test
    void testToString() {
        // Arrange
        Stock stock = new Stock("AAPL", "Apple Inc.", 150.0);

        // Act
        String stockString = stock.toString();

        // Assert
        assertNotNull(stockString);
        assertTrue(stockString.contains("AAPL"));
        assertTrue(stockString.contains("Apple Inc."));
        assertTrue(stockString.contains("150.0"));
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Stock stock1 = new Stock("AAPL", "Apple Inc.", 150.0);
        Stock stock2 = new Stock("AAPL", "Apple Inc.", 150.0);
        Stock stock3 = new Stock("GOOGL", "Google LLC", 2800.0);

        // Act & Assert
        assertEquals(stock1, stock2);
        assertNotEquals(stock1, stock3);
        assertEquals(stock1.hashCode(), stock2.hashCode());
        assertNotEquals(stock1.hashCode(), stock3.hashCode());
    }

    @Test
    void testSettersAndEquality() {
        // Arrange
        Stock stock = new Stock("NFLX");
        stock.setCurrentPrice(400.0);
        stock.setName("Netflix Inc.");

        Stock updatedStock = new Stock("NFLX", "Netflix Inc.", 400.0);

        // Act & Assert
        assertEquals(stock, updatedStock);
    }
}
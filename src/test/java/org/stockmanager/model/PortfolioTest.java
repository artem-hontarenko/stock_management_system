package org.stockmanager.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Unit tests for the Portfolio class.
 */
public class PortfolioTest {

    @Test
    void testConstructorAndInitialization() {
        // Arrange & Act
        Portfolio portfolio = new Portfolio();

        // Assert
        assertNotNull(portfolio.getPositions());
        assertTrue(portfolio.getPositions().isEmpty());
    }

    @Test
    void testAddOrUpdatePosition_NewPosition() {
        // Arrange
        Portfolio portfolio = new Portfolio();

        // Act
        portfolio.addOrUpdatePosition("AAPL", 10, 150.0);

        // Assert
        Position position = portfolio.getPosition("AAPL");
        assertNotNull(position);
        assertEquals(10, position.getQuantity());
        assertEquals(150.0, position.getAverageBuyPrice());
    }

    @Test
    void testAddOrUpdatePosition_UpdateExistingPosition() {
        // Arrange
        Portfolio portfolio = new Portfolio();
        portfolio.addOrUpdatePosition("AAPL", 10, 150.0);

        // Act
        portfolio.addOrUpdatePosition("AAPL", 5, 200.0);

        // Assert
        Position position = portfolio.getPosition("AAPL");
        assertNotNull(position);
        assertEquals(15, position.getQuantity());
        // Check that average buy price has been updated correctly
        assertEquals(166.67, position.getAverageBuyPrice(), 0.01);
    }

    @Test
    void testGetPosition_NonExistentPosition() {
        // Arrange
        Portfolio portfolio = new Portfolio();

        // Act
        Position position = portfolio.getPosition("MSFT");

        // Assert
        assertNull(position);
    }

    @Test
    void testSellShares_Success() {
        // Arrange
        Portfolio portfolio = new Portfolio();
        portfolio.addOrUpdatePosition("GOOGL", 20, 2500.0);

        // Act
        boolean result = portfolio.sellShares("GOOGL", 10);

        // Assert
        assertTrue(result);
        Position position = portfolio.getPosition("GOOGL");
        assertNotNull(position);
        assertEquals(10, position.getQuantity());
    }

    @Test
    void testSellShares_InsufficientShares() {
        // Arrange
        Portfolio portfolio = new Portfolio();
        portfolio.addOrUpdatePosition("AMZN", 5, 3500.0);

        // Act
        boolean result = portfolio.sellShares("AMZN", 10);

        // Assert
        assertFalse(result);
        Position position = portfolio.getPosition("AMZN");
        assertNotNull(position);
        assertEquals(5, position.getQuantity());
    }

    @Test
    void testSellShares_RemovePositionWhenZeroQuantity() {
        // Arrange
        Portfolio portfolio = new Portfolio();
        portfolio.addOrUpdatePosition("TSLA", 10, 900.0);

        // Act
        boolean result = portfolio.sellShares("TSLA", 10);

        // Assert
        assertTrue(result);
        assertNull(portfolio.getPosition("TSLA"));
        assertFalse(portfolio.getPositions().containsKey("TSLA"));
    }

    @Test
    void testToString() {
        // Arrange
        Portfolio portfolio = new Portfolio();
        portfolio.addOrUpdatePosition("AAPL", 10, 150.0);
        portfolio.addOrUpdatePosition("GOOGL", 5, 2500.0);

        // Act
        String portfolioString = portfolio.toString();

        // Assert
        assertNotNull(portfolioString); // Make sure it's not null
        assertTrue(portfolioString.contains("AAPL")); // Check specific symbols
        assertTrue(portfolioString.contains("GOOGL"));
    }

}
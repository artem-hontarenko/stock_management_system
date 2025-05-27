package org.stockmanager.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for the User class.
 */
public class UserTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange & Act
        User user = new User("testUser", 100.0);

        // Assert
        assertEquals("testUser", user.getUsername());
        assertEquals(100.0, user.getBalance());
    }

    @Test
    void testDeposit() {
        // Arrange
        User user = new User("testUser", 100.0);

        // Act
        user.deposit(50.0);

        // Assert
        assertEquals(150.0, user.getBalance());
    }

    @Test
    void testWithdrawSuccess() {
        // Arrange
        User user = new User("testUser", 100.0);

        // Act
        boolean result = user.withdraw(50.0);

        // Assert
        assertTrue(result);
        assertEquals(50.0, user.getBalance());
    }

    @Test
    void testWithdrawFailure() {
        // Arrange
        User user = new User("testUser", 100.0);

        // Act
        boolean result = user.withdraw(150.0);

        // Assert
        assertFalse(result);
        assertEquals(100.0, user.getBalance());
    }

    @Test
    void testToString() {
        // Arrange
        User user = new User("testUser", 100.0);

        // Act
        String userString = user.toString();

        // Assert
        assertNotNull(userString);
        assertTrue(userString.contains("testUser"));
        assertTrue(userString.contains("100.0"));
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        User user1 = new User("testUser", 100.0);
        User user2 = new User("testUser", 100.0);
        User user3 = new User("anotherUser", 200.0);

        // Act & Assert
        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }
}
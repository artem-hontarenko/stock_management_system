package org.stockmanager.model;

import java.util.Objects;
import java.io.Serializable;

/**
 * Represents a user in the system, including their username and available balance.
 * Provides methods to manage and update the user's balance.
 */
public class User implements Serializable{
    private static final long serialVersionUID = 1L;
    private String username;
    private double balance;

    /**
     * Constructs a new User with the specified username and initial balance.
     *
     * @param username      The username of the user.
     * @param initialBalance The initial balance of the user in fiat currency.
     */
    public User(String username, double initialBalance) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.username = username;
        this.balance = initialBalance;
    }

    /**
     * Retrieves the username of the user.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Retrieves the current balance of the user.
     *
     * @return The current balance of the user.
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Deposits a specified amount into the user's balance.
     *
     * @param amount The amount to deposit.
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        this.balance += amount;
    }

    /**
     * Withdraws a specified amount from the user's balance, if sufficient funds are available.
     *
     * @param amount The amount to withdraw.
     * @return {@code true} if the withdrawal was successful,
     *         or {@code false} if there were insufficient funds.
     */
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    /**
     * Returns a string representation of the user, summarizing their details.
     *
     * @return A string containing information about the user's username and balance.
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", balance=" + String.format("%.2f", balance) +
                '}';
    }

    /**
     * Compares this user to another object for equality.
     * Two users are considered equal if their usernames and balances match.
     *
     * @param o The object to compare with this user.
     * @return {@code true} if the objects are equal, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    /**
     * Computes the hash code for this user.
     *
     * @return The hash code based on the user's username and balance.
     */
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}


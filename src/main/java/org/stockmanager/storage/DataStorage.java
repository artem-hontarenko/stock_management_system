package org.stockmanager.storage;

import org.stockmanager.model.Portfolio;
import org.stockmanager.model.Transaction;
import org.stockmanager.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides simple file-based persistent storage for user data, portfolios, and transactions
 * using Java Serialization.
 * Designed for use in applications where data persistence is required.
 */
public class DataStorage {

    private Map<String, User> users = new HashMap<>();
    private Map<String, Portfolio> portfolios = new HashMap<>(); // Keyed by username
    private Map<String, List<Transaction>> transactions = new HashMap<>(); // Keyed by username

    private static final String USERS_FILE = "users.dat";
    private static final String PORTFOLIOS_FILE = "portfolios.dat";
    private static final String TRANSACTIONS_FILE = "transactions.dat";

    /**
     * Constructs a new DataStorage instance and loads existing data from files.
     */
    public DataStorage() {
        loadAllData();
    }

    // --- Data Loading ---
    @SuppressWarnings("unchecked")
    private <T> T loadDataFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            // Return a new empty map if the file doesn't exist (e.g., first run)
            if (filePath.equals(USERS_FILE) || filePath.equals(PORTFOLIOS_FILE) || filePath.equals(TRANSACTIONS_FILE)) {
                return (T) new HashMap<String, Object>();
            }
            return null; // Should not happen with current file paths
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (T) ois.readObject();
        } catch (FileNotFoundException e) {
            System.err.println("Data file not found (will be created on next save): " + filePath);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data from " + filePath + ": " + e.getMessage());
            // In case of corruption, might be better to start fresh or handle error more gracefully
        }
        // Return a new empty map if loading failed for HashMap types
        if (filePath.equals(USERS_FILE) || filePath.equals(PORTFOLIOS_FILE) || filePath.equals(TRANSACTIONS_FILE)) {
            return (T) new HashMap<String, Object>();
        }
        return null;
    }

    private void loadAllData() {
        Map<String, User> loadedUsers = loadDataFromFile(USERS_FILE);
        if (loadedUsers != null) {
            this.users = loadedUsers;
        } else {
            this.users = new HashMap<>(); // Ensure it's initialized
        }

        Map<String, Portfolio> loadedPortfolios = loadDataFromFile(PORTFOLIOS_FILE);
        if (loadedPortfolios != null) {
            this.portfolios = loadedPortfolios;
        } else {
            this.portfolios = new HashMap<>(); // Ensure it's initialized
        }

        Map<String, List<Transaction>> loadedTransactions = loadDataFromFile(TRANSACTIONS_FILE);
        if (loadedTransactions != null) {
            this.transactions = loadedTransactions;
        } else {
            this.transactions = new HashMap<>(); // Ensure it's initialized
        }
        System.out.println("Data loaded from files.");
    }

    // --- Data Persistence ---
    private synchronized void saveDataToFile(String filePath, Object data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Error saving data to " + filePath + ": " + e.getMessage());
            e.printStackTrace(); // For debugging
        }
    }

    // --- User Data ---
    /**
     * Saves or updates a user's data in the storage and persists it to a file.
     *
     * @param user The user object to save.
     */
    public synchronized void saveUser(User user) {
        if (user != null) {
            users.put(user.getUsername(), user);
            saveDataToFile(USERS_FILE, users);
        }
    }

    /**
     * Retrieves a user's data by their username.
     *
     * @param username The username of the user.
     * @return The {@code User} object, or {@code null} if the user does not exist.
     */
    public synchronized User getUser(String username) {
        return users.get(username);
    }

    // --- Portfolio Data ---
    /**
     * Saves or updates a portfolio for a specified user and persists it to a file.
     *
     * @param username  The username of the user.
     * @param portfolio The portfolio to save.
     */
    public synchronized void savePortfolio(String username, Portfolio portfolio) {
        if (username != null && portfolio != null) {
            portfolios.put(username, portfolio);
            saveDataToFile(PORTFOLIOS_FILE, portfolios);
        }
    }

    /**
     * Retrieves the portfolio associated with a specific user.
     * If no portfolio is found, a new empty Portfolio is returned (but not saved until modified).
     *
     * @param username The username of the user.
     * @return The {@code Portfolio} object, or a new empty one if none exists for the user.
     */
    public synchronized Portfolio getPortfolio(String username) {
        // Return the portfolio, or a new empty one if none exists for the user
        return portfolios.getOrDefault(username, new Portfolio());
    }


    // --- Transaction Data ---
    /**
     * Saves or updates transaction history for a user and persists it to a file.
     *
     * @param username         The username of the user.
     * @param userTransactions The list of transactions to save.
     */
    public synchronized void saveTransactions(String username, List<Transaction> userTransactions) {
        if (username != null && userTransactions != null) {
            // Store a copy to avoid issues if the original list passed in is modified elsewhere
            transactions.put(username, new ArrayList<>(userTransactions));
            saveDataToFile(TRANSACTIONS_FILE, transactions);
        }
    }

    /**
     * Retrieves the transaction history for a specified user.
     *
     * @param username The username of the user.
     * @return A list of {@code Transaction} objects, or an empty list if no transactions exist.
     */
    public synchronized List<Transaction> getTransactions(String username) {
        // Return a copy to prevent external modification
        return new ArrayList<>(transactions.getOrDefault(username, new ArrayList<>()));
    }

    // --- Utility ---
    /**
     * Checks whether a user exists in the storage.
     *
     * @param username The username to check.
     * @return {@code true} if the user exists, otherwise {@code false}.
     */
    public synchronized boolean userExists(String username) {
        return users.containsKey(username);
    }
}
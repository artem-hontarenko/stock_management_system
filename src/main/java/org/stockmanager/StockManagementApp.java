package org.stockmanager;

import org.stockmanager.model.Portfolio;
import org.stockmanager.model.User;
import org.stockmanager.model.Transaction; // Make sure Transaction is imported
import org.stockmanager.model.Position; // Assuming Position class exists for portfolio.getPosition()
import org.stockmanager.model.Stock;    // Assuming Stock class exists for apiService.getStockInfo()
import org.stockmanager.model.TransactionType; // Assuming TransactionType enum exists

import org.stockmanager.service.PortfolioManager;
import org.stockmanager.service.ReportGenerator;
import org.stockmanager.service.StockApiService;
import org.stockmanager.storage.DataStorage;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDateTime; // For new Transaction

/**
 * The main application class for managing a user's stock investments and portfolio.
 * Provides options for user login/creation, viewing stock quotes, buying/selling stocks,
 * depositing funds, and generating reports.
 */
public class StockManagementApp {
    private Scanner scanner;
    private User currentUser;
    private PortfolioManager portfolioManager;
    private ReportGenerator reportGenerator;
    private StockApiService apiService;
    private DataStorage dataStorage;

    private static final String CANCEL_KEYWORD = "cancel";

    /**
     * The entry point for the Stock Management application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        StockManagementApp app = new StockManagementApp();
        app.start();
    }

    /**
     * Starts the application, handling the initialization of components and user interactions.
     */
    public void start() {
        scanner = new Scanner(System.in);
        apiService = new StockApiService(); // Initialize API service
        dataStorage = new DataStorage();    // Initialize data storage

        System.out.println("Welcome to the Stock Management System!");

        if (!handleLoginOrCreateUser()) {
            System.out.println("Exiting application.");
            scanner.close(); // Close scanner on exit
            return; // Exit if login fails
        }

        // Load existing portfolio and transactions or create new ones
        Portfolio userPortfolio = dataStorage.getPortfolio(currentUser.getUsername());
        List<Transaction> userTransactions = dataStorage.getTransactions(currentUser.getUsername());

        // Ensure PortfolioManager is initialized with potentially loaded/newly created user data
        portfolioManager = new PortfolioManager(currentUser, userPortfolio, userTransactions, apiService, dataStorage);
        reportGenerator = new ReportGenerator(portfolioManager, apiService);


        boolean running = true;
        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim(); // Trim whitespace from input
            running = processCommand(choice);
        }

        System.out.println("Exiting Stock Management System. Goodbye, " + currentUser.getUsername() + "!");
        scanner.close(); // Close the scanner when the application exits
    }

    /**
     * Handles user login or account creation. Prompts the user for input and manages authentication or registration.
     *
     * @return {@code true} if the user successfully logs in or creates an account, {@code false} otherwise.
     */
    private boolean handleLoginOrCreateUser() {
        while (true) {
            System.out.print("Enter username (or type 'exit' to quit): ");
            String username = scanner.nextLine().trim();

            if (username.equalsIgnoreCase("exit")) {
                return false; // User chose to quit
            }
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty.");
                continue;
            }

            if (dataStorage.userExists(username)) {
                // User exists, load their data
                currentUser = dataStorage.getUser(username);
                System.out.println("Welcome back, " + username + "!");
                // Portfolio and transactions for the existing user will be (re)loaded
                // or confirmed when PortfolioManager is initialized/re-initialized.
                // If switching users, ensure PortfolioManager is updated.
                Portfolio userPortfolio = dataStorage.getPortfolio(currentUser.getUsername());
                List<Transaction> userTransactions = dataStorage.getTransactions(currentUser.getUsername());
                portfolioManager = new PortfolioManager(currentUser, userPortfolio, userTransactions, apiService, dataStorage);
                reportGenerator = new ReportGenerator(portfolioManager, apiService); // Re-init with new user's manager
                return true;
            } else {
                // User does not exist, prompt to create
                System.out.print("User '" + username + "' not found. Create new user? (yes/no): ");
                String createChoice = scanner.nextLine().trim();
                if (createChoice.equalsIgnoreCase("yes") || createChoice.equalsIgnoreCase("y")) {
                    double initialDeposit = -1;
                    String depositInput;
                    boolean validInput = false;
                    while(!validInput) {
                        System.out.print("Enter initial deposit amount ($) (or type '"+ CANCEL_KEYWORD +"' to cancel user creation): ");
                        depositInput = scanner.nextLine().trim();
                        if (depositInput.equalsIgnoreCase(CANCEL_KEYWORD)) {
                            System.out.println("User creation cancelled.");
                            break; // Exit deposit loop, will go back to username prompt
                        }
                        try {
                            initialDeposit = Double.parseDouble(depositInput);
                            if (initialDeposit < 0) {
                                System.out.println("Initial deposit cannot be negative.");
                            } else {
                                validInput = true;
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid input. Please enter a number or '" + CANCEL_KEYWORD + "'.");
                        }
                    }

                    if (!validInput) { // User cancelled deposit input during creation
                        continue; // Go back to "Enter username"
                    }

                    // Create new user, portfolio, and transaction list
                    currentUser = new User(username, initialDeposit);
                    Portfolio newPortfolio = new Portfolio();
                    List<Transaction> newTransactions = new java.util.ArrayList<>(); // Empty list

                    // Add initial deposit transaction if amount > 0
                    if (initialDeposit > 0) {
                        // Assuming Transaction constructor and TransactionType.DEPOSIT exist
                        Transaction depositTx = new Transaction(TransactionType.DEPOSIT, LocalDateTime.now(), initialDeposit);
                        newTransactions.add(depositTx);
                    }

                    // Save the new user and their empty portfolio/transactions
                    dataStorage.saveUser(currentUser);
                    dataStorage.savePortfolio(username, newPortfolio);
                    dataStorage.saveTransactions(username, newTransactions);

                    System.out.println("User '" + username + "' created successfully with initial balance: $" + String.format("%.2f", initialDeposit));
                    // Initialize PortfolioManager and ReportGenerator for the new user
                    portfolioManager = new PortfolioManager(currentUser, newPortfolio, newTransactions, apiService, dataStorage);
                    reportGenerator = new ReportGenerator(portfolioManager, apiService);
                    return true;
                } else {
                    System.out.println("User creation cancelled by choice.");
                    // Loop back to ask for username again
                }
            }
        }
    }

    /**
     * Displays the main menu for the application, allowing the user to choose from various actions.
     */
    private void displayMenu() {
        System.out.println("\n===== STOCK MANAGEMENT SYSTEM MENU =====");
        System.out.println("User: " + currentUser.getUsername() + " | Balance: $" + String.format("%.2f", currentUser.getBalance()));
        System.out.println("----------------------------------------");
        System.out.println("1. View Portfolio Summary");
        System.out.println("2. View Transaction History");
        System.out.println("3. View Profit/Loss Report");
        System.out.println("4. Get Stock Price");
        System.out.println("5. Buy Stock");
        System.out.println("6. Sell Stock");
        System.out.println("7. Deposit Funds");
        System.out.println("8. Exit Application");
        System.out.println("----------------------------------------");
        System.out.print("Enter your choice (1-8): ");
    }

    /**
     * Processes a user's menu choice and executes the corresponding operation.
     *
     * @param choice The user's menu choice.
     * @return {@code true} to continue the application, or {@code false} to exit.
     */
    private boolean processCommand(String choice) {
        switch (choice) {
            case "1":
                reportGenerator.displayPortfolioSummary();
                break;
            case "2":
                reportGenerator.displayTransactionHistory();
                break;
            case "3":
                reportGenerator.displayProfitLossReport();
                break;
            case "4":
                handleGetStockPrice();
                break;
            case "5":
                handleBuyStock();
                break;
            case "6":
                handleSellStock();
                break;
            case "7":
                handleDepositFunds();
                break;
            case "8":
                System.out.println("Exiting the application. Goodbye!");
                return false;
            default:
                System.out.println("Invalid choice '" + choice + "'. Please enter a number between 1 and 8.");
                break;
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();

        return true;
    }

    /**
     * Handles the functionality for retrieving and displaying a live stock quote.
     * Prompts the user for a stock symbol and fetches the latest price and details.
     */
    private void handleGetStockPrice() {
        System.out.print("Enter stock symbol to get stock price (or type '" + CANCEL_KEYWORD + "' to abort): ");
        String symbolInput = scanner.nextLine().trim();

        if (symbolInput.equalsIgnoreCase(CANCEL_KEYWORD)) {
            System.out.println("Get stock price operation cancelled.");
            return;
        }
        if (symbolInput.isEmpty()) {
            System.out.println("Stock symbol cannot be empty.");
            return;
        }
        String symbol = symbolInput.toUpperCase();
        Stock stock = apiService.getStockInfo(symbol);
        if (stock != null) {
            System.out.printf("Price for %s (%s): $%.2f%n", stock.getSymbol(), stock.getName(), stock.getCurrentPrice());
        } else {
            System.out.println("Could not retrieve quote for symbol '" + symbol + "'. Check symbol or API status.");
        }
    }

    /**
     * Handles the functionality for buying stocks.
     * Prompts the user for a stock symbol and the number of shares to purchase,
     * and processes the transaction if valid.
     */
    private void handleBuyStock() {
        System.out.print("Enter stock symbol to buy (or type '" + CANCEL_KEYWORD + "' to abort): ");
        String symbolInput = scanner.nextLine().trim();

        if (symbolInput.equalsIgnoreCase(CANCEL_KEYWORD)) {
            System.out.println("Buy operation cancelled.");
            return;
        }
        if (symbolInput.isEmpty()) {
            System.out.println("Stock symbol cannot be empty.");
            return;
        }
        String symbol = symbolInput.toUpperCase();

        // Show current price and max shares user *could* buy with their balance
        int maxPossibleShares = portfolioManager.calculateMaxShares(symbol, currentUser.getBalance());
        if (maxPossibleShares == -1) {
            // Error message already printed by calculateMaxShares or getStockInfo
            return; // Exit if we couldn't get price info
        } else if (maxPossibleShares == 0) {
            System.out.println("Cannot buy " + symbol + ". Either price is too high or balance is too low.");
            Stock stock = apiService.getStockInfo(symbol); // Fetch again for price display if needed
            if(stock != null && stock.getCurrentPrice() > 0) {
                System.out.printf("(Current Price: $%.2f, Your Balance: $%.2f)%n", stock.getCurrentPrice(), currentUser.getBalance());
            }
            return;
        } else {
            Stock stock = apiService.getStockInfo(symbol); // Fetch again for price display
            if (stock != null) {
                System.out.printf("(Current Price for %s: $%.2f. You can afford a maximum of %d shares with your current balance.)%n",
                        symbol, stock.getCurrentPrice(), maxPossibleShares);
            } else {
                System.out.println("Could not retrieve current price for " + symbol + ". Aborting buy operation.");
                return;
            }
        }

        int quantity = -1;
        boolean quantitySet = false;
        while (!quantitySet) {
            System.out.print("Enter number of shares to buy (or type '" + CANCEL_KEYWORD + "' to abort): ");
            String quantityInputStr = scanner.nextLine().trim();

            if (quantityInputStr.equalsIgnoreCase(CANCEL_KEYWORD)) {
                System.out.println("Buy operation cancelled.");
                return;
            }

            try {
                quantity = Integer.parseInt(quantityInputStr);
                if (quantity <= 0) {
                    System.out.println("Number of shares must be positive.");
                } else if (quantity > maxPossibleShares) { // maxPossibleShares already checked > 0
                    System.out.println("You cannot afford " + quantity + " shares. Max possible is " + maxPossibleShares + ".");
                } else {
                    quantitySet = true; // Valid quantity
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter a whole number or '" + CANCEL_KEYWORD + "'.");
            }
        }

        // Execute the buy operation
        portfolioManager.buyStock(symbol, quantity);
    }

    /**
     * Handles the functionality for selling stocks.
     * Prompts the user for a stock symbol and the number of shares to sell,
     * and processes the transaction if the user has sufficient holdings.
     */
    private void handleSellStock() {
        System.out.print("Enter stock symbol to sell (or type '" + CANCEL_KEYWORD + "' to abort): ");
        String symbolInput = scanner.nextLine().trim();

        if (symbolInput.equalsIgnoreCase(CANCEL_KEYWORD)) {
            System.out.println("Sell operation cancelled.");
            return;
        }
        if (symbolInput.isEmpty()) {
            System.out.println("Stock symbol cannot be empty.");
            return;
        }
        String symbol = symbolInput.toUpperCase();

        // Check if user owns this stock and display quantity
        Portfolio portfolio = portfolioManager.getPortfolio();
        Position position = portfolio.getPosition(symbol); // Assuming Portfolio has getPosition(String symbol)
        if (position == null || position.getQuantity() == 0) {
            System.out.println("You do not own any shares of " + symbol + ".");
            return;
        }

        System.out.println("You currently own " + position.getQuantity() + " shares of " + symbol + ".");
        Stock stock = apiService.getStockInfo(symbol);
        if (stock != null) {
            System.out.printf("(Current Market Price for %s: $%.2f)%n", symbol, stock.getCurrentPrice());
        } else {
            System.out.println("Warning: Could not fetch current market price for " + symbol + ". Proceeding with sale if quantity is valid.");
        }


        int quantity = -1;
        boolean quantitySet = false;
        while (!quantitySet) {
            System.out.print("Enter number of shares to sell (max " + position.getQuantity() + ", or type '" + CANCEL_KEYWORD + "' to abort): ");
            String quantityInputStr = scanner.nextLine().trim();

            if (quantityInputStr.equalsIgnoreCase(CANCEL_KEYWORD)) {
                System.out.println("Sell operation cancelled.");
                return;
            }

            try {
                quantity = Integer.parseInt(quantityInputStr);
                if (quantity <= 0) {
                    System.out.println("Number of shares must be positive.");
                } else if (quantity > position.getQuantity()) {
                    System.out.println("You cannot sell more shares than you own (" + position.getQuantity() + ").");
                } else {
                    quantitySet = true; // Valid quantity
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter a whole number or '" + CANCEL_KEYWORD + "'.");
            }
        }

        // Execute the sell operation
        portfolioManager.sellStock(symbol, quantity);
    }

    /**
     * Handles the functionality for depositing funds into the user's account.
     * Prompts the user to input the deposit amount and updates their account balance.
     */
    private void handleDepositFunds() {
        double amount = -1;
        boolean amountSet = false;

        while (!amountSet) {
            System.out.print("Enter amount to deposit ($) (or type '" + CANCEL_KEYWORD + "' to abort): ");
            String amountInputStr = scanner.nextLine().trim();

            if (amountInputStr.equalsIgnoreCase(CANCEL_KEYWORD)) {
                System.out.println("Deposit operation cancelled.");
                return;
            }

            try {
                amount = Double.parseDouble(amountInputStr);
                if (amount <= 0) {
                    System.out.println("Deposit amount must be positive.");
                } else {
                    amountSet = true;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter a number or '" + CANCEL_KEYWORD + "'.");
            }
        }
        portfolioManager.deposit(amount);
    }
}
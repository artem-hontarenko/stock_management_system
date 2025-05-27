# Stock Management System - User Documentation

## 1. Introduction
Welcome to the Stock Management System! This console-based application allows you to manage a virtual stock portfolio, track your investments, and make trading decisions using simulated real-time market data (via Alpha Vantage API).

## 2. Getting Started
- Launch the application.
- You will be prompted to enter a username.
    - If you are a new user, your username will not be found. The system will ask if you want to create a new user.
        - Type 'yes'.
        - Enter an initial deposit amount (e.g., 10000). This will be your starting cash balance.

## 3. Main Menu
Once logged in, you will see the main menu with your current username and cash balance displayed. Choose an option by typing its number and pressing Enter:

**1. View Portfolio Summary:**
- Displays all stocks you own, including quantity, average purchase price, current market price (fetched live), and total current value of each holding.
- Also shows your total stock holdings value and current cash balance.

**2. View Transaction History:**
- Lists all your past activities: deposits, stock purchases (BUY), and stock sales (SELL), sorted with the most recent first.
- Includes timestamp, type, stock symbol (if applicable), quantity, price per share/deposit amount, and total amount.

**3. View Profit/Loss Report:**
- For each stock you own, this report shows:
- Quantity, average buy price, current market price.
- Total cost of your holding, its current market value.
- Absolute profit or loss ($) and percentage profit or loss (%).
- An overall portfolio profit/loss is also calculated.

**4. Get Stock Price:**
- Enter a stock symbol (e.g., IBM, AAPL, MSFT) to fetch its current market price.

**5. Buy Stock:**
- Enter the stock symbol you wish to purchase.
- The system will show the current price and the maximum shares you can afford with your entire balance.
- Enter the number of shares you want to buy.
- The system will confirm the purchase, deduct the cost from your cash balance, and add the stock to your portfolio.

**6. Sell Stock:**
- Enter the stock symbol you wish to sell.
- The system will show how many shares you currently own.
- Enter the number of shares you want to sell (cannot exceed the quantity you own).
- The system will confirm the sale, add the proceeds to your cash balance, and update your portfolio.

**7. Deposit Funds:**
- Enter the amount of cash you want to add to your account.
- This amount will be added to your cash balance.

**8. Logout/Switch Account:**
- Logout from the current user.
- Returns to "Getting started" state.

**9. Exit application:**
- Closes the application.

## 4. Important Notes
- **API Key:** This application requires a valid Alpha Vantage API key to fetch stock prices. Ensure the key is correctly configured in the `StockApiService.java` file. The free tier of Alpha Vantage has limitations on the number of API calls per day. So if the limit was exceeded it would require waiting for a day or creating your own API key. The instructions on how to create an API key is written below. 
- **In-Memory Storage:** All user data (account, portfolio, transactions) is stored in memory. This means all data will be LOST when the application is closed. This version is for demonstration and does not save data persistently.
- **Market Data:** Stock prices are fetched from an external API and are subject to the API's availability and accuracy.
- **Stuck on the Continue**: if the app is stuck after you press Enter to continue, just press Enter 1-2 times again and choose the action you want to do.


## 5. Creating a new API key.
- Open the link - https://www.alphavantage.co/support/#api-key
- Fill in the simple form and obtain a key.
- Go to the StockApiService.java in org.stockmanager.service package and paste the obtained API key into the corresponding place on the line 22.
- New 25 API calls are available for you today. 

## 6. Interacting with the System
- Follow the on-screen prompts.
- When asked for input (e.g., stock symbol, quantity), type your response and press Enter.
- After most actions, you'll be prompted to "Press Enter to continue..." before the menu reappears.

Thank you for using the Stock Management System!
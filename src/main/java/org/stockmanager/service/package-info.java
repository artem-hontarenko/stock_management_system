package org.stockmanager.service;

/**
 * Provides service classes responsible for implementing business logic
 * and facilitating interactions between the application's model classes
 * and external systems or data layers.
 *
 * <p>The {@code service} package encapsulates the core application functionalities, such as:</p>
 * <ul>
 *   <li>Managing operations on stocks and portfolios.</li>
 *   <li>Processing stock trading activities like buying and selling shares.</li>
 *   <li>Integrating with APIs or data sources to fetch real-time stock data and updates.</li>
 * </ul>
 *
 * <p>The key classes in this package include:</p>
 * <ul>
 *   <li>{@link org.stockmanager.service.PortfolioManager} - Provides operations to manage and manipulate
 *       portfolios, such as adding, updating, selling positions, and retrieving portfolio summaries.</li>
 *   <li>{@link org.stockmanager.service.StockApiService} - Handles actions related to stocks, such as fetching
 *       stock details, tracking stock prices, and updating stock information.</li>
 *   <li>{@link org.stockmanager.service.ReportGenerator} - Generates the report with details about stocks purchases
 *       current portfolio, etc. </li>
 * </ul>
 *
 * <p>By centralizing business logic in services, this package promotes a clear separation of concerns,
 * ensuring simplified and maintainable code structure throughout the application.</p>
 *
 * @see org.stockmanager.service.PortfolioManager
 * @see org.stockmanager.service.StockApiService
 * @see org.stockmanager.service.ReportGenerator
 */

package org.stockmanager.model;

/**
 * Contains the core data models for the stock management application.
 * This package includes classes that define the structure of entities
 * utilized in the operations and business logic of the application.
 *
 * <p>The {@code model} package includes:</p>
 * <ul>
 *   <li>{@link org.stockmanager.model.Stock} - Represents an individual stock with details such as
 *       symbol, name, and current price.</li>
 *   <li>{@link org.stockmanager.model.Portfolio} - Represents a collection of stocks and facilitates
 *       operations such as adding, updating, or selling stock positions.</li>
 *   <li>{@link org.stockmanager.model.Position} - Encapsulates stock quantities and pricing information
 *       within a particular portfolio.</li>
 *   <li>{@link org.stockmanager.model.Transaction} -  Represents a financial transaction, which can be
 *       a stock purchase, sale, or deposit.</li>
 *   <li>{@link org.stockmanager.model.User} - Represents a user in the system, including their username
 *       and available balance.<li/>
 *   <li>{@link org.stockmanager.model.TransactionType} - Represents the type of a financial transaction
 *       in a portfolio.<li/>
 * </ul>
 *
 * <p>This package is central to the application's functionality. It defines the
 * fundamental business entities required for accurate tracking, processing,
 * and management of stock-related data.</p>
 *
 * @see org.stockmanager.model.Stock
 * @see org.stockmanager.model.Portfolio
 * @see org.stockmanager.model.Position
 * @see org.stockmanager.model.Transaction
 * @see org.stockmanager.model.User
 * @see org.stockmanager.model.TransactionType
 */


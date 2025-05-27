package org.stockmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.stockmanager.model.Stock;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Service for interacting with the Alpha Vantage Stock API to fetch real-time stock data.
 * Provides methods to retrieve stock information such as the latest price for a given symbol.
 */
public class StockApiService {

    private static final String API_KEY = "RZOCJALWSFJWTRC5"; // <-- Paste the API key here

    private static final String BASE_URL = "https://www.alphavantage.co/query";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new StockApiService, initializing HTTP client and JSON parser.
     */
    public StockApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10)) // Set connection timeout
                .build();
        this.objectMapper = new ObjectMapper();

    }

    /**
     * Fetches real-time stock data (or latest trading day data) for a given symbol.
     * Uses Alpha Vantage GLOBAL_QUOTE function.
     *
     * @param symbol The stock symbol (e.g., "IBM", "AAPL")
     * @return A Stock object with current price and info, or null if fetching fails or symbol not found.
     */
    public Stock getStockInfo(String symbol) {
        if (API_KEY == null || API_KEY.isEmpty()) {
            System.err.println("API Key is missing. Cannot fetch stock data.");
            return null;
        }

        try {
            String encodedSymbol = URLEncoder.encode(symbol, StandardCharsets.UTF_8);
            String function = "GLOBAL_QUOTE";
            String urlString = String.format("%s?function=%s&symbol=%s&apikey=%s",
                    BASE_URL, function, encodedSymbol, API_KEY);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .timeout(Duration.ofSeconds(15)) // Set request timeout
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Error fetching stock data for " + symbol + ". HTTP Status: " + response.statusCode());
                System.err.println("Response body: " + response.body());
                return null;
            }

            String jsonResponse = response.body();
            // System.out.println("API Response for " + symbol + ": " + jsonResponse); // Debugging

            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode globalQuote = rootNode.path("Global Quote");

            if (globalQuote.isMissingNode() || globalQuote.isEmpty()) {
                // Check for API limit note or other messages
                if(rootNode.has("Note")) {
                    System.err.println("API Note for " + symbol + ": " + rootNode.path("Note").asText());
                    System.err.println("This might indicate reaching the free tier API call limit.");
                } else if (rootNode.has("Error Message")) {
                    System.err.println("API Error for " + symbol + ": " + rootNode.path("Error Message").asText());
                } else {
                    System.err.println("Error: 'Global Quote' data not found in API response for " + symbol + ". Is the symbol valid?");
                    System.err.println("Full Response: " + jsonResponse); // Uncomment for more debugging
                }
                return null;
            }

            String fetchedSymbol = globalQuote.path("01. symbol").asText(null);
            String priceStr = globalQuote.path("05. price").asText(null);
            // Alpha Vantage doesn't reliably provide the full name in GLOBAL_QUOTE
            // We could make another API call (SYMBOL_SEARCH) but that uses more quota.
            String name = "N/A"; // Default name

            if (fetchedSymbol == null || priceStr == null) {
                System.err.println("Error: Could not parse symbol or price from API response for " + symbol);
                return null;
            }

            try {
                double price = Double.parseDouble(priceStr);
                return new Stock(fetchedSymbol, name, price);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing stock price '" + priceStr + "' for symbol " + symbol);
                return null;
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error during API call for symbol " + symbol + ": " + e.getMessage());
            // Log the stack trace for detailed debugging if needed
            // e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while fetching stock data for " + symbol + ": " + e.getMessage());
            // e.printStackTrace();
            return null;
        }
    }
}
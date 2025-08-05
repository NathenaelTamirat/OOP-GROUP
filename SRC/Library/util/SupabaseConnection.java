package util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Supabase Connection Manager
 * 
 * This class manages Supabase database connections using REST API calls.
 * It provides a clean interface for CRUD operations and handles authentication.
 * 
 * @author Library Management Team
 * @version 3.0
 * @since 2024
 */
public class SupabaseConnection {
    
    // HTTP Client configuration
    private static final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
    
    // Connection monitoring
    private static final AtomicInteger requestCount = new AtomicInteger(0);
    private static volatile LocalDateTime lastRequestTime = null;
    private static volatile boolean isInitialized = false;
    
    // Security: Prevent instantiation
    private SupabaseConnection() {
        throw new UnsupportedOperationException("SupabaseConnection is a utility class and cannot be instantiated");
    }
    
    /**
     * Initializes the Supabase connection and validates configuration
     */
    public static void initialize() {
        if (isInitialized) {
            return;
        }
        
        Logger.log("Initializing Supabase connection...");
        
        try {
            // Initialize configuration first
            ConfigManager.initialize();
            
            // Test connection by making a simple query
            String testResponse = executeGet("users", "select=id&limit=1");
            if (testResponse != null) {
                Logger.log("Supabase connection initialized successfully");
                isInitialized = true;
            } else {
                throw new RuntimeException("Failed to connect to Supabase");
            }
        } catch (Exception e) {
            Logger.log("Failed to initialize Supabase connection: " + e.getMessage());
            throw new RuntimeException("Supabase initialization failed", e);
        }
    }
    
    /**
     * Executes a GET request to Supabase
     * 
     * @param table The table name
     * @param queryParams Query parameters (e.g., "select=*&limit=10")
     * @return JSON response as string
     */
    public static String executeGet(String table, String queryParams) {
        try {
            String url = ConfigManager.getConfig("SUPABASE_URL") + 
                        ConfigManager.getConfig("REST_ENDPOINT", "/rest/v1") + "/" + table;
            if (queryParams != null && !queryParams.isEmpty()) {
                url += "?" + queryParams;
            }
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", ConfigManager.getConfig("SUPABASE_ANON_KEY"))
                .header("Authorization", "Bearer " + ConfigManager.getConfig("SUPABASE_ANON_KEY"))
                .header("Content-Type", "application/json")
                .GET()
                .timeout(Duration.ofSeconds(ConfigManager.getIntConfig("REQUEST_TIMEOUT", 30)))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                logRequest("GET", table, response.statusCode());
                return response.body();
            } else {
                Logger.log("GET request failed for table " + table + ": " + response.statusCode() + " - " + response.body());
                return null;
            }
            
        } catch (Exception e) {
            Logger.log("Error executing GET request: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Executes a POST request to Supabase (INSERT)
     * 
     * @param table The table name
     * @param jsonData JSON data to insert
     * @return JSON response as string
     */
    public static String executePost(String table, String jsonData) {
        try {
            String url = ConfigManager.getConfig("SUPABASE_URL") + 
                        ConfigManager.getConfig("REST_ENDPOINT", "/rest/v1") + "/" + table;
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", ConfigManager.getConfig("SUPABASE_ANON_KEY"))
                .header("Authorization", "Bearer " + ConfigManager.getConfig("SUPABASE_ANON_KEY"))
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .timeout(Duration.ofSeconds(ConfigManager.getIntConfig("REQUEST_TIMEOUT", 30)))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 201) {
                logRequest("POST", table, response.statusCode());
                return response.body();
            } else {
                Logger.log("POST request failed for table " + table + ": " + response.statusCode() + " - " + response.body());
                return null;
            }
            
        } catch (Exception e) {
            Logger.log("Error executing POST request: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Executes a PATCH request to Supabase (UPDATE)
     * 
     * @param table The table name
     * @param jsonData JSON data to update
     * @param filter Filter condition (e.g., "id=eq.1")
     * @return JSON response as string
     */
    public static String executePatch(String table, String jsonData, String filter) {
        try {
            String url = ConfigManager.getConfig("SUPABASE_URL") + 
                        ConfigManager.getConfig("REST_ENDPOINT", "/rest/v1") + "/" + table;
            if (filter != null && !filter.isEmpty()) {
                url += "?" + filter;
            }
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", ConfigManager.getConfig("SUPABASE_ANON_KEY"))
                .header("Authorization", "Bearer " + ConfigManager.getConfig("SUPABASE_ANON_KEY"))
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonData))
                .timeout(Duration.ofSeconds(ConfigManager.getIntConfig("REQUEST_TIMEOUT", 30)))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                logRequest("PATCH", table, response.statusCode());
                return response.body();
            } else {
                Logger.log("PATCH request failed for table " + table + ": " + response.statusCode() + " - " + response.body());
                return null;
            }
            
        } catch (Exception e) {
            Logger.log("Error executing PATCH request: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Executes a DELETE request to Supabase
     * 
     * @param table The table name
     * @param filter Filter condition (e.g., "id=eq.1")
     * @return true if successful, false otherwise
     */
    public static boolean executeDelete(String table, String filter) {
        try {
            String url = ConfigManager.getConfig("SUPABASE_URL") + 
                        ConfigManager.getConfig("REST_ENDPOINT", "/rest/v1") + "/" + table;
            if (filter != null && !filter.isEmpty()) {
                url += "?" + filter;
            }
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", ConfigManager.getConfig("SUPABASE_ANON_KEY"))
                .header("Authorization", "Bearer " + ConfigManager.getConfig("SUPABASE_ANON_KEY"))
                .header("Content-Type", "application/json")
                .DELETE()
                .timeout(Duration.ofSeconds(ConfigManager.getIntConfig("REQUEST_TIMEOUT", 30)))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 204) {
                logRequest("DELETE", table, response.statusCode());
                return true;
            } else {
                Logger.log("DELETE request failed for table " + table + ": " + response.statusCode() + " - " + response.body());
                return false;
            }
            
        } catch (Exception e) {
            Logger.log("Error executing DELETE request: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Logs request information for monitoring
     */
    private static void logRequest(String method, String table, int statusCode) {
        requestCount.incrementAndGet();
        lastRequestTime = LocalDateTime.now();
        Logger.log(method + " request to " + table + " completed with status " + statusCode);
    }
    
    /**
     * Gets connection statistics for monitoring
     * 
     * @return Connection statistics as a formatted string
     */
    public static String getConnectionStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("Supabase Connection Statistics:\n");
        stats.append("Initialized: ").append(isInitialized).append("\n");
        stats.append("Total Requests: ").append(requestCount.get()).append("\n");
        stats.append("Last Request Time: ").append(lastRequestTime != null ? 
            lastRequestTime.format(DateTimeFormatter.ISO_LOCAL_TIME) : "Never").append("\n");
        stats.append("Supabase URL: ").append(ConfigManager.getConfig("SUPABASE_URL")).append("\n");
        return stats.toString();
    }
    
    /**
     * Validates if the connection is working
     * 
     * @return true if connection is valid, false otherwise
     */
    public static boolean isConnectionValid() {
        try {
            String response = executeGet("users", "select=id&limit=1");
            return response != null;
        } catch (Exception e) {
            Logger.log("Connection validation failed: " + e.getMessage());
            return false;
        }
    }
} 
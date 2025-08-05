package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Manager for Supabase Settings
 * 
 * This class manages configuration properties from both file and environment variables.
 * Environment variables take precedence over file settings.
 * 
 * @author Library Management Team
 * @version 2.0
 * @since 2024
 */
public class ConfigManager {
    
    private static final String CONFIG_FILE = "config/supabase_config.properties";
    private static Properties properties = null;
    private static boolean isInitialized = false;
    private static boolean isInitializing = false; // Prevent infinite loops
    
    // Security: Prevent instantiation
    private ConfigManager() {
        throw new UnsupportedOperationException("ConfigManager is a utility class and cannot be instantiated");
    }
    
    /**
     * Initializes the configuration system
     */
    public static void initialize() {
        if (isInitialized || isInitializing) {
            return;
        }
        
        isInitializing = true;
        
        try {
            System.out.println("Initializing configuration manager...");
            
            // Load from properties file first
            loadFromFile();
            
            // Load from environment variables (overrides file)
            loadFromEnvironment();
            
            // Validate configuration
            validateConfiguration();
            
            isInitialized = true;
            System.out.println("Configuration manager initialized successfully");
            
        } catch (Exception e) {
            System.err.println("Failed to initialize configuration: " + e.getMessage());
            throw new RuntimeException("Configuration initialization failed", e);
        } finally {
            isInitializing = false;
        }
    }
    
    /**
     * Gets a configuration value
     * 
     * @param key The configuration key
     * @return The configuration value or null if not found
     */
    public static String getConfig(String key) {
        if (!isInitialized && !isInitializing) {
            initialize();
        }
        
        if (properties == null) {
            return null;
        }
        
        return properties.getProperty(key);
    }
    
    /**
     * Gets a configuration value with default
     * 
     * @param key The configuration key
     * @param defaultValue The default value if not found
     * @return The configuration value or default
     */
    public static String getConfig(String key, String defaultValue) {
        String value = getConfig(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Gets an integer configuration value with default
     * 
     * @param key The configuration key
     * @param defaultValue The default value if not found or invalid
     * @return The integer value or default
     */
    public static int getIntConfig(String key, int defaultValue) {
        String value = getConfig(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.err.println("Invalid integer configuration for " + key + ": " + value);
            }
        }
        return defaultValue;
    }
    
    /**
     * Loads configuration from properties file
     */
    private static void loadFromFile() {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties = new Properties();
                properties.load(input);
                System.out.println("Configuration loaded from file: " + CONFIG_FILE);
            } else {
                System.out.println("Configuration file not found: " + CONFIG_FILE);
                properties = new Properties();
            }
        } catch (IOException e) {
            System.err.println("Error loading configuration file: " + e.getMessage());
            properties = new Properties();
        }
    }
    
    /**
     * Loads configuration from environment variables
     */
    private static void loadFromEnvironment() {
        String[] envKeys = {
            "SUPABASE_URL",
            "SUPABASE_ANON_KEY",
            "API_VERSION",
            "REST_ENDPOINT",
            "CONNECTION_TIMEOUT",
            "REQUEST_TIMEOUT",
            "MAX_RETRIES"
        };
        
        for (String key : envKeys) {
            String envValue = System.getenv(key);
            if (envValue != null && !envValue.trim().isEmpty()) {
                if (properties == null) {
                    properties = new Properties();
                }
                properties.setProperty(key, envValue);
                System.out.println("Loaded environment variable: " + key);
            }
        }
    }
    
    /**
     * Validates that all required configuration is present
     */
    private static void validateConfiguration() {
        String[] requiredKeys = {"SUPABASE_URL", "SUPABASE_ANON_KEY"};
        
        for (String key : requiredKeys) {
            String value = properties.getProperty(key);
            if (value == null || value.trim().isEmpty()) {
                throw new RuntimeException("Missing required configuration: " + key);
            }
        }
        
        // Validate URL format
        String url = properties.getProperty("SUPABASE_URL");
        if (!url.startsWith("https://")) {
            throw new RuntimeException("Invalid Supabase URL format: " + url);
        }
        
        // Validate API key format (basic check)
        String apiKey = properties.getProperty("SUPABASE_ANON_KEY");
        if (apiKey.length() < 50) {
            throw new RuntimeException("Invalid Supabase API key format");
        }
    }
    
    /**
     * Gets all configuration as a formatted string for debugging
     * 
     * @return Configuration summary (without sensitive data)
     */
    public static String getConfigurationSummary() {
        if (!isInitialized && !isInitializing) {
            initialize();
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("Configuration Summary:\n");
        summary.append("Initialized: ").append(isInitialized).append("\n");
        summary.append("Supabase URL: ").append(getConfig("SUPABASE_URL")).append("\n");
        summary.append("API Version: ").append(getConfig("API_VERSION", "v1")).append("\n");
        summary.append("Connection Timeout: ").append(getIntConfig("CONNECTION_TIMEOUT", 30)).append("s\n");
        summary.append("Request Timeout: ").append(getIntConfig("REQUEST_TIMEOUT", 30)).append("s\n");
        summary.append("Max Retries: ").append(getIntConfig("MAX_RETRIES", 3)).append("\n");
        
        return summary.toString();
    }
    
    /**
     * Checks if configuration is properly initialized
     * 
     * @return true if initialized, false otherwise
     */
    public static boolean isInitialized() {
        return isInitialized;
    }
} 
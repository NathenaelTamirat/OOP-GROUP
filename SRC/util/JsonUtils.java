package util;

import java.util.regex.Pattern;

/**
 * Simple JSON Utility for basic JSON operations
 * 
 * This class provides basic JSON parsing and creation without external dependencies.
 * It's designed for simple JSON operations needed for Supabase integration.
 * 
 * @author Library Management Team
 * @version 1.0
 * @since 2024
 */
public class JsonUtils {
    
    /**
     * Creates a simple JSON object from key-value pairs
     * 
     * @param pairs Key-value pairs (key1, value1, key2, value2, ...)
     * @return JSON string
     */
    public static String createJson(String... pairs) {
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("Pairs must be even number of arguments");
        }
        
        StringBuilder json = new StringBuilder("{");
        for (int i = 0; i < pairs.length; i += 2) {
            if (i > 0) json.append(",");
            json.append("\"").append(escapeJson(pairs[i])).append("\":");
            
            String value = pairs[i + 1];
            if (value == null) {
                json.append("null");
            } else if (isNumeric(value)) {
                json.append(value);
            } else if (value.equals("true") || value.equals("false")) {
                json.append(value);
            } else {
                json.append("\"").append(escapeJson(value)).append("\"");
            }
        }
        json.append("}");
        return json.toString();
    }
    
    /**
     * Extracts a value from a JSON string by key
     * 
     * @param json JSON string
     * @param key Key to extract
     * @return Value or null if not found
     */
    public static String extractValue(String json, String key) {
        if (json == null || key == null) return null;
        
        String pattern = "\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern p = Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        
        if (m.find()) {
            return m.group(1);
        }
        
        // Try to find numeric or boolean values
        pattern = "\"" + Pattern.quote(key) + "\"\\s*:\\s*([^,}\\s]+)";
        p = Pattern.compile(pattern);
        m = p.matcher(json);
        
        if (m.find()) {
            return m.group(1);
        }
        
        return null;
    }
    
    /**
     * Checks if a string is numeric
     */
    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Escapes special characters in JSON strings
     */
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
    
    /**
     * Parses a JSON array and returns the first element
     * 
     * @param jsonArray JSON array string
     * @return First element or null if empty
     */
    public static String getFirstElement(String jsonArray) {
        if (jsonArray == null || !jsonArray.startsWith("[") || !jsonArray.endsWith("]")) {
            return null;
        }
        
        String content = jsonArray.substring(1, jsonArray.length() - 1).trim();
        if (content.isEmpty()) {
            return null;
        }
        
        // Find the first complete object
        int braceCount = 0;
        int start = -1;
        int end = -1;
        
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            
            if (c == '{') {
                if (start == -1) start = i;
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0 && start != -1) {
                    end = i + 1;
                    break;
                }
            }
        }
        
        if (start != -1 && end != -1) {
            return content.substring(start, end);
        }
        
        return null;
    }
} 
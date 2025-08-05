
// Provides static methods to validate empty fields, email format, password strength, and ISBN.

package util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Comprehensive Input Validation and Security Utility
 * 
 * This class provides robust input validation with security measures to prevent
 * injection attacks, ensure data integrity, and maintain system security.
 * All validation methods include detailed error reporting and logging.
 * 
 * @author Library Management Team
 * @version 2.0
 * @since 2024
 */
public class Validator {
    
    // Security: Prevent instantiation
    private Validator() {
        throw new UnsupportedOperationException("Validator is a utility class and cannot be instantiated");
    }
    
    // Validation patterns and constants
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    private static final Pattern ISBN_10_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern ISBN_13_PATTERN = Pattern.compile("^\\d{13}$");
    
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z\\s\\-']{2,50}$"
    );
    
    private static final Pattern TITLE_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9\\s\\-',.!?()]{1,200}$"
    );
    
    // Validation limits
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 128;
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MIN_TITLE_LENGTH = 1;
    private static final int MAX_TITLE_LENGTH = 200;
    
    /**
     * Validates if a string is not null, empty, or contains only whitespace
     * 
     * @param text The string to validate
     * @return true if the string is valid, false otherwise
     */
    public static boolean isNotEmpty(String text) {
        if (text == null) {
            Logger.logDebug("Validation failed: text is null");
            return false;
        }
        
        String trimmed = text.trim();
        boolean isValid = !trimmed.isEmpty();
        
        if (!isValid) {
            Logger.logDebug("Validation failed: text is empty or whitespace only");
        }
        
        return isValid;
    }
    
    /**
     * Validates email format with comprehensive security checks
     * 
     * @param email The email address to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) {
            Logger.logDebug("Email validation failed: email is null or empty");
            return false;
        }
        
        // Security: Check for potential injection patterns
        if (containsSuspiciousPatterns(email)) {
            Logger.logWarning("Email validation failed: suspicious patterns detected in email");
            return false;
        }
        
        // Check length limits
        if (email.length() > 254) { // RFC 5321 limit
            Logger.logDebug("Email validation failed: email too long");
            return false;
        }
        
        // Validate email format
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        boolean isValid = matcher.matches();
        
        if (!isValid) {
            Logger.logDebug("Email validation failed: invalid email format - " + email);
        }
        
        return isValid;
    }
    
    /**
     * Validates password strength with comprehensive security requirements
     * 
     * @param password The password to validate
     * @return true if password meets security requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (!isNotEmpty(password)) {
            Logger.logDebug("Password validation failed: password is null or empty");
            return false;
        }
        
        // Check length requirements
        if (password.length() < MIN_PASSWORD_LENGTH) {
            Logger.logDebug("Password validation failed: password too short (minimum " + MIN_PASSWORD_LENGTH + " characters)");
            return false;
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            Logger.logDebug("Password validation failed: password too long (maximum " + MAX_PASSWORD_LENGTH + " characters)");
            return false;
        }
        
        // Security: Check for common weak passwords
        if (isCommonWeakPassword(password)) {
            Logger.logWarning("Password validation failed: common weak password detected");
            return false;
        }
        
        // Validate password complexity
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        boolean isValid = matcher.matches();
        
        if (!isValid) {
            Logger.logDebug("Password validation failed: password does not meet complexity requirements");
        }
        
        return isValid;
    }
    
    /**
     * Validates ISBN format (supports both ISBN-10 and ISBN-13)
     * 
     * @param isbn The ISBN to validate
     * @return true if ISBN is valid, false otherwise
     */
    public static boolean isValidISBN(String isbn) {
        if (!isNotEmpty(isbn)) {
            Logger.logDebug("ISBN validation failed: ISBN is null or empty");
            return false;
        }
        
        // Remove hyphens and spaces for validation
        String cleanIsbn = isbn.replaceAll("[\\s-]", "");
        
        // Check for ISBN-10 or ISBN-13 format
        boolean isValid = ISBN_10_PATTERN.matcher(cleanIsbn).matches() || 
                         ISBN_13_PATTERN.matcher(cleanIsbn).matches();
        
        if (!isValid) {
            Logger.logDebug("ISBN validation failed: invalid ISBN format - " + isbn);
        }
        
        return isValid;
    }
    
    /**
     * Validates person names with security and format checks
     * 
     * @param name The name to validate
     * @return true if name is valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (!isNotEmpty(name)) {
            Logger.logDebug("Name validation failed: name is null or empty");
            return false;
        }
        
        // Check length requirements
        if (name.length() < MIN_NAME_LENGTH) {
            Logger.logDebug("Name validation failed: name too short (minimum " + MIN_NAME_LENGTH + " characters)");
            return false;
        }
        
        if (name.length() > MAX_NAME_LENGTH) {
            Logger.logDebug("Name validation failed: name too long (maximum " + MAX_NAME_LENGTH + " characters)");
            return false;
        }
        
        // Security: Check for suspicious patterns
        if (containsSuspiciousPatterns(name)) {
            Logger.logWarning("Name validation failed: suspicious patterns detected in name");
            return false;
        }
        
        // Validate name format
        Matcher matcher = NAME_PATTERN.matcher(name);
        boolean isValid = matcher.matches();
        
        if (!isValid) {
            Logger.logDebug("Name validation failed: invalid name format - " + name);
        }
        
        return isValid;
    }
    
    /**
     * Validates book titles with security and format checks
     * 
     * @param title The title to validate
     * @return true if title is valid, false otherwise
     */
    public static boolean isValidTitle(String title) {
        if (!isNotEmpty(title)) {
            Logger.logDebug("Title validation failed: title is null or empty");
            return false;
        }
        
        // Check length requirements
        if (title.length() < MIN_TITLE_LENGTH) {
            Logger.logDebug("Title validation failed: title too short (minimum " + MIN_TITLE_LENGTH + " character)");
            return false;
        }
        
        if (title.length() > MAX_TITLE_LENGTH) {
            Logger.logDebug("Title validation failed: title too long (maximum " + MAX_TITLE_LENGTH + " characters)");
            return false;
        }
        
        // Security: Check for suspicious patterns
        if (containsSuspiciousPatterns(title)) {
            Logger.logWarning("Title validation failed: suspicious patterns detected in title");
            return false;
        }
        
        // Validate title format
        Matcher matcher = TITLE_PATTERN.matcher(title);
        boolean isValid = matcher.matches();
        
        if (!isValid) {
            Logger.logDebug("Title validation failed: invalid title format - " + title);
        }
        
        return isValid;
    }
    
    /**
     * Validates user role with security checks
     * 
     * @param role The role to validate
     * @return true if role is valid, false otherwise
     */
    public static boolean isValidRole(String role) {
        if (!isNotEmpty(role)) {
            Logger.logDebug("Role validation failed: role is null or empty");
            return false;
        }
        
        // Only allow predefined roles
        boolean isValid = "admin".equalsIgnoreCase(role) || "student".equalsIgnoreCase(role);
        
        if (!isValid) {
            Logger.logDebug("Role validation failed: invalid role - " + role);
        }
        
        return isValid;
    }
    
    /**
     * Validates numeric input with range checks
     * 
     * @param value The numeric value to validate
     * @param min Minimum allowed value
     * @param max Maximum allowed value
     * @return true if value is within range, false otherwise
     */
    public static boolean isValidNumericRange(int value, int min, int max) {
        boolean isValid = value >= min && value <= max;
        
        if (!isValid) {
            Logger.logDebug("Numeric validation failed: value " + value + " not in range [" + min + ", " + max + "]");
        }
        
        return isValid;
    }
    
    /**
     * Validates date format (YYYY-MM-DD)
     * 
     * @param date The date string to validate
     * @return true if date format is valid, false otherwise
     */
    public static boolean isValidDateFormat(String date) {
        if (!isNotEmpty(date)) {
            Logger.logDebug("Date validation failed: date is null or empty");
            return false;
        }
        
        // Check date format pattern
        boolean isValid = date.matches("^\\d{4}-\\d{2}-\\d{2}$");
        
        if (isValid) {
            try {
                // Validate actual date values
                java.time.LocalDate.parse(date);
            } catch (Exception e) {
                isValid = false;
                Logger.logDebug("Date validation failed: invalid date values - " + date);
            }
        } else {
            Logger.logDebug("Date validation failed: invalid date format - " + date);
        }
        
        return isValid;
    }
    
    /**
     * Checks for suspicious patterns that might indicate injection attacks
     * 
     * @param input The input to check
     * @return true if suspicious patterns are found, false otherwise
     */
    private static boolean containsSuspiciousPatterns(String input) {
        if (input == null) {
            return false;
        }
        
        // Check for SQL injection patterns
        String lowerInput = input.toLowerCase();
        String[] suspiciousPatterns = {
            "select", "insert", "update", "delete", "drop", "create", "alter",
            "union", "exec", "execute", "script", "javascript", "vbscript",
            "onload", "onerror", "onclick", "onmouseover", "eval(", "alert(",
            "--", "/*", "*/", "xp_", "sp_", "waitfor", "delay"
        };
        
        for (String pattern : suspiciousPatterns) {
            if (lowerInput.contains(pattern)) {
                Logger.logWarning("Suspicious pattern detected: " + pattern + " in input");
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks if password is a common weak password
     * 
     * @param password The password to check
     * @return true if password is weak, false otherwise
     */
    private static boolean isCommonWeakPassword(String password) {
        if (password == null) {
            return false;
        }
        
        String lowerPassword = password.toLowerCase();
        String[] weakPasswords = {
            "password", "123456", "12345678", "qwerty", "abc123", "password123",
            "admin", "admin123", "root", "user", "guest", "test", "demo",
            "welcome", "login", "pass", "pass123", "secret", "letmein"
        };
        
        for (String weak : weakPasswords) {
            if (lowerPassword.equals(weak)) {
                Logger.logWarning("Weak password detected: " + weak);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Gets validation statistics for monitoring
     * 
     * @return Validation statistics as a formatted string
     */
    public static String getValidationStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("Validation System Statistics:\n");
        stats.append("Min Password Length: ").append(MIN_PASSWORD_LENGTH).append("\n");
        stats.append("Max Password Length: ").append(MAX_PASSWORD_LENGTH).append("\n");
        stats.append("Min Name Length: ").append(MIN_NAME_LENGTH).append("\n");
        stats.append("Max Name Length: ").append(MAX_NAME_LENGTH).append("\n");
        stats.append("Min Title Length: ").append(MIN_TITLE_LENGTH).append("\n");
        stats.append("Max Title Length: ").append(MAX_TITLE_LENGTH).append("\n");
        return stats.toString();
    }
}
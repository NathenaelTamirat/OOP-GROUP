
// Utility for logging events/errors to logs/app_log.txt.

package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Secure Logging Utility with File Rotation and Error Handling
 * 
 * This class provides comprehensive logging functionality with security measures,
 * file rotation, and proper error handling. It prevents log injection attacks
 * and ensures log files don't grow indefinitely.
 * 
 * @author Library Management Team
 * @version 2.0
 * @since 2024
 */
public class Logger {
    
    // Configuration constants
    private static final String LOG_DIRECTORY = "logs";
    private static final String LOG_FILENAME = "library_system.log";
    private static final String ERROR_LOG_FILENAME = "error.log";
    private static final int MAX_LOG_SIZE_BYTES = 10 * 1024 * 1024; // 10MB
    private static final int MAX_LOG_FILES = 5;
    
    // Thread safety
    private static final ReentrantLock logLock = new ReentrantLock();
    private static volatile boolean isInitialized = false;
    
    // Security: Prevent instantiation
    private Logger() {
        throw new UnsupportedOperationException("Logger is a utility class and cannot be instantiated");
    }
    
    /**
     * Logs an informational message with proper formatting and security
     * 
     * @param message The message to log (will be sanitized)
     */
    public static void log(String message) {
        logMessage(message, "INFO");
    }
    
    /**
     * Logs a warning message with proper formatting and security
     * 
     * @param message The warning message to log (will be sanitized)
     */
    public static void logWarning(String message) {
        logMessage(message, "WARNING");
    }
    
    /**
     * Logs an error message with proper formatting and security
     * 
     * @param message The error message to log (will be sanitized)
     */
    public static void logError(String message) {
        logMessage(message, "ERROR");
    }
    
    /**
     * Logs an error message with exception details
     * 
     * @param message The error message to log
     * @param exception The exception that occurred
     */
    public static void logError(String message, Exception exception) {
        StringBuilder fullMessage = new StringBuilder();
        fullMessage.append(message).append(" - Exception: ").append(exception.getClass().getSimpleName());
        fullMessage.append(" - Message: ").append(exception.getMessage());
        
        // Add stack trace for debugging (limited to first 10 lines)
        StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace.length > 0) {
            fullMessage.append(" - Stack Trace: ");
            for (int i = 0; i < Math.min(10, stackTrace.length); i++) {
                fullMessage.append(stackTrace[i].toString());
                if (i < Math.min(9, stackTrace.length - 1)) {
                    fullMessage.append(" | ");
                }
            }
        }
        
        logMessage(fullMessage.toString(), "ERROR");
    }
    
    /**
     * Logs a debug message (only in development mode)
     * 
     * @param message The debug message to log
     */
    public static void logDebug(String message) {
        // Only log debug messages if system property is set
        if (Boolean.getBoolean("library.debug")) {
            logMessage(message, "DEBUG");
        }
    }
    
    /**
     * Core logging method with security and error handling
     * 
     * @param message The message to log
     * @param level The log level (INFO, WARNING, ERROR, DEBUG)
     */
    private static void logMessage(String message, String level) {
        if (message == null) {
            message = "null";
        }
        
        // Security: Sanitize message to prevent injection
        String sanitizedMessage = sanitizeMessage(message);
        
        logLock.lock();
        try {
            // Initialize logging system if needed
            if (!isInitialized) {
                initializeLoggingSystem();
            }
            
            // Create formatted log entry
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String logEntry = String.format("[%s] [%s] %s%n", timestamp, level, sanitizedMessage);
            
            // Write to main log file
            writeToLogFile(LOG_FILENAME, logEntry);
            
            // Write errors to separate error log
            if ("ERROR".equals(level)) {
                writeToLogFile(ERROR_LOG_FILENAME, logEntry);
            }
            
            // Also output to console for immediate visibility
            System.out.println(logEntry.trim());
            
        } catch (Exception e) {
            // Fallback to console if logging fails
            System.err.println("Logging failed: " + e.getMessage());
            System.err.println("Original message: " + sanitizedMessage);
        } finally {
            logLock.unlock();
        }
    }
    
    /**
     * Sanitizes log messages to prevent injection attacks
     * 
     * @param message The message to sanitize
     * @return Sanitized message
     */
    private static String sanitizeMessage(String message) {
        if (message == null) {
            return "null";
        }
        
        // Remove potentially dangerous characters and limit length
        String sanitized = message
            .replaceAll("[\\r\\n\\t]", " ") // Remove newlines and tabs
            .replaceAll("\\s+", " ") // Normalize whitespace
            .trim();
        
        // Limit message length to prevent log flooding
        if (sanitized.length() > 1000) {
            sanitized = sanitized.substring(0, 997) + "...";
        }
        
        return sanitized;
    }
    
    /**
     * Initializes the logging system and creates necessary directories
     */
    private static void initializeLoggingSystem() {
        try {
            // Create logs directory if it doesn't exist
            Path logDir = Paths.get(LOG_DIRECTORY);
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
            
            // Check and rotate log files if necessary
            checkAndRotateLogFiles();
            
            isInitialized = true;
            
        } catch (IOException e) {
            System.err.println("Failed to initialize logging system: " + e.getMessage());
            // Continue without logging rather than failing completely
        }
    }
    
    /**
     * Writes a log entry to the specified file with error handling
     * 
     * @param filename The log file to write to
     * @param logEntry The formatted log entry
     */
    private static void writeToLogFile(String filename, String logEntry) {
        Path logPath = Paths.get(LOG_DIRECTORY, filename);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(logPath.toString(), true))) {
            writer.write(logEntry);
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to log file " + filename + ": " + e.getMessage());
        }
    }
    
    /**
     * Checks log file sizes and rotates them if necessary
     */
    private static void checkAndRotateLogFiles() {
        try {
            Path logPath = Paths.get(LOG_DIRECTORY, LOG_FILENAME);
            
            if (Files.exists(logPath) && Files.size(logPath) > MAX_LOG_SIZE_BYTES) {
                rotateLogFiles();
            }
        } catch (IOException e) {
            System.err.println("Failed to check log file size: " + e.getMessage());
        }
    }
    
    /**
     * Rotates log files to prevent them from growing too large
     */
    private static void rotateLogFiles() {
        try {
            // Remove oldest log file if we have too many
            Path oldestLog = Paths.get(LOG_DIRECTORY, LOG_FILENAME + "." + MAX_LOG_FILES);
            if (Files.exists(oldestLog)) {
                Files.delete(oldestLog);
            }
            
            // Shift existing log files
            for (int i = MAX_LOG_FILES - 1; i >= 1; i--) {
                Path source = Paths.get(LOG_DIRECTORY, LOG_FILENAME + "." + i);
                Path target = Paths.get(LOG_DIRECTORY, LOG_FILENAME + "." + (i + 1));
                
                if (Files.exists(source)) {
                    Files.move(source, target);
                }
            }
            
            // Rename current log file
            Path currentLog = Paths.get(LOG_DIRECTORY, LOG_FILENAME);
            Path rotatedLog = Paths.get(LOG_DIRECTORY, LOG_FILENAME + ".1");
            
            if (Files.exists(currentLog)) {
                Files.move(currentLog, rotatedLog);
            }
            
        } catch (IOException e) {
            System.err.println("Failed to rotate log files: " + e.getMessage());
        }
    }
    
    /**
     * Gets logging system statistics for monitoring
     * 
     * @return Logging statistics as a formatted string
     */
    public static String getLoggingStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("Logging System Statistics:\n");
        stats.append("Initialized: ").append(isInitialized).append("\n");
        stats.append("Log Directory: ").append(LOG_DIRECTORY).append("\n");
        stats.append("Main Log File: ").append(LOG_FILENAME).append("\n");
        stats.append("Error Log File: ").append(ERROR_LOG_FILENAME).append("\n");
        stats.append("Max Log Size: ").append(MAX_LOG_SIZE_BYTES / 1024 / 1024).append("MB\n");
        stats.append("Max Log Files: ").append(MAX_LOG_FILES).append("\n");
        return stats.toString();
    }
}
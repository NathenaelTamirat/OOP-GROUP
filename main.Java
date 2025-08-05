/**
 * Library Management System - Main Entry Point
 * 
 * This application provides comprehensive library management functionality including
 * book inventory, user management, issue/return operations, and reporting.
 * 
 * @author Library Management Team
 * @version 3.0
 * @since 2024
 */

import UI.SimpleLoginFrame;
import util.SupabaseConnection;

public class Main {
    
    /**
     * Application entry point with comprehensive error handling and initialization
     * 
     * @param args Command line arguments (currently unused but available for future extensions)
     */
    public static void main(String[] args) {
        try {
            // Initialize logging system
            System.out.println("Library Management System starting...");
            System.out.println("Java version: " + System.getProperty("java.version"));
            System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
            
            // Validate Supabase connectivity before launching UI
            validateSupabaseConnection();
            
            // Launch application with proper Swing threading
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    SimpleLoginFrame loginFrame = new SimpleLoginFrame();
                    loginFrame.setVisible(true);
                    System.out.println("Login frame displayed successfully");
                } catch (Exception e) {
                    System.err.println("Critical error launching login frame: " + e.getMessage());
                    showErrorDialog("Failed to launch application", e);
                    System.exit(1);
                }
            });
            
        } catch (Exception e) {
            System.err.println("Fatal error during application startup: " + e.getMessage());
            showErrorDialog("Application startup failed", e);
            System.exit(1);
        }
    }
    
    /**
     * Validates Supabase connection and configuration
     * 
     * @throws RuntimeException if Supabase connection fails
     */
    private static void validateSupabaseConnection() {
        System.out.println("Validating Supabase connection...");
        
        try {
            // Initialize Supabase connection
            SupabaseConnection.initialize();
            
            // Test basic Supabase operations
            if (!SupabaseConnection.isConnectionValid()) {
                throw new RuntimeException("Supabase connectivity test failed");
            }
            
            System.out.println("Supabase connection validated successfully");
            
        } catch (Exception e) {
            System.err.println("Supabase connection failed: " + e.getMessage());
            throw new RuntimeException("Supabase connection failed. Please check configuration and ensure Supabase is accessible.", e);
        }
    }
    
    /**
     * Displays error dialog with detailed information for debugging
     * 
     * @param title Dialog title
     * @param exception The exception that occurred
     */
    private static void showErrorDialog(String title, Exception exception) {
        try {
            javax.swing.SwingUtilities.invokeLater(() -> {
                StringBuilder message = new StringBuilder();
                message.append("Application Error: ").append(exception.getMessage()).append("\n\n");
                message.append("Technical Details:\n");
                message.append("Exception: ").append(exception.getClass().getSimpleName()).append("\n");
                message.append("Stack Trace:\n");
                
                for (StackTraceElement element : exception.getStackTrace()) {
                    if (element.getClassName().startsWith("UI.") || 
                        element.getClassName().startsWith("service.") || 
                        element.getClassName().startsWith("dao.") ||
                        element.getClassName().equals("Main")) {
                        message.append("  ").append(element.toString()).append("\n");
                    }
                }
                
                javax.swing.JOptionPane.showMessageDialog(
                    null, 
                    message.toString(), 
                    title, 
                    javax.swing.JOptionPane.ERROR_MESSAGE
                );
            });
        } catch (Exception e) {
            // Fallback to console output if Swing fails
            System.err.println("Error displaying error dialog: " + e.getMessage());
            System.err.println("Original error: " + exception.getMessage());
        }
    }
}
import util.ConfigManager;
import util.SupabaseConnection;

public class TestSupabase {
    public static void main(String[] args) {
        try {
            System.out.println("Testing Supabase Connection...");
            
            // Initialize configuration
            ConfigManager.initialize();
            System.out.println("Configuration loaded");
            
            // Initialize Supabase connection
            SupabaseConnection.initialize();
            System.out.println("Supabase connection initialized");
            
            // Test connection
            if (SupabaseConnection.isConnectionValid()) {
                System.out.println("Supabase connection is valid");
                
                // Test a simple query
                String response = SupabaseConnection.executeGet("users", "select=id&limit=1");
                if (response != null) {
                    System.out.println("Test query successful");
                    System.out.println("Response: " + response);
                } else {
                    System.out.println("Test query failed");
                }
            } else {
                System.out.println("Supabase connection is not valid");
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
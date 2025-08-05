package library.exception;

/**
 * Exception thrown when a requested user cannot be found in the system.
 */
public class UserNotFoundException extends LibraryException {
    
    /**
     * Constructs a new UserNotFoundException for a specific user ID.
     * 
     * @param userId the ID of the user that was not found
     */
    public UserNotFoundException(String userId) {
        super("User not found with ID: " + userId);
    }
    
    /**
     * Constructs a new UserNotFoundException with a custom message.
     * 
     * @param message the detail message explaining the exception
     * @param userId the ID of the user that was not found
     */
    public UserNotFoundException(String message, String userId) {
        super(message + " (User ID: " + userId + ")");
    }
}
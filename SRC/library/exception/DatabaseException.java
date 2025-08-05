package library.exception;

/**
 * Exception thrown for database-related operations that fail.
 */
public class DatabaseException extends LibraryException {
    
    /**
     * Constructs a new DatabaseException with the specified detail message.
     * 
     * @param message the detail message explaining the exception
     */
    public DatabaseException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new DatabaseException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the exception
     * @param cause the cause of this exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
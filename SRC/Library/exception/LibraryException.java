package library.exception;

/**
 * Base exception class for all library management system exceptions.
 * This class serves as the root of the custom exception hierarchy.
 */
public class LibraryException extends Exception {
    
    /**
     * Constructs a new LibraryException with the specified detail message.
     * 
     * @param message the detail message explaining the exception
     */
    public LibraryException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new LibraryException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the exception
     * @param cause the cause of this exception
     */
    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new LibraryException with the specified cause.
     * 
     * @param cause the cause of this exception
     */
    public LibraryException(Throwable cause) {
        super(cause);
    }
}
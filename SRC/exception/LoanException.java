package library.exception;

/**
 * Exception thrown for loan-related operations that fail.
 */
public class LoanException extends LibraryException {
    
    /**
     * Constructs a new LoanException with the specified detail message.
     * 
     * @param message the detail message explaining the exception
     */
    public LoanException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new LoanException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the exception
     * @param cause the cause of this exception
     */
    public LoanException(String message, Throwable cause) {
        super(message, cause);
    }
}
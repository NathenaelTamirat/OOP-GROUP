package library.exception;

/**
 * Exception thrown when a requested book cannot be found in the system.
 */
public class BookNotFoundException extends LibraryException {
    
    /**
     * Constructs a new BookNotFoundException for a specific book ID.
     * 
     * @param bookId the ID of the book that was not found
     */
    public BookNotFoundException(String bookId) {
        super("Book not found with ID: " + bookId);
    }
    
    /**
     * Constructs a new BookNotFoundException with a custom message.
     * 
     * @param message the detail message explaining the exception
     */
    public BookNotFoundException(String message, String bookId) {
        super(message + " (Book ID: " + bookId + ")");
    }
}
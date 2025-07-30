
// Interface for book CRUD/search operations.

package interfaces;

import java.util.List;
import model.Book;

public interface BookOperations {
    boolean addBook(Book book);
    boolean updateBook(Book book);
    boolean deleteBook(int bookId);
    Book getBookById(int bookId);
    List<Book> searchBooks(String keyword);
    List<Book> getAllBooks();
}
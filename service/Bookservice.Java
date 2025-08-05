package service;

import model.Book;
import dao.SupabaseBookDao;
import util.Logger;
import java.util.List;

public class BookService {
    private SupabaseBookDao bookDao = new SupabaseBookDao();
    
    public boolean addBook(Book book) {
        try {
            boolean success = bookDao.addBook(book);
            if (success) {
                Logger.log("Book added: " + book.getTitle());
            }
            return success;
        } catch (Exception e) {
            Logger.log("Error adding book: " + e.getMessage());
            return false;
        }
    }
    
    public Book getBookById(int id) {
        try {
            return bookDao.getBookById(id);
        } catch (Exception e) {
            Logger.log("Error getting book by ID: " + e.getMessage());
            return null;
        }
    }
    
    public List<Book> getAllBooks() {
        try {
            return bookDao.getAllBooks();
        } catch (Exception e) {
            Logger.log("Error getting all books: " + e.getMessage());
            return null;
        }
    }
    
    public List<Book> searchBooks(String searchTerm) {
        try {
            return bookDao.searchBooks(searchTerm);
        } catch (Exception e) {
            Logger.log("Error searching books: " + e.getMessage());
            return null;
        }
    }
    
    public boolean updateBook(Book book) {
        try {
            boolean success = bookDao.updateBook(book);
            if (success) {
                Logger.log("Book updated: " + book.getTitle());
            }
            return success;
        } catch (Exception e) {
            Logger.log("Error updating book: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteBook(int bookId) {
        try {
            boolean success = bookDao.deleteBook(bookId);
            if (success) {
                Logger.log("Book deleted: " + bookId);
            }
            return success;
        } catch (Exception e) {
            Logger.log("Error deleting book: " + e.getMessage());
            return false;
        }
    }
    
    public List<Book> getAvailableBooks() {
        try {
            return bookDao.getAvailableBooks();
        } catch (Exception e) {
            Logger.log("Error getting available books: " + e.getMessage());
            return null;
        }
    }
    
    public List<Book> getIssuedBooks() {
        try {
            return bookDao.getIssuedBooks();
        } catch (Exception e) {
            Logger.log("Error getting issued books: " + e.getMessage());
            return null;
        }
    }
}

package dao;

import model.Book;
import util.SupabaseConnection;
import util.JsonUtils;
import util.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Supabase Book Data Access Object
 * 
 * This class handles all book-related database operations using Supabase REST API.
 * It provides CRUD operations for books with proper error handling and logging.
 * 
 * @author Library Management Team
 * @version 3.0
 * @since 2024
 */
public class SupabaseBookDao {
    
    /**
     * Adds a new book to the database
     * 
     * @param book The book to add
     * @return true if successful, false otherwise
     */
    public boolean addBook(Book book) {
        try {
            String jsonData = JsonUtils.createJson(
                "title", book.getTitle(),
                "author", book.getAuthor(),
                "isbn", book.getIsbn(),
                "issued", String.valueOf(book.isIssued())
            );
            
            String response = SupabaseConnection.executePost("books", jsonData);
            if (response != null) {
                Logger.log("Book added successfully: " + book.getTitle());
                return true;
            } else {
                Logger.log("Failed to add book: " + book.getTitle());
                return false;
            }
        } catch (Exception e) {
            Logger.log("Error adding book: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets a book by ID
     * 
     * @param id The book ID
     * @return Book object or null if not found
     */
    public Book getBookById(int id) {
        try {
            String queryParams = "select=*&id=eq." + id;
            String response = SupabaseConnection.executeGet("books", queryParams);
            
            if (response != null && !response.equals("[]")) {
                String bookJson = JsonUtils.getFirstElement(response);
                if (bookJson != null) {
                    return parseBookFromJson(bookJson);
                }
            }
        } catch (Exception e) {
            Logger.log("Error getting book by ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Gets a book by ISBN
     * 
     * @param isbn The ISBN to search for
     * @return Book object or null if not found
     */
    public Book getBookByIsbn(String isbn) {
        try {
            String queryParams = "select=*&isbn=eq." + isbn;
            String response = SupabaseConnection.executeGet("books", queryParams);
            
            if (response != null && !response.equals("[]")) {
                String bookJson = JsonUtils.getFirstElement(response);
                if (bookJson != null) {
                    return parseBookFromJson(bookJson);
                }
            }
        } catch (Exception e) {
            Logger.log("Error getting book by ISBN: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Gets all books from the database
     * 
     * @return List of all books
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try {
            String response = SupabaseConnection.executeGet("books", "select=*&order=id");
            
            if (response != null && !response.equals("[]")) {
                String content = response.substring(1, response.length() - 1);
                String[] bookJsons = splitJsonArray(content);
                
                for (String bookJson : bookJsons) {
                    if (bookJson != null && !bookJson.trim().isEmpty()) {
                        Book book = parseBookFromJson(bookJson);
                        if (book != null) {
                            books.add(book);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.log("Error getting all books: " + e.getMessage());
        }
        return books;
    }
    
    /**
     * Gets available books (not issued)
     * 
     * @return List of available books
     */
    public List<Book> getAvailableBooks() {
        List<Book> books = new ArrayList<>();
        try {
            String response = SupabaseConnection.executeGet("books", "select=*&issued=eq.false&order=title");
            
            if (response != null && !response.equals("[]")) {
                String content = response.substring(1, response.length() - 1);
                String[] bookJsons = splitJsonArray(content);
                
                for (String bookJson : bookJsons) {
                    if (bookJson != null && !bookJson.trim().isEmpty()) {
                        Book book = parseBookFromJson(bookJson);
                        if (book != null) {
                            books.add(book);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.log("Error getting available books: " + e.getMessage());
        }
        return books;
    }
    
    /**
     * Gets issued books
     * 
     * @return List of issued books
     */
    public List<Book> getIssuedBooks() {
        List<Book> books = new ArrayList<>();
        try {
            String response = SupabaseConnection.executeGet("books", "select=*&issued=eq.true&order=title");
            
            if (response != null && !response.equals("[]")) {
                String content = response.substring(1, response.length() - 1);
                String[] bookJsons = splitJsonArray(content);
                
                for (String bookJson : bookJsons) {
                    if (bookJson != null && !bookJson.trim().isEmpty()) {
                        Book book = parseBookFromJson(bookJson);
                        if (book != null) {
                            books.add(book);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.log("Error getting issued books: " + e.getMessage());
        }
        return books;
    }
    
    /**
     * Updates an existing book
     * 
     * @param book The book to update
     * @return true if successful, false otherwise
     */
    public boolean updateBook(Book book) {
        try {
            String jsonData = JsonUtils.createJson(
                "title", book.getTitle(),
                "author", book.getAuthor(),
                "isbn", book.getIsbn(),
                "issued", String.valueOf(book.isIssued())
            );
            
            String filter = "id=eq." + book.getId();
            String response = SupabaseConnection.executePatch("books", jsonData, filter);
            
            if (response != null) {
                Logger.log("Book updated successfully: " + book.getTitle());
                return true;
            } else {
                Logger.log("Failed to update book: " + book.getTitle());
                return false;
            }
        } catch (Exception e) {
            Logger.log("Error updating book: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deletes a book by ID
     * 
     * @param id The book ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteBook(int id) {
        try {
            String filter = "id=eq." + id;
            boolean success = SupabaseConnection.executeDelete("books", filter);
            
            if (success) {
                Logger.log("Book deleted successfully: ID " + id);
                return true;
            } else {
                Logger.log("Failed to delete book: ID " + id);
                return false;
            }
        } catch (Exception e) {
            Logger.log("Error deleting book: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Searches books by title, author, or ISBN
     * 
     * @param searchTerm The search term
     * @return List of matching books
     */
    public List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        try {
            // Search by title (case insensitive)
            String titleQuery = "select=*&title=ilike.*" + searchTerm + "*";
            String titleResponse = SupabaseConnection.executeGet("books", titleQuery);
            addBooksFromResponse(books, titleResponse);
            
            // Search by author (case insensitive)
            String authorQuery = "select=*&author=ilike.*" + searchTerm + "*";
            String authorResponse = SupabaseConnection.executeGet("books", authorQuery);
            addBooksFromResponse(books, authorResponse);
            
            // Search by ISBN (exact match)
            String isbnQuery = "select=*&isbn=eq." + searchTerm;
            String isbnResponse = SupabaseConnection.executeGet("books", isbnQuery);
            addBooksFromResponse(books, isbnResponse);
            
        } catch (Exception e) {
            Logger.log("Error searching books: " + e.getMessage());
        }
        return books;
    }
    
    /**
     * Updates book issue status
     * 
     * @param bookId The book ID
     * @param issued The issue status
     * @return true if successful, false otherwise
     */
    public boolean updateBookStatus(int bookId, boolean issued) {
        try {
            String jsonData = JsonUtils.createJson("issued", String.valueOf(issued));
            String filter = "id=eq." + bookId;
            String response = SupabaseConnection.executePatch("books", jsonData, filter);
            
            if (response != null) {
                Logger.log("Book status updated: ID " + bookId + " issued=" + issued);
                return true;
            } else {
                Logger.log("Failed to update book status: ID " + bookId);
                return false;
            }
        } catch (Exception e) {
            Logger.log("Error updating book status: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Parses a Book object from JSON string
     * 
     * @param bookJson JSON string representing a book
     * @return Book object or null if parsing fails
     */
    private Book parseBookFromJson(String bookJson) {
        try {
            String idStr = JsonUtils.extractValue(bookJson, "id");
            String title = JsonUtils.extractValue(bookJson, "title");
            String author = JsonUtils.extractValue(bookJson, "author");
            String isbn = JsonUtils.extractValue(bookJson, "isbn");
            String genre = JsonUtils.extractValue(bookJson, "genre");
            String status = JsonUtils.extractValue(bookJson, "status");
            String quantityStr = JsonUtils.extractValue(bookJson, "quantity");
            
            if (idStr != null && title != null && author != null && isbn != null) {
                int id = Integer.parseInt(idStr);
                String finalGenre = genre != null ? genre : "Unknown";
                String finalStatus = status != null ? status : "available";
                int quantity = quantityStr != null ? Integer.parseInt(quantityStr) : 1;
                return new Book(id, title, author, isbn, finalGenre, finalStatus, quantity);
            }
        } catch (Exception e) {
            Logger.log("Error parsing book JSON: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Adds books from a JSON response to the list
     * 
     * @param books List to add books to
     * @param response JSON response string
     */
    private void addBooksFromResponse(List<Book> books, String response) {
        if (response != null && !response.equals("[]")) {
            String content = response.substring(1, response.length() - 1);
            String[] bookJsons = splitJsonArray(content);
            
            for (String bookJson : bookJsons) {
                if (bookJson != null && !bookJson.trim().isEmpty()) {
                    Book book = parseBookFromJson(bookJson);
                    if (book != null && !containsBook(books, book.getId())) {
                        books.add(book);
                    }
                }
            }
        }
    }
    
    /**
     * Checks if a book list contains a book with the given ID
     * 
     * @param books List of books
     * @param bookId ID to check for
     * @return true if found, false otherwise
     */
    private boolean containsBook(List<Book> books, int bookId) {
        for (Book book : books) {
            if (book.getId() == bookId) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Splits a JSON array string into individual JSON objects
     * 
     * @param content JSON array content (without brackets)
     * @return Array of JSON object strings
     */
    private String[] splitJsonArray(String content) {
        List<String> objects = new ArrayList<>();
        int braceCount = 0;
        int start = -1;
        
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            
            if (c == '{') {
                if (start == -1) start = i;
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0 && start != -1) {
                    objects.add(content.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        
        return objects.toArray(new String[0]);
    }
} 
// File: model/Book.java
// Book entity: represents a book in the library.

package model;

public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private String status;
    private int quantity;
    private boolean issued;

    public Book(int id, String title, String author, String isbn, String genre, String status, int quantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.status = status;
        this.quantity = quantity;
        this.issued = false;
    }
    
    public Book(String title, String author, String isbn, String genre, String status, int quantity) {
        this(0, title, author, isbn, genre, status, quantity);
    }
    
    public Book(String title, String author, String isbn) {
        this(0, title, author, isbn, "Unknown", "available", 1);
    }
    
    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getGenre() { return genre; }
    public String getStatus() { return status; }
    public int getQuantity() { return quantity; }
    public boolean isIssued() { return issued; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setStatus(String status) { this.status = status; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setIssued(boolean issued) { this.issued = issued; }
}
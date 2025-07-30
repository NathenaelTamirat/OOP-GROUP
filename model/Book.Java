// File: model/Book.java
// Book entity: represents a book in the library.

package model;

public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private boolean issued;

    public Book(int id, String title, String author, String isbn, boolean issued) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.issued = issued;
    }
    public Book(String title, String author, String isbn) {
        this(0, title, author, isbn, false);
    }
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public boolean isIssued() { return issued; }
    public void setIssued(boolean issued) { this.issued = issued; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
}
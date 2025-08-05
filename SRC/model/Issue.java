
// Issue entity: represents a book borrowed by a user.

package model;

import java.time.LocalDate;

public class Issue {
    private int id;
    private int bookId;
    private int userId;
    private LocalDate issueDate;
    private LocalDate returnDate;

    public Issue(int id, int bookId, int userId, LocalDate issueDate, LocalDate returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
    }
    
    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public int getUserId() { return userId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    
    public void setId(int id) { this.id = id; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
}

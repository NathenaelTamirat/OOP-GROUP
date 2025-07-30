
// Issue entity: represents a book borrowed by a user.

package model;

public class Issue {
    private int id;
    private int bookId;
    private int userId;
    private String issueDate;
    private String returnDate;

    public Issue(int id, int bookId, int userId, String issueDate, String returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
    }
    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public int getUserId() { return userId; }
    public String getIssueDate() { return issueDate; }
    public String getReturnDate() { return returnDate; }
}
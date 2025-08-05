
package service;

import dao.SupabaseBookDao;
import dao.SupabaseIssueDao;
import dao.SupabaseUserDao;
import java.time.LocalDate;
import java.util.List;
import model.Book;
import model.Issue;
import model.User;
import util.Logger;

public class IssueService {
    private SupabaseIssueDao issueDao = new SupabaseIssueDao();
    private SupabaseBookDao bookDao = new SupabaseBookDao();
    private SupabaseUserDao userDao = new SupabaseUserDao();
    
    public boolean issueBook(int bookId, int userId) {
        try {
            Logger.log("Attempting to issue book ID: " + bookId + " to user ID: " + userId);
            
            // Check if book exists
            Book book = bookDao.getBookById(bookId);
            if (book == null) {
                Logger.log("Book not found with ID: " + bookId);
                return false;
            }
            
            // Check if user exists
            User user = userDao.getUserById(userId);
            if (user == null) {
                Logger.log("User not found with ID: " + userId);
                return false;
            }
            
            // Check if book is available (not already issued)
            if ("issued".equals(book.getStatus()) || book.isIssued()) {
                Logger.log("Book already issued: " + book.getTitle());
                return false;
            }
            
            // Check if user already has this book issued
            List<Issue> userIssues = issueDao.getIssuesByUserId(userId);
            for (Issue existingIssue : userIssues) {
                if (existingIssue.getBookId() == bookId && existingIssue.getReturnDate() == null) {
                    Logger.log("User already has this book issued");
                    return false;
                }
            }
            
            // Create issue record
            Issue issue = new Issue(0, bookId, userId, LocalDate.now(), null);
            boolean success = issueDao.addIssue(issue);
            
            if (success) {
                // Update book status to issued
                book.setStatus("issued");
                book.setIssued(true);
                bookDao.updateBook(book);
                Logger.log("Book issued successfully: " + book.getTitle() + " to " + user.getName());
                return true;
            } else {
                Logger.log("Failed to create issue record");
                return false;
            }
        } catch (Exception e) {
            Logger.log("Error issuing book: " + e.getMessage());
            return false;
        }
    }
    
    public boolean returnBook(int issueId) {
        try {
            Logger.log("Attempting to return book for issue ID: " + issueId);
            
            // Get the issue record
            Issue issue = issueDao.getIssueById(issueId);
            if (issue == null) {
                Logger.log("Issue not found with ID: " + issueId);
                return false;
            }
            
            // Check if already returned
            if (issue.getReturnDate() != null) {
                Logger.log("Book already returned for issue ID: " + issueId);
                return false;
            }
            
            // Get the book
            Book book = bookDao.getBookById(issue.getBookId());
            if (book == null) {
                Logger.log("Book not found for issue ID: " + issueId);
                return false;
            }
            
            // Update issue with return date
            issue.setReturnDate(LocalDate.now());
            boolean success = issueDao.updateIssue(issue);
            
            if (success) {
                // Update book status to available
                book.setStatus("available");
                book.setIssued(false);
                bookDao.updateBook(book);
                Logger.log("Book returned successfully: " + book.getTitle());
                return true;
            } else {
                Logger.log("Failed to update issue record");
                return false;
            }
        } catch (Exception e) {
            Logger.log("Error returning book: " + e.getMessage());
            return false;
        }
    }
    
    public List<Issue> getIssuesByUserId(int userId) {
        try {
            return issueDao.getIssuesByUserId(userId);
        } catch (Exception e) {
            Logger.log("Error getting issues by user: " + e.getMessage());
            return null;
        }
    }
    
    public List<Issue> getAllIssues() {
        try {
            return issueDao.getAllIssues();
        } catch (Exception e) {
            Logger.log("Error getting all issues: " + e.getMessage());
            return null;
        }
    }
    
    public double calculateFine(Issue issue) {
        if (issue.getReturnDate() != null) {
            return 0.0; // Book already returned
        }
        
        LocalDate dueDate = issue.getIssueDate().plusDays(14); // 14 days loan period
        LocalDate today = LocalDate.now();
        
        if (today.isAfter(dueDate)) {
            long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
            return daysLate * 0.50; // $0.50 per day
        }
        
        return 0.0;
    }
}
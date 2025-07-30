
// Service layer for book issue/return; calls IssueDAO.

package service;

import dao.IssueDAO;
import model.Issue;

public class IssueService {
    private IssueDAO issueDAO = new IssueDAO();

    public boolean issueBook(int bookId, int userId) {
        return issueDAO.issueBook(bookId, userId);
    }
    public boolean returnBook(int bookId, int userId) {
        return issueDAO.returnBook(bookId, userId);
    }
    public Issue getIssueDetails(int issueId) {
        return issueDAO.getIssueDetails(issueId);
    }
}
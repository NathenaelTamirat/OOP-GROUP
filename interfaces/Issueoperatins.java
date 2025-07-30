
// Interface for book issue/return operations.

package interfaces;

import model.Issue;

public interface IssueOperations {
    boolean issueBook(int bookId, int userId);
    boolean returnBook(int bookId, int userId);
    Issue getIssueDetails(int issueId);
}
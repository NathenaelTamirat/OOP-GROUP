
// Tab for issuing and returning books; admin can issue/return for any user, students can only return.

package ui;

import javax.swing.*;
import service.IssueService;
import model.User;

public class IssuePanel extends JPanel {
    private IssueService issueService = new IssueService();
    private User user;

    public IssuePanel(User user) {
        this.user = user;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JButton issueBtn = new JButton("Issue Book");
        JButton returnBtn = new JButton("Return Book");

        if (user.getRole().equals("admin")) add(issueBtn);
        add(returnBtn);

        issueBtn.addActionListener(e -> {
            int bookId = Integer.parseInt(JOptionPane.showInputDialog("Book ID:"));
            int userId = Integer.parseInt(JOptionPane.showInputDialog("User ID:"));
            if (issueService.issueBook(bookId, userId)) {
                JOptionPane.showMessageDialog(this, "Book issued!");
            } else {
                JOptionPane.showMessageDialog(this, "Error issuing book.");
            }
        });

        returnBtn.addActionListener(e -> {
            int bookId = Integer.parseInt(JOptionPane.showInputDialog("Book ID:"));
            int userId = user.getRole().equals("admin")
                ? Integer.parseInt(JOptionPane.showInputDialog("User ID:"))
                : user.getId();
            if (issueService.returnBook(bookId, userId)) {
                JOptionPane.showMessageDialog(this, "Book returned!");
            } else {
                JOptionPane.showMessageDialog(this, "Error returning book.");
            }
        });
    }
}
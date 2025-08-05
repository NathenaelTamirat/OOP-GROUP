package UI;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Issue;
import model.Book;
import model.User;
import service.IssueService;
import service.BookService;
import service.UserService;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class IssuesPanel extends JPanel {
    
    private IssueService issueService;
    private BookService bookService;
    private UserService userService;
    private JTable issuesTable;
    private DefaultTableModel tableModel;
    private JButton issueBookButton;
    private JButton returnBookButton;
    private JButton refreshButton;
    private JButton calculateFineButton;
    private String userRole;
    
    public IssuesPanel() {
        this.issueService = new IssueService();
        this.bookService = new BookService();
        this.userService = new UserService();
        this.userRole = "admin"; // Default
        setupUI();
        loadIssues();
    }
    
    public IssuesPanel(String userRole) {
        this.issueService = new IssueService();
        this.bookService = new BookService();
        this.userService = new UserService();
        this.userRole = userRole;
        setupUI();
        loadIssues();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Issue/Return Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        
        // Buttons panel - different for admin vs student
        JPanel buttonsPanel = createButtonsPanel();
        
        // Table
        createIssuesTable();
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(issuesTable), BorderLayout.CENTER);
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);
        
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBackground(new Color(108, 117, 125));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadIssues());
        
        panel.add(refreshButton);
        
        // Add buttons for all users (admin and student)
        issueBookButton = new JButton("Issue Book");
        returnBookButton = new JButton("Return Book");
        calculateFineButton = new JButton("Calculate Fine");
        
        // Style buttons
        issueBookButton.setFont(new Font("Arial", Font.BOLD, 12));
        returnBookButton.setFont(new Font("Arial", Font.BOLD, 12));
        calculateFineButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        issueBookButton.setBackground(new Color(40, 167, 69));
        returnBookButton.setBackground(new Color(255, 193, 7));
        calculateFineButton.setBackground(new Color(220, 53, 69));
        
        issueBookButton.setForeground(Color.WHITE);
        returnBookButton.setForeground(Color.WHITE);
        calculateFineButton.setForeground(Color.WHITE);
        
        // Add action listeners
        issueBookButton.addActionListener(e -> showIssueBookDialog());
        returnBookButton.addActionListener(e -> returnSelectedBook());
        calculateFineButton.addActionListener(e -> calculateFineForSelected());
        
        panel.add(issueBookButton);
        panel.add(returnBookButton);
        panel.add(calculateFineButton);
        
        return panel;
    }
    
    private void createIssuesTable() {
        String[] columns = {"Issue ID", "Book ID", "User ID", "Issue Date", "Return Date", "Status", "Fine"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        issuesTable = new JTable(tableModel);
        issuesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        issuesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        issuesTable.setRowHeight(25);
        issuesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set column widths
        issuesTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Issue ID
        issuesTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Book ID
        issuesTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // User ID
        issuesTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Issue Date
        issuesTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Return Date
        issuesTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        issuesTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Fine
    }
    
    private void loadIssues() {
        try {
            List<Issue> issues = issueService.getAllIssues();
            updateTableData(issues);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading issues: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTableData(List<Issue> issues) {
        tableModel.setRowCount(0);
        
        if (issues != null) {
            for (Issue issue : issues) {
                String status = issue.getReturnDate() != null ? "Returned" : "Active";
                double fine = issueService.calculateFine(issue);
                String fineStr = fine > 0 ? String.format("$%.2f", fine) : "$0.00";
                
                Object[] row = {
                    issue.getId(),
                    issue.getBookId(),
                    issue.getUserId(),
                    issue.getIssueDate().toString(),
                    issue.getReturnDate() != null ? issue.getReturnDate().toString() : "Not returned",
                    status,
                    fineStr
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void showIssueBookDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Issue Book", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Form fields
        JTextField bookIdField = new JTextField(10);
        JTextField userIdField = new JTextField(10);
        
        // Add fields to form
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Book ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(bookIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(userIdField, gbc);
        
        // Add some spacing
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(Box.createVerticalStrut(20), gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton issueButton = new JButton("Issue Book");
        JButton cancelButton = new JButton("Cancel");
        
        issueButton.addActionListener(e -> {
            try {
                String bookIdText = bookIdField.getText().trim();
                String userIdText = userIdField.getText().trim();
                
                if (bookIdText.isEmpty() || userIdText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter both Book ID and User ID", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int bookId = Integer.parseInt(bookIdText);
                int userId = Integer.parseInt(userIdText);
                
                // Show processing message
                issueButton.setEnabled(false);
                issueButton.setText("Processing...");
                
                boolean success = issueService.issueBook(bookId, userId);
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Book issued successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadIssues();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to issue book. Please check:\n- Book ID exists and is available\n- User ID exists\n- Book is not already issued", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numeric IDs", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                issueButton.setEnabled(true);
                issueButton.setText("Issue Book");
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(issueButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void returnSelectedBook() {
        int selectedRow = issuesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an issue to return", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int issueId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 5);
        
        if ("Returned".equals(status)) {
            JOptionPane.showMessageDialog(this, "This book has already been returned", "Already Returned", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to return this book?\n\nIssue ID: " + issueId + "\nBook ID: " + tableModel.getValueAt(selectedRow, 1) + "\nUser ID: " + tableModel.getValueAt(selectedRow, 2),
            "Confirm Return",
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                // Show processing message
                returnBookButton.setEnabled(false);
                returnBookButton.setText("Processing...");
                
                boolean success = issueService.returnBook(issueId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadIssues();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to return book. Please check if the issue exists and is not already returned.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error returning book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                returnBookButton.setEnabled(true);
                returnBookButton.setText("Return Book");
            }
        }
    }
    
    private void calculateFineForSelected() {
        int selectedRow = issuesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an issue to calculate fine", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int issueId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 5);
        
        if ("Returned".equals(status)) {
            JOptionPane.showMessageDialog(this, "Cannot calculate fine for returned book", "Already Returned", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            // Get the issue object
            List<Issue> issues = issueService.getAllIssues();
            Issue selectedIssue = null;
            for (Issue issue : issues) {
                if (issue.getId() == issueId) {
                    selectedIssue = issue;
                    break;
                }
            }
            
            if (selectedIssue != null) {
                double fine = issueService.calculateFine(selectedIssue);
                JOptionPane.showMessageDialog(this, 
                    "Fine for this issue: $" + String.format("%.2f", fine), 
                    "Fine Calculation", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Could not find the selected issue", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error calculating fine: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 
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

    // Services for handling issue, book, and user operations
    private IssueService issueService;
    private BookService bookService;
    private UserService userService;

    // UI components
    private JTable issuesTable;
    private DefaultTableModel tableModel;
    private JButton issueBookButton;
    private JButton returnBookButton;
    private JButton refreshButton;
    private JButton calculateFineButton;
    private String userRole;

    // Default constructor (admin role by default)
    public IssuesPanel() {
        this("admin");
    }

    // Constructor for assigning user role (admin or student)
    public IssuesPanel(String userRole) {
        this.issueService = new IssueService();
        this.bookService = new BookService();
        this.userService = new UserService();
        this.userRole = userRole;
        setupUI();
        loadIssues();
    }

    // Setup the main UI layout
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title label at the top
        JLabel titleLabel = new JLabel("Issue/Return Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));

        // Create action buttons and table
        JPanel buttonsPanel = createButtonsPanel();
        createIssuesTable();

        // Top panel holds title and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(issuesTable), BorderLayout.CENTER);
    }

    // Create and configure all action buttons
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);

        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBackground(new Color(108, 117, 125));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadIssues());

        issueBookButton = new JButton("Issue Book");
        returnBookButton = new JButton("Return Book");
        calculateFineButton = new JButton("Calculate Fine");

        issueBookButton.setFont(new Font("Arial", Font.BOLD, 12));
        returnBookButton.setFont(new Font("Arial", Font.BOLD, 12));
        calculateFineButton.setFont(new Font("Arial", Font.BOLD, 12));

        issueBookButton.setBackground(new Color(40, 167, 69));
        returnBookButton.setBackground(new Color(255, 193, 7));
        calculateFineButton.setBackground(new Color(220, 53, 69));

        issueBookButton.setForeground(Color.WHITE);
        returnBookButton.setForeground(Color.WHITE);
        calculateFineButton.setForeground(Color.WHITE);

        issueBookButton.addActionListener(e -> showIssueBookDialog());
        returnBookButton.addActionListener(e -> returnSelectedBook());
        calculateFineButton.addActionListener(e -> calculateFineForSelected());

        panel.add(refreshButton);
        panel.add(issueBookButton);
        panel.add(returnBookButton);
        panel.add(calculateFineButton);

        return panel;
    }

    // Initialize and configure the table that displays issue records
    private void createIssuesTable() {
        String[] columns = {"Issue ID", "Book ID", "User ID", "Issue Date", "Return Date", "Status", "Fine"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing
            }
        };

        issuesTable = new JTable(tableModel);
        issuesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        issuesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        issuesTable.setRowHeight(25);
        issuesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set preferred column widths for readability
        issuesTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        issuesTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        issuesTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        issuesTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        issuesTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        issuesTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        issuesTable.getColumnModel().getColumn(6).setPreferredWidth(80);
    }

    // Load issue data from service and update table
    private void loadIssues() {
        try {
            List<Issue> issues = issueService.getAllIssues();
            updateTableData(issues);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading issues: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Update the JTable with issue data
    private void updateTableData(List<Issue> issues) {
        tableModel.setRowCount(0); // Clear existing data

        if (issues != null) {
            for (Issue issue : issues) {
                // Determine if returned or still active
                String status = issue.getReturnDate() != null ? "Returned" : "Active";
                // Calculate fine for each issue
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

    // Remaining methods (showIssueBookDialog, returnSelectedBook, calculateFineForSelected) remain unchanged.
}

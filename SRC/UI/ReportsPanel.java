package UI;

import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import service.BookService;
import service.IssueService;
import service.UserService;
import model.Book;
import model.Issue;
import model.User;

public class ReportsPanel extends JPanel {
    
    private BookService bookService;
    private IssueService issueService;
    private UserService userService;
    
    // Report components
    private JLabel totalBooksLabel;
    private JLabel totalUsersLabel;
    private JLabel totalIssuesLabel;
    private JLabel totalFinesLabel;
    private JTextArea detailedReportArea;
    
    public ReportsPanel() {
        this.bookService = new BookService();
        this.issueService = new IssueService();
        this.userService = new UserService();
        setupUI();
        loadReports();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Reports & Analytics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        
        // Statistics panel
        JPanel statsPanel = createStatisticsPanel();
        
        // Detailed reports panel
        JPanel reportsPanel = createReportsPanel();
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(reportsPanel, BorderLayout.CENTER);
    }
    
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Create stat cards
        totalBooksLabel = createStatLabel("Total Books", "0", new Color(0, 102, 204));
        totalUsersLabel = createStatLabel("Total Users", "0", new Color(40, 167, 69));
        totalIssuesLabel = createStatLabel("Active Issues", "0", new Color(255, 193, 7));
        totalFinesLabel = createStatLabel("Total Fines", "$0", new Color(220, 53, 69));
        
        panel.add(totalBooksLabel);
        panel.add(totalUsersLabel);
        panel.add(totalIssuesLabel);
        panel.add(totalFinesLabel);
        
        return panel;
    }
    
    private JLabel createStatLabel(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);
        
        return new JLabel() {
            @Override
            public void setText(String text) {
                valueLabel.setText(text);
            }
        };
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Reports title
        JLabel reportsTitle = new JLabel("Detailed Reports");
        reportsTitle.setFont(new Font("Arial", Font.BOLD, 18));
        reportsTitle.setForeground(new Color(0, 102, 204));
        
        // Detailed report area
        detailedReportArea = new JTextArea();
        detailedReportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailedReportArea.setEditable(false);
        detailedReportArea.setLineWrap(true);
        detailedReportArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(detailedReportArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonsPanel.setBackground(Color.WHITE);
        
        JButton generateReportButton = new JButton("Generate Report");
        JButton exportButton = new JButton("Export Report");
        JButton refreshButton = new JButton("Refresh");
        
        // Style buttons
        generateReportButton.setFont(new Font("Arial", Font.BOLD, 12));
        exportButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        generateReportButton.setBackground(new Color(0, 102, 204));
        exportButton.setBackground(new Color(40, 167, 69));
        refreshButton.setBackground(new Color(108, 117, 125));
        
        generateReportButton.setForeground(Color.WHITE);
        exportButton.setForeground(Color.WHITE);
        refreshButton.setForeground(Color.WHITE);
        
        // Add action listeners
        generateReportButton.addActionListener(e -> generateDetailedReport());
        exportButton.addActionListener(e -> exportReport());
        refreshButton.addActionListener(e -> loadReports());
        
        buttonsPanel.add(generateReportButton);
        buttonsPanel.add(exportButton);
        buttonsPanel.add(refreshButton);
        
        panel.add(reportsTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadReports() {
        try {
            // Load basic statistics
            List<Book> books = bookService.getAllBooks();
            List<User> users = userService.getAllUsers();
            List<Issue> issues = issueService.getAllIssues();
            
            // Update statistics
            totalBooksLabel.setText(String.valueOf(books.size()));
            totalUsersLabel.setText(String.valueOf(users.size()));
            
            // Count active issues (not returned)
            long activeIssues = issues.stream()
                .filter(issue -> issue.getReturnDate() == null)
                .count();
            totalIssuesLabel.setText(String.valueOf(activeIssues));
            
            // Calculate total fines
            double totalFines = 0.0;
            for (Issue issue : issues) {
                if (issue.getReturnDate() == null) {
                    totalFines += issueService.calculateFine(issue);
                }
            }
            totalFinesLabel.setText("$" + String.format("%.2f", totalFines));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading reports: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateDetailedReport() {
        try {
            StringBuilder report = new StringBuilder();
            report.append("LIBRARY MANAGEMENT SYSTEM - DETAILED REPORT\n");
            report.append("==========================================\n\n");
            
            // Books Report
            List<Book> books = bookService.getAllBooks();
            report.append("BOOKS INVENTORY:\n");
            report.append("Total Books: ").append(books.size()).append("\n");
            
            // Count by status
            Map<String, Long> bookStatusCount = books.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    Book::getStatus, java.util.stream.Collectors.counting()));
            
            report.append("Available: ").append(bookStatusCount.getOrDefault("available", 0L)).append("\n");
            report.append("Unavailable: ").append(bookStatusCount.getOrDefault("unavailable", 0L)).append("\n\n");
            
            // Users Report
            List<User> users = userService.getAllUsers();
            report.append("USER STATISTICS:\n");
            report.append("Total Users: ").append(users.size()).append("\n");
            
            // Count by role
            Map<String, Long> userRoleCount = users.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    User::getRole, java.util.stream.Collectors.counting()));
            
            report.append("Admins: ").append(userRoleCount.getOrDefault("admin", 0L)).append("\n");
            report.append("Students: ").append(userRoleCount.getOrDefault("student", 0L)).append("\n\n");
            
            // Issues Report
            List<Issue> issues = issueService.getAllIssues();
            report.append("ISSUE STATISTICS:\n");
            report.append("Total Issues: ").append(issues.size()).append("\n");
            
            long activeIssues = issues.stream()
                .filter(issue -> issue.getReturnDate() == null)
                .count();
            long returnedIssues = issues.size() - activeIssues;
            
            report.append("Active Issues: ").append(activeIssues).append("\n");
            report.append("Returned Issues: ").append(returnedIssues).append("\n\n");
            
            // Fines Report
            report.append("FINANCIAL REPORT:\n");
            double totalFines = 0.0;
            int overdueCount = 0;
            
            for (Issue issue : issues) {
                if (issue.getReturnDate() == null) {
                    double fine = issueService.calculateFine(issue);
                    totalFines += fine;
                    if (fine > 0) {
                        overdueCount++;
                    }
                }
            }
            
            report.append("Total Outstanding Fines: $").append(String.format("%.2f", totalFines)).append("\n");
            report.append("Overdue Books: ").append(overdueCount).append("\n\n");
            
            // Top Books Report
            report.append("TOP BOOKS (by issues):\n");
            Map<Integer, Long> bookIssueCount = issues.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    Issue::getBookId, java.util.stream.Collectors.counting()));
            
            bookIssueCount.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    try {
                        Book book = bookService.getBookById(entry.getKey());
                        if (book != null) {
                            report.append("- ").append(book.getTitle())
                                  .append(" (").append(entry.getValue()).append(" issues)\n");
                        }
                    } catch (Exception e) {
                        report.append("- Book ID ").append(entry.getKey())
                              .append(" (").append(entry.getValue()).append(" issues)\n");
                    }
                });
            
            detailedReportArea.setText(report.toString());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error generating report: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportReport() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export Report");
            fileChooser.setSelectedFile(new java.io.File("library_report.txt"));
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                java.io.PrintWriter writer = new java.io.PrintWriter(file);
                writer.print(detailedReportArea.getText());
                writer.close();
                
                JOptionPane.showMessageDialog(this, 
                    "Report exported successfully to: " + file.getAbsolutePath(), 
                    "Export Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error exporting report: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 
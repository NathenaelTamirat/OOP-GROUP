package library.gui;

import library.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RequestManagementPanel extends JPanel {
    
    private LibraryManagementGUI mainFrame;
    private JTable requestsTable;
    private DefaultTableModel tableModel;
    private Timer refreshTimer;
    
    public RequestManagementPanel(LibraryManagementGUI mainFrame) {
        this.mainFrame = mainFrame;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        startAutoRefresh();
        refreshData();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"Request ID", "Username", "Book Title", "Request Date", "Return Date", "Status", "Book Info"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        requestsTable = new JTable(tableModel);
        requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestsTable.setRowHeight(30);
    }
    
    private void setupLayout() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBorder(BorderFactory.createTitledBorder("Pending Book Requests"));
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshData());
        headerPanel.add(refreshBtn);
        
        JButton debugBtn = new JButton("Debug Info");
        debugBtn.addActionListener(e -> showDebugInfo());
        headerPanel.add(debugBtn);
        
        add(headerPanel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton approveBtn = new JButton("Approve Request");
        JButton denyBtn = new JButton("Deny Request");
        
        approveBtn.addActionListener(e -> approveRequest());
        denyBtn.addActionListener(e -> denyRequest());
        
        actionPanel.add(approveBtn);
        actionPanel.add(denyBtn);
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Auto-refresh every 3 seconds to catch new requests
        refreshTimer = new Timer(3000, e -> refreshData());
    }
    
    private void startAutoRefresh() {
        refreshTimer.start();
    }
    
    public void refreshData() {
        tableModel.setRowCount(0);
        
        for (BorrowRequest request : mainFrame.getBorrowRequests().values()) {
            Book book = mainFrame.getBooks().get(request.getBookId());
            
            tableModel.addRow(new Object[]{
                request.getRequestId(),
                request.getUsername(),
                book != null ? book.getTitle() : "Unknown Book",
                request.getRequestDate(),
                request.getReturnDate(),
                request.getStatus().getDisplayName(),
                "Available: " + (book != null ? book.getAvailableCopies() : 0)
            });
        }
    }
    
    private void approveRequest() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a request to approve!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String requestId = (String) tableModel.getValueAt(selectedRow, 0);
        BorrowRequest request = mainFrame.getBorrowRequests().get(requestId);
        
        if (request == null) {
            JOptionPane.showMessageDialog(this, "Request not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (request.getStatus() != BorrowRequest.RequestStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "Only pending requests can be approved!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Book book = mainFrame.getBooks().get(request.getBookId());
        if (book == null || book.getAvailableCopies() <= 0) {
            JOptionPane.showMessageDialog(this, "Book is not available!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create loan
        String loanId = "L" + String.format("%03d", mainFrame.getLoans().size() + 1);
        LocalDateTime dueDate = request.getReturnDate().atTime(23, 59);
        Loan loan = new Loan(loanId, getUserIdByUsername(request.getUsername()), request.getBookId(), dueDate);
        
        // Update book availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        
        // Add loan to system
        mainFrame.getLoans().put(loanId, loan);
        
        // Update request status
        request.setStatus(BorrowRequest.RequestStatus.APPROVED);
        
        // Log the approval
        mainFrame.getLogger().logInfo("Request approved: " + requestId + " for user: " + request.getUsername() + ", Loan created: " + loanId);
        
        // Remember selected row
        int selectedRowAfter = selectedRow;
        
        // Refresh data and restore selection
        refreshData();
        if (selectedRowAfter < requestsTable.getRowCount()) {
            requestsTable.setRowSelectionInterval(selectedRowAfter, selectedRowAfter);
        }
        
        // Force refresh of book panel to show updated availability
        SwingUtilities.invokeLater(() -> {
            try {
                mainFrame.showPanel("BOOKS"); // This will refresh the book display
                mainFrame.showPanel("REQUESTS"); // Return to requests panel
            } catch (Exception e) {
                // Ignore refresh errors
            }
        });
        
        mainFrame.updateStatus("Request approved: " + requestId + " - Loan created: " + loanId);
        mainFrame.updateDashboardStats();
        
        JOptionPane.showMessageDialog(this, "Request approved successfully!\nLoan ID: " + loanId + "\nBook availability updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void denyRequest() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a request to deny!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String requestId = (String) tableModel.getValueAt(selectedRow, 0);
        BorrowRequest request = mainFrame.getBorrowRequests().get(requestId);
        
        if (request == null) {
            JOptionPane.showMessageDialog(this, "Request not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (request.getStatus() != BorrowRequest.RequestStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "Only pending requests can be denied!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String reason = JOptionPane.showInputDialog(this, "Enter reason for denial (optional):", "Deny Request", JOptionPane.QUESTION_MESSAGE);
        
        request.setStatus(BorrowRequest.RequestStatus.DENIED);
        
        // Log the denial
        String logMessage = "Request denied: " + requestId + " for user: " + request.getUsername();
        if (reason != null && !reason.trim().isEmpty()) {
            logMessage += " - Reason: " + reason.trim();
        }
        mainFrame.getLogger().logInfo(logMessage);
        
        // Remember selected row
        int selectedRowAfter = selectedRow;
        
        // Refresh data and restore selection
        refreshData();
        if (selectedRowAfter < requestsTable.getRowCount()) {
            requestsTable.setRowSelectionInterval(selectedRowAfter, selectedRowAfter);
        }
        
        mainFrame.updateStatus("Request denied: " + requestId);
        
        JOptionPane.showMessageDialog(this, "Request denied successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String getUserIdByUsername(String username) {
        // First check registered users from login dialog
        for (LoginDialog.UserCredential userCred : LoginDialog.getRegisteredUsers().values()) {
            if (userCred.username.equals(username)) {
                // Find corresponding user in main system
                return mainFrame.getUsers().values().stream()
                    .filter(user -> user.getEmail().equals(userCred.email))
                    .map(User::getId)
                    .findFirst()
                    .orElse("U001");
            }
        }
        
        // Fallback to name matching
        return mainFrame.getUsers().values().stream()
            .filter(user -> user.getName().equals(username))
            .map(User::getId)
            .findFirst()
            .orElse("U001");
    }
    
    private void showDebugInfo() {
        StringBuilder debug = new StringBuilder();
        debug.append("Total Requests: ").append(mainFrame.getBorrowRequests().size()).append("\n\n");
        
        for (BorrowRequest request : mainFrame.getBorrowRequests().values()) {
            debug.append("Request ID: ").append(request.getRequestId()).append("\n");
            debug.append("Username: ").append(request.getUsername()).append("\n");
            debug.append("Book ID: ").append(request.getBookId()).append("\n");
            debug.append("Status: ").append(request.getStatus()).append("\n");
            debug.append("Request Date: ").append(request.getRequestDate()).append("\n");
            debug.append("Return Date: ").append(request.getReturnDate()).append("\n\n");
        }
        
        JTextArea textArea = new JTextArea(debug.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Debug Info - All Requests", JOptionPane.INFORMATION_MESSAGE);
    }
}
package library.gui;

import library.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserDashboardPanel extends JPanel {
    
    private LibraryManagementGUI mainFrame;
    private JTabbedPane tabbedPane;
    private JTable requestsTable, borrowedTable, returnedTable;
    private DefaultTableModel requestsModel, borrowedModel, returnedModel;
    
    public UserDashboardPanel(LibraryManagementGUI mainFrame) {
        this.mainFrame = mainFrame;
        initializeComponents();
        setupLayout();
        refreshData();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        tabbedPane = new JTabbedPane();
        
        // Requests tab
        String[] requestColumns = {"Request ID", "Book Title", "Request Date", "Return Date", "Status"};
        requestsModel = new DefaultTableModel(requestColumns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        requestsTable = new JTable(requestsModel);
        requestsTable.setRowHeight(25);
        
        // Borrowed books tab
        String[] borrowedColumns = {"Loan ID", "Book Title", "Borrow Date", "Due Date", "Status"};
        borrowedModel = new DefaultTableModel(borrowedColumns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        borrowedTable = new JTable(borrowedModel);
        borrowedTable.setRowHeight(25);
        
        // Returned books tab
        String[] returnedColumns = {"Loan ID", "Book Title", "Borrow Date", "Return Date", "Fine"};
        returnedModel = new DefaultTableModel(returnedColumns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        returnedTable = new JTable(returnedModel);
        returnedTable.setRowHeight(25);
    }
    
    private void setupLayout() {
        // Requests panel
        JPanel requestsPanel = new JPanel(new BorderLayout());
        requestsPanel.add(new JScrollPane(requestsTable), BorderLayout.CENTER);
        
        JPanel requestsHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshRequestsBtn = new JButton("Refresh");
        refreshRequestsBtn.addActionListener(e -> refreshRequestsData());
        requestsHeader.add(refreshRequestsBtn);
        requestsPanel.add(requestsHeader, BorderLayout.NORTH);
        
        // Borrowed books panel
        JPanel borrowedPanel = new JPanel(new BorderLayout());
        borrowedPanel.add(new JScrollPane(borrowedTable), BorderLayout.CENTER);
        
        JPanel borrowedHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBorrowedBtn = new JButton("Refresh");
        refreshBorrowedBtn.addActionListener(e -> refreshBorrowedData());
        borrowedHeader.add(refreshBorrowedBtn);
        borrowedPanel.add(borrowedHeader, BorderLayout.NORTH);
        
        // Returned books panel
        JPanel returnedPanel = new JPanel(new BorderLayout());
        returnedPanel.add(new JScrollPane(returnedTable), BorderLayout.CENTER);
        
        JPanel returnedHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshReturnedBtn = new JButton("Refresh");
        refreshReturnedBtn.addActionListener(e -> refreshReturnedData());
        returnedHeader.add(refreshReturnedBtn);
        returnedPanel.add(returnedHeader, BorderLayout.NORTH);
        
        // Add tabs
        tabbedPane.addTab("My Requests", requestsPanel);
        tabbedPane.addTab("Borrowed Books", borrowedPanel);
        tabbedPane.addTab("Return History", returnedPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    public void refreshData() {
        refreshRequestsData();
        refreshBorrowedData();
        refreshReturnedData();
    }
    
    private void refreshRequestsData() {
        requestsModel.setRowCount(0);
        String currentUser = mainFrame.getUserSession().getCurrentUser();
        
        for (BorrowRequest request : mainFrame.getBorrowRequests().values()) {
            if (request.getUsername().equals(currentUser)) {
                Book book = mainFrame.getBooks().get(request.getBookId());
                requestsModel.addRow(new Object[]{
                    request.getRequestId(),
                    book != null ? book.getTitle() : "Unknown Book",
                    request.getRequestDate(),
                    request.getReturnDate(),
                    request.getStatus().getDisplayName()
                });
            }
        }
    }
    
    private void refreshBorrowedData() {
        borrowedModel.setRowCount(0);
        String currentUser = mainFrame.getUserSession().getCurrentUser();
        
        for (Loan loan : mainFrame.getLoans().values()) {
            User user = mainFrame.getUsers().get(loan.getUserId());
            if (user != null && user.getName().equals(currentUser) && loan.getStatus().isActive()) {
                Book book = mainFrame.getBooks().get(loan.getBookId());
                borrowedModel.addRow(new Object[]{
                    loan.getId(),
                    book != null ? book.getTitle() : "Unknown Book",
                    loan.getLoanDate().toLocalDate(),
                    loan.getDueDate().toLocalDate(),
                    loan.getStatus().getDisplayName()
                });
            }
        }
    }
    
    private void refreshReturnedData() {
        returnedModel.setRowCount(0);
        String currentUser = mainFrame.getUserSession().getCurrentUser();
        
        for (Loan loan : mainFrame.getLoans().values()) {
            User user = mainFrame.getUsers().get(loan.getUserId());
            if (user != null && user.getName().equals(currentUser) && loan.getStatus() == LoanStatus.RETURNED) {
                Book book = mainFrame.getBooks().get(loan.getBookId());
                returnedModel.addRow(new Object[]{
                    loan.getId(),
                    book != null ? book.getTitle() : "Unknown Book",
                    loan.getLoanDate().toLocalDate(),
                    loan.getReturnDate() != null ? loan.getReturnDate().toLocalDate() : "N/A",
                    String.format("$%.2f", loan.getFineAmount())
                });
            }
        }
    }
}
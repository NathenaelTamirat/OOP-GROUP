package library.gui;

import library.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoanManagementPanel extends JPanel {
    
    private LibraryManagementGUI mainFrame;
    private JTable loansTable;
    private DefaultTableModel tableModel;
    private JTextField searchField, loanIdField, dueDateField;
    private JComboBox<String> statusFilter;
    private JComboBox<User> userCombo;
    private JComboBox<Book> bookCombo;
    private JTextArea notesArea, loanDetailsArea;
    private JLabel selectedLoanLabel;
    
    public LoanManagementPanel(LibraryManagementGUI mainFrame) {
        this.mainFrame = mainFrame;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] columnNames = {"Loan ID", "User", "Book", "Loan Date", "Due Date", "Return Date", "Status", "Fine"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        loansTable = new JTable(tableModel);
        loansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loansTable.setRowHeight(25);
        
        searchField = new JTextField(20);
        statusFilter = new JComboBox<>(new String[]{"All", "Active", "Overdue", "Returned", "Lost"});
        loanIdField = new JTextField(15);
        
        userCombo = new JComboBox<>();
        userCombo.setRenderer(new ComboBoxRenderer.UserRenderer());
        bookCombo = new JComboBox<>();
        bookCombo.setRenderer(new ComboBoxRenderer.BookRenderer());
        
        dueDateField = new JTextField(15);
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        
        selectedLoanLabel = new JLabel("No loan selected");
        loanDetailsArea = new JTextArea(5, 30);
        loanDetailsArea.setEditable(false);
        loanDetailsArea.setBackground(getBackground());
        loanDetailsArea.setBorder(BorderFactory.createEtchedBorder());
        
        LocalDateTime defaultDueDate = LocalDateTime.now().plusDays(14);
        dueDateField.setText(defaultDueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }
    
    private void setupLayout() {
        add(createSearchPanel(), BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        JScrollPane tableScrollPane = new JScrollPane(loansTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 250));
        leftPanel.add(tableScrollPane, BorderLayout.CENTER);
        leftPanel.add(createLoanFormPanel(), BorderLayout.SOUTH);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(createLoanDetailsPanel());
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));
        
        panel.add(new JLabel("Search:"));
        panel.add(searchField);
        
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> performSearch());
        panel.add(searchBtn);
        
        panel.add(Box.createHorizontalStrut(20));
        panel.add(new JLabel("Status:"));
        panel.add(statusFilter);
        
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> clearSearch());
        panel.add(clearBtn);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshData());
        panel.add(refreshBtn);
        
        return panel;
    }
    

    private JPanel createLoanFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Issue New Loan"));
        
        // Create form fields panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(new JLabel("Loan ID:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(loanIdField, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("User:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(userCombo, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(new JLabel("Book:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(bookCombo, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("Due Date:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(dueDateField, gbc);
        
        // Row 3 - Notes
        gbc.gridx = 0; gbc.gridy = 2;
        fieldsPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        fieldsPanel.add(new JScrollPane(notesArea), gbc);
        
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        
        JButton issueLoanButton = new JButton("Issue Loan");
        issueLoanButton.addActionListener(e -> issueLoan());
        buttonsPanel.add(issueLoanButton);
        
        JButton clearFormButton = new JButton("Clear Form");
        clearFormButton.addActionListener(e -> clearLoanForm());
        buttonsPanel.add(clearFormButton);
        
        formPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return formPanel;
    }
    

    private JPanel createLoanDetailsPanel() {
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Loan Details & Actions"));
        detailsPanel.setPreferredSize(new Dimension(350, 400));
        
        // Selected loan label
        selectedLoanLabel.setFont(selectedLoanLabel.getFont().deriveFont(Font.BOLD));
        detailsPanel.add(selectedLoanLabel, BorderLayout.NORTH);
        
        // Loan details area
        JScrollPane detailsScrollPane = new JScrollPane(loanDetailsArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Action buttons panel
        JPanel actionsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        actionsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JButton returnBookButton = new JButton("Return Book");
        returnBookButton.addActionListener(e -> returnBook());
        actionsPanel.add(returnBookButton);
        
        JButton extendLoanButton = new JButton("Extend Loan");
        extendLoanButton.addActionListener(e -> extendLoan());
        actionsPanel.add(extendLoanButton);
        
        JButton markLostButton = new JButton("Mark as Lost");
        markLostButton.addActionListener(e -> markAsLost());
        actionsPanel.add(markLostButton);
        
        JButton calculateFineButton = new JButton("Calculate Fine");
        calculateFineButton.addActionListener(e -> calculateFine());
        actionsPanel.add(calculateFineButton);
        
        JButton viewUserLoansButton = new JButton("View User's Loans");
        viewUserLoansButton.addActionListener(e -> viewUserLoans());
        actionsPanel.add(viewUserLoansButton);
        
        detailsPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        return detailsPanel;
    }
    
    private void setupEventHandlers() {
        loansTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && loansTable.getSelectedRow() >= 0) {
                loadLoanDetails(loansTable.getSelectedRow());
            }
        });
        
        statusFilter.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());
    }
    
    public void refreshData() {
        refreshTable();
        refreshComboBoxes();
        updateLoanStatuses();
    }
    
    private void updateLoanStatuses() {
        mainFrame.getLoans().values().forEach(Loan::updateStatus);
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        
        for (Loan loan : mainFrame.getLoans().values()) {
            // Update loan status before displaying
            loan.updateStatus();
            
            User user = mainFrame.getUsers().get(loan.getUserId());
            Book book = mainFrame.getBooks().get(loan.getBookId());
            
            String returnDateDisplay = "Not Returned";
            if (loan.getReturnDate() != null) {
                returnDateDisplay = loan.getReturnDate().toLocalDate().toString();
            }
            
            tableModel.addRow(new Object[]{
                loan.getId(),
                user != null ? user.getName() : "Unknown User",
                book != null ? book.getTitle() : "Unknown Book",
                loan.getLoanDate().toLocalDate(),
                loan.getDueDate().toLocalDate(),
                returnDateDisplay,
                loan.getStatus().getDisplayName(),
                String.format("$%.2f", loan.getFineAmount())
            });
        }
    }
    
    private void refreshComboBoxes() {
        userCombo.removeAllItems();
        bookCombo.removeAllItems();
        
        mainFrame.getUsers().values().stream()
            .filter(User::canBorrowMore)
            .forEach(userCombo::addItem);
        
        mainFrame.getBooks().values().stream()
            .filter(Book::isAvailable)
            .forEach(bookCombo::addItem);
    }
    

    private void performSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        String statusFilter = (String) this.statusFilter.getSelectedItem();
        
        tableModel.setRowCount(0);
        
        for (Loan loan : mainFrame.getLoans().values()) {
            User user = mainFrame.getUsers().get(loan.getUserId());
            Book book = mainFrame.getBooks().get(loan.getBookId());
            
            boolean matchesSearch = searchText.isEmpty() || 
                    loan.getId().toLowerCase().contains(searchText) ||
                    (user != null && user.getName().toLowerCase().contains(searchText)) ||
                    (book != null && book.getTitle().toLowerCase().contains(searchText));
            
            boolean matchesStatus = "All".equals(statusFilter) ||
                    loan.getStatus().getDisplayName().equals(statusFilter);
            
            if (matchesSearch && matchesStatus) {
                // Update loan status before displaying
                loan.updateStatus();
                
                String returnDateDisplay = "Not Returned";
                if (loan.getReturnDate() != null) {
                    returnDateDisplay = loan.getReturnDate().toLocalDate().toString();
                }
                
                Object[] rowData = {
                    loan.getId(),
                    user != null ? user.getName() : "Unknown User",
                    book != null ? book.getTitle() : "Unknown Book",
                    loan.getLoanDate().toLocalDate(),
                    loan.getDueDate().toLocalDate(),
                    returnDateDisplay,
                    loan.getStatus().getDisplayName(),
                    String.format("$%.2f", loan.getFineAmount())
                };
                tableModel.addRow(rowData);
            }
        }
        
        mainFrame.updateStatus("Search completed - " + tableModel.getRowCount() + " loans found");
    }
    

    private void clearSearch() {
        searchField.setText("");
        statusFilter.setSelectedItem("All");
        refreshTable();
        mainFrame.updateStatus("Search cleared - showing all loans");
    }
    

    private void loadLoanDetails(int selectedRow) {
        String loanId = (String) tableModel.getValueAt(selectedRow, 0);
        Loan loan = mainFrame.getLoans().get(loanId);
        
        if (loan != null) {
            selectedLoanLabel.setText("Selected: " + loanId);
            loanDetailsArea.setText(loan.getLoanSummary());
        }
    }
    

    private void issueLoan() {
        try {
            if (!validateLoanForm()) {
                return;
            }
            
            String loanId = loanIdField.getText().trim();
            if (mainFrame.getLoans().containsKey(loanId)) {
                JOptionPane.showMessageDialog(this, "Loan ID already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User selectedUser = (User) userCombo.getSelectedItem();
            Book selectedBook = (Book) bookCombo.getSelectedItem();
            
            if (selectedUser == null || selectedBook == null) {
                JOptionPane.showMessageDialog(this, "Please select both user and book!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse due date
            LocalDateTime dueDate;
            try {
                dueDate = LocalDateTime.parse(dueDateField.getText().trim(), 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid due date format! Use: yyyy-MM-dd HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create loan
            Loan loan = new Loan(loanId, selectedUser.getId(), selectedBook.getId(), dueDate);
            loan.setNotes(notesArea.getText().trim());
            
            // Process the loan
            if (selectedBook.borrowCopy(loan)) {
                selectedUser.addLoan(loan);
                mainFrame.getLoans().put(loan.getId(), loan);
                
                refreshData();
                clearLoanForm();
                mainFrame.updateStatus("Loan issued successfully: " + loanId);
                mainFrame.getLogger().logBookBorrowed(selectedUser.getId(), selectedBook.getId(), loanId);
                
            } else {
                JOptionPane.showMessageDialog(this, "Failed to issue loan - book may not be available!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error issuing loan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.getLogger().logError("Error issuing loan", e);
        }
    }
    

    private void returnBook() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a loan to return!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String loanId = (String) tableModel.getValueAt(selectedRow, 0);
        Loan loan = mainFrame.getLoans().get(loanId);
        
        if (loan == null) {
            JOptionPane.showMessageDialog(this, "Loan not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (loan.getStatus() == LoanStatus.RETURNED) {
            JOptionPane.showMessageDialog(this, "Book is already returned!", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Return the book
        if (loan.returnBook()) {
            Book book = mainFrame.getBooks().get(loan.getBookId());
            User user = mainFrame.getUsers().get(loan.getUserId());
            
            if (book != null) {
                book.returnCopy(loanId);
            }
            if (user != null) {
                user.returnBook(loan.getBookId());
            }
            
            refreshData();
            loadLoanDetails(selectedRow);
            
            String message = "Book returned successfully!";
            if (loan.getFineAmount() > 0) {
                message += " Fine amount: $" + String.format("%.2f", loan.getFineAmount());
            }
            
            mainFrame.updateStatus(message);
            mainFrame.getLogger().logInfo("Book returned: " + loanId);
            
            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    

    private void extendLoan() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a loan to extend!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String loanId = (String) tableModel.getValueAt(selectedRow, 0);
        Loan loan = mainFrame.getLoans().get(loanId);
        
        if (loan == null || loan.getStatus() == LoanStatus.RETURNED) {
            JOptionPane.showMessageDialog(this, "Cannot extend this loan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String daysStr = JOptionPane.showInputDialog(this, "Enter number of days to extend:", "Extend Loan", JOptionPane.QUESTION_MESSAGE);
        if (daysStr != null) {
            try {
                int days = Integer.parseInt(daysStr.trim());
                if (days > 0 && loan.extendDueDate(days)) {
                    refreshData();
                    loadLoanDetails(selectedRow);
                    mainFrame.updateStatus("Loan extended by " + days + " days");
                    mainFrame.getLogger().logInfo("Loan extended: " + loanId + " by " + days + " days");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to extend loan!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number of days!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    

    private void markAsLost() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a loan to mark as lost!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String loanId = (String) tableModel.getValueAt(selectedRow, 0);
        Loan loan = mainFrame.getLoans().get(loanId);
        
        if (loan == null || loan.getStatus() == LoanStatus.RETURNED) {
            JOptionPane.showMessageDialog(this, "Cannot mark this loan as lost!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to mark this book as lost?", 
                "Confirm Lost Book", 
                JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            String fineStr = JOptionPane.showInputDialog(this, "Enter replacement fine amount:", "Lost Book Fine", JOptionPane.QUESTION_MESSAGE);
            if (fineStr != null) {
                try {
                    double fine = Double.parseDouble(fineStr.trim());
                    loan.markAsLost(fine);
                    
                    refreshData();
                    loadLoanDetails(selectedRow);
                    mainFrame.updateStatus("Book marked as lost with fine: $" + String.format("%.2f", fine));
                    mainFrame.getLogger().logInfo("Book marked as lost: " + loanId);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid fine amount!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    

    private void calculateFine() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a loan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String loanId = (String) tableModel.getValueAt(selectedRow, 0);
        Loan loan = mainFrame.getLoans().get(loanId);
        
        if (loan != null) {
            double fine = loan.calculateFine();
            refreshData();
            loadLoanDetails(selectedRow);
            
            String message = fine > 0 ? 
                    "Fine calculated: $" + String.format("%.2f", fine) : 
                    "No fine - book is not overdue";
            
            JOptionPane.showMessageDialog(this, message, "Fine Calculation", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.updateStatus(message);
        }
    }
    

    private void viewUserLoans() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a loan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String loanId = (String) tableModel.getValueAt(selectedRow, 0);
        Loan loan = mainFrame.getLoans().get(loanId);
        
        if (loan != null) {
            User user = mainFrame.getUsers().get(loan.getUserId());
            if (user != null) {
                // Switch to user management panel and show user details
                mainFrame.showPanel("USERS");
                // Note: In a more sophisticated implementation, we would pass the user ID
                // to the user panel to automatically select and show the user
            }
        }
    }
    

    private boolean validateLoanForm() {
        if (loanIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Loan ID is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (userCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a user!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (bookCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a book!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (dueDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Due date is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void clearLoanForm() {
        loanIdField.setText("");
        userCombo.setSelectedItem(null);
        bookCombo.setSelectedItem(null);
        notesArea.setText("");
        
        LocalDateTime defaultDueDate = LocalDateTime.now().plusDays(14);
        dueDateField.setText(defaultDueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }
}
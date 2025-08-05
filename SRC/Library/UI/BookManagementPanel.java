package library.gui;

import library.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class BookManagementPanel extends JPanel {
    
    private LibraryManagementGUI mainFrame;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<Category> categoryFilter;
    
    private JTextField idField, titleField, authorField, isbnField, yearField;
    private JTextField totalCopiesField, availableCopiesField;
    private JComboBox<Category> categoryCombo;
    
    public BookManagementPanel(LibraryManagementGUI mainFrame) {
        this.mainFrame = mainFrame;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] columns;
        if (mainFrame.getUserSession().isAdmin()) {
            columns = new String[]{"ID", "Title", "Author", "ISBN", "Year", "Category", "Available/Total"};
        } else {
            columns = new String[]{"ID", "Title", "Author", "ISBN", "Year", "Category", "Available/Total", "Action"};
        }
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        booksTable = new JTable(tableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.setRowHeight(25);
        
        searchField = new JTextField(20);
        categoryFilter = new JComboBox<>();
        categoryFilter.addItem(null);
        
        idField = new JTextField(10);
        titleField = new JTextField(20);
        authorField = new JTextField(20);
        isbnField = new JTextField(15);
        yearField = new JTextField(10);
        totalCopiesField = new JTextField(10);
        totalCopiesField.setText("1");
        availableCopiesField = new JTextField(10);
        availableCopiesField.setText("1");
        categoryCombo = new JComboBox<>();
        categoryCombo.setRenderer(new ComboBoxRenderer.CategoryRenderer());
        
        setTooltips();
    }
    
    private void setTooltips() {
        idField.setToolTipText("Enter a unique book ID (e.g., B001)");
        titleField.setToolTipText("Enter the book title");
        authorField.setToolTipText("Enter the author's name");
        isbnField.setToolTipText("Enter the ISBN number");
        yearField.setToolTipText("Enter publication year (e.g., 2023)");
        totalCopiesField.setToolTipText("Enter total number of copies");
        availableCopiesField.setToolTipText("Enter available copies (≤ total copies)");
    }
    

    private void setupLayout() {
        // Create search panel
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);
        
        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        
        // Create table panel
        JScrollPane tableScrollPane = new JScrollPane(booksTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 300));
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Create form panel - only for admins
        if (mainFrame.getUserSession().isAdmin()) {
            JPanel formPanel = createFormPanel();
            contentPanel.add(formPanel, BorderLayout.SOUTH);
        }
        
        add(contentPanel, BorderLayout.CENTER);
    }
    

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(searchButton);
        
        searchPanel.add(Box.createHorizontalStrut(20));
        
        searchPanel.add(new JLabel("Category:"));
        searchPanel.add(categoryFilter);
        
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearSearch());
        searchPanel.add(clearButton);
        
        return searchPanel;
    }
    

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));
        
        // Create form fields panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(idField, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(titleField, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(authorField, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(isbnField, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        fieldsPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(yearField, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(categoryCombo, gbc);
        
        // Row 4
        gbc.gridx = 0; gbc.gridy = 3;
        fieldsPanel.add(new JLabel("Total Copies:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(totalCopiesField, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("Available:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(availableCopiesField, gbc);
        
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Create buttons panel - admin only
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Add Book");
        addButton.addActionListener(e -> addBook());
        addButton.setToolTipText("Add a new book to the library");
        buttonsPanel.add(addButton);
        
        JButton generateIdButton = new JButton("Generate ID");
        generateIdButton.addActionListener(e -> generateBookId());
        generateIdButton.setToolTipText("Generate a unique book ID");
        buttonsPanel.add(generateIdButton);
        
        JButton updateButton = new JButton("Update Book");
        updateButton.addActionListener(e -> updateBook());
        buttonsPanel.add(updateButton);
        
        JButton deleteButton = new JButton("Delete Book");
        deleteButton.addActionListener(e -> deleteBook());
        buttonsPanel.add(deleteButton);
        
        JButton clearFormButton = new JButton("Clear Form");
        clearFormButton.addActionListener(e -> clearForm());
        buttonsPanel.add(clearFormButton);
        
        formPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return formPanel;
    }
    

    private void setupEventHandlers() {
        // Table selection listener
        booksTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = booksTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadBookToForm(selectedRow);
                }
            }
        });
        
        // Add mouse click listener for user requests
        if (!mainFrame.getUserSession().isAdmin()) {
            booksTable.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = booksTable.rowAtPoint(evt.getPoint());
                    int col = booksTable.columnAtPoint(evt.getPoint());
                    if (row >= 0 && col == 7) { // Action column
                        String action = (String) tableModel.getValueAt(row, col);
                        if ("Request".equals(action)) {
                            requestToBorrow(row);
                        }
                    }
                }
            });
        }
        
        // Category filter listener
        categoryFilter.addActionListener(e -> performSearch());
        
        // Search field enter key listener
        searchField.addActionListener(e -> performSearch());
    }
    
    /**
     * Refreshes the data in the table and combo boxes.
     */
    public void refreshData() {
        refreshTable();
        refreshCategoryComboBoxes();
    }
    

    private void refreshTable() {
        tableModel.setRowCount(0);
        
        for (Book book : mainFrame.getBooks().values()) {
            if (mainFrame.getUserSession().isAdmin()) {
                Object[] rowData = {
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.getPublicationYear(),
                    book.getCategory() != null ? book.getCategory().getName() : "No Category",
                    book.getAvailableCopies() + "/" + book.getTotalCopies()
                };
                tableModel.addRow(rowData);
            } else {
                Object[] rowData = {
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.getPublicationYear(),
                    book.getCategory() != null ? book.getCategory().getName() : "No Category",
                    book.getAvailableCopies() + "/" + book.getTotalCopies(),
                    book.getAvailableCopies() > 0 ? "Request" : "Unavailable"
                };
                tableModel.addRow(rowData);
            }
        }
    }
    

    private void refreshCategoryComboBoxes() {
        categoryFilter.removeAllItems();
        categoryFilter.addItem(null); // "All categories" option
        
        categoryCombo.removeAllItems();
        categoryCombo.addItem(null); // "No category" option
        
        for (Category category : mainFrame.getCategories().values()) {
            categoryFilter.addItem(category);
            categoryCombo.addItem(category);
        }
    }
    

    private void performSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        Category selectedCategory = (Category) categoryFilter.getSelectedItem();
        
        tableModel.setRowCount(0);
        
        for (Book book : mainFrame.getBooks().values()) {
            boolean matchesSearch = searchText.isEmpty() || 
                    book.getTitle().toLowerCase().contains(searchText) ||
                    book.getAuthor().toLowerCase().contains(searchText) ||
                    book.getIsbn().toLowerCase().contains(searchText);
            
            boolean matchesCategory = selectedCategory == null || 
                    (book.getCategory() != null && book.getCategory().equals(selectedCategory));
            
            if (matchesSearch && matchesCategory) {
                if (mainFrame.getUserSession().isAdmin()) {
                    Object[] rowData = {
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getIsbn(),
                        book.getPublicationYear(),
                        book.getCategory() != null ? book.getCategory().getName() : "No Category",
                        book.getAvailableCopies() + "/" + book.getTotalCopies()
                    };
                    tableModel.addRow(rowData);
                } else {
                    Object[] rowData = {
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getIsbn(),
                        book.getPublicationYear(),
                        book.getCategory() != null ? book.getCategory().getName() : "No Category",
                        book.getAvailableCopies() + "/" + book.getTotalCopies(),
                        book.getAvailableCopies() > 0 ? "Request" : "Unavailable"
                    };
                    tableModel.addRow(rowData);
                }
            }
        }
        
        mainFrame.updateStatus("Search completed - " + tableModel.getRowCount() + " books found");
    }
    

    private void clearSearch() {
        searchField.setText("");
        categoryFilter.setSelectedItem(null);
        refreshTable();
        mainFrame.updateStatus("Search cleared - showing all books");
    }
    
    private void loadBookToForm(int selectedRow) {
        if (!mainFrame.getUserSession().isAdmin()) {
            // Users can only view book details, not edit
            String bookId = (String) tableModel.getValueAt(selectedRow, 0);
            Book book = mainFrame.getBooks().get(bookId);
            
            if (book != null) {
                String bookDetails = String.format(
                    "Book Details:\n\n" +
                    "ID: %s\n" +
                    "Title: %s\n" +
                    "Author: %s\n" +
                    "ISBN: %s\n" +
                    "Year: %d\n" +
                    "Category: %s\n" +
                    "Available Copies: %d/%d",
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.getPublicationYear(),
                    book.getCategory() != null ? book.getCategory().getName() : "No Category",
                    book.getAvailableCopies(),
                    book.getTotalCopies()
                );
                
                JOptionPane.showMessageDialog(this, bookDetails, "Book Information", JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }
        
        String bookId = (String) tableModel.getValueAt(selectedRow, 0);
        Book book = mainFrame.getBooks().get(bookId);
        
        if (book != null) {
            idField.setText(book.getId());
            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            isbnField.setText(book.getIsbn());
            yearField.setText(String.valueOf(book.getPublicationYear()));
            totalCopiesField.setText(String.valueOf(book.getTotalCopies()));
            availableCopiesField.setText(String.valueOf(book.getAvailableCopies()));
            categoryCombo.setSelectedItem(book.getCategory());
        }
    }
    

    private void addBook() {
        try {
            if (!validateForm()) {
                return;
            }
            
            String id = idField.getText().trim();
            if (mainFrame.getBooks().containsKey(id)) {
                JOptionPane.showMessageDialog(this, "Book ID already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Book book = createBookFromForm();
            mainFrame.getBooks().put(book.getId(), book);
            
            refreshTable();
            clearForm();
            mainFrame.updateStatus("Book added successfully: " + book.getTitle());
            mainFrame.getLogger().logInfo("Book added: " + book.getId());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.getLogger().logError("Error adding book", e);
        }
    }
    

    private void updateBook() {
        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a book to update!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!validateForm()) {
                return;
            }
            
            Book existingBook = mainFrame.getBooks().get(id);
            if (existingBook == null) {
                JOptionPane.showMessageDialog(this, "Book not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update book properties
            existingBook.setTitle(titleField.getText().trim());
            existingBook.setAuthor(authorField.getText().trim());
            existingBook.setIsbn(isbnField.getText().trim());
            existingBook.setPublicationYear(Integer.parseInt(yearField.getText().trim()));
            existingBook.setTotalCopies(Integer.parseInt(totalCopiesField.getText().trim()));
            existingBook.setAvailableCopies(Integer.parseInt(availableCopiesField.getText().trim()));
            existingBook.setCategory((Category) categoryCombo.getSelectedItem());
            
            refreshTable();
            mainFrame.updateStatus("Book updated successfully: " + existingBook.getTitle());
            mainFrame.getLogger().logInfo("Book updated: " + existingBook.getId());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.getLogger().logError("Error updating book", e);
        }
    }
    

    private void deleteBook() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Book book = mainFrame.getBooks().get(id);
        if (book == null) {
            JOptionPane.showMessageDialog(this, "Book not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if book has active loans
        boolean hasActiveLoans = mainFrame.getLoans().values().stream()
                .anyMatch(loan -> loan.getBookId().equals(id) && loan.getStatus().isActive());
        
        if (hasActiveLoans) {
            JOptionPane.showMessageDialog(this, "Cannot delete book with active loans!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete the book '" + book.getTitle() + "'?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            mainFrame.getBooks().remove(id);
            refreshTable();
            clearForm();
            mainFrame.updateStatus("Book deleted successfully: " + book.getTitle());
            mainFrame.getLogger().logInfo("Book deleted: " + id);
        }
    }
    

    private Book createBookFromForm() {
        String id = idField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String isbn = isbnField.getText().trim();
        int year = Integer.parseInt(yearField.getText().trim());
        int totalCopies = Integer.parseInt(totalCopiesField.getText().trim());
        int availableCopies = Integer.parseInt(availableCopiesField.getText().trim());
        Category category = (Category) categoryCombo.getSelectedItem();
        
        Book book = new Book(id, title, author, isbn, year, totalCopies, availableCopies);
        book.setCategory(category);
        
        return book;
    }
    

    private boolean validateForm() {
        if (idField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Book ID is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (authorField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Author is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (isbnField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ISBN is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            int year = Integer.parseInt(yearField.getText().trim());
            if (year < 1000 || year > 2030) {
                JOptionPane.showMessageDialog(this, "Please enter a valid publication year!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid publication year!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            String totalCopiesText = totalCopiesField.getText().trim();
            String availableCopiesText = availableCopiesField.getText().trim();
            
            if (totalCopiesText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Total copies field cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (availableCopiesText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Available copies field cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            int totalCopies = Integer.parseInt(totalCopiesText);
            int availableCopies = Integer.parseInt(availableCopiesText);
            
            if (totalCopies < 1) {
                JOptionPane.showMessageDialog(this, "Total copies must be at least 1!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (availableCopies < 0) {
                JOptionPane.showMessageDialog(this, "Available copies cannot be negative!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (availableCopies > totalCopies) {
                JOptionPane.showMessageDialog(this, "Available copies cannot exceed total copies!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid whole numbers for copies!\nMake sure there are no spaces or special characters.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    

    private void generateBookId() {
        int nextId = mainFrame.getBooks().size() + 1;
        String generatedId;
        
        // Keep generating until we find a unique ID
        do {
            generatedId = String.format("B%03d", nextId);
            nextId++;
        } while (mainFrame.getBooks().containsKey(generatedId));
        
        idField.setText(generatedId);
        mainFrame.updateStatus("Generated book ID: " + generatedId);
    }
    

    private void clearForm() {
        idField.setText("");
        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        yearField.setText("");
        totalCopiesField.setText("1"); // Set default value
        availableCopiesField.setText("1"); // Set default value
        categoryCombo.setSelectedItem(null);
    }
    

    private void requestToBorrow(int selectedRow) {
        String bookId = (String) tableModel.getValueAt(selectedRow, 0);
        Book book = mainFrame.getBooks().get(bookId);
        
        if (book == null || book.getAvailableCopies() <= 0) {
            JOptionPane.showMessageDialog(this, "Book is not available for borrowing!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create date picker dialog
        JDialog dateDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Select Return Date", true);
        dateDialog.setSize(700, 450);
        dateDialog.setLocationRelativeTo(this);
        dateDialog.setResizable(false);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel bookLabel = new JLabel("Request to Borrow: " + book.getTitle());
        bookLabel.setFont(bookLabel.getFont().deriveFont(Font.BOLD, 16f));
        titlePanel.add(bookLabel);
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel dateLabel = new JLabel("Return Date:");
        dateLabel.setFont(dateLabel.getFont().deriveFont(Font.BOLD, 14f));
        formPanel.add(dateLabel, gbc);
        
        // Much larger date input field
        JTextField dateField = new JTextField(35);
        dateField.setText(java.time.LocalDate.now().plusDays(14).toString());
        dateField.setFont(dateField.getFont().deriveFont(Font.PLAIN, 18f));
        dateField.setPreferredSize(new Dimension(400, 50));
        dateField.setMinimumSize(new Dimension(400, 50));
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(dateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 15, 15, 15);
        JLabel helpLabel = new JLabel("<html><center><i><b>Enter date in format: YYYY-MM-DD</b><br/>Example: " + java.time.LocalDate.now().plusDays(7).toString() + "<br/>Date must be in the future</i></center></html>");
        helpLabel.setForeground(new Color(100, 100, 100));
        helpLabel.setFont(helpLabel.getFont().deriveFont(Font.ITALIC, 13f));
        formPanel.add(helpLabel, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton submitBtn = new JButton("Submit Request");
        submitBtn.setPreferredSize(new Dimension(150, 40));
        submitBtn.setFont(submitBtn.getFont().deriveFont(Font.BOLD, 14f));
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(120, 40));
        cancelBtn.setFont(cancelBtn.getFont().deriveFont(14f));
        
        submitBtn.addActionListener(e -> {
            try {
                java.time.LocalDate returnDate = java.time.LocalDate.parse(dateField.getText().trim());
                if (returnDate.isBefore(java.time.LocalDate.now().plusDays(1))) {
                    JOptionPane.showMessageDialog(dateDialog, "Return date must be after today!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create borrow request
                String requestId = "R" + String.format("%03d", mainFrame.getBorrowRequests().size() + 1);
                String currentUser = mainFrame.getUserSession().getCurrentUser();
                BorrowRequest request = new BorrowRequest(requestId, currentUser, bookId, returnDate);
                
                // Add to requests map
                mainFrame.getBorrowRequests().put(requestId, request);
                
                // Log the request creation
                mainFrame.getLogger().logInfo("Borrow request created: " + requestId + " by user: " + currentUser + " for book: " + book.getTitle());
                
                // Request created successfully
                
                JOptionPane.showMessageDialog(dateDialog, "Your request has been sent. Awaiting admin approval.\nRequest ID: " + requestId, "Success", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.updateStatus("Borrow request submitted: " + book.getTitle() + " (ID: " + requestId + ")");
                dateDialog.dispose();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dateDialog, "Invalid date format! Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dateDialog.dispose());
        
        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dateDialog.add(panel);
        dateDialog.setVisible(true);
    }
}
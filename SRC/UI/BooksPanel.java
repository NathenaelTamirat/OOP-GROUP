package UI;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Book;
import service.BookService;
import java.util.List;

public class BooksPanel extends JPanel {
    
    // UI and service components
    private BookService bookService;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private String userRole;

    // Default constructor (sets role as "admin")
    public BooksPanel() {
        this.bookService = new BookService();
        this.userRole = "admin"; // Default role
        setupUI();               // Build the UI
        loadBooks();             // Load initial list of books
    }

    // Overloaded constructor to accept role (admin/user)
    public BooksPanel(String userRole) {
        this.bookService = new BookService();
        this.userRole = userRole;
        setupUI();
        loadBooks();
    }

    // Set up all UI components and layout
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create title label
        JLabel titleLabel = new JLabel("Books Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));

        // Create search and button panels
        JPanel searchPanel = createSearchPanel();
        JPanel buttonsPanel = createButtonsPanel();

        // Create and configure the table
        createBooksTable();

        // Combine title, search, and buttons into the top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Add components to the main layout
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(booksTable), BorderLayout.CENTER);
    }

    // Creates the panel for search functionality
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setBackground(new Color(0, 102, 204));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> searchBooks());

        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(searchButton);

        return panel;
    }

    // Creates the buttons panel with role-based actions
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);

        // Refresh button
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBackground(new Color(108, 117, 125));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadBooks());
        panel.add(refreshButton);

        // Admin-only buttons (can also be shown for user if needed)
        addButton = new JButton("Add Book");
        editButton = new JButton("Edit Book");
        deleteButton = new JButton("Delete Book");

        // Set fonts and colors
        addButton.setFont(new Font("Arial", Font.BOLD, 12));
        editButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));

        addButton.setBackground(new Color(40, 167, 69));   // Green
        editButton.setBackground(new Color(255, 193, 7));  // Yellow
        deleteButton.setBackground(new Color(220, 53, 69));// Red

        addButton.setForeground(Color.WHITE);
        editButton.setForeground(Color.WHITE);
        deleteButton.setForeground(Color.WHITE);

        // Button actions
        addButton.addActionListener(e -> showAddBookDialog());
        editButton.addActionListener(e -> editSelectedBook());
        deleteButton.addActionListener(e -> deleteSelectedBook());

        // Add buttons to panel
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);

        return panel;
    }

    // Create the JTable that shows books
    private void createBooksTable() {
        String[] columnNames = {"ID", "Title", "Author", "ISBN", "Genre", "Status", "Quantity"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table cells are not editable
            }
        };

        booksTable = new JTable(tableModel);
        booksTable.setFont(new Font("Arial", Font.PLAIN, 12));
        booksTable.setRowHeight(25);
        booksTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set preferred column widths
        booksTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        booksTable.getColumnModel().getColumn(1).setPreferredWidth(200);  // Title
        booksTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Author
        booksTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // ISBN
        booksTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Genre
        booksTable.getColumnModel().getColumn(5).setPreferredWidth(80);   // Status
        booksTable.getColumnModel().getColumn(6).setPreferredWidth(80);   // Quantity
    }

    // Load all books from the database and display them in the table
    private void loadBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            updateTableData(books);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading books: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Update the JTable with a list of books
    private void updateTableData(List<Book> books) {
        tableModel.setRowCount(0); // Clear existing rows
        for (Book book : books) {
            Object[] row = {
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getGenre(),
                book.getStatus(),
                book.getQuantity()
            };
            tableModel.addRow(row);
        }
    }

    // Search books by title, author, or ISBN
    private void searchBooks() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadBooks(); // If empty, show all books
            return;
        }

        try {
            List<Book> allBooks = bookService.getAllBooks();
            List<Book> filteredBooks = allBooks.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(searchTerm.toLowerCase()) ||
                              book.getAuthor().toLowerCase().contains(searchTerm.toLowerCase()) ||
                              book.getIsbn().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
            updateTableData(filteredBooks);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error searching books: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Show dialog to add a new book
    private void showAddBookDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Book", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField isbnField = new JTextField(20);
        JTextField genreField = new JTextField(20);
        JTextField quantityField = new JTextField(20);

        // Add form labels and fields
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; formPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1; formPanel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1; formPanel.add(isbnField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Genre:"), gbc);
        gbc.gridx = 1; formPanel.add(genreField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; formPanel.add(quantityField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add Book");
        JButton cancelButton = new JButton("Cancel");

        // Action to add a book
        addButton.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String isbn = isbnField.getText().trim();
                String genre = genreField.getText().trim();
                String quantityText = quantityField.getText().trim();

                if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all required fields", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int quantity = quantityText.isEmpty() ? 1 : Integer.parseInt(quantityText);

                Book newBook = new Book(title, author, isbn, genre, "available", quantity);
                boolean success = bookService.addBook(newBook);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadBooks();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add book", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid quantity", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Triggered when user selects a book and clicks "Edit"
    private void editSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get selected book data
        int bookId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);
        String author = (String) tableModel.getValueAt(selectedRow, 2);
        String isbn = (String) tableModel.getValueAt(selectedRow, 3);
        String genre = (String) tableModel.getValueAt(selectedRow, 4);
        String status = (String) tableModel.getValueAt(selectedRow, 5);
        int quantity = (Integer) tableModel.getValueAt(selectedRow, 6);

        showEditBookDialog(bookId, title, author, isbn, genre, status, quantity);
    }

    // Show dialog to edit a selected book
    private void showEditBookDialog(int bookId, String title, String author, String isbn, String genre, String status, int quantity) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Book", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create input fields with current book info
        JTextField titleField = new JTextField(title, 20);
        JTextField authorField = new JTextField(author, 20);
        JTextField isbnField = new JTextField(isbn, 20);
        JTextField genreField = new JTextField(genre, 20);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"available", "unavailable"});
        statusCombo.setSelectedItem(status);
        JTextField quantityField = new JTextField(String.valueOf(quantity), 20);

        // Add form components
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; formPanel.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1; formPanel.add(authorField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1; formPanel.add(isbnField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Genre:"), gbc);
        gbc.gridx = 1; formPanel.add(genreField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; formPanel.add(statusCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; formPanel.add(quantityField, gbc);

        // Save and cancel buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String newTitle = titleField.getText().trim();
                String newAuthor = authorField.getText().trim();
                String newIsbn = isbnField.getText().trim();
                String newGenre = genreField.getText().trim();
                String newStatus = (String) statusCombo.getSelectedItem();
                int newQuantity = Integer.parseInt(quantityField.getText().trim());

                if (newTitle.isEmpty() || newAuthor.isEmpty() || newIsbn.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all required fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Book updatedBook = new Book(bookId, newTitle, newAuthor, newIsbn, newGenre, newStatus, newQuantity);
                boolean success = bookService.updateBook(updatedBook);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Book updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadBooks();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update book", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid quantity number", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Delete the selected book
    private void deleteSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);

        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the book '" + title + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            boolean success = bookService.deleteBook(bookId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Book deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBooks();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete book", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

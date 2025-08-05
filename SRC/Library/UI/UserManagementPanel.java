package library.gui;

import library.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserManagementPanel extends JPanel {
    
    private LibraryManagementGUI mainFrame;
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JTextField searchField, idField, nameField, emailField, phoneField;
    private JComboBox<String> statusFilter;
    private JCheckBox activeCheckBox;
    
    public UserManagementPanel(LibraryManagementGUI mainFrame) {
        this.mainFrame = mainFrame;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] columnNames = {"ID", "Name", "Username", "Email", "Phone", "Status", "Books Borrowed", "Total Loans", "Registration"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        usersTable = new JTable(tableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.setRowHeight(25);
        
        searchField = new JTextField(20);
        statusFilter = new JComboBox<>(new String[]{"All", "Active", "Inactive"});
        idField = new JTextField(15);
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(15);
        activeCheckBox = new JCheckBox("Active", true);
    }
    
    private void setupLayout() {
        add(createSearchPanel(), BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        JScrollPane tableScrollPane = new JScrollPane(usersTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 300));
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);
        contentPanel.add(createFormPanel(), BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
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
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("User Details"));
        
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        addFormField(fieldsPanel, gbc, "ID:", idField, 0, 0);
        addFormField(fieldsPanel, gbc, "Name:", nameField, 2, 0);
        addFormField(fieldsPanel, gbc, "Email:", emailField, 0, 1);
        addFormField(fieldsPanel, gbc, "Phone:", phoneField, 2, 1);
        addFormField(fieldsPanel, gbc, "Status:", activeCheckBox, 0, 2);
        
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        return formPanel;
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int x, int y) {
        gbc.gridx = x; gbc.gridy = y;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = x + 1;
        panel.add(field, gbc);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton[] buttons = {
            new JButton("Add User"),
            new JButton("Update User"),
            new JButton("Delete User"),
            new JButton("Clear Form"),
            new JButton("View Loans")
        };
        
        Runnable[] actions = {
            this::addUser,
            this::updateUser,
            this::deleteUser,
            this::clearForm,
            this::viewUserLoans
        };
        
        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            buttons[i].addActionListener(e -> actions[index].run());
            panel.add(buttons[i]);
        }
        
        return panel;
    }
    
    private void setupEventHandlers() {
        usersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && usersTable.getSelectedRow() >= 0) {
                loadUserToForm(usersTable.getSelectedRow());
            }
        });
        
        statusFilter.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());
    }
    
    public void refreshData() {
        refreshTable();
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        
        for (User user : mainFrame.getUsers().values()) {
            LoginDialog.UserCredential userCred = LoginDialog.getRegisteredUsers().values().stream()
                .filter(cred -> cred.email.equals(user.getEmail()))
                .findFirst().orElse(null);
            
            String username = userCred != null ? userCred.username : "N/A";
            boolean isRegisteredUser = userCred != null;
            
            tableModel.addRow(new Object[]{
                user.getId(),
                user.getName(),
                username,
                user.getEmail(),
                user.getPhone() != null ? user.getPhone() : "",
                user.isActive() ? "Active" : "Inactive",
                user.getBorrowedBooksCount(),
                user.getTotalLoansCount(),
                isRegisteredUser ? "Self-Registered" : "Admin Added"
            });
        }
    }
    
    private void performSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        String status = (String) statusFilter.getSelectedItem();
        
        tableModel.setRowCount(0);
        
        for (User user : mainFrame.getUsers().values()) {
            if (matchesSearch(user, searchText) && matchesStatus(user, status)) {
                LoginDialog.UserCredential userCred = LoginDialog.getRegisteredUsers().values().stream()
                    .filter(cred -> cred.email.equals(user.getEmail()))
                    .findFirst().orElse(null);
                
                String username = userCred != null ? userCred.username : "N/A";
                boolean isRegisteredUser = userCred != null;
                
                tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getName(),
                    username,
                    user.getEmail(),
                    user.getPhone() != null ? user.getPhone() : "",
                    user.isActive() ? "Active" : "Inactive",
                    user.getBorrowedBooksCount(),
                    user.getTotalLoansCount(),
                    isRegisteredUser ? "Self-Registered" : "Admin Added"
                });
            }
        }
        
        mainFrame.updateStatus("Search completed - " + tableModel.getRowCount() + " users found");
    }
    
    private boolean matchesSearch(User user, String searchText) {
        return searchText.isEmpty() || 
               user.getName().toLowerCase().contains(searchText) ||
               user.getEmail().toLowerCase().contains(searchText) ||
               user.getId().toLowerCase().contains(searchText);
    }
    
    private boolean matchesStatus(User user, String status) {
        return "All".equals(status) ||
               ("Active".equals(status) && user.isActive()) ||
               ("Inactive".equals(status) && !user.isActive());
    }
    
    private void clearSearch() {
        searchField.setText("");
        statusFilter.setSelectedItem("All");
        refreshTable();
        mainFrame.updateStatus("Search cleared - showing all users");
    }
    
    private void loadUserToForm(int selectedRow) {
        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        User user = mainFrame.getUsers().get(userId);
        
        if (user != null) {
            idField.setText(user.getId());
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
            activeCheckBox.setSelected(user.isActive());
        }
    }
    
    private void addUser() {
        try {
            if (!validateForm()) return;
            
            String id = idField.getText().trim();
            if (mainFrame.getUsers().containsKey(id)) {
                showError("User ID already exists!");
                return;
            }
            
            User user = createUserFromForm();
            mainFrame.getUsers().put(user.getId(), user);
            
            refreshTable();
            clearForm();
            mainFrame.updateStatus("User added successfully: " + user.getName());
            mainFrame.getLogger().logInfo("User added: " + user.getId());
            
        } catch (Exception e) {
            showError("Error adding user: " + e.getMessage());
            mainFrame.getLogger().logError("Error adding user", e);
        }
    }
    
    private void updateUser() {
        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                showError("Please select a user to update!");
                return;
            }
            
            if (!validateForm()) return;
            
            User user = mainFrame.getUsers().get(id);
            if (user == null) {
                showError("User not found!");
                return;
            }
            
            user.setName(nameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setPhone(phoneField.getText().trim());
            user.setActive(activeCheckBox.isSelected());
            
            refreshTable();
            mainFrame.updateStatus("User updated successfully: " + user.getName());
            mainFrame.getLogger().logInfo("User updated: " + user.getId());
            
        } catch (Exception e) {
            showError("Error updating user: " + e.getMessage());
            mainFrame.getLogger().logError("Error updating user", e);
        }
    }
    
    private void deleteUser() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            showError("Please select a user to delete!");
            return;
        }
        
        User user = mainFrame.getUsers().get(id);
        if (user == null) {
            showError("User not found!");
            return;
        }
        
        boolean hasActiveLoans = mainFrame.getLoans().values().stream()
                .anyMatch(loan -> loan.getUserId().equals(id) && loan.getStatus().isActive());
        
        if (hasActiveLoans) {
            showError("Cannot delete user with active loans!");
            return;
        }
        
        if (JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete the user '" + user.getName() + "'?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            
            mainFrame.getUsers().remove(id);
            refreshTable();
            clearForm();
            mainFrame.updateStatus("User deleted successfully: " + user.getName());
            mainFrame.getLogger().logInfo("User deleted: " + id);
        }
    }
    
    private void viewUserLoans() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            showError("Please select a user to view loans!");
            return;
        }
        
        User user = mainFrame.getUsers().get(id);
        if (user == null) {
            showError("User not found!");
            return;
        }
        
        showUserLoansDialog(user);
    }
    
    private void showUserLoansDialog(User user) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "Loans for " + user.getName(), true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        
        String[] columnNames = {"Loan ID", "Book ID", "Loan Date", "Due Date", "Status", "Fine"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        mainFrame.getLoans().values().stream()
            .filter(loan -> loan.getUserId().equals(user.getId()))
            .forEach(loan -> model.addRow(new Object[]{
                loan.getId(),
                loan.getBookId(),
                loan.getLoanDate().toLocalDate(),
                loan.getDueDate().toLocalDate(),
                loan.getStatus().getDisplayName(),
                String.format("$%.2f", loan.getFineAmount())
            }));
        
        dialog.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private User createUserFromForm() {
        String phone = phoneField.getText().trim();
        User user = new User(idField.getText().trim(), nameField.getText().trim(), 
                           emailField.getText().trim(), phone.isEmpty() ? null : phone);
        user.setActive(activeCheckBox.isSelected());
        return user;
    }
    
    private boolean validateForm() {
        if (idField.getText().trim().isEmpty()) {
            showError("User ID is required!");
            return false;
        }
        
        if (nameField.getText().trim().isEmpty()) {
            showError("Name is required!");
            return false;
        }
        
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showError("Email is required!");
            return false;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address!");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        activeCheckBox.setSelected(true);
    }
}
package library.gui;

import library.util.Logger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class LoginDialog extends JDialog {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton, cancelButton;
    private JRadioButton adminRadio, userRadio;
    private JLabel statusLabel;
    
    private boolean loginSuccessful = false;
    private String authenticatedUser, userRole;
    private Logger logger;
    private static Map<String, UserCredential> registeredUsers = new HashMap<>();
    
    public static class UserCredential {
        String username, password, role, name, email, phone;
        
        UserCredential(String username, String password, String role, String name, String email, String phone) {
            this.username = username;
            this.password = password;
            this.role = role;
            this.name = name;
            this.email = email;
            this.phone = phone;
        }
    }
    
    public LoginDialog(Frame parent) {
        super(parent, "Library Management System - Login", true);
        this.logger = Logger.getInstance();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register as User");
        cancelButton = new JButton("Cancel");
        
        adminRadio = new JRadioButton("Admin", true);
        userRadio = new JRadioButton("User");
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(adminRadio);
        roleGroup.add(userRadio);
        
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
    }
    

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Please enter your credentials");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel titleContainer = new JPanel(new BorderLayout());
        titleContainer.add(titleLabel, BorderLayout.CENTER);
        titleContainer.add(subtitleLabel, BorderLayout.SOUTH);
        titleContainer.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Role selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JPanel rolePanel = new JPanel(new FlowLayout());
        rolePanel.add(new JLabel("Login as:"));
        rolePanel.add(adminRadio);
        rolePanel.add(userRadio);
        formPanel.add(rolePanel, gbc);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(passwordField, gbc);
        
        // Status label
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(statusLabel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to main panel
        mainPanel.add(titleContainer, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(e -> performLogin());
        registerButton.addActionListener(e -> showRegistrationDialog());
        cancelButton.addActionListener(e -> dispose());
        
        // Enter key triggers login
        KeyAdapter enterListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performLogin();
            }
        };
        usernameField.addKeyListener(enterListener);
        passwordField.addKeyListener(enterListener);
        
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                usernameField.requestFocus();
            }
        });
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please enter both username and password.");
            return;
        }
        
        if (authenticateUser(username, password)) {
            loginSuccessful = true;
            authenticatedUser = username;
            logger.logUserActivity(authenticatedUser, "Logged in successfully");
            dispose();
        } else {
            showStatus("Invalid username or password.");
            passwordField.setText("");
            passwordField.requestFocus();
            logger.logUserActivity(username, "Failed login attempt");
        }
    }
    
    private boolean authenticateUser(String username, String password) {
        if (adminRadio.isSelected()) {
            if ("admin".equals(username) && "admin".equals(password)) {
                userRole = "ADMIN";
                return true;
            }
            showStatus("Invalid admin credentials!");
            return false;
        }
        
        UserCredential user = registeredUsers.get(username);
        if (user != null && user.password.equals(password)) {
            userRole = "USER";
            return true;
        }
        showStatus("Invalid user credentials or user not registered!");
        return false;
    }
    
    private void showRegistrationDialog() {
        JDialog dialog = new JDialog(this, "User Registration", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        
        JTextField nameField = new JTextField(25);
        JTextField emailField = new JTextField(25);
        JTextField phoneField = new JTextField(25);
        JTextField usernameField = new JTextField(25);
        JPasswordField passwordField = new JPasswordField(25);
        JPasswordField confirmField = new JPasswordField(25);
        
        String[] labels = {"Full Name:", "Email:", "Phone:", "Username:", "Password:", "Confirm Password:"};
        JComponent[] fields = {nameField, emailField, phoneField, usernameField, passwordField, confirmField};
        
        for (int i = 0; i < labels.length; i++) {
            c.gridx = 0; c.gridy = i; c.anchor = GridBagConstraints.EAST;
            panel.add(new JLabel(labels[i]), c);
            c.gridx = 1; c.anchor = GridBagConstraints.WEST;
            panel.add(fields[i], c);
        }
        
        JButton submitBtn = new JButton("Register");
        JButton cancelBtn = new JButton("Cancel");
        
        submitBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());
            
            String error = validateRegistration(name, email, username, password, confirm);
            if (error != null) {
                JOptionPane.showMessageDialog(dialog, error, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            UserCredential user = new UserCredential(username, password, "USER", name, email, phone);
            registeredUsers.put(username, user);
            addUserToMainSystem(user);
            logger.logInfo("New user registered: " + username);
            
            JOptionPane.showMessageDialog(dialog, "Registration successful! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);
        
        c.gridx = 0; c.gridy = 6; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, c);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private String validateRegistration(String name, String email, String username, String password, String confirm) {
        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            return "All fields are required!";
        }
        if (!password.equals(confirm)) {
            return "Passwords do not match!";
        }
        if (registeredUsers.containsKey(username)) {
            return "Username already exists!";
        }
        if (!isValidEmail(email)) {
            return "Invalid email! Email must contain @ symbol and be properly formatted.";
        }
        if (!isValidPassword(password)) {
            return "Invalid password! Password must be more than 6 characters long.";
        }
        return null;
    }
    
    private void addUserToMainSystem(UserCredential user) {
        if (userRegistrationCallback != null) {
            userRegistrationCallback.onUserRegistered(user);
        }
    }
    
    public interface UserRegistrationCallback {
        void onUserRegistered(UserCredential user);
    }
    
    private UserRegistrationCallback userRegistrationCallback;
    
    public void setUserRegistrationCallback(UserRegistrationCallback callback) {
        this.userRegistrationCallback = callback;
    }
    
    private void showStatus(String message) {
        statusLabel.setText(message);
        Timer timer = new Timer(3000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
    
    public boolean isLoginSuccessful() { return loginSuccessful; }
    public String getAuthenticatedUser() { return authenticatedUser; }
    public String getUserRole() { return userRole; }
    public boolean isAdmin() { return "ADMIN".equals(userRole); }
    public boolean isUser() { return "USER".equals(userRole); }
    
    public static Map<String, UserCredential> getRegisteredUsers() { return registeredUsers; }
    public static UserCredential getUserCredential(String username) { return registeredUsers.get(username); }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return email.contains("@");
    }
    
    private boolean isValidPassword(String password) {
        return password != null && password.length() > 6;
    }
}
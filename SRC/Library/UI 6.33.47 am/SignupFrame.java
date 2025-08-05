package UI;

import java.awt.*;
import javax.swing.*;
import service.AuthService;
import model.User;
import util.Validator;

public class SignupFrame extends JFrame {
    
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JButton signupButton;
    private JButton backButton;
    private JLabel statusLabel;
    private AuthService authService;
    
    public SignupFrame() {
        this.authService = new AuthService();
        setupUI();
    }
    
    private void setupUI() {
        setTitle("Library Management System - Sign Up");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Create components
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        roleComboBox = new JComboBox<>(new String[]{"student", "admin"});
        signupButton = new JButton("Sign Up");
        backButton = new JButton("Back to Login");
        statusLabel = new JLabel("Create your account");
        statusLabel.setForeground(Color.BLUE);
        
        // Layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Full Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);
        
        // Role
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        panel.add(roleComboBox, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panel.add(statusLabel, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        // Add action listeners
        signupButton.addActionListener(e -> handleSignup());
        backButton.addActionListener(e -> goBackToLogin());
        
        // Enter key support
        getRootPane().setDefaultButton(signupButton);
        
        setContentPane(panel);
    }
    
    private void handleSignup() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();
        
        // Validate input
        if (!validateSignupInput(name, email, password, confirmPassword)) {
            return;
        }
        
        try {
            // Create new user
            User newUser = new User(0, name, email, password, role);
            boolean success = authService.registerUser(newUser);
            
            if (success) {
                statusLabel.setText("Account created successfully!");
                statusLabel.setForeground(new Color(0, 128, 0));
                
                // Show success dialog
                JOptionPane.showMessageDialog(this, 
                    "Account created successfully!\nWelcome, " + name + "\nYou can now login with your credentials.",
                    "Sign Up Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Go back to login
                goBackToLogin();
                
            } else {
                statusLabel.setText("Failed to create account. Email might already exist.");
                statusLabel.setForeground(Color.RED);
            }
            
        } catch (Exception e) {
            statusLabel.setText("Sign up failed: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            System.err.println("Sign up error: " + e.getMessage());
        }
    }
    
    private boolean validateSignupInput(String name, String email, String password, String confirmPassword) {
        // Check for empty fields
        if (name.isEmpty()) {
            showError("Please enter your full name");
            nameField.requestFocus();
            return false;
        }
        
        if (email.isEmpty()) {
            showError("Please enter your email address");
            emailField.requestFocus();
            return false;
        }
        
        if (password.isEmpty()) {
            showError("Please enter a password");
            passwordField.requestFocus();
            return false;
        }
        
        if (confirmPassword.isEmpty()) {
            showError("Please confirm your password");
            confirmPasswordField.requestFocus();
            return false;
        }
        
        // Validate email format
        if (!Validator.isValidEmail(email)) {
            showError("Please enter a valid email address");
            emailField.requestFocus();
            return false;
        }
        
        // Validate password length
        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            passwordField.requestFocus();
            return false;
        }
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(Color.RED);
    }
    
    private void goBackToLogin() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            SimpleLoginFrame loginFrame = new SimpleLoginFrame();
            loginFrame.setVisible(true);
        });
    }
} 
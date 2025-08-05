package UI;

import java.awt.*;
import javax.swing.*;
import model.User;
import service.AuthService;

public class SimpleLoginFrame extends JFrame {
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JLabel statusLabel;
    private AuthService authService;
    
    public SimpleLoginFrame() {
        this.authService = new AuthService();
        setupUI();
    }
    
    private void setupUI() {
        setTitle("Library Management System - Login");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Create components
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");
        statusLabel = new JLabel("Enter your credentials");
        statusLabel.setForeground(Color.BLUE);
        
        // Layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(statusLabel, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        // Add action listeners
        loginButton.addActionListener(e -> handleLogin());
        signupButton.addActionListener(e -> goToSignup());
        
        // Enter key support
        getRootPane().setDefaultButton(loginButton);
        
        setContentPane(panel);
    }
    
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both email and password");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        try {
            User user = authService.authenticateUser(email, password);
            if (user != null) {
                statusLabel.setText("Login successful! Welcome, " + user.getName());
                statusLabel.setForeground(new Color(0, 128, 0));
                
                // Show success dialog
                JOptionPane.showMessageDialog(this, 
                    "Login successful!\nWelcome, " + user.getName() + "\nRole: " + user.getRole(),
                    "Login Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Close login frame and open dashboard
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    MainDashboard dashboard = new MainDashboard(user);
                    dashboard.setVisible(true);
                });
                
            } else {
                statusLabel.setText("Invalid email or password");
                statusLabel.setForeground(Color.RED);
                passwordField.setText("");
            }
        } catch (Exception e) {
            statusLabel.setText("Login failed: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            System.err.println("Login error: " + e.getMessage());
        }
    }
    
    private void goToSignup() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            SignupFrame signupFrame = new SignupFrame();
            signupFrame.setVisible(true);
        });
    }
} 
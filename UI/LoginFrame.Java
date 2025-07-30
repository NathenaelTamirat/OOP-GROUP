// File: ui/LoginFrame.java
// Login screen for both admin and student, opens MainDashboard or RegisterFrame.

package ui;

import javax.swing.*;
import service.AuthService;
import model.User;

public class LoginFrame extends JFrame {
    private AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("Library Management Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginBtn);
        panel.add(registerBtn);

        add(panel);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            if (authService.login(email, password)) {
                User user = authService.getUserByEmail(email);
                JOptionPane.showMessageDialog(this, "Login successful!");
                this.dispose();
                new MainDashboard(user).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        });

        registerBtn.addActionListener(e -> {
            this.dispose();
            new RegisterFrame().setVisible(true);
        });
    }
}
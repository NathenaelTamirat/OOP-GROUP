
// Registration window to sign up new admin or student users.

package ui;

import javax.swing.*;
import service.AuthService;
import model.User;

public class RegisterFrame extends JFrame {
    private AuthService authService = new AuthService();

    public RegisterFrame() {
        setTitle("Library Registration");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        String[] roles = {"admin", "student"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleBox);
        panel.add(registerBtn);
        panel.add(backBtn);

        add(panel);

        registerBtn.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String)roleBox.getSelectedItem();
            User user = new User(0, name, email, password, role);
            if (authService.register(user)) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
                this.dispose();
                new LoginFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed.");
            }
        });

        backBtn.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });
    }
}
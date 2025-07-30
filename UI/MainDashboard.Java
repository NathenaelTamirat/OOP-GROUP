
// Main window with tabs for Books, Issues, Users (admin only).

package ui;

import javax.swing.*;
import model.User;

public class MainDashboard extends JFrame {
    public MainDashboard(User user) {
        setTitle("Library Dashboard - " + user.getRole());
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Books", new BookPanel(user.getRole().equals("admin")));
        tabs.addTab("Issues", new IssuePanel(user));
        if (user.getRole().equals("admin")) {
            tabs.addTab("Users", new UserPanel());
        }

        add(tabs);
    }
}
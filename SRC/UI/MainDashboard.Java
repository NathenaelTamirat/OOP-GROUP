package UI;

import java.awt.*;
import javax.swing.*;
import model.User;
import service.AuthService;
import service.BookService;
import service.IssueService;
import service.UserService;

public class MainDashboard extends JFrame {
    
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Services
    private AuthService authService;
    private BookService bookService;
    private IssueService issueService;
    private UserService userService;
    
    // UI Components
    private JLabel welcomeLabel;
    private JButton booksButton;
    private JButton issuesButton;
    private JButton usersButton;
    private JButton reportsButton;
    private JButton logoutButton;
    
    public MainDashboard(User user) {
        this.currentUser = user;
        this.authService = new AuthService();
        this.bookService = new BookService();
        this.issueService = new IssueService();
        this.userService = new UserService();
        
        setupUI();
        loadDashboardData();
    }
    
    private void setupUI() {
        setTitle("Library Management System - Dashboard");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Main layout
        setLayout(new BorderLayout());
        
        // Header
        createHeader();
        
        // Navigation panel
        createNavigationPanel();
        
        // Content area
        createContentArea();
        
        // Status bar
        createStatusBar();
    }
    
    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setPreferredSize(new Dimension(1000, 80));
        
        // Welcome message
        welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Logout button
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logoutButton.addActionListener(e -> handleLogout());
        
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(248, 249, 250));
        navPanel.setPreferredSize(new Dimension(250, 700));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Navigation buttons
        booksButton = createNavButton("Books Management", "Manage books, add new books, search and update");
        issuesButton = createNavButton("Issue/Return", "Issue books to users, return books, track fines");
        
        // Add buttons to navigation panel
        navPanel.add(booksButton);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(issuesButton);
        
        // Admin-only features
        if ("admin".equals(currentUser.getRole())) {
            navPanel.add(Box.createVerticalStrut(15));
            usersButton = createNavButton("User Management", "Manage users, view profiles, update information");
            navPanel.add(usersButton);
            
            navPanel.add(Box.createVerticalStrut(15));
            reportsButton = createNavButton("Reports & Analytics", "View reports, analytics, and statistics");
            navPanel.add(reportsButton);
        }
        
        // Add some spacing at the bottom
        navPanel.add(Box.createVerticalGlue());
        
        add(navPanel, BorderLayout.WEST);
    }
    
    private JButton createNavButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(255, 255, 255));
        button.setForeground(new Color(0, 102, 204));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        button.setToolTipText(tooltip);
        button.setMaximumSize(new Dimension(200, 60));
        button.setPreferredSize(new Dimension(200, 60));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 248, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 255, 255));
            }
        });
        
        return button;
    }
    
    private void createContentArea() {
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        
        // Add action listeners
        booksButton.addActionListener(e -> showBooksPanel());
        issuesButton.addActionListener(e -> showIssuesPanel());
        
        // Admin-only action listeners
        if ("admin".equals(currentUser.getRole())) {
            usersButton.addActionListener(e -> showUsersPanel());
            reportsButton.addActionListener(e -> showReportsPanel());
        }
        
        // Show default dashboard
        showDashboardPanel();
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(248, 249, 250));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel timeLabel = new JLabel("System Online");
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(0, 128, 0));
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(timeLabel, BorderLayout.EAST);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void showDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(Color.WHITE);
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Dashboard title
        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        
        // Statistics panel
        JPanel statsPanel = createStatisticsPanel();
        
        // Quick actions panel - different for admin vs student
        JPanel quickActionsPanel = createQuickActionsPanel();
        
        dashboardPanel.add(titleLabel, BorderLayout.NORTH);
        dashboardPanel.add(statsPanel, BorderLayout.CENTER);
        dashboardPanel.add(quickActionsPanel, BorderLayout.SOUTH);
        
        contentPanel.add(dashboardPanel, "dashboard");
        cardLayout.show(contentPanel, "dashboard");
    }
    
    private JPanel createStatisticsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        
        // Create stat cards
        statsPanel.add(createStatCard("Total Books", "0", new Color(0, 102, 204)));
        statsPanel.add(createStatCard("Issued Books", "0", new Color(255, 193, 7)));
        statsPanel.add(createStatCard("Total Users", "0", new Color(40, 167, 69)));
        statsPanel.add(createStatCard("Total Fines", "$0", new Color(220, 53, 69)));
        
        return statsPanel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        
        return card;
    }
    
    private JPanel createQuickActionsPanel() {
        JPanel quickPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        quickPanel.setBackground(Color.WHITE);
        quickPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        if ("admin".equals(currentUser.getRole())) {
            // Admin quick actions
            JButton addBookBtn = new JButton("Add New Book");
            JButton issueBookBtn = new JButton("Issue Book");
            JButton addUserBtn = new JButton("Add New User");
            
            // Style buttons
            addBookBtn.setFont(new Font("Arial", Font.BOLD, 12));
            issueBookBtn.setFont(new Font("Arial", Font.BOLD, 12));
            addUserBtn.setFont(new Font("Arial", Font.BOLD, 12));
            
            addBookBtn.setBackground(new Color(0, 102, 204));
            issueBookBtn.setBackground(new Color(40, 167, 69));
            addUserBtn.setBackground(new Color(255, 193, 7));
            
            addBookBtn.setForeground(Color.WHITE);
            issueBookBtn.setForeground(Color.WHITE);
            addUserBtn.setForeground(Color.WHITE);
            
            // Add action listeners for admin
            addBookBtn.addActionListener(e -> showBooksPanel());
            issueBookBtn.addActionListener(e -> showIssuesPanel());
            addUserBtn.addActionListener(e -> showUsersPanel());
            
            quickPanel.add(addBookBtn);
            quickPanel.add(issueBookBtn);
            quickPanel.add(addUserBtn);
        } else {
            // Student quick actions
            JButton viewBooksBtn = new JButton("View Books");
            JButton myIssuesBtn = new JButton("My Issues");
            JButton searchBooksBtn = new JButton("Search Books");
            
            // Style buttons
            viewBooksBtn.setFont(new Font("Arial", Font.BOLD, 12));
            myIssuesBtn.setFont(new Font("Arial", Font.BOLD, 12));
            searchBooksBtn.setFont(new Font("Arial", Font.BOLD, 12));
            
            viewBooksBtn.setBackground(new Color(0, 102, 204));
            myIssuesBtn.setBackground(new Color(40, 167, 69));
            searchBooksBtn.setBackground(new Color(255, 193, 7));
            
            viewBooksBtn.setForeground(Color.WHITE);
            myIssuesBtn.setForeground(Color.WHITE);
            searchBooksBtn.setForeground(Color.WHITE);
            
            // Add action listeners for student
            viewBooksBtn.addActionListener(e -> showBooksPanel());
            myIssuesBtn.addActionListener(e -> showIssuesPanel());
            searchBooksBtn.addActionListener(e -> showBooksPanel());
            
            quickPanel.add(viewBooksBtn);
            quickPanel.add(myIssuesBtn);
            quickPanel.add(searchBooksBtn);
        }
        
        return quickPanel;
    }
    
    private void showBooksPanel() {
        System.out.println("Showing Books Panel for user role: " + currentUser.getRole());
        
        // Check if panel already exists
        Component existingPanel = null;
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof BooksPanel) {
                existingPanel = comp;
                break;
            }
        }
        
        if (existingPanel == null) {
            // Create new panel if it doesn't exist
            BooksPanel booksPanel = new BooksPanel(currentUser.getRole());
            // Add back button to books panel
            addBackButtonToPanel(booksPanel, "Books Management");
            contentPanel.add(booksPanel, "books");
            System.out.println("Created new Books Panel");
        } else {
            System.out.println("Reusing existing Books Panel");
        }
        
        cardLayout.show(contentPanel, "books");
        contentPanel.revalidate();
        contentPanel.repaint();
        System.out.println("Books Panel should now be visible");
    }
    
    private void showIssuesPanel() {
        System.out.println("Showing Issues Panel for user role: " + currentUser.getRole());
        
        // Check if panel already exists
        Component existingPanel = null;
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof IssuesPanel) {
                existingPanel = comp;
                break;
            }
        }
        
        if (existingPanel == null) {
            // Create new panel if it doesn't exist
            IssuesPanel issuesPanel = new IssuesPanel(currentUser.getRole());
            // Add back button to issues panel
            addBackButtonToPanel(issuesPanel, "Issue/Return Management");
            contentPanel.add(issuesPanel, "issues");
            System.out.println("Created new Issues Panel");
        } else {
            System.out.println("Reusing existing Issues Panel");
        }
        
        cardLayout.show(contentPanel, "issues");
        contentPanel.revalidate();
        contentPanel.repaint();
        System.out.println("Issues Panel should now be visible");
    }
    
    private void showUsersPanel() {
        System.out.println("Showing Users Panel for user role: " + currentUser.getRole());
        
        // Check if panel already exists
        Component existingPanel = null;
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof UsersPanel) {
                existingPanel = comp;
                break;
            }
        }
        
        if (existingPanel == null) {
            // Create new panel if it doesn't exist
            UsersPanel usersPanel = new UsersPanel();
            // Add back button to users panel
            addBackButtonToPanel(usersPanel, "User Management");
            contentPanel.add(usersPanel, "users");
            System.out.println("Created new Users Panel");
        } else {
            System.out.println("Reusing existing Users Panel");
        }
        
        cardLayout.show(contentPanel, "users");
        contentPanel.revalidate();
        contentPanel.repaint();
        System.out.println("Users Panel should now be visible");
    }
    
    private void showReportsPanel() {
        System.out.println("Showing Reports Panel for user role: " + currentUser.getRole());
        
        // Check if panel already exists
        Component existingPanel = null;
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof ReportsPanel) {
                existingPanel = comp;
                break;
            }
        }
        
        if (existingPanel == null) {
            // Create new panel if it doesn't exist
            ReportsPanel reportsPanel = new ReportsPanel();
            // Add back button to reports panel
            addBackButtonToPanel(reportsPanel, "Reports & Analytics");
            contentPanel.add(reportsPanel, "reports");
            System.out.println("Created new Reports Panel");
        } else {
            System.out.println("Reusing existing Reports Panel");
        }
        
        cardLayout.show(contentPanel, "reports");
        contentPanel.revalidate();
        contentPanel.repaint();
        System.out.println("Reports Panel should now be visible");
    }
    
    private void loadDashboardData() {
        // TODO: Load actual statistics from services
        // For now, we'll just show placeholder data
        System.out.println("Loading dashboard data for user: " + currentUser.getName());
    }
    
    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                SimpleLoginFrame loginFrame = new SimpleLoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }

    private void addBackButtonToPanel(JPanel panel, String panelTitle) {
        // Create a back button panel
        JPanel backButtonPanel = new JPanel(new BorderLayout());
        backButtonPanel.setBackground(Color.WHITE);
        backButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Create back button with arrow icon
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        backButton.addActionListener(e -> showDashboardPanel());
        
        // Add panel title
        JLabel titleLabel = new JLabel(panelTitle);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(0, 102, 204));
        
        backButtonPanel.add(backButton, BorderLayout.WEST);
        backButtonPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Insert the back button panel at the top of the main panel
        if (panel.getLayout() instanceof BorderLayout) {
            // If panel already has BorderLayout, add back button to NORTH
            panel.add(backButtonPanel, BorderLayout.NORTH);
        } else {
            // If panel has different layout, create a new container
            JPanel container = new JPanel(new BorderLayout());
            container.add(backButtonPanel, BorderLayout.NORTH);
            container.add(panel, BorderLayout.CENTER);
            
            // Replace the panel in the content panel
            for (int i = 0; i < contentPanel.getComponentCount(); i++) {
                Component comp = contentPanel.getComponent(i);
                if (comp == panel) {
                    contentPanel.remove(i);
                    contentPanel.add(container, i);
                    break;
                }
            }
        }
    }
}

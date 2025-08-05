package library.gui;

import library.util.*;
import library.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class LibraryManagementGUI extends JFrame {
    
    private Logger logger;
    private ConfigManager configManager;
    private UserSession userSession;
    
    private Map<String, Book> books;
    private Map<String, User> users;
    private Map<String, Loan> loans;
    private Map<Integer, Category> categories;
    private Map<String, BorrowRequest> borrowRequests;
    private static Map<String, BorrowRequest> globalBorrowRequests = new HashMap<>();
    
    private JPanel contentPanel;
    private JLabel statusLabel;
    private BookManagementPanel bookPanel;
    private UserManagementPanel userPanel;
    private LoanManagementPanel loanPanel;
    private RequestManagementPanel requestPanel;
    private UserDashboardPanel userDashboardPanel;
    
    private JLabel booksStatsLabel, usersStatsLabel, loansStatsLabel, categoriesStatsLabel;
    
    public LibraryManagementGUI() {
        try {
            initializeSystem();
            initializeData();
            
            // Show login dialog first
            if (!showLoginDialog()) {
                System.exit(0); // Exit if login is cancelled
                return;
            }
            
            initializeGUI();
            setupEventHandlers();
            loadSampleData();
            setVisible(true); // Make the window visible after successful login
        } catch (Exception e) {
            if (logger != null) {
                logger.logError("Failed to initialize GUI", e);
            }
            System.err.println("Failed to initialize GUI: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("GUI initialization failed", e);
        }
    }
    
    private void initializeSystem() {
        logger = Logger.getInstance();
        configManager = ConfigManager.getInstance();
        logger.setLogLevel(configManager.getLogLevel());
        logger.logSystemStart();
    }
    
    private void initializeData() {
        books = new HashMap<>();
        users = new HashMap<>();
        loans = new HashMap<>();
        categories = new HashMap<>();
        borrowRequests = globalBorrowRequests; // Use the global persistent map
        userSession = UserSession.getInstance();
    }
    
    private boolean showLoginDialog() {
        LoginDialog dialog = new LoginDialog(null);
        
        dialog.setUserRegistrationCallback(user -> {
            String userId = "U" + String.format("%03d", users.size() + 1);
            User newUser = new User(userId, user.name, user.email, user.phone);
            users.put(userId, newUser);
            logger.logInfo("Registered user added to system: " + user.username);
            
            updateDashboardStats();
            updateStatus("New user registered: " + user.name + " - Dashboard updated!");
            
            if (userSession.isAdmin()) {
                javax.swing.Timer timer = new javax.swing.Timer(500, e -> {
                    String msg = String.format("📢 New User Registration!\n\nName: %s\nEmail: %s\nPhone: %s\nUsername: %s\n\nUser has been added to the system and can now login.",
                        user.name, user.email, user.phone, user.username);
                    JOptionPane.showMessageDialog(this, msg, "New User Registered", JOptionPane.INFORMATION_MESSAGE);
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
        
        dialog.setVisible(true);
        
        if (dialog.isLoginSuccessful()) {
            userSession.login(dialog.getAuthenticatedUser(), dialog.getUserRole());
            logger.logUserActivity(userSession.getCurrentUser(), "Logged in successfully");
            return true;
        }
        return false;
    }
    
    private void initializeGUI() {
        String userInfo = userSession.isAdmin() ? " (Administrator)" : " (User)";
        setTitle("Library Management System - " + configManager.getSystemVersion() + " - " + userSession.getCurrentUser() + userInfo);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.logError("Failed to set look and feel", e);
        }
        
        createMenuBar();
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBorder(BorderFactory.createEtchedBorder());
        
        bookPanel = new BookManagementPanel(this);
        userPanel = new UserManagementPanel(this);
        loanPanel = new LoanManagementPanel(this);
        requestPanel = new RequestManagementPanel(this);
        userDashboardPanel = new UserDashboardPanel(this);
        
        contentPanel.add(createWelcomePanel(), "WELCOME");
        contentPanel.add(bookPanel, "BOOKS");
        contentPanel.add(userPanel, "USERS");
        contentPanel.add(loanPanel, "LOANS");
        contentPanel.add(requestPanel, "REQUESTS");
        contentPanel.add(userDashboardPanel, "DASHBOARD");
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        statusLabel = new JLabel("System initialized successfully");
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(240, 240, 240));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(mainPanel);
        showPanel("WELCOME");
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(createMenuItem("Logout", 'L', e -> logout()));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Exit", 'x', e -> exitApplication()));
        
        JMenu managementMenu = new JMenu("Management");
        managementMenu.add(createMenuItem("Books", 'B', e -> showPanel("BOOKS")));
        
        if (userSession.isAdmin()) {
            managementMenu.add(createMenuItem("Users", 'U', e -> showPanel("USERS")));
            managementMenu.add(createMenuItem("Loans", 'L', e -> showPanel("LOANS")));
            managementMenu.add(createMenuItem("Requests", 'R', e -> showPanel("REQUESTS")));
        } else {
            managementMenu.add(createMenuItem("My Dashboard", 'D', e -> showPanel("DASHBOARD")));
        }
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(createMenuItem("About", 'A', e -> showAboutDialog()));
        
        menuBar.add(fileMenu);
        menuBar.add(managementMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private JMenuItem createMenuItem(String text, char mnemonic, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.setMnemonic(mnemonic);
        item.addActionListener(action);
        return item;
    }
    
    private void addHoverEffect(JButton button) {
        Color originalColor = button.getBackground();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 40, 30));
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
    }
    

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Title label
        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Navigation buttons - different based on user role
        JPanel navPanel = new JPanel(new FlowLayout());
        
        JButton homeBtn = new JButton("Home");
        homeBtn.addActionListener(e -> showPanel("WELCOME"));
        addHoverEffect(homeBtn);
        navPanel.add(homeBtn);
        
        JButton booksBtn = new JButton("Books");
        booksBtn.addActionListener(e -> showPanel("BOOKS"));
        addHoverEffect(booksBtn);
        navPanel.add(booksBtn);
        
        if (!userSession.isAdmin()) {
            JButton dashboardBtn = new JButton("My Dashboard");
            dashboardBtn.addActionListener(e -> showPanel("DASHBOARD"));
            addHoverEffect(dashboardBtn);
            navPanel.add(dashboardBtn);
            
            JButton profileBtn = new JButton("My Profile");
            profileBtn.addActionListener(e -> showUserProfile());
            addHoverEffect(profileBtn);
            navPanel.add(profileBtn);
        }
        
        if (userSession.isAdmin()) {
            JButton usersBtn = new JButton("Users");
            usersBtn.addActionListener(e -> showPanel("USERS"));
            addHoverEffect(usersBtn);
            navPanel.add(usersBtn);
            
            JButton loansBtn = new JButton("Loans");
            loansBtn.addActionListener(e -> showPanel("LOANS"));
            addHoverEffect(loansBtn);
            navPanel.add(loansBtn);
            
            JButton requestsBtn = new JButton("Requests");
            requestsBtn.addActionListener(e -> showPanel("REQUESTS"));
            addHoverEffect(requestsBtn);
            navPanel.add(requestsBtn);
        }
        
        JPanel userPanel = new JPanel(new FlowLayout());
        JLabel userLabel = new JLabel("Welcome, " + userSession.getCurrentUser());
        userLabel.setFont(userLabel.getFont().deriveFont(Font.BOLD));
        userPanel.add(userLabel);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        addHoverEffect(logoutBtn);
        userPanel.add(logoutBtn);
        
        headerPanel.add(navPanel, BorderLayout.CENTER);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    

    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("<html><center><h1>Welcome to Library Management System</h1>" +
                "<p>Version: " + configManager.getSystemVersion() + "</p>" +
                "<p>Use the navigation buttons or menu to access different sections.</p></center></html>");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        
        // Statistics panel - only for admins
        if (userSession.isAdmin()) {
            JPanel statsPanel = createStatsPanel();
            welcomePanel.add(statsPanel, BorderLayout.SOUTH);
        }
        
        return welcomePanel;
    }
    

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("System Statistics"));
        
        // Books statistics
        JPanel booksStats = new JPanel(new BorderLayout());
        booksStats.setBorder(BorderFactory.createEtchedBorder());
        booksStatsLabel = new JLabel("<html><center><h2>" + books.size() + "</h2><p>Total Books</p></center></html>");
        booksStatsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        booksStats.add(booksStatsLabel);
        
        // Users statistics
        JPanel usersStats = new JPanel(new BorderLayout());
        usersStats.setBorder(BorderFactory.createEtchedBorder());
        usersStatsLabel = new JLabel("<html><center><h2>" + users.size() + "</h2><p>Total Users</p></center></html>");
        usersStatsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        usersStats.add(usersStatsLabel);
        
        // Active loans statistics - update statuses first
        loans.values().forEach(loan -> loan.updateStatus());
        long activeLoans = loans.values().stream().filter(loan -> loan.getStatus().isActive()).count();
        JPanel loansStats = new JPanel(new BorderLayout());
        loansStats.setBorder(BorderFactory.createEtchedBorder());
        loansStatsLabel = new JLabel("<html><center><h2>" + activeLoans + "</h2><p>Active Loans</p></center></html>");
        loansStatsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loansStats.add(loansStatsLabel);
        
        // Categories statistics
        JPanel categoriesStats = new JPanel(new BorderLayout());
        categoriesStats.setBorder(BorderFactory.createEtchedBorder());
        categoriesStatsLabel = new JLabel("<html><center><h2>" + categories.size() + "</h2><p>Categories</p></center></html>");
        categoriesStatsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        categoriesStats.add(categoriesStatsLabel);
        
        statsPanel.add(booksStats);
        statsPanel.add(usersStats);
        statsPanel.add(loansStats);
        statsPanel.add(categoriesStats);
        
        return statsPanel;
    }
    
    // Stats labels are already declared above
    

    public void updateDashboardStats() {
        SwingUtilities.invokeLater(() -> {
            if (usersStatsLabel != null) {
                usersStatsLabel.setText("<html><center><h2>" + users.size() + "</h2><p>Total Users</p></center></html>");
            }
            if (booksStatsLabel != null) {
                booksStatsLabel.setText("<html><center><h2>" + books.size() + "</h2><p>Total Books</p></center></html>");
            }
            if (loansStatsLabel != null) {
                // Update loan statuses first
                loans.values().forEach(loan -> loan.updateStatus());
                long activeLoans = loans.values().stream().filter(loan -> loan.getStatus().isActive()).count();
                loansStatsLabel.setText("<html><center><h2>" + activeLoans + "</h2><p>Active Loans</p></center></html>");
            }
            if (categoriesStatsLabel != null) {
                categoriesStatsLabel.setText("<html><center><h2>" + categories.size() + "</h2><p>Categories</p></center></html>");
            }
        });
    }
    

    private void setupEventHandlers() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }
    

    public void showPanel(String panelName) {
        // Check permissions
        if (!hasPermissionForPanel(panelName)) {
            updateStatus("Access denied: Insufficient permissions");
            return;
        }
        
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, panelName);
        
        // Update status
        switch (panelName) {
            case "WELCOME":
                updateStatus("Welcome to Library Management System - " + userSession.getCurrentUser());
                break;
            case "BOOKS":
                updateStatus("Book Management - " + books.size() + " books in system");
                bookPanel.refreshData(); // This will show updated book availability
                break;
            case "USERS":
                // Refresh registered users before showing
                loadRegisteredUsers();
                updateStatus("User Management - " + users.size() + " users in system");
                userPanel.refreshData();
                // Update dashboard stats when viewing users
                updateDashboardStats();
                break;
            case "LOANS":
                updateStatus("Loan Management - " + loans.size() + " loans in system");
                loanPanel.refreshData();
                break;
            case "REQUESTS":
                updateStatus("Request Management - " + borrowRequests.size() + " requests in system");
                requestPanel.refreshData();
                break;
            case "DASHBOARD":
                updateStatus("My Dashboard - " + userSession.getCurrentUser());
                userDashboardPanel.refreshData();
                break;

        }
    }
    

    private boolean hasPermissionForPanel(String panelName) {
        switch (panelName) {
            case "WELCOME":
            case "BOOKS":
                return true; // All users can access these
            case "USERS":
            case "LOANS":
            case "REQUESTS":
                return userSession.isAdmin(); // Only admins
            case "DASHBOARD":
                return !userSession.isAdmin(); // Only users
            default:
                return false;
        }
    }
    

    private void logout() {
        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            logger.logUserActivity(userSession.getCurrentUser(), "Logged out");
            userSession.logout();
            dispose();
            
            // Restart the application with login
            SwingUtilities.invokeLater(() -> {
                try {
                    new LibraryManagementGUI().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            });
        }
    }
    

    public void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
        if (logger != null) {
            logger.logInfo("Status: " + message);
        }
    }
    
    private void showAboutDialog() {
        String aboutText;
        
        if (userSession.isAdmin()) {
            aboutText = "<html><center>" +
                    "<h2>Library Management System - Admin Panel</h2>" +
                    "<p>Version: " + configManager.getSystemVersion() + "</p>" +
                    "<p>Welcome to the Administrator Dashboard with full system access. " +
                    "This comprehensive management system provides complete book management capabilities " +
                    "including adding, editing, and deleting books from the library catalog. " +
                    "Monitor user registrations and track all user activities through the user management panel. " +
                    "Access real-time dashboard statistics to view system performance and usage metrics. " +
                    "Utilize advanced search and filtering tools to efficiently manage library resources. " +
                    "Generate system analytics and reports for administrative oversight.</p>" +
                    "<p>Built with Java Swing for reliable desktop performance.</p>" +
                    "</center></html>";
        } else {
            aboutText = "<html><center>" +
                    "<h2>Library Management System - User Portal</h2>" +
                    "<p>Version: " + configManager.getSystemVersion() + "</p>" +
                    "<p>Welcome to your personal library catalog browser. " +
                    "Explore our extensive collection by browsing the complete library catalog " +
                    "or use the search functionality to find books by title or author. " +
                    "Filter books by categories to discover new titles in your areas of interest. " +
                    "View detailed book information including availability status and descriptions. " +
                    "Navigate through over 13 different book categories ranging from fiction to science.</p>" +
                    "<p>Enjoy your reading experience with our user-friendly interface.</p>" +
                    "</center></html>";
        }
        
        JOptionPane.showMessageDialog(this, aboutText, "About", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showUserProfile() {
        String currentUser = userSession.getCurrentUser();
        LoginDialog.UserCredential userCred = LoginDialog.getUserCredential(currentUser);
        
        if (userCred == null) {
            JOptionPane.showMessageDialog(this, "Profile information not available.", "Profile", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        User user = users.values().stream()
            .filter(u -> u.getEmail().equals(userCred.email))
            .findFirst().orElse(null);
        
        JDialog profileDialog = new JDialog(this, "My Profile - " + userCred.name, true);
        profileDialog.setSize(500, 400);
        profileDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        String profileInfo = "<html><h2>User Profile</h2>" +
            "<p><b>Name:</b> " + userCred.name + "</p>" +
            "<p><b>Username:</b> " + userCred.username + "</p>" +
            "<p><b>Email:</b> " + userCred.email + "</p>" +
            "<p><b>Phone:</b> " + (userCred.phone != null ? userCred.phone : "Not provided") + "</p>" +
            "<p><b>Account Type:</b> " + userCred.role + "</p>";
        
        if (user != null) {
            profileInfo += "<p><b>Books Currently Borrowed:</b> " + user.getBorrowedBooksCount() + "</p>" +
                "<p><b>Total Books Borrowed:</b> " + user.getTotalLoansCount() + "</p>" +
                "<p><b>Account Status:</b> " + (user.isActive() ? "Active" : "Inactive") + "</p>";
        }
        
        profileInfo += "</html>";
        
        JLabel profileLabel = new JLabel(profileInfo);
        panel.add(profileLabel, BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> profileDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(closeBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        profileDialog.add(panel);
        profileDialog.setVisible(true);
        
        updateStatus("Viewing user profile: " + userCred.name);
    }
    

    private void exitApplication() {
        int option = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to exit?", 
                "Exit Application", 
                JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            logger.logSystemShutdown();
            System.exit(0);
        }
    }
    

    private void loadSampleData() {
        try {
            // Create sample categories
            Category fiction = new Category(1, "Fiction", "Fictional literature including novels and short stories");
            Category science = new Category(2, "Science", "Scientific literature and research publications");
            Category history = new Category(3, "History", "Historical books and biographies");
            Category biography = new Category(4, "Biography", "Biographical and autobiographical works");
            Category mystery = new Category(5, "Mystery", "Mystery and thriller novels");
            Category romance = new Category(6, "Romance", "Romance and love stories");
            Category fantasy = new Category(7, "Fantasy", "Fantasy and magical stories");
            Category horror = new Category(8, "Horror", "Horror and suspense stories");
            Category selfHelp = new Category(9, "Self-Help", "Self-improvement and motivational books");
            Category cooking = new Category(10, "Cooking", "Cookbooks and culinary guides");
            Category travel = new Category(11, "Travel", "Travel guides and adventure stories");
            Category technology = new Category(12, "Technology", "Technology and computer science books");
            Category health = new Category(13, "Health", "Health and wellness books");
            
            categories.put(fiction.getId(), fiction);
            categories.put(science.getId(), science);
            categories.put(history.getId(), history);
            categories.put(biography.getId(), biography);
            categories.put(mystery.getId(), mystery);
            categories.put(romance.getId(), romance);
            categories.put(fantasy.getId(), fantasy);
            categories.put(horror.getId(), horror);
            categories.put(selfHelp.getId(), selfHelp);
            categories.put(cooking.getId(), cooking);
            categories.put(travel.getId(), travel);
            categories.put(technology.getId(), technology);
            categories.put(health.getId(), health);
            
            // Create sample books
            Book book1 = new Book("B001", "The Great Gatsby", "F. Scott Fitzgerald", "978-0-7432-7356-5", 1925);
            book1.setCategory(fiction);
            book1.setTotalCopies(3);
            book1.setAvailableCopies(3);
            books.put(book1.getId(), book1);
            
            Book book2 = new Book("B002", "A Brief History of Time", "Stephen Hawking", "978-0-553-38016-3", 1988);
            book2.setCategory(science);
            book2.setTotalCopies(2);
            book2.setAvailableCopies(2);
            books.put(book2.getId(), book2);
            
            Book book3 = new Book("B003", "1984", "George Orwell", "978-0-452-28423-4", 1949);
            book3.setCategory(fiction);
            book3.setTotalCopies(4);
            book3.setAvailableCopies(3);
            books.put(book3.getId(), book3);
            
            // Create sample users
            User user1 = new User("U001", "John Doe", "john.doe@email.com", "555-0123");
            users.put(user1.getId(), user1);
            
            User user2 = new User("U002", "Jane Smith", "jane.smith@email.com", "555-0456");
            users.put(user2.getId(), user2);
            
            User user3 = new User("U003", "Bob Johnson", "bob.johnson@email.com", "555-0789");
            users.put(user3.getId(), user3);
            
            // Load any previously registered users from login dialog
            loadRegisteredUsers();
            
            // Create sample loan
            Loan loan1 = new Loan("L001", user1.getId(), book3.getId());
            book3.borrowCopy(loan1);
            user1.addLoan(loan1);
            loans.put(loan1.getId(), loan1);
            
            logger.logInfo("Sample data loaded successfully");
            updateStatus("Sample data loaded - Ready to use");
            
        } catch (Exception e) {
            logger.logError("Failed to load sample data", e);
            updateStatus("Error loading sample data");
        }
    }
    

    private void loadRegisteredUsers() {
        for (LoginDialog.UserCredential userCred : LoginDialog.getRegisteredUsers().values()) {
            // Check if user already exists
            boolean userExists = users.values().stream()
                .anyMatch(user -> user.getEmail().equals(userCred.email));
            
            if (!userExists) {
                String userId = "U" + String.format("%03d", users.size() + 1);
                User newUser = new User(userId, userCred.name, userCred.email, userCred.phone);
                users.put(userId, newUser);
                logger.logInfo("Loaded registered user: " + userCred.username);
            }
        }
    }
    
    // Getter methods for data access
    public Map<String, Book> getBooks() { return books; }
    public Map<String, User> getUsers() { return users; }
    public Map<String, Loan> getLoans() { return loans; }
    public Map<Integer, Category> getCategories() { return categories; }
    public Map<String, BorrowRequest> getBorrowRequests() { return borrowRequests; }

    public Logger getLogger() { return logger; }
    public UserSession getUserSession() { return userSession; }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new LibraryManagementGUI().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                        "Failed to start application: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
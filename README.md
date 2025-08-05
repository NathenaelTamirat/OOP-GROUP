# Library Management System

A comprehensive Java Swing desktop application for managing library operations including book management, user management, issue/return system, and reporting.

## 🚀 Features

### 📚 Book Management
- Add, edit, and delete books
- Search books by title, author, or ISBN
- View book availability status
- ISBN validation

### 👥 User Management
- User registration and authentication
- Role-based access control (Admin/Student)
- User profile management
- Password change functionality

### 📖 Issue/Return System
- Issue books to users
- Return books with fine calculation
- Track issue history
- Automatic fine calculation ($0.50/day late)

### 📊 Reporting System
- Daily activity reports
- Monthly statistics
- Book inventory reports
- User activity reports
- Fine reports

### 🔐 Security
- Password validation
- Email format validation
- Role-based permissions

## 🛠️ Technology Stack

- **Frontend**: Java Swing
- **Backend**: Java (Business Logic)
- **Database**: PostgreSQL
- **Architecture**: MVC Pattern with DAO

## 📁 Project Structure

```
├── main.Java                 # Application entry point
├── config/
│   └── db_config.txt        # Database configuration
├── model/
│   ├── Person.java          # Abstract base class
│   ├── User.Java            # User entity
│   ├── Book.Java            # Book entity
│   └── Issue.java           # Issue entity
├── dao/
│   ├── UserDao.java         # User data access
│   ├── BookDao.java         # Book data access
│   └── IssueDao.java        # Issue data access
├── service/
│   ├── AuthService.java     # Authentication service
│   ├── BookService.java     # Book business logic
│   ├── IssueService.java    # Issue business logic
│   └── UserService.java     # User business logic
├── UI/
│   ├── LoginFrame.Java      # Login screen
│   ├── MainDashboard.Java   # Main application window
│   ├── BookPanel.Java       # Book management panel
│   ├── IssuePanel.java      # Issue/return panel
│   ├── UserPanel.java       # User management panel
│   ├── ReportPanel.java     # Reporting panel
│   └── registerfrane.java   # Registration screen
├── interfaces/
│   ├── Bookoperations.java  # Book operations interface
│   ├── Issueoperatins.java # Issue operations interface
├── util/
│   ├── DatabaseConnection.java # Database connection utility
│   ├── Logger.Java          # Logging utility
│   └── Validator.Java       # Input validation utility
└── database_setup.sql       # Database schema and sample data
```

## 🚀 Setup Instructions

### Prerequisites
- Java 8 or higher
- PostgreSQL 12 or higher
- PostgreSQL JDBC Driver

### Database Setup
1. Install PostgreSQL
2. Create a database named `library_management`
3. Run the database setup script:
   ```bash
   psql -U postgres -d library_management -f database_setup.sql
   ```

### Configuration
1. Update `config/db_config.txt` with your database credentials:
   ```
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=library_management
   DB_USER=your_username
   DB_PASSWORD=your_password
   DB_URL=jdbc:postgresql://localhost:5432/library_management
   ```

### Running the Application
1. Compile the Java files:
   ```bash
   javac -cp ".:postgresql-jdbc.jar" *.java */*.java */*/*.java
   ```

2. Run the application:
   ```bash
   java -cp ".:postgresql-jdbc.jar" Main
   ```

## 👤 Default Users

### Admin Account
- **Email**: admin@library.com
- **Password**: admin123
- **Role**: admin

### Student Accounts
- **Email**: john@student.com
- **Password**: student123
- **Role**: student
- **Email**: jane@student.com
- **Password**: student123
- **Role**: student

## 📋 Usage Guide

### For Administrators
1. **Login** with admin credentials
2. **Books Tab**: Manage book inventory
3. **Issues Tab**: Issue/return books, view all transactions
4. **Users Tab**: Manage user accounts
5. **Reports Tab**: Generate various reports

### For Students
1. **Login** with student credentials
2. **Books Tab**: Search and view available books
3. **Issues Tab**: View personal issue history, return books

## 🔧 Key Features Explained

### Fine Calculation
- Books are issued for 14 days
- Late returns incur $0.50 per day fine
- Fines are calculated automatically on return

### Search Functionality
- Search books by title, author, or ISBN
- Search users by name, email, or role
- Real-time filtering

### Reporting
- **Daily Report**: Today's issues and returns
- **Monthly Report**: Monthly statistics and popular books
- **Book Report**: Complete inventory status
- **User Report**: User statistics and activity
- **Fine Report**: Outstanding fines and totals

## 🛡️ Security Features

- Password strength validation
- Email format validation
- Role-based access control
- Input sanitization
- SQL injection prevention

## 🔄 Database Schema

### Users Table
- id (SERIAL PRIMARY KEY)
- name (VARCHAR)
- email (VARCHAR UNIQUE)
- password (VARCHAR)
- role (VARCHAR)

### Books Table
- id (SERIAL PRIMARY KEY)
- title (VARCHAR)
- author (VARCHAR)
- isbn (VARCHAR UNIQUE)
- issued (BOOLEAN)

### Issues Table
- id (SERIAL PRIMARY KEY)
- book_id (INTEGER FOREIGN KEY)
- user_id (INTEGER FOREIGN KEY)
- issue_date (DATE)
- return_date (DATE)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🐛 Troubleshooting

### Common Issues
1. **Database Connection Error**: Check PostgreSQL service and credentials
2. **Compilation Error**: Ensure Java 8+ and PostgreSQL JDBC driver
3. **Login Issues**: Verify user credentials in database

### Support
For issues and questions, please check the database logs and application console output.
# Library Management System - Supabase Edition

A comprehensive Java Swing desktop application for managing library operations using **Supabase** as the backend database. This version replaces the custom PostgreSQL setup with Supabase's managed database service.

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
- Supabase Row Level Security (RLS)
- **Secure API key management** (Environment variables + Properties files)

## 🛠️ Technology Stack

- **Frontend**: Java Swing
- **Backend**: Java (Business Logic)
- **Database**: Supabase (PostgreSQL with REST API)
- **Architecture**: MVC Pattern with DAO

## 📁 Project Structure

```
├── Main.java                 # Application entry point
├── util/
│   ├── SupabaseConnection.java # Supabase REST API connection
│   ├── ConfigManager.java     # Secure configuration management
│   ├── JsonUtils.java         # JSON utility functions
│   └── Logger.Java           # Logging utility
├── dao/
│   ├── SupabaseUserDao.java  # User data access (Supabase)
│   ├── SupabaseBookDao.java  # Book data access (Supabase)
│   └── SupabaseIssueDao.java # Issue data access (Supabase)
├── service/
│   ├── AuthService.java      # Authentication service
│   ├── BookService.java      # Book business logic
│   ├── IssueService.java     # Issue business logic
│   └── UserService.java      # User business logic
├── UI/
│   ├── LoginFrame.Java       # Login screen
│   ├── MainDashboard.Java    # Main application window
│   ├── BookPanel.Java        # Book management panel
│   ├── IssuePanel.java       # Issue/return panel
│   ├── UserPanel.java        # User management panel
│   ├── ReportPanel.java      # Reporting panel
│   └── registerfrane.java    # Registration screen
├── model/
│   ├── Person.java           # Abstract base class
│   ├── User.Java             # User entity
│   ├── Book.Java             # Book entity
│   └── Issue.java            # Issue entity
├── config/
│   ├── supabase_config.properties.template # Configuration template
│   └── supabase_config.properties          # Your actual config (not in git)
├── setup_config.sh           # Linux/Mac setup script
├── setup_config.bat          # Windows setup script
├── supabase_setup.sql        # Supabase database schema
└── .gitignore               # Git ignore rules
```

## 🚀 Quick Setup

### 1. **Automatic Configuration Setup**

**Linux/Mac:**
```bash
chmod +x setup_config.sh
./setup_config.sh
```

**Windows:**
```cmd
setup_config.bat
```

The setup script will prompt you for your Supabase credentials and create the configuration file automatically.

### 2. **Manual Configuration Setup**

1. **Copy the template:**
   ```bash
   cp config/supabase_config.properties.template config/supabase_config.properties
   ```

2. **Edit the configuration file:**
   ```bash
   # Edit config/supabase_config.properties with your Supabase credentials
   SUPABASE_URL=https://your-project-id.supabase.co
   SUPABASE_ANON_KEY=your-anon-key-here
   ```

### 3. **Environment Variables (Alternative)**

You can also set environment variables instead of using the properties file:

```bash
# Linux/Mac
export SUPABASE_URL=https://your-project-id.supabase.co
export SUPABASE_ANON_KEY=your-anon-key-here

# Windows
set SUPABASE_URL=https://your-project-id.supabase.co
set SUPABASE_ANON_KEY=your-anon-key-here
```

## 🚀 Supabase Setup Instructions

### Prerequisites
- Java 8 or higher
- Supabase account
- Internet connection for Supabase API access

### Supabase Project Setup

1. **Create a Supabase Project**
   - Go to [supabase.com](https://supabase.com)
   - Create a new project
   - Note your project URL and anon key

2. **Configure Database**
   - Go to your Supabase project dashboard
   - Navigate to SQL Editor
   - Run the `supabase_setup.sql` script to create tables and sample data

3. **Get Your Credentials**
   - **Project URL**: Found in your project dashboard
   - **Anon Key**: Go to Settings > API in your Supabase dashboard

### Running the Application

1. **Compile the Java files:**
   ```bash
   javac -cp ".:*" *.java */*.java */*/*.java
   ```

2. **Run the application:**
   ```bash
   java -cp ".:*" Main
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

## 🔧 Key Features Explained

### Supabase Integration
- **REST API**: All database operations use Supabase's REST API
- **Real-time**: Built-in real-time capabilities (can be extended)
- **Security**: Row Level Security (RLS) policies
- **Scalability**: Managed PostgreSQL database

### Secure Configuration Management
- **Properties File**: Configuration stored in `config/supabase_config.properties`
- **Environment Variables**: Override with environment variables
- **Template System**: Safe template file for version control
- **Validation**: Automatic configuration validation

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
- Supabase Row Level Security (RLS)
- API key authentication
- **Secure configuration management**
- **Environment variable support**

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

## 🆚 Migration from PostgreSQL

### What Changed
1. **Database Connection**: Replaced JDBC with REST API calls
2. **Data Access**: New Supabase DAO classes
3. **Configuration**: Secure configuration management system
4. **Dependencies**: No longer need PostgreSQL JDBC driver

### Benefits of Supabase
1. **Managed Service**: No database administration required
2. **Real-time**: Built-in real-time capabilities
3. **Security**: Row Level Security and API authentication
4. **Scalability**: Automatic scaling and backups
5. **API**: REST API with automatic documentation

## 🔒 Security Best Practices

### Configuration Security
- ✅ **Never commit API keys to version control**
- ✅ **Use environment variables in production**
- ✅ **Template files for safe sharing**
- ✅ **Automatic configuration validation**

### Application Security
- ✅ **Input validation and sanitization**
- ✅ **Role-based access control**
- ✅ **Secure password handling**
- ✅ **SQL injection prevention**

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
1. **Configuration Error**: Run the setup script or check your config file
2. **Supabase Connection Error**: Check internet connection and API key
3. **Compilation Error**: Ensure Java 8+ is installed
4. **Login Issues**: Verify user credentials in Supabase dashboard

### Support
For issues and questions, please check the application logs and Supabase dashboard for API errors.

## 🔗 Useful Links

- [Supabase Documentation](https://supabase.com/docs)
- [Supabase REST API](https://supabase.com/docs/reference/javascript)
- [Java HTTP Client](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpClient.html)
- [Environment Variables Guide](https://docs.oracle.com/javase/tutorial/essential/environment/env.html) 
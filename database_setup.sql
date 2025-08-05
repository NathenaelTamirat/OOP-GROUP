-- Library Management System Database Setup
-- SQLite Database Schema

-- Create Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Create Books Table
CREATE TABLE IF NOT EXISTS books (
    id VARCHAR(10) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    publication_year INTEGER,
    category_id INTEGER,
    total_copies INTEGER DEFAULT 1,
    available_copies INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Create Users Table
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Loans Table
CREATE TABLE IF NOT EXISTS loans (
    id VARCHAR(10) PRIMARY KEY,
    user_id VARCHAR(10) NOT NULL,
    book_id VARCHAR(10) NOT NULL,
    loan_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    fine_amount DECIMAL(10,2) DEFAULT 0.00,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- Create Borrow Requests Table
CREATE TABLE IF NOT EXISTS borrow_requests (
    id VARCHAR(10) PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    book_id VARCHAR(10) NOT NULL,
    request_date DATE DEFAULT CURRENT_DATE,
    return_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- Insert Default Categories
INSERT OR IGNORE INTO categories (id, name, description) VALUES
(1, 'Fiction', 'Fictional literature including novels and short stories'),
(2, 'Science', 'Scientific literature and research publications'),
(3, 'History', 'Historical books and biographies'),
(4, 'Biography', 'Biographical and autobiographical works'),
(5, 'Mystery', 'Mystery and thriller novels'),
(6, 'Romance', 'Romance and love stories'),
(7, 'Fantasy', 'Fantasy and magical stories'),
(8, 'Horror', 'Horror and suspense stories'),
(9, 'Self-Help', 'Self-improvement and motivational books'),
(10, 'Cooking', 'Cookbooks and culinary guides'),
(11, 'Travel', 'Travel guides and adventure stories'),
(12, 'Technology', 'Technology and computer science books'),
(13, 'Health', 'Health and wellness books');

-- Insert Sample Books
INSERT OR IGNORE INTO books (id, title, author, isbn, publication_year, category_id, total_copies, available_copies) VALUES
('B001', 'The Great Gatsby', 'F. Scott Fitzgerald', '978-0-7432-7356-5', 1925, 1, 3, 3),
('B002', 'A Brief History of Time', 'Stephen Hawking', '978-0-553-38016-3', 1988, 2, 2, 2),
('B003', '1984', 'George Orwell', '978-0-452-28423-4', 1949, 1, 4, 3);

-- Insert Sample Users
INSERT OR IGNORE INTO users (id, name, email, phone, role) VALUES
('U001', 'John Doe', 'john.doe@email.com', '555-0123', 'USER'),
('U002', 'Jane Smith', 'jane.smith@email.com', '555-0456', 'USER'),
('U003', 'Bob Johnson', 'bob.johnson@email.com', '555-0789', 'USER');

-- Create Indexes for Better Performance
CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_books_author ON books(author);
CREATE INDEX IF NOT EXISTS idx_books_category ON books(category_id);
CREATE INDEX IF NOT EXISTS idx_loans_user ON loans(user_id);
CREATE INDEX IF NOT EXISTS idx_loans_book ON loans(book_id);
CREATE INDEX IF NOT EXISTS idx_loans_status ON loans(status);
CREATE INDEX IF NOT EXISTS idx_requests_status ON borrow_requests(status);
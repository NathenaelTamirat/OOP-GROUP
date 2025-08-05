-- Library Management System Database Schema
-- SQLite Database Schema

-- Create Categories table
CREATE TABLE IF NOT EXISTS categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Books table
CREATE TABLE IF NOT EXISTS books (
    id VARCHAR(20) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    category_id INTEGER,
    publication_year INTEGER,
    total_copies INTEGER DEFAULT 1,
    available_copies INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    CHECK (total_copies >= 0),
    CHECK (available_copies >= 0),
    CHECK (available_copies <= total_copies)
);

-- Create Users table
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'MEMBER',
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    CHECK (role IN ('MEMBER', 'LIBRARIAN'))
);

-- Create Loans table
CREATE TABLE IF NOT EXISTS loans (
    id VARCHAR(20) PRIMARY KEY,
    user_id VARCHAR(20) NOT NULL,
    book_id VARCHAR(20) NOT NULL,
    loan_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    fine_amount DECIMAL(10,2) DEFAULT 0.00,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    CHECK (status IN ('ACTIVE', 'RETURNED', 'OVERDUE', 'LOST')),
    CHECK (fine_amount >= 0)
);

-- Create Loan History table for tracking loan activities
CREATE TABLE IF NOT EXISTS loan_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    loan_id VARCHAR(20) NOT NULL,
    action VARCHAR(50) NOT NULL,
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (loan_id) REFERENCES loans(id)
);

-- Create System Config table for dynamic configuration
CREATE TABLE IF NOT EXISTS system_config (
    config_key VARCHAR(100) PRIMARY KEY,
    config_value VARCHAR(500),
    description TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_books_author ON books(author);
CREATE INDEX IF NOT EXISTS idx_books_isbn ON books(isbn);
CREATE INDEX IF NOT EXISTS idx_books_category ON books(category_id);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_loans_user ON loans(user_id);
CREATE INDEX IF NOT EXISTS idx_loans_book ON loans(book_id);
CREATE INDEX IF NOT EXISTS idx_loans_status ON loans(status);
CREATE INDEX IF NOT EXISTS idx_loans_due_date ON loans(due_date);

-- Insert default categories
INSERT OR IGNORE INTO categories (name, description) VALUES
('Fiction', 'Fictional literature including novels and short stories'),
('Non-Fiction', 'Factual books including biographies, history, and science'),
('Science', 'Scientific literature and research publications'),
('Technology', 'Books about technology, programming, and engineering'),
('History', 'Historical books and documentaries'),
('Biography', 'Biographical and autobiographical works'),
('Reference', 'Reference materials including dictionaries and encyclopedias'),
('Children', 'Books specifically written for children'),
('Education', 'Educational textbooks and learning materials'),
('Arts', 'Books about arts, music, and creative subjects');

-- Insert default system configuration
INSERT OR IGNORE INTO system_config (config_key, config_value, description) VALUES
('max_loan_days', '14', 'Maximum number of days a book can be borrowed'),
('max_books_per_user', '5', 'Maximum number of books a user can borrow simultaneously'),
('fine_per_day', '0.50', 'Fine amount per day for overdue books'),
('system_initialized', 'true', 'Flag indicating system has been initialized'),
('last_backup', '', 'Timestamp of last system backup');

-- Create triggers for automatic timestamp updates
CREATE TRIGGER IF NOT EXISTS update_books_timestamp 
    AFTER UPDATE ON books
    BEGIN
        UPDATE books SET updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id;
    END;

CREATE TRIGGER IF NOT EXISTS update_config_timestamp 
    AFTER UPDATE ON system_config
    BEGIN
        UPDATE system_config SET updated_at = CURRENT_TIMESTAMP WHERE config_key = NEW.config_key;
    END;

-- Create trigger to automatically update book availability when loans are created/returned
CREATE TRIGGER IF NOT EXISTS update_book_availability_on_loan
    AFTER INSERT ON loans
    WHEN NEW.status = 'ACTIVE'
    BEGIN
        UPDATE books 
        SET available_copies = available_copies - 1 
        WHERE id = NEW.book_id AND available_copies > 0;
    END;

CREATE TRIGGER IF NOT EXISTS update_book_availability_on_return
    AFTER UPDATE ON loans
    WHEN OLD.status = 'ACTIVE' AND NEW.status = 'RETURNED'
    BEGIN
        UPDATE books 
        SET available_copies = available_copies + 1 
        WHERE id = NEW.book_id;
    END;
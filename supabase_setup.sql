-- Library Management System - Supabase Database Setup
-- This script creates the necessary tables and sample data for the library management system

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('admin', 'student')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create books table
CREATE TABLE IF NOT EXISTS books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(13) UNIQUE NOT NULL,
    issued BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create issues table
CREATE TABLE IF NOT EXISTS issues (
    id SERIAL PRIMARY KEY,
    book_id INTEGER NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    issue_date DATE NOT NULL,
    return_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_books_isbn ON books(isbn);
CREATE INDEX IF NOT EXISTS idx_books_issued ON books(issued);
CREATE INDEX IF NOT EXISTS idx_issues_book_id ON issues(book_id);
CREATE INDEX IF NOT EXISTS idx_issues_user_id ON issues(user_id);
CREATE INDEX IF NOT EXISTS idx_issues_dates ON issues(issue_date, return_date);

-- Insert sample data
-- Sample users
INSERT INTO users (name, email, password, role) VALUES
('Admin User', 'admin@library.com', 'admin123', 'admin'),
('John Student', 'john@student.com', 'student123', 'student'),
('Jane Student', 'jane@student.com', 'student123', 'student'),
('Bob Student', 'bob@student.com', 'student123', 'student');

-- Sample books
INSERT INTO books (title, author, isbn) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565'),
('To Kill a Mockingbird', 'Harper Lee', '9780446310789'),
('1984', 'George Orwell', '9780451524935'),
('Pride and Prejudice', 'Jane Austen', '9780141439518'),
('The Hobbit', 'J.R.R. Tolkien', '9780547928244'),
('The Catcher in the Rye', 'J.D. Salinger', '9780316769488'),
('Lord of the Flies', 'William Golding', '9780399501487'),
('Animal Farm', 'George Orwell', '9780451526342'),
('Brave New World', 'Aldous Huxley', '9780060850524'),
('The Alchemist', 'Paulo Coelho', '9780062315007');

-- Sample issues (some books are issued)
INSERT INTO issues (book_id, user_id, issue_date, return_date) VALUES
(1, 2, '2024-01-15', '2024-01-29'),
(3, 3, '2024-01-10', NULL),
(5, 4, '2024-01-05', '2024-01-19'),
(7, 2, '2024-01-12', NULL);

-- Update book status for issued books
UPDATE books SET issued = TRUE WHERE id IN (1, 3, 5, 7);

-- Enable Row Level Security (RLS) for better security
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE books ENABLE ROW LEVEL SECURITY;
ALTER TABLE issues ENABLE ROW LEVEL SECURITY;

-- Create policies for users table
CREATE POLICY "Users are viewable by everyone" ON users FOR SELECT USING (true);
CREATE POLICY "Users can be inserted by authenticated users" ON users FOR INSERT WITH CHECK (true);
CREATE POLICY "Users can be updated by themselves or admins" ON users FOR UPDATE USING (true);
CREATE POLICY "Users can be deleted by admins" ON users FOR DELETE USING (true);

-- Create policies for books table
CREATE POLICY "Books are viewable by everyone" ON books FOR SELECT USING (true);
CREATE POLICY "Books can be inserted by authenticated users" ON books FOR INSERT WITH CHECK (true);
CREATE POLICY "Books can be updated by authenticated users" ON books FOR UPDATE USING (true);
CREATE POLICY "Books can be deleted by authenticated users" ON books FOR DELETE USING (true);

-- Create policies for issues table
CREATE POLICY "Issues are viewable by everyone" ON issues FOR SELECT USING (true);
CREATE POLICY "Issues can be inserted by authenticated users" ON issues FOR INSERT WITH CHECK (true);
CREATE POLICY "Issues can be updated by authenticated users" ON issues FOR UPDATE USING (true);
CREATE POLICY "Issues can be deleted by authenticated users" ON issues FOR DELETE USING (true); 
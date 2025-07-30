
// Tab for viewing/searching books; admin can add, edit, delete.

package ui;

import javax.swing.*;
import service.BookService;
import model.Book;
import java.util.List;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class BookPanel extends JPanel {
    private BookService bookService = new BookService();
    private JTable table;
    private DefaultTableModel model;
    private boolean isAdmin;

    public BookPanel(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "ISBN", "Issued"}, 0);
        table = new JTable(model);
        refreshTable();

        JPanel topPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        if (isAdmin) {
            JButton addBtn = new JButton("Add");
            JButton editBtn = new JButton("Edit");
            JButton deleteBtn = new JButton("Delete");
            btnPanel.add(addBtn);
            btnPanel.add(editBtn);
            btnPanel.add(deleteBtn);

            addBtn.addActionListener(e -> addBookDialog());
            editBtn.addActionListener(e -> editBookDialog());
            deleteBtn.addActionListener(e -> deleteBookDialog());
        }
        add(btnPanel, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText();
            List<Book> books = bookService.searchBooks(keyword);
            updateTable(books);
        });
    }

    private void refreshTable() {
        List<Book> books = bookService.getAllBooks();
        updateTable(books);
    }

    private void updateTable(List<Book> books) {
        model.setRowCount(0);
        for (Book b : books) {
            model.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(), b.isIssued()});
        }
    }

    private void addBookDialog() {
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField isbnField = new JTextField();
        Object[] fields = {"Title:", titleField, "Author:", authorField, "ISBN:", isbnField};
        int option = JOptionPane.showConfirmDialog(this, fields, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Book b = new Book(titleField.getText(), authorField.getText(), isbnField.getText());
            if (bookService.addBook(b)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Book added!");
            } else {
                JOptionPane.showMessageDialog(this, "Error adding book.");
            }
        }
    }

    private void editBookDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a book first.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        Book book = bookService.getBookById(id);
        JTextField titleField = new JTextField(book.getTitle());
        JTextField authorField = new JTextField(book.getAuthor());
        JTextField isbnField = new JTextField(book.getIsbn());
        Object[] fields = {"Title:", titleField, "Author:", authorField, "ISBN:", isbnField};
        int option = JOptionPane.showConfirmDialog(this, fields, "Edit Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            book.setTitle(titleField.getText());
            book.setAuthor(authorField.getText());
            book.setIsbn(isbnField.getText());
            if (bookService.updateBook(book)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Book updated!");
            } else {
                JOptionPane.showMessageDialog(this, "Error updating book.");
            }
        }
    }

    private void deleteBookDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a book first.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        int option = JOptionPane.showConfirmDialog(this, "Delete book?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            if (bookService.deleteBook(id)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Book deleted!");
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting book.");
            }
        }
    }
}
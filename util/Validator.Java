
// Provides static methods to validate empty fields, email format, password strength, and ISBN.

package util;

public class Validator {
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
    public static boolean isValidPassword(String password) {
        return password != null &&
               password.length() >= 6 &&
               password.matches(".*[A-Za-z].*") &&
               password.matches(".*\\d.*");
    }
    public static boolean isValidISBN(String isbn) {
        return isbn != null &&
               (isbn.matches("\\d{10}") || isbn.matches("\\d{13}"));
    }
}
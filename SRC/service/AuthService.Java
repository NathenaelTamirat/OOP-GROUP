package service;

import dao.SupabaseUserDao;
import model.User;
import util.Logger;

public class AuthService {
    private SupabaseUserDao userDao = new SupabaseUserDao();
    
    public User authenticateUser(String email, String password) {
        try {
            User user = userDao.getUserByEmail(email);
            if (user != null && user.getPassword().equals(password)) {
                Logger.log("User authenticated: " + email);
                return user;
            }
            Logger.log("Authentication failed for: " + email);
            return null;
        } catch (Exception e) {
            Logger.log("Authentication error: " + e.getMessage());
            return null;
        }
    }
    
    public boolean registerUser(User user) {
        try {
            // Check if user already exists
            User existingUser = userDao.getUserByEmail(user.getEmail());
            if (existingUser != null) {
                Logger.log("Registration failed: Email already exists - " + user.getEmail());
                return false;
            }
            
            // Add new user
            boolean success = userDao.addUser(user);
            if (success) {
                Logger.log("User registered successfully: " + user.getEmail());
            } else {
                Logger.log("Registration failed: Could not add user - " + user.getEmail());
            }
            return success;
        } catch (Exception e) {
            Logger.log("Registration error: " + e.getMessage());
            return false;
        }
    }
}

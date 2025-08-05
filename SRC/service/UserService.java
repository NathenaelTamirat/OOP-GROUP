package service;

import dao.SupabaseUserDao;
import java.util.List;
import model.User;
import util.Logger;

public class UserService {
    private SupabaseUserDao userDao = new SupabaseUserDao();
    
    public boolean addUser(User user) {
        try {
            boolean success = userDao.addUser(user);
            if (success) {
                Logger.log("User added: " + user.getEmail());
        }
            return success;
        } catch (Exception e) {
            Logger.log("Error adding user: " + e.getMessage());
            return false;
        }
    }
    
    public User getUserByEmail(String email) {
        try {
        return userDao.getUserByEmail(email);
        } catch (Exception e) {
            Logger.log("Error getting user by email: " + e.getMessage());
            return null;
        }
    }
    
    public List<User> getAllUsers() {
        try {
        return userDao.getAllUsers();
        } catch (Exception e) {
            Logger.log("Error getting all users: " + e.getMessage());
            return null;
        }
    }
    
    public boolean updateUser(User user) {
        try {
            boolean success = userDao.updateUser(user);
            if (success) {
                Logger.log("User updated: " + user.getEmail());
    }
            return success;
        } catch (Exception e) {
            Logger.log("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteUser(int userId) {
        try {
            boolean success = userDao.deleteUser(userId);
            if (success) {
                Logger.log("User deleted: " + userId);
            }
            return success;
        } catch (Exception e) {
            Logger.log("Error deleting user: " + e.getMessage());
            return false;
        }
    }
} 
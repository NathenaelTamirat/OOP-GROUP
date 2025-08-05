package dao;

import model.User;
import util.SupabaseConnection;
import util.JsonUtils;
import util.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Supabase User Data Access Object
 * 
 * This class handles all user-related database operations using Supabase REST API.
 * It provides CRUD operations for users with proper error handling and logging.
 * 
 * @author Library Management Team
 * @version 3.0
 * @since 2024
 */
public class SupabaseUserDao {
    
    /**
     * Adds a new user to the database
     * 
     * @param user The user to add
     * @return true if successful, false otherwise
     */
    public boolean addUser(User user) {
        try {
            String jsonData = JsonUtils.createJson(
                "name", user.getName(),
                "email", user.getEmail(),
                "password", user.getPassword(),
                "role", user.getRole()
            );
            
            String response = SupabaseConnection.executePost("users", jsonData);
            if (response != null) {
                Logger.log("User added successfully: " + user.getEmail());
                return true;
            } else {
                Logger.log("Failed to add user: " + user.getEmail());
                return false;
            }
        } catch (Exception e) {
            Logger.log("Error adding user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets a user by email address
     * 
     * @param email The email to search for
     * @return User object or null if not found
     */
    public User getUserByEmail(String email) {
        try {
            String queryParams = "select=*&email=eq." + email;
            String response = SupabaseConnection.executeGet("users", queryParams);
            
            if (response != null && !response.equals("[]")) {
                String userJson = JsonUtils.getFirstElement(response);
                if (userJson != null) {
                    return parseUserFromJson(userJson);
                }
            }
        } catch (Exception e) {
            Logger.log("Error getting user by email: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Gets a user by ID
     * 
     * @param id The user ID
     * @return User object or null if not found
     */
    public User getUserById(int id) {
        try {
            String queryParams = "select=*&id=eq." + id;
            String response = SupabaseConnection.executeGet("users", queryParams);
            
            if (response != null && !response.equals("[]")) {
                String userJson = JsonUtils.getFirstElement(response);
                if (userJson != null) {
                    return parseUserFromJson(userJson);
                }
            }
        } catch (Exception e) {
            Logger.log("Error getting user by ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Gets all users from the database
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            String response = SupabaseConnection.executeGet("users", "select=*&order=id");
            
            if (response != null && !response.equals("[]")) {
                // Parse multiple users from JSON array
                String content = response.substring(1, response.length() - 1);
                String[] userJsons = splitJsonArray(content);
                
                for (String userJson : userJsons) {
                    if (userJson != null && !userJson.trim().isEmpty()) {
                        User user = parseUserFromJson(userJson);
                        if (user != null) {
                            users.add(user);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.log("Error getting all users: " + e.getMessage());
        }
        return users;
    }
    
    /**
     * Updates an existing user
     * 
     * @param user The user to update
     * @return true if successful, false otherwise
     */
    public boolean updateUser(User user) {
        try {
            String jsonData = JsonUtils.createJson(
                "name", user.getName(),
                "email", user.getEmail(),
                "password", user.getPassword(),
                "role", user.getRole()
            );
            
            String filter = "id=eq." + user.getId();
            String response = SupabaseConnection.executePatch("users", jsonData, filter);
            
            if (response != null) {
                Logger.log("User updated successfully: " + user.getEmail());
                return true;
            } else {
                Logger.log("Failed to update user: " + user.getEmail());
                return false;
            }
        } catch (Exception e) {
            Logger.log("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deletes a user by ID
     * 
     * @param id The user ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(int id) {
        try {
            String filter = "id=eq." + id;
            boolean success = SupabaseConnection.executeDelete("users", filter);
            
            if (success) {
                Logger.log("User deleted successfully: ID " + id);
                return true;
            } else {
                Logger.log("Failed to delete user: ID " + id);
                return false;
            }
        } catch (Exception e) {
            Logger.log("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Searches users by name, email, or role
     * 
     * @param searchTerm The search term
     * @return List of matching users
     */
    public List<User> searchUsers(String searchTerm) {
        List<User> users = new ArrayList<>();
        try {
            // Search by name (case insensitive)
            String nameQuery = "select=*&name=ilike.*" + searchTerm + "*";
            String nameResponse = SupabaseConnection.executeGet("users", nameQuery);
            addUsersFromResponse(users, nameResponse);
            
            // Search by email (case insensitive)
            String emailQuery = "select=*&email=ilike.*" + searchTerm + "*";
            String emailResponse = SupabaseConnection.executeGet("users", emailQuery);
            addUsersFromResponse(users, emailResponse);
            
            // Search by role (exact match)
            String roleQuery = "select=*&role=eq." + searchTerm;
            String roleResponse = SupabaseConnection.executeGet("users", roleQuery);
            addUsersFromResponse(users, roleResponse);
            
        } catch (Exception e) {
            Logger.log("Error searching users: " + e.getMessage());
        }
        return users;
    }
    
    /**
     * Parses a User object from JSON string
     * 
     * @param userJson JSON string representing a user
     * @return User object or null if parsing fails
     */
    private User parseUserFromJson(String userJson) {
        try {
            String idStr = JsonUtils.extractValue(userJson, "id");
            String name = JsonUtils.extractValue(userJson, "name");
            String email = JsonUtils.extractValue(userJson, "email");
            String password = JsonUtils.extractValue(userJson, "password");
            String role = JsonUtils.extractValue(userJson, "role");
            
            if (idStr != null && name != null && email != null && password != null && role != null) {
                int id = Integer.parseInt(idStr);
                return new User(id, name, email, password, role);
            }
        } catch (Exception e) {
            Logger.log("Error parsing user JSON: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Adds users from a JSON response to the list
     * 
     * @param users List to add users to
     * @param response JSON response string
     */
    private void addUsersFromResponse(List<User> users, String response) {
        if (response != null && !response.equals("[]")) {
            String content = response.substring(1, response.length() - 1);
            String[] userJsons = splitJsonArray(content);
            
            for (String userJson : userJsons) {
                if (userJson != null && !userJson.trim().isEmpty()) {
                    User user = parseUserFromJson(userJson);
                    if (user != null && !containsUser(users, user.getId())) {
                        users.add(user);
                    }
                }
            }
        }
    }
    
    /**
     * Checks if a user list contains a user with the given ID
     * 
     * @param users List of users
     * @param userId ID to check for
     * @return true if found, false otherwise
     */
    private boolean containsUser(List<User> users, int userId) {
        for (User user : users) {
            if (user.getId() == userId) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Splits a JSON array string into individual JSON objects
     * 
     * @param content JSON array content (without brackets)
     * @return Array of JSON object strings
     */
    private String[] splitJsonArray(String content) {
        List<String> objects = new ArrayList<>();
        int braceCount = 0;
        int start = -1;
        
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            
            if (c == '{') {
                if (start == -1) start = i;
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0 && start != -1) {
                    objects.add(content.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        
        return objects.toArray(new String[0]);
    }
} 
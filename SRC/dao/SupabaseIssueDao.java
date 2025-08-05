package dao;

import model.Issue;
import util.SupabaseConnection;
import util.JsonUtils;
import util.Logger;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Supabase Issue Data Access Object
 * 
 * This class handles all issue-related database operations using Supabase REST API.
 * It provides CRUD operations for issues with proper error handling and logging.
 * 
 * @author Library Management Team
 * @version 3.0
 * @since 2024
 */
public class SupabaseIssueDao {
    
    /**
     * Adds a new issue to the database
     * 
     * @param issue The issue to add
     * @return true if successful, false otherwise
     */
    public boolean addIssue(Issue issue) {
        try {
            String jsonData = JsonUtils.createJson(
                "book_id", String.valueOf(issue.getBookId()),
                "user_id", String.valueOf(issue.getUserId()),
                "issue_date", issue.getIssueDate().toString(),
                "return_date", issue.getReturnDate() != null ? issue.getReturnDate().toString() : null
            );
            
            String response = SupabaseConnection.executePost("issues", jsonData);
            if (response != null) {
                Logger.log("Issue added successfully: Book ID " + issue.getBookId() + ", User ID " + issue.getUserId());
                return true;
            } else {
                Logger.log("Failed to add issue: Book ID " + issue.getBookId() + ", User ID " + issue.getUserId());
                return false;
            }
        } catch (Exception e) {
            Logger.log("Error adding issue: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets an issue by ID
     * 
     * @param id The issue ID
     * @return Issue object or null if not found
     */
    public Issue getIssueById(int id) {
        try {
            String queryParams = "select=*&id=eq." + id;
            String response = SupabaseConnection.executeGet("issues", queryParams);
            
            if (response != null && !response.equals("[]")) {
                String issueJson = JsonUtils.getFirstElement(response);
                if (issueJson != null) {
                    return parseIssueFromJson(issueJson);
                }
            }
        } catch (Exception e) {
            Logger.log("Error getting issue by ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Gets all issues from the database
     * 
     * @return List of all issues
     */
    public List<Issue> getAllIssues() {
        List<Issue> issues = new ArrayList<>();
        try {
            String response = SupabaseConnection.executeGet("issues", "select=*&order=issue_date.desc");
            
            if (response != null && !response.equals("[]")) {
                String content = response.substring(1, response.length() - 1);
                String[] issueJsons = splitJsonArray(content);
                
                for (String issueJson : issueJsons) {
                    if (issueJson != null && !issueJson.trim().isEmpty()) {
                        Issue issue = parseIssueFromJson(issueJson);
                        if (issue != null) {
                            issues.add(issue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.log("Error getting all issues: " + e.getMessage());
        }
        return issues;
    }
    
    /**
     * Gets issues for a specific user
     * 
     * @param userId The user ID
     * @return List of issues for the user
     */
    public List<Issue> getIssuesByUserId(int userId) {
        List<Issue> issues = new ArrayList<>();
        try {
            String queryParams = "select=*&user_id=eq." + userId + "&order=issue_date.desc";
            String response = SupabaseConnection.executeGet("issues", queryParams);
            
            if (response != null && !response.equals("[]")) {
                String content = response.substring(1, response.length() - 1);
                String[] issueJsons = splitJsonArray(content);
                
                for (String issueJson : issueJsons) {
                    if (issueJson != null && !issueJson.trim().isEmpty()) {
                        Issue issue = parseIssueFromJson(issueJson);
                        if (issue != null) {
                            issues.add(issue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.log("Error getting issues by user ID: " + e.getMessage());
        }
        return issues;
    }
    
    /**
     * Gets issues for a specific book
     * 
     * @param bookId The book ID
     * @return List of issues for the book
     */
    public List<Issue> getIssuesByBookId(int bookId) {
        List<Issue> issues = new ArrayList<>();
        try {
            String queryParams = "select=*&book_id=eq." + bookId + "&order=issue_date.desc";
            String response = SupabaseConnection.executeGet("issues", queryParams);
            
            if (response != null && !response.equals("[]")) {
                String content = response.substring(1, response.length() - 1);
                String[] issueJsons = splitJsonArray(content);
                
                for (String issueJson : issueJsons) {
                    if (issueJson != null && !issueJson.trim().isEmpty()) {
                        Issue issue = parseIssueFromJson(issueJson);
                        if (issue != null) {
                            issues.add(issue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.log("Error getting issues by book ID: " + e.getMessage());
        }
        return issues;
    }
    
    /**
     * Gets active issues (not returned)
     * 
     * @return List of active issues
     */
    public List<Issue> getActiveIssues() {
        List<Issue> issues = new ArrayList<>();
        try {
            String queryParams = "select=*&return_date=is.null&order=issue_date.desc";
            String response = SupabaseConnection.executeGet("issues", queryParams);
            
            if (response != null && !response.equals("[]")) {
                String content = response.substring(1, response.length() - 1);
                String[] issueJsons = splitJsonArray(content);
                
                for (String issueJson : issueJsons) {
                    if (issueJson != null && !issueJson.trim().isEmpty()) {
                        Issue issue = parseIssueFromJson(issueJson);
                        if (issue != null) {
                            issues.add(issue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.log("Error getting active issues: " + e.getMessage());
        }
        return issues;
    }
    
    /**
     * Gets returned issues
     * 
     * @return List of returned issues
     */
    public List<Issue> getReturnedIssues() {
        List<Issue> issues = new ArrayList<>();
        try {
            String queryParams = "select=*&return_date=not.is.null&order=return_date.desc";
            String response = SupabaseConnection.executeGet("issues", queryParams);
            
            if (response != null && !response.equals("[]")) {
                String content = response.substring(1, response.length() - 1);
                String[] issueJsons = splitJsonArray(content);
                
                for (String issueJson : issueJsons) {
                    if (issueJson != null && !issueJson.trim().isEmpty()) {
                        Issue issue = parseIssueFromJson(issueJson);
                        if (issue != null) {
                            issues.add(issue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.log("Error getting returned issues: " + e.getMessage());
        }
        return issues;
    }
    
    /**
     * Updates an existing issue
     * 
     * @param issue The issue to update
     * @return true if successful, false otherwise
     */
    public boolean updateIssue(Issue issue) {
        try {
            String jsonData = JsonUtils.createJson(
                "book_id", String.valueOf(issue.getBookId()),
                "user_id", String.valueOf(issue.getUserId()),
                "issue_date", issue.getIssueDate().toString(),
                "return_date", issue.getReturnDate() != null ? issue.getReturnDate().toString() : null
            );
            
            String filter = "id=eq." + issue.getId();
            String response = SupabaseConnection.executePatch("issues", jsonData, filter);
            
            if (response != null) {
                Logger.log("Issue updated successfully: ID " + issue.getId());
                return true;
            } else {
                Logger.log("Failed to update issue: ID " + issue.getId());
                return false;
            }
        } catch (Exception e) {
            Logger.log("Error updating issue: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deletes an issue by ID
     * 
     * @param id The issue ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteIssue(int id) {
        try {
            String filter = "id=eq." + id;
            boolean success = SupabaseConnection.executeDelete("issues", filter);
            
            if (success) {
                Logger.log("Issue deleted successfully: ID " + id);
                return true;
            } else {
                Logger.log("Failed to delete issue: ID " + id);
                return false;
            }
        } catch (Exception e) {
            Logger.log("Error deleting issue: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Returns a book (sets return date)
     * 
     * @param issueId The issue ID
     * @param returnDate The return date
     * @return true if successful, false otherwise
     */
    public boolean returnBook(int issueId, LocalDate returnDate) {
        try {
            String jsonData = JsonUtils.createJson("return_date", returnDate.toString());
            String filter = "id=eq." + issueId;
            String response = SupabaseConnection.executePatch("issues", jsonData, filter);
            
            if (response != null) {
                Logger.log("Book returned successfully: Issue ID " + issueId + " on " + returnDate);
                return true;
            } else {
                Logger.log("Failed to return book: Issue ID " + issueId);
                return false;
            }
        } catch (Exception e) {
            Logger.log("Error returning book: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets issues for a specific date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of issues in the date range
     */
    public List<Issue> getIssuesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Issue> issues = new ArrayList<>();
        try {
            String queryParams = "select=*&issue_date=gte." + startDate + "&issue_date=lte." + endDate + "&order=issue_date.desc";
            String response = SupabaseConnection.executeGet("issues", queryParams);
            
            if (response != null && !response.equals("[]")) {
                String content = response.substring(1, response.length() - 1);
                String[] issueJsons = splitJsonArray(content);
                
                for (String issueJson : issueJsons) {
                    if (issueJson != null && !issueJson.trim().isEmpty()) {
                        Issue issue = parseIssueFromJson(issueJson);
                        if (issue != null) {
                            issues.add(issue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.log("Error getting issues by date range: " + e.getMessage());
        }
        return issues;
    }
    
    /**
     * Parses an Issue object from JSON string
     * 
     * @param issueJson JSON string representing an issue
     * @return Issue object or null if parsing fails
     */
    private Issue parseIssueFromJson(String issueJson) {
        try {
            String idStr = JsonUtils.extractValue(issueJson, "id");
            String bookIdStr = JsonUtils.extractValue(issueJson, "book_id");
            String userIdStr = JsonUtils.extractValue(issueJson, "user_id");
            String issueDateStr = JsonUtils.extractValue(issueJson, "issue_date");
            String returnDateStr = JsonUtils.extractValue(issueJson, "return_date");
            
            if (idStr != null && bookIdStr != null && userIdStr != null && issueDateStr != null) {
                int id = Integer.parseInt(idStr);
                int bookId = Integer.parseInt(bookIdStr);
                int userId = Integer.parseInt(userIdStr);
                LocalDate issueDate = LocalDate.parse(issueDateStr);
                LocalDate returnDate = returnDateStr != null ? LocalDate.parse(returnDateStr) : null;
                
                return new Issue(id, bookId, userId, issueDate, returnDate);
            }
        } catch (Exception e) {
            Logger.log("Error parsing issue JSON: " + e.getMessage());
        }
        return null;
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
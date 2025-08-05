# 🚀 Library Management System - Team Assignment Guide

## 📋 **OVERVIEW**
This document categorizes the remaining files into 3 distinct categories for your 3 teammates. Each teammate should work on their assigned category to avoid merge conflicts.

**IMPORTANT**: Your teammates already have the full folder locally. They just need to push their assigned files from their existing folders.

---

## 👥 **TEAMMATE 1: GUI/UI Components**
**Focus: User Interface and User Experience**

### 📁 **Files to Push from Your Local Folder:**
```
SRC/UI/
├── BooksPanel.java
├── IssuesPanel.java  
├── MainDashboard.Java
├── ReportsPanel.java
├── SignupFrame.java
├── SimpleLoginFrame.java
└── UsersPanel.java
```

### 🎯 **Responsibilities:**
- Create all UI panels and frames
- Handle user interface design
- Implement user interactions
- Manage form validations
- Create responsive layouts

### ⚠️ **Conflict Prevention:**
- Work ONLY in `SRC/UI/` directory
- Don't modify any service or model files
- Use existing model classes for data binding
- Coordinate with Teammate 2 for service integration

---

## 👥 **TEAMMATE 2: Service Layer & Business Logic**
**Focus: Business Logic and Data Processing**

### 📁 **Files to Push from Your Local Folder:**
```
SRC/service/
├── AuthService.Java
├── Bookservice.Java
├── IssueService.Java
└── UserService.java
```

### 🎯 **Responsibilities:**
- Implement business logic
- Handle data processing
- Manage authentication
- Coordinate between UI and DAO layers
- Implement validation rules

### ⚠️ **Conflict Prevention:**
- Work ONLY in `SRC/service/` directory
- Don't modify UI or model files directly
- Use existing DAO classes for data access
- Coordinate with Teammate 1 for UI integration
- Coordinate with Teammate 3 for utility functions

---

## 👥 **TEAMMATE 3: Utilities & Configuration**
**Focus: System Utilities and Configuration Management**

### 📁 **Files to Push from Your Local Folder:**
```
SRC/util/
├── ConfigManager.java
├── JsonUtils.java
├── Logger.Java
├── SupabaseConnection.java
├── UserSession.java
└── Validator.Java
```

### 📁 **Additional Files:**
```
SRC/dao/
├── SupabaseBookDao.java
├── SupabaseIssueDao.java
└── SupabaseUserDao.java
```

### 🎯 **Responsibilities:**
- Database connection management
- Configuration handling
- Logging and error tracking
- Data validation utilities
- Session management

### ⚠️ **Conflict Prevention:**
- Work ONLY in `SRC/util/` and `SRC/dao/` directories
- Don't modify UI or service files
- Provide utility functions for other teammates
- Handle all database connections

---

## 🔄 **WORKFLOW INSTRUCTIONS (FOR EXISTING FOLDERS)**

### **Step 1: Navigate to Your Local Folder**
```bash
cd [your-local-library-management-folder]
```

### **Step 2: Connect to GitHub Repository**
```bash
git remote add origin https://github.com/NathenaelTamirat/OOP-GROUP.git
git fetch origin
```

### **Step 3: Create Your Branch**
```bash
# Teammate 1
git checkout -b teammate1-ui-components

# Teammate 2  
git checkout -b teammate2-service-layer

# Teammate 3
git checkout -b teammate3-utilities-config
```

### **Step 4: Add Only Your Assigned Files**
```bash
# Teammate 1 - Add UI files only
git add SRC/UI/

# Teammate 2 - Add service files only  
git add SRC/service/

# Teammate 3 - Add util and dao files only
git add SRC/util/ SRC/dao/
```

### **Step 5: Commit and Push**
```bash
git commit -m "Add [your category] components"
git push origin [your-branch-name]
```

### **Step 6: Create Pull Request**
- Create PR from your branch to `main`
- Add detailed description of your changes
- Tag other teammates for review

---

## 🚫 **CONFLICT AVOIDANCE RULES**

### **DO NOT:**
- ❌ Modify files outside your assigned directory
- ❌ Change existing model classes
- ❌ Modify `pom.xml` or `.gitignore`
- ❌ Work on the same files as other teammates
- ❌ Push directly to main branch
- ❌ Add files from other teammates' categories

### **DO:**
- ✅ Work only in your assigned directories
- ✅ Use existing interfaces and models
- ✅ Communicate with teammates for integration
- ✅ Test your components thoroughly
- ✅ Create descriptive commit messages
- ✅ Only add your assigned files to git

---

## 📞 **COORDINATION POINTS**

### **Teammate 1 ↔ Teammate 2:**
- UI components need service methods
- Service layer needs UI event handlers
- Coordinate on data flow patterns

### **Teammate 2 ↔ Teammate 3:**
- Services need utility functions
- Database connections for services
- Validation and logging integration

### **Teammate 1 ↔ Teammate 3:**
- UI needs configuration settings
- Session management for UI
- Error handling and logging

---

## 🎯 **SUCCESS CRITERIA**

### **When Complete:**
- ✅ All UI components are functional
- ✅ All services are implemented
- ✅ All utilities are working
- ✅ No merge conflicts
- ✅ System integrates properly
- ✅ All tests pass

---

## 📞 **EMERGENCY CONTACTS**
If you encounter conflicts or issues:
1. **STOP** working immediately
2. **COMMUNICATE** with your teammates
3. **CREATE** a backup branch
4. **RESOLVE** conflicts together
5. **TEST** thoroughly before merging

---

**Good luck, team! 🚀** 
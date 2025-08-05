# 🚀 Simple Steps for Teammates

## 📋 **You already have the full folder! Just follow these steps:**

### **Step 1: Open your local folder**
```bash
cd [your-library-management-folder]
```

### **Step 2: Connect to GitHub**
```bash
git remote add origin https://github.com/NathenaelTamirat/OOP-GROUP.git
git fetch origin
```

### **Step 3: Create your branch**
```bash
# Choose your branch based on your assignment:
git checkout -b teammate1-ui-components    # For UI work
git checkout -b teammate2-service-layer    # For service work  
git checkout -b teammate3-utilities-config # For utilities work
```

### **Step 4: Add ONLY your assigned files**
```bash
# TEAMMATE 1 (UI):
git add SRC/UI/

# TEAMMATE 2 (Service):
git add SRC/service/

# TEAMMATE 3 (Utilities & DAO):
git add SRC/util/ SRC/dao/
```

### **Step 5: Commit and push**
```bash
git commit -m "Add [your category] components"
git push origin [your-branch-name]
```

### **Step 6: Create Pull Request**
- Go to GitHub repository
- Click "Compare & pull request"
- Add description of your changes
- Tag teammates for review

---

## 🎯 **Your Assignments:**

### **TEAMMATE 1**: Push `SRC/UI/` folder (7 files)
### **TEAMMATE 2**: Push `SRC/service/` folder (4 files)  
### **TEAMMATE 3**: Push `SRC/util/` and `SRC/dao/` folders (9 files)

---

## ⚠️ **IMPORTANT:**
- ✅ Only add your assigned files
- ✅ Don't modify existing files
- ✅ Use your own branch
- ✅ Communicate with teammates
- ❌ Don't push everything at once

**That's it! 🚀** 
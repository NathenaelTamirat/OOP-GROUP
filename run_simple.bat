@echo off
REM Simple Library Management System - Run Script (Windows)
REM This script compiles and runs the working version

echo 🚀 Starting Library Management System (Simple Version)...
echo ======================================================

REM Clean previous builds
if exist "bin" rmdir /s /q "bin"
mkdir bin

REM Compile step by step
echo 📦 Compiling utility classes...
javac -d bin -cp ".;*" util\*.java
if %errorlevel% neq 0 (
    echo ❌ Failed to compile utility classes
    pause
    exit /b 1
)

echo 📦 Compiling model classes...
javac -d bin -cp ".;*;bin" model\*.java
if %errorlevel% neq 0 (
    echo ❌ Failed to compile model classes
    pause
    exit /b 1
)

echo 📦 Compiling DAO classes...
javac -d bin -cp ".;*;bin" dao\*.java
if %errorlevel% neq 0 (
    echo ❌ Failed to compile DAO classes
    pause
    exit /b 1
)

echo 📦 Compiling service classes...
javac -d bin -cp ".;*;bin" service\*.java
if %errorlevel% neq 0 (
    echo ❌ Failed to compile service classes
    pause
    exit /b 1
)

echo 📦 Compiling UI classes...
javac -d bin -cp ".;*;bin" UI\SimpleLoginFrame.java UI\SignupFrame.java UI\BooksPanel.java UI\IssuesPanel.java UI\UsersPanel.java UI\ReportsPanel.java UI\MainDashboard.java
if %errorlevel% neq 0 (
    echo ❌ Failed to compile UI classes
    pause
    exit /b 1
)

echo 📦 Compiling main class...
javac -d bin -cp ".;*;bin" Main.java
if %errorlevel% neq 0 (
    echo ❌ Failed to compile main class
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo.
echo 🚀 Launching application...
echo.
echo 📝 Test Credentials:
echo    Admin: admin@library.com / admin123
echo    Student: john@student.com / student123
echo.
echo 💡 You can also create a new account using the Sign Up button!
echo.

REM Run the application
java -cp ".;*;bin" Main

if %errorlevel% neq 0 (
    echo ❌ Application failed to start
    pause
    exit /b 1
)

echo ✅ Application closed successfully
pause 
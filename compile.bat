@echo off
REM Library Management System - Compilation Script (Windows)
REM This script compiles all Java files in the correct order

echo 🔨 Compiling Library Management System...
echo ========================================

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Compile utility classes first (dependencies)
echo 📦 Compiling utility classes...
javac -d bin -cp ".;*" util\*.java
if %errorlevel% neq 0 (
    echo ❌ Failed to compile utility classes
    pause
    exit /b 1
)

REM Compile model classes
echo 📦 Compiling model classes...
javac -d bin -cp ".;*;bin" model\*.java
if %errorlevel% neq 0 (
    echo ❌ Failed to compile model classes
    pause
    exit /b 1
)

REM Compile DAO classes
echo 📦 Compiling DAO classes...
javac -d bin -cp ".;*;bin" dao\*.java
if %errorlevel% neq 0 (
    echo ❌ Failed to compile DAO classes
    pause
    exit /b 1
)

REM Compile service classes
echo 📦 Compiling service classes...
javac -d bin -cp ".;*;bin" service\*.java
if %errorlevel% neq 0 (
    echo ❌ Failed to compile service classes
    pause
    exit /b 1
)

REM Compile UI classes
echo 📦 Compiling UI classes...
javac -d bin -cp ".;*;bin" UI\*.java
if %errorlevel% neq 0 (
    echo ❌ Failed to compile UI classes
    pause
    exit /b 1
)

REM Compile main class
echo 📦 Compiling main class...
javac -d bin -cp ".;*;bin" Main.java
if %errorlevel% neq 0 (
    echo ❌ Failed to compile main class
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo.
echo 🚀 To run the application:
echo    java -cp ".;*;bin" Main
echo.
pause 
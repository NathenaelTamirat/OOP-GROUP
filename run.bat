@echo off
REM Library Management System - Run Script (Windows)
REM This script runs the compiled application

echo 🚀 Starting Library Management System...
echo ======================================

REM Check if bin directory exists
if not exist "bin" (
    echo ❌ Compiled classes not found!
    echo Please run compile.bat first.
    pause
    exit /b 1
)

REM Check if configuration exists
if not exist "config\supabase_config.properties" (
    echo ❌ Configuration file not found!
    echo Please run setup_config.bat first.
    pause
    exit /b 1
)

REM Run the application
echo 📱 Launching application...
java -cp ".;*;bin" Main

if %errorlevel% neq 0 (
    echo ❌ Application failed to start
    pause
    exit /b 1
)

echo ✅ Application closed successfully
pause 
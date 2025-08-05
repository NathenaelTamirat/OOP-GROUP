@echo off
REM Library Management System - Configuration Setup Script (Windows)
REM This script helps users set up their Supabase configuration

echo 🔧 Library Management System - Configuration Setup
echo ==================================================

REM Check if config file already exists
if exist "config\supabase_config.properties" (
    echo ⚠️  Configuration file already exists!
    set /p overwrite="Do you want to overwrite it? (y/N): "
    if /i not "%overwrite%"=="y" (
        echo Setup cancelled.
        pause
        exit /b 0
    )
)

echo.
echo 📋 Please provide your Supabase configuration:
echo.

REM Get Supabase URL
set /p supabase_url="Enter your Supabase Project URL (e.g., https://your-project.supabase.co): "
if "%supabase_url%"=="" (
    echo ❌ Supabase URL is required!
    pause
    exit /b 1
)

REM Get Supabase API Key
set /p supabase_key="Enter your Supabase Anonymous Key: "
if "%supabase_key%"=="" (
    echo ❌ Supabase API Key is required!
    pause
    exit /b 1
)

REM Create config directory if it doesn't exist
if not exist "config" mkdir config

REM Create the configuration file
(
echo # Supabase Configuration
echo # This file contains the Supabase project configuration
echo # DO NOT commit this file to version control in production
echo.
echo # Supabase Project URL
echo SUPABASE_URL=%supabase_url%
echo.
echo # Supabase Anonymous Key (Public API Key)
echo SUPABASE_ANON_KEY=%supabase_key%
echo.
echo # API Configuration
echo API_VERSION=v1
echo REST_ENDPOINT=/rest/v1
echo.
echo # Connection Settings
echo CONNECTION_TIMEOUT=30
echo REQUEST_TIMEOUT=30
echo MAX_RETRIES=3
) > config\supabase_config.properties

echo.
echo ✅ Configuration file created successfully!
echo 📁 Location: config\supabase_config.properties
echo.
echo 🔒 Security Note: This file contains sensitive information.
echo    Make sure it's not committed to version control.
echo.
echo 🚀 You can now run the application with:
echo    javac -cp ".;*" *.java */*.java */*/*.java
echo    java -cp ".;*" Main
echo.
echo 📝 Don't forget to set up your Supabase database using the supabase_setup.sql script!
pause 
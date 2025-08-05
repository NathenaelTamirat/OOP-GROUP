@echo off
REM Library Management System - Clean Script (Windows)
REM This script removes build artifacts and problematic directories

echo 🧹 Cleaning Library Management System...
echo ======================================

REM Remove __MACOSX directory (macOS metadata)
if exist "__MACOSX" (
    echo 🗑️  Removing __MACOSX directory...
    rmdir /s /q "__MACOSX"
    echo ✅ __MACOSX directory removed
)

REM Remove compiled classes
if exist "bin" (
    echo 🗑️  Removing compiled classes...
    rmdir /s /q "bin"
    echo ✅ Compiled classes removed
)

REM Remove log files
if exist "logs" (
    echo 🗑️  Removing log files...
    rmdir /s /q "logs"
    echo ✅ Log files removed
)

REM Remove temporary files
echo 🗑️  Removing temporary files...
del /q *.tmp 2>nul
del /q *.temp 2>nul
del /q *.class 2>nul
echo ✅ Temporary files removed

echo ✅ Cleanup completed!
echo.
echo 🚀 You can now recompile with: compile.bat
echo.
pause 
#!/bin/bash

# Library Management System - Clean Script (Linux/Mac)
# This script removes build artifacts and problematic directories

echo "🧹 Cleaning Library Management System..."
echo "======================================"

# Remove __MACOSX directory (macOS metadata)
if [ -d "__MACOSX" ]; then
    echo "🗑️  Removing __MACOSX directory..."
    rm -rf "__MACOSX"
    echo "✅ __MACOSX directory removed"
fi

# Remove compiled classes
if [ -d "bin" ]; then
    echo "🗑️  Removing compiled classes..."
    rm -rf "bin"
    echo "✅ Compiled classes removed"
fi

# Remove log files
if [ -d "logs" ]; then
    echo "🗑️  Removing log files..."
    rm -rf "logs"
    echo "✅ Log files removed"
fi

# Remove temporary files
echo "🗑️  Removing temporary files..."
find . -name "*.tmp" -delete 2>/dev/null
find . -name "*.temp" -delete 2>/dev/null
find . -name "*.class" -delete 2>/dev/null
echo "✅ Temporary files removed"

echo "✅ Cleanup completed!"
echo ""
echo "🚀 You can now recompile with: ./compile.sh"
echo "" 
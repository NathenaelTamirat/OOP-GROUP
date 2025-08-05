#!/bin/bash

# Library Management System - Run Script (Linux/Mac)
# This script runs the compiled application

echo "🚀 Starting Library Management System..."
echo "======================================"

# Check if bin directory exists
if [ ! -d "bin" ]; then
    echo "❌ Compiled classes not found!"
    echo "Please run compile.sh first."
    exit 1
fi

# Check if configuration exists
if [ ! -f "config/supabase_config.properties" ]; then
    echo "❌ Configuration file not found!"
    echo "Please run setup_config.sh first."
    exit 1
fi

# Run the application
echo "📱 Launching application..."
java -cp ".:*:bin" Main

if [ $? -ne 0 ]; then
    echo "❌ Application failed to start"
    exit 1
fi

echo "✅ Application closed successfully" 
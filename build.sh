#!/bin/bash

# Library Management System Build Script

echo "🏗️  Building Library Management System..."

# Check if PostgreSQL JDBC driver exists
if [ ! -f "postgresql-jdbc.jar" ]; then
    echo "⚠️  PostgreSQL JDBC driver not found!"
    echo "📥 Please download postgresql-jdbc.jar and place it in the project root"
    echo "🔗 Download from: https://jdbc.postgresql.org/download/"
    exit 1
fi

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile all Java files
echo "📦 Compiling Java files..."
javac -cp ".:postgresql-jdbc.jar" -d bin *.java */*.java */*/*.java

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo ""
    echo "🚀 To run the application:"
    echo "   java -cp \"bin:postgresql-jdbc.jar\" Main"
    echo ""
    echo "📋 Make sure PostgreSQL is running and database is set up!"
else
    echo "❌ Compilation failed!"
    exit 1
fi 
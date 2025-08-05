#!/bin/bash

# Library Management System - Compilation Script (Linux/Mac)
# This script compiles all Java files in the correct order

echo "🔨 Compiling Library Management System..."
echo "========================================"

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile utility classes first (dependencies)
echo "📦 Compiling utility classes..."
javac -d bin -cp ".:*" util/*.java
if [ $? -ne 0 ]; then
    echo "❌ Failed to compile utility classes"
    exit 1
fi

# Compile model classes
echo "📦 Compiling model classes..."
javac -d bin -cp ".:*:bin" model/*.java
if [ $? -ne 0 ]; then
    echo "❌ Failed to compile model classes"
    exit 1
fi

# Compile DAO classes
echo "📦 Compiling DAO classes..."
javac -d bin -cp ".:*:bin" dao/*.java
if [ $? -ne 0 ]; then
    echo "❌ Failed to compile DAO classes"
    exit 1
fi

# Compile service classes
echo "📦 Compiling service classes..."
javac -d bin -cp ".:*:bin" service/*.java
if [ $? -ne 0 ]; then
    echo "❌ Failed to compile service classes"
    exit 1
fi

# Compile UI classes
echo "📦 Compiling UI classes..."
javac -d bin -cp ".:*:bin" UI/*.java
if [ $? -ne 0 ]; then
    echo "❌ Failed to compile UI classes"
    exit 1
fi

# Compile main class
echo "📦 Compiling main class..."
javac -d bin -cp ".:*:bin" Main.java
if [ $? -ne 0 ]; then
    echo "❌ Failed to compile main class"
    exit 1
fi

echo "✅ Compilation successful!"
echo ""
echo "🚀 To run the application:"
echo "   java -cp \".:*:bin\" Main"
echo "" 
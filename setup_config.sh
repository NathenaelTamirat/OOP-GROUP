#!/bin/bash

# Library Management System - Configuration Setup Script
# This script helps users set up their Supabase configuration

echo "🔧 Library Management System - Configuration Setup"
echo "=================================================="

# Check if config file already exists
if [ -f "config/supabase_config.properties" ]; then
    echo "⚠️  Configuration file already exists!"
    read -p "Do you want to overwrite it? (y/N): " overwrite
    if [[ ! $overwrite =~ ^[Yy]$ ]]; then
        echo "Setup cancelled."
        exit 0
    fi
fi

echo ""
echo "📋 Please provide your Supabase configuration:"
echo ""

# Get Supabase URL
read -p "Enter your Supabase Project URL (e.g., https://your-project.supabase.co): " supabase_url
if [ -z "$supabase_url" ]; then
    echo "❌ Supabase URL is required!"
    exit 1
fi

# Get Supabase API Key
read -p "Enter your Supabase Anonymous Key: " supabase_key
if [ -z "$supabase_key" ]; then
    echo "❌ Supabase API Key is required!"
    exit 1
fi

# Create config directory if it doesn't exist
mkdir -p config

# Create the configuration file
cat > config/supabase_config.properties << EOF
# Supabase Configuration
# This file contains the Supabase project configuration
# DO NOT commit this file to version control in production

# Supabase Project URL
SUPABASE_URL=$supabase_url

# Supabase Anonymous Key (Public API Key)
SUPABASE_ANON_KEY=$supabase_key

# API Configuration
API_VERSION=v1
REST_ENDPOINT=/rest/v1

# Connection Settings
CONNECTION_TIMEOUT=30
REQUEST_TIMEOUT=30
MAX_RETRIES=3
EOF

echo ""
echo "✅ Configuration file created successfully!"
echo "📁 Location: config/supabase_config.properties"
echo ""
echo "🔒 Security Note: This file contains sensitive information."
echo "   Make sure it's not committed to version control."
echo ""
echo "🚀 You can now run the application with:"
echo "   javac -cp \".:*\" *.java */*.java */*/*.java"
echo "   java -cp \".:*\" Main"
echo ""
echo "📝 Don't forget to set up your Supabase database using the supabase_setup.sql script!" 
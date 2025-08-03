-- Database setup script for ACME Medical
-- Run this as root user

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS acmemedical;

-- Create user if it doesn't exist
CREATE USER IF NOT EXISTS 'cst8277'@'localhost' IDENTIFIED BY '8277';

-- Grant all privileges to the user
GRANT ALL PRIVILEGES ON acmemedical.* TO 'cst8277'@'localhost';

-- Flush privileges to apply changes
FLUSH PRIVILEGES;

-- Show the created database
SHOW DATABASES;

-- Show user privileges
SHOW GRANTS FOR 'cst8277'@'localhost'; 
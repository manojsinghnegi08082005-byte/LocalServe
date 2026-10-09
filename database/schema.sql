-- LocalServe (Java version) database schema
-- Import this into MySQL before running the app:
--   mysql -u root -p < schema.sql

CREATE DATABASE IF NOT EXISTS localserve_db;
USE localserve_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,   -- stores SHA-256 hex hash
    role ENUM('user','provider') NOT NULL
);

CREATE TABLE IF NOT EXISTS provider_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,      -- UNIQUE required for ON DUPLICATE KEY UPDATE
    name VARCHAR(100) NOT NULL,
    service VARCHAR(100) NOT NULL,
    price_per_hour DECIMAL(10,2) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    experience INT NOT NULL,
    -- where the provider takes jobs (NULL = not entered yet, such providers are never shown to customers)
    service_city VARCHAR(100) NULL,
    service_state VARCHAR(100) NULL,
    service_pin_code VARCHAR(6) NULL,   -- string, so leading zeros are kept
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_provider_pin (service_pin_code)
);

CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    provider_id INT NOT NULL,
    status ENUM('pending','accepted','rejected') NOT NULL DEFAULT 'pending',
    booking_date DATE NOT NULL,
    time_slot VARCHAR(50) NOT NULL,
    -- location the customer asked for, saved as typed at booking time (NULL for old bookings)
    service_city VARCHAR(100) NULL,
    service_state VARCHAR(100) NULL,
    service_pin_code VARCHAR(6) NULL,
    service_address VARCHAR(255) NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (provider_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    message TEXT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);

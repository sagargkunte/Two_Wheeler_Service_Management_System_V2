-- ==============================================================
-- SQL Schema for Two-Wheeler Service Management System
-- Database: two_wheeler_db (MySQL)
-- ==============================================================

CREATE DATABASE IF NOT EXISTS two_wheeler_db;
USE two_wheeler_db;

-- 1. Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL, -- 'owner' or 'mechanic'
    is_verified BOOLEAN DEFAULT FALSE,
    otp VARCHAR(255) NULL,
    otp_expires DATETIME NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Mechanics table
CREATE TABLE IF NOT EXISTS mechanics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    shop_name VARCHAR(255) NOT NULL,
    address VARCHAR(500) NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    services_offered TEXT NULL,
    description TEXT NULL,
    rating DOUBLE DEFAULT 4.5,
    reviews_count INT DEFAULT 12,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_mechanic_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Bikes table
CREATE TABLE IF NOT EXISTS bikes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    registration_number VARCHAR(100) NULL,
    problem TEXT NOT NULL,
    service_type VARCHAR(50) NOT NULL, -- 'repair' or 'servicing'
    status VARCHAR(50) DEFAULT 'requested', -- 'requested', 'in-repair', 'ready-for-delivery', 'delivered'
    location VARCHAR(500) NULL,
    latitude DOUBLE NULL,
    longitude DOUBLE NULL,
    mechanic_id BIGINT NULL,
    appointment_id BIGINT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bike_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_bike_mechanic FOREIGN KEY (mechanic_id) REFERENCES mechanics(id) ON DELETE SET NULL
);

-- 4. Appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bike_id BIGINT NOT NULL,
    mechanic_id BIGINT NOT NULL,
    appointment_date VARCHAR(50) NOT NULL,
    appointment_time VARCHAR(50) NOT NULL,
    notes TEXT NULL,
    status VARCHAR(50) DEFAULT 'booked', -- 'booked', 'bike-received', 'under-repair', 'completed', 'cancelled'
    estimated_completion VARCHAR(100) NULL,
    mechanic_notes TEXT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_bike FOREIGN KEY (bike_id) REFERENCES bikes(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_mechanic FOREIGN KEY (mechanic_id) REFERENCES mechanics(id) ON DELETE CASCADE
);

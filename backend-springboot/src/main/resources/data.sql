-- ==============================================================
-- Sample Demo Data for Two-Wheeler Service Management System
-- Passwords are all BCrypt-hashed for "password123":
-- $2a$10$7Zrqc0.5yR2fDqjR/N2/b.QoT5nB7tqWb/YQ/3e9p3Y0Xy8s8S2eq
-- ==============================================================

USE two_wheeler_db;

-- Insert Demo Users (1 owner, 3 mechanics)
INSERT IGNORE INTO users (id, name, email, phone, password, type, is_verified, created_at, updated_at)
VALUES 
(1, 'Rahul Sharma', 'owner@example.com', '9876543210', '$2a$10$30j.0KUr0350g.34HfppseoJU60wAwDVHDJWqGWsCPYhaSNrpUQUS', 'owner', TRUE, NOW(), NOW()),
(2, 'Amit Verma', 'amit@mechanic.com', '9811223344', '$2a$10$30j.0KUr0350g.34HfppseoJU60wAwDVHDJWqGWsCPYhaSNrpUQUS', 'mechanic', TRUE, NOW(), NOW()),
(3, 'Suresh Patel', 'suresh@mechanic.com', '9822334455', '$2a$10$30j.0KUr0350g.34HfppseoJU60wAwDVHDJWqGWsCPYhaSNrpUQUS', 'mechanic', TRUE, NOW(), NOW()),
(4, 'Vikram Singh', 'vikram@mechanic.com', '9833445566', '$2a$10$30j.0KUr0350g.34HfppseoJU60wAwDVHDJWqGWsCPYhaSNrpUQUS', 'mechanic', TRUE, NOW(), NOW());

-- Insert Demo Mechanics
INSERT IGNORE INTO mechanics (id, user_id, shop_name, address, latitude, longitude, services_offered, description, rating, reviews_count, created_at, updated_at)
VALUES 
(1, 2, 'Speedy Auto & Bike Garage', 'MG Road, City Center', 18.5204, 73.8567, 'General Servicing, Engine Overhaul, Brake Repair, Oil Change', 'Specialized in multi-brand two-wheeler repairs with 10+ years experience.', 4.8, 42, NOW(), NOW()),
(2, 3, 'City Moto Repairs & Spares', 'Station Road, Near Bus Stand', 18.5284, 73.8645, 'Chain Lubrication, Carburetor Tuning, Electrical Diagnostics', 'Authorized service partner with genuine parts and quick turnaround.', 4.6, 28, NOW(), NOW()),
(3, 4, 'Pro Riders Bike Care', 'Highway Junction, Ring Road', 18.5140, 73.8420, 'Full Servicing, Tyre Replacement, Battery Check, Washing', 'Complete diagnostic and service point for commuter and sports bikes.', 4.7, 35, NOW(), NOW());

-- Insert Demo Bike for Owner
INSERT IGNORE INTO bikes (id, user_id, name, model, registration_number, problem, service_type, status, location, latitude, longitude, mechanic_id, appointment_id, created_at, updated_at)
VALUES 
(1, 1, 'Royal Enfield', 'Classic 350', 'MH 12 AB 1234', 'Engine oil leaking and unusual rattling sound from engine', 'repair', 'in-repair', 'Shivaji Nagar, Pune', 18.5314, 73.8446, 1, 1, NOW(), NOW());

-- Insert Demo Appointment
INSERT IGNORE INTO appointments (id, bike_id, mechanic_id, appointment_date, appointment_time, notes, status, estimated_completion, mechanic_notes, created_at, updated_at)
VALUES 
(1, 1, 1, '2026-11-21', '10:30 AM', 'Please inspect the engine gasket and change engine oil.', 'bike-received', 'Today by 5:00 PM', 'Inspecting gasket seal and oil drain bolt.', NOW(), NOW());

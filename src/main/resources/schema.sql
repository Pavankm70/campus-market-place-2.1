-- ==============================================================================
-- CAMPUS MARKETPLACE - AIVEN MYSQL DATABASE SCHEMA & DEMO DATA
-- Compatible with Aiven Cloud MySQL 8.x / Local MySQL / MariaDB
-- Default student account credentials:
--   Email:    alex@nmit.ac.in (or sarah@nmit.ac.in, marcus@nmit.ac.in)
--   Password: Password123!
-- ==============================================================================

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS wishlists;
DROP TABLE IF EXISTS inquiry_replies;
DROP TABLE IF EXISTS inquiries;
DROP TABLE IF EXISTS listings;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    campus_name VARCHAR(100) DEFAULT 'Main Campus',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. LISTINGS TABLE
CREATE TABLE IF NOT EXISTS listings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    image_url LONGTEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    condition_type VARCHAR(50) DEFAULT 'Good',
    isbn VARCHAR(50),
    author VARCHAR(150),
    pickup_location VARCHAR(150) DEFAULT 'Campus Library / Student Center',
    seller_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_listings_seller FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_listings_seller (seller_id),
    INDEX idx_listings_category (category),
    INDEX idx_listings_status (status),
    INDEX idx_listings_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. INQUIRIES TABLE
CREATE TABLE IF NOT EXISTS inquiries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    listing_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    contact_info VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inquiries_listing FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE CASCADE,
    CONSTRAINT fk_inquiries_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_inquiries_receiver FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_inquiries_listing (listing_id),
    INDEX idx_inquiries_sender (sender_id),
    INDEX idx_inquiries_receiver (receiver_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. INQUIRY REPLIES TABLE
CREATE TABLE IF NOT EXISTS inquiry_replies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inquiry_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_replies_inquiry FOREIGN KEY (inquiry_id) REFERENCES inquiries(id) ON DELETE CASCADE,
    CONSTRAINT fk_replies_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_replies_inquiry (inquiry_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. WISHLISTS TABLE
CREATE TABLE IF NOT EXISTS wishlists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    listing_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wishlists_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_wishlists_listing FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_listing_wishlist UNIQUE (user_id, listing_id),
    INDEX idx_wishlists_user (user_id),
    INDEX idx_wishlists_listing (listing_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- DEMO SEED DATA (BCrypt encrypted password: Password123!)
-- ==============================================================================
INSERT INTO users (id, name, email, password, phone, campus_name, created_at, updated_at) VALUES
(1, 'Alex Rivera', 'alex@nmit.ac.in', '$2a$10$EolXxDSwNVrHkj8ff5n2OupQsusuEaYcA2OnH9JpHmQel9ZAGDaKy', '9876543210', 'NMIT Main Campus', NOW(), NOW()),
(2, 'Sarah Chen', 'sarah@nmit.ac.in', '$2a$10$EolXxDSwNVrHkj8ff5n2OupQsusuEaYcA2OnH9JpHmQel9ZAGDaKy', '9876543211', 'NMIT Science Quad', NOW(), NOW()),
(3, 'Marcus Johnson', 'marcus@nmit.ac.in', '$2a$10$EolXxDSwNVrHkj8ff5n2OupQsusuEaYcA2OnH9JpHmQel9ZAGDaKy', '9876543212', 'NMIT Engineering Hall', NOW(), NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO listings (id, title, description, price, category, image_url, status, condition_type, isbn, author, pickup_location, seller_id, created_at, updated_at) VALUES
(1, 'Introduction to Algorithms (CLRS 4th Edition)', 'Essential textbook for CS201 / Algorithms course. Barely highlighted, binding in mint condition. Includes cheat-sheet inserts.', 65.00, 'BOOKS', 'https://images.unsplash.com/photo-1532012164546-f432f2e3777a?w=800&auto=format&fit=crop&q=80', 'AVAILABLE', 'Like New', '9780262046305', 'Thomas H. Cormen, Charles E. Leiserson', 'CS Building Lobby / Library 2nd Floor', 1, NOW(), NOW()),
(2, 'TI-84 Plus CE Color Graphing Calculator', 'Preloaded with all programs for Calculus, Stats, and Physics. Comes with official USB charging cable and slide protective cover.', 75.00, 'ELECTRONICS', 'https://images.unsplash.com/photo-1587145820266-a5951ee6f620?w=800&auto=format&fit=crop&q=80', 'AVAILABLE', 'Good', NULL, NULL, 'Student Union Food Court', 3, NOW(), NOW()),
(3, 'Chemistry Lab Coat & Safety Goggles Set', 'Size M white 100% cotton flame-resistant lab coat and UV-blocking splash-proof safety goggles. Required for CHEM 101/102 labs.', 22.00, 'LAB_SUPPLIES', 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&auto=format&fit=crop&q=80', 'AVAILABLE', 'Good', NULL, NULL, 'Science Hall Lab Wing', 2, NOW(), NOW()),
(4, 'Campbell Biology (12th Edition)', 'Standard textbook for General Biology BIO110. Clean pages, no missing diagrams. Saved me during midterm prep.', 80.00, 'BOOKS', 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800&auto=format&fit=crop&q=80', 'AVAILABLE', 'Very Good', '9780135188743', 'Lisa A. Urry, Michael L. Cain', 'Health Sciences Atrium', 2, NOW(), NOW()),
(5, 'Ergonomic Mesh Dorm Desk Chair', 'Breathable mesh high-back chair with adjustable armrests and lumbar support. Fits nicely into standard dorm desks. Pick up on North Campus.', 45.00, 'FURNITURE', 'https://images.unsplash.com/photo-1580481077195-c3a821a506cb?w=800&auto=format&fit=crop&q=80', 'AVAILABLE', 'Good', NULL, NULL, 'Maple Residence Hall Lounge', 1, NOW(), NOW()),
(6, 'Apple iPad 9th Gen 64GB WiFi with Apple Pencil', 'Space Gray iPad with 1st Gen Apple Pencil. Screen protector applied from day 1. Fantastic for GoodNotes or Notability lecture notes.', 240.00, 'ELECTRONICS', 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=800&auto=format&fit=crop&q=80', 'SOLD', 'Like New', NULL, NULL, 'Campus Tech Store Front', 3, NOW(), NOW()),
(7, 'Official Campus Athletics Varsity Hoodie (Size M)', 'Heavyweight 80/20 cotton blend campus hoodie. Navy blue with embroidered golden crest. Only worn twice, laundered and ready.', 30.00, 'CLOTHING', 'https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=800&auto=format&fit=crop&q=80', 'AVAILABLE', 'Like New', NULL, NULL, 'Recreation Center entrance', 1, NOW(), NOW()),
(8, 'Midori & Moleskine Grid Notebook Set + Fineliners', '3 unused dotted/grid notebooks and a set of 6 waterproof archival ink fineliner pens (0.1 to 0.8mm). Perfect for bullet journaling and engineering sketch notes.', 18.00, 'STATIONERY', 'https://images.unsplash.com/photo-1585776245991-cf89dd7fc73a?w=800&auto=format&fit=crop&q=80', 'AVAILABLE', 'New', NULL, NULL, 'University Bookstore Plaza', 2, NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- =============================================
-- 圖書館管理系統 DDL 腳本
-- =============================================

-- 創建數據庫
CREATE DATABASE EsunLibrarySystem;
GO

USE EsunLibrarySystem;
GO

-- 創建用戶表
CREATE TABLE users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    phone_number NVARCHAR(20) NOT NULL UNIQUE,
    password_hash NVARCHAR(255) NOT NULL,
    salt NVARCHAR(255) NOT NULL,
    user_name NVARCHAR(50),
    registration_time DATETIME2 DEFAULT GETDATE(),
    last_login_time DATETIME2
);

-- 創建書籍表
CREATE TABLE books (
    isbn NVARCHAR(13) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    author NVARCHAR(255),
    introduction NVARCHAR(MAX),
    image_url NVARCHAR(500)
);

-- 創建庫存表
CREATE TABLE inventory (
    inventory_id INT IDENTITY(1,1) PRIMARY KEY,
    isbn NVARCHAR(13) NOT NULL,
    status NVARCHAR(50) NOT NULL DEFAULT 'Available',
    store_time DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (isbn) REFERENCES books(isbn)
);

-- 創建借閱記錄表
CREATE TABLE borrowing_records (
    record_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    inventory_id INT NOT NULL,
    borrowing_time DATETIME2 DEFAULT GETDATE(),
    return_time DATETIME2,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (inventory_id) REFERENCES inventory(inventory_id)
);

-- 創建索引
CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_inventory_status ON inventory(status);
CREATE INDEX idx_inventory_isbn ON inventory(isbn);
CREATE INDEX idx_borrowing_user ON borrowing_records(user_id);
CREATE INDEX idx_borrowing_inventory ON borrowing_records(inventory_id);
CREATE INDEX idx_borrowing_return_time ON borrowing_records(return_time);

-- 創建約束
ALTER TABLE inventory ADD CONSTRAINT chk_status 
    CHECK (status IN ('Available', 'Borrowed', 'Processing', 'Lost', 'Damaged', 'Discarded'));

GO 
-- My milestone (update: 2025/08/06)
-- 1. Create the database
-- 2. Create the each table
-- 3. Select each table to check if it was created successfully
-- 4. collect book information, ready to insert
-- 5. insert book information into the books table
-- 6. break down to DDL and DML
-- 7.create stored procedures 

-- for cleaning up the database
drop table borrowing_records;
drop table users;
drop table inventory;
drop table books;

-- for checking tables
USE EsunLibrarySystem;
SELECT * FROM users;
SELECT * FROM books;
SELECT * FROM inventory;
SELECT * FROM borrowing_records;


-- start run the script from here
-- This script creates the EsunLibrarySystem database and its tables, inserts initial data

-- for checking if the database exists
USE master;
GO
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'EsunLibrarySystem')
BEGIN
    DROP DATABASE EsunLibrarySystem;
END
GO

CREATE DATABASE EsunLibrarySystem;
GO

USE EsunLibrarySystem;
GO

-- user table
CREATE TABLE users (
  user_id INT PRIMARY KEY IDENTITY(1,1),
  phone_number VARCHAR(20) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  salt VARCHAR(255) NOT NULL,
  user_name VARCHAR(100),
  registration_time DATETIME DEFAULT GETDATE(),
  last_login_time DATETIME
);

-- book table (must be created before inventory)
CREATE TABLE books (
  isbn NVARCHAR(13) PRIMARY KEY,
  name NVARCHAR(255),
  author NVARCHAR(100),
  introduction NVARCHAR(MAX),
  image_url NVARCHAR(500)
);

-- inventory table
CREATE TABLE inventory (
  inventory_id INT PRIMARY KEY IDENTITY(1,1),
  isbn NVARCHAR(13),
  store_time DATETIME DEFAULT GETDATE(),
  status NVARCHAR(10) NOT NULL DEFAULT N'Available' CHECK (status IN (N'Available', N'Borrowed', N'Processing', N'Lost', N'Damaged', N'Discarded')),
  FOREIGN KEY (isbn) REFERENCES books(isbn)
);

-- borrowing table
CREATE TABLE borrowing_records (
  record_id INT PRIMARY KEY IDENTITY(1,1),
  user_id INT,
  inventory_id INT,
  borrowing_time DATETIME DEFAULT GETDATE(),
  return_time DATETIME,
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (inventory_id) REFERENCES inventory(inventory_id)
);

-- Create indexes for better performance
CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_borrowing_user ON borrowing_records(user_id);
CREATE INDEX idx_borrowing_inventory ON borrowing_records(inventory_id);
CREATE INDEX idx_inventory_status ON inventory(status);

-- insert book information into the books table
INSERT INTO books (isbn, name, author, introduction, image_url) VALUES
('9789863128359', 'AI Artificial Intelligence Introduction', 'Luo Guangzhi', 'This is an introductory book about artificial intelligence, suitable for beginners.', 'https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/b2b/newItem/2025/07/17/172_111600119_629_mainCoverImage1.jpg'),
('9786264250559', 'Cybersecurity Handbook', 'Steve Wilson', 'This is a practical handbook about information security.', 'https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/b2b/newItem/2025/06/24/8113_112533581_874_mainCoverImage1.jpg'),
('9789865022686', 'Java Programming', 'Cai Wenlong', 'This is a basic Java programming textbook.', 'https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/Upload/Product/201910/o/637056885942171250.jpg');

-- insert to inventory table
INSERT INTO inventory (isbn, status) VALUES
('9789863128359', 'Available'),
('9786264250559', 'Available'),
('9789865022686', 'Available');

-- end of creating database and tables
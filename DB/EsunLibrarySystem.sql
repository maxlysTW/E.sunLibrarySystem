CREATE DATABASE EsunLibrarySystem;

USE EsunLibrarySystem;

-- user table
CREATE TABLE users (
  user_id INT PRIMARY KEY,
  phone_number VARCHAR(20) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  salt VARCHAR(255) NOT NULL,
  user_name VARCHAR(100),
  registration_time DATETIME,
  last_login_time DATETIME
);
-- select user table to check if it was created successfully
SELECT * FROM users;

-- inventory table
CREATE TABLE inventory (
  inventory_id INT PRIMARY KEY,
  isbn VARCHAR(13),
  store_time DATETIME,
  status VARCHAR(10) NOT NULL CHECK (status IN ('在庫', '出借中', '整理中(歸還後未入庫)', '遺失', '損毀', '廢棄')),
  FOREIGN KEY (isbn) REFERENCES books(isbn)
);
-- select inventory table to check if it was created successfully
SELECT * FROM inventory;

-- book table
CREATE TABLE books (
  isbn VARCHAR(13) PRIMARY KEY,
  name VARCHAR(255),
  author VARCHAR(100),
  introduction TEXT
);
-- select book table to check if it was created successfully
SELECT * FROM books;

-- borrowing table
CREATE TABLE borrowing_records (
  record_id INT PRIMARY KEY,
  user_id INT,
  inventory_id INT,
  borrowing_time DATETIME,
  return_time DATETIME,
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (inventory_id) REFERENCES inventory(inventory_id)
);
-- select borrowing table to check if it was created successfully
SELECT * FROM borrowing_records;
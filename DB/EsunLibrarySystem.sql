-- My milestone (update: 2025/08/06)
-- 1. Create the database
-- 2. Create the each table
-- 3. Select each table to check if it was created successfully
-- 4. collect book information, ready to insert
-- 5. insert book information into the books table

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
  status NVARCHAR(10) NOT NULL DEFAULT N'在庫' CHECK (status IN (N'在庫', N'出借中', N'整理中(歸還後未入庫)', N'遺失', N'損毀', N'廢棄')),
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
('9789863128359', N'AI 人工智慧入門', N'羅光志', N'這是一本關於人工智慧的入門書籍，適合初學者閱讀。', 'https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/b2b/newItem/2025/07/17/172_111600119_629_mainCoverImage1.jpg'),
('9786264250559', N'資安教戰手冊', N'Steve Wilson', N'這是一本關於資訊安全的實用手冊。', 'https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/b2b/newItem/2025/06/24/8113_112533581_874_mainCoverImage1.jpg'),
('9789865022686', N'Java 程式設計', N'蔡文龍', N'這是一本 Java 程式設計的基礎教材。', 'https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/Upload/Product/201910/o/637056885942171250.jpg');

-- insert to inventory table
INSERT INTO inventory (isbn, status) VALUES
('9789863128359', N'在庫'),
('9786264250559', N'在庫'),
('9789865022686', N'在庫');

-- end of creating database and tables


-- Stored Procedures for business logic
GO

-- 1. User Registration Procedure
CREATE PROCEDURE sp_RegisterUser
    @PhoneNumber VARCHAR(20),
    @PasswordHash VARCHAR(255),
    @Salt VARCHAR(255),
    @UserName VARCHAR(100),
    @UserId INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- Check if phone number already exists
        IF EXISTS (SELECT 1 FROM users WHERE phone_number = @PhoneNumber)
        BEGIN
            RAISERROR ('Phone number already registered', 16, 1);
            RETURN;
        END
        
        -- Insert new user
        INSERT INTO users (phone_number, password_hash, salt, user_name, registration_time)
        VALUES (@PhoneNumber, @PasswordHash, @Salt, @UserName, GETDATE());
        
        SET @UserId = SCOPE_IDENTITY();
        
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;

GO

-- 2. User Login Procedure
CREATE PROCEDURE sp_UserLogin
    @PhoneNumber VARCHAR(20),
    @PasswordHash VARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @UserId INT;
    DECLARE @UserName VARCHAR(100);
    
    -- Verify user credentials
    SELECT @UserId = user_id, @UserName = user_name
    FROM users 
    WHERE phone_number = @PhoneNumber AND password_hash = @PasswordHash;
    
    IF @UserId IS NOT NULL
    BEGIN
        -- Update last login time
        UPDATE users 
        SET last_login_time = GETDATE()
        WHERE user_id = @UserId;
        
        -- Return user info
        SELECT @UserId as user_id, @UserName as user_name, @PhoneNumber as phone_number;
    END
    ELSE
    BEGIN
        RAISERROR ('Invalid credentials', 16, 1);
    END
END;

GO

-- 3. Borrow Book Procedure
CREATE PROCEDURE sp_BorrowBook
    @UserId INT,
    @InventoryId INT,
    @RecordId INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- Check if user exists
        IF NOT EXISTS (SELECT 1 FROM users WHERE user_id = @UserId)
        BEGIN
            RAISERROR ('User not found', 16, 1);
            RETURN;
        END
        
        -- Check if inventory exists and is available
        IF NOT EXISTS (SELECT 1 FROM inventory WHERE inventory_id = @InventoryId AND status = N'在庫')
        BEGIN
            RAISERROR ('Book not available for borrowing', 16, 1);
            RETURN;
        END
        
        -- Check if user already borrowed this book
        IF EXISTS (SELECT 1 FROM borrowing_records WHERE user_id = @UserId AND inventory_id = @InventoryId AND return_time IS NULL)
        BEGIN
            RAISERROR ('User already borrowed this book', 16, 1);
            RETURN;
        END
        
        -- Update inventory status
        UPDATE inventory 
        SET status = N'出借中'
        WHERE inventory_id = @InventoryId;
        
        -- Create borrowing record
        INSERT INTO borrowing_records (user_id, inventory_id, borrowing_time)
        VALUES (@UserId, @InventoryId, GETDATE());
        
        SET @RecordId = SCOPE_IDENTITY();
        
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;

GO

-- 4. Return Book Procedure
CREATE PROCEDURE sp_ReturnBook
    @UserId INT,
    @InventoryId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- Check if borrowing record exists
        IF NOT EXISTS (SELECT 1 FROM borrowing_records WHERE user_id = @UserId AND inventory_id = @InventoryId AND return_time IS NULL)
        BEGIN
            RAISERROR ('No active borrowing record found', 16, 1);
            RETURN;
        END
        
        -- Update borrowing record
        UPDATE borrowing_records 
        SET return_time = GETDATE()
        WHERE user_id = @UserId AND inventory_id = @InventoryId AND return_time IS NULL;
        
        -- Update inventory status
        UPDATE inventory 
        SET status = N'整理中(歸還後未入庫)'
        WHERE inventory_id = @InventoryId;
        
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;

GO

-- 5. Get Available Books Procedure
CREATE PROCEDURE sp_GetAvailableBooks
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        i.inventory_id,
        b.isbn,
        b.name,
        b.author,
        b.introduction,
        b.image_url,
        i.store_time,
        i.status
    FROM inventory i
    INNER JOIN books b ON i.isbn = b.isbn
    WHERE i.status = N'在庫'
    ORDER BY b.name;
END;

GO

-- 6. Get User Borrowing History Procedure
CREATE PROCEDURE sp_GetUserBorrowingHistory
    @UserId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        br.record_id,
        br.borrowing_time,
        br.return_time,
        b.isbn,
        b.name,
        b.author,
        b.image_url,
        i.status
    FROM borrowing_records br
    INNER JOIN inventory i ON br.inventory_id = i.inventory_id
    INNER JOIN books b ON i.isbn = b.isbn
    WHERE br.user_id = @UserId
    ORDER BY br.borrowing_time DESC;
END;

-- select tables to check if they were created successfully
SELECT 'Users table:' as table_name;
SELECT * FROM users;

SELECT 'Books table:' as table_name;
SELECT * FROM books;

SELECT 'Inventory table:' as table_name;
SELECT * FROM inventory;

SELECT 'Borrowing records table:' as table_name;
SELECT * FROM borrowing_records;
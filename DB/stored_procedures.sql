-- start to borrow books
CREATE PROCEDURE sp_BorrowBook
    @UserId INT,
    @InventoryId INT,
    @RecordId INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- check if the book is available
        DECLARE @Status NVARCHAR(50);
        SELECT @Status = status FROM inventory WHERE inventory_id = @InventoryId;
        
        IF @Status != 'Available'
        BEGIN
            RAISERROR ('此書籍目前不可借閱', 16, 1);
            RETURN;
        END
        
        -- check if the user has already borrowed this book
        IF EXISTS (SELECT 1 FROM borrowing_records WHERE user_id = @UserId AND inventory_id = @InventoryId AND return_time IS NULL)
        BEGIN
            RAISERROR ('您已借閱此書', 16, 1);
            RETURN;
        END

        -- update inventory status
        UPDATE inventory SET status = 'Borrowed' WHERE inventory_id = @InventoryId;
        
        -- create borrowing record
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
END
GO

-- ReturnBook
CREATE PROCEDURE sp_ReturnBook
    @UserId INT,
    @InventoryId INT,
    @RecordId INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- check record existence
        SELECT @RecordId = record_id 
        FROM borrowing_records 
        WHERE user_id = @UserId AND inventory_id = @InventoryId AND return_time IS NULL;
        
        IF @RecordId IS NULL
        BEGIN
            RAISERROR ('沒有找到有效的借閱記錄', 16, 1);
            RETURN;
        END

        -- remove the borrowing record
        UPDATE borrowing_records 
        SET return_time = GETDATE() 
        WHERE record_id = @RecordId;

        -- update inventory status
        UPDATE inventory SET status = 'Available' WHERE inventory_id = @InventoryId;
        
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END
GO

-- Get user borrowing history stored procedure
CREATE PROCEDURE sp_GetUserBorrowingHistory
    @UserId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        br.record_id,
        br.user_id,
        br.inventory_id,
        br.borrowing_time,
        br.return_time,
        b.name as book_name,
        b.author as book_author,
        b.isbn as book_isbn,
        CASE 
            WHEN br.return_time IS NULL THEN '借閱中'
            ELSE '已歸還'
        END as status
    FROM borrowing_records br
    INNER JOIN inventory i ON br.inventory_id = i.inventory_id
    INNER JOIN books b ON i.isbn = b.isbn
    WHERE br.user_id = @UserId
    ORDER BY br.borrowing_time DESC;
END
GO 
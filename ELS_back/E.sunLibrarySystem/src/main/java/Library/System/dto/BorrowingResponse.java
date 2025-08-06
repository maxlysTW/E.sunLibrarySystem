package Library.System.dto;

import java.time.LocalDateTime;

public class BorrowingResponse {
    private Integer recordId;
    private Integer userId;
    private Integer inventoryId;
    private LocalDateTime borrowingTime;
    private LocalDateTime returnTime;
    private String userName;
    private String bookName;
    private String bookAuthor;
    private String bookIsbn;
    private String status;

    // Constructors
    public BorrowingResponse() {}

    public BorrowingResponse(Integer recordId, Integer userId, Integer inventoryId, 
                           LocalDateTime borrowingTime, LocalDateTime returnTime,
                           String userName, String bookName, String bookAuthor, 
                           String bookIsbn, String status) {
        this.recordId = recordId;
        this.userId = userId;
        this.inventoryId = inventoryId;
        this.borrowingTime = borrowingTime;
        this.returnTime = returnTime;
        this.userName = userName;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.bookIsbn = bookIsbn;
        this.status = status;
    }

    // Getters and Setters
    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public LocalDateTime getBorrowingTime() {
        return borrowingTime;
    }

    public void setBorrowingTime(LocalDateTime borrowingTime) {
        this.borrowingTime = borrowingTime;
    }

    public LocalDateTime getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(LocalDateTime returnTime) {
        this.returnTime = returnTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

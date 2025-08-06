package Library.System.dto;

import java.time.LocalDateTime;

/**
 * 借還書回應 DTO
 */
public class BorrowingResponse {
    
    private String message;
    private Integer recordId;
    private Integer userId;
    private Integer inventoryId;
    private LocalDateTime borrowingTime;
    private LocalDateTime returnTime;
    private String status;
    
    public BorrowingResponse() {}
    
    public BorrowingResponse(String message) {
        this.message = message;
    }
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}

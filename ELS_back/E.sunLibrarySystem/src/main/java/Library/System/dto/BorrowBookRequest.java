package Library.System.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 借書請求 DTO
 */
public class BorrowBookRequest {
    
    @NotNull(message = "書籍ID不能為空")
    private Integer inventoryId;
    
    public BorrowBookRequest() {}
    
    public BorrowBookRequest(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }
    
    public Integer getInventoryId() {
        return inventoryId;
    }
    
    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }
}

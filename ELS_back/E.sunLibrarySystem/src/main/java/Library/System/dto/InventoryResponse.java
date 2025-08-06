package Library.System.dto;

import java.time.LocalDateTime;

public class InventoryResponse {
    private Integer inventoryId;
    private String isbn;
    private LocalDateTime storeTime;
    private String status;
    private BookResponse book;

    public InventoryResponse() {}

    public InventoryResponse(Integer inventoryId, String isbn, LocalDateTime storeTime, String status, BookResponse book) {
        this.inventoryId = inventoryId;
        this.isbn = isbn;
        this.storeTime = storeTime;
        this.status = status;
        this.book = book;
    }

    // Getters and Setters
    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDateTime getStoreTime() {
        return storeTime;
    }

    public void setStoreTime(LocalDateTime storeTime) {
        this.storeTime = storeTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BookResponse getBook() {
        return book;
    }

    public void setBook(BookResponse book) {
        this.book = book;
    }
} 
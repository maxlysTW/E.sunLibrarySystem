package Library.System.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory")
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Integer inventoryId;
    
    @Column(name = "isbn", length = 13)
    private String isbn;
    
    @Column(name = "store_time")
    private LocalDateTime storeTime = LocalDateTime.now();
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn", insertable = false, updatable = false)
    private Book book;
    
    // Constructors
    public Inventory() {}
    
    public Inventory(String isbn, String status) {
        this.isbn = isbn;
        this.status = status;
        this.storeTime = LocalDateTime.now();
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
    
    public Book getBook() {
        return book;
    }
    
    public void setBook(Book book) {
        this.book = book;
    }
} 
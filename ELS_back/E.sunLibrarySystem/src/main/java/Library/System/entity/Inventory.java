/**
 * 庫存實體類別 - 對應資料庫中的 inventory 表
 * 
 * 此實體類別代表圖書館系統中的圖書庫存管理，包含以下功能：
 * 1. 圖書庫存項目管理（每本實體書的狀態追蹤）
 * 2. 圖書狀態管理（可借閱、已借出、遺失等）
 * 3. 入庫時間記錄
 * 4. 與圖書實體的關聯關係
 * 
 * 資料庫對應：
 * - 表名：inventory
 * - 主鍵：inventory_id (自動遞增)
 * - 外鍵：isbn (關聯到 books 表)
 * 
 * @author E.sun Library System Team
 * @version 1.0
 * @since 2025
 */
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
    
    /** 庫存項目唯一識別碼，主鍵，自動遞增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Integer inventoryId;
    
    /** 圖書ISBN，關聯到圖書資料 */
    @Column(name = "isbn", length = 13)
    private String isbn;
    
    /** 圖書入庫時間 */
    @Column(name = "store_time")
    private LocalDateTime storeTime = LocalDateTime.now();
    
    /** 圖書狀態（available-可借閱、borrowed-已借出、lost-遺失等） */
    @Column(name = "status", nullable = false)
    private String status;
    
    /** 關聯的圖書實體，透過 ISBN 建立關聯 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "isbn", insertable = false, updatable = false)
    private Book book;
    
    // Constructors
    
    /**
     * 預設建構子
     */
    public Inventory() {}
    
    /**
     * 建構子 - 用於建立新庫存項目
     * 
     * @param isbn 圖書ISBN
     * @param status 圖書狀態
     */
    public Inventory(String isbn, String status) {
        this.isbn = isbn;
        this.status = status;
        this.storeTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    
    /**
     * 取得庫存ID
     * @return 庫存項目唯一識別碼
     */
    public Integer getInventoryId() {
        return inventoryId;
    }
    
    /**
     * 設定庫存ID
     * @param inventoryId 庫存項目唯一識別碼
     */
    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }
    
    /**
     * 取得圖書ISBN
     * @return 國際標準書號
     */
    public String getIsbn() {
        return isbn;
    }
    
    /**
     * 設定圖書ISBN
     * @param isbn 國際標準書號
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    /**
     * 取得入庫時間
     * @return 圖書入庫的時間
     */
    public LocalDateTime getStoreTime() {
        return storeTime;
    }
    
    /**
     * 設定入庫時間
     * @param storeTime 圖書入庫的時間
     */
    public void setStoreTime(LocalDateTime storeTime) {
        this.storeTime = storeTime;
    }
    
    /**
     * 取得圖書狀態
     * @return 圖書當前狀態
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * 設定圖書狀態
     * @param status 圖書當前狀態
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * 取得關聯的圖書實體
     * @return 對應的圖書物件
     */
    public Book getBook() {
        return book;
    }
    
    /**
     * 設定關聯的圖書實體
     * @param book 對應的圖書物件
     */
    public void setBook(Book book) {
        this.book = book;
    }
} 
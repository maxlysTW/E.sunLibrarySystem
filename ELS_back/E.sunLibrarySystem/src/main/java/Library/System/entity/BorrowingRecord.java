/**
 * 借閱記錄實體類別 - 對應資料庫中的 borrowing_records 表
 * 
 * 此實體類別代表圖書館系統中的借閱記錄管理，包含以下功能：
 * 1. 借閱行為記錄（借書、還書時間）
 * 2. 使用者與圖書的關聯追蹤
 * 3. 借閱狀態管理（借閱中、已歸還）
 * 4. 與使用者、庫存的關聯關係
 * 
 * 資料庫對應：
 * - 表名：borrowing_records
 * - 主鍵：record_id (自動遞增)
 * - 外鍵：user_id (關聯到 users 表)、inventory_id (關聯到 inventory 表)
 * 
 * @author E.sun Library System Team
 * @version 1.0
 * @since 2025
 */
package Library.System.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "borrowing_records")
public class BorrowingRecord {
    
    /** 借閱記錄唯一識別碼，主鍵，自動遞增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;
    
    /** 借閱者的使用者ID */
    @Column(name = "user_id")
    private Integer userId;
    
    /** 被借閱圖書的庫存ID */
    @Column(name = "inventory_id")
    private Integer inventoryId;
    
    /** 借閱開始時間 */
    @Column(name = "borrowing_time")
    private LocalDateTime borrowingTime;
    
    /** 歸還時間（null表示尚未歸還） */
    @Column(name = "return_time")
    private LocalDateTime returnTime;
    
    /** 關聯的使用者實體，避免在JSON序列化時造成循環引用 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;
    
    /** 關聯的庫存實體，避免在JSON序列化時造成循環引用 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inventory_id", insertable = false, updatable = false)
    @JsonIgnore
    private Inventory inventory;
    
    // Constructors
    
    /**
     * 預設建構子
     */
    public BorrowingRecord() {}
    
    /**
     * 建構子 - 用於建立新借閱記錄
     * 
     * @param userId 借閱者的使用者ID
     * @param inventoryId 被借閱圖書的庫存ID
     */
    public BorrowingRecord(Integer userId, Integer inventoryId) {
        this.userId = userId;
        this.inventoryId = inventoryId;
        this.borrowingTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    
    /**
     * 取得借閱記錄ID
     * @return 借閱記錄唯一識別碼
     */
    public Integer getRecordId() {
        return recordId;
    }
    
    /**
     * 設定借閱記錄ID
     * @param recordId 借閱記錄唯一識別碼
     */
    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }
    
    /**
     * 取得使用者ID
     * @return 借閱者的使用者ID
     */
    public Integer getUserId() {
        return userId;
    }
    
    /**
     * 設定使用者ID
     * @param userId 借閱者的使用者ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    /**
     * 取得庫存ID
     * @return 被借閱圖書的庫存ID
     */
    public Integer getInventoryId() {
        return inventoryId;
    }
    
    /**
     * 設定庫存ID
     * @param inventoryId 被借閱圖書的庫存ID
     */
    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }
    
    /**
     * 取得借閱時間
     * @return 借閱開始的時間
     */
    public LocalDateTime getBorrowingTime() {
        return borrowingTime;
    }
    
    /**
     * 設定借閱時間
     * @param borrowingTime 借閱開始的時間
     */
    public void setBorrowingTime(LocalDateTime borrowingTime) {
        this.borrowingTime = borrowingTime;
    }
    
    /**
     * 取得歸還時間
     * @return 歸還時間（null表示尚未歸還）
     */
    public LocalDateTime getReturnTime() {
        return returnTime;
    }
    
    /**
     * 設定歸還時間
     * @param returnTime 歸還時間
     */
    public void setReturnTime(LocalDateTime returnTime) {
        this.returnTime = returnTime;
    }
    
    /**
     * 取得關聯的使用者實體
     * @return 借閱者物件
     */
    public User getUser() {
        return user;
    }
    
    /**
     * 設定關聯的使用者實體
     * @param user 借閱者物件
     */
    public void setUser(User user) {
        this.user = user;
    }
    
    /**
     * 取得關聯的庫存實體
     * @return 被借閱的庫存項目物件
     */
    public Inventory getInventory() {
        return inventory;
    }
    
    /**
     * 設定關聯的庫存實體
     * @param inventory 被借閱的庫存項目物件
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
} 
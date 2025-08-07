/**
 * 使用者實體類別 - 對應資料庫中的 users 表
 * 
 * 此實體類別代表圖書館系統中的使用者，包含以下功能：
 * 1. 使用者基本資料管理（使用者名稱、手機號碼）
 * 2. 密碼安全管理（密碼雜湊、鹽值）
 * 3. 使用者行為追蹤（註冊時間、最後登入時間）
 * 
 * 資料庫對應：
 * - 表名：users
 * - 主鍵：user_id (自動遞增)
 * - 唯一約束：phone_number (手機號碼)
 * 
 * @author E.sun Library System Team
 * @version 1.0
 * @since 2025
 */
package Library.System.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    
    /** 使用者唯一識別碼，主鍵，自動遞增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    
    /** 手機號碼，作為登入帳號使用，具有唯一性約束 */
    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;
    
    /** 密碼雜湊值，使用 BCrypt 加密儲存 */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    /** 密碼加密用的鹽值，提升密碼安全性 */
    @Column(name = "salt", nullable = false)
    private String salt;
    
    /** 使用者顯示名稱，支援中文 */
    @Column(name = "user_name", columnDefinition = "NVARCHAR(50)")
    private String userName;
    
    /** 使用者註冊時間 */
    @Column(name = "registration_time")
    private LocalDateTime registrationTime;
    
    /** 使用者最後登入時間 */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    
    // Constructors
    
    /**
     * 預設建構子
     */
    public User() {}
    
    /**
     * 建構子 - 用於建立新使用者
     * 
     * @param phoneNumber 手機號碼
     * @param passwordHash 密碼雜湊值
     * @param salt 鹽值
     * @param userName 使用者名稱
     */
    public User(String phoneNumber, String passwordHash, String salt, String userName) {
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.userName = userName;
        this.registrationTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    
    /**
     * 取得使用者ID
     * @return 使用者唯一識別碼
     */
    public Integer getUserId() {
        return userId;
    }
    
    /**
     * 設定使用者ID
     * @param userId 使用者唯一識別碼
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    /**
     * 取得手機號碼
     * @return 手機號碼
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    /**
     * 設定手機號碼
     * @param phoneNumber 手機號碼
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    /**
     * 取得密碼雜湊值
     * @return 加密後的密碼
     */
    public String getPasswordHash() {
        return passwordHash;
    }
    
    /**
     * 設定密碼雜湊值
     * @param passwordHash 加密後的密碼
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    /**
     * 取得鹽值
     * @return 密碼加密用鹽值
     */
    public String getSalt() {
        return salt;
    }
    
    /**
     * 設定鹽值
     * @param salt 密碼加密用鹽值
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }
    
    /**
     * 取得使用者名稱
     * @return 使用者顯示名稱
     */
    public String getUserName() {
        return userName;
    }
    
    /**
     * 設定使用者名稱
     * @param userName 使用者顯示名稱
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    /**
     * 取得註冊時間
     * @return 使用者註冊的時間
     */
    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }
    
    /**
     * 設定註冊時間
     * @param registrationTime 使用者註冊的時間
     */
    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }
    
    /**
     * 取得最後登入時間
     * @return 使用者最後登入的時間
     */
    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }
    
    /**
     * 設定最後登入時間
     * @param lastLoginTime 使用者最後登入的時間
     */
    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
} 
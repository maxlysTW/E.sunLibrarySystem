package Library.System.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(name = "salt", nullable = false)
    private String salt;
    
    @Column(name = "user_name")
    private String userName;
    
    @Column(name = "registration_time")
    private LocalDateTime registrationTime;
    
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    
    // Constructors
    public User() {}
    
    public User(String phoneNumber, String passwordHash, String salt, String userName) {
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.userName = userName;
        this.registrationTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getSalt() {
        return salt;
    }
    
    public void setSalt(String salt) {
        this.salt = salt;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }
    
    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }
    
    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }
    
    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
} 
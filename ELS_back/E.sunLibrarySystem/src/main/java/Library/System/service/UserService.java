/**
 * 使用者服務類別 - 處理使用者相關的業務邏輯
 * 
 * 此服務類別負責管理使用者的核心業務功能，包含以下主要功能：
 * 1. 使用者註冊 - 驗證資料並建立新使用者帳號
 * 2. 使用者登入 - 身份驗證與密碼驗證
 * 3. 使用者查詢 - 根據ID或手機號碼查詢使用者資料
 * 4. 資料驗證 - 確保輸入資料的正確性和安全性
 * 5. 密碼安全 - 使用鹽值和雜湊演算法保護使用者密碼
 * 
 * 安全特性：
 * - 密碼使用 BCrypt 加密儲存
 * - 支援鹽值增強密碼安全性
 * - 輸入參數嚴格驗證
 * - 完整的錯誤處理和日誌記錄
 * 
 * @author MaxLin
 * @version 1.0
 * @since 2025/08/07
 */
package Library.System.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Library.System.common.PasswordUtil;
import Library.System.entity.User;
import Library.System.repository.UserRepository;

@Service
@Transactional
public class UserService {
    
    /** 日誌記錄器，用於記錄服務運行過程中的資訊 */
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    /** 使用者資料存取物件，負責與資料庫互動 */
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 使用者註冊服務
     * 
     * 執行完整的使用者註冊流程，包含資料驗證、重複性檢查、密碼加密等
     * 
     * @param phoneNumber 手機號碼，作為使用者的登入帳號
     * @param password 使用者密碼，將進行加密儲存
     * @param userName 使用者顯示名稱
     * @return User 新建立的使用者實體
     * @throws IllegalArgumentException 當輸入參數不符合要求時
     * @throws RuntimeException 當手機號碼已存在或其他系統錯誤時
     */
    public User registerUser(String phoneNumber, String password, String userName) {
        logger.info("Attempting to register user with phone number: {}", phoneNumber);
        
        // 驗證輸入參數的完整性
        validateRegistrationInput(phoneNumber, password, userName);
        
        // 檢查手機號碼是否已存在
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            logger.warn("Registration failed: Phone number already exists: {}", phoneNumber);
            throw new RuntimeException("手機號碼已註冊");
        }
        
        try {
            // 生成安全的鹽值和密碼雜湊
            String salt = PasswordUtil.generateSalt();
            String passwordHash = PasswordUtil.hashPassword(password, salt);
            
            // 建立並儲存新使用者
            User user = new User(phoneNumber, passwordHash, salt, userName);
            User savedUser = userRepository.save(user);
            
            logger.info("User registered successfully: {}", savedUser.getUserId());
            return savedUser;
        } catch (Exception e) {
            logger.error("Error during user registration: {}", e.getMessage(), e);
            throw new RuntimeException("註冊失敗，請稍後再試", e);
        }
    }
    
    /**
     * 使用者登入服務
     * 
     * 驗證使用者身份並更新最後登入時間
     * 
     * @param phoneNumber 手機號碼
     * @param password 使用者密碼
     * @return User 登入成功的使用者實體
     * @throws IllegalArgumentException 當輸入參數為空時
     * @throws RuntimeException 當使用者不存在或密碼錯誤時
     */
    public User loginUser(String phoneNumber, String password) {
        logger.info("Attempting login for phone number: {}", phoneNumber);
        
        // 驗證輸入參數
        validateLoginInput(phoneNumber, password);
        
        try {
            // 查詢使用者是否存在
            Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
            if (userOpt.isEmpty()) {
                logger.warn("Login failed: User not found for phone number: {}", phoneNumber);
                throw new RuntimeException("使用者不存在");
            }
            
            User user = userOpt.get();
            
            // 驗證密碼正確性
            if (!PasswordUtil.verifyPassword(password, user.getSalt(), user.getPasswordHash())) {
                logger.warn("Login failed: Invalid password for phone number: {}", phoneNumber);
                throw new RuntimeException("密碼錯誤");
            }
            
            // 更新最後登入時間並儲存
            user.setLastLoginTime(LocalDateTime.now());
            User updatedUser = userRepository.save(user);
            
            logger.info("User login successful: {}", updatedUser.getUserId());
            return updatedUser;
        } catch (RuntimeException e) {
            throw e; // 重新拋出業務邏輯異常
        } catch (Exception e) {
            logger.error("Error during user login: {}", e.getMessage(), e);
            throw new RuntimeException("登入失敗，請稍後再試", e);
        }
    }
    
    /**
     * 根據使用者ID查詢使用者
     * 
     * @param userId 使用者唯一識別碼
     * @return Optional<User> 可能包含使用者實體的容器
     * @throws IllegalArgumentException 當使用者ID為空時
     */
    public Optional<User> findById(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("使用者ID不能為空");
        }
        return userRepository.findById(userId);
    }
    
    /**
     * 根據手機號碼查詢使用者
     * 
     * @param phoneNumber 手機號碼
     * @return Optional<User> 可能包含使用者實體的容器
     * @throws IllegalArgumentException 當手機號碼為空時
     */
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("手機號碼不能為空");
        }
        return userRepository.findByPhoneNumber(phoneNumber);
    }
    
    /**
     * 檢查指定手機號碼是否已被註冊
     * 
     * @param phoneNumber 要檢查的手機號碼
     * @return boolean true表示已存在，false表示可用
     */
    public boolean existsByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
    
    /**
     * 驗證註冊輸入參數的私有方法
     * 
     * @param phoneNumber 手機號碼
     * @param password 密碼
     * @param userName 使用者名稱
     * @throws IllegalArgumentException 當任何參數不符合要求時
     */
    private void validateRegistrationInput(String phoneNumber, String password, String userName) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("手機號碼不能為空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密碼不能為空");
        }
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("使用者名稱不能為空");
        }
        
        // 檢查手機號碼格式（台灣手機號碼格式）
        if (!phoneNumber.matches("^09\\d{8}$")) {
            throw new IllegalArgumentException("手機號碼格式不正確");
        }
        
        // 檢查密碼長度
        if (password.length() < 6) {
            throw new IllegalArgumentException("密碼長度至少6位");
        }
        
        // 檢查使用者名稱長度
        if (userName.length() < 2 || userName.length() > 20) {
            throw new IllegalArgumentException("使用者名稱長度應在2-20個字元之間");
        }
    }
    
    /**
     * 驗證登入輸入參數的私有方法
     * 
     * @param phoneNumber 手機號碼
     * @param password 密碼
     * @throws IllegalArgumentException 當任何參數為空時
     */
    private void validateLoginInput(String phoneNumber, String password) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("手機號碼不能為空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密碼不能為空");
        }
    }
} 
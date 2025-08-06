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
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 使用者註冊
     */
    public User registerUser(String phoneNumber, String password, String userName) {
        logger.info("Attempting to register user with phone number: {}", phoneNumber);
        
        // 驗證輸入參數
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("手機號碼不能為空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密碼不能為空");
        }
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("使用者名稱不能為空");
        }
        
        // 檢查手機號碼格式
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
        
        // 檢查手機號碼是否已存在
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            logger.warn("Registration failed: Phone number already exists: {}", phoneNumber);
            throw new RuntimeException("手機號碼已註冊");
        }
        
        try {
            // 生成鹽值和密碼雜湊
            String salt = PasswordUtil.generateSalt();
            String passwordHash = PasswordUtil.hashPassword(password, salt);
            
            // 建立新使用者
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
     * 使用者登入
     */
    public User loginUser(String phoneNumber, String password) {
        logger.info("Attempting login for phone number: {}", phoneNumber);
        
        // 驗證輸入參數
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("手機號碼不能為空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密碼不能為空");
        }
        
        try {
            // 查詢使用者
            Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
            if (userOpt.isEmpty()) {
                logger.warn("Login failed: User not found for phone number: {}", phoneNumber);
                throw new RuntimeException("使用者不存在");
            }
            
            User user = userOpt.get();
            
            // 驗證密碼
            if (!PasswordUtil.verifyPassword(password, user.getSalt(), user.getPasswordHash())) {
                logger.warn("Login failed: Invalid password for phone number: {}", phoneNumber);
                throw new RuntimeException("密碼錯誤");
            }
            
            // 更新最後登入時間
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
     * 根據 ID 查詢使用者
     */
    public Optional<User> findById(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("使用者ID不能為空");
        }
        return userRepository.findById(userId);
    }
    
    /**
     * 根據手機號碼查詢使用者
     */
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("手機號碼不能為空");
        }
        return userRepository.findByPhoneNumber(phoneNumber);
    }
    
    /**
     * 檢查手機號碼是否已存在
     */
    public boolean existsByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
} 
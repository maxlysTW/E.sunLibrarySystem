package Library.System.service;

import Library.System.common.PasswordUtil;
import Library.System.entity.User;
import Library.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 使用者註冊
     */
    public User registerUser(String phoneNumber, String password, String userName) {
        // 檢查手機號碼是否已存在
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new RuntimeException("手機號碼已註冊");
        }
        
        // 生成鹽值和密碼雜湊
        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hashPassword(password, salt);
        
        // 建立新使用者
        User user = new User(phoneNumber, passwordHash, salt, userName);
        return userRepository.save(user);
    }
    
    /**
     * 使用者登入
     */
    public User loginUser(String phoneNumber, String password) {
        // 查詢使用者
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("使用者不存在");
        }
        
        User user = userOpt.get();
        
        // 驗證密碼
        if (!PasswordUtil.verifyPassword(password, user.getSalt(), user.getPasswordHash())) {
            throw new RuntimeException("密碼錯誤");
        }
        
        // 更新最後登入時間
        user.setLastLoginTime(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    /**
     * 根據 ID 查詢使用者
     */
    public Optional<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }
    
    /**
     * 根據手機號碼查詢使用者
     */
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }
} 
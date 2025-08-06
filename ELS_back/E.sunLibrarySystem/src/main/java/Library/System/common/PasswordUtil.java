package Library.System.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    
    private static final SecureRandom RANDOM = new SecureRandom();
    
    /**
     * 生成隨機鹽值
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * 使用 SHA-256 雜湊密碼
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    /**
     * 驗證密碼
     */
    public static boolean verifyPassword(String password, String salt, String hashedPassword) {
        String newHash = hashPassword(password, salt);
        return newHash.equals(hashedPassword);
    }
} 
/**
 * 密碼工具類別 - 提供密碼加密和驗證相關的安全功能
 * 
 * 此工具類別實作了安全的密碼處理機制，包含以下功能：
 * 1. 隨機鹽值生成 - 提升密碼安全性，防範彩虹表攻擊
 * 2. 密碼雜湊加密 - 使用 SHA-256 演算法進行不可逆加密
 * 3. 密碼驗證 - 比對輸入密碼與儲存的雜湊值
 * 
 * 安全特性：
 * - 使用 SecureRandom 產生高品質的隨機鹽值
 * - 採用 SHA-256 雜湊演算法確保密碼安全
 * - 支援鹽值機制防止相同密碼產生相同雜湊
 * - Base64 編碼確保資料儲存和傳輸的正確性
 * 
 * 注意事項：
 * - 所有方法都是靜態方法，可直接調用
 * - 密碼加密過程不可逆，請妥善保存鹽值
 * - 建議定期更新密碼以維持帳號安全
 * 
 * @author E.sun Library System Team
 * @version 1.0
 * @since 2025
 */
package Library.System.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    
    /** 安全隨機數產生器，用於生成高品質的隨機鹽值 */
    private static final SecureRandom RANDOM = new SecureRandom();
    
    /** 鹽值長度（位元組數） */
    private static final int SALT_LENGTH = 16;
    
    /** 雜湊演算法名稱 */
    private static final String HASH_ALGORITHM = "SHA-256";
    
    /**
     * 生成隨機鹽值
     * 
     * 使用 SecureRandom 產生指定長度的隨機位元組陣列，
     * 然後使用 Base64 編碼轉換為字串格式以便儲存
     * 
     * @return String Base64 編碼的隨機鹽值
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * 使用 SHA-256 演算法雜湊密碼
     * 
     * 結合密碼和鹽值進行雜湊運算，產生不可逆的密碼雜湊值。
     * 這個過程可以有效防止相同密碼產生相同雜湊值的問題。
     * 
     * @param password 原始密碼字串
     * @param salt Base64 編碼的鹽值
     * @return String Base64 編碼的密碼雜湊值
     * @throws RuntimeException 當 SHA-256 演算法不可用時
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            // 先加入鹽值，再加入密碼進行雜湊
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    /**
     * 驗證密碼正確性
     * 
     * 使用相同的鹽值和雜湊演算法處理輸入的密碼，
     * 然後與儲存的雜湊值進行比對以驗證密碼是否正確。
     * 
     * @param password 待驗證的原始密碼
     * @param salt 該密碼對應的鹽值
     * @param hashedPassword 儲存在資料庫中的密碼雜湊值
     * @return boolean true表示密碼正確，false表示密碼錯誤
     */
    public static boolean verifyPassword(String password, String salt, String hashedPassword) {
        String newHash = hashPassword(password, salt);
        return newHash.equals(hashedPassword);
    }
} 
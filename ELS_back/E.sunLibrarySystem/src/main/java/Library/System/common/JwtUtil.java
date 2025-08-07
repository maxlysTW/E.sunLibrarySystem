/**
 * JWT 工具類別 - 提供 JSON Web Token 的生成、驗證和解析功能
 * 
 * 此工具類別負責處理使用者身份認證的 Token 管理，包含以下功能：
 * 1. Token 生成 - 為已驗證使用者創建 JWT Token
 * 2. Token 驗證 - 檢查 Token 的有效性和完整性
 * 3. 資料提取 - 從 Token 中提取使用者資訊
 * 4. 過期檢查 - 監控 Token 的過期狀態
 * 
 * 安全特性：
 * - 使用 HMAC-SHA 演算法進行數位簽章
 * - 支援 Token 過期時間設定
 * - 完整的異常處理機制
 * - 詳細的日誌記錄
 * 
 * 配置參數：
 * - jwt.secret: JWT 簽章密鑰（需在 application.properties 中設定）
 * - jwt.expiration: Token 過期時間（毫秒）
 * 
 * @author E.sun Library System Team
 * @version 1.0
 * @since 2025
 */
package Library.System.common;

import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    
    /** 日誌記錄器 */
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    /** Token 即將過期的時間閾值（1小時，單位：毫秒） */
    private static final long EXPIRING_SOON_THRESHOLD = 3600000L;
    
    /** JWT 簽章密鑰，從配置文件注入 */
    @Value("${jwt.secret}")
    private String secretKey;
    
    /** JWT Token 過期時間，從配置文件注入 */
    @Value("${jwt.expiration}")
    private long expirationTime;
    
    /** 加密用的密鑰物件，延遲初始化 */
    private SecretKey key;
    
    /**
     * 取得或初始化簽章密鑰
     * 
     * 使用懶加載方式初始化密鑰，確保配置參數已正確注入
     * 
     * @return SecretKey 用於 JWT 簽章的密鑰
     */
    private SecretKey getKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(secretKey.getBytes());
        }
        return key;
    }
    
    /**
     * 生成 JWT Token
     * 
     * 為指定的使用者創建包含使用者資訊和過期時間的 JWT Token
     * 
     * @param userId 使用者唯一識別碼
     * @param phoneNumber 使用者手機號碼
     * @return String 生成的 JWT Token 字串
     * @throws RuntimeException 當 Token 生成失敗時
     */
    public String generateToken(Integer userId, String phoneNumber) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expirationTime);
            
            String token = Jwts.builder()
                    .setSubject(String.valueOf(userId))      // 設定使用者ID為主體
                    .claim("phoneNumber", phoneNumber)       // 添加手機號碼聲明
                    .setIssuedAt(now)                       // 設定發行時間
                    .setExpiration(expiryDate)              // 設定過期時間
                    .signWith(getKey())                     // 使用密鑰簽章
                    .compact();                             // 生成最終 Token
            
            logger.debug("Generated JWT token for user: {}", userId);
            return token;
        } catch (Exception e) {
            logger.error("Error generating JWT token for user: {}", userId, e);
            throw new RuntimeException("Token generation failed", e);
        }
    }
    
    /**
     * 驗證 JWT Token 的有效性
     * 
     * 檢查 Token 的簽章、格式和過期狀態
     * 
     * @param token 待驗證的 JWT Token
     * @return boolean true表示Token有效，false表示無效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);
            logger.debug("Token validation successful");
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            logger.warn("Unsupported JWT token: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            logger.warn("Malformed JWT token: {}", e.getMessage());
            return false;
        } catch (SecurityException e) {
            logger.warn("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.warn("JWT token is empty: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during token validation: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 從 Token 中提取使用者ID
     * 
     * 解析 JWT Token 並提取其中的使用者識別碼
     * 
     * @param token JWT Token 字串
     * @return Integer 使用者唯一識別碼
     * @throws RuntimeException 當 Token 無效或解析失敗時
     */
    public Integer getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return Integer.parseInt(claims.getSubject());
        } catch (Exception e) {
            logger.error("Error extracting user ID from token", e);
            throw new RuntimeException("Invalid token", e);
        }
    }
    
    /**
     * 從 Token 中提取手機號碼
     * 
     * 解析 JWT Token 並提取其中的手機號碼聲明
     * 
     * @param token JWT Token 字串
     * @return String 使用者手機號碼
     * @throws RuntimeException 當 Token 無效或解析失敗時
     */
    public String getPhoneNumberFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.get("phoneNumber", String.class);
        } catch (Exception e) {
            logger.error("Error extracting phone number from token", e);
            throw new RuntimeException("Invalid token", e);
        }
    }
    
    /**
     * 檢查 Token 是否即將過期
     * 
     * 判斷 Token 的剩餘有效時間是否少於指定閾值，
     * 用於提醒前端或系統進行 Token 刷新
     * 
     * @param token JWT Token 字串
     * @return boolean true表示即將過期，false表示仍有充足時間
     */
    public boolean isTokenExpiringSoon(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            Date expiration = claims.getExpiration();
            Date now = new Date();
            long timeUntilExpiration = expiration.getTime() - now.getTime();
            
            // 剩餘時間少於閾值時認為即將過期
            return timeUntilExpiration < EXPIRING_SOON_THRESHOLD;
        } catch (Exception e) {
            logger.error("Error checking token expiration", e);
            return false;
        }
    }
} 
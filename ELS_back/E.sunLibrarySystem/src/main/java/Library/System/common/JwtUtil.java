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
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration}")
    private long expirationTime;
    
    private SecretKey key;
    
    /**
     * 初始化密鑰
     */
    private SecretKey getKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(secretKey.getBytes());
        }
        return key;
    }
    
    /**
     * 生成 JWT Token
     */
    public String generateToken(Integer userId, String phoneNumber) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expirationTime);
            
            String token = Jwts.builder()
                    .setSubject(String.valueOf(userId))
                    .claim("phoneNumber", phoneNumber)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(getKey())
                    .compact();
            
            logger.debug("Generated JWT token for user: {}", userId);
            return token;
        } catch (Exception e) {
            logger.error("Error generating JWT token for user: {}", userId, e);
            throw new RuntimeException("Token generation failed", e);
        }
    }
    
    /**
     * 驗證 JWT Token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);
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
        }
    }
    
    /**
     * 從 Token 中取得使用者 ID
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
     * 從 Token 中取得手機號碼
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
            
            // 如果剩餘時間少於1小時，認為即將過期
            return timeUntilExpiration < 3600000;
        } catch (Exception e) {
            logger.error("Error checking token expiration", e);
            return false;
        }
    }
} 
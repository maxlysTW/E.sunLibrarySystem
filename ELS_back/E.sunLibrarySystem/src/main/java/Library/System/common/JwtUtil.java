package Library.System.common;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    
    private static final String SECRET_KEY = "your-secret-key-must-be-at-least-256-bits-long";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours
    
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    
    /**
     * 生成 JWT Token
     */
    public String generateToken(Integer userId, String phoneNumber) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("phoneNumber", phoneNumber)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }
    
    /**
     * 驗證 JWT Token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * 從 Token 中取得使用者 ID
     */
    public Integer getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return Integer.parseInt(claims.getSubject());
    }
    
    /**
     * 從 Token 中取得手機號碼
     */
    public String getPhoneNumberFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.get("phoneNumber", String.class);
    }
} 
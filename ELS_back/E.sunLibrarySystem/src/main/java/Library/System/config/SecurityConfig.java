/**
 * Spring Security 安全配置類別 - 配置系統的安全性設定
 * 
 * 此配置類別負責設定 Spring Security 的安全策略，包含以下功能：
 * 1. CORS 跨域請求處理 - 允許前端應用程式跨域存取
 * 2. CSRF 保護設定 - 針對 REST API 進行適當的 CSRF 配置
 * 3. 會話管理策略 - 配置為無狀態模式以支援 JWT Token
 * 4. 端點存取權限 - 定義各 API 端點的存取權限
 * 5. 身份驗證方式 - 禁用傳統的表單登入和 HTTP Basic 認證
 * 
 * 安全策略：
 * - 無狀態會話：適合 JWT Token 認證方式
 * - 開放端點：認證相關和部分業務 API 可公開存取
 * - CORS 支援：支援前端跨域請求
 * - 禁用不必要的安全功能：簡化 REST API 的安全配置
 * 
 * 端點權限配置：
 * - /api/auth/** - 公開存取（註冊、登入）
 * - /api/books/** - 公開存取（圖書查詢）
 * - /api/borrowing/** - 公開存取（借閱功能，實際權限在業務邏輯中控制）
 * - 其他端點 - 需要身份驗證
 * 
 * @author E.sun Library System Team
 * @version 1.0
 * @since 2025
 */
package Library.System.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置 Spring Security 的過濾器鏈
     * 
     * 定義整個應用程式的安全策略，包括 CORS、CSRF、會話管理、
     * 端點存取權限等各方面的安全設定。
     * 
     * @param http HttpSecurity 物件，用於配置 HTTP 安全設定
     * @param corsConfigurationSource CORS 配置源，定義跨域請求規則
     * @return SecurityFilterChain 配置完成的安全過濾器鏈
     * @throws Exception 當配置過程發生錯誤時拋出異常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
            // 啟用 CORS 跨域請求支援
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            
            // 禁用 CSRF 保護，因為 REST API 使用 Token 認證，不需要 CSRF 保護
            .csrf(csrf -> csrf.disable())
            
            // 配置會話管理為無狀態模式，適合 JWT Token 認證方式
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 配置各端點的存取權限
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()     // 認證相關端點：允許所有人存取
                .requestMatchers("/api/books/**").permitAll()    // 圖書查詢端點：允許所有人存取
                .requestMatchers("/api/borrowing/**").permitAll() // 借閱相關端點：允許所有人存取（業務邏輯中會檢查 JWT）
                .anyRequest().authenticated()                    // 其他所有端點：需要身份驗證
            )
            
            // 禁用表單登入，因為我們使用 JWT Token 進行身份驗證
            .formLogin(form -> form.disable())
            
            // 禁用 HTTP Basic 身份驗證，使用更安全的 JWT Token 方式
            .httpBasic(basic -> basic.disable());
        
        return http.build();
    }
}

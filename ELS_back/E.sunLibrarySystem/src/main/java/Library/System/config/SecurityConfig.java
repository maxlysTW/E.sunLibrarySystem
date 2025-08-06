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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
            // 配置 CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            
            // 禁用 CSRF，因為我們使用的是 REST API
            .csrf(csrf -> csrf.disable())
            
            // 配置 session 管理（無狀態，適用於 JWT）
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 配置授權規則
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()  // 允許身份驗證端點
                .requestMatchers("/api/books/**").permitAll() // 允許書籍相關端點
                .requestMatchers("/api/borrowing/**").permitAll() // 允許借閱相關端點
                .anyRequest().authenticated()  // 其他請求需要身份驗證
            )
            
            // 禁用表單登入（我們使用 JWT）
            .formLogin(form -> form.disable())
            
            // 禁用 HTTP Basic 身份驗證
            .httpBasic(basic -> basic.disable());
        
        return http.build();
    }
}

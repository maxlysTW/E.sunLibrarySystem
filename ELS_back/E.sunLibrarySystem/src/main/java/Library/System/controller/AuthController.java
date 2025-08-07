/**
 * 認證控制器 - 處理使用者註冊與登入相關的 REST API 請求
 * 
 * 此控制器負責管理使用者的認證功能，包含以下主要功能：
 * 1. 使用者註冊 - 建立新的使用者帳號
 * 2. 使用者登入 - 驗證身份並產生 JWT Token
 * 3. 輸入資料驗證 - 確保請求資料的正確性
 * 4. 錯誤處理 - 提供友善的錯誤訊息
 * 
 * API端點：
 * - POST /api/auth/register - 使用者註冊
 * - POST /api/auth/login - 使用者登入
 * 
 * @author E.sun Library System Team
 * @version 1.0
 * @since 2025
 */
package Library.System.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Library.System.common.JwtUtil;
import Library.System.dto.ApiResponse;
import Library.System.dto.LoginRequest;
import Library.System.dto.UserRegistrationRequest;
import Library.System.entity.User;
import Library.System.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    
    /** 使用者服務，處理使用者相關的業務邏輯 */
    @Autowired
    private UserService userService;
    
    /** JWT 工具類，負責 Token 的生成和驗證 */
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 使用者註冊 API
     * 
     * 接收使用者註冊資訊並建立新帳號
     * 
     * @param request 使用者註冊請求物件，包含手機號碼、密碼、使用者名稱
     * @return ResponseEntity 包含註冊結果的 API 回應
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            // 呼叫使用者服務進行註冊
            User user = userService.registerUser(
                request.getPhoneNumber(), 
                request.getPassword(), 
                request.getUserName()
            );
            
            // 建構回應資料
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", user.getUserId());
            userData.put("userName", user.getUserName());
            userData.put("phoneNumber", user.getPhoneNumber());
            userData.put("registrationTime", user.getRegistrationTime());
            
            return ResponseEntity.ok(ApiResponse.success("註冊成功", userData));
        } catch (Exception e) {
            // 處理註冊失敗的情況
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "REGISTRATION_ERROR"));
        }
    }
    
    /**
     * 使用者登入 API
     * 
     * 驗證使用者身份並產生 JWT Token 用於後續的 API 呼叫認證
     * 
     * @param request 登入請求物件，包含手機號碼和密碼
     * @return ResponseEntity 包含登入結果和 JWT Token 的 API 回應
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest request) {
        try {
            // 驗證使用者身份
            User user = userService.loginUser(request.getPhoneNumber(), request.getPassword());
            
            // 生成 JWT Token
            String token = jwtUtil.generateToken(user.getUserId(), user.getPhoneNumber());
            
            // 建構登入回應資料
            Map<String, Object> loginData = new HashMap<>();
            loginData.put("token", token);
            loginData.put("userId", user.getUserId());
            loginData.put("userName", user.getUserName());
            loginData.put("phoneNumber", user.getPhoneNumber());
            loginData.put("lastLoginTime", user.getLastLoginTime());
            
            return ResponseEntity.ok(ApiResponse.success("登入成功", loginData));
        } catch (Exception e) {
            // 處理登入失敗的情況
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "LOGIN_ERROR"));
        }
    }
} 
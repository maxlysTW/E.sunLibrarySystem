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
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 使用者註冊
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            User user = userService.registerUser(
                request.getPhoneNumber(), 
                request.getPassword(), 
                request.getUserName()
            );
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", user.getUserId());
            userData.put("userName", user.getUserName());
            userData.put("phoneNumber", user.getPhoneNumber());
            userData.put("registrationTime", user.getRegistrationTime());
            
            return ResponseEntity.ok(ApiResponse.success("註冊成功", userData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "REGISTRATION_ERROR"));
        }
    }
    
    /**
     * 使用者登入
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.loginUser(request.getPhoneNumber(), request.getPassword());
            
            // 生成 JWT Token
            String token = jwtUtil.generateToken(user.getUserId(), user.getPhoneNumber());
            
            Map<String, Object> loginData = new HashMap<>();
            loginData.put("token", token);
            loginData.put("userId", user.getUserId());
            loginData.put("userName", user.getUserName());
            loginData.put("phoneNumber", user.getPhoneNumber());
            loginData.put("lastLoginTime", user.getLastLoginTime());
            
            return ResponseEntity.ok(ApiResponse.success("登入成功", loginData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "LOGIN_ERROR"));
        }
    }
} 
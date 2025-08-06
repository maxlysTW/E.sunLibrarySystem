package Library.System.controller;

import Library.System.common.JwtUtil;
import Library.System.entity.User;
import Library.System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 使用者註冊
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String password = request.get("password");
            String userName = request.get("userName");
            
            if (phoneNumber == null || password == null || userName == null) {
                return ResponseEntity.badRequest().body("所有欄位都必須填寫");
            }
            
            User user = userService.registerUser(phoneNumber, password, userName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "註冊成功");
            response.put("userId", user.getUserId());
            response.put("userName", user.getUserName());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 使用者登入
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String password = request.get("password");
            
            if (phoneNumber == null || password == null) {
                return ResponseEntity.badRequest().body("手機號碼和密碼都必須填寫");
            }
            
            User user = userService.loginUser(phoneNumber, password);
            
            // 生成 JWT Token
            String token = jwtUtil.generateToken(user.getUserId(), user.getPhoneNumber());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "登入成功");
            response.put("token", token);
            response.put("userId", user.getUserId());
            response.put("userName", user.getUserName());
            response.put("phoneNumber", user.getPhoneNumber());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 
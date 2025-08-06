package Library.System.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LoginRequest {
    
    @NotBlank(message = "手機號碼不能為空")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    private String phoneNumber;
    
    @NotBlank(message = "密碼不能為空")
    private String password;
    
    // Constructors
    public LoginRequest() {}
    
    public LoginRequest(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
    
    // Getters and Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
} 
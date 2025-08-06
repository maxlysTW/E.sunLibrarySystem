package Library.System.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserRegistrationRequest {
    
    @NotBlank(message = "手機號碼不能為空")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    private String phoneNumber;
    
    @NotBlank(message = "密碼不能為空")
    private String password;
    
    @NotBlank(message = "使用者名稱不能為空")
    private String userName;
    
    // Constructors
    public UserRegistrationRequest() {}
    
    public UserRegistrationRequest(String phoneNumber, String password, String userName) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.userName = userName;
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
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
} 
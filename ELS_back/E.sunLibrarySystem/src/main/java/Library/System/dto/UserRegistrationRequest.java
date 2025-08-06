package Library.System.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegistrationRequest {
    
    @NotBlank(message = "手機號碼不能為空")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    private String phoneNumber;
    
    @NotBlank(message = "使用者名稱不能為空")
    @Size(min = 2, max = 20, message = "使用者名稱長度應在2-20個字元之間")
    private String userName;
    
    @NotBlank(message = "密碼不能為空")
    @Size(min = 6, message = "密碼長度至少6位")
    private String password;
    
    // Constructors
    public UserRegistrationRequest() {}
    
    public UserRegistrationRequest(String phoneNumber, String userName, String password) {
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.password = password;
    }
    
    // Getters and Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
} 
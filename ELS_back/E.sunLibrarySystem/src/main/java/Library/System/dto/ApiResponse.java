/**
 * API 回應資料傳輸物件 - 統一的 REST API 回應格式
 * 
 * 此類別定義了系統中所有 REST API 回應的統一格式，包含以下功能：
 * 1. 統一回應結構 - 確保所有 API 回應格式一致
 * 2. 成功/失敗狀態 - 明確區分操作結果
 * 3. 錯誤代碼支援 - 提供詳細的錯誤分類
 * 4. 時間戳記 - 記錄回應產生時間
 * 5. 泛型資料支援 - 支援各種類型的回應資料
 * 
 * 使用方式：
 * - 成功回應：ApiResponse.success("操作成功", data)
 * - 失敗回應：ApiResponse.error("錯誤訊息", "ERROR_CODE")
 * 
 * 回應格式：
 * {
 *   "success": true/false,
 *   "message": "操作訊息",
 *   "data": 實際資料,
 *   "timestamp": "回應時間",
 *   "errorCode": "錯誤代碼（可選）"
 * }
 * 
 * @param <T> 回應資料的類型，支援泛型
 * @author MaxLin
 * @version 1.0
 * @since 2025/08/07
 */
package Library.System.dto;

import java.time.LocalDateTime;

public class ApiResponse<T> {
    
    /** 操作是否成功 */
    private boolean success;
    
    /** 回應訊息，描述操作結果或錯誤資訊 */
    private String message;
    
    /** 回應資料，泛型支援各種資料類型 */
    private T data;
    
    /** 回應產生的時間戳記 */
    private LocalDateTime timestamp;
    
    /** 錯誤代碼，用於詳細的錯誤分類（可選） */
    private String errorCode;

    /**
     * 預設建構子
     * 自動設定當前時間為時間戳記
     */
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 完整建構子
     * 
     * @param success 是否成功
     * @param message 回應訊息
     * @param data 回應資料
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 含錯誤代碼的完整建構子
     * 
     * @param success 是否成功
     * @param message 回應訊息
     * @param data 回應資料
     * @param errorCode 錯誤代碼
     */
    public ApiResponse(boolean success, String message, T data, String errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }

    // 靜態工廠方法 - 建立成功回應

    /**
     * 建立成功回應（含訊息和資料）
     * 
     * @param <T> 資料類型
     * @param message 成功訊息
     * @param data 回應資料
     * @return ApiResponse 成功回應物件
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * 建立成功回應（僅含訊息）
     * 
     * @param <T> 資料類型
     * @param message 成功訊息
     * @return ApiResponse 成功回應物件
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    /**
     * 建立成功回應（僅含資料，使用預設成功訊息）
     * 
     * @param <T> 資料類型
     * @param data 回應資料
     * @return ApiResponse 成功回應物件
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "操作成功", data);
    }

    // 靜態工廠方法 - 建立失敗回應

    /**
     * 建立失敗回應（僅含錯誤訊息）
     * 
     * @param <T> 資料類型
     * @param message 錯誤訊息
     * @return ApiResponse 失敗回應物件
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /**
     * 建立失敗回應（含錯誤訊息和錯誤代碼）
     * 
     * @param <T> 資料類型
     * @param message 錯誤訊息
     * @param errorCode 錯誤代碼
     * @return ApiResponse 失敗回應物件
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode);
    }

    // Getters and Setters

    /**
     * 取得操作是否成功
     * @return boolean true表示成功，false表示失敗
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 設定操作是否成功
     * @param success 操作結果
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 取得回應訊息
     * @return String 回應訊息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 設定回應訊息
     * @param message 回應訊息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 取得回應資料
     * @return T 回應資料物件
     */
    public T getData() {
        return data;
    }

    /**
     * 設定回應資料
     * @param data 回應資料物件
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * 取得時間戳記
     * @return LocalDateTime 回應產生時間
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * 設定時間戳記
     * @param timestamp 回應產生時間
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 取得錯誤代碼
     * @return String 錯誤代碼
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 設定錯誤代碼
     * @param errorCode 錯誤代碼
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
} 
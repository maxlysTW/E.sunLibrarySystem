/**
 * 借閱控制器 - 處理圖書借閱相關的 REST API 請求
 * 
 * 此控制器負責管理圖書的借閱業務，包含以下主要功能：
 * 1. 圖書借閱 - 處理使用者借書請求
 * 2. 圖書歸還 - 處理使用者還書請求
 * 3. 借閱記錄查詢 - 查看使用者的借閱歷史
 * 4. 未歸還圖書查詢 - 查看使用者目前借閱中的圖書
 * 5. 可借閱圖書查詢 - 查看系統中可借閱的圖書
 * 6. 圖書可借閱狀態檢查 - 檢查特定圖書是否可借閱
 * 
 * 權限管理：
 * - 所有 API 都需要有效的 JWT Token 進行身份驗證
 * - 支援跨域請求，允許前端應用程式存取
 * 
 * API端點：
 * - POST /api/borrowing/borrow - 借書
 * - POST /api/borrowing/return - 還書
 * - GET /api/borrowing/history - 查詢借閱歷史
 * - GET /api/borrowing/active - 查詢未歸還圖書
 * - GET /api/borrowing/available-books - 查詢可借閱圖書
 * - GET /api/borrowing/check-availability/{inventoryId} - 檢查圖書可借閱狀態
 * 
 * @author E.sun Library System Team
 * @version 1.0
 * @since 2025
 */
package Library.System.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Library.System.common.JwtUtil;
import Library.System.dto.ApiResponse;
import Library.System.dto.BorrowBookRequest;
import Library.System.dto.BorrowingResponse;
import Library.System.entity.BorrowingRecord;
import Library.System.service.BorrowingService;

@RestController
@RequestMapping("/api/borrowing")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class BorrowingController {
    
    /** 借閱服務，處理借閱相關的業務邏輯 */
    @Autowired
    private BorrowingService borrowingService;
    
    /** JWT 工具類，負責 Token 的驗證和解析 */
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 借書 API
     * 
     * 處理使用者借書請求，包含權限驗證和借閱業務邏輯
     * 
     * @param token 使用者的 JWT Token，從 Authorization header 取得
     * @param request 借書請求物件，包含要借閱的庫存ID
     * @return ResponseEntity 包含借閱結果的 API 回應
     */
    @PostMapping("/borrow")
    public ResponseEntity<ApiResponse<Map<String, Object>>> borrowBook(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody BorrowBookRequest request) {
        try {
            // 調試用：記錄接收到的 token
            System.out.println("Received Authorization header: " + token);
            
            // 檢查 token 是否為空
            if (token == null || token.trim().isEmpty()) {
                System.out.println("Token is null or empty");
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("缺少 Authorization Token", "MISSING_TOKEN"));
            }
            
            // 移除 "Bearer " 前綴並驗證 Token
            String jwtToken = token.replace("Bearer ", "");
            System.out.println("JWT token after removing Bearer: " + jwtToken);
            
            if (!jwtUtil.validateToken(jwtToken)) {
                System.out.println("Token validation failed");
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("無效的 Token", "INVALID_TOKEN"));
            }
            
            // 從 token 中獲取使用者 ID 並執行借閱
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            BorrowingRecord record = borrowingService.borrowBook(userId, request.getInventoryId());
            
            // 構建成功回應資料
            Map<String, Object> borrowData = new HashMap<>();
            borrowData.put("recordId", record.getRecordId());
            borrowData.put("userId", record.getUserId());
            borrowData.put("inventoryId", record.getInventoryId());
            borrowData.put("borrowingTime", record.getBorrowingTime());
            borrowData.put("bookName", "Unknown"); // 預設書名，可透過 inventoryId 進一步查詢
            
            return ResponseEntity.ok(ApiResponse.success("借閱成功", borrowData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("借閱失敗: " + e.getMessage(), "BORROW_ERROR"));
        }
    }
    
    /**
     * 還書 API
     * 
     * 處理使用者還書請求，更新借閱記錄並變更圖書狀態
     * 
     * @param token 使用者的 JWT Token
     * @param request 還書請求物件，包含要歸還的庫存ID
     * @return ResponseEntity 包含歸還結果的 API 回應
     */
    @PostMapping("/return")
    public ResponseEntity<ApiResponse<Map<String, Object>>> returnBook(
            @RequestHeader("Authorization") String token,
            @RequestBody BorrowBookRequest request) {
        try {
            // 驗證 Token
            String jwtToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("無效的 Token", "INVALID_TOKEN"));
            }
            
            // 執行還書業務邏輯
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            BorrowingRecord record = borrowingService.returnBook(userId, request.getInventoryId());
            
            // 構建成功回應資料
            Map<String, Object> returnData = new HashMap<>();
            returnData.put("recordId", record.getRecordId());
            returnData.put("userId", record.getUserId());
            returnData.put("inventoryId", record.getInventoryId());
            returnData.put("returnTime", record.getReturnTime());
            returnData.put("bookName", "Unknown"); // 預設書名，可透過 inventoryId 進一步查詢
            
            return ResponseEntity.ok(ApiResponse.success("歸還成功", returnData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("歸還失敗: " + e.getMessage(), "RETURN_ERROR"));
        }
    }
    
    /**
     * 查詢使用者借閱歷史記錄 API
     * 
     * 取得指定使用者的完整借閱歷史，包含已歸還和未歸還的記錄
     * 
     * @param token 使用者的 JWT Token
     * @return ResponseEntity 包含借閱歷史列表的 API 回應
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<BorrowingResponse>>> getBorrowingHistory(
            @RequestHeader("Authorization") String token) {
        try {
            // 驗證 Token 並查詢借閱歷史
            String jwtToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("無效的 Token", "INVALID_TOKEN"));
            }
            
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            List<BorrowingResponse> history = borrowingService.getUserBorrowingHistory(userId);
            
            return ResponseEntity.ok(ApiResponse.success("查詢成功", history));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "HISTORY_ERROR"));
        }
    }
    
    /**
     * 查詢使用者未歸還圖書 API
     * 
     * 取得指定使用者目前借閱中（未歸還）的圖書清單
     * 
     * @param token 使用者的 JWT Token
     * @return ResponseEntity 包含未歸還圖書列表的 API 回應
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<BorrowingResponse>>> getActiveBorrowings(
            @RequestHeader("Authorization") String token) {
        try {
            // 驗證 Token 並查詢未歸還圖書
            String jwtToken = token.replace("Bearer ", "");
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("無效的 Token", "INVALID_TOKEN"));
            }
            
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            List<BorrowingResponse> activeBorrowings = borrowingService.getUserActiveBorrowings(userId);
            
            return ResponseEntity.ok(ApiResponse.success("查詢成功", activeBorrowings));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "ACTIVE_BORROWINGS_ERROR"));
        }
    }
    
    /**
     * 查詢系統中可借閱的圖書 API
     * 
     * 取得所有狀態為可借閱的圖書庫存項目
     * 
     * @return ResponseEntity 包含可借閱圖書列表的 API 回應
     */
    @GetMapping("/available-books")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAvailableBooks() {
        try {
            List<Library.System.entity.Inventory> availableBooks = borrowingService.getAvailableBooks();
            
            // 構建回應資料，包含圖書清單和總數
            Map<String, Object> booksData = new HashMap<>();
            booksData.put("books", availableBooks);
            booksData.put("totalCount", availableBooks.size());
            
            return ResponseEntity.ok(ApiResponse.success("查詢成功", booksData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "AVAILABLE_BOOKS_ERROR"));
        }
    }
    
    /**
     * 檢查特定圖書的可借閱狀態 API
     * 
     * 根據庫存ID檢查該圖書項目是否可以借閱
     * 
     * @param inventoryId 庫存項目ID
     * @return ResponseEntity 包含可借閱狀態資訊的 API 回應
     */
    @GetMapping("/check-availability/{inventoryId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkBookAvailability(
            @PathVariable Integer inventoryId) {
        try {
            boolean isAvailable = borrowingService.isBookAvailable(inventoryId);
            
            // 構建狀態回應資料
            Map<String, Object> availabilityData = new HashMap<>();
            availabilityData.put("inventoryId", inventoryId);
            availabilityData.put("isAvailable", isAvailable);
            availabilityData.put("status", isAvailable ? "可借閱" : "不可借閱");
            
            return ResponseEntity.ok(ApiResponse.success("查詢成功", availabilityData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "AVAILABILITY_CHECK_ERROR"));
        }
    }
} 
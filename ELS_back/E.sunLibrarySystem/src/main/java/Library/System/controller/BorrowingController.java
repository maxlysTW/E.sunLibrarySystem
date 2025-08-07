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
 * @author MaxLin
 * @version 1.0
 * @since 2025/08/07
 */
package Library.System.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    /** 日誌記錄器，用於記錄借閱控制器的運行過程 */
    private static final Logger logger = LoggerFactory.getLogger(BorrowingController.class);
    
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
        
        logger.info("收到借書請求 - 庫存ID: {}", request.getInventoryId());
        
        try {
            // 記錄接收到的 token（僅記錄長度，不記錄內容以保護隱私）
            if (token != null) {
                logger.debug("收到 Authorization header，長度: {}", token.length());
            } else {
                logger.warn("缺少 Authorization header");
            }
            
            // 檢查 token 是否為空
            if (token == null || token.trim().isEmpty()) {
                logger.warn("借書失敗 - Token 為空或無效");
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("缺少 Authorization Token", "MISSING_TOKEN"));
            }
            
            // 移除 "Bearer " 前綴並驗證 Token
            String jwtToken = token.replace("Bearer ", "");
            logger.debug("處理 JWT token，長度: {}", jwtToken.length());
            
            if (!jwtUtil.validateToken(jwtToken)) {
                logger.warn("借書失敗 - Token 驗證失敗");
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("無效的 Token", "INVALID_TOKEN"));
            }
            
            // 從 token 中獲取使用者 ID 並執行借閱
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            logger.info("Token 驗證成功，使用者ID: {}", userId);
            
            BorrowingRecord record = borrowingService.borrowBook(userId, request.getInventoryId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("recordId", record.getRecordId());
            response.put("userId", record.getUserId());
            response.put("inventoryId", record.getInventoryId());
            response.put("borrowingTime", record.getBorrowingTime());
            response.put("message", "借書成功");
            
            logger.info("借書成功 - 使用者ID: {}, 庫存ID: {}, 紀錄ID: {}", 
                       userId, request.getInventoryId(), record.getRecordId());
            
            return ResponseEntity.ok(ApiResponse.success("借書成功", response));
            
        } catch (RuntimeException e) {
            logger.warn("借書失敗 - 業務邏輯錯誤: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BORROW_ERROR"));
        } catch (Exception e) {
            logger.error("借書失敗 - 系統錯誤: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("借書失敗，請稍後再試", "SYSTEM_ERROR"));
        }
    }
    
    /**
     * 還書 API
     * 
     * 處理使用者還書請求，包含權限驗證和還書業務邏輯
     * 
     * @param token 使用者的 JWT Token
     * @param request 還書請求物件，包含要歸還的庫存ID
     * @return ResponseEntity 包含還書結果的 API 回應
     */
    @PostMapping("/return")
    public ResponseEntity<ApiResponse<Map<String, Object>>> returnBook(
            @RequestHeader("Authorization") String token,
            @RequestBody BorrowBookRequest request) {
        
        logger.info("收到還書請求 - 庫存ID: {}", request.getInventoryId());
        
        try {
            // 移除 "Bearer " 前綴並驗證 Token
            String jwtToken = token.replace("Bearer ", "");
            logger.debug("處理還書 JWT token，長度: {}", jwtToken.length());
            
            if (!jwtUtil.validateToken(jwtToken)) {
                logger.warn("還書失敗 - Token 驗證失敗");
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("無效的 Token", "INVALID_TOKEN"));
            }
            
            // 從 token 中獲取使用者 ID 並執行還書
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            logger.info("還書 Token 驗證成功，使用者ID: {}", userId);
            
            BorrowingRecord record = borrowingService.returnBook(userId, request.getInventoryId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("recordId", record.getRecordId());
            response.put("userId", record.getUserId());
            response.put("inventoryId", record.getInventoryId());
            response.put("returnTime", record.getReturnTime());
            response.put("message", "還書成功");
            
            logger.info("還書成功 - 使用者ID: {}, 庫存ID: {}, 紀錄ID: {}", 
                       userId, request.getInventoryId(), record.getRecordId());
            
            return ResponseEntity.ok(ApiResponse.success("還書成功", response));
            
        } catch (RuntimeException e) {
            logger.warn("還書失敗 - 業務邏輯錯誤: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "RETURN_ERROR"));
        } catch (Exception e) {
            logger.error("還書失敗 - 系統錯誤: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("還書失敗，請稍後再試", "SYSTEM_ERROR"));
        }
    }
    
    /**
     * 查詢借閱歷史 API
     * 
     * 查詢使用者的所有借閱紀錄
     * 
     * @param token 使用者的 JWT Token
     * @return ResponseEntity 包含借閱歷史的 API 回應
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<BorrowingResponse>>> getBorrowingHistory(
            @RequestHeader("Authorization") String token) {
        
        logger.debug("收到查詢借閱歷史請求");
        
        try {
            // 移除 "Bearer " 前綴並驗證 Token
            String jwtToken = token.replace("Bearer ", "");
            logger.debug("處理查詢歷史 JWT token，長度: {}", jwtToken.length());
            
            if (!jwtUtil.validateToken(jwtToken)) {
                logger.warn("查詢借閱歷史失敗 - Token 驗證失敗");
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("無效的 Token", "INVALID_TOKEN"));
            }
            
            // 從 token 中獲取使用者 ID 並查詢借閱歷史
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            logger.debug("查詢借閱歷史 Token 驗證成功，使用者ID: {}", userId);
            
            List<BorrowingResponse> history = borrowingService.getUserBorrowingHistory(userId);
            
            logger.info("成功查詢借閱歷史 - 使用者ID: {}, 紀錄數量: {}", userId, history.size());
            
            return ResponseEntity.ok(ApiResponse.success("查詢成功", history));
            
        } catch (RuntimeException e) {
            logger.warn("查詢借閱歷史失敗 - 業務邏輯錯誤: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "QUERY_ERROR"));
        } catch (Exception e) {
            logger.error("查詢借閱歷史失敗 - 系統錯誤: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("查詢失敗，請稍後再試", "SYSTEM_ERROR"));
        }
    }
    
    /**
     * 查詢未歸還圖書 API
     * 
     * 查詢使用者目前借閱中（未歸還）的圖書
     * 
     * @param token 使用者的 JWT Token
     * @return ResponseEntity 包含未歸還圖書的 API 回應
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<BorrowingResponse>>> getActiveBorrowings(
            @RequestHeader("Authorization") String token) {
        
        logger.debug("收到查詢未歸還圖書請求");
        
        try {
            // 移除 "Bearer " 前綴並驗證 Token
            String jwtToken = token.replace("Bearer ", "");
            logger.debug("處理查詢未歸還圖書 JWT token，長度: {}", jwtToken.length());
            
            if (!jwtUtil.validateToken(jwtToken)) {
                logger.warn("查詢未歸還圖書失敗 - Token 驗證失敗");
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("無效的 Token", "INVALID_TOKEN"));
            }
            
            // 從 token 中獲取使用者 ID 並查詢未歸還圖書
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            logger.debug("查詢未歸還圖書 Token 驗證成功，使用者ID: {}", userId);
            
            List<BorrowingResponse> activeBorrowings = borrowingService.getUserActiveBorrowings(userId);
            
            logger.info("成功查詢未歸還圖書 - 使用者ID: {}, 未歸還數量: {}", userId, activeBorrowings.size());
            
            return ResponseEntity.ok(ApiResponse.success("查詢成功", activeBorrowings));
            
        } catch (RuntimeException e) {
            logger.warn("查詢未歸還圖書失敗 - 業務邏輯錯誤: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "QUERY_ERROR"));
        } catch (Exception e) {
            logger.error("查詢未歸還圖書失敗 - 系統錯誤: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("查詢失敗，請稍後再試", "SYSTEM_ERROR"));
        }
    }
    
    /**
     * 查詢可借閱圖書 API
     * 
     * 查詢系統中所有可借閱的圖書
     * 
     * @return ResponseEntity 包含可借閱圖書的 API 回應
     */
    @GetMapping("/available-books")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAvailableBooks() {
        
        logger.debug("收到查詢可借閱圖書請求");
        
        try {
            List<Library.System.entity.Inventory> availableBooks = borrowingService.getAvailableBooks();
            
            Map<String, Object> response = new HashMap<>();
            response.put("books", availableBooks);
            response.put("count", availableBooks.size());
            
            logger.info("成功查詢可借閱圖書 - 數量: {}", availableBooks.size());
            
            return ResponseEntity.ok(ApiResponse.success("查詢成功", response));
            
        } catch (RuntimeException e) {
            logger.warn("查詢可借閱圖書失敗 - 業務邏輯錯誤: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "QUERY_ERROR"));
        } catch (Exception e) {
            logger.error("查詢可借閱圖書失敗 - 系統錯誤: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("查詢失敗，請稍後再試", "SYSTEM_ERROR"));
        }
    }
    
    /**
     * 檢查圖書可借閱狀態 API
     * 
     * 檢查特定圖書是否可借閱
     * 
     * @param inventoryId 庫存ID
     * @return ResponseEntity 包含可借閱狀態的 API 回應
     */
    @GetMapping("/check-availability/{inventoryId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkBookAvailability(
            @PathVariable Integer inventoryId) {
        
        logger.debug("收到檢查圖書可借閱狀態請求 - 庫存ID: {}", inventoryId);
        
        try {
            boolean isAvailable = borrowingService.isBookAvailable(inventoryId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("inventoryId", inventoryId);
            response.put("isAvailable", isAvailable);
            response.put("status", isAvailable ? "可借閱" : "不可借閱");
            
            logger.info("成功檢查圖書可借閱狀態 - 庫存ID: {}, 可借閱: {}", inventoryId, isAvailable);
            
            return ResponseEntity.ok(ApiResponse.success("查詢成功", response));
            
        } catch (RuntimeException e) {
            logger.warn("檢查圖書可借閱狀態失敗 - 業務邏輯錯誤: 庫存ID: {}, 錯誤: {}", inventoryId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "QUERY_ERROR"));
        } catch (Exception e) {
            logger.error("檢查圖書可借閱狀態失敗 - 系統錯誤: 庫存ID: {}, 錯誤: {}", inventoryId, e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("查詢失敗，請稍後再試", "SYSTEM_ERROR"));
        }
    }
} 
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
import Library.System.entity.BorrowingRecord;
import Library.System.service.BorrowingService;

@RestController
@RequestMapping("/api/borrowing")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class BorrowingController {
    
    @Autowired
    private BorrowingService borrowingService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 借書
     */
    @PostMapping("/borrow")
    public ResponseEntity<ApiResponse<Map<String, Object>>> borrowBook(
            @RequestBody BorrowBookRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            // 移除 "Bearer " 前綴
            String jwtToken = token.replace("Bearer ", "");
            
            // 驗證 Token
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("無效的 Token", "INVALID_TOKEN"));
            }
            
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            BorrowingRecord record = borrowingService.borrowBook(userId, request.getInventoryId());
            
            Map<String, Object> borrowData = new HashMap<>();
            borrowData.put("recordId", record.getRecordId());
            borrowData.put("borrowingTime", record.getBorrowingTime());
            borrowData.put("inventoryId", record.getInventory().getInventoryId());
            borrowData.put("bookName", record.getInventory().getBook().getName());
            
            return ResponseEntity.ok(ApiResponse.success("借書成功", borrowData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BORROW_ERROR"));
        }
    }
    
    /**
     * 還書
     */
    @PostMapping("/return")
    public ResponseEntity<ApiResponse<Map<String, Object>>> returnBook(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token) {
        try {
            // 移除 "Bearer " 前綴
            String jwtToken = token.replace("Bearer ", "");
            
            // 驗證 Token
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("無效的 Token", "INVALID_TOKEN"));
            }
            
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            Integer inventoryId = (Integer) request.get("inventoryId");
            
            if (inventoryId == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("請提供書籍 ID", "MISSING_INVENTORY_ID"));
            }
            
            BorrowingRecord record = borrowingService.returnBook(userId, inventoryId);
            
            Map<String, Object> returnData = new HashMap<>();
            returnData.put("recordId", record.getRecordId());
            returnData.put("returnTime", record.getReturnTime());
            returnData.put("inventoryId", record.getInventory().getInventoryId());
            returnData.put("bookName", record.getInventory().getBook().getName());
            
            return ResponseEntity.ok(ApiResponse.success("還書成功", returnData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "RETURN_ERROR"));
        }
    }
    
    /**
     * 查詢使用者的借閱紀錄
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<BorrowingRecord>>> getBorrowingHistory(
            @RequestHeader("Authorization") String token) {
        try {
            // 移除 "Bearer " 前綴
            String jwtToken = token.replace("Bearer ", "");
            
            // 驗證 Token
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("無效的 Token", "INVALID_TOKEN"));
            }
            
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            List<BorrowingRecord> history = borrowingService.getUserBorrowingHistory(userId);
            
            return ResponseEntity.ok(ApiResponse.success("查詢成功", history));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "HISTORY_ERROR"));
        }
    }
    
    /**
     * 查詢使用者的未歸還書籍
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<BorrowingRecord>>> getActiveBorrowings(
            @RequestHeader("Authorization") String token) {
        try {
            // 移除 "Bearer " 前綴
            String jwtToken = token.replace("Bearer ", "");
            
            // 驗證 Token
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("無效的 Token", "INVALID_TOKEN"));
            }
            
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            List<BorrowingRecord> activeBorrowings = borrowingService.getUserActiveBorrowings(userId);
            
            return ResponseEntity.ok(ApiResponse.success("查詢成功", activeBorrowings));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "ACTIVE_BORROWINGS_ERROR"));
        }
    }
    
    /**
     * 查詢可借閱的書籍
     */
    @GetMapping("/available-books")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAvailableBooks() {
        try {
            List<Library.System.entity.Inventory> availableBooks = borrowingService.getAvailableBooks();
            
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
     * 檢查特定書籍是否可借閱
     */
    @GetMapping("/check-availability/{inventoryId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkBookAvailability(
            @PathVariable Integer inventoryId) {
        try {
            boolean isAvailable = borrowingService.isBookAvailable(inventoryId);
            
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
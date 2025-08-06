package Library.System.controller;

import Library.System.common.JwtUtil;
import Library.System.entity.BorrowingRecord;
import Library.System.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrowing")
@CrossOrigin(origins = "http://localhost:5173")
public class BorrowingController {
    
    @Autowired
    private BorrowingService borrowingService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 借書
     */
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody Map<String, Object> request,
                                       @RequestHeader("Authorization") String token) {
        try {
            // 移除 "Bearer " 前綴
            String jwtToken = token.replace("Bearer ", "");
            
            // 驗證 Token
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(401).body("無效的 Token");
            }
            
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            Integer inventoryId = (Integer) request.get("inventoryId");
            
            if (inventoryId == null) {
                return ResponseEntity.badRequest().body("請提供書籍 ID");
            }
            
            BorrowingRecord record = borrowingService.borrowBook(userId, inventoryId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "借書成功");
            response.put("recordId", record.getRecordId());
            response.put("borrowingTime", record.getBorrowingTime());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 還書
     */
    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestBody Map<String, Object> request,
                                       @RequestHeader("Authorization") String token) {
        try {
            // 移除 "Bearer " 前綴
            String jwtToken = token.replace("Bearer ", "");
            
            // 驗證 Token
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(401).body("無效的 Token");
            }
            
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            Integer inventoryId = (Integer) request.get("inventoryId");
            
            if (inventoryId == null) {
                return ResponseEntity.badRequest().body("請提供書籍 ID");
            }
            
            borrowingService.returnBook(userId, inventoryId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "還書成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 查詢使用者的借閱紀錄
     */
    @GetMapping("/history")
    public ResponseEntity<List<BorrowingRecord>> getBorrowingHistory(@RequestHeader("Authorization") String token) {
        try {
            // 移除 "Bearer " 前綴
            String jwtToken = token.replace("Bearer ", "");
            
            // 驗證 Token
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(401).build();
            }
            
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            List<BorrowingRecord> history = borrowingService.getUserBorrowingHistory(userId);
            
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 查詢使用者的未歸還書籍
     */
    @GetMapping("/active")
    public ResponseEntity<List<BorrowingRecord>> getActiveBorrowings(@RequestHeader("Authorization") String token) {
        try {
            // 移除 "Bearer " 前綴
            String jwtToken = token.replace("Bearer ", "");
            
            // 驗證 Token
            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.status(401).build();
            }
            
            Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
            List<BorrowingRecord> activeBorrowings = borrowingService.getUserActiveBorrowings(userId);
            
            return ResponseEntity.ok(activeBorrowings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 
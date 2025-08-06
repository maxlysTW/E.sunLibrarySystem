package Library.System.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Library.System.entity.BorrowingRecord;
import Library.System.entity.Inventory;
import Library.System.entity.User;
import Library.System.repository.BorrowingRecordRepository;
import Library.System.repository.InventoryRepository;
import Library.System.repository.UserRepository;

@Service
public class BorrowingService {
    
    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 借書功能 - 使用資料庫交易確保資料完整性
     */
    @Transactional
    public BorrowingRecord borrowBook(Integer userId, Integer inventoryId) {
        // 1. 檢查使用者是否存在
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("使用者不存在");
        }
        
        // 2. 檢查書籍是否存在且可借閱
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(inventoryId);
        if (inventoryOpt.isEmpty()) {
            throw new RuntimeException("書籍不存在");
        }
        
        Inventory inventory = inventoryOpt.get();
        String status = inventory.getStatus();
        System.out.println("Inventory status: " + status); // 調試用
        if (!"在庫".equals(status)) {
            throw new RuntimeException("此書籍目前不可借閱，狀態：" + status);
        }
        
        // 3. 檢查使用者是否已借閱此書籍
        if (borrowingRecordRepository.existsActiveBorrowing(userId, inventoryId)) {
            throw new RuntimeException("您已借閱此書籍");
        }
        
        // 4. 檢查此書籍是否已被其他人借閱
        Optional<BorrowingRecord> existingBorrow = borrowingRecordRepository.findByInventoryIdAndReturnTimeIsNull(inventoryId);
        if (existingBorrow.isPresent()) {
            throw new RuntimeException("此書籍已被其他使用者借閱");
        }
        
        // 5. 更新庫存狀態為「已借閱」
        inventory.setStatus("已借閱");
        inventoryRepository.save(inventory);
        
        // 6. 建立借閱紀錄
        BorrowingRecord record = new BorrowingRecord(userId, inventoryId);
        return borrowingRecordRepository.save(record);
    }
    
    /**
     * 還書功能 - 使用資料庫交易確保資料完整性
     */
    @Transactional
    public BorrowingRecord returnBook(Integer userId, Integer inventoryId) {
        // 1. 檢查是否有有效的借閱紀錄
        Optional<BorrowingRecord> recordOpt = borrowingRecordRepository.findByInventoryIdAndReturnTimeIsNull(inventoryId);
        if (recordOpt.isEmpty()) {
            throw new RuntimeException("沒有找到有效的借閱紀錄");
        }
        
        BorrowingRecord record = recordOpt.get();
        
        // 2. 檢查是否為借閱者本人
        if (!record.getUserId().equals(userId)) {
            throw new RuntimeException("您沒有借閱此書籍，無法歸還");
        }
        
        // 3. 更新借閱紀錄的歸還時間
        record.setReturnTime(LocalDateTime.now());
        borrowingRecordRepository.save(record);
        
        // 4. 更新庫存狀態為「可借閱」
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(inventoryId);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            inventory.setStatus("在庫");
            inventoryRepository.save(inventory);
        } else {
            throw new RuntimeException("書籍庫存資料異常");
        }
        
        return record;
    }
    
    /**
     * 查詢使用者的借閱紀錄
     */
    @Transactional(readOnly = true)
    public List<BorrowingRecord> getUserBorrowingHistory(Integer userId) {
        return borrowingRecordRepository.findByUserIdOrderByBorrowingTimeDesc(userId);
    }
    
    /**
     * 查詢使用者的未歸還書籍
     */
    @Transactional(readOnly = true)
    public List<BorrowingRecord> getUserActiveBorrowings(Integer userId) {
        return borrowingRecordRepository.findActiveBorrowingsByUserId(userId);
    }
    
    /**
     * 查詢可借閱的書籍
     */
    @Transactional(readOnly = true)
    public List<Inventory> getAvailableBooks() {
        return inventoryRepository.findAvailableBooks();
    }
    
    /**
     * 檢查書籍是否可借閱
     */
    @Transactional(readOnly = true)
    public boolean isBookAvailable(Integer inventoryId) {
        return inventoryRepository.isBookAvailable(inventoryId);
    }
} 
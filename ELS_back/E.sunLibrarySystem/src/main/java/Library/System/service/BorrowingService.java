package Library.System.service;

import Library.System.entity.BorrowingRecord;
import Library.System.entity.Inventory;
import Library.System.entity.User;
import Library.System.repository.BorrowingRecordRepository;
import Library.System.repository.InventoryRepository;
import Library.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BorrowingService {
    
    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 借書
     */
    public BorrowingRecord borrowBook(Integer userId, Integer inventoryId) {
        // 檢查使用者是否存在
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("使用者不存在");
        }
        
        // 檢查書籍是否可借閱
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(inventoryId);
        if (inventoryOpt.isEmpty()) {
            throw new RuntimeException("書籍不存在");
        }
        
        Inventory inventory = inventoryOpt.get();
        if (!"在庫".equals(inventory.getStatus())) {
            throw new RuntimeException("書籍不可借閱");
        }
        
        // 檢查使用者是否已借閱此書
        if (borrowingRecordRepository.existsActiveBorrowing(userId, inventoryId)) {
            throw new RuntimeException("您已借閱此書");
        }
        
        // 更新庫存狀態
        inventory.setStatus("出借中");
        inventoryRepository.save(inventory);
        
        // 建立借閱紀錄
        BorrowingRecord record = new BorrowingRecord(userId, inventoryId);
        return borrowingRecordRepository.save(record);
    }
    
    /**
     * 還書
     */
    public void returnBook(Integer userId, Integer inventoryId) {
        // 檢查是否有有效的借閱紀錄
        Optional<BorrowingRecord> recordOpt = borrowingRecordRepository.findByInventoryIdAndReturnTimeIsNull(inventoryId);
        if (recordOpt.isEmpty()) {
            throw new RuntimeException("沒有找到有效的借閱紀錄");
        }
        
        BorrowingRecord record = recordOpt.get();
        if (!record.getUserId().equals(userId)) {
            throw new RuntimeException("您沒有借閱此書");
        }
        
        // 更新借閱紀錄
        record.setReturnTime(LocalDateTime.now());
        borrowingRecordRepository.save(record);
        
        // 更新庫存狀態
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(inventoryId);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            inventory.setStatus("整理中(歸還後未入庫)");
            inventoryRepository.save(inventory);
        }
    }
    
    /**
     * 查詢使用者的借閱紀錄
     */
    public List<BorrowingRecord> getUserBorrowingHistory(Integer userId) {
        return borrowingRecordRepository.findByUserIdOrderByBorrowingTimeDesc(userId);
    }
    
    /**
     * 查詢使用者的未歸還書籍
     */
    public List<BorrowingRecord> getUserActiveBorrowings(Integer userId) {
        return borrowingRecordRepository.findActiveBorrowingsByUserId(userId);
    }
} 
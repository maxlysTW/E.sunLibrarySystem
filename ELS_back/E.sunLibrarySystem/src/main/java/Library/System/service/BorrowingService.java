package Library.System.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Library.System.dto.BorrowingResponse;
import Library.System.entity.Book;
import Library.System.entity.BorrowingRecord;
import Library.System.entity.Inventory;
import Library.System.entity.User;
import Library.System.repository.BookRepository;
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
    
    @Autowired
    private BookRepository bookRepository;
    
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
        if (!"Available".equals(status)) {
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
        inventory.setStatus("Borrowed");
        inventoryRepository.save(inventory);
        
        // 6. 建立借閱紀錄
        BorrowingRecord record = new BorrowingRecord(userId, inventoryId);
        record = borrowingRecordRepository.save(record);
        
        // 7. 重新查詢以獲取關聯實體
        return borrowingRecordRepository.findByIdWithAssociations(record.getRecordId())
                .orElseThrow(() -> new RuntimeException("借閱紀錄創建失敗"));
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
            inventory.setStatus("Available");
            inventoryRepository.save(inventory);
        } else {
            throw new RuntimeException("書籍庫存資料異常");
        }
        
        // 5. 重新查詢以獲取關聯實體
        return borrowingRecordRepository.findByIdWithAssociations(record.getRecordId())
                .orElseThrow(() -> new RuntimeException("借閱紀錄查詢失敗"));
    }
    
    /**
     * 查詢使用者的借閱紀錄
     */
    @Transactional(readOnly = true)
    public List<BorrowingResponse> getUserBorrowingHistory(Integer userId) {
        List<BorrowingRecord> records = borrowingRecordRepository.findByUserIdOrderByBorrowingTimeDesc(userId);
        return records.stream()
                .map(this::convertToBorrowingResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 查詢使用者的未歸還書籍
     */
    @Transactional(readOnly = true)
    public List<BorrowingResponse> getUserActiveBorrowings(Integer userId) {
        List<BorrowingRecord> records = borrowingRecordRepository.findActiveBorrowingsByUserId(userId);
        return records.stream()
                .map(this::convertToBorrowingResponse)
                .collect(Collectors.toList());
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
    
    /**
     * 將 BorrowingRecord 轉換為 BorrowingResponse
     */
    private BorrowingResponse convertToBorrowingResponse(BorrowingRecord record) {
        String userName = "Unknown";
        String bookName = "Unknown";
        String bookAuthor = "Unknown";
        String bookIsbn = "Unknown";
        String status = record.getReturnTime() == null ? "借閱中" : "已歸還";
        
        if (record.getUser() != null) {
            userName = record.getUser().getUserName();
        }
        
        // 通過 inventoryId 查詢書籍信息
        Integer inventoryId = record.getInventoryId();
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(inventoryId);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            bookIsbn = inventory.getIsbn();
            
            // 查詢書籍詳細信息
            Optional<Book> bookOpt = bookRepository.findById(bookIsbn);
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                bookName = book.getName();
                bookAuthor = book.getAuthor();
            }
        }
        
        return new BorrowingResponse(
            record.getRecordId(),
            record.getUserId(),
            inventoryId,
            record.getBorrowingTime(),
            record.getReturnTime(),
            userName,
            bookName,
            bookAuthor,
            bookIsbn,
            status
        );
    }
} 
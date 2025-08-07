package Library.System.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    /** 日誌記錄器，用於記錄借閱服務的運行過程 */
    private static final Logger logger = LoggerFactory.getLogger(BorrowingService.class);
    
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
        logger.info("開始處理借書請求 - 使用者ID: {}, 庫存ID: {}", userId, inventoryId);
        
        try {
            // 1. 檢查使用者是否存在
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                logger.warn("借書失敗 - 使用者不存在: {}", userId);
                throw new RuntimeException("使用者不存在");
            }
            logger.debug("使用者驗證成功: {}", userId);
            
            // 2. 檢查書籍是否存在且可借閱
            Optional<Inventory> inventoryOpt = inventoryRepository.findById(inventoryId);
            if (inventoryOpt.isEmpty()) {
                logger.warn("借書失敗 - 書籍不存在: {}", inventoryId);
                throw new RuntimeException("書籍不存在");
            }
            
            Inventory inventory = inventoryOpt.get();
            String status = inventory.getStatus();
            logger.debug("書籍庫存狀態: {} - 庫存ID: {}", status, inventoryId);
            
            if (!"Available".equals(status)) {
                logger.warn("借書失敗 - 書籍不可借閱，狀態: {} - 庫存ID: {}", status, inventoryId);
                throw new RuntimeException("此書籍目前不可借閱，狀態：" + status);
            }
            
            // 3. 檢查使用者是否已借閱此書籍
            if (borrowingRecordRepository.existsActiveBorrowing(userId, inventoryId)) {
                logger.warn("借書失敗 - 使用者已借閱此書: 使用者ID: {}, 庫存ID: {}", userId, inventoryId);
                throw new RuntimeException("您已借閱此書籍");
            }
            
            // 4. 檢查此書籍是否已被其他人借閱
            Optional<BorrowingRecord> existingBorrow = borrowingRecordRepository.findByInventoryIdAndReturnTimeIsNull(inventoryId);
            if (existingBorrow.isPresent()) {
                logger.warn("借書失敗 - 書籍已被其他使用者借閱: 庫存ID: {}", inventoryId);
                throw new RuntimeException("此書籍已被其他使用者借閱");
            }
            
            // 5. 更新庫存狀態為「已借閱」
            inventory.setStatus("Borrowed");
            inventoryRepository.save(inventory);
            logger.info("庫存狀態已更新為已借閱: 庫存ID: {}", inventoryId);
            
            // 6. 建立借閱紀錄
            BorrowingRecord record = new BorrowingRecord(userId, inventoryId);
            record = borrowingRecordRepository.save(record);
            logger.info("借閱紀錄已建立: 紀錄ID: {}, 使用者ID: {}, 庫存ID: {}", 
                       record.getRecordId(), userId, inventoryId);
            
            // 7. 重新查詢以獲取關聯實體
            BorrowingRecord result = borrowingRecordRepository.findByIdWithAssociations(record.getRecordId())
                    .orElseThrow(() -> new RuntimeException("借閱紀錄創建失敗"));
            
            logger.info("借書成功完成 - 使用者ID: {}, 庫存ID: {}, 紀錄ID: {}", 
                       userId, inventoryId, result.getRecordId());
            return result;
            
        } catch (RuntimeException e) {
            logger.error("借書過程中發生業務邏輯錯誤: 使用者ID: {}, 庫存ID: {}, 錯誤: {}", 
                        userId, inventoryId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("借書過程中發生系統錯誤: 使用者ID: {}, 庫存ID: {}, 錯誤: {}", 
                        userId, inventoryId, e.getMessage(), e);
            throw new RuntimeException("借書失敗，請稍後再試", e);
        }
    }
    
    /**
     * 還書功能 - 使用資料庫交易確保資料完整性
     */
    @Transactional
    public BorrowingRecord returnBook(Integer userId, Integer inventoryId) {
        logger.info("開始處理還書請求 - 使用者ID: {}, 庫存ID: {}", userId, inventoryId);
        
        try {
            // 1. 檢查是否有有效的借閱紀錄
            Optional<BorrowingRecord> recordOpt = borrowingRecordRepository.findByInventoryIdAndReturnTimeIsNull(inventoryId);
            if (recordOpt.isEmpty()) {
                logger.warn("還書失敗 - 沒有找到有效的借閱紀錄: 庫存ID: {}", inventoryId);
                throw new RuntimeException("沒有找到有效的借閱紀錄");
            }
            
            BorrowingRecord record = recordOpt.get();
            logger.debug("找到借閱紀錄: 紀錄ID: {}, 借閱者ID: {}", record.getRecordId(), record.getUserId());
            
            // 2. 檢查是否為借閱者本人
            if (!record.getUserId().equals(userId)) {
                logger.warn("還書失敗 - 非借閱者本人嘗試還書: 實際借閱者ID: {}, 嘗試還書者ID: {}, 庫存ID: {}", 
                           record.getUserId(), userId, inventoryId);
                throw new RuntimeException("您沒有借閱此書籍，無法歸還");
            }
            
            // 3. 更新借閱紀錄的歸還時間
            record.setReturnTime(LocalDateTime.now());
            borrowingRecordRepository.save(record);
            logger.info("借閱紀錄已更新歸還時間: 紀錄ID: {}, 歸還時間: {}", 
                       record.getRecordId(), record.getReturnTime());
            
            // 4. 更新庫存狀態為「可借閱」
            Optional<Inventory> inventoryOpt = inventoryRepository.findById(inventoryId);
            if (inventoryOpt.isPresent()) {
                Inventory inventory = inventoryOpt.get();
                inventory.setStatus("Available");
                inventoryRepository.save(inventory);
                logger.info("庫存狀態已更新為可借閱: 庫存ID: {}", inventoryId);
            } else {
                logger.error("還書失敗 - 書籍庫存資料異常: 庫存ID: {}", inventoryId);
                throw new RuntimeException("書籍庫存資料異常");
            }
            
            // 5. 重新查詢以獲取關聯實體
            BorrowingRecord result = borrowingRecordRepository.findByIdWithAssociations(record.getRecordId())
                    .orElseThrow(() -> new RuntimeException("借閱紀錄查詢失敗"));
            
            logger.info("還書成功完成 - 使用者ID: {}, 庫存ID: {}, 紀錄ID: {}", 
                       userId, inventoryId, result.getRecordId());
            return result;
            
        } catch (RuntimeException e) {
            logger.error("還書過程中發生業務邏輯錯誤: 使用者ID: {}, 庫存ID: {}, 錯誤: {}", 
                        userId, inventoryId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("還書過程中發生系統錯誤: 使用者ID: {}, 庫存ID: {}, 錯誤: {}", 
                        userId, inventoryId, e.getMessage(), e);
            throw new RuntimeException("還書失敗，請稍後再試", e);
        }
    }
    
    /**
     * 查詢使用者的借閱紀錄
     */
    @Transactional(readOnly = true)
    public List<BorrowingResponse> getUserBorrowingHistory(Integer userId) {
        logger.debug("查詢使用者借閱歷史: 使用者ID: {}", userId);
        
        try {
            List<BorrowingRecord> records = borrowingRecordRepository.findByUserIdOrderByBorrowingTimeDesc(userId);
            List<BorrowingResponse> responses = records.stream()
                    .map(this::convertToBorrowingResponse)
                    .collect(Collectors.toList());
            
            logger.debug("成功查詢使用者借閱歷史: 使用者ID: {}, 紀錄數量: {}", userId, responses.size());
            return responses;
        } catch (Exception e) {
            logger.error("查詢使用者借閱歷史失敗: 使用者ID: {}, 錯誤: {}", userId, e.getMessage(), e);
            throw new RuntimeException("查詢借閱歷史失敗", e);
        }
    }
    
    /**
     * 查詢使用者的未歸還書籍
     */
    @Transactional(readOnly = true)
    public List<BorrowingResponse> getUserActiveBorrowings(Integer userId) {
        logger.debug("查詢使用者未歸還書籍: 使用者ID: {}", userId);
        
        try {
            List<BorrowingRecord> records = borrowingRecordRepository.findActiveBorrowingsByUserId(userId);
            List<BorrowingResponse> responses = records.stream()
                    .map(this::convertToBorrowingResponse)
                    .collect(Collectors.toList());
            
            logger.debug("成功查詢使用者未歸還書籍: 使用者ID: {}, 未歸還數量: {}", userId, responses.size());
            return responses;
        } catch (Exception e) {
            logger.error("查詢使用者未歸還書籍失敗: 使用者ID: {}, 錯誤: {}", userId, e.getMessage(), e);
            throw new RuntimeException("查詢未歸還書籍失敗", e);
        }
    }
    
    /**
     * 查詢可借閱的書籍
     */
    @Transactional(readOnly = true)
    public List<Inventory> getAvailableBooks() {
        logger.debug("查詢可借閱書籍列表");
        
        try {
            List<Inventory> books = inventoryRepository.findAvailableBooks();
            logger.debug("成功查詢可借閱書籍: 數量: {}", books.size());
            return books;
        } catch (Exception e) {
            logger.error("查詢可借閱書籍失敗: 錯誤: {}", e.getMessage(), e);
            throw new RuntimeException("查詢可借閱書籍失敗", e);
        }
    }
    
    /**
     * 檢查書籍是否可借閱
     */
    @Transactional(readOnly = true)
    public boolean isBookAvailable(Integer inventoryId) {
        logger.debug("檢查書籍可借閱狀態: 庫存ID: {}", inventoryId);
        
        try {
            boolean isAvailable = inventoryRepository.isBookAvailable(inventoryId);
            logger.debug("書籍可借閱狀態檢查完成: 庫存ID: {}, 可借閱: {}", inventoryId, isAvailable);
            return isAvailable;
        } catch (Exception e) {
            logger.error("檢查書籍可借閱狀態失敗: 庫存ID: {}, 錯誤: {}", inventoryId, e.getMessage(), e);
            throw new RuntimeException("檢查書籍可借閱狀態失敗", e);
        }
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
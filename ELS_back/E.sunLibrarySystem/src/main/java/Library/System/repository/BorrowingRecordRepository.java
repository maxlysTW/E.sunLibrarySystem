package Library.System.repository;

import Library.System.entity.BorrowingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Integer> {
    
    /**
     * 查詢使用者的借閱紀錄
     */
    List<BorrowingRecord> findByUserIdOrderByBorrowingTimeDesc(Integer userId);
    
    /**
     * 查詢使用者的未歸還書籍
     */
    @Query("SELECT br FROM BorrowingRecord br WHERE br.userId = :userId AND br.returnTime IS NULL")
    List<BorrowingRecord> findActiveBorrowingsByUserId(@Param("userId") Integer userId);
    
    /**
     * 檢查使用者是否已借閱特定書籍
     */
    @Query("SELECT COUNT(br) > 0 FROM BorrowingRecord br WHERE br.userId = :userId AND br.inventoryId = :inventoryId AND br.returnTime IS NULL")
    boolean existsActiveBorrowing(@Param("userId") Integer userId, @Param("inventoryId") Integer inventoryId);
    
    /**
     * 查詢特定庫存的借閱紀錄
     */
    Optional<BorrowingRecord> findByInventoryIdAndReturnTimeIsNull(Integer inventoryId);
} 
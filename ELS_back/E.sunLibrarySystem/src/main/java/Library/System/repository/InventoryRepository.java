package Library.System.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import Library.System.entity.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    
    /**
     * 查詢可借閱的書籍
     */
    @Query("SELECT i FROM Inventory i LEFT JOIN FETCH i.book WHERE i.status = 'Available'")
    List<Inventory> findAvailableBooks();
    
    /**
     * 查詢所有書籍（包含庫存信息）
     */
    @Query("SELECT i FROM Inventory i LEFT JOIN FETCH i.book")
    List<Inventory> findAllBooks();
    
    /**
     * 根據 ISBN 查詢庫存
     */
    List<Inventory> findByIsbn(String isbn);
    
    /**
     * 根據狀態查詢庫存
     */
    List<Inventory> findByStatus(String status);
    
    /**
     * 檢查書籍是否可借閱
     */
    @Query("SELECT COUNT(i) > 0 FROM Inventory i WHERE i.inventoryId = :inventoryId AND i.status = 'Available'")
    boolean isBookAvailable(@Param("inventoryId") Integer inventoryId);
} 
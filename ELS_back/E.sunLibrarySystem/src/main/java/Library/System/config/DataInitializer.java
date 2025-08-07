package Library.System.config;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import Library.System.entity.Book;
import Library.System.entity.Inventory;
import Library.System.repository.BookRepository;
import Library.System.repository.InventoryRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    
    /** 日誌記錄器，用於記錄資料初始化的過程 */
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("開始檢查資料初始化...");
        
        long bookCount = bookRepository.count();
        logger.info("當前書籍數量: {}", bookCount);
        
        if (bookCount == 0) {
            logger.info("沒有書籍資料，開始初始化...");
            initializeBooks();
        } else {
            logger.info("已有書籍資料，跳過初始化");
        }
    }
    
    private void initializeBooks() {
        List<Book> books = Arrays.asList(
            new Book("9789865020059", "原子習慣", "詹姆斯‧克利爾", 
                    "本書作者詹姆斯‧克利爾是習慣養成領域的專家，他將複雜的行為科學簡化為實用的策略，幫助讀者建立好習慣、戒除壞習慣。", 
                    "https://example.com/atomic-habits.jpg"),
            
            new Book("9789865020060", "深度工作力", "卡爾‧紐波特", 
                    "在分心時代，專注力是最稀缺的資源。本書提供實用的策略，幫助你在充滿干擾的世界中培養深度工作的能力。", 
                    "https://example.com/deep-work.jpg"),
            
            new Book("9789865020061", "刻意練習", "安德斯‧艾瑞克森", 
                    "本書揭示卓越表現背後的秘密：刻意練習。作者透過研究各行各業的頂尖人才，說明如何透過有目的的練習達到專業水準。", 
                    "https://example.com/deliberate-practice.jpg"),
            
            new Book("9789865020062", "心流", "米哈里‧契克森米哈賴", 
                    "心流是一種完全沉浸於當下活動的狀態，本書探討如何創造更多的心流體驗，讓生活更加充實和有意義。", 
                    "https://example.com/flow.jpg"),
            
            new Book("9789865020063", "思考，快與慢", "丹尼爾‧康納曼", 
                    "諾貝爾經濟學獎得主丹尼爾‧康納曼探討人類思考的兩種模式：快速直覺的系統一和緩慢理性的系統二。", 
                    "https://example.com/thinking-fast-and-slow.jpg")
        );
        
        for (Book book : books) {
            try {
                Book savedBook = bookRepository.save(book);
                logger.debug("成功保存書籍: ISBN: {}, 書名: {}", savedBook.getIsbn(), savedBook.getName());
                
                // 為每本書創建庫存項目
                Inventory inventory = new Inventory(savedBook.getIsbn(), "Available");
                Inventory savedInventory = inventoryRepository.save(inventory);
                logger.debug("成功創建庫存: 庫存ID: {}, ISBN: {}", savedInventory.getInventoryId(), savedInventory.getIsbn());
                
            } catch (Exception e) {
                logger.error("初始化書籍失敗: ISBN: {}, 書名: {}, 錯誤: {}", 
                           book.getIsbn(), book.getName(), e.getMessage(), e);
            }
        }
        
        logger.info("資料初始化完成！");
    }
} 
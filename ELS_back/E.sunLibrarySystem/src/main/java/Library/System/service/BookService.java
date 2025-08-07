package Library.System.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Library.System.dto.BookResponse;
import Library.System.dto.InventoryResponse;
import Library.System.entity.Book;
import Library.System.entity.Inventory;
import Library.System.repository.BookRepository;
import Library.System.repository.InventoryRepository;

@Service
@Transactional
public class BookService {
    
    /** 日誌記錄器，用於記錄書籍服務的運行過程 */
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    /**
     * 查詢所有可借閱的書籍
     */
    public List<InventoryResponse> getAvailableBooks() {
        logger.debug("查詢所有可借閱書籍");
        
        try {
            List<Inventory> inventories = inventoryRepository.findAvailableBooks();
            List<InventoryResponse> responses = inventories.stream()
                    .map(this::convertToInventoryResponse)
                    .collect(Collectors.toList());
            
            logger.info("成功查詢可借閱書籍 - 數量: {}", responses.size());
            return responses;
        } catch (Exception e) {
            logger.error("查詢可借閱書籍失敗: 錯誤: {}", e.getMessage(), e);
            throw new RuntimeException("查詢可借閱書籍失敗", e);
        }
    }
    
    /**
     * 將 Inventory 轉換為 InventoryResponse
     */
    private InventoryResponse convertToInventoryResponse(Inventory inventory) {
        Book book = inventory.getBook();
        BookResponse bookResponse = null;
        
        if (book != null) {
            bookResponse = new BookResponse(
                book.getIsbn(),
                book.getName(),
                book.getAuthor(),
                book.getIntroduction(),
                book.getImageUrl()
            );
        }
        
        return new InventoryResponse(
            inventory.getInventoryId(),
            inventory.getIsbn(),
            inventory.getStoreTime(),
            inventory.getStatus(),
            bookResponse
        );
    }
    
    /**
     * 根據 ISBN 查詢書籍
     */
    public Optional<Book> findByIsbn(String isbn) {
        logger.debug("根據 ISBN 查詢書籍: {}", isbn);
        
        try {
            Optional<Book> book = bookRepository.findById(isbn);
            if (book.isPresent()) {
                logger.debug("成功找到書籍: ISBN: {}, 書名: {}", isbn, book.get().getName());
            } else {
                logger.debug("未找到書籍: ISBN: {}", isbn);
            }
            return book;
        } catch (Exception e) {
            logger.error("根據 ISBN 查詢書籍失敗: ISBN: {}, 錯誤: {}", isbn, e.getMessage(), e);
            throw new RuntimeException("查詢書籍失敗", e);
        }
    }
    
    /**
     * 根據書名查詢書籍
     */
    public Optional<Book> findByName(String name) {
        logger.debug("根據書名查詢書籍: {}", name);
        
        try {
            Optional<Book> book = bookRepository.findByName(name);
            if (book.isPresent()) {
                logger.debug("成功找到書籍: 書名: {}, ISBN: {}", name, book.get().getIsbn());
            } else {
                logger.debug("未找到書籍: 書名: {}", name);
            }
            return book;
        } catch (Exception e) {
            logger.error("根據書名查詢書籍失敗: 書名: {}, 錯誤: {}", name, e.getMessage(), e);
            throw new RuntimeException("查詢書籍失敗", e);
        }
    }
    
    /**
     * 根據作者查詢書籍
     */
    public Optional<Book> findByAuthor(String author) {
        logger.debug("根據作者查詢書籍: {}", author);
        
        try {
            Optional<Book> book = bookRepository.findByAuthor(author);
            if (book.isPresent()) {
                logger.debug("成功找到書籍: 作者: {}, ISBN: {}", author, book.get().getIsbn());
            } else {
                logger.debug("未找到書籍: 作者: {}", author);
            }
            return book;
        } catch (Exception e) {
            logger.error("根據作者查詢書籍失敗: 作者: {}, 錯誤: {}", author, e.getMessage(), e);
            throw new RuntimeException("查詢書籍失敗", e);
        }
    }
    
    /**
     * 查詢所有書籍
     */
    public List<Book> findAllBooks() {
        logger.debug("查詢所有書籍");
        
        try {
            List<Book> books = bookRepository.findAll();
            logger.info("成功查詢所有書籍 - 數量: {}", books.size());
            return books;
        } catch (Exception e) {
            logger.error("查詢所有書籍失敗: 錯誤: {}", e.getMessage(), e);
            throw new RuntimeException("查詢所有書籍失敗", e);
        }
    }
    
    /**
     * 查詢所有書籍（包含庫存信息）
     */
    public List<InventoryResponse> getAllBooksWithInventory() {
        logger.debug("查詢所有書籍（包含庫存信息）");
        
        try {
            List<Inventory> inventories = inventoryRepository.findAllBooks();
            List<InventoryResponse> responses = inventories.stream()
                    .map(this::convertToInventoryResponse)
                    .collect(Collectors.toList());
            
            logger.info("成功查詢所有書籍（包含庫存信息） - 數量: {}", responses.size());
            return responses;
        } catch (Exception e) {
            logger.error("查詢所有書籍（包含庫存信息）失敗: 錯誤: {}", e.getMessage(), e);
            throw new RuntimeException("查詢所有書籍失敗", e);
        }
    }
    
    /**
     * 檢查書籍是否可借閱
     */
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
     * 添加書籍
     */
    public Book addBook(String isbn, String name, String author, String introduction, String imageUrl) {
        logger.info("開始添加書籍 - ISBN: {}, 書名: {}, 作者: {}", isbn, name, author);
        
        try {
            // 檢查書籍是否已存在
            if (bookRepository.findById(isbn).isPresent()) {
                logger.warn("添加書籍失敗 - 書籍已存在: ISBN: {}", isbn);
                throw new RuntimeException("書籍已存在");
            }
            
            Book book = new Book();
            book.setIsbn(isbn);
            book.setName(name);
            book.setAuthor(author);
            book.setIntroduction(introduction);
            book.setImageUrl(imageUrl);
            
            Book savedBook = bookRepository.save(book);
            logger.info("成功添加書籍 - ISBN: {}, 書名: {}, 作者: {}", 
                       savedBook.getIsbn(), savedBook.getName(), savedBook.getAuthor());
            
            return savedBook;
        } catch (RuntimeException e) {
            logger.error("添加書籍失敗 - 業務邏輯錯誤: ISBN: {}, 錯誤: {}", isbn, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("添加書籍失敗 - 系統錯誤: ISBN: {}, 錯誤: {}", isbn, e.getMessage(), e);
            throw new RuntimeException("添加書籍失敗", e);
        }
    }
    
    /**
     * 添加庫存
     */
    public Inventory addInventory(String isbn) {
        logger.info("開始添加庫存 - ISBN: {}", isbn);
        
        try {
            Book book = bookRepository.findById(isbn)
                    .orElseThrow(() -> new RuntimeException("書籍不存在"));
            
            Inventory inventory = new Inventory(isbn, "Available");
            Inventory savedInventory = inventoryRepository.save(inventory);
            
            logger.info("成功添加庫存 - 庫存ID: {}, ISBN: {}, 書名: {}", 
                       savedInventory.getInventoryId(), isbn, book.getName());
            
            return savedInventory;
        } catch (RuntimeException e) {
            logger.error("添加庫存失敗 - 業務邏輯錯誤: ISBN: {}, 錯誤: {}", isbn, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("添加庫存失敗 - 系統錯誤: ISBN: {}, 錯誤: {}", isbn, e.getMessage(), e);
            throw new RuntimeException("添加庫存失敗", e);
        }
    }
} 
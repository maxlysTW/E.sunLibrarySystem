/**
 * 圖書控制器 - 處理圖書相關的 REST API 請求
 * 
 * 此控制器負責管理圖書的各種操作，包含以下主要功能：
 * 1. 圖書查詢 - 支援多種查詢方式（全部、ISBN、書名、作者）
 * 2. 圖書庫存資訊查詢 - 顯示圖書的可借閱狀態
 * 3. 圖書新增 - 添加新的圖書到系統中（測試功能）
 * 4. 庫存管理 - 管理圖書的庫存項目（測試功能）
 * 
 * API端點：
 * - GET /api/books/available - 查詢可借閱圖書
 * - GET /api/books/{isbn} - 根據ISBN查詢圖書
 * - GET /api/books/search/name - 根據書名搜尋圖書
 * - GET /api/books/search/author - 根據作者搜尋圖書
 * - GET /api/books/all - 查詢所有圖書
 * - POST /api/books/add - 添加新圖書
 * - POST /api/books/inventory/add - 添加庫存項目
 * 
 * @author MaxLin
 * @version 1.0
 * @since 2025/08/07
 */
package Library.System.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Library.System.dto.ApiResponse;
import Library.System.dto.InventoryResponse;
import Library.System.entity.Book;
import Library.System.entity.Inventory;
import Library.System.service.BookService;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class BookController {
    
    /** 日誌記錄器，用於記錄圖書控制器的運行過程 */
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    
    /** 圖書服務，處理圖書相關的業務邏輯 */
    @Autowired
    private BookService bookService;
    
    /**
     * 查詢所有可借閱的圖書（包含庫存資訊）
     * 
     * 回傳系統中所有圖書的清單，每本書都會附帶庫存狀態資訊
     * 
     * @return ResponseEntity 包含圖書列表的 API 回應
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAvailableBooks() {
        logger.debug("收到查詢可借閱圖書請求");
        
        try {
            List<InventoryResponse> books = bookService.getAllBooksWithInventory();
            logger.info("成功查詢可借閱圖書 - 數量: {}", books.size());
            return ResponseEntity.ok(ApiResponse.success("查詢成功", books));
        } catch (RuntimeException e) {
            logger.warn("查詢可借閱圖書失敗 - 業務邏輯錯誤: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BOOKS_QUERY_ERROR"));
        } catch (Exception e) {
            logger.error("查詢可借閱圖書失敗 - 系統錯誤: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("查詢失敗，請稍後再試", "SYSTEM_ERROR"));
        }
    }
    
    /**
     * 根據 ISBN 查詢特定圖書
     * 
     * 透過國際標準書號 (ISBN) 精確查找圖書資訊
     * 
     * @param isbn 國際標準書號
     * @return ResponseEntity 包含圖書資訊的 API 回應
     */
    @GetMapping("/{isbn}")
    public ResponseEntity<ApiResponse<Book>> getBookByIsbn(@PathVariable String isbn) {
        logger.debug("收到根據 ISBN 查詢圖書請求: {}", isbn);
        
        try {
            return bookService.findByIsbn(isbn)
                    .map(book -> {
                        logger.info("成功查詢圖書 - ISBN: {}, 書名: {}", isbn, book.getName());
                        return ResponseEntity.ok(ApiResponse.success("查詢成功", book));
                    })
                    .orElseGet(() -> {
                        logger.warn("未找到圖書 - ISBN: {}", isbn);
                        return ResponseEntity.ok(ApiResponse.error("書籍不存在", "BOOK_NOT_FOUND"));
                    });
        } catch (RuntimeException e) {
            logger.warn("根據 ISBN 查詢圖書失敗 - 業務邏輯錯誤: ISBN: {}, 錯誤: {}", isbn, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BOOK_QUERY_ERROR"));
        } catch (Exception e) {
            logger.error("根據 ISBN 查詢圖書失敗 - 系統錯誤: ISBN: {}, 錯誤: {}", isbn, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("查詢失敗，請稍後再試", "SYSTEM_ERROR"));
        }
    }
    
    /**
     * 根據書名查詢圖書
     * 
     * 透過完整的書名搜尋圖書資訊
     * 
     * @param name 圖書名稱
     * @return ResponseEntity 包含圖書資訊的 API 回應
     */
    @GetMapping("/search/name")
    public ResponseEntity<ApiResponse<Book>> getBookByName(@RequestParam String name) {
        logger.debug("收到根據書名查詢圖書請求: {}", name);
        
        try {
            return bookService.findByName(name)
                    .map(book -> {
                        logger.info("成功查詢圖書 - 書名: {}, ISBN: {}", name, book.getIsbn());
                        return ResponseEntity.ok(ApiResponse.success("查詢成功", book));
                    })
                    .orElseGet(() -> {
                        logger.warn("未找到圖書 - 書名: {}", name);
                        return ResponseEntity.ok(ApiResponse.error("書籍不存在", "BOOK_NOT_FOUND"));
                    });
        } catch (RuntimeException e) {
            logger.warn("根據書名查詢圖書失敗 - 業務邏輯錯誤: 書名: {}, 錯誤: {}", name, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BOOK_QUERY_ERROR"));
        } catch (Exception e) {
            logger.error("根據書名查詢圖書失敗 - 系統錯誤: 書名: {}, 錯誤: {}", name, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("查詢失敗，請稍後再試", "SYSTEM_ERROR"));
        }
    }
    
    /**
     * 根據作者查詢圖書
     * 
     * 透過作者姓名搜尋圖書資訊
     * 
     * @param author 作者姓名
     * @return ResponseEntity 包含圖書資訊的 API 回應
     */
    @GetMapping("/search/author")
    public ResponseEntity<ApiResponse<Book>> getBookByAuthor(@RequestParam String author) {
        logger.debug("收到根據作者查詢圖書請求: {}", author);
        
        try {
            return bookService.findByAuthor(author)
                    .map(book -> {
                        logger.info("成功查詢圖書 - 作者: {}, ISBN: {}", author, book.getIsbn());
                        return ResponseEntity.ok(ApiResponse.success("查詢成功", book));
                    })
                    .orElseGet(() -> {
                        logger.warn("未找到圖書 - 作者: {}", author);
                        return ResponseEntity.ok(ApiResponse.error("書籍不存在", "BOOK_NOT_FOUND"));
                    });
        } catch (RuntimeException e) {
            logger.warn("根據作者查詢圖書失敗 - 業務邏輯錯誤: 作者: {}, 錯誤: {}", author, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BOOK_QUERY_ERROR"));
        } catch (Exception e) {
            logger.error("根據作者查詢圖書失敗 - 系統錯誤: 作者: {}, 錯誤: {}", author, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("查詢失敗，請稍後再試", "SYSTEM_ERROR"));
        }
    }
    
    /**
     * 查詢所有圖書（包含庫存資訊）
     * 
     * 回傳系統中所有圖書的清單，每本書都會附帶庫存狀態資訊
     * 
     * @return ResponseEntity 包含圖書列表的 API 回應
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAllBooks() {
        logger.debug("收到查詢所有圖書請求");
        
        try {
            List<InventoryResponse> books = bookService.getAllBooksWithInventory();
            logger.info("成功查詢所有圖書 - 數量: {}", books.size());
            return ResponseEntity.ok(ApiResponse.success("查詢成功", books));
        } catch (RuntimeException e) {
            logger.warn("查詢所有圖書失敗 - 業務邏輯錯誤: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BOOKS_QUERY_ERROR"));
        } catch (Exception e) {
            logger.error("查詢所有圖書失敗 - 系統錯誤: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("查詢失敗，請稍後再試", "SYSTEM_ERROR"));
        }
    }
    
    /**
     * 添加新圖書（測試功能）
     * 
     * 新增圖書到系統中，用於測試目的
     * 
     * @param request 包含圖書資訊的請求物件
     * @return ResponseEntity 包含新增結果的 API 回應
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Book>> addBook(@RequestBody Map<String, Object> request) {
        logger.info("收到添加圖書請求 - ISBN: {}, 書名: {}", 
                   request.get("isbn"), request.get("name"));
        
        try {
            String isbn = (String) request.get("isbn");
            String name = (String) request.get("name");
            String author = (String) request.get("author");
            String introduction = (String) request.get("introduction");
            String imageUrl = (String) request.get("imageUrl");
            
            Book book = bookService.addBook(isbn, name, author, introduction, imageUrl);
            
            logger.info("成功添加圖書 - ISBN: {}, 書名: {}, 作者: {}", 
                       book.getIsbn(), book.getName(), book.getAuthor());
            
            return ResponseEntity.ok(ApiResponse.success("添加成功", book));
        } catch (RuntimeException e) {
            logger.warn("添加圖書失敗 - 業務邏輯錯誤: ISBN: {}, 錯誤: {}", 
                       request.get("isbn"), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BOOK_ADD_ERROR"));
        } catch (Exception e) {
            logger.error("添加圖書失敗 - 系統錯誤: ISBN: {}, 錯誤: {}", 
                        request.get("isbn"), e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("添加失敗，請稍後再試", "SYSTEM_ERROR"));
        }
    }
    
    /**
     * 添加庫存項目（測試功能）
     * 
     * 為現有圖書添加庫存項目，用於測試目的
     * 
     * @param request 包含 ISBN 的請求物件
     * @return ResponseEntity 包含新增結果的 API 回應
     */
    @PostMapping("/inventory/add")
    public ResponseEntity<ApiResponse<Inventory>> addInventory(@RequestBody Map<String, Object> request) {
        logger.info("收到添加庫存請求 - ISBN: {}", request.get("isbn"));
        
        try {
            String isbn = (String) request.get("isbn");
            Inventory inventory = bookService.addInventory(isbn);
            
            logger.info("成功添加庫存 - 庫存ID: {}, ISBN: {}", 
                       inventory.getInventoryId(), inventory.getIsbn());
            
            return ResponseEntity.ok(ApiResponse.success("添加成功", inventory));
        } catch (RuntimeException e) {
            logger.warn("添加庫存失敗 - 業務邏輯錯誤: ISBN: {}, 錯誤: {}", 
                       request.get("isbn"), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "INVENTORY_ADD_ERROR"));
        } catch (Exception e) {
            logger.error("添加庫存失敗 - 系統錯誤: ISBN: {}, 錯誤: {}", 
                        request.get("isbn"), e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("添加失敗，請稍後再試", "SYSTEM_ERROR"));
        }
    }
} 
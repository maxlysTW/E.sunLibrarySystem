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
 * @author E.sun Library System Team
 * @version 1.0
 * @since 2025
 */
package Library.System.controller;

import java.util.List;
import java.util.Map;

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
        try {
            List<InventoryResponse> books = bookService.getAllBooksWithInventory();
            return ResponseEntity.ok(ApiResponse.success("查詢成功", books));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BOOKS_QUERY_ERROR"));
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
        try {
            return bookService.findByIsbn(isbn)
                    .map(book -> ResponseEntity.ok(ApiResponse.success("查詢成功", book)))
                    .orElse(ResponseEntity.ok(ApiResponse.error("書籍不存在", "BOOK_NOT_FOUND")));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BOOK_QUERY_ERROR"));
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
        try {
            return bookService.findByName(name)
                    .map(book -> ResponseEntity.ok(ApiResponse.success("查詢成功", book)))
                    .orElse(ResponseEntity.ok(ApiResponse.error("書籍不存在", "BOOK_NOT_FOUND")));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BOOK_SEARCH_ERROR"));
        }
    }
    
    /**
     * 根據作者查詢圖書
     * 
     * 透過作者姓名搜尋該作者的圖書作品
     * 
     * @param author 作者姓名
     * @return ResponseEntity 包含圖書資訊的 API 回應
     */
    @GetMapping("/search/author")
    public ResponseEntity<ApiResponse<Book>> getBookByAuthor(@RequestParam String author) {
        try {
            return bookService.findByAuthor(author)
                    .map(book -> ResponseEntity.ok(ApiResponse.success("查詢成功", book)))
                    .orElse(ResponseEntity.ok(ApiResponse.error("書籍不存在", "BOOK_NOT_FOUND")));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BOOK_SEARCH_ERROR"));
        }
    }
    
    /**
     * 查詢所有圖書（包含庫存資訊）
     * 
     * 回傳系統中所有圖書的完整清單，包含每本書的庫存狀態
     * 
     * @return ResponseEntity 包含所有圖書列表的 API 回應
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAllBooks() {
        try {
            List<InventoryResponse> books = bookService.getAllBooksWithInventory();
            return ResponseEntity.ok(ApiResponse.success("查詢成功", books));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BOOKS_QUERY_ERROR"));
        }
    }
    
    /**
     * 添加新圖書到系統中（測試功能）
     * 
     * 接收圖書基本資訊並創建新的圖書記錄
     * 
     * @param request 包含圖書資訊的請求物件
     * @return ResponseEntity 包含新增圖書資訊的 API 回應
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Book>> addBook(@RequestBody Map<String, Object> request) {
        try {
            String isbn = (String) request.get("isbn");
            String name = (String) request.get("name");
            String author = (String) request.get("author");
            String introduction = (String) request.get("introduction");
            String imageUrl = (String) request.get("imageUrl");
            
            Book book = bookService.addBook(isbn, name, author, introduction, imageUrl);
            return ResponseEntity.ok(ApiResponse.success("書籍添加成功", book));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BOOK_ADD_ERROR"));
        }
    }
    
    /**
     * 為指定圖書添加庫存項目（測試功能）
     * 
     * 為已存在的圖書創建新的庫存記錄，增加可借閱的數量
     * 
     * @param request 包含 ISBN 的請求物件
     * @return ResponseEntity 包含新增庫存資訊的 API 回應
     */
    @PostMapping("/inventory/add")
    public ResponseEntity<ApiResponse<Inventory>> addInventory(@RequestBody Map<String, Object> request) {
        try {
            String isbn = (String) request.get("isbn");
            Inventory inventory = bookService.addInventory(isbn);
            return ResponseEntity.ok(ApiResponse.success("庫存添加成功", inventory));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "INVENTORY_ADD_ERROR"));
        }
    }
} 
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
    
    @Autowired
    private BookService bookService;
    
    /**
     * 查詢所有可借閱的書籍
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAvailableBooks() {
        try {
            List<InventoryResponse> books = bookService.getAvailableBooks();
            return ResponseEntity.ok(ApiResponse.success("查詢成功", books));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BOOKS_QUERY_ERROR"));
        }
    }
    
    /**
     * 根據 ISBN 查詢書籍
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
     * 根據書名查詢書籍
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
     * 根據作者查詢書籍
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
     * 查詢所有書籍
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Book>>> getAllBooks() {
        try {
            List<Book> books = bookService.findAllBooks();
            return ResponseEntity.ok(ApiResponse.success("查詢成功", books));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "BOOKS_QUERY_ERROR"));
        }
    }
    
    /**
     * 添加書籍（測試用）
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
     * 添加庫存（測試用）
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
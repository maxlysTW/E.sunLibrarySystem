package Library.System.controller;

import Library.System.entity.Inventory;
import Library.System.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:5173")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    /**
     * 查詢所有可借閱的書籍
     */
    @GetMapping("/available")
    public ResponseEntity<List<Inventory>> getAvailableBooks() {
        List<Inventory> books = bookService.getAvailableBooks();
        return ResponseEntity.ok(books);
    }
    
    /**
     * 根據 ISBN 查詢書籍
     */
    @GetMapping("/{isbn}")
    public ResponseEntity<?> getBookByIsbn(@PathVariable String isbn) {
        return bookService.findByIsbn(isbn)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根據書名查詢書籍
     */
    @GetMapping("/search/name")
    public ResponseEntity<?> getBookByName(@RequestParam String name) {
        return bookService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根據作者查詢書籍
     */
    @GetMapping("/search/author")
    public ResponseEntity<?> getBookByAuthor(@RequestParam String author) {
        return bookService.findByAuthor(author)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 查詢所有書籍
     */
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllBooks() {
        List<Inventory> books = bookService.getAvailableBooks();
        return ResponseEntity.ok(books);
    }
} 
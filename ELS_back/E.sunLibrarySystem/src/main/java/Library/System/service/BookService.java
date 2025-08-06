package Library.System.service;

import Library.System.entity.Book;
import Library.System.entity.Inventory;
import Library.System.repository.BookRepository;
import Library.System.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    /**
     * 查詢所有可借閱的書籍
     */
    public List<Inventory> getAvailableBooks() {
        return inventoryRepository.findAvailableBooks();
    }
    
    /**
     * 根據 ISBN 查詢書籍
     */
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findById(isbn);
    }
    
    /**
     * 根據書名查詢書籍
     */
    public Optional<Book> findByName(String name) {
        return bookRepository.findByName(name);
    }
    
    /**
     * 根據作者查詢書籍
     */
    public Optional<Book> findByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }
    
    /**
     * 查詢所有書籍
     */
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }
    
    /**
     * 檢查書籍是否可借閱
     */
    public boolean isBookAvailable(Integer inventoryId) {
        return inventoryRepository.isBookAvailable(inventoryId);
    }
} 
package Library.System.repository;

import Library.System.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    
    /**
     * 根據書名查詢書籍
     */
    Optional<Book> findByName(String name);
    
    /**
     * 根據作者查詢書籍
     */
    Optional<Book> findByAuthor(String author);
} 
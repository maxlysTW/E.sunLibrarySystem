package Library.System.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import Library.System.entity.Book;
import Library.System.entity.Inventory;
import Library.System.repository.BookRepository;
import Library.System.repository.InventoryRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        // 檢查是否已有資料
        if (bookRepository.count() == 0) {
            initializeBooks();
        }
    }

    private void initializeBooks() {
        // 創建書籍
        Book book1 = new Book();
        book1.setIsbn("9789863128359");
        book1.setName("AI Artificial Intelligence Introduction");
        book1.setAuthor("Luo Guangzhi");
        book1.setIntroduction("This is an introductory book about artificial intelligence, suitable for beginners.");
        book1.setImageUrl("https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/b2b/newItem/2025/07/17/172_111600119_629_mainCoverImage1.jpg");
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setIsbn("9786264250559");
        book2.setName("Cybersecurity Handbook");
        book2.setAuthor("Steve Wilson");
        book2.setIntroduction("This is a practical handbook about information security.");
        book2.setImageUrl("https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/b2b/newItem/2025/06/24/8113_112533581_874_mainCoverImage1.jpg");
        bookRepository.save(book2);

        Book book3 = new Book();
        book3.setIsbn("9789865022686");
        book3.setName("Java Programming");
        book3.setAuthor("Cai Wenlong");
        book3.setIntroduction("This is a basic Java programming textbook.");
        book3.setImageUrl("https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/Upload/Product/201910/o/637056885942171250.jpg");
        bookRepository.save(book3);

        // 創建庫存
        Inventory inventory1 = new Inventory("9789863128359", "Available");
        inventoryRepository.save(inventory1);

        Inventory inventory2 = new Inventory("9786264250559", "Available");
        inventoryRepository.save(inventory2);

        Inventory inventory3 = new Inventory("9789865022686", "Available");
        inventoryRepository.save(inventory3);

        System.out.println("資料初始化完成！");
    }
} 
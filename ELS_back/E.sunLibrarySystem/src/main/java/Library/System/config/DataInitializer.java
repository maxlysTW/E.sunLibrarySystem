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
        book1.setName("生成式⇄AI: 52個零程式互動體驗, 打造新世代人工智慧素養");
        book1.setAuthor("羅光志");
        book1.setIntroduction("在 AI 模型不停推陳出新，生成的內容越來越逼真，你是不是也開始對 AI 躍躍欲試，但卻又不得其門而入呢？這本書的誕生，就是為了讓你我都能看得懂、用得上、不被落下。");
        book1.setImageUrl("https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/b2b/newItem/2025/07/17/172_111600119_629_mainCoverImage1.jpg");
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setIsbn("9786264250559");
        book2.setName("LLM資安教戰手冊: 打造安全的AI應用程式");
        book2.setAuthor("Steve Wilson");
        book2.setIntroduction("AI工具爆炸性成長，從ChatGPT到企業內部專屬LLM，生成式AI已大量融入我們的生活與工作。但我們真的準備好「安全上線」了嗎？");
        book2.setImageUrl("https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/b2b/newItem/2025/06/24/8113_112533581_874_mainCoverImage1.jpg");
        bookRepository.save(book2);

        Book book3 = new Book();
        book3.setIsbn("9789865022686");
        book3.setName("Java SE 12基礎必修課");
        book3.setAuthor("蔡文龍/ 何嘉益/ 張志成/ 張力元");
        book3.setIntroduction("專家與教師共同執筆，由OCJP（原SCJP）與MTA Java認證講師、科技大學教授程式設計教師共同編著，針對初學者學習程式設計所編寫的入門教材。");
        book3.setImageUrl("https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/Upload/Product/201910/o/637056885942171250.jpg");
        bookRepository.save(book3);

        // 創建庫存
        Inventory inventory1 = new Inventory("9789863128359", "在庫");
        inventoryRepository.save(inventory1);

        Inventory inventory2 = new Inventory("9786264250559", "在庫");
        inventoryRepository.save(inventory2);

        Inventory inventory3 = new Inventory("9789865022686", "在庫");
        inventoryRepository.save(inventory3);

        System.out.println("資料初始化完成！");
    }
} 
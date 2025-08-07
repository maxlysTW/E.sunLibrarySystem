/**
 * 圖書實體類別 - 對應資料庫中的 books 表
 * 
 * 此實體類別代表圖書館系統中的圖書資料，包含以下功能：
 * 1. 圖書基本資訊管理（書名、作者、ISBN）
 * 2. 圖書詳細描述（簡介、封面圖片）
 * 3. 圖書唯一標識管理
 * 
 * 資料庫對應：
 * - 表名：books
 * - 主鍵：isbn (國際標準書號)
 * - 支援中文內容儲存
 * 
 * @author E.sun Library System Team
 * @version 1.0
 * @since 2025
 */
package Library.System.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "books")
public class Book {
    
    /** 國際標準書號，作為圖書的唯一識別碼和主鍵 */
    @Id
    @Column(name = "isbn", length = 13)
    private String isbn;
    
    /** 圖書名稱 */
    @Column(name = "name")
    private String name;
    
    /** 圖書作者 */
    @Column(name = "author")
    private String author;
    
    /** 圖書簡介，支援大量文字內容和中文字元 */
    @Column(name = "introduction", columnDefinition = "NVARCHAR(MAX)")
    private String introduction;
    
    /** 圖書封面圖片的URL連結 */
    @Column(name = "image_url")
    private String imageUrl;
    
    // Constructors
    
    /**
     * 預設建構子
     */
    public Book() {}
    
    /**
     * 建構子 - 用於建立新圖書
     * 
     * @param isbn ISBN 國際標準書號
     * @param name 圖書名稱
     * @param author 作者
     * @param introduction 圖書簡介
     * @param imageUrl 封面圖片URL
     */
    public Book(String isbn, String name, String author, String introduction, String imageUrl) {
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.introduction = introduction;
        this.imageUrl = imageUrl;
    }
    
    // Getters and Setters
    
    /**
     * 取得ISBN
     * @return 國際標準書號
     */
    public String getIsbn() {
        return isbn;
    }
    
    /**
     * 設定ISBN
     * @param isbn 國際標準書號
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    /**
     * 取得圖書名稱
     * @return 圖書名稱
     */
    public String getName() {
        return name;
    }
    
    /**
     * 設定圖書名稱
     * @param name 圖書名稱
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 取得作者
     * @return 圖書作者
     */
    public String getAuthor() {
        return author;
    }
    
    /**
     * 設定作者
     * @param author 圖書作者
     */
    public void setAuthor(String author) {
        this.author = author;
    }
    
    /**
     * 取得圖書簡介
     * @return 圖書簡介內容
     */
    public String getIntroduction() {
        return introduction;
    }
    
    /**
     * 設定圖書簡介
     * @param introduction 圖書簡介內容
     */
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    
    /**
     * 取得封面圖片URL
     * @return 圖書封面圖片連結
     */
    public String getImageUrl() {
        return imageUrl;
    }
    
    /**
     * 設定封面圖片URL
     * @param imageUrl 圖書封面圖片連結
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
} 
package Library.System.dto;

public class BookResponse {
    private String isbn;
    private String name;
    private String author;
    private String introduction;
    private String imageUrl;

    public BookResponse() {}

    public BookResponse(String isbn, String name, String author, String introduction, String imageUrl) {
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.introduction = introduction;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
} 
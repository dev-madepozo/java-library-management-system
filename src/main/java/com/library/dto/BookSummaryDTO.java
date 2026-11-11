package com.library.dto;

public class BookSummaryDTO {

    private String title;
    private String isbn;
    private String authorName;

    public BookSummaryDTO(String title, String isbn, String authorName) {
        this.title = title;
        this.isbn = isbn;
        this.authorName = authorName;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getAuthorName() {
        return authorName;
    }
}

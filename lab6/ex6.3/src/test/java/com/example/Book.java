package com.example;

import java.time.LocalDate;

public class Book {
    private final String title;
    private final String author;
    private final String category;
    private final LocalDate published;

    public Book(String title, String author, String category, LocalDate published) {
        this.title = title == null ? "" : title;
        this.author = author == null ? "" : author;
        this.category = category == null ? "" : category;
        this.published = published;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getPublished() {
        return published;
    }
}

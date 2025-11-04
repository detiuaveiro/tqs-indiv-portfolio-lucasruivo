package com.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Library {

    private final List<Book> books = new ArrayList<>();

    public void addBook(Book b) {
        if (b != null) books.add(b);
    }

    public void clear() {
        books.clear();
    }

    public List<Book> findByAuthor(String author) {
        return books.stream()
                .filter(b -> b.getAuthor() != null && b.getAuthor().equals(author))
                .collect(Collectors.toList());
    }

    public List<Book> findByCategory(String category) {
        return books.stream()
                .filter(b -> b.getCategory() != null && b.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public List<Book> findByTitle(String title) {
        return books.stream()
                .filter(b -> b.getTitle() != null && b.getTitle().equals(title))
                .collect(Collectors.toList());
    }

    public List<Book> findByTitleContaining(String text) {
        return books.stream()
                .filter(b -> b.getTitle() != null && b.getTitle().contains(text))
                .collect(Collectors.toList());
    }

    public List<Book> findByAuthorAndCategory(String author, String category) {
        return books.stream()
                .filter(b -> b.getAuthor() != null && b.getAuthor().equals(author))
                .filter(b -> b.getCategory() != null && b.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public List<Book> findByPublicationBetween(LocalDate from, LocalDate to) {
        return books.stream()
                .filter(b -> b.getPublished() != null)
                .filter(b -> (b.getPublished().isEqual(from) || b.getPublished().isAfter(from))
                        && (b.getPublished().isEqual(to) || b.getPublished().isBefore(to)))
                .collect(Collectors.toList());
    }

    public List<Book> getAll() {
        return new ArrayList<>(books);
    }
}

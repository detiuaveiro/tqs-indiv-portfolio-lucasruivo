package com.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StepDefinitions {

    private Library library;
    private List<Book> lastSearchResults;
    private String lastMessage;

    @Before
    public void setup() {
        library = new Library();
        lastSearchResults = new ArrayList<>();
        lastMessage = "";
    }

    @ParameterType("([0-9]{4}-[0-9]{2}-[0-9]{2})")
    public LocalDate iso8601Date(String date) {
        return LocalDate.parse(date);
    }

    @Given("an empty book database")
    public void anEmptyBookDatabase() {
        library.clear();
        lastSearchResults.clear();
        lastMessage = "";
    }

    @Given("the following books exist:")
    public void theFollowingBooksExist(DataTable table) {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String title = row.getOrDefault("title", "").trim();
            String author = row.getOrDefault("author", "").trim();
            String category = row.getOrDefault("category", "").trim();
            String published = row.getOrDefault("published", "").trim();
            LocalDate date = published.isEmpty() ? null : LocalDate.parse(published);
            Book b = new Book(title, author, category, date);
            library.addBook(b);
        }
    }

    @When("I search for books by author {string}")
    public void iSearchForBooksByAuthor(String author) {
        lastSearchResults = library.findByAuthor(author);
        if (lastSearchResults.isEmpty()) lastMessage = "No books found";
    }

    @When("I search for books in the {string} category")
    public void iSearchForBooksInTheCategory(String category) {
        lastSearchResults = library.findByCategory(category);
        if (lastSearchResults.isEmpty()) lastMessage = "No books found";
    }

    @When("I search for books with the title {string}")
    public void iSearchForBooksWithTheTitle(String title) {
        lastSearchResults = library.findByTitle(title);
        if (lastSearchResults.isEmpty()) lastMessage = "No books found";
    }

    @When("I search for books with the title containing {string}")
    public void iSearchForBooksWithTheTitleContaining(String text) {
        lastSearchResults = library.findByTitleContaining(text);
        if (lastSearchResults.isEmpty()) lastMessage = "No books found";
    }

    @When("I search for books by author {string} and category {string}")
    public void iSearchForBooksByAuthorAndCategory(String author, String category) {
        lastSearchResults = library.findByAuthorAndCategory(author, category);
        if (lastSearchResults.isEmpty()) lastMessage = "No books found";
    }

    @When("the customer searches for books published between {iso8601Date} and {iso8601Date}")
    public void theCustomerSearchesForBooksPublishedBetween(LocalDate from, LocalDate to) {
        lastSearchResults = library.findByPublicationBetween(from, to);
        if (lastSearchResults.isEmpty()) lastMessage = "No books found";
    }

    @Then("I should see {int} book\\(s\\) in the results")
    public void iShouldSeeNumberBooksInTheResults(Integer expected) {
        assertEquals(expected.intValue(), lastSearchResults.size());
    }

    @Then("each book should have author {string}")
    public void eachBookShouldHaveAuthor(String author) {
        for (Book b : lastSearchResults) {
            assertEquals(author, b.getAuthor());
        }
    }

    @Then("each book should be in category {string}")
    public void eachBookShouldBeInCategory(String category) {
        for (Book b : lastSearchResults) {
            assertEquals(category, b.getCategory());
        }
    }

    @Then("the result should contain the book titled {string}")
    public void theResultShouldContainTheBookTitled(String title) {
        boolean found = lastSearchResults.stream().anyMatch(b -> title.equals(b.getTitle()));
        assertTrue(found, "Expected to find book titled: " + title);
    }

    @Then("I should see a message {string}")
    public void iShouldSeeAMessage(String message) {
        assertEquals(message, lastMessage);
    }

}

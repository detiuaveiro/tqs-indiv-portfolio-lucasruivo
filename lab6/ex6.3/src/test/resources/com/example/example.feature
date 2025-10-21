Feature: Book Search

  As a user of the book search system,
  I want to search for books using various criteria,
  So that I can find books that match my interests.

  Scenario: Search for books by author
    Given the book database contains books by "J.K. Rowling"
    When I search for books by author "J.K. Rowling"
    Then I should see a list of books written by "J.K. Rowling"

  Scenario: Search for books by category
    Given the book database contains books in the "Fantasy" category
    When I search for books in the "Fantasy" category
    Then I should see a list of books in the "Fantasy" category

  Scenario: Search for books with no results
    Given the book database does not contain books by "Unknown Author"
    When I search for books by author "Unknown Author"
    Then I should see a message "No books found"

  Scenario: Search for books by title
    Given the book database contains a book titled "The Hobbit"
    When I search for books with the title "The Hobbit"
    Then I should see the book "The Hobbit" in the results

  Scenario: Search for books by partial title
    Given the book database contains a book titled "The Lord of the Rings"
    When I search for books with the title containing "Lord"
    Then I should see the book "The Lord of the Rings" in the results

  Scenario: Search for books by multiple criteria
    Given the book database contains books by "George Orwell" in the "Dystopian" category
    When I search for books by author "George Orwell" and category "Dystopian"
    Then I should see a list of books written by "George Orwell" in the "Dystopian" category
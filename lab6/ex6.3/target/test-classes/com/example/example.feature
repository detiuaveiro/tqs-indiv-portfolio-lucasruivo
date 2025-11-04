Feature: Book Search

  As a user of the book search system,
  I want to search for books using various criteria,
  So that I can find books that match my interests.

  # Populate the database using a DataTable for convenience
  Background: the library is empty
    Given an empty book database

  Scenario: Search for books by author
    Given the following books exist:
      | title                        | author          | category   | published   |
      | Harry Potter and the Sorcerer| J.K. Rowling    | Fantasy    | 1997-06-26  |
      | A Brief History of Time      | Stephen Hawking | Science    | 1988-04-01  |
    When I search for books by author "J.K. Rowling"
    Then I should see 1 book(s) in the results
    And each book should have author "J.K. Rowling"

  Scenario: Search for books by category
    Given the following books exist:
      | title                | author         | category | published  |
      | The Hobbit           | J.R.R. Tolkien | Fantasy  | 1937-09-21 |
      | Dune                 | Frank Herbert  | Sci-Fi   | 1965-08-01 |
    When I search for books in the "Fantasy" category
    Then I should see 1 book(s) in the results
    And each book should be in category "Fantasy"

  Scenario: Search for books with no results
    Given the following books exist:
      | title        | author        | category | published  |
      | Some Book    | Some Author   | Misc     | 2000-01-01 |
    When I search for books by author "Unknown Author"
    Then I should see 0 book(s) in the results

  Scenario: Search for books by title (exact)
    Given the following books exist:
      | title                  | author         | category | published  |
      | The Hobbit             | J.R.R. Tolkien | Fantasy  | 1937-09-21 |
    When I search for books with the title "The Hobbit"
    Then I should see 1 book(s) in the results
    And the result should contain the book titled "The Hobbit"

  Scenario: Search for books by partial title
    Given the following books exist:
      | title                        | author         | category | published  |
      | The Lord of the Rings        | J.R.R. Tolkien | Fantasy  | 1954-07-29 |
    When I search for books with the title containing "Lord"
    Then I should see 1 book(s) in the results
    And the result should contain the book titled "The Lord of the Rings"

  Scenario: Search for books by multiple criteria
    Given the following books exist:
      | title         | author         | category  | published  |
      | 1984          | George Orwell  | Dystopian | 1949-06-08 |
      | Animal Farm   | George Orwell  | Satire    | 1945-08-17 |
    When I search for books by author "George Orwell" and category "Dystopian"
    Then I should see 1 book(s) in the results

  Scenario: Search books published between two dates
    Given the following books exist:
      | title              | author          | category | published  |
      | Old Book           | Old Author      | History  | 1900-01-01 |
      | Mid Century Book   | Mid Author      | History  | 1955-05-05 |
      | Modern Book        | Modern Author   | History  | 2005-09-09 |
    When the customer searches for books published between 1950-01-01 and 2010-12-31
    Then I should see 2 book(s) in the results

  Scenario: Populate using DataTable and filter by date range
    Given the following books exist:
      | title               | author        | category | published  |
      | One good book       | Anonymous     | Fiction  | 2013-03-12 |
      | Some other book     | Tim Tomson    | Fiction  | 2020-08-23 |
    When the customer searches for books published between 2010-01-01 and 2015-12-31
    Then I should see 1 book(s) in the results

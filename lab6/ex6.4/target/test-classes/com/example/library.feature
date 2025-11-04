Feature: Web Book Search (fake online library)

  As a user of the online library UI,
  I want to search for books using the web interface,
  So that I can find books that match my interests.

  Background: the library webapp is available
    Given the library webapp is open

  Scenario: Search for books by author using the UI
    Given the following books exist on the page:
      | title                       | author          | category | published  |
      | Harry Potter and the Sorcerer| J.K. Rowling    | Fantasy  | 1997-06-26 |
      | A Brief History of Time     | Stephen Hawking | Science  | 1988-04-01 |
    When I search for books by author "J.K. Rowling" using the UI
    Then I should see 1 result(s) in the UI

  Scenario: Search for books by category using the UI
    Given the following books exist on the page:
      | title      | author         | category | published  |
      | The Hobbit | J.R.R. Tolkien | Fantasy  | 1937-09-21 |
      | Dune       | Frank Herbert  | Sci-Fi   | 1965-08-01 |
    When I search for books in the "Fantasy" category using the UI
    Then I should see 1 result(s) in the UI

  Scenario: Search for books with no results using the UI
    Given the following books exist on the page:
      | title     | author      | category | published  |
      | Some Book | Some Author | Misc     | 2000-01-01 |
    When I search for books by author "Unknown Author" using the UI
    Then I should see 0 result(s) in the UI

  Scenario: Search books published between two dates using the UI
    Given the following books exist on the page:
      | title              | author        | category | published  |
      | Old Book           | Old Author    | History  | 1900-01-01 |
      | Mid Century Book   | Mid Author    | History  | 1955-05-05 |
      | Modern Book        | Modern Author | History  | 2005-09-09 |
    When I search for books published between 1950-01-01 and 2010-12-31 using the UI
    Then I should see 2 result(s) in the UI

Feature: Basic Arithmetic
  Background: A Calculator
  Given a calculator I just turned on

  Scenario: add numbers
    When I add 1 and 2
    Then the result is 3

  Scenario: Subtraction
    When I subtract 7 to 2
    Then the result is 5

  Scenario: Multiply numbers
    When I multiply 3 by 4
    Then the result is 12

  Scenario: Divide numbers
    When I divide 10 by 2
    Then the result is 5

  Scenario: Divide by zero
    When I divide 10 by 0
    Then the result is undefined

  Scenario Outline: Several additions
    When I add <a> and <b>
    Then the result is <c>
    Examples: Single digits
      | a | b | c |
      | 1 | 2 | 3 |
      | 3 | 7 | 10 |
      | -3 | -4 | -7 |

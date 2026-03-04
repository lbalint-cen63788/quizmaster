Feature: Take a numerical question
  Numerical questions accept a typed number as the answer instead of selecting
  from choices. The user sees a number input field and receives correct/incorrect
  feedback based on the entered value.

  Background:
    Given numerical question "How many regions does Czechia have?" with correct answer "14"

  Scenario Outline: Numerical question feedback
    When I take question "How many regions does Czechia have?"
    Then I see a number input
    When I enter "<answer>"
    Then I see feedback "<feedback>"

    Examples:
      | answer | feedback   |
      | 48     | Incorrect! |
      | 14     | Correct!   |

  Scenario Outline: Quizmaker-created numerical question uses number input for quiz taker
    Given a numerical question "How many regions does Czechia have?" with correct answer "14" bookmarked as "regions"
    When I take question "regions"
    Then I see a number input
    When I enter "<answer>"
    Then I see feedback "<feedback>"

    Examples:
      | answer | feedback   |
      | 11     | Incorrect! |
      | 14     | Correct!   |

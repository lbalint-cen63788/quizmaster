Feature: Quiz answer state after navigation

  Background:
    Given workspace "Regression" with questions
      | bookmark | question                            | answers                              |
      | Sky      | What is the standard colour of sky? | Red, Blue (*), Green, Black          |
      | France   | What is capital of France?          | Marseille, Lyon, Paris (*), Toulouse |
    And a quiz "Quiz" with all questions
      | pass score | 85 |

  Scenario: Quiz question is displayed and not answered
    Given I start quiz "Quiz"
    Then I see question "Sky"
    And no answer is selected

  Scenario: After page refresh no answer is selected
    Given I start quiz "Quiz"
    When I answer "Green"
    And I refresh page
    Then no answer is selected

  Scenario: After next page is displayed, no answer and explanation is displayed
    Given I start quiz "Quiz"
    When I answer "Green"
    Then no answer is selected

  Scenario: After completing the quiz and seeing the result and updating the url to the same quiz and clicking "Start", the quiz starts from the beginning
    Given I start quiz "Quiz"
    When I answer "Green"
    And I answer "Paris"
    And I proceed to the score page
    And I start quiz "Quiz"
    Then I see question "Sky"
    And no answer is selected

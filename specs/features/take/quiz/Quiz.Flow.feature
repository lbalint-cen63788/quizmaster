Feature: Quiz navigation flow

  Background:
    Given workspace "Flow" with questions
      | question                    | answers                                            |
      | Which animal has long nose? | Elephant (*), Anteater (*), Swordfish (*), Bulldog |
      | What is capital of France?  | Marseille, Lyon, Paris (*), Toulouse               |
    And a quiz "Quiz" with all questions

  Scenario: Quiz question is not answered and the next button is clicked
    When I start quiz "Quiz"
    And I skip the question
    Then I see question "What is capital of France?"
    And I see bookmark to previous question "Which animal has long nose?"
    And I see bookmark link "Which animal has long nose?"

  Scenario: User proceed to last question
    When I start quiz "Quiz"
    And I answer "Elephant"
    Then I see question "What is capital of France?"
    Then I see buttons "Back"
    When I answer "Lyon"
    Then I see buttons "Back, Evaluate"

  Scenario: User navigate to evaluation page
    When I start quiz "Quiz"
    And I answer "Elephant"
    Then I see question "What is capital of France?"
    Then I see buttons "Back"
    When I answer "Lyon"
    When I evaluate the quiz

  Scenario: User reloads page on answered question
    When I start quiz "Quiz"
    And I answer "Elephant"
    * I check answer "Lyon,Paris"
    * I uncheck answer "Lyon"
    * I refresh the page
    Then no answer is selected

  Scenario: Back button is not visible on the quiz page
    When I start quiz "Quiz"
    Then I see buttons "Next"

  Scenario: Back button is visible
    When I start quiz "Quiz"
    And I answer "Elephant"
    Then I see buttons "Back"

  Scenario: Back button is clicked
    When I start quiz "Quiz"
    And I answer "Elephant"
    Then I see question "What is capital of France?"
    And I go back to previous question
    Then I see question "Which animal has long nose?"

  Scenario: Last question is not answered and there are any skipped questions
    When I start quiz "Quiz"
    And I skip the question
    Then I see question "What is capital of France?"
    Then I see buttons "Back, Next"

  Scenario: Last question is answered and there are any skipped questions
    When I start quiz "Quiz"
    And I skip the question
    Then I see question "What is capital of France?"
    When I answer "Paris"
    Then I see buttons "Back, Next"

  Scenario: Last question is answered and show skipped question
    When I start quiz "Quiz"
    And I skip the question
    Then I see question "What is capital of France?"
    When I answer "Paris"
    Then I see buttons "Back, Next"
    When I proceed to the next question
    Then I see question "Which animal has long nose?"

  Scenario: Last question is skipped and there are any skipped questions
    When I start quiz "Quiz"
    And I skip the question
    Then I see question "What is capital of France?"
    Then I see buttons "Back, Next"
    When I skip the question
    Then I see question "Which animal has long nose?"

  Scenario: Do not show skipped question which was submitted
    When I start quiz "Quiz"
    And I skip the question
    Then I see question "What is capital of France?"
    When I answer "Paris"
    Then I see buttons "Back, Next"
    When I proceed to the next question
    Then I see question "Which animal has long nose?"
    When I answer "Elephant"
    Then I see buttons "Back, Evaluate"

  Scenario: Remembered answer after back button
    When I start quiz "Quiz"
    And I answer "Elephant"
    Then I see question "What is capital of France?"
    When I go back to previous question
    Then I see answer "Elephant" checked

  Scenario: Remembered multiple choices after back button
    When I start quiz "Quiz"
    Then I see question "Which animal has long nose?"
    When I answer "Elephant, Anteater"
    Then I see question "What is capital of France?"
    When I go back to previous question
    Then I see answer "Elephant" checked
    Then I see answer "Anteater" checked

  Scenario: Submit button is visible as active when answer is checked
    When I start quiz "Quiz"
    Then I see question "Which animal has long nose?"
    When I check answer "Elephant"
    Then I see the submit button as active

  Scenario: Submit button is visible as inactive when no answer is checked
    When I start quiz "Quiz"
    Then I see question "Which animal has long nose?"
    Then I see the submit button as inactive
    When I check answer "Elephant"
    Then I see the submit button as active
    When I uncheck answer "Elephant"
    Then I see the submit button as inactive

  Scenario: When proceeding answer is submitted
    When I start quiz "Quiz"
    Then I see question "Which animal has long nose?"
    When I check answer "Elephant"
    And I proceed to the next question
    Then I see question "What is capital of France?"
    When I go back to previous question
    Then I see answer "Elephant" checked

  @skip
  Scenario: When going back answer is submitted
    Given workspace "Flow 3Q" with questions
      | question                       | answers                                            |
      | Which animal has long nose?    | Elephant (*), Anteater (*), Swordfish (*), Bulldog |
      | What is capital of France?     | Marseille, Lyon, Paris (*), Toulouse               |
      | What is capital of Madagascar? | Antananarivo (*), Nairobi, Cairo, Dakar            |
    And a quiz "Three Questions" with all questions
    When I start quiz "Three Questions"
    Then I see question "Which animal has long nose?"
    And I skip the question
    Then I see question "What is capital of France?"
    And I check answer "Paris"
    When I skip the question
    Then I see question "What is capital of Madagascar?"
    When I go back to previous question
    Then I see answer "Paris" checked

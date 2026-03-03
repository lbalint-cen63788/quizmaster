Feature: Edit CR_Quiz from Workspace and mark questions belonging to the quiz

  Background:
    Given a quiz "CR_Quiz" with questions
      | question    | answers     |
      | Question 1? | answer1 , answer2 (*), answer3 |
      | Question 2? |  answer1 (*), answer2, answer3 |
      | Question 3? |  answer1, answer2 (*), answer3 |
      | Question 4? | answer1 , answer2 (*), answer3 |
@skip
Scenario Outline: Mark the questions belonging to the quiz
    When I navigate to edit quiz "CR_Quiz"
    And questions belonging to the quiz are marked
    Then I see question is marked "<selectedQuestion1>"
    And I see question is marked "<selectedQuestion2>"
    And I see question is not marked "<notselectedQuestion3>"
    And I see question is not marked "<notselectedQuestion4>"


 Examples:
      | selectedQuestion1      | selectedQuestion2               | notselectedQuestion3 | notselectedQuestion4 |
      |  Question 1?           | Question 2?                     |  Question 3?         | Question 4?          |

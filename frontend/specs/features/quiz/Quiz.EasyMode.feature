Feature: Take a quiz in EasyMode

  Background:
    Given questions
      | bookmark | question                                | answers                                           | easy mode |
      | Q1       | Which of these countries are in Europe? | Italy (*), France (*), Morocco, Spain (*), Canada | false     |
      | Q2       | Which of these countries are in Europe? | Italy (*), France (*), Morocco, Spain (*), Canada | true      |
      | Q3       | What is the capital of France?          | Paris (*), Lyon, Marseille                        |           |
    And quizes
      | bookmark                    | questions  | easy mode   |
      | QuizWithEasyModePerQuestion | Q1, Q2, Q3 | PerQuestion |
      | QuizWithEasyModeAlways      | Q1, Q2, Q3 | Always      |
      | QuizWithEasyModeNever       | Q1, Q2, Q3 | Never       |

  @skip
  Scenario: Quiz Easy Mode PerQuestion - question Easy Mode mixed
    When I start quiz "QuizWithEasyModePerQuestion"
    Then I do not see correct answers count
    When I proceed to the next question
    Then I see that question has number of correct answers displayed
    When I proceed to the next question
    Then I do not see correct answers count

  @skip
  Scenario: Quiz Easy Mode Always - overrides question Easy Mode mixed
    When I start quiz "QuizWithEasyModeAlways"
    Then I see that question has number of correct answers displayed
    When I proceed to the next question
    Then I see that question has number of correct answers displayed
    When I proceed to the next question
    Then I do not see correct answers count

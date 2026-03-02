Feature: Take a quiz with partial score

  Background:
    Given workspace "Partial Score" with questions
      | bookmark | question                                              | answers                                      | explanation |
      | Planets  | Which of the following are planets?                    | Mars (*), Pluto, Titan, Venus (*), Earth (*) | Planets     |
      | Sky      | What is the standard colour of sky?                   | Red, Blue (*), Green, Black                  | Rayleigh    |
    And a quiz "Quiz" with all questions
      | pass score | 75 |

  Scenario Outline: Quiz with multiple choice question with partial score
    Quiz is scored as follows:
    - all correct answers gives 1 point
    - one incorrect answer selected gives 0.5 point
    - more than one incorrect answer selected gives 0 point

    When I start the quiz
    And I answer "<answer>"
    And I answer "Blue"
    And I evaluate the quiz
    Then I see the result <correct> correct out of 2, <percentage>%, <result>, required passScore 75%

    Examples:
      | answer                    | correct | percentage | result |
      | Mars, Venus, Earth        | 2       | 100        | passed |
      | Mars, Venus, Titan, Earth | 1.5     | 75         | passed |
      | Mars, Venus               | 1.5     | 75         | passed |
      | Mars, Pluto               | 1       | 50         | failed |
      | Mars, Pluto, Venus, Titan | 1       | 50         | failed |
      | Pluto, Titan              | 1       | 50         | failed |

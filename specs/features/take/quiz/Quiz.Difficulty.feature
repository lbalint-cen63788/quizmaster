Feature: Quiz Difficulty
  Quiz-level difficulty setting overrides individual question difficulty.
  - Default: keeps each question's own difficulty setting
  - Easy: forces all questions to show correct answer count
  - Hard: hides correct answer count for all questions
  Note: Single choice questions never show the count since they always have one answer.

  Scenario Outline: Quiz Difficulty
    Quiz difficulty can override individual questions difficulty.
    - Default difficulty keeps each individual question difficulty.
    - Easy difficulty overrides all questions to easy.
    - Hard difficulty overrides all questions to hard.

    Note: Single choice questions never show the correct answers count, they're already easy.

    Given workspace "Difficulty" with questions
      | bookmark | easy  | question           | answers                         |
      | Single   |       | Capital of France? | Paris (*), Nice                 |
      | Easy     | true  | Food?              | Pork (*), Fish (*), Shoe        |
      | Hard     | false | Animal?            | Dog (*), Cat (*), Bird (*), Car |
    And a quiz "Quiz" with questions "Single, Easy, Hard"
      | difficulty | <difficulty> |
    When I start quiz "Quiz"
    And I progress through the questions
    Then I see the correct answers count
      | Single | <single> |
      | Easy   | <easy>   |
      | Hard   | <hard>   |

    Examples:
      | difficulty    | single | easy | hard |
      | Keep Question | -      | 2    | -    |
      | Easy          | -      | 2    | 3    |
      | Hard          | -      | -    | -    |

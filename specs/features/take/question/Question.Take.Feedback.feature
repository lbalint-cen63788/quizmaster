Feature: Question answer feedback
  After submitting an answer, the quiz taker receives feedback indicating
  whether their answer is correct or incorrect.
  - Single choice: overall Correct!/Incorrect! feedback
  - Multiple choice: overall feedback (all correct must be selected, no
    incorrect selected) plus per-answer color coding (green for correct,
    red for selected-incorrect, neutral for unselected-incorrect)

  Background:
    Given questions
      | bookmark  | question                              | answers                           |
      | Australia | What's the capital city of Australia? | Sydney, Canberra (*), Melbourne   |
      | Planets   | Which of the following are planets?   | Mars (*), Pluto, Venus (*), Titan |

  Scenario Outline: Single choice question feedback
    Question is answered correctly if the correct answer is selected

    When I take question "Australia"
    And I answer "<answer>"
    Then I see feedback "<feedback>"

    Examples:
      | answer   | feedback   |
      | Sydney   | Incorrect! |
      | Canberra | Correct!   |

  Scenario Outline: Multiple choice question feedback
    Question is answered correctly only if
    - all correct answers are selected, and,
    - no incorrect answers are selected

    When I take question "Planets"
    And I answer "<answer>"
    Then I see feedback "<feedback>"
    Examples:
      | answer                    | feedback   |
      | Mars, Venus               | Correct!   |
      | Mars, Venus, Titan        | Incorrect! |
      | Mars, Pluto               | Incorrect! |
      | Mars, Pluto, Venus, Titan | Incorrect! |
      | Pluto, Titan              | Incorrect! |

  Scenario Outline: Multiple choice question per-answer feedback
    Upon submitting the question, each answer is marked with a color:
    - green if the answer is correct
    - red if the answer is selected while incorrect
    - none if the answer is not selected while incorrect

    When I take question "Planets"
    And I answer "<answer>"
    Then I see individual color feedback per answer:
      | answer | color   |
      | Mars   | <mars>  |
      | Pluto  | <pluto> |
      | Venus  | <venus> |
      | Titan  | <titan> |

    Examples:
      | answer                    | mars  | pluto | venus | titan |
      | Mars, Venus               | 🟩🟩🟩 | ◼️◼️◼️ | 🟩🟩🟩 | ◼️◼️◼️ |
      | Mars, Venus, Titan        | 🟩🟩🟩 | ◼️◼️◼️ | 🟩🟩🟩 | 🟥🟥🟥 |
      | Mars, Pluto               | 🟩🟩🟩 | 🟥🟥🟥 | 🟩🟩🟩 | ◼️◼️◼️ |
      | Mars, Pluto, Venus, Titan | 🟩🟩🟩 | 🟥🟥🟥 | 🟩🟩🟩 | 🟥🟥🟥 |
      | Pluto, Titan              | 🟩🟩🟩 | 🟥🟥🟥 | 🟩🟩🟩 | 🟥🟥🟥 |

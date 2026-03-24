Feature: Show stats
  After completing a quiz, statistics are available showing results per attempt.
  The stats page displays a table with duration and score percentage. An empty
  stats page is shown when no attempts have been recorded yet.

  Scenario: Show empty stats page for quiz
    - Shows empty stats page for brand new created quiz.

    Given workspace "Stats Empty" with questions
      | question              | answers         |
      | Jaký nábytek má Ikea? | Stůl (*), Auto  |
      | Jaké nádobí má Ikea?  | Talíř (*), Kolo |
    And a quiz "Quiz" with all questions
    When I open stats for quiz "Quiz"
    And I see stats page for quiz "Quiz"

  Scenario: Show stats page for successfully answered quiz
    Given workspace "Stats Success" with questions
      | question              | answers         |
      | Jaký nábytek má Ikea? | Stůl (*), Auto  |
      | Jaké nádobí má Ikea?  | Talíř (*), Kolo |
    And a quiz "Stats Quiz" with all questions
    And I take quiz "Stats Quiz" with answers
      | question              | answers |
      | Jaký nábytek má Ikea? | Stůl    |
      | Jaké nádobí má Ikea?  | Talíř   |
    When I open stats for quiz "Stats Quiz"
    Then I see stats page for quiz "Stats Quiz"
    And I see stats table
      | Duration | Score |
      |          |   100 |

  Scenario: Show stats page for unsuccessfully answered quiz
    Given workspace "Stats Failure" with questions
      | question              | answers         |
      | Jaký nábytek má Ikea? | Stůl (*), Auto  |
      | Jaké nádobí má Ikea?  | Talíř (*), Kolo |
    And a quiz "Stats Quiz" with all questions
    And I take quiz "Stats Quiz" with answers
      | question              | answers |
      | Jaký nábytek má Ikea? | Auto    |
      | Jaké nádobí má Ikea?  | Kolo    |
    When I open stats for quiz "Stats Quiz"
    Then I see stats page for quiz "Stats Quiz"
    And I see stats table
      | Duration | Score |
      |          |     0 |

  Scenario: Duration is calculated correctly
    Given workspace "Stats Duration" with questions
      | question              | answers         |
      | Jaký nábytek má Ikea? | Stůl (*), Auto  |
      | Jaké nádobí má Ikea?  | Talíř (*), Kolo |
    And a quiz "Stats Quiz" with all questions
    When I take quiz "Stats Quiz" with answers in 10 seconds
      | question              | answers |
      | Jaký nábytek má Ikea? | Stůl    |
      | Jaké nádobí má Ikea?  | Kolo    |
    And I open stats for quiz "Stats Quiz"
    Then I see stats page for quiz "Stats Quiz"
    And I see stats table
      | Duration      |    |
      | 10 seconds    |    |


  Scenario: Show stats page with summary for successfully answered quiz
      Given workspace "Stats Success" with questions
        | question              | answers         |
        | Jaký nábytek má Ikea? | Stůl (*), Auto  |
        | Jaké nádobí má Ikea?  | Talíř (*), Kolo |
      And a quiz "Stats Quiz" with all questions
      And I take quiz "Stats Quiz" with answers
        | question              | answers |
        | Jaký nábytek má Ikea? | Stůl    |
        | Jaké nádobí má Ikea?  | Talíř   |
      When I open stats for quiz "Stats Quiz"
      Then I see stats page for quiz "Stats Quiz"
      And I see summary stats table
        | Summary  |          |
        | Started  | Finished |
        |       1  |        1 |
      And I see attempt stats table
        | Attempts |       |
        | Duration | Score |
        |          |   100 |

  Scenario: Show stats page with summary for timed out quiz
    Given workspace "Stats Success" with questions
      | question              | answers         |
      | Jaký nábytek má Ikea? | Stůl (*), Auto  |
      | Jaké nádobí má Ikea?  | Talíř (*), Kolo |
    And a quiz "Stats Quiz" with all questions
    And I take quiz "Stats Quiz" which I do not complete in time limit
      | question              | answers |
      | Jaký nábytek má Ikea? | Stůl    |
      | Jaké nádobí má Ikea?  |         |
    When I open stats for quiz "Stats Quiz"
    Then I see stats page for quiz "Stats Quiz"
    And I see summary stats table
      | Summary  |          |           |
      | Started  | Finished | Timeout   |
      |       1  |        0 |         1 |

  @skip
  Scenario: Show stats page with score 100/100
    Given workspace "Stats Perfect Score" with questions
      | question              | answers         |
      | Jaký nábytek má Ikea? | Stůl (*), Auto  |
      | Jaké nádobí má Ikea?  | Talíř (*), Kolo |
    And a quiz "Stats Quiz" with all questions
    And I take quiz "Stats Quiz" with answers
      | question              | answers |
      | Jaký nábytek má Ikea? | Stůl    |
      | Jaké nádobí má Ikea?  | Talíř   |
    When I open stats for quiz "Stats Quiz"
    Then I see stats page for quiz "Stats Quiz"
    And I see summary stats table
      | Summary |          |
      | Started | Finished |
      |       1 |        1 |
    And I see attempt stats table
      | Attempts |         |
      | Duration | Score   |
      |          | 100/100 |

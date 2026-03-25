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

  # Duration (individual): full, half, zero
  Scenario: Duration for full correct attempt
    Given workspace "Stats Duration Full" with questions
      | question              | answers         |
      | Jaký nábytek má Ikea? | Stůl (*), Auto  |
      | Jaké nádobí má Ikea?  | Talíř (*), Kolo |
    And a quiz "Stats Quiz" with all questions
    When I take quiz "Stats Quiz" with answers in 10 seconds
      | question              | answers |
      | Jaký nábytek má Ikea? | Stůl    |
      | Jaké nádobí má Ikea?  | Talíř   |
    And I open stats for quiz "Stats Quiz"
    Then I see stats page for quiz "Stats Quiz"
    And I see attempt stats table
      | Attempts   |          |                 |                   |       |
      | Duration   |          |                 |                   |       |
      | 10 seconds |          |                 |                   |       |

  Scenario: Duration for half correct attempt
    Given workspace "Stats Duration Half" with questions
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
    And I see attempt stats table
      | Attempts   |          |                 |                   |       |
      | Duration   |          |                 |                   |       |
      | 10 seconds |          |                 |                   |       |

  Scenario: Duration for zero correct attempt
    Given workspace "Stats Duration Zero" with questions
      | question              | answers         |
      | Jaký nábytek má Ikea? | Stůl (*), Auto  |
      | Jaké nádobí má Ikea?  | Talíř (*), Kolo |
    And a quiz "Stats Quiz" with all questions
    When I take quiz "Stats Quiz" with answers in 10 seconds
      | question              | answers |
      | Jaký nábytek má Ikea? | Auto    |
      | Jaké nádobí má Ikea?  | Kolo    |
    And I open stats for quiz "Stats Quiz"
    Then I see stats page for quiz "Stats Quiz"
    And I see attempt stats table
      | Attempts   |          |                 |                   |       |
      | Duration   |          |                 |                   |       |
      | 10 seconds |          |                 |                   |       |

  # Points (individual): full, half, zero
  Scenario: Points full 2/2
    Given workspace "Stats Points Full" with questions
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
    And I see attempt stats table
      | Attempts |          |                 |                   |       |
      |          | Points   |                 |                   |       |
      |          | 2/2      |                 |                   |       |

  Scenario: Points half 1/2
    Given workspace "Stats Points Half" with questions
      | question              | answers         |
      | Jaký nábytek má Ikea? | Stůl (*), Auto  |
      | Jaké nádobí má Ikea?  | Talíř (*), Kolo |
    And a quiz "Stats Quiz" with all questions
    And I take quiz "Stats Quiz" with answers
      | question              | answers |
      | Jaký nábytek má Ikea? | Stůl    |
      | Jaké nádobí má Ikea?  | Kolo    |
    When I open stats for quiz "Stats Quiz"
    Then I see stats page for quiz "Stats Quiz"
    And I see attempt stats table
      | Attempts |          |                 |                   |       |
      |          | Points   |                 |                   |       |
      |          | 1/2      |                 |                   |       |

  Scenario: Points zero 0/2
    Given workspace "Stats Points Zero" with questions
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
    And I see attempt stats table
      | Attempts |          |                 |                   |       |
      |          | Points   |                 |                   |       |
      |          | 0/2      |                 |                   |       |


  # Correct Answers (individual): full, half, zero
  Scenario: Correct Answers full 2 (100%)
    Given workspace "Stats Correct Full" with questions
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
    And I see attempt stats table
      | Attempts |          |                 |                   |       |
      |          |          | Correct Answers |                   |       |
      |          |          | 2 (100%)        |                   |       |

  Scenario: Correct Answers half 1 (50%)
    Given workspace "Stats Correct Half" with questions
      | question              | answers         |
      | Jaký nábytek má Ikea? | Stůl (*), Auto  |
      | Jaké nádobí má Ikea?  | Talíř (*), Kolo |
    And a quiz "Stats Quiz" with all questions
    And I take quiz "Stats Quiz" with answers
      | question              | answers |
      | Jaký nábytek má Ikea? | Stůl    |
      | Jaké nádobí má Ikea?  | Kolo    |
    When I open stats for quiz "Stats Quiz"
    Then I see stats page for quiz "Stats Quiz"
    And I see attempt stats table
      | Attempts |          |                 |                   |       |
      |          |          | Correct Answers |                   |       |
      |          |          | 1 (50%)         |                   |       |

  Scenario: Correct Answers zero 0 (0%)
    Given workspace "Stats Correct Zero" with questions
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
    And I see attempt stats table
      | Attempts |          |                 |                   |       |
      |          |          | Correct Answers |                   |       |
      |          |          | 0 (0%)          |                   |       |


  # Incorrect Answers (individual): full, half, zero
  Scenario: Incorrect Answers full 2 (100%)
    Given workspace "Stats Incorrect Full" with questions
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
    And I see attempt stats table
      | Attempts |          |                 |                   |       |
      |          |          |                 | Incorrect Answers |       |
      |          |          |                 | 2 (100%)          |       |

  Scenario: Incorrect Answers half 1 (50%)
    Given workspace "Stats Incorrect Half" with questions
      | question              | answers         |
      | Jaký nábytek má Ikea? | Stůl (*), Auto  |
      | Jaké nádobí má Ikea?  | Talíř (*), Kolo |
    And a quiz "Stats Quiz" with all questions
    And I take quiz "Stats Quiz" with answers
      | question              | answers |
      | Jaký nábytek má Ikea? | Stůl    |
      | Jaké nádobí má Ikea?  | Kolo    |
    When I open stats for quiz "Stats Quiz"
    Then I see stats page for quiz "Stats Quiz"
    And I see attempt stats table
      | Attempts |          |                 |                   |       |
      |          |          |                 | Incorrect Answers |       |
      |          |          |                 | 1 (50%)           |       |

  Scenario: Incorrect Answers zero 0 (0%)
    Given workspace "Stats Incorrect Zero" with questions
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
    And I see attempt stats table
      | Attempts |          |                 |                   |       |
      |          |          |                 | Incorrect Answers |       |
      |          |          |                 | 0 (0%)            |       |


  # Score (individual): full, half, zero
  Scenario: Score full 100
    Given workspace "Stats Score Full" with questions
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
    And I see attempt stats table
      | Attempts |          |                 |                   |       |
      |          |          |                 |                   | Score |
      |          |          |                 |                   | 100   |

  Scenario: Score half 50
    Given workspace "Stats Score Half" with questions
      | question              | answers         |
      | Jaký nábytek má Ikea? | Stůl (*), Auto  |
      | Jaké nádobí má Ikea?  | Talíř (*), Kolo |
    And a quiz "Stats Quiz" with all questions
    And I take quiz "Stats Quiz" with answers
      | question              | answers |
      | Jaký nábytek má Ikea? | Stůl    |
      | Jaké nádobí má Ikea?  | Kolo    |
    When I open stats for quiz "Stats Quiz"
    Then I see stats page for quiz "Stats Quiz"
    And I see attempt stats table
      | Attempts |          |                 |                   |       |
      |          |          |                 |                   | Score |
      |          |          |                 |                   | 50    |

  Scenario: Score zero 0
    Given workspace "Stats Score Zero" with questions
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
    And I see attempt stats table
      | Attempts |          |                 |                   |       |
      |          |          |                 |                   | Score |
      |          |          |                 |                   | 0     |

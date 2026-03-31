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
      | Attempts   |          |                 |                   |       |          |
      | Duration   |          |                 |                   |       | Status   |
      | 10 seconds |          |                 |                   |       | Success |

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
      | Attempts   |          |                 |                   |       |          |
      | Duration   |          |                 |                   |       | Status   |
      | 10 seconds |          |                 |                   |       | Failed |

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
      | Attempts   |          |                 |                   |       |          |
      | Duration   |          |                 |                   |       | Status   |
      | 10 seconds |          |                 |                   |       | Failed |

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
      | Attempts |          |                 |                   |       |          |
      |          | Points   |                 |                   |       | Status   |
      |          | 2/2      |                 |                   |       | Success |

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
      | Attempts |          |                 |                   |       |          |
      |          | Points   |                 |                   |       | Status   |
      |          | 1/2      |                 |                   |       | Failed |

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
      | Attempts |          |                 |                   |       |          |
      |          | Points   |                 |                   |       | Status   |
      |          | 0/2      |                 |                   |       | Failed |


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
      | Attempts |          |                 |                   |       |          |
      |          |          | Correct Answers |                   |       | Status   |
      |          |          | 2 (100%)        |                   |       | Success |

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
      | Attempts |          |                 |                   |       |          |
      |          |          | Correct Answers |                   |       | Status   |
      |          |          | 1 (50%)         |                   |       | Failed |

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
      | Attempts |          |                 |                   |       |          |
      |          |          | Correct Answers |                   |       | Status   |
      |          |          | 0 (0%)          |                   |       | Failed |


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
      | Attempts |          |                 |                   |       |          |
      |          |          |                 | Incorrect Answers |       | Status   |
      |          |          |                 | 2 (100%)          |       | Failed |

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
      | Attempts |          |                 |                   |       |          |
      |          |          |                 | Incorrect Answers |       | Status   |
      |          |          |                 | 1 (50%)           |       | Failed |

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
      | Attempts |          |                 |                   |       |          |
      |          |          |                 | Incorrect Answers |       | Status   |
      |          |          |                 | 0 (0%)            |       | Success |


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
      | Attempts |          |                 |                   |       |          |
      |          |          |                 |                   | Score | Status   |
      |          |          |                 |                   | 100   | Success |

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
      | Attempts |          |                 |                   |       |          |
      |          |          |                 |                   | Score | Status   |
      |          |          |                 |                   | 50    | Failed |

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
      | Attempts |          |                 |                   |       |          |
      |          |          |                 |                   | Score | Status   |
      |          |          |                 |                   | 0     | Failed |


  # Status (individual): Success, Timeout
  Scenario: Status shows Success for completed quiz
    Given workspace "Stats Status Success" with questions
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
      | Attempts |       |                 |                   |     |          |
      | Duration | Points| Correct Answers | Incorrect Answers | Score| Status   |
      |          |       |                 |                   |     | Success |

  # Summary stats
  Scenario: Summary stats for completed quiz
    Given workspace "Stats Summary Success" with questions
      | bookmark | question   | answers  |
      | Q1       | 1 + 1 = ?  | 2 (*), 3 |
      | Q2       | 2 + 2 = ?  | 4 (*), 5 |
    And a quiz "Stats Quiz" with all questions
    When I start the quiz
    And I answer 2 questions correctly
    And I proceed to the score page
    And I open stats for quiz "Stats Quiz"
    Then I see summary stats table
      | Summary |         |        |         |
      | Started | Success | Failed | Timeout |
      |       1 |       1 |      0 |       0 |

  @skip
  Scenario: Summary stats for timed out quiz
    Given workspace "Stats Summary Timeout" with questions
      | bookmark | question   | answers  |
      | Q1       | 1 + 1 = ?  | 2 (*), 3 |
      | Q2       | 2 + 2 = ?  | 4 (*), 5 |
    And a quiz "Stats Quiz" with all questions
      | time limit | 5 |
    When I take quiz "Stats Quiz" which I do not complete in time limit
      | question  | answers |
      | 1 + 1 = ? | 2       |
    And I open stats for quiz "Stats Quiz"
    Then I see summary stats table
      | Summary |         |        |         |
      | Started | Success | Failed | Timeout |
      |       1 |       0 |      0 |       1 |

# Status should show Timeout but we are unable to simulate waiting
  @skip
  Scenario: Status shows Timeout for timed out quiz
    Given workspace "Stats Status Timeout" with questions
      | question              | answers         |
      | Jaký nábytek má Ikea? | Stůl (*), Auto  |
      | Jaké nádobí má Ikea?  | Talíř (*), Kolo |
    And a quiz "Stats Quiz" with all questions
      | time limit |
      | 5          |
    When I take quiz "Stats Quiz" which I do not complete in time limit
      | question              | answers |
      | Jaký nábytek má Ikea? | Stůl    |
    And I open stats for quiz "Stats Quiz"
    Then I see stats page for quiz "Stats Quiz"
    And I see attempt stats table
      | Attempts |       |                 |                   |     |          |
      | Duration | Points| Correct Answers | Incorrect Answers | Score| Status   |
      |          |       |                 |                   |     | Timeout  |

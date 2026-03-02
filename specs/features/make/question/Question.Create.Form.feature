Feature: Create question form
  The question creation form starts with sensible defaults: empty question text,
  two empty answer fields, single choice mode, and no explanations visible.
  Answers can be added and removed. The explanation checkbox controls whether
  explanation fields are shown. After successful submission, the user receives
  a take-question URL and an edit-question URL.

  Scenario: Default values
    Given I start creating a question
    * I check show explanations checkbox
    Then I see empty question field
    * I see multiple choice is unchecked
    * I see 2 empty answer fields, incorrect, with empty explanations fields, unable to delete
    * I see empty question explanation field

  Scenario: Question take and question edit URLs
    Successfully created question has two URLs:
    - question take URL the quiz maker can share with quiz takers
    - private edit URL (with UUID editId) for future edits

    Given I start creating a question
    * I check show explanations checkbox
    * I enter question "2 + 2 = ?"
    * I enter answers
      | 4 | * |
      | 5 |   |

    When I submit the question
    Then I see question edit page
    And I see question-take URL and question-edit URL

  Scenario: Delete second answer out of three
    Given I start creating a question
    * I check show explanations checkbox
    * I enter answers
      | AA | * |
      | BB |   |
      | CC |   |
    Then I delete answer 2
    * I see the answers fields
      | AA | * |
      | CC |   |

  Scenario: Explanation fields are hidden by default
    When I start creating a question
    Then I see show explanation checkbox is unchecked
    And I see explanation fields are not visible

  Scenario: Explanation fields are visible when toggling "Show explanation" checkbox
    When I start creating a question
    And I check "Show explanation" checkbox
    Then I see show explanation checkbox is checked
    And I see explanation fields are visible

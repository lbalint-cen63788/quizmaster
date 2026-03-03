Feature: AI assist - generate question
  @skip
  Scenario: Generate question with AI assist and save it into workspace
    Given I open question create page for workspace "c5b5b9e9-aa70-4794-a207-23e65a1941b3"
    Then I see AI assist button
    When I click AI assist button
    Then I see AI assist popup
    When I fill AI prompt "Vygeneruj otázku do quizu na téma města a přidej čtyři možnosti odpovědí, kdy je pouze jedna správně."
    And I click AI generate button
    Then AI assist popup is closed
    And I see the form prefilled with generated question and answers
    When I save the question
    Then I see the generated question in the workspace list

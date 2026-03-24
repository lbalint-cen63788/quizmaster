Feature: Generate question and answers using AI

  Scenario: Generate single-choice question and answers using AI assist
    Given I start creating a question
    When I ask AI: "Vygeneruj mi otázku na téma: Hlavní město nějakého státu"
    Then request to AI assistant contains question "Vygeneruj mi otázku na téma: Hlavní město nějakého státu"
    And AI assistant returns generated question
    And AI assistant returns generated answers with only one correct answer
    And Question type is set to "Single choice"
    And Question field is updated to AI generated question
    And answer1 field is filled with AI generated correct answer
    And answer2 field is filled with AI generated incorrect answer

  Scenario: AI prompt section is visible when creating question
    When I start creating a question
    Then I see AI section

  @skip
  Scenario: AI prompt section is hidden when editing question
    Given a question "What is the capital of Czech Republic?"
      * with answers:
        | Brno   |   | No Brno |
        | Prague | * | Yes     |
        | Berlin |   | Germany |
      * with explanation "Czechia is a country in Europe. Czechs love beer."
      * saved and bookmarked as "Czechia"
    When I enter question "What is the capital of Slovakia?"
    Then I do not see AI section

Feature: Generate question and answers using AI

  @slow
  Scenario: Generate single-choice question and answers using AI assist
    Given I start creating a question
    When I ask AI: "Vygeneruj mi otázku na téma: Hlavní město nějakého státu"
    Then request to AI assistant contains question "Vygeneruj mi otázku na téma: Hlavní město nějakého státu"
    And Question field is not empty
    And the question is single choice
    And AI assistant returns at least 2 generated answers
    And AI assistant returns generated answers with only one correct answer

  @slow
  Scenario: Generate multiple-choice question and answers using AI assist
    Given I start creating a question
    When I mark the question as multiple choice
    And I ask AI: "Vygeneruj mi otázku na téma: Hlavní města v Evropě"
    Then request to AI assistant contains question "Vygeneruj mi otázku na téma: Hlavní města v Evropě"
    And the question is multiple choice
    And Question field is not empty
    And AI assistant returns at least 2 generated answers
    And AI assistant returns at least 2 correct answers

  Scenario: AI prompt section is visible when creating question
    Given I start creating a question
    When the question is single choice
    Then I see AI section

    When I mark the question as multiple choice
    Then the question is multiple choice
    And I see AI section

    When I mark the question as numerical choice
    Then the question is numerical choice
    And I do not see AI section

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

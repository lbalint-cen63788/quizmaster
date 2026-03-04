@skip
Feature: Generate question using AI

  Scenario: Generate question with AI assist and save it
    Given I start creating a question
    And AI assistant returns generated question "Kolik je 2 + 2?"
    When I enter AI instructions into field Question: "Vygeneruj otázku do quizu"
    And I click on button "AI assist"
    Then request to AI assistant contains question "Vygeneruj otázku do quizu"
    And question field is updated to "Kolik je 2 + 2?"
    When I save the question

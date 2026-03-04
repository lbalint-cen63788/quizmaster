Feature: Generate question and answers using AI

  Scenario: Generate single-choice question and answers using AI assist
    Given I start creating a question
    When I enter text into field Question: "Vygeneruj mi otázku na téma: Hlavní město nějakého státu"
    And I click on button "AI assist"
    Then request to AI assistant contains question "Vygeneruj mi otázku na téma: Hlavní město nějakého státu"
    And AI assistant returns generated question
    And AI assistant returns generated answers with only one correct answer
    And Question type is set to "Single choice"
    And Question field is updated to AI generated question
    And answer1 field is filled with AI generated correct answer
    And answer2 field is filled with AI generated incorrect answer
    When I save the question
    Then I see question-take URL and question-edit URL

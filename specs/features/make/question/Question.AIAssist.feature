Feature: Generate question using AI
  @skip
  Scenario: Generate question with AI assist and save it
    Given I start creating a question
    When I enter AI instructions into field Question: "Vygeneruj otázku do quizu"
    And I click on button "AI assist"
    And I see prefilled valid AI question
    When I save the question

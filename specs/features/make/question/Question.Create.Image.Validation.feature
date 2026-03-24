Feature: Image URL validation when creating a question
  Quiz makers should receive validation feedback when entering an invalid image URL
  - Valid URLs (http://, https://) should be accepted and preview should display
  - Invalid URLs (malformed, wrong protocol, XSS) should show validation error
  - Empty image URL is optional and should not show error

  Scenario: Invalid image URL shows validation error
    Given I start creating a question
    When I enter an invalid image URL "not-a-url"
    Then I see error messages
      | invalid-image-url |
    And I do not see image preview

  Scenario: Invalid URL protocol shows validation error
    Given I start creating a question
    When I enter an invalid image URL "ftp://example.com/image.jpg"
    Then I see error messages
      | invalid-image-url |

  Scenario: XSS URL attempt shows validation error
    Given I start creating a question
    When I enter an invalid image URL "javascript:alert('xss')"
    Then I see error messages
      | invalid-image-url |

  Scenario: Valid HTTPS URL is accepted
    Given I start creating a question
    When I enter image URL "https://example.com/image.jpg"
    Then I see no error messages
    And I see image preview

  Scenario: Valid HTTP URL is accepted
    Given I start creating a question
    When I enter image URL "http://placekitten.com/300/200"
    Then I see no error messages
    And I see image preview

  Scenario: Empty image URL is optional
    Given I start creating a question
    Then I do not see image preview
    And I see no error messages

  Scenario: URL validation error clears when corrected
    Given I start creating a question
    When I enter an invalid image URL "not-a-url"
    Then I see error messages
      | invalid-image-url |
    When I clear image URL and enter "https://example.com/image.jpg"
    Then I see no error messages
    And I see image preview

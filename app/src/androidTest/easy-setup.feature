# new feature
# Tags: optional
    
Feature: Easy Setup
    
  Scenario: Successful Data Input
    Given a setup activity
    When the user enters 72 in the height text field
    And the user clicks the save button
    Then the app accepts that height
    And show a toast message

  Scenario: User Already Inputted the Height
    Given that the user has inputted my height
    When the user opens the app
    Then the app should never request for height again

  Scenario: User enters invalid height
    Given that the user hasn't entered the height
    And the user opens the app
    And the app request the height
    When the user enters an invalid height value (negative number, non-numeric etc.)
    Then the app rejects the value and prompts again
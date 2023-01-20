Feature: Create device

  Background:
    Given user logged in

  Scenario: Create customer device without name
    When create device without name
    Then device does not exist

  Scenario: Create customer device without OS type
    When create device without OS type
    Then device does not exist

  Scenario: Create customer device without country code
    When create device without country code
    Then device does not exist

  Scenario: Create customer device with non ISO country code
    When create device with non ISO country code
    Then device does not exist

  Scenario: Create customer device with all fields valid
    When create device "JVTest#Date_5" with OS type "iPhone" and country code "CZ"
    Then device exists
    And device name is "JVTest#Date_5"

  Scenario: Create 2 duplicate customer devices
    When create device "JVTest#Date_6" with OS type "WindowsPhone" and country code "ES"
    And create device "JVTest#Date_6" with OS type "WindowsPhone" and country code "ES"
    Then device exist 2 times
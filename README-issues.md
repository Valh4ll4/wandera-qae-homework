# QA Engineer Device Manager Bug Overview

## Tracked bugs

***Severities and urgencies are subject to Product Owner review are only present for relative comparisons.***

| ID     | Name                                                             | Severity | Urgency |
|--------|------------------------------------------------------------------|----------|---------|
| DMAN-1 | Input form incorrect validation of field presence (OS type)      | High     | Medium  |
| DMAN-2 | Input form incorrect validation of field presence (country code) | High     | Medium  |
| DMAN-3 | Input form incorrect validation of field content (country code)  | Medium   | Medium  |
| DMAN-4 | User is not able to log out                                      | High     | High    |
| DMAN-5 | Input form does not report problems with input                   | Low      | Low     |
| DMAN-6 | Deleting a record does not have confirmation window              | Medium   | Low     |

## Details

### DMAN-1

- Name: Input form incorrect validation of field presence (OS type)
- **Description: When creating a device record, it is possible to create one without OS type.**
- Reproduction steps:
    - log into the application
    - open modal window for creation (click on '+ Add New')
    - fill out any non-empty name (e.g. 'Test 2')
    - fill out valid (ISO) country code (e.g. 'US')
    - save the device (click 'Save')

### DMAN-2

- Name: Input form incorrect validation of field presence (country code)
- **Description: When creating a device record, it is possible to create one without country code.**
- Reproduction steps:
    - log into the application
    - open modal window for creation (click on '+ Add New')
    - fill out any non-empty name (e.g. 'Test 3')
    - select any OS type from dropdown (e.g. 'WindowsPhone')
    - save the device (click 'Save')

### DMAN-3

- Name: Input form incorrect validation of field content (country code)
- **Description: When creating a device record, it is possible to create one with invalid country code.**
- Reproduction steps:
    - log into the application
    - open modal window for creation (click on '+ Add New')
    - fill out any non-empty name (e.g. 'Test 4')
    - select any OS type from dropdown (e.g. 'iPhone')
    - fill out invalid country code (e.g. 'CAT')
    - save the device (click 'Save')

### DMAN-4

- Name: User is not able to log out
- **Description: After user logs in, there is no possibility to log out. Logout button does not work.**
- Reproduction steps:
    - log into the application
    - log out (click on 'Logout'')

### DMAN-5

- Name: Input form does not report problems with input
- **Description: When there is invalid input in device creation form, no validation message is displayed.**
- Reproduction steps:
    - log into the application
    - open modal window for creation (click on '+ Add New')
    - select any OS type from dropdown (e.g. 'Android')
    - fill out valid (ISO) country code (e.g. 'MX')
    - save the device (click 'Save')

### DMAN-6

- Name: Deleting a record does not have confirmation window
- **Description: When deleting a record, no confirmation is shown and records are deleted right away.**
- Reproduction steps:
    - log into the application
    - select at least one device
    - click on 'Delete selected'

## Other issues

As noted in `DMAN-5` there are usability issues. Namely, little visualisation for the user to know what the system is
doing. Here is a list of usability issues / proposals for making the application more usable:

- There is no visual indication when the application is working in background and user is waiting for async response.
  Adding visual element for showing that e.g. device list table is 'being updated' would help users and automation
  software to explicitly know when the update finished. This element (or these kinds of elements) should definitely have
  usable `id` attributes for automation.
- There seems to be no validation at all on the front-end. When creating a device, only constraint is that device name
  may not be empty (which is most probably a constraint on DB level). Validating each input field on front-end as well
  as back-end would benefit the quality of the solution. Validation errors with give visual element (for example input
  field) can be shown right next to the relevant field. Back-end validations and errors can be shown in a small
  non-blocking popup at the top center to catch user attention. This is one of the ways to make the app more
  user-friendly.
- Similarly, during login procedure there is no information why logging in did not work (if there was a problem).
- Specifically for automation, not all important elements did have `id` attribute. And `class` attributes that were used
  could as well only be used with `[contains(@class,'...')` because of the GUI framework nature. This needs to be
  addressed for better ability to test the webapp with automation.
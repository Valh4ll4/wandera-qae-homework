# QA Engineer Device Manager Homework Overview

This document outlines all the activities done with the homework project.
Also, instructions on how to do specific tasks and results of the testing are included.

## Device manager description

The device manager is a simple web application that enables user to do several things:

- log in (authenticate) (US1) (enabling other User Stories)
- create a device (US2) <--- **AUTOMATION PROJECT**
- list and sort devices (US3)
- delete device(s) (US4)

User should be able to log in (given valid credentials) and log out. After login a table of device records should be
displayed with option to create a new device, delete devices or log out. When creating a device, there are 3 input
fields (device name, OS type and country code). Documentation states that name duplicities are allowed, every field
should be provided and country code should be in ISO 3166-1 alpha-2 format. On successful storing of the device, 4th
attribute is stored (creation date). These 4 attributes can be used in the table to sort records. One or more devices
may be selected and deleted.

## What was done

- test plan for testing the whole application
    - test cases detailed in [Test cases](#test-scenarios-&-cases)
- testing within scope of functional E2E tests
- usability testing (user friendly-ness of the web application)
    - notes are present in `README-issues.md`
- automation of US2
- manual testing of US1, US3 and US4
- proposal document for performance testing strategy
- bug tracking (**see part [Bug listing](#bugs)**)
- **note: the testing was based on Chrome**

### HTML selectors

During automation testing `id` selectors were used where possible, although on some occasions, parts of `class`
attributes needed to be used (`id` was not present). XPath was used in a way that layout changes of the page should not
break the tests (absolute paths were not used at all). Example usage for finding rows or the
table: `//table[contains(@class,'mdl-data-table')]/tbody/tr`. See `README-issues.md` for further comments.

## What was not done

- other forms of testing
    - performance
    - security
    - etc.

## How to run automation

The automated tests are included in `20220717-jvano-wandera-qae-homework.zip`.
Inside is a Maven Java project that contains the tests using BDD Cucumber + Serenity.
There is a configuration at `src/test/resources/serenity.conf` which states if tests are run at headless
mode: `headless.mode = true` or `headless.mode = false`.

When happy with the configuration, tests can be launched from the root of the folder with:

```
mvn clean verify
```

### How to export automation to JAR and run via Java command (experimental)

This is possible by producing a fat JAR as we would with production application.
WARNING: Whether the tests run in headless mode or not is controlled by a configuration baked into the JAR. It is not
controllable by inputting a parameter to JAR file itself. It needs to be changed in  `src/test/resources/serenity.conf`
or `src/main/resources/serenity.conf` based on whether the files have been already moved or not. Similarly, other
aspects
like logger level can be controlled.

1. Change sources from `test` to `main`
    1. Edit `src/test/java/starter/CucumberTestSuite.java`
        1. Change `"src/test/resources/features"` to `"src/main/resources/features"`
    2. Move all files from `src/main` to `src/test`
2. Change `pom.xml` for the libraries to be visible in `main`
    1. For example delete all `<scope>test</scope>` strings
3. Add a main class/method to project
    1. Create a class anywhere (e.g. `src/main/java/starter/Exec.java`)
    2. The structure should be following:
    ```java
    package starter;
    
    import net.serenitybdd.cucumber.CucumberWithSerenity;
    import org.junit.runner.notification.RunNotifier;
    import org.junit.runners.model.InitializationError;
    
    public class Exec
    {
        public static void main(String[] args) throws InitializationError {
            CucumberWithSerenity runner = new CucumberWithSerenity(CucumberTestSuite.class);
            runner.run(new RunNotifier());
        }
    }
    ```
4. Add configurations to `pom.xml` for the fat JAR to be created with correct manifest (classpath, main method).
    1. Add following plugin to `pom.xml`
    ```xml
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-assembly-plugin</artifactId>
        <version>3.1.1</version>
    
        <configuration>
            <descriptorRefs>
                <descriptorRef>jar-with-dependencies</descriptorRef>
            </descriptorRefs>
            <archive>
                <manifest>
                    <addClasspath>true</addClasspath>
                    <mainClass>starter.Exec</mainClass>
                </manifest>
            </archive>
        </configuration>
    
        <executions>
            <execution>
                <id>make-assembly</id>
                <phase>package</phase>
                <goals>
                    <goal>single</goal>
                </goals>
            </execution>
        </executions>
    
    </plugin>
    ```
    2. Part `<mainClass>starter.Exec</mainClass>` must respect the fully qualified name of the class created before (in
       step 3)
5. Generate JAR with dependencies
    1. in root folder launch `mvn clean package`
6. Launch the JAR
    1. The JAR is located in the `target`
    2. `java -jar target/wandera-qae-homework-1.0.0-SNAPSHOT-jar-with-dependencies.jar`

## Test scenarios & cases

### User Story 1 - Log in

- Login without credentials
    - Open login page
    - Click on Login
- Login with only username
    - Open login page
    - Enter valid username
    - Click on Login
- Login with only password
    - Open login page
    - Enter valid password
    - Click on Login
- Login with valid username but incorrect password
    - Open login page
    - Enter valid username
    - Enter incorrect password
    - Click on Login
- Login with invalid username but valid password
    - Open login page
    - Enter invalid username
    - Enter valid password
    - Click on Login
- Login with valid credentials
    - Open login page
    - Enter valid username
    - Enter valid password
    - Click on Login

### User Story 2 - Create a device

- Check device creation without name
- Check device creation without OS type
- Check device creation without country code
- Check device creation with invalid country code
- Check device creation with all fields valid
- Check device creation with all fields valid 2 times

### User Story 3 - List device

Given there are at least 3 device with each field different:

- Verify sorting devices according to name
- Verify sorting devices according to OS type
- Verify sorting devices according to creation date
- Verify sorting devices according to country code

### User Story 4 - Delete device(s)

Given there are at least 4 devices with different names:

- Check deletion of 0 devices
- Check deletion of 1 device
- Check deletion of 2 devices

## Bugs

Several bugs were discovered. Following is an overview with details being in separate file `README-issues.md`.

### Bug list

***Severities and urgencies are subject to Product Owner review are only present for relative comparisons.***

| ID     | Name                                                             | Severity | Urgency |
|--------|------------------------------------------------------------------|----------|---------|
| DMAN-1 | Input form incorrect validation of field presence (OS type)      | High     | Medium  |
| DMAN-2 | Input form incorrect validation of field presence (country code) | High     | Medium  |
| DMAN-3 | Input form incorrect validation of field content (country code)  | Medium   | Medium  |
| DMAN-4 | User is not able to log out                                      | High     | High    |
| DMAN-5 | Input form does not report problems with input                   | Low      | Low     |
| DMAN-6 | Deleting a record does not have confirmation window              | Medium   | Low     |

For more information (details, reproduction steps), see `README-issues.md`.




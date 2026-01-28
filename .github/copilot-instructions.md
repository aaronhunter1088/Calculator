# Calculator Application - GitHub Copilot Instructions

## Project Overview

This is a Java Swing-based Calculator application (v4.6.2) that provides multiple calculator views:
- **Basic**: Standard arithmetic calculator
- **Programmer**: Binary, hexadecimal, and other programming-related calculations
- **Scientific**: Advanced mathematical operations
- **Date**: Date calculations and differences
- **Converter**: Unit conversions (area, angle, etc.)

## Technology Stack

- **Language**: Java 25
- **UI Framework**: Java Swing
- **Build Tool**: Apache Maven 3.9+
- **Testing**: JUnit Jupiter (JUnit 5) with Mockito
- **Logging**: Apache Log4j2
- **Dependencies**: See pom.xml for full list

## Project Structure

```
src/
├── main/java/
│   ├── Calculators/      # Main calculator logic
│   ├── Converters/       # Unit conversion methods
│   ├── Interfaces/       # Interface definitions
│   ├── Panels/           # UI panels for each calculator view
│   ├── Runnables/        # Main application entry point
│   ├── Types/            # Enums, utilities, and type definitions
│   └── Utilities/        # Helper utilities
├── main/resources/       # Application resources
└── test/java/
    ├── Calculators/      # Calculator logic tests
    ├── Panels/           # Panel tests
    └── Parent/           # Base test classes and test utilities
```

## Coding Standards

### Package Structure
- Use capitalized package names (e.g., `Calculators`, `Panels`, `Types`)
- Follow the existing package organization

### Imports
- Use static imports for commonly used constants and utilities
- Group imports by package (standard Java, third-party, project)
- Examples:
  ```java
  import static Types.CalculatorBase.*;
  import static Types.CalculatorView.*;
  ```

### Code Style
- Use Log4j2 for logging: `Logger LOGGER = LogManager.getLogger(ClassName.class.getSimpleName());`
  - Note: Some classes use `ClassName.class` instead of `ClassName.class.getSimpleName()` - both patterns are acceptable
- Follow JavaDoc conventions for class and method documentation
- Use meaningful variable names
- Keep methods focused and single-purpose

### Testing
- Use JUnit 5 (Jupiter) for all tests
- Use Mockito for mocking dependencies
- Test classes should extend `TestParent` when applicable
- Use parameterized tests with `@ParameterizedTest` and `@MethodSource` for testing multiple scenarios
- Follow the naming convention: `[ClassName]Test.java` or `[ClassName]Tests.java`

## Build and Test Commands

### Build
```bash
mvn clean package
```

### Run Tests
```bash
mvn test
```

### Install to Local Repository
```bash
mvn install -DskipTests
```

### Create JAR with Dependencies
```bash
mvn clean package -Dmaven.test.skip=true
```

## Development Guidelines

### When Adding New Features
1. Determine which calculator view the feature belongs to
2. Update the appropriate Panel class (BasicPanel, ProgrammerPanel, etc.)
3. Add business logic to the Calculator class or relevant utility classes
4. Create corresponding test cases
5. Update documentation if adding new calculator views or significant features

### When Fixing Bugs
1. Write a test that reproduces the bug
2. Fix the bug in the appropriate class
3. Ensure all tests pass
4. Update documentation if the fix changes behavior

### UI Components
- Use Java Swing components
- Follow the existing panel structure and layout patterns
- Maintain consistent UI styling across all calculator views
- Consider platform-specific behavior using `SystemDetector` (implements `OSDetector` interface)

### Constants and Enums
- Use `CalculatorView` enum for view types
- Use `CalculatorBase` enum for number bases (BASE_BINARY, BASE_OCTAL, BASE_DECIMAL, BASE_HEXADECIMAL)
- Use `CalculatorByte` enum for byte sizes
- Use `CalculatorConverterType` enum for converter types
- Use `DateOperation` enum for date operations
- Store text constants in `Texts` class

## Common Patterns

### Event Listeners
- Use `CalculatorKeyListener` for keyboard events
- Use `CalculatorMouseListener` for mouse events
- Implement ActionListeners inline for button actions

### State Management
- Use `Preferences` API for storing user preferences
- Use instance variables in Calculator class for runtime state

### Error Handling
- Use `CalculatorError` class for error messages
- Log errors appropriately with Log4j2
- Display user-friendly error messages in the UI

## Important Notes

- The main entry point is `Runnables.CalculatorMain`
- Output JAR files are placed in `dist/version4/`
- Parent POM manages dependency versions
- Some features (Programmer and Scientific calculators) may have incomplete logic
- The project uses custom UI built manually (not JavaFX)

## Dependencies Management

- Dependency versions are managed in the parent POM
- Avoid adding new dependencies unless absolutely necessary
- Check parent POM before adding version specifications

## When Working with Copilot

- Ask for code that follows existing patterns in the codebase
- Request tests that match the existing test structure
- Ensure any generated code uses the project's logging, constants, and utility classes
- Verify that Swing UI code is compatible with the manual UI approach used in this project

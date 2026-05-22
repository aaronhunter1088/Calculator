# Calculator – Claude Agent Guide

Java 25 + Swing calculator app, built with Apache Maven. Entry point: `Runnables.CalculatorMain`. Output JARs land in `dist/version#/`.

---

## Build & Run Commands

```bash
# Compile and run tests
mvn clean package

# Run tests only
mvn test

# Install to local repo (skip tests)
mvn install -DskipTests

# Build fat JAR without running tests
mvn clean package -Dmaven.test.skip=true
```

### Running the JAR

```bash
# Views: basic, programmer, scientific, date, converter
java -jar Calculator-4.6.3-jar-with-dependencies.jar basic

# Date requires a sub-option: 1 = difference between dates, 2 = add/subtract days
java -jar Calculator-4.6.3-jar-with-dependencies.jar date 1

# Converter requires a type: area, angle
java -jar Calculator-4.6.3-jar-with-dependencies.jar converter area
```

Set `logLevel` as an environment variable to control log verbosity. If unset, a warning is logged and `all` is used.

---

## Package Structure

```
src/main/java/
  Calculators/     Calculator.java, CalculatorError.java
  Converters/      AngleMethods.java, AreaMethods.java
  Interfaces/      CalculatorType.java, OSDetector.java
  Panels/          BasicPanel, ProgrammerPanel, ScientificPanel, DatePanel, ConverterPanel
  Runnables/       CalculatorMain.java  ← entry point
  Types/           Enums, constants, listeners, SystemDetector
  Utilities/       CalculatorUtility, LoggingUtility, PemdasUtility

src/test/java/
  Calculators/     CalculatorTests.java
  Converters/      AngleMethodsTest.java, AreaMethodsTest.java
  Panels/          BasicPanelTest, ConverterPanelTest, DatePanelTest,
                   ProgrammerPanelTest, ScientificPanelTest
  Parent/          TestParent.java, ArgumentsForTests.java  ← test infrastructure
  Utilities/       CalculatorUtilityTest, LoggingUtilityTest, PemdasUtiltyTest
```

All packages use **capitalised names** (e.g. `Calculators`, `Panels`, `Types`).

---

## Key Types & Constants

| Symbol | Source | Notes |
|---|---|---|
| `CalculatorView` | `Types.CalculatorView` | `VIEW_BASIC`, `VIEW_PROGRAMMER`, `VIEW_SCIENTIFIC`, `VIEW_DATE`, `VIEW_CONVERTER` |
| `CalculatorBase` | `Types.CalculatorBase` | `BASE_BINARY`, `BASE_OCTAL`, `BASE_DECIMAL`, `BASE_HEXADECIMAL` |
| `CalculatorByte` | `Types.CalculatorByte` | `BYTE_BYTE`, `BYTE_WORD`, `BYTE_DWORD`, `BYTE_QWORD` |
| `CalculatorConverterType` | `Types.CalculatorConverterType` | `AREA`, `ANGLE` |
| `DateOperation` | `Types.DateOperation` | `DIFFERENCE_BETWEEN_DATES`, `ADD_SUBTRACT_DAYS` |
| All string literals | `Types.Texts` | Import with `import static Types.Texts.*` |

Never hard-code button labels or error strings — always reference `Types.Texts` constants (e.g. `ADDITION`, `ENTER_A_NUMBER`, `INFINITY`, `ARGUMENT_SEPARATOR`).

---

## Architecture: Calculator State

`Calculator` extends `JFrame` and is the single stateful object. Key state fields:

```java
String[] values = new String[]{"", "", "", ""};
// Non-pemdas [0] = first number, [1] = second number, [2] = active operator, [3] = result
// Pemdas mode uses [0] for the current formula being entered, and [3] for the result (for chaining). [1] and [2] are 
// unused while entering a pemdas formula, for now.

String[] memoryValues = new String[10]; // rolls over after 10 entries
int valuesPosition = 0;   // 0 or 1, tracks which number is being entered
int memoryPosition = 0;   // 0-9, tracks where the next memory entry will be stored (rolls over after 10)
int memoryRecallPosition = 0; // 0-9, tracks which memory entry will be recalled next (rolls over after 10)

boolean obtainingFirstNumber = true;
boolean negativeNumber;
boolean pemdasIsActive;
String delimiter = COMMA; // thousands delimiter, changeable by user
// Need to add delimiter for decimal point as well, and ensure it's handled correctly in all operations and displays.
```

Each Panel (`BasicPanel`, `ProgrammerPanel`, etc.) is instantiated once inside `Calculator` and accessed via `calculator.getBasicPanel()`, `calculator.getProgrammerPanel()`, etc.

---

## Logging Conventions

```java
// Standard logger declaration
private final Logger LOGGER = LogManager.getLogger(MyClass.class.getSimpleName());

// Static context (e.g. in Calculator.java)
private static final Logger LOGGER = LogManager.getLogger(Calculator.class.getSimpleName());
```

Use `LoggingUtility` static helpers instead of direct logger calls where they exist:

```java
LoggingUtility.logActionButton(actionEvent, LOGGER); // at start of any action handler
LoggingUtility.confirm(calculator, LOGGER, "message"); // after a result
LoggingUtility.logEmptyValue(operation, calculator, LOGGER);
LoggingUtility.logOperation(LOGGER, calculator);
```

---

## UI Conventions

- All UI is hand-built Swing — no GUI designer, no JavaFX.
- `MetalLookAndFeel` is the default look; users can switch via the Style menu.
- OS detection: use `SystemDetector` (implements `OSDetector`) injected into `Calculator`.
- Use `CalculatorKeyListener` for keyboard events; `CalculatorMouseListener` for mouse events.
- Button action strings map 1-to-1 with `Types.Texts` constants (e.g. button labelled `"+"` is `ADDITION`).
- `textPane` (`JTextPane`) displays the current expression; `historyTextPane` displays a running log.
- `calculator.appendTextToPane(String)` adds text to the display.
- `calculator.getTextPaneValue()` / `calculator.getHistoryTextPaneValue()` read display state.

---

## Testing

### Infrastructure

All test classes:
1. Extend `TestParent` (provides `calculator`, `actionEvent` mock, `systemDetector` mock, and helper methods).
2. Declare a `static Logger LOGGER`.
3. Set `System.setProperty("appName", TheTestClass.class.getSimpleName())` in a `static {}` block.
4. Use `@BeforeAll` / `@AfterAll` to open and close Mockito mocks.

```java
@BeforeAll
static void beforeAll() {
    mocks = MockitoAnnotations.openMocks(MyTest.class);
}

@AfterAll
static void afterAll() {
    if (mocks != null) mocks.close();
}
```

### Writing a Test

Use `ArgumentsForTests` (builder pattern) to describe inputs and expected outputs:

```java
ArgumentsForTests.builder(PERCENT)              // operatorUnderTest
    .testName("5%")
    .firstNumber("5")
    .firstUnaryOperator(PERCENT)
    .firstUnaryResult("0.05")
    .build()
```

When `values[vP]` and the `textPane` display differ, separate them with `ARGUMENT_SEPARATOR` (`"|"`):
```java
.firstBinaryResult("0.04|0.04 +")  // values[vP]="0.04", textPane shows "0.04 +"
```

### Test Execution Flow

```java
@BeforeEach
void setUp() {
    calculator = new Calculator(VIEW_BASIC);
    postConstructCalculator();                // pack() + setVisible(true)
    setupWhenThen(actionEvent, arguments);    // wire mocked ActionEvent
}

String history = "";
history = performTest(arguments, history, LOGGER);  // run the scenario
assertHistory(arguments, history);                  // verify history pane
```

### Parameterized Tests

Use `@ParameterizedTest` + `@MethodSource`. Place the provider method **directly below** the test method. Split valid and invalid cases into separate methods and tests:

```java
@ParameterizedTest(name = "{0}")
@MethodSource("validPercentButtonCases")
void testValidPercentButton(ArgumentsForTests arguments) { ... }

private static Stream<ArgumentsForTests> validPercentButtonCases() {
    return Stream.of(
        ArgumentsForTests.builder(PERCENT).testName("5%")...build(),
        ...
    );
}

@ParameterizedTest(name = "{0}")
@MethodSource("invalidPercentButtonCases")
void testInvalidPercentButton(ArgumentsForTests arguments) { ... }

private static Stream<ArgumentsForTests> invalidPercentButtonCases() { ... }
```

### `ArgumentsForTests` fields

| Field | Purpose |
|---|---|
| `firstNumber` / `secondNumber` | Input digits (commas accepted — stripped automatically) |
| `firstUnaryOperator` / `secondUnaryOperator` | Operator applied before a binary op (%, √, x², ⅟x, ±, etc.) |
| `firstBinaryOperator` / `secondBinaryOperator` | Binary operator (+, -, ✕, ÷, =) |
| `firstUnaryResult` / `firstBinaryResult` (and second variants) | Expected result; use `\|` separator when textPane ≠ values[vP] |
| `initialState` | Pre-condition state applied before `setupWhenThen` runs |
| `currentBase` / `previousBase` | Programmer base transitions |
| `calculatorByte` | Programmer byte size for the test |

---

## Known Incomplete Features

- **Programmer calculator**: UI is complete; some logic is missing.
- **Scientific calculator**: UI is complete; most logic is not implemented.

When working on these panels, check `ProgrammerPanel.java` / `ScientificPanel.java` for `TODO` comments and the `performNextOperatorAction` switch in `TestParent` for the full list of currently supported operator methods.

---

## Dependencies

Managed in the parent POM (`com.skvllprodvctions:parent:1.0.0`). Do **not** add version tags in `Calculator/pom.xml`.

| Dependency | Use |
|---|---|
| `org.apache.logging.log4j:log4j-core/api` | Logging |
| `org.apache.commons:commons-lang3` | String utilities (used in `CalculatorUtility`) |
| `net.sourceforge.jdatepicker:jdatepicker` | Date picker widget in `DatePanel` |
| `com.apple:AppleJavaExtensions` | Mac integration |
| JUnit Jupiter (api / engine / params) | Testing |
| Mockito (core + junit-jupiter) | Test mocking |

## Claude Agent Specific Capabilities
None at the moment. Add as necessary.
package Panels;

import Calculators.Calculator;
import Parent.ArgumentsForTests;
import Parent.TestParent;
import Types.SystemDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import static Types.Texts.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * BasicPanelTest
 * <p>
 * This class tests the {@link BasicPanel} class.
 * <p>
 * Each button is split between valid and invalid
 * test cases. Each test starts with finishing
 * constructing the Calculator. It then sets up
 * the when/then mocking for the ActionEvent. The
 * test is then performed, recording the history
 * for each button action. Finally, the history
 * is asserted against the actual calculator history.
 * The {@link ArgumentsForTests} class is used to
 * pass in the test parameters and expected results.
 * Pass in the arguments in the same manner as you
 * would if using the calculator manually.
 *
 * @author Michael Ball
 * @version 4.0
 */
class BasicPanelTest extends TestParent
{
    static { System.setProperty("appName", BasicPanelTest.class.getSimpleName()); }
    private static final Logger LOGGER = LogManager.getLogger(BasicPanelTest.class.getSimpleName());

    @BeforeAll
    static void beforeAll()
    { mocks = MockitoAnnotations.openMocks(BasicPanelTest.class); }

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(() -> {
            try
            {
                LOGGER.info("Starting test");
                calculator = spy(new Calculator());
                Preferences.userNodeForPackage(Calculator.class).clear(); // remove keys
                calculator.setSystemDetector(new SystemDetector());
                calculator.getBasicPanel().setCalculator(calculator); // links spyCalc to BasicPanel
            }
            catch (Exception ignored) {}
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(() -> {
            LOGGER.info("Test complete. Closing the calculator...");
            calculator.setVisible(false);
            calculator.dispose();
        });
    }

    @AfterAll
    static void afterAll()
    {
        LOGGER.info("Finished running {}", BasicPanelTest.class.getSimpleName());
        try
        {
            if (mocks != null)
            { mocks.close(); }
        }
        catch (Exception e)
        { LOGGER.error("Error closing mocks: {}", e.getMessage()); }
    }

    /* Valid PERCENT */
    @ParameterizedTest
    @DisplayName("Test Valid Percent Button Action")
    @MethodSource("validPercentButtonCases")
    void testValidPercentButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<ArgumentsForTests> validPercentButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(PERCENT).testName("0%").firstNumber("0").firstUnaryOperator(PERCENT).firstUnaryResult("0").build(),
                ArgumentsForTests.builder(PERCENT).testName("5%").firstNumber("5").firstUnaryOperator(PERCENT).firstUnaryResult("0.05").build(),
                ArgumentsForTests.builder(PERCENT).testName("-0.05%").firstNumber("-5").firstUnaryOperator(PERCENT).firstUnaryResult("-0.05").build(),
                ArgumentsForTests.builder(PERCENT).testName("0.0025%").firstNumber("0.25").firstUnaryOperator(PERCENT).firstUnaryResult("0.0025").build(),
                ArgumentsForTests.builder(PERCENT).testName("0.04% + 0.02% = 0.06% -").firstNumber("4").firstUnaryOperator(PERCENT).firstUnaryResult("0.04").firstBinaryOperator(ADDITION).firstBinaryResult("0.04|0.04 +").secondNumber("2").secondUnaryOperator(PERCENT).secondUnaryResult("0.02").secondBinaryOperator(SUBTRACTION).secondBinaryResult("0.06|0.06 -").build(),
                ArgumentsForTests.builder(PERCENT).testName("0.1% + 0.2% = 0.3% -").firstNumber("10").firstUnaryOperator(PERCENT).firstUnaryResult("0.1").firstBinaryOperator(ADDITION).firstBinaryResult("0.1|0.1 +").secondNumber("20").secondUnaryOperator(PERCENT).secondUnaryResult("0.2").secondBinaryOperator(SUBTRACTION).secondBinaryResult("0.3|0.3 -").build()
        );
    }

    /* Invalid PERCENT */
    @ParameterizedTest
    @DisplayName("Test Invalid Percent Button Action")
    @MethodSource("invalidPercentButtonCases")
    void testInvalidPercentButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<ArgumentsForTests> invalidPercentButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(PERCENT).testName("% with empty textPane").firstNumber(EMPTY).firstUnaryOperator(PERCENT).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(PERCENT).testName("% with INFINITY").firstNumber(INFINITY).firstUnaryOperator(PERCENT).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+INFINITY).build(),
                ArgumentsForTests.builder(PERCENT).testName("% with ENTER_A_NUMBER").firstNumber(ENTER_A_NUMBER).firstUnaryOperator(PERCENT).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(PERCENT).testName("% when binary operator is active").firstNumber("0.05").firstBinaryOperator(ADDITION).firstBinaryResult("0.05|0.05 "+ADDITION).secondUnaryOperator(PERCENT).secondUnaryResult(EMPTY+"|0.05 "+ADDITION).build()
        );
    }

    /* Valid SQUARE ROOT */
    @ParameterizedTest
    @DisplayName("Test Valid SquareRoot Button Action")
    @MethodSource("validSquareRootButtonCases")
    void testValidSquareRootCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<ArgumentsForTests> validSquareRootButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(SQUARE_ROOT).testName("25 √, 5").firstNumber("25").firstUnaryOperator(SQUARE_ROOT).firstUnaryResult("5").build(),
                ArgumentsForTests.builder(SQUARE_ROOT).testName("16 √, 4").firstNumber("16").firstUnaryOperator(SQUARE_ROOT).firstUnaryResult("4").build(),
                ArgumentsForTests.builder(SQUARE_ROOT).testName("0 √, 0").firstNumber("0").firstUnaryOperator(SQUARE_ROOT).firstUnaryResult("0").build(),
                ArgumentsForTests.builder(SQUARE_ROOT).testName("25.5 √, 5.049752469181039").firstNumber("25.5").firstUnaryOperator(SQUARE_ROOT).firstUnaryResult("5.049752469181039").build()
        );
    }

    /* Invalid SQUARE ROOT */
    @ParameterizedTest
    @DisplayName("Test Invalid SquareRoot Button Action")
    @MethodSource("invalidSquareRootButtonCases")
    void testInvalidSquareRootButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<ArgumentsForTests> invalidSquareRootButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(SQUARE_ROOT).testName("√ with empty textPane").firstUnaryOperator(SQUARE_ROOT).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(SQUARE_ROOT).testName("√ with INFINITY").firstNumber(INFINITY).firstUnaryOperator(SQUARE_ROOT).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+INFINITY).build(),
                ArgumentsForTests.builder(SQUARE_ROOT).testName("√ with ENTER_A_NUMBER").firstNumber(ENTER_A_NUMBER).firstUnaryOperator(SQUARE_ROOT).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(SQUARE_ROOT).testName("√ negative number").firstNumber("-5").firstUnaryOperator(SQUARE_ROOT).firstUnaryResult("-5|"+ONLY_POSITIVES).build(),
                ArgumentsForTests.builder(SQUARE_ROOT).testName("√ when binary operator is active").firstNumber("16").firstBinaryOperator(ADDITION).firstBinaryResult("16|16 "+ADDITION).secondUnaryOperator(SQUARE_ROOT).secondUnaryResult(EMPTY+"|16 "+ADDITION).build()
        );
    }

    /* Valid SQUARED */
    @ParameterizedTest
    @DisplayName("Test Valid Squared Button Action")
    @MethodSource("validSquaredButtonCases")
    void testValidSquaredButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<ArgumentsForTests> validSquaredButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(SQUARED).testName("5², 25").firstNumber("5").firstUnaryOperator(SQUARED).firstUnaryResult("25").build(),
                ArgumentsForTests.builder(SQUARED).testName("-4², 16").firstNumber("-4").firstUnaryOperator(SQUARED).firstUnaryResult("16").build(),
                ArgumentsForTests.builder(SQUARED).testName("0², 0").firstNumber("0").firstUnaryOperator(SQUARED).firstUnaryResult("0").build(),
                ArgumentsForTests.builder(SQUARED).testName("-5², 25").firstNumber("-5").firstUnaryOperator(SQUARED).firstUnaryResult("25").build(),
                ArgumentsForTests.builder(SQUARED).testName("2.5², 6.25").firstNumber("2.5").firstUnaryOperator(SQUARED).firstUnaryResult("6.25").build(),
                ArgumentsForTests.builder(SQUARED).testName("5.5², 30.25").firstNumber("5.5").firstUnaryOperator(SQUARED).firstUnaryResult("30.25").build(),
                ArgumentsForTests.builder(SQUARED).testName("5² + 4²").firstNumber("5").firstUnaryOperator(SQUARED).firstUnaryResult("25").firstBinaryOperator(ADDITION).firstBinaryResult("25|25 "+ADDITION).secondNumber("4").secondUnaryOperator(SQUARED).secondUnaryResult("16").build()
        );
    }

    /* Invalid SQUARED */
    @ParameterizedTest
    @DisplayName("Test Invalid Squared Button Action")
    @MethodSource("invalidSquaredButtonCases")
    void testInvalidSquaredButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<ArgumentsForTests> invalidSquaredButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(SQUARED).testName("x² with empty textPane")
                        .firstUnaryOperator(SQUARED).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(SQUARED).testName("x² with ENTER_A_NUMBER")
                        .firstNumber(ENTER_A_NUMBER).firstUnaryOperator(SQUARED).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(SQUARED).testName("x² when binary operator is active")
                        .firstNumber("5").firstBinaryOperator(ADDITION).firstBinaryResult("5|5 "+ADDITION)
                        .secondUnaryOperator(SQUARED).secondUnaryResult(EMPTY+"|5 "+ADDITION).build()
        );
    }

    /* Valid FRACTION */
    @ParameterizedTest()
    @DisplayName("Test Valid Fraction Button Action")
    @MethodSource("validFractionCases")
    void testValidFractionButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<ArgumentsForTests> validFractionCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(FRACTION).testName("0⅟x, 0|INFINITY").firstNumber("0").firstUnaryOperator(FRACTION).firstUnaryResult("0|"+INFINITY).build(),
                ArgumentsForTests.builder(FRACTION).testName("5⅟x, 0.2").firstNumber("5").firstUnaryOperator(FRACTION).firstUnaryResult("0.2").build(),
                ArgumentsForTests.builder(FRACTION).testName("0.25⅟x, 4").firstNumber("0.25").firstUnaryOperator(FRACTION).firstUnaryResult("4").build(),
                ArgumentsForTests.builder(FRACTION).testName("4⅟x, 0.25 + 2⅟x, 0.5 -, 0.75|0.75 -").firstNumber("4").firstUnaryOperator(FRACTION).firstUnaryResult("0.25").firstBinaryOperator(ADDITION).firstBinaryResult("0.25|0.25 +").secondNumber("2").secondUnaryOperator(FRACTION).secondUnaryResult("0.5").secondBinaryOperator(SUBTRACTION).secondBinaryResult("0.75|0.75 -").build()
        );
    }

    /* Invalid FRACTION */
    @ParameterizedTest()
    @DisplayName("Test Invalid Fraction Button Action")
    @MethodSource("getInvalidFractionCases")
    void testInvalidFractionButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<ArgumentsForTests> getInvalidFractionCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(FRACTION).testName("⅟x with empty textPane")
                        .firstUnaryOperator(FRACTION).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(FRACTION).testName("⅟x with ENTER_A_NUMBER")
                        .firstNumber(ENTER_A_NUMBER).firstUnaryOperator(FRACTION).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(FRACTION).testName("⅟x with INFINITY")
                        .firstNumber(INFINITY).firstUnaryOperator(FRACTION).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+INFINITY).build(),
                ArgumentsForTests.builder(FRACTION).testName("⅟x when binary operator is active")
                        .firstNumber("5").firstBinaryOperator(ADDITION).firstBinaryResult("5|5 "+ADDITION)
                        .secondUnaryOperator(FRACTION).secondUnaryResult(EMPTY+"|5 "+ADDITION).build()
        );
    }

    /* Valid CLEAR ENTRY */
    @ParameterizedTest
    @DisplayName("Test Valid ClearEntry Button Action")
    @MethodSource("validClearEntryCases")
    void testValidClearEntryButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<ArgumentsForTests> validClearEntryCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(CLEAR_ENTRY).testName("CLEAR ENTRY clears textPane").firstNumber(EMPTY).firstUnaryOperator(CLEAR_ENTRY).firstUnaryResult(EMPTY).build(),
                ArgumentsForTests.builder(CLEAR_ENTRY).testName("CLEAR ENTRY clears 100").firstNumber("100").firstUnaryOperator(CLEAR_ENTRY).firstUnaryResult(EMPTY).build(),
                ArgumentsForTests.builder(CLEAR_ENTRY).testName("CLEAR ENTRY clears 100 +").firstNumber("100").firstBinaryOperator(ADDITION).firstBinaryResult("100|100 "+ADDITION).secondUnaryOperator(CLEAR_ENTRY).secondUnaryResult(EMPTY).build(),
                ArgumentsForTests.builder(CLEAR_ENTRY).testName("CLEAR ENTRY only 100").firstNumber("2,727").firstBinaryOperator(SUBTRACTION).firstBinaryResult("2,727|2,727 "+SUBTRACTION).secondNumber("100").secondUnaryOperator(CLEAR_ENTRY).secondUnaryResult(EMPTY).build(),
                ArgumentsForTests.builder(CLEAR_ENTRY).testName("CLEAR ENTRY only 2,727").firstNumber("3.14").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("3.14|3.14 "+MULTIPLICATION).secondNumber("2,727").secondUnaryOperator(CLEAR_ENTRY).secondUnaryResult(EMPTY).build()
        );
    }

    /* Invalid CLEAR ENTRY */

    /* Valid CLEAR */
    @ParameterizedTest
    @DisplayName("Test Valid Clear Button Action")
    @MethodSource("validClearButtonCases")
    void testValidClearButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<ArgumentsForTests> validClearButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(CLEAR).testName("CLEAR default state").firstNumber(EMPTY).firstUnaryOperator(CLEAR).firstUnaryResult(EMPTY+"|0").build(),
                ArgumentsForTests.builder(CLEAR).testName("CLEAR 1 number").firstNumber("100").firstUnaryOperator(CLEAR).firstUnaryResult(EMPTY+"|0").build(),
                ArgumentsForTests.builder(CLEAR).testName("CLEAR 1 number and operator").firstNumber("100").firstBinaryOperator(ADDITION).firstBinaryResult("100|100 "+ADDITION).secondUnaryOperator(CLEAR).secondUnaryResult(EMPTY+"|0").build(),
                ArgumentsForTests.builder(CLEAR).testName("CLEAR 2 numbers and operator").firstNumber("2,727").firstBinaryOperator(SUBTRACTION).firstBinaryResult("2,727|2,727 "+SUBTRACTION).secondNumber("100").secondUnaryOperator(CLEAR).secondUnaryResult(EMPTY+"|0").build()
        );
    }

    /* Invalid CLEAR */

    /* Valid DELETE ⌫ */
    @ParameterizedTest
    @DisplayName("Test Valid Delete Button Action")
    @MethodSource("validDeleteButtonCases")
    void testValidDeleteButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<ArgumentsForTests> validDeleteButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(DELETE).testName("Delete from 35, 3").firstNumber("35").firstUnaryOperator(DELETE).firstUnaryResult("3"),
                ArgumentsForTests.builder(DELETE).testName("Delete from 3, EMPTY").firstNumber("3").firstUnaryOperator(DELETE).firstUnaryResult(EMPTY),
                ArgumentsForTests.builder(DELETE).testName("Delete from 35.12, 35.1").firstNumber("35.12").firstUnaryOperator(DELETE).firstUnaryResult("35.1"),
                ArgumentsForTests.builder(DELETE).testName("Delete from 35.1, 35.").firstNumber("35.1").firstUnaryOperator(DELETE).firstUnaryResult("35."),
                ArgumentsForTests.builder(DELETE).testName("Delete 3, EMPTY, Delete again, ENTER_A_NUMBER").firstNumber("3").firstUnaryOperator(DELETE).firstUnaryResult(EMPTY).firstBinaryResult(DELETE).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER),
                ArgumentsForTests.builder(DELETE).testName("Delete after ADDITION").firstNumber("35").firstBinaryOperator(ADDITION).firstBinaryResult("35|35 "+ADDITION).secondUnaryOperator(DELETE).secondUnaryResult("35"),
                ArgumentsForTests.builder(DELETE).testName("Delete after SUBTRACTION").firstNumber("1").firstBinaryOperator(SUBTRACTION).firstBinaryResult("1|1 "+SUBTRACTION).secondUnaryOperator(DELETE).secondUnaryResult("1"),
                ArgumentsForTests.builder(DELETE).testName("Delete after badText (ENTER_A_NUMBER)").firstNumber(ENTER_A_NUMBER).firstUnaryResult(DELETE).firstUnaryResult(EMPTY),
                ArgumentsForTests.builder(DELETE).testName("Delete when EMPTY").firstUnaryOperator(DELETE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER),
                ArgumentsForTests.builder(DELETE).testName("Delete readjusts commas").firstNumber("12,345").firstUnaryOperator(DELETE).firstUnaryResult("1,234")
        );
    }
    /* Invalid DELETE */

    /* Valid ADD */
    @ParameterizedTest
    @DisplayName("Test Valid Add Button Action")
    @MethodSource("validAddButtonCases")
    void testValidAddTestCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<ArgumentsForTests> validAddButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(ADDITION).testName("1 +").firstNumber("1").firstBinaryOperator(ADDITION).firstBinaryResult("1|1 "+ADDITION),
                ArgumentsForTests.builder(ADDITION).testName("1 + 2 = 3").firstNumber("1").firstBinaryOperator(ADDITION).firstBinaryResult("1|1 "+ADDITION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"3"),
                ArgumentsForTests.builder(ADDITION).testName("1.5 + 2.3 = 3.8").firstNumber("1.5").firstBinaryOperator(ADDITION).firstBinaryResult("1.5|1.5 "+ADDITION).secondNumber("2.3").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"3.8"),
                ArgumentsForTests.builder(ADDITION).testName("1 + 5 = 6 +")
                        .firstNumber("1").firstBinaryOperator(ADDITION).firstBinaryResult("1|1 "+ADDITION)
                        .secondNumber("5").secondUnaryOperator(EQUALS).secondUnaryResult(EMPTY+ARGUMENT_SEPARATOR+"6")
                        .secondBinaryOperator(ADDITION).secondBinaryResult(EMPTY+"|6 "+ADDITION).build()
        );
    }

    @Test
    void testPressedAdditionAfterEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(EQUALS).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        calculator.performAddButtonAction(actionEvent);
        assertEquals("-4 +", calculator.getTextPaneValue(), "Expected textPane to be -4 +");
        assertEquals("-4", calculator.getValues()[0], "Expected values[0] to be -4");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");
    }

    /* Invalid ADD */
    @ParameterizedTest
    @DisplayName("Test Invalid Add Button Action")
    @MethodSource("invalidAddButtonCases")
    void testInvalidAddTestCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<ArgumentsForTests> invalidAddButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(ADDITION).testName("ADD with empty textPane").firstBinaryOperator(ADDITION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(ADDITION).testName("1 + +").firstNumber("1").firstUnaryOperator(ADDITION).firstUnaryResult("1|1 "+ADDITION).firstBinaryOperator(ADDITION).firstBinaryResult("1|1 "+ADDITION).build(),
                ArgumentsForTests.builder(ADDITION).testName("ADD with badText (ENTER_A_NUMBER)").firstNumber(ENTER_A_NUMBER).firstBinaryOperator(ADDITION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(ADDITION).testName("ADDITION with badText (ERROR)").firstNumber(ERROR).firstBinaryOperator(ADDITION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ERROR).build()
        );
    }

    /* Valid SUBTRACT */
    @ParameterizedTest
    @DisplayName("Test Valid Subtract Button Action")
    @MethodSource("validSubtractButtonCases")
    void testValidSubtractTestCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<ArgumentsForTests> validSubtractButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(SUBTRACTION).testName("1 -").firstNumber("1").firstBinaryOperator(SUBTRACTION).firstBinaryResult("1|1 "+SUBTRACTION),
                ArgumentsForTests.builder(SUBTRACTION).testName("1 - 2 = -1").firstNumber("1").firstBinaryOperator(SUBTRACTION).firstBinaryResult("1|1 "+SUBTRACTION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"-1"),
                ArgumentsForTests.builder(SUBTRACTION).testName("1.5 - 2.3 = -0.8").firstNumber("1.5").firstBinaryOperator(SUBTRACTION).firstBinaryResult("1.5|1.5 "+SUBTRACTION).secondNumber("2.3").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"-0.8"),
                ArgumentsForTests.builder(SUBTRACTION).testName("-, Number is negative").firstBinaryOperator(SUBTRACTION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+SUBTRACTION).build(),
                ArgumentsForTests.builder(SUBTRACTION).testName("1 - -, Operator is set, next number is negative").firstNumber("1").firstUnaryOperator(SUBTRACTION).firstUnaryResult("1|1 "+SUBTRACTION).firstBinaryOperator(SUBTRACTION).firstBinaryResult("1|"+SUBTRACTION).build()
        );
    }

    @Test
    @DisplayName("-5 - -5 =")
    void pressedSubtractThen5ThenSubtractThenSubtractThen5()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(SUBTRACTION).thenReturn(FIVE)
                .thenReturn(SUBTRACTION)
                .thenReturn(SUBTRACTION).thenReturn(FIVE)
                .thenReturn(EQUALS);
        calculator.performSubtractButtonAction(actionEvent);
        assertTrue(calculator.isNegativeNumber(), "Expected isNumberNegative to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("-5", calculator.getValues()[0], "Values[0] is not -5");
        assertEquals("-5", calculator.getTextPaneValue(), "textPane does not equal -5");
        calculator.performSubtractButtonAction(actionEvent);
        assertFalse(calculator.isNegativeNumber(), "Expected isNumberNegative to be false");
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
        calculator.performSubtractButtonAction(actionEvent);
        assertTrue(calculator.isNegativeNumber(), "Expected isNumberNegative to be true");
        calculator.performNumberButtonAction(actionEvent);
        //assertEquals(-5, Double.parseDouble(calculator.getValues()[1]), delta, "Values[1] is not -5");
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(EMPTY, calculator.getValues()[0], "Values[0] is blank");
        assertEquals(ZERO, calculator.getTextPaneValue(), "textPane does not equal 0");
    }

    /* Invalid SUBTRACT */
    @ParameterizedTest
    @DisplayName("Test Invalid Subtract Button Action")
    @MethodSource("invalidSubtractButtonCases")
    void testInvalidSubtractTestCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<ArgumentsForTests> invalidSubtractButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(SUBTRACTION).testName("SUBTRACT with badText (ENTER_A_NUMBER)").firstNumber(ENTER_A_NUMBER).firstBinaryOperator(SUBTRACTION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(SUBTRACTION).testName("SUBTRACT with badText (ERROR)").firstNumber(ERROR).firstBinaryOperator(SUBTRACTION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ERROR).build()
        );
    }

    /* Valid MULTIPLY */
    @ParameterizedTest
    @DisplayName("Test Valid Multiply Button Action")
    @MethodSource("validMultiplyButtonCases")
    void testValidMultiplyTestCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<ArgumentsForTests> validMultiplyButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(MULTIPLICATION).testName("1 ✕").firstNumber("1").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("1|1 "+MULTIPLICATION),
                ArgumentsForTests.builder(MULTIPLICATION).testName("1 ✕ 2 = 2").firstNumber("1").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("1|1 "+MULTIPLICATION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"2"),
                ArgumentsForTests.builder(MULTIPLICATION).testName("1.5 ✕ 2.3 = 3.45").firstNumber("1.5").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("1.5|1.5 "+MULTIPLICATION).secondNumber("2.3").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"3.45"),
                ArgumentsForTests.builder(MULTIPLICATION).testName("5.5 ✕ 10.2 = 56.1").firstNumber("5.5").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("5.5|5.5 "+MULTIPLICATION).secondNumber("10.2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"56.1"),
                ArgumentsForTests.builder(MULTIPLICATION).testName("15 ✕").firstNumber("15").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("15|15 "+MULTIPLICATION)
        );
    }

    /* Invalid MULTIPLY */
    @ParameterizedTest
    @DisplayName("Test Invalid Multiply Button Action")
    @MethodSource("invalidMultiplyButtonCases")
    void testInvalidMultiplyTestCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<ArgumentsForTests> invalidMultiplyButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(MULTIPLICATION).testName("✕ with empty textPane")
                        .firstBinaryOperator(MULTIPLICATION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(MULTIPLICATION).testName("1 ✕ ✕")
                        .firstNumber("1").firstUnaryOperator(MULTIPLICATION).firstUnaryResult("1|1 "+MULTIPLICATION).firstBinaryOperator(MULTIPLICATION).firstBinaryResult("1|1 "+MULTIPLICATION).build(),
                ArgumentsForTests.builder(MULTIPLICATION).testName("✕ with badText (ENTER_A_NUMBER)")
                        .firstNumber(ENTER_A_NUMBER).firstBinaryOperator(MULTIPLICATION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(MULTIPLICATION).testName("✕ with badText (ERROR)")
                        .firstNumber(ERROR).firstBinaryOperator(MULTIPLICATION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ERROR).build()
        );
    }

    /* Valid DIVIDE */
    @ParameterizedTest
    @DisplayName("Test Valid Divide Button Action")
    @MethodSource("validDivideButtonCases")
    void testValidDivideTestCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<ArgumentsForTests> validDivideButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(DIVISION).testName("1 ÷").firstNumber("1").firstBinaryOperator(DIVISION).firstBinaryResult("1|1 "+DIVISION),
                ArgumentsForTests.builder(DIVISION).testName("1 ÷ 2 = 0.5").firstNumber("1").firstBinaryOperator(DIVISION).firstBinaryResult("1|1 "+DIVISION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"0.5"),
                ArgumentsForTests.builder(DIVISION).testName("1.5 ÷ 2.3 = 0.6521739130434782608695652173913043").firstNumber("1.5").firstBinaryOperator(DIVISION).firstBinaryResult("1.5|1.5 "+DIVISION).secondNumber("2.3").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"0.6521739130434782608695652173913043"),
                ArgumentsForTests.builder(DIVISION).testName("15 ÷").firstNumber("15").firstBinaryOperator(DIVISION).firstBinaryResult("15|15 "+DIVISION)
        );
    }

    /* Invalid DIVIDE */
    @ParameterizedTest
    @DisplayName("Test Invalid Divide Button Action")
    @MethodSource("invalidDivideButtonCases")
    void testInvalidDivideTestCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<ArgumentsForTests> invalidDivideButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(DIVISION).testName("÷ with empty textPane").firstBinaryOperator(DIVISION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(DIVISION).testName("1 ÷ ÷").firstNumber("1").firstUnaryOperator(DIVISION).firstUnaryResult("1|1 "+DIVISION).firstBinaryOperator(DIVISION).firstBinaryResult("1|1 "+DIVISION).build(),
                ArgumentsForTests.builder(DIVISION).testName("÷ with badText (ENTER_A_NUMBER)").firstNumber(ENTER_A_NUMBER).firstBinaryOperator(DIVISION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(DIVISION).testName("÷ with badText (ERROR)").firstNumber(ERROR).firstBinaryOperator(DIVISION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ERROR).build(),
                ArgumentsForTests.builder(DIVISION).testName("15.5 ÷ 0 = INFINITY").firstNumber("15.5").firstBinaryOperator(DIVISION).firstBinaryResult("15.5|15.5 "+DIVISION).secondNumber("0").firstBinaryOperator(DIVISION).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+INFINITY).build()
        );
    }

    /* Valid NUMBERS */
    @ParameterizedTest
    @DisplayName("Test Valid Number Button Cases")
    @MethodSource("validNumberButtonCases")
    void testValidNumberButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<ArgumentsForTests> validNumberButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder("0").testName("Enter 0").firstNumber("0").build(),
                ArgumentsForTests.builder("1").testName("Enter 1").firstNumber("1").build(),
                ArgumentsForTests.builder("2").testName("Enter 2").firstNumber("2").build(),
                ArgumentsForTests.builder("3").testName("Enter 3").firstNumber("3").build(),
                ArgumentsForTests.builder("4").testName("Enter 4").firstNumber("4").build(),
                ArgumentsForTests.builder("5").testName("Enter 5").firstNumber("5").build(),
                ArgumentsForTests.builder("6").testName("Enter 6").firstNumber("6").build(),
                ArgumentsForTests.builder("7").testName("Enter 7").firstNumber("7").build(),
                ArgumentsForTests.builder("8").testName("Enter 8").firstNumber("8").build(),
                ArgumentsForTests.builder("9").testName("Enter 9").firstNumber("9").build(),
                ArgumentsForTests.builder("1").testName("1 +").firstNumber("1").firstBinaryOperator(ADDITION).firstBinaryResult("1|1 "+ADDITION).build(),
                ArgumentsForTests.builder("1").testName("1 -").firstNumber("1").firstBinaryOperator(SUBTRACTION).firstBinaryResult("1|1 "+SUBTRACTION).build(),
                ArgumentsForTests.builder("1").testName("1 ✕").firstNumber("1").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("1|1 "+MULTIPLICATION).build(),
                ArgumentsForTests.builder("1").testName("1 ÷").firstNumber("1").firstBinaryOperator(DIVISION).firstBinaryResult("1|1 "+DIVISION).build(),
                ArgumentsForTests.builder("1").testName("Enter 15").firstNumber("15").build(),
                ArgumentsForTests.builder("1").testName("Enter number with badText (ENTER_A_NUMBER) displayed").firstNumber(ENTER_A_NUMBER).secondNumber("150").build(),
                ArgumentsForTests.builder("1.008").testName("Press √ on 1.008").initialState(ArgumentsForTests.builder("1").firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+"1.0008").build()).firstUnaryOperator(SQUARE_ROOT).firstUnaryResult("1.000399920031984").build()
        );
    }

    /* Invalid NUMBERS ?? */

    /* Valid NEGATE */
    @ParameterizedTest
    @DisplayName("Test Negate Button Action")
    @MethodSource("validNegateButtonCases")
    void testValidNegateButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<ArgumentsForTests> validNegateButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(NEGATE).testName("1 NEGATE").firstNumber("1").firstUnaryOperator(NEGATE).firstUnaryResult("-1").build(),
                ArgumentsForTests.builder(NEGATE).testName("-1 NEGATE").firstNumber("-1").firstUnaryOperator(NEGATE).firstUnaryResult("1").build(),
                ArgumentsForTests.builder(NEGATE).testName("1 NEGATE NEGATE").firstNumber("1").firstUnaryOperator(NEGATE).firstUnaryResult("-1").firstBinaryOperator(NEGATE).firstBinaryResult("1").build(),
                ArgumentsForTests.builder(NEGATE).testName("1 NEGATE +").firstNumber("1").firstUnaryOperator(NEGATE).firstUnaryResult("-1").firstBinaryOperator(ADDITION).firstBinaryResult("-1|-1 "+ADDITION).build(),
                ArgumentsForTests.builder(NEGATE).testName("5 NEGATE + 2 NEGATE + 3 +").firstNumber("-5").firstUnaryOperator(NEGATE).firstUnaryResult("5").firstBinaryOperator(ADDITION).firstBinaryResult("5|5 "+ADDITION).secondNumber("2").secondUnaryOperator(NEGATE).secondUnaryResult("-2").secondBinaryOperator(ADDITION).secondBinaryResult("3|3 "+ADDITION).build()
        );
    }

    /* Invalid NEGATE */
    @ParameterizedTest
    @DisplayName("Test Invalid Negate Button Action")
    @MethodSource("invalidNegateButtonCases")
    void testInvalidNegateButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<ArgumentsForTests> invalidNegateButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(NEGATE).testName("NEGATE with empty textPane").firstUnaryOperator(NEGATE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(NEGATE).testName("NEGATE with badText (ENTER_A_NUMBER)").firstNumber(ENTER_A_NUMBER).firstUnaryOperator(NEGATE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(NEGATE).testName("NEGATE with badText (INFINITY)").firstNumber(INFINITY).firstUnaryOperator(NEGATE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+INFINITY).build(),
                ArgumentsForTests.builder(NEGATE).testName("NEGATE with badText (ERROR)").firstNumber(ERROR).firstUnaryOperator(NEGATE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ERROR).build()
        );
    }

    /* Valid DECIMAL */
    @ParameterizedTest
    @DisplayName("Test Valid Decimal Button Action")
    @MethodSource("validDecimalButtonCases")
    void testValidDecimalButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<ArgumentsForTests> validDecimalButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(DECIMAL).testName("DECIMAL with ENTER_A_NUMBER").firstNumber(ENTER_A_NUMBER).firstUnaryOperator(DECIMAL).firstUnaryResult("0.").build(),
                ArgumentsForTests.builder(DECIMAL).testName("0.").firstNumber("0.").build(),
                ArgumentsForTests.builder(DECIMAL).testName("554.").firstNumber("544.").build(),
                ArgumentsForTests.builder(DECIMAL).testName("1,234.").firstNumber("1,234.").build(),
                ArgumentsForTests.builder(DECIMAL).testName("1.5 + 2").firstNumber("1.5").firstBinaryOperator(ADDITION).firstBinaryResult("1.5|1.5 "+ADDITION).secondNumber("2").build()
        );
    }

    /* Invalid DECIMAL */

    /* Valid EQUALS */
    @ParameterizedTest
    @DisplayName("Test Valid Equals Button Action")
    @MethodSource("validEqualsButtonCases")
    void testValidEqualsTestCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<ArgumentsForTests> validEqualsButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(ADDITION).testName("1 + 2 = 3").firstNumber("1").firstBinaryOperator(ADDITION).firstBinaryResult("1|1 "+ADDITION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"3"),
                ArgumentsForTests.builder(SUBTRACTION).testName("1 - 2 = 3").firstNumber("1").firstBinaryOperator(SUBTRACTION).firstBinaryResult("1|1 "+SUBTRACTION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"-1"),
                ArgumentsForTests.builder(MULTIPLICATION).testName("1 ✕ 2 = 3").firstNumber("1").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("1|1 "+MULTIPLICATION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"2"),
                ArgumentsForTests.builder(DIVISION).testName("1 ÷ 2 = 0.5").firstNumber("1").firstBinaryOperator(DIVISION).firstBinaryResult("1|1 "+DIVISION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"0.5"),
                ArgumentsForTests.builder(ADDITION).testName("5 SQUARED + 4 SQUARED = 41").firstNumber("5").firstUnaryOperator(SQUARED).firstUnaryResult("25").firstBinaryOperator(ADDITION).firstBinaryResult("25|25 "+ADDITION).secondNumber("4").secondUnaryOperator(SQUARED).secondUnaryResult("16").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"41")
        );
    }

    /* Invalid EQUALS (that's it) */
    @ParameterizedTest
    @DisplayName("Test Invalid Equals Button Action")
    @MethodSource("invalidEqualsButtonCases")
    void testInvalidEqualsButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<ArgumentsForTests> invalidEqualsButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(EQUALS).testName("-6 + =").firstNumber("-6").firstUnaryOperator(ADDITION).firstUnaryResult("-6|-6 "+ADDITION).firstBinaryOperator(EQUALS).firstBinaryResult("-6|"+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(EQUALS).testName("6 + =").firstNumber("6").firstUnaryOperator(ADDITION).firstUnaryResult("6|6 "+ADDITION).firstBinaryOperator(EQUALS).firstBinaryResult("6|"+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(EQUALS).testName("6 - =").firstNumber("6").firstUnaryOperator(SUBTRACTION).firstUnaryResult("6|6 "+SUBTRACTION).firstBinaryOperator(EQUALS).firstBinaryResult("6|"+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(EQUALS).testName("6 ✕ =").firstNumber("6").firstUnaryOperator(MULTIPLICATION).firstUnaryResult("6|6 "+MULTIPLICATION).firstBinaryOperator(EQUALS).firstBinaryResult("6|"+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(EQUALS).testName("6 ÷ =").firstNumber("6").firstUnaryOperator(DIVISION).firstUnaryResult("6|6 "+DIVISION).firstBinaryOperator(EQUALS).firstBinaryResult("6|"+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(EQUALS).testName("5.5 + =").firstNumber("5.5").firstUnaryOperator(ADDITION).firstUnaryResult("5.5|5.5 "+ADDITION).firstBinaryOperator(EQUALS).firstBinaryResult("5.5|"+ENTER_A_NUMBER).build()
        );
    }
    /*############## Test Button Actions ##################*/
}

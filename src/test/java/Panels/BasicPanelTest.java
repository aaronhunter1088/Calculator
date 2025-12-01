package Panels;

import Calculators.Calculator;
import Parent.ArgumentsForTests;
import Parent.TestParent;
import Types.SystemDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
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
    private static Stream<Arguments> validPercentButtonCases()
    {
        /*
        * case 1: Input 0, press %, expect 0
        * case 2: Input 5, press %, expect 0.05
        * case 3: Input -5, press %, expect -0.05
        * case 4: Input 0.25, press %, expect 0.0025
        * case 5: Input 4, press %, expect 0.04, press +, expect 0.04 +, Input 2, expect 2, press %, expect 0.02, press -, expect 0.06
        * case 6: Input 10, press %, expect 0.1, press +, expect 0.1 +, Input 20, expect 20, press %, expect 0.2, press -, expect 0.3
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(PERCENT)
                        .firstNumber("0").firstUnaryOperator(PERCENT).firstUnaryResult("0").build()),
                Arguments.of(ArgumentsForTests.builder(PERCENT)
                        .firstNumber("5").firstUnaryOperator(PERCENT).firstUnaryResult("0.05").build()),
                Arguments.of(ArgumentsForTests.builder(PERCENT)
                .firstNumber("-5").firstUnaryOperator(PERCENT).firstUnaryResult("-0.05").build()),
                Arguments.of(ArgumentsForTests.builder(PERCENT)
                        .firstNumber("0.25").firstUnaryOperator(PERCENT).firstUnaryResult("0.0025").build()),
                Arguments.of(ArgumentsForTests.builder(PERCENT)
                .firstNumber("4").firstUnaryOperator(PERCENT).firstUnaryResult("0.04")
                        .firstBinaryOperator(ADDITION).firstBinaryResult("0.04|0.04 +")
                        .secondNumber("2").secondUnaryOperator(PERCENT).secondUnaryResult("0.02")
                        .secondBinaryOperator(SUBTRACTION).secondBinaryResult("0.06|0.06 -").build()),
                Arguments.of(ArgumentsForTests.builder(PERCENT)
                        .firstNumber("10").firstUnaryOperator(PERCENT).firstUnaryResult("0.1")
                        .firstBinaryOperator(ADDITION).firstBinaryResult("0.1|0.1 +")
                        .secondNumber("20").secondUnaryOperator(PERCENT).secondUnaryResult("0.2")
                        .secondBinaryOperator(SUBTRACTION).secondBinaryResult("0.3|0.3 -").build())
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
    private static Stream<Arguments> invalidPercentButtonCases()
    {
        /*
         * case 1: cannot press % when textPane is empty
         * case 2: cannot press % when textPane is badText (INFINITY)
         * case 3: cannot press % when textPane is badText (ENTER_A_NUMBER)
         * case 4: cannot press % when operator is active
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(PERCENT)
                        .firstNumber(EMPTY).firstUnaryOperator(PERCENT).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(PERCENT)
                        .firstNumber(INFINITY).firstUnaryOperator(PERCENT).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+INFINITY).build()),
                Arguments.of(ArgumentsForTests.builder(PERCENT)
                        .firstNumber(ENTER_A_NUMBER).firstUnaryOperator(PERCENT).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(PERCENT)
                        .firstNumber("0.05").firstBinaryOperator(ADDITION).firstBinaryResult("0.05|0.05 "+ADDITION)
                        .secondUnaryOperator(PERCENT).secondUnaryResult(EMPTY+"|0.05 "+ADDITION).build())
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
    private static Stream<Arguments> validSquareRootButtonCases()
    {
        /*
         * case 1: Input 25. Press SQUARE ROOT. Expect 5.
         * case 2: Input 16. Press SQUARE ROOT. Expect 4.
         * case 3: Input 0. Press SQUARE ROOT. Expect 0.
         * case 4: Input 25.5. Press SQUARE ROOT. Expect 5.049752469181039.
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(SQUARE_ROOT).firstNumber("25").firstUnaryOperator(SQUARE_ROOT).firstUnaryResult("5").build()),
                Arguments.of(ArgumentsForTests.builder(SQUARE_ROOT).firstNumber("16").firstUnaryOperator(SQUARE_ROOT).firstUnaryResult("4").build()),
                Arguments.of(ArgumentsForTests.builder(SQUARE_ROOT).firstNumber("0").firstUnaryOperator(SQUARE_ROOT).firstUnaryResult("0").build()),
                Arguments.of(ArgumentsForTests.builder(SQUARE_ROOT).firstNumber("25.5").firstUnaryOperator(SQUARE_ROOT).firstUnaryResult("5.049752469181039").build())
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
    private static Stream<Arguments> invalidSquareRootButtonCases()
    {
        /*
         * case 1: cannot press SQUARE ROOT when textPane is empty
         * case 2: cannot press SQUARE ROOT when textPane is badText (INFINITY)
         * case 3: cannot press SQUARE ROOT when textPane is badText (ENTER_A_NUMBER)
         * case 4: cannot press SQUARE ROOT when current value is negative
         * case 5: cannot press SQUARE ROOT when operator is active
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(SQUARE_ROOT)
                        .firstUnaryOperator(SQUARE_ROOT).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(SQUARE_ROOT)
                        .firstNumber(INFINITY).firstUnaryOperator(SQUARE_ROOT).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+INFINITY).build()),
                Arguments.of(ArgumentsForTests.builder(SQUARE_ROOT)
                        .firstNumber(ENTER_A_NUMBER).firstUnaryOperator(SQUARE_ROOT).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(SQUARE_ROOT)
                        .firstNumber("-5").firstUnaryOperator(SQUARE_ROOT).firstUnaryResult("-5|"+ONLY_POSITIVES).build()),
                Arguments.of(ArgumentsForTests.builder(SQUARE_ROOT)
                        .firstNumber("16").firstBinaryOperator(ADDITION).firstBinaryResult("16|16 "+ADDITION)
                        .secondUnaryOperator(SQUARE_ROOT).secondUnaryResult(EMPTY+"|16 "+ADDITION).build())
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
    private static Stream<Arguments> validSquaredButtonCases()
    {
        /*
         * case 1: Input 5. Press SQUARED. Expect 25.
         * case 2: Input -4. Press SQUARED. Expect 16.
         * case 3: Input 0. Press SQUARED. Expect 0.
         * case 4: Input -4. Press SQUARED. Expect 16.
         * case 5: Input -5. Press SQUARED. Expect 25.
         * case 6: Input 2.5. Press SQUARED. Expect 6.25.
         * case 7: Input 5.5. Press SQUARED. Expect 30.25.
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(SQUARED).simpleName("5²").firstNumber("5").firstUnaryOperator(SQUARED).firstUnaryResult("25").build()),
                Arguments.of(ArgumentsForTests.builder(SQUARED).simpleName("-4²").firstNumber("-4").firstUnaryOperator(SQUARED).firstUnaryResult("16").build()),
                Arguments.of(ArgumentsForTests.builder(SQUARED).simpleName("0²").firstNumber("0").firstUnaryOperator(SQUARED).firstUnaryResult("0").build()),
                Arguments.of(ArgumentsForTests.builder(SQUARED).simpleName("-5²").firstNumber("-5").firstUnaryOperator(SQUARED).firstUnaryResult("25").build()),
                Arguments.of(ArgumentsForTests.builder(SQUARED).simpleName("2.5²").firstNumber("2.5").firstUnaryOperator(SQUARED).firstUnaryResult("6.25").build()),
                Arguments.of(ArgumentsForTests.builder(SQUARED).simpleName("5.5²").firstNumber("5.5").firstUnaryOperator(SQUARED).firstUnaryResult("30.25").build()),
                Arguments.of(ArgumentsForTests.builder(SQUARED).simpleName("5² + 4²").firstNumber("5").firstUnaryOperator(SQUARED).firstUnaryResult("25").firstBinaryOperator(ADDITION).firstBinaryResult("25|25 "+ADDITION).secondNumber("4").secondUnaryOperator(SQUARED).secondUnaryResult("16").build())
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
    private static Stream<Arguments> invalidSquaredButtonCases()
    {
        /*
         * case 1: No firstNumber. Pressed SQUARED. Expect ENTER_A_NUMBER
         * case 2: firstNumber is badText. Press SQUARED. Expect ENTER_A_NUMBER
         * case 3: Enter number. Enter binary operator. Press SQUARED. Expect number + operator
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(SQUARED)
                        .firstUnaryOperator(SQUARED).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(SQUARED)
                        .firstNumber(ENTER_A_NUMBER).firstUnaryOperator(SQUARED).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(SQUARED)
                        .firstNumber("5").firstBinaryOperator(ADDITION).firstBinaryResult("5|5 "+ADDITION)
                        .secondUnaryOperator(SQUARED).secondUnaryResult(EMPTY+"|5 "+ADDITION).build())
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
    private static Stream<Arguments> validFractionCases()
    {
        /*
        String firstNumber, (FRACTION button pressed) String firstResult,
        String firstOperator, String secondNumber, (FRACTION button pressed) String secondResult,
        String secondOperator
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(FRACTION)
                        .firstNumber("0").firstUnaryOperator(FRACTION).firstUnaryResult("0|"+INFINITY).build()),
                Arguments.of(ArgumentsForTests.builder(FRACTION)
                        .firstNumber("5").firstUnaryOperator(FRACTION).firstUnaryResult("0.2").build()),
                Arguments.of(ArgumentsForTests.builder(FRACTION)
                        .firstNumber("0.25").firstUnaryOperator(FRACTION).firstUnaryResult("4").build()),
                Arguments.of(ArgumentsForTests.builder(FRACTION)
                        .firstNumber("4").firstUnaryOperator(FRACTION).firstUnaryResult("0.25")
                        .firstBinaryOperator(ADDITION).firstBinaryResult("0.25|0.25 +")
                        .secondNumber("2").secondUnaryOperator(FRACTION).secondUnaryResult("0.5")
                        .secondBinaryOperator(SUBTRACTION).secondBinaryResult("0.75|0.75 -").build())
        );
//
//                Arguments.of("0", INFINITY),
//                Arguments.of("5", "0.2"),
//                Arguments.of("0.25", "4"),
//                Arguments.of("4", "0.25", ADDITION, "2", "0.5", SUBTRACTION)
//        );
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
    private static Stream<Arguments> getInvalidFractionCases()
    {
        /*
         * case 1: cannot press FRACTION when textPane is empty
         * case 2: cannot press FRACTION when textPane is badText (INFINITY)
         * case 3: cannot press FRACTION when textPane is badText (ENTER_A_NUMBER)
         * case 4: cannot press FRACTION when operator is active
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(FRACTION).firstUnaryOperator(FRACTION).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(FRACTION).firstNumber(ENTER_A_NUMBER).firstUnaryOperator(FRACTION).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(FRACTION).firstNumber(INFINITY).firstUnaryOperator(FRACTION).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+INFINITY).build()),
                Arguments.of(ArgumentsForTests.builder(FRACTION).firstNumber("5").firstBinaryOperator(ADDITION).firstBinaryResult("5|5 "+ADDITION)
                        .secondUnaryOperator(FRACTION).secondUnaryResult(EMPTY+"|5 "+ADDITION).build())
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
    private static Stream<Arguments> validClearEntryCases()
    {
        /*
         * case 1: CLEAR ENTRY when textPane is empty -> remains empty
         * case 2: CLEAR ENTRY when textPane has a number -> becomes empty
         * case 3: CLEAR ENTRY when first number and operator are present -> clears textPane
         * case 4: CLEAR ENTRY when first number, operator, and second number are present
         * case 5: CLEAR ENTRY when first number, operator, second number, and decimal are present
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(CLEAR_ENTRY).firstNumber(EMPTY).firstUnaryOperator(CLEAR_ENTRY).firstUnaryResult(EMPTY).build()),
                Arguments.of(ArgumentsForTests.builder(CLEAR_ENTRY).firstNumber("100").firstUnaryOperator(CLEAR_ENTRY).firstUnaryResult(EMPTY).build()),
                Arguments.of(ArgumentsForTests.builder(CLEAR_ENTRY).firstNumber("100").firstBinaryOperator(ADDITION).firstBinaryResult("100|100 "+ADDITION).secondUnaryOperator(CLEAR_ENTRY).secondUnaryResult(EMPTY).build()),
                Arguments.of(ArgumentsForTests.builder(CLEAR_ENTRY).firstNumber("2,727").firstBinaryOperator(SUBTRACTION).firstBinaryResult("2,727|2,727 "+SUBTRACTION).secondNumber("100").secondUnaryOperator(CLEAR_ENTRY).secondUnaryResult(EMPTY).build()),
                Arguments.of(ArgumentsForTests.builder(CLEAR_ENTRY).firstNumber("3.14").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("3.14|3.14 "+MULTIPLICATION).secondNumber("2,727").secondUnaryOperator(CLEAR_ENTRY).secondUnaryResult(EMPTY).build())
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
    private static Stream<Arguments> validClearButtonCases()
    {
        /*
         * case 1: CLEAR when textPane is empty -> textPane shows 0
         * case 2: CLEAR when textPane has a number -> textPane shows 0
         * case 3: CLEAR when first number and operator are present -> clears all, textPane shows 0
         * case 4: CLEAR when first number, operator, and second number are present -> clears all, textPane shows 0
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(CLEAR).firstNumber(EMPTY).firstUnaryOperator(CLEAR).firstUnaryResult(EMPTY+"|0").build()),
                Arguments.of(ArgumentsForTests.builder(CLEAR).firstNumber("100").firstUnaryOperator(CLEAR).firstUnaryResult(EMPTY+"|0").build()),
                Arguments.of(ArgumentsForTests.builder(CLEAR).firstNumber("100").firstBinaryOperator(ADDITION).firstBinaryResult("100|100 "+ADDITION).secondUnaryOperator(CLEAR).secondUnaryResult(EMPTY+"|0").build()),
                Arguments.of(ArgumentsForTests.builder(CLEAR).firstNumber("2,727").firstBinaryOperator(SUBTRACTION).firstBinaryResult("2,727|2,727 "+SUBTRACTION).secondNumber("100").secondUnaryOperator(CLEAR).secondUnaryResult(EMPTY+"|0").build())
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
    private static Stream<Arguments> validDeleteButtonCases()
    {
        /*
         * case 1: Enter 35, press DELETE -> textPane shows 3
         * case 2: Enter 3, press DELETE -> textPane shows EMPTY
         * case 3: Enter 35.12, press DELETE -> textPane shows 35.1
         * case 4: Enter 35.1, press DELETE -> textPane shows 35.
         * case 5: Enter 3, press DELETE -> textPane shows EMPTY, history shows DELETE and ENTER_A_NUMBER
         * case 6: Enter 35, press ADDITION, textPane shows 35 +, press DELETE -> textPane shows 35
         * case 7: Enter 1, press SUBTRACTION, textPane shows 1 -, press DELETE -> textPane shows 1
         * case 8: textPane shows ENTER_A_NUMBER, press DELETE -> textPane shows EMPTY
         * case 9: textPane is EMPTY, press DELETE -> textPane shows ENTER_A_NUMBER
         * case 10: Enter 12,345, press DELETE -> textPane shows 1,234
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("35").firstUnaryOperator(DELETE).firstUnaryResult("3")),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("3").firstUnaryOperator(DELETE).firstUnaryResult(EMPTY)),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("35.12").firstUnaryOperator(DELETE).firstUnaryResult("35.1")),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("35.1").firstUnaryOperator(DELETE).firstUnaryResult("35.")),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("3").firstUnaryOperator(DELETE).firstUnaryResult(EMPTY).firstBinaryResult(DELETE).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER)),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("35").firstBinaryOperator(ADDITION).firstBinaryResult("35|35 "+ADDITION).secondUnaryOperator(DELETE).secondUnaryResult("35")),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("1").firstBinaryOperator(SUBTRACTION).firstBinaryResult("1|1 "+SUBTRACTION).secondUnaryOperator(DELETE).secondUnaryResult("1")),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber(ENTER_A_NUMBER).firstUnaryResult(DELETE).firstUnaryResult(EMPTY)),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstUnaryOperator(DELETE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER)),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("12,345").firstUnaryOperator(DELETE).firstUnaryResult("1,234"))
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
    private static Stream<Arguments> validAddButtonCases()
    {
        /*
         * case 1: Enter a number, press ADD -> operator set to ADD, textPane shows number + ADDITION
         * case 2: Enter first number, press ADD, enter second number, press EQUALS -> result shows 3
         * case 3: Enter 1.5, press ADD, enter 2.3, press EQUALS -> result shown 3.8
         * case 4: Enter 1, press ADD, enter 5, press EQUALS -> shows 6, press ADD again -> textPane shows 6 +.
         *         secondUnaryResult has EMPTY|6 because it checks values[1] but we are now at values[0]
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(ADDITION).simpleName("1 +").firstNumber("1").firstBinaryOperator(ADDITION).firstBinaryResult("1|1 "+ADDITION)),
                Arguments.of(ArgumentsForTests.builder(ADDITION).simpleName("1 + 2 = 3").firstNumber("1").firstBinaryOperator(ADDITION).firstBinaryResult("1|1 "+ADDITION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"3")),
                Arguments.of(ArgumentsForTests.builder(ADDITION).simpleName("1.5 + 2.3 = 3.8").firstNumber("1.5").firstBinaryOperator(ADDITION).firstBinaryResult("1.5|1.5 "+ADDITION).secondNumber("2.3").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"3.8")),
                Arguments.of(ArgumentsForTests.builder(ADDITION).simpleName("1 + 5 = 6 +")
                        .firstNumber("1").firstBinaryOperator(ADDITION).firstBinaryResult("1|1 "+ADDITION)
                        .secondNumber("5").secondUnaryOperator(EQUALS).secondUnaryResult(EMPTY+ARGUMENT_SEPARATOR+"6")
                        .secondBinaryOperator(ADDITION).secondBinaryResult(EMPTY+"|6 "+ADDITION).build())
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
    private static Stream<Arguments> invalidAddButtonCases()
    {
        /*
         * case 1: Pressed ADD when textPane is empty
         * case 2: Enter a number, press ADD, press ADD again
         * case 3: Pressed ADD when textPane shows badText (ENTER_A_NUMBER)
         * case 4: Pressed ADD when textPane shows badText (ERROR)
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(ADDITION).firstBinaryOperator(ADDITION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(ADDITION).firstNumber("1").firstUnaryOperator(ADDITION).firstUnaryResult("1|1 "+ADDITION).firstBinaryOperator(ADDITION).firstBinaryResult("1|1 "+ADDITION).build()),
                Arguments.of(ArgumentsForTests.builder(ADDITION).firstNumber(ENTER_A_NUMBER).firstBinaryOperator(ADDITION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(ADDITION).firstNumber(ERROR).firstBinaryOperator(ADDITION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ERROR).build())
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
    private static Stream<Arguments> validSubtractButtonCases()
    {
        /*
         * case 1: Enter a number, press SUBTRACT -> operator set to SUBTRACT, textPane shows number + SUBTRACTION
         * case 2: Enter first number, press SUBTRACT, enter second number, press EQUALS -> result shown
         * case 3: Enter first decimal number, press SUBTRACT, enter second decimal number, press EQUALS -> result shown
         * case 4: Pressed SUBTRACT when textPane is empty -> textPane shows SUBTRACTION
         * case 5: Enter a number, press SUBTRACT, press SUBTRACT again, textPane shows SUBTRACTION
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(SUBTRACTION).firstNumber("1").firstBinaryOperator(SUBTRACTION).firstBinaryResult("1|1 "+SUBTRACTION)),
                Arguments.of(ArgumentsForTests.builder(SUBTRACTION).firstNumber("1").firstBinaryOperator(SUBTRACTION).firstBinaryResult("1|1 "+SUBTRACTION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"-1")),
                Arguments.of(ArgumentsForTests.builder(SUBTRACTION).firstNumber("1.5").firstBinaryOperator(SUBTRACTION).firstBinaryResult("1.5|1.5 "+SUBTRACTION).secondNumber("2.3").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"-0.8")),
                Arguments.of(ArgumentsForTests.builder(SUBTRACTION).firstBinaryOperator(SUBTRACTION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+SUBTRACTION).build()),
                Arguments.of(ArgumentsForTests.builder(SUBTRACTION).firstNumber("1").firstUnaryOperator(SUBTRACTION).firstUnaryResult("1|1 "+SUBTRACTION).firstBinaryOperator(SUBTRACTION).firstBinaryResult("1|"+SUBTRACTION).build())
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
    private static Stream<Arguments> invalidSubtractButtonCases()
    {
        /*
         * case 1: Pressed SUBTRACT when textPane shows badText (ENTER_A_NUMBER)
         * case 2: Pressed SUBTRACT when textPane shows badText (ERROR)
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(SUBTRACTION).firstNumber(ENTER_A_NUMBER).firstBinaryOperator(SUBTRACTION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(SUBTRACTION).firstNumber(ERROR).firstBinaryOperator(SUBTRACTION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ERROR).build())
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
    private static Stream<Arguments> validMultiplyButtonCases()
    {
        /*
         * case 1: Enter a number, press MULTIPLY -> operator set to MULTIPLY, textPane shows number + MULTIPLY
         * case 2: Enter first number, press MULTIPLY, enter second number, press EQUALS -> result shown
         * case 3: Enter first decimal number, press MULTIPLY, enter second decimal number, press EQUALS -> result shown
         * case 4: Enter first decimal number, press MULTIPLY, enter second decimal number, press EQUALS -> result shown
         * case 5: Enter a number, press MULTIPLY -> operator set to MULTIPLY, textPane shows number + MULTIPLY
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(MULTIPLICATION).firstNumber("1").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("1|1 "+MULTIPLICATION)),
                Arguments.of(ArgumentsForTests.builder(MULTIPLICATION).firstNumber("1").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("1|1 "+MULTIPLICATION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"2")),
                Arguments.of(ArgumentsForTests.builder(MULTIPLICATION).firstNumber("1.5").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("1.5|1.5 "+MULTIPLICATION).secondNumber("2.3").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"3.45")),
                Arguments.of(ArgumentsForTests.builder(MULTIPLICATION).firstNumber("5.5").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("5.5|5.5 "+MULTIPLICATION).secondNumber("10.2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"56.1")),
                Arguments.of(ArgumentsForTests.builder(MULTIPLICATION).firstNumber("15").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("15|15 "+MULTIPLICATION))
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
    private static Stream<Arguments> invalidMultiplyButtonCases()
    {
        /*
         * case 1: Pressed MULTIPLY when textPane is empty
         * case 2: Enter a number, press MULTIPLY, press MULTIPLY again
         * case 3: Pressed MULTIPLY when textPane shows badText (ENTER_A_NUMBER)
         * case 4: Pressed MULTIPLY when textPane shows badText (ERROR)
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(MULTIPLICATION).firstBinaryOperator(MULTIPLICATION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(MULTIPLICATION).firstNumber("1").firstUnaryOperator(MULTIPLICATION).firstUnaryResult("1|1 "+MULTIPLICATION).firstBinaryOperator(MULTIPLICATION).firstBinaryResult("1|1 "+MULTIPLICATION).build()),
                Arguments.of(ArgumentsForTests.builder(MULTIPLICATION).firstNumber(ENTER_A_NUMBER).firstBinaryOperator(MULTIPLICATION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(MULTIPLICATION).firstNumber(ERROR).firstBinaryOperator(MULTIPLICATION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ERROR).build())
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
    private static Stream<Arguments> validDivideButtonCases()
    {
        /*
         * case 1: Enter a number, press DIVIDE -> operator set to DIVIDE, textPane shows number + DIVIDE
         * case 2: Enter first number, press DIVIDE, enter second number, press EQUALS -> result shown
         * case 3: Enter first decimal number, press DIVIDE, enter second decimal number, press EQUALS -> result shown
         * case 4: Enter a number, press DIVIDE -> operator set to DIVIDE, textPane shows number + DIVIDE
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(DIVISION).firstNumber("1").firstBinaryOperator(DIVISION).firstBinaryResult("1|1 "+DIVISION)),
                Arguments.of(ArgumentsForTests.builder(DIVISION).firstNumber("1").firstBinaryOperator(DIVISION).firstBinaryResult("1|1 "+DIVISION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"0.5")),
                Arguments.of(ArgumentsForTests.builder(DIVISION).firstNumber("1.5").firstBinaryOperator(DIVISION).firstBinaryResult("1.5|1.5 "+DIVISION).secondNumber("2.3").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"0.6521739130434782608695652173913043")),
                Arguments.of(ArgumentsForTests.builder(DIVISION).firstNumber("15").firstBinaryOperator(DIVISION).firstBinaryResult("15|15 "+DIVISION))
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
    private static Stream<Arguments> invalidDivideButtonCases()
    {
        /*
         * case 1: Pressed DIVIDE when textPane is empty
         * case 2: Enter a number, press DIVIDE, press DIVIDE again
         * case 3: Pressed DIVIDE when textPane shows badText (ENTER_A_NUMBER)
         * case 4: Pressed DIVIDE when textPane shows badText (ERROR)
         * case 5: Divide by 0
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(DIVISION).firstBinaryOperator(DIVISION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(DIVISION).firstNumber("1").firstUnaryOperator(DIVISION).firstUnaryResult("1|1 "+DIVISION).firstBinaryOperator(DIVISION).firstBinaryResult("1|1 "+DIVISION).build()),
                Arguments.of(ArgumentsForTests.builder(DIVISION).firstNumber(ENTER_A_NUMBER).firstBinaryOperator(DIVISION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(DIVISION).firstNumber(ERROR).firstBinaryOperator(DIVISION).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ERROR).build()),
                Arguments.of(ArgumentsForTests.builder(EQUALS).firstNumber("15.5").firstBinaryOperator(DIVISION).firstBinaryResult("15.5|15.5 "+DIVISION).secondNumber("0").firstBinaryOperator(DIVISION).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+INFINITY).build())
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
    private static Stream<Arguments> validNumberButtonCases()
    {
        /*
         * case 1: Enter 0, Expect 0
         * case 2: Enter 1, Expect 1
         * case 3: Enter 2, Expect 2
         * case 4: Enter 3, Expect 3
         * case 5: Enter 4, Expect 4
         * case 6: Enter 5, Expect 5
         * case 7: Enter 6, Expect 6
         * case 8: Enter 7, Expect 7
         * case 9: Enter 8, Expect 8
         * case 10: Enter 9, Expect 9
         * case 11: Enter 1, then ADDITION, Expect "1 +"
         * case 12: Enter 1, then SUBTRACTION, Expect "1 -
         * case 13: Enter 1, then MULTIPLICATION, Expect "1 ×"
         * case 14: Enter 1, then DIVISION, Expect "1 ÷
         * case 15: Enter 15, Expect 15
         * case 16: Enter 150 after "Enter a Number" is displayed, Expect 150
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder("0").firstNumber("0").build()),
                Arguments.of(ArgumentsForTests.builder("1").firstNumber("1").build()),
                Arguments.of(ArgumentsForTests.builder("2").firstNumber("2").build()),
                Arguments.of(ArgumentsForTests.builder("3").firstNumber("3").build()),
                Arguments.of(ArgumentsForTests.builder("4").firstNumber("4").build()),
                Arguments.of(ArgumentsForTests.builder("5").firstNumber("5").build()),
                Arguments.of(ArgumentsForTests.builder("6").firstNumber("6").build()),
                Arguments.of(ArgumentsForTests.builder("7").firstNumber("7").build()),
                Arguments.of(ArgumentsForTests.builder("8").firstNumber("8").build()),
                Arguments.of(ArgumentsForTests.builder("9").firstNumber("9").build()),
                Arguments.of(ArgumentsForTests.builder("1").firstNumber("1").firstBinaryOperator(ADDITION).firstBinaryResult("1|1 "+ADDITION).build()),
                Arguments.of(ArgumentsForTests.builder("1").firstNumber("1").firstBinaryOperator(SUBTRACTION).firstBinaryResult("1|1 "+SUBTRACTION).build()),
                Arguments.of(ArgumentsForTests.builder("1").firstNumber("1").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("1|1 "+MULTIPLICATION).build()),
                Arguments.of(ArgumentsForTests.builder("1").firstNumber("1").firstBinaryOperator(DIVISION).firstBinaryResult("1|1 "+DIVISION).build()),
                Arguments.of(ArgumentsForTests.builder("1").firstNumber("15").build()),
                Arguments.of(ArgumentsForTests.builder("1").firstNumber(ENTER_A_NUMBER).secondNumber("150").build()),
                Arguments.of(ArgumentsForTests.builder("1")
                        .initialState(ArgumentsForTests.builder("1").firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+"1.0008").build())
                        .firstUnaryOperator(SQUARE_ROOT).firstUnaryResult("1.000399920031984").build())
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
    private static Stream<Arguments> validNegateButtonCases()
    {
        /*
         * case 1: Enter 1, press NEGATE -> results in -1
         * case 2: Enter -1, press NEGATE -> results in 1
         * case 3: Enter 1, press NEGATE, press NEGATE -> results in 1
         * case 4: Enter 1, press NEGATE, press ADDITION -> results in -1 +
         * case 5: Enter -5, press NEGATE, press ADDITION, enter 2, press NEGATE, press ADDITION -> results in 3 +
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(NEGATE).simpleName("1 NEGATE").firstNumber("1").firstUnaryOperator(NEGATE).firstUnaryResult("-1").build()),
                Arguments.of(ArgumentsForTests.builder(NEGATE).simpleName("-1 NEGATE").firstNumber("-1").firstUnaryOperator(NEGATE).firstUnaryResult("1").build()),
                Arguments.of(ArgumentsForTests.builder(NEGATE).simpleName("1 NEGATE NEGATE").firstNumber("1").firstUnaryOperator(NEGATE).firstUnaryResult("-1").firstBinaryOperator(NEGATE).firstBinaryResult("1").build()),
                Arguments.of(ArgumentsForTests.builder(NEGATE).simpleName("1 NEGATE +").firstNumber("1").firstUnaryOperator(NEGATE).firstUnaryResult("-1").firstBinaryOperator(ADDITION).firstBinaryResult("-1|-1 "+ADDITION).build()),
                Arguments.of(ArgumentsForTests.builder(NEGATE).simpleName("5 NEGATE + 2 NEGATE + 3 +").firstNumber("-5").firstUnaryOperator(NEGATE).firstUnaryResult("5").firstBinaryOperator(ADDITION).firstBinaryResult("5|5 "+ADDITION).secondNumber("2").secondUnaryOperator(NEGATE).secondUnaryResult("-2").secondBinaryOperator(ADDITION).secondBinaryResult("3|3 "+ADDITION).build())
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
    private static Stream<Arguments> invalidNegateButtonCases()
    {
        /*
         * case 1: cannot press NEGATE when textPane is empty
         * case 2: cannot press NEGATE when textPane is badText (INFINITY)
         * case 3: cannot press NEGATE when textPane is badText (ENTER_A_NUMBER)
         * case 4: cannot press NEGATE when textPane is badText (ERROR)
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(NEGATE).firstUnaryOperator(NEGATE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(NEGATE).firstNumber(ENTER_A_NUMBER).firstUnaryOperator(NEGATE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(NEGATE).firstNumber(INFINITY).firstUnaryOperator(NEGATE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+INFINITY).build()),
                Arguments.of(ArgumentsForTests.builder(NEGATE).firstNumber(ERROR).firstUnaryOperator(NEGATE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ERROR).build())
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
    private static Stream<Arguments> validDecimalButtonCases()
    {
        /*
         * case 1: press DECIMAL when firstNumber is ENTER_A_NUMBER -> results in "0."
         * case 2: Input "0." as firstNumber. Ensure values[0] is "0."
         * case 3: Input "544." as firstNumber. Ensure values[0] is "544."
         * case 4: Input "1234." as firstNumber. Ensure values[0] is "1,234."
         * case 5: Input "1.5" as firstNumber, press ADDITION, input "2" as secondNumber. Ensure values are correct.
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(DECIMAL).firstNumber(ENTER_A_NUMBER).firstUnaryOperator(DECIMAL).firstUnaryResult("0.").build()),
                Arguments.of(ArgumentsForTests.builder(DECIMAL).firstNumber("0.").build()),
                Arguments.of(ArgumentsForTests.builder(DECIMAL).firstNumber("544.").build()),
                Arguments.of(ArgumentsForTests.builder(DECIMAL).firstNumber("1,234.").build()),
                Arguments.of(ArgumentsForTests.builder(DECIMAL).firstNumber("1.5").firstBinaryOperator(ADDITION).firstBinaryResult("1.5|1.5 "+ADDITION).secondNumber("2").build())
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
    private static Stream<Arguments> validEqualsButtonCases()
    {
        /*
         * case 1: Number 1, Press ADD, Number 2, Press EQUALS -> results in 3
         * case 2: Number 1, Press SUBTRACT, Number 2, Press EQUALS -> results in -1
         * case 3: Number 1, Press MULTIPLY, Number 2, Press EQUALS -> results in 2
         * case 4: Number 1, Press DIVIDE, Number 2, Press EQUALS -> results in 0.5
         * case 5: Number 5, Press SQUARED, Press ADD, Number 4, Press SQUARED, Press EQUALS -> results in 41
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(ADDITION).firstNumber("1").firstBinaryOperator(ADDITION).firstBinaryResult("1|1 "+ADDITION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"3")),
                Arguments.of(ArgumentsForTests.builder(SUBTRACTION).firstNumber("1").firstBinaryOperator(SUBTRACTION).firstBinaryResult("1|1 "+SUBTRACTION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"-1")),
                Arguments.of(ArgumentsForTests.builder(MULTIPLICATION).firstNumber("1").firstBinaryOperator(MULTIPLICATION).firstBinaryResult("1|1 "+MULTIPLICATION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"2")),
                Arguments.of(ArgumentsForTests.builder(DIVISION).firstNumber("1").firstBinaryOperator(DIVISION).firstBinaryResult("1|1 "+DIVISION).secondNumber("2").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"0.5")),
                Arguments.of(ArgumentsForTests.builder(ADDITION).simpleName("5 SQUARED + 4 SQUARED = 41")
                        .firstNumber("5")
                        .firstUnaryOperator(SQUARED).firstUnaryResult("25")
                        .firstBinaryOperator(ADDITION).firstBinaryResult("25|25 "+ADDITION)
                        .secondNumber("4").secondUnaryOperator(SQUARED).secondUnaryResult("16")
                        .secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+"41"))
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
    private static Stream<Arguments> invalidEqualsButtonCases()
    {
        /*
         * case 1: press EQUALS when only negative firstNumber and ADDITION is set (no secondNumber)
         * case 2: press EQUALS when only firstNumber and ADDITION is set (no secondNumber)
         * case 3: press EQUALS when only firstNumber and SUBTRACTION is set (no secondNumber)
         * case 4: press EQUALS when only firstNumber and MULTIPLICATION is set (no secondNumber)
         * case 5: press EQUALS when only firstNumber and DIVISION is set (no secondNumber)
         * case 6: press EQUALS when only decimal firstNumber and ADDITION is set (no secondNumber)
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(EQUALS).firstNumber("-6").firstUnaryOperator(ADDITION).firstUnaryResult("-6|-6 "+ADDITION).firstBinaryOperator(EQUALS).firstBinaryResult("-6|"+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(EQUALS).firstNumber("6").firstUnaryOperator(ADDITION).firstUnaryResult("6|6 "+ADDITION).firstBinaryOperator(EQUALS).firstBinaryResult("6|"+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(EQUALS).firstNumber("6").firstUnaryOperator(SUBTRACTION).firstUnaryResult("6|6 "+SUBTRACTION).firstBinaryOperator(EQUALS).firstBinaryResult("6|"+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(EQUALS).firstNumber("6").firstUnaryOperator(MULTIPLICATION).firstUnaryResult("6|6 "+MULTIPLICATION).firstBinaryOperator(EQUALS).firstBinaryResult("6|"+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(EQUALS).firstNumber("6").firstUnaryOperator(DIVISION).firstUnaryResult("6|6 "+DIVISION).firstBinaryOperator(EQUALS).firstBinaryResult("6|"+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(EQUALS).firstNumber("5.5").firstUnaryOperator(ADDITION).firstUnaryResult("5.5|5.5 "+ADDITION).firstBinaryOperator(EQUALS).firstBinaryResult("5.5|"+ENTER_A_NUMBER).build())
        );
    }
    /*############## Test Button Actions ##################*/
}

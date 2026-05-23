package calculator.panels;

import calculator.entities.Calculator;
import calculator.test.ArgumentsForTests;
import calculator.test.TestParent;
import calculator.entities.CalculatorBase;
import calculator.entities.SystemDetector;
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

import static calculator.entities.CalculatorBase.*;
import static calculator.entities.CalculatorByte.*;
import static calculator.entities.CalculatorView.VIEW_PROGRAMMER;
import static calculator.entities.Texts.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * ProgrammerPanelTest
 * <p>
 * This class tests the {@link ProgrammerPanel} class.
 *
 * @author Michael Ball
 * @version 4.0
 */
class ProgrammerPanelTest extends TestParent
{
    private static final Logger LOGGER = LogManager.getLogger(ProgrammerPanelTest.class);

    static {
        System.setProperty("appName", ProgrammerPanelTest.class.getSimpleName());
    }

    @BeforeAll
    static void beforeAll()
    {
        mocks = MockitoAnnotations.openMocks(BasicPanelTest.class);
    }

    @AfterAll
    static void afterAll()
    {
        LOGGER.info("Finished running {}", ProgrammerPanel.class.getSimpleName());
    }

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(() -> {
            try {
                LOGGER.info("Starting test");
                calculator = spy(new Calculator());
                calculator.performViewMenuAction(actionEvent, VIEW_PROGRAMMER);
                Preferences.userNodeForPackage(Calculator.class).clear(); // remove keys
                calculator.setSystemDetector(new SystemDetector());
                calculator.getProgrammerPanel().setCalculator(calculator); // links spyCalc to ProgrammerPanel
            }
            catch (Exception ignored) {
            }
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(() -> {
            LOGGER.info("Test complete. Closing the calculator...");
            try {
                calculator.setVisible(false);
                calculator.dispose();
            }
            catch (Exception ignored) {}
        });
    }

    /* Valid OR */
    @ParameterizedTest
    @DisplayName("Test Valid OR Button Cases")
    @MethodSource("validOrButtonCases")
    void testValidOrButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }

    private static Stream<ArgumentsForTests> validOrButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(OR)
                        .firstNumber("5").secondUnaryResult(OR).firstUnaryResult("5 OR").build(),
                ArgumentsForTests.builder(OR)
                        .firstNumber("5").firstBinaryOperator(OR).firstBinaryResult("5 OR")
                        .secondNumber("7").secondBinaryOperator(EQUALS).secondBinaryResult("7").build(),
                ArgumentsForTests.builder(OR).testName("Test 5.5 OR 7.4 =")
                        .firstNumber("5.5").firstBinaryOperator(OR).firstBinaryResult("5.5 OR")
                        .secondNumber("7.4").secondBinaryOperator(EQUALS).secondBinaryResult("7").build()
        );
    }

    /* Invalid NOT */

    /* Invalid OR */
    @ParameterizedTest
    @DisplayName("Test Invalid OR Button Cases")
    @MethodSource("invalidOrButtonCases")
    void testPushOrWhenValuesAtZeroNotSet(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }

    private static Stream<ArgumentsForTests> invalidOrButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(OR).firstBinaryResult(OR).firstBinaryResult(EMPTY + ARGUMENT_SEPARATOR + ENTER_A_NUMBER).build()
                //, more
        );
    }

    /* Invalid XOR */
    @ParameterizedTest
    @DisplayName("Test Invalid XOR Button Cases")
    @MethodSource("invalidXorButtonCases")
    void testInvalidXorButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }

    private static Stream<ArgumentsForTests> invalidXorButtonCases()
    {
        return Stream.of(
                // TODO: Add cases
                ArgumentsForTests.builder(XOR).firstBinaryResult(XOR).firstBinaryResult(EMPTY + ARGUMENT_SEPARATOR + ENTER_A_NUMBER).build()
        );
    }

    /* Valid NOT */
    @ParameterizedTest
    @DisplayName("Test Valid NOT Button Cases")
    @MethodSource("validNotButtonCases")
    void testValidNotButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        calculator.setCalculatorBase(arguments.getCurrentBase());
        calculator.setPreviousBase(arguments.getPreviousBase());
        calculator.setCalculatorByte(arguments.getCalculatorByte());
        // copilot edit
        if (!arguments.getFirstNumber().isEmpty()) {
            String firstNumber = arguments.getFirstNumber().replace(BLANK, EMPTY);
            if (calculator.getCalculatorBase() != BASE_DECIMAL) {
                // Convert from the explicitly set current base to decimal
                calculator.getValues()[0] = calculator.convertFromBaseToBase(calculator.getCalculatorBase(), BASE_DECIMAL, firstNumber);
            } else {
                // Already in decimal format, use as-is
                calculator.getValues()[0] = firstNumber;
            }
        }
        if (!arguments.getSecondNumber().isEmpty()) {
            String secondNumber = arguments.getSecondNumber().replace(BLANK, EMPTY);
            if (calculator.getCalculatorBase() != BASE_DECIMAL) {
                calculator.getValues()[1] = calculator.convertFromBaseToBase(calculator.getCalculatorBase(), BASE_DECIMAL, secondNumber);
            } else {
                calculator.getValues()[1] = secondNumber;
            }
        }
        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }

    private static Stream<ArgumentsForTests> validNotButtonCases()
    {
        // TODO: Need a way to set the base and byte
        return Stream.of(
                ArgumentsForTests.builder(NOT)
                        .currentBase(BASE_BINARY)
                        .previousBase(BASE_HEXADECIMAL)
                        .calculatorByte(BYTE_BYTE)
                        .firstNumber("0000 1011")
                        .firstUnaryOperator(NOT)
                        .firstUnaryResult("-12|1111 0100").build(),
                ArgumentsForTests.builder(NOT)
                        .currentBase(BASE_DECIMAL)
                        .previousBase(BASE_OCTAL)
                        .calculatorByte(BYTE_BYTE)
                        .firstNumber("5").firstUnaryResult("...11111010").build()
        );
    }

    /* Valid AND */
    @ParameterizedTest
    @DisplayName("Test Valid AND Button Cases")
    @MethodSource("validAndButtonCases")
    void testPerformAdd(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }

    private static Stream<ArgumentsForTests> validAndButtonCases()
    {
        return Stream.of(
                ArgumentsForTests.builder(AND).firstNumber("5").firstBinaryOperator(AND).firstBinaryResult("5|5 " + AND)
                        .secondNumber("6").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY + "|4").build()
        );
    }

    /* Invalid AND */
    @ParameterizedTest
    @DisplayName("Test Invalid AND Button Cases")
    @MethodSource("invalidAndButtonCases")
    void testClickedButtonAndWhenTextPaneBlank(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }

    private static Stream<ArgumentsForTests> invalidAndButtonCases()
    {
        /*
         * case 1: Clicked AND when textPane is blank, expect prompt to enter a number
         * case 2: Clicked AND when textPane contains badText (ENTER_A_NUMBER), expect prompt to enter a number
         * case 3: Enter 1 number. Click AND. Click AND again. Expect prompt to enter a number
         */
        return Stream.of(
                ArgumentsForTests.builder(AND).firstBinaryOperator(AND).firstBinaryResult(EMPTY + ARGUMENT_SEPARATOR + ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(AND).firstNumber(ENTER_A_NUMBER).firstUnaryResult(AND).firstUnaryResult(EMPTY + ARGUMENT_SEPARATOR + ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(AND).firstNumber(FIVE).firstBinaryOperator(AND).firstBinaryResult("5|5 " + AND)
                        .secondBinaryOperator(AND).secondBinaryResult(EMPTY + ARGUMENT_SEPARATOR + ENTER_A_NUMBER).build()
        );
    }

    // TODO: Place these single tests within their corresponding Parameterized test.
    @Test
    @DisplayName("Test OR with negative decimal and positive decimal")
    void testPerformOrWithNegativeDecimalValue()
    {
        calculator.setCalculatorBase(BASE_DECIMAL);
        calculator.getValues()[0] = SUBTRACTION + ONE + FOUR + FIVE;
        calculator.getValues()[1] = EIGHT;

        String result = calculator.getProgrammerPanel().performOr();

        assertEquals(SUBTRACTION + ONE + FOUR + FIVE, result, "Expected -145 OR 8 to be -145");
    }

    @Test
    @DisplayName("Test AND with negative decimal and positive decimal")
    void testPerformAndWithNegativeDecimalValue()
    {
        calculator.setCalculatorBase(BASE_DECIMAL);
        calculator.getValues()[0] = SUBTRACTION + ONE + FOUR + FIVE;
        calculator.getValues()[1] = EIGHT;

        String result = calculator.getProgrammerPanel().performAnd();

        assertEquals(EIGHT, result, "Expected -145 AND 8 to be 8");
    }

    @Test
    @DisplayName("Test XOR with negative decimal and positive decimal")
    void testPerformXorWithNegativeDecimalValue()
    {
        calculator.setCalculatorBase(BASE_DECIMAL);
        calculator.getValues()[0] = SUBTRACTION + ONE + FOUR + FIVE;
        calculator.getValues()[1] = EIGHT;

        String result = calculator.getProgrammerPanel().performXor();

        assertEquals(SUBTRACTION + ONE + FIVE + THREE, result, "Expected -145 XOR 8 to be -153");
    }

    @Test
    @DisplayName("Test And Button continued operation")
    void testPerformAndContinuedOperation()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(FIVE)
                .thenReturn(AND)
                .thenReturn(SIX)
                .thenReturn(AND);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getTextPaneValue(), "Expected textPane to be 5");

        calculator.getProgrammerPanel().performAndButtonAction(actionEvent);
        assertEquals("5 AND", calculator.getTextPaneValue(), "Expected textPane to be 5 AND");
        assertEquals(AND, calculator.getValueAt(2), "Expecting AND at values[2]");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(SIX, calculator.getTextPaneValue(), "Expected textPane to contain 6");
        assertEquals(AND, calculator.getValueAt(2), "Expecting AND at values[2]");

        calculator.getProgrammerPanel().performAndButtonAction(actionEvent);
        assertEquals("4 AND", calculator.getTextPaneValue(), "Expected textPane to be 4 AND");
        assertEquals(AND, calculator.getValueAt(2), "Expecting AND at values[2]");
    }

    // Why could 0 AND some other number not work?
    @Test
    void testClickedButtonAndWhenValuesAtZeroIsEmpty()
    {
        when(actionEvent.getActionCommand()).thenReturn(AND);
        calculator.getProgrammerPanel().performAndButtonAction(actionEvent);
        assertEquals(EMPTY, calculator.getValues()[0], "Expected values[0] to be empty");
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to say " + ENTER_A_NUMBER);
    }
    // END TODO

    /* Valid MODULUS */
    @ParameterizedTest()
    @MethodSource("getValidModulusCases")
    @DisplayName("Test Modulus Button")
    void testModulusButton(CalculatorBase base, String firstNumber, String secondNumber,
                           String nextOperator, String expectedResult)
    {
        calculator.setCalculatorBase(base);
        calculator.getValues()[0] = firstNumber;
        calculator.getValues()[1] = secondNumber;
        calculator.getValues()[2] = MODULUS;
        calculator.setValuesPosition(1);
        calculator.appendTextToPane(calculator.getValueAt());
        when(actionEvent.getActionCommand())
                .thenReturn(nextOperator); // can be any button push
        performNextOperatorAction(calculator, actionEvent, LOGGER, nextOperator);

        assertEquals(expectedResult, calculator.getTextPaneValue(), "Text Pane result not as expected");
        assertEquals(EMPTY, calculator.getValueAt(0), "values[0] result should be EMPTY");
        assertEquals(EMPTY, calculator.getValueAt(1), "values[1] result should be EMPTY");
        assertEquals(EMPTY, calculator.getValueAt(2), "values[2] result should be EMPTY");
    }

    private static Stream<Arguments> getValidModulusCases()
    {
        return Stream.of(
                Arguments.of(BASE_BINARY, FIVE, THREE, EQUALS, "0000 0010"),
                Arguments.of(BASE_DECIMAL, FIVE, THREE, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, FOUR, THREE, EQUALS, ONE),
                Arguments.of(BASE_DECIMAL, FOUR, ZERO, EQUALS, INFINITY),
                Arguments.of(BASE_DECIMAL, SUBTRACTION + FOUR, TWO, EQUALS, ZERO),
                Arguments.of(BASE_DECIMAL, ONE + ZERO, THREE, EQUALS, ONE),
                Arguments.of(BASE_DECIMAL, ONE + SEVEN, FIVE, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, THREE, SEVEN, EQUALS, THREE),
                Arguments.of(BASE_DECIMAL, FOUR, ONE + ZERO, EQUALS, FOUR),
                Arguments.of(BASE_DECIMAL, SEVEN, SEVEN, EQUALS, ZERO),
                Arguments.of(BASE_DECIMAL, TWO + FIVE, FOUR, EQUALS, ONE),
                Arguments.of(BASE_DECIMAL, ONE + ZERO + ZERO, THREE + ZERO, EQUALS, ONE + ZERO),
                Arguments.of(BASE_DECIMAL, TWO + FOUR, SIX, EQUALS, ZERO),
                Arguments.of(BASE_DECIMAL, EIGHT + ONE, NINE, EQUALS, ZERO),
                Arguments.of(BASE_DECIMAL, SUBTRACTION + ONE + ZERO, THREE, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, SUBTRACTION + FIVE, SEVEN, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, ONE + ZERO, SUBTRACTION + THREE, EQUALS, ONE),
                Arguments.of(BASE_DECIMAL, SUBTRACTION + TWO + ZERO, SUBTRACTION + SIX, EQUALS, FOUR),
                Arguments.of(BASE_DECIMAL, ONE + ZERO.repeat(5), SEVEN, EQUALS, FIVE),
                Arguments.of(BASE_DECIMAL, TWO + TWO, FIVE, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, TWO + ZERO, SIX, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, TWO, FOUR, EQUALS, TWO)
        );
    }

    /* Invalid MODULUS */

    /* Valid CLEAR_ENTRY */

    /* Invalid CLEAR_ENTRY */

    /* Valid CLEAR */

    /* Invalid CLEAR */

    /* Valid DELETE */

    /* Invalid DELETE */

    /* Valid ADD */

    /* Invalid ADD */

    /* Valid SUBTRACT */

    /* Invalid SUBTRACT */

    /* Valid MULTIPLY */

    /* Invalid MULTIPLY */

    /* Valid DIVIDE */

    /* Invalid DIVIDE */

    /* Valid NUMBERS */

    /* Invalid NUMBERS ?? */

    /* Valid OPENING ( */
    @ParameterizedTest
    @DisplayName("Test Valid ( Button Action")
    @MethodSource("validLeftParenthesisButtonCases")
    void testValidLeftParenthesisButtonCases(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);
        calculator.setCalculatorBase(BASE_DECIMAL);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }

    private static Stream<ArgumentsForTests> validLeftParenthesisButtonCases() {
        return Stream.of(
                ArgumentsForTests.builder(LEFT_PARENTHESIS).testName("Test ( button shows () in textPane")
                        .firstUnaryOperator(LEFT_PARENTHESIS + ARGUMENT_SEPARATOR + EMPTY)
                        .firstUnaryResult(EMPTY).build(),
                ArgumentsForTests.builder(LEFT_PARENTHESIS).testName("Test 12,345 Then ( Keeps Delimiter")
                        .firstNumber("12,345")
                        .firstUnaryOperator(LEFT_PARENTHESIS)
                        .firstUnaryResult("12345" + ARGUMENT_SEPARATOR + "12,345" + SPACE + LEFT_PARENTHESIS + RIGHT_PARENTHESIS).build()
        );
    }

    /* Invalid CLOSING ) */

    /* Valid NEGATE */

    /* Invalid NEGATE */

    /* Valid DECIMAL */

    /* Invalid DECIMAL */

    /* Valid EQUALS */

    /* Invalid EQUALS (that's it) */

}
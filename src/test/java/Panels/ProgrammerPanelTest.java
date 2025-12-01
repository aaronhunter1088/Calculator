package Panels;

import Calculators.Calculator;
import Parent.ArgumentsForTests;
import Parent.TestParent;
import Types.CalculatorBase;
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

import static Types.CalculatorBase.BASE_BINARY;
import static Types.CalculatorBase.BASE_DECIMAL;
import static Types.Texts.*;
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
    static { System.setProperty("appName", ProgrammerPanelTest.class.getSimpleName()); }
    private static final Logger LOGGER = LogManager.getLogger(ProgrammerPanelTest.class);

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
                calculator.getProgrammerPanel().setCalculator(calculator); // links spyCalc to ProgrammerPanel
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
    { LOGGER.info("Finished running {}", ProgrammerPanel.class.getSimpleName()); }

    /* Valid LSH */

    /* Invalid LSH */

    /* Valid RSH */

    /* Invalid RSH */

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
                        .firstNumber("5").secondBinaryResult(OR).firstBinaryResult("5 OR").build()
                        .secondNumber("7").secondBinaryOperator(EQUALS).secondBinaryResult("3").build()
        );
    }

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
                // TODO: Add cases
        );
    }

    /* Valid XOR */

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
                ArgumentsForTests.builder(XOR).firstBinaryResult(XOR).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()
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

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory, LOGGER);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<ArgumentsForTests> validNotButtonCases()
    {
        // TODO: Need a way to set the base and byte
        return Stream.of(
                ArgumentsForTests.builder(NOT)
                        .firstNumber("0000 1011").firstUnaryResult("1111 0100").build(),
                ArgumentsForTests.builder(NOT)
                        .firstNumber("5").firstUnaryResult("...11111010").build()
        );
    }

    /* Invalid NOT */

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
                ArgumentsForTests.builder(AND).firstNumber("5").firstBinaryOperator(AND).firstBinaryResult("5|5 "+AND)
                        .secondNumber("6").secondBinaryOperator(EQUALS).secondBinaryResult(EMPTY+"|4").build()
        );
    }

    @Test
    @DisplayName("Test And Button continued operation")
    void testPerformAndContinuedOperation()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(FIVE).thenReturn(FIVE) // leave,
                .thenReturn(AND)
                .thenReturn(SIX).thenReturn(SIX) // leave
                .thenReturn(AND);
        calculator.getProgrammerPanel().getCalculator().performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getTextPaneValue(), "Expected textPane to be 5");

        calculator.getProgrammerPanel().performAndButtonAction(actionEvent);
        assertEquals("5 AND", calculator.getTextPaneValue(), "Expected textPane to be 5 AND");
        assertEquals(AND, calculator.getValueAt(2), "Expecting AND at values[2]");

        calculator.getProgrammerPanel().getCalculator().performNumberButtonAction(actionEvent);
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
                ArgumentsForTests.builder(AND).firstBinaryOperator(AND).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(AND).firstNumber(ENTER_A_NUMBER).firstUnaryResult(AND).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build(),
                ArgumentsForTests.builder(AND).firstNumber(FIVE).firstBinaryOperator(AND).firstBinaryResult("5|5 "+AND)
                        .secondBinaryOperator(AND).secondBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()
        );
    }

    /* Valid SHIFT */

    /* Invalid SHIFT ?? */

    /* Valid MODULUS */
    @ParameterizedTest()
    @MethodSource("getModulusCases")
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
    private static Stream<Arguments> getModulusCases()
    {
        return Stream.of(
                Arguments.of(BASE_BINARY, FIVE, THREE, EQUALS, "00000010"),
                Arguments.of(BASE_DECIMAL, FIVE, THREE, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, FOUR, THREE, EQUALS, ONE),
                Arguments.of(BASE_DECIMAL, FOUR, ZERO, EQUALS, INFINITY),
                Arguments.of(BASE_DECIMAL, SUBTRACTION+FOUR, TWO, EQUALS, ZERO),
                Arguments.of(BASE_DECIMAL, ONE+ZERO, THREE, EQUALS, ONE),
                Arguments.of(BASE_DECIMAL, ONE+SEVEN, FIVE, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, THREE, SEVEN, EQUALS, THREE),
                Arguments.of(BASE_DECIMAL, FOUR, ONE+ZERO, EQUALS, FOUR),
                Arguments.of(BASE_DECIMAL, SEVEN, SEVEN, EQUALS, ZERO),
                Arguments.of(BASE_DECIMAL, TWO+FIVE, FOUR, EQUALS, ONE),
                Arguments.of(BASE_DECIMAL, ONE+ZERO+ZERO, THREE+ZERO, EQUALS, ONE+ZERO),
                Arguments.of(BASE_DECIMAL, TWO+FOUR, SIX, EQUALS, ZERO),
                Arguments.of(BASE_DECIMAL, EIGHT+ONE, NINE, EQUALS, ZERO),
                Arguments.of(BASE_DECIMAL, SUBTRACTION+ONE+ZERO, THREE, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, SUBTRACTION+FIVE, SEVEN, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, ONE+ZERO, SUBTRACTION+THREE, EQUALS, ONE),
                Arguments.of(BASE_DECIMAL, SUBTRACTION+TWO+ZERO, SUBTRACTION+SIX, EQUALS, FOUR),
                Arguments.of(BASE_DECIMAL, ONE+ZERO.repeat(5), SEVEN, EQUALS, FOUR),
                Arguments.of(BASE_DECIMAL, TWO+TWO, FIVE, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, TWO+ZERO, SIX, EQUALS, TWO),
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

    /* Invalid CLOSING ) */

    /* Valid NEGATE */

    /* Invalid NEGATE */

    /* Valid DECIMAL */

    /* Invalid DECIMAL */

    /* Valid EQUALS */

    /* Invalid EQUALS (that's it) */

}
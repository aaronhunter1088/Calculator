package Panels;

import Calculators.Calculator;
import Parent.ArgumentsForTests;
import Parent.TestParent;
import Types.CalculatorUtility;
import Types.SystemDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import static Types.CalculatorUtility.*;
import static Types.CalculatorView.VIEW_BASIC;
import static Types.Texts.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * BasicPanelTest
 * <p>
 * This class tests the {@link BasicPanel} class.
 *
 * @author Michael Ball
 * @version 4.0
 */
//@ExtendWith(MockitoExtension.class)
class BasicPanelTest extends TestParent
{
    static { System.setProperty("appName", BasicPanelTest.class.getSimpleName()); }
    private static final Logger LOGGER = LogManager.getLogger(BasicPanelTest.class.getSimpleName());

    BasicPanel basicPanel;
    //private final double delta = 0.000001d;

    @Mock
    public ActionEvent actionEvent;

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
                basicPanel = calculator.getBasicPanel();
                basicPanel.setCalculator(calculator);
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

    /*
    Used by all tests
     */
    private String performTest(ArgumentsForTests arguments, String previousHistory)
    {
        // Extract arguments, place in variables
        String firstNumber = removeThousandsDelimiter(arguments.getFirstNumber(), calculator.getThousandsDelimiter());
        String firstUnaryOperator = arguments.getFirstUnaryOperator();
        String firstUnaryTextPaneResult = arguments.getFirstUnaryResult();
        String firstUnaryValuesResult = removeThousandsDelimiter(firstUnaryTextPaneResult, calculator.getThousandsDelimiter());
        if (firstUnaryTextPaneResult.split("\\|").length > 1) // "0|INFINITY or 35|35 +"
        {
            firstUnaryValuesResult = removeThousandsDelimiter(firstUnaryTextPaneResult.split("\\|")[0], calculator.getThousandsDelimiter());
            firstUnaryTextPaneResult = firstUnaryTextPaneResult.split("\\|")[1];
        }
        String firstBinaryOperator = arguments.getFirstBinaryOperator();
        String firstBinaryTextPaneResult = arguments.getFirstBinaryResult();
        String firstBinaryValuesResult = removeThousandsDelimiter(firstBinaryTextPaneResult, calculator.getThousandsDelimiter());
        if (firstBinaryTextPaneResult.split("\\|").length > 1)
        {
            firstBinaryValuesResult = removeThousandsDelimiter(firstBinaryTextPaneResult.split("\\|")[0], calculator.getThousandsDelimiter());
            firstBinaryTextPaneResult = firstBinaryTextPaneResult.split("\\|")[1];
        }
        String secondNumber = removeThousandsDelimiter(arguments.getSecondNumber(), calculator.getThousandsDelimiter());
        String secondUnaryOperator = arguments.getSecondUnaryOperator();
        String secondUnaryTextPaneResult = arguments.getSecondUnaryResult();
        String secondUnaryValuesResult = removeThousandsDelimiter(secondUnaryTextPaneResult, calculator.getThousandsDelimiter());
        if (secondUnaryTextPaneResult.split("\\|").length > 1)
        {
            secondUnaryValuesResult = removeThousandsDelimiter(secondUnaryTextPaneResult.split("\\|")[0], calculator.getThousandsDelimiter());
            secondUnaryTextPaneResult = secondUnaryTextPaneResult.split("\\|")[1];
        }
        String secondBinaryOperator = arguments.getSecondBinaryOperator();
        String secondBinaryTextPaneResult = arguments.getSecondBinaryResult();
        String secondBinaryValuesResult = removeThousandsDelimiter(secondBinaryTextPaneResult, calculator.getThousandsDelimiter());
        if (secondBinaryTextPaneResult.split("\\|").length > 1)
        {
            secondBinaryValuesResult = removeThousandsDelimiter(secondBinaryTextPaneResult.split("\\|")[0], calculator.getThousandsDelimiter());
            secondBinaryTextPaneResult = secondBinaryTextPaneResult.split("\\|")[1];
        }

        // Start test
        if (!firstNumber.isEmpty() && !calculator.getBadText(firstNumber).equals(firstNumber))
        {
            performNumberButtonActionForEachCharacter(actionEvent, firstNumber);
            previousHistory = calculator.getHistoryTextPane().getText();
        }
        else exhaustActionEvent("firstNumber", actionEvent, LOGGER);

        if (!firstUnaryOperator.isEmpty())
        {
            performNextOperatorAction(calculator, actionEvent, LOGGER, firstUnaryOperator);
            assertEquals(firstUnaryTextPaneResult, calculator.getTextPaneValue(), "textPane is not as expected");
            assertEquals(firstUnaryValuesResult, calculator.getValues()[0], "Values[0] is not as expected");
            previousHistory = calculator.getHistoryTextPane().getText();
        }
        else exhaustActionEvent("firstUnaryOperator", actionEvent, LOGGER);

        if (!firstBinaryOperator.isEmpty())
        {
            performNextOperatorAction(calculator, actionEvent, LOGGER, firstBinaryOperator);
            assertEquals(firstBinaryTextPaneResult, calculator.getTextPaneValue(), "textPane is not as expected");
            assertEquals(firstBinaryValuesResult, calculator.getValues()[0], "Values[0] is not as expected");
            previousHistory = calculator.getHistoryTextPane().getText();
        }
        else exhaustActionEvent("firstBinaryOperator", actionEvent, LOGGER);

        if (!secondNumber.isEmpty())
        {
            performNumberButtonActionForEachCharacter(actionEvent, secondNumber);
            previousHistory = calculator.getHistoryTextPane().getText();
        }
        else exhaustActionEvent("secondNumber", actionEvent, LOGGER);

        if (!secondUnaryOperator.isEmpty())
        {
            performNextOperatorAction(calculator, actionEvent, LOGGER, secondUnaryOperator);
            assertEquals(secondUnaryTextPaneResult, calculator.getTextPaneValue(), "textPane is not as expected");
            assertEquals(secondUnaryValuesResult, calculator.getValues()[calculator.getValuesPosition()], "Values[vP] is not as expected");
            previousHistory = calculator.getHistoryTextPane().getText();
        }
        else exhaustActionEvent("secondUnaryOperator", actionEvent, LOGGER);

        if (!secondBinaryOperator.isEmpty())
        {
            performNextOperatorAction(calculator, actionEvent, LOGGER, secondBinaryOperator);
            assertEquals(secondBinaryTextPaneResult, calculator.getTextPaneValue(), "textPane is not as expected");
            assertEquals(secondBinaryValuesResult, calculator.getValues()[1], "Values[1] is not as expected");
            previousHistory = calculator.getHistoryTextPane().getText();
        }
        else exhaustActionEvent("secondBinaryOperator", actionEvent, LOGGER);

        return previousHistory;
    }

    /**
     * Tests that the history is as expected after
     * pushing a button.
     * Method writeHistory calls writeHistoryWithMessage.
     *
     * @param arguments for the test
     * @param previousHistory the previous history before the button was pushed
     */
    private void assertHistory(ArgumentsForTests arguments, String previousHistory)
    {
        String firstNumber = arguments.getFirstNumber();
        String firstUnaryTextPaneResult = arguments.getFirstUnaryResult();
        String firstUnaryValuesResult = firstUnaryTextPaneResult;
        if (firstUnaryTextPaneResult.split("\\|").length > 1) // "0|INFINITY or 35|35 +"
        {
            firstUnaryValuesResult = firstUnaryTextPaneResult.split("\\|")[0];
            firstUnaryTextPaneResult = firstUnaryTextPaneResult.split("\\|")[1];
        }
        String buttonText = LEFT_PARENTHESIS + arguments.getOperatorUnderTest() + RIGHT_PARENTHESIS + SPACE;

        switch (arguments.getOperatorUnderTest())
        {
            case MEMORY_STORE -> {
                assertEquals(previousHistory, calculator.getHistoryTextPane().getText(),
                        "History textPane should show memory");
            }
            case DELETE -> {
                // calls writeHistory
                assertEquals(previousHistory, calculator.getHistoryTextPane().getText(),
                        "History textPane should show the button pressed");
            }
            default -> {
                // TODO: Fix
                assertEquals(previousHistory + NEWLINE + buttonText,
                        calculator.getHistoryTextPane().getText(),
                        "History textPane should show the button pressed");
            }
        }
    }

    /* Valid MEMORY STORE */
    @ParameterizedTest
    @DisplayName("Test Valid MemoryStore Button Action")
    @MethodSource("validMemoryStoreButtonActionProvider")
    void testValidMemoryStoreButtonAction(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPane().getText();
        previousHistory = performTest(arguments, previousHistory);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<Arguments> validMemoryStoreButtonActionProvider()
    {
        /* Input, Optional Operator, (MemoryStore), Result of MemoryStore
        String firstNumber, (EMPTY or 1 or more digits, thousands delimiter will be removed)
        String optionalOperator, (EMPTY or ANY Unary operator)
        (MS button pressed, not pass in as it is understood)
        String resultStoredInMemory, (expected result in memories[0] after MS pressed)
         */
        return Stream.of(
//                Arguments.of("1,234", EMPTY, "1,234"),
                Arguments.of(ArgumentsForTests.builder(MEMORY_STORE)
                        .firstNumber("1,234")
                        .firstUnaryOperator(MEMORY_STORE)
                        .firstUnaryResult("1,234")
                        .build())
        );
    }

    @Test
    @DisplayName("Test MemoryStore overwrites memory when memory is full")
    void testMemoryStoreOverwritesMemoryWhenMemoryIsFull()
    {
        postConstructCalculator();
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_STORE);
        calculator.getTextPane().setText(TWO);
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(10);
        assertEquals("15", calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 15");
        calculator.performMemoryStoreAction(actionEvent);
        assertEquals(TWO, calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 2");
    }

    /* Invalid MEMORY STORE */
    @ParameterizedTest
    @DisplayName("Test Invalid MemoryStore Button Action")
    @MethodSource("invalidMemoryStoreButtonActionProvider")
    void testInvalidMemoryStoreButtonAction(ArgumentsAccessor arguments)
    {
        postConstructCalculator();
        String firstNumber = removeThousandsDelimiter(arguments.getString(0), calculator.getThousandsDelimiter());
        int firstNumberLength = firstNumber != null ? firstNumber.length() : 0;
        String firstOperator = arguments.getString(1);
        String firstExpectedResult = removeThousandsDelimiter(arguments.getString(2), calculator.getThousandsDelimiter());

        setupWhenThenFirstOperatorThenOperatorUnderTest(actionEvent, MEMORY_STORE, firstNumber, firstOperator);

        if (firstNumberLength != 0) performNumberButtonActionForEachCharacter(actionEvent, firstNumber);
        if (firstOperator != null && !firstOperator.isEmpty())
        {
            performNextOperatorAction(calculator, actionEvent, LOGGER, firstOperator);
            assertEquals(addThousandsDelimiter(firstNumber, calculator.getThousandsDelimiter()) + SPACE + firstOperator,
                    calculator.getTextPaneValue(),
                    "Expecting textPane to show: " + firstNumber + SPACE + firstOperator);
        }
        String previousHistory = calculator.getHistoryTextPane().getText();
        calculator.performMemoryStoreAction(actionEvent);

        String actualMemoryAtPosition = calculator.getMemoryValues()[calculator.getLowestMemoryPosition()];
        if (!actualMemoryAtPosition.isEmpty())
        {
            assertEquals(addThousandsDelimiter(firstNumber , calculator.getThousandsDelimiter()), calculator.getTextPaneValue(), "TextPane value is not as expected");
            assertEquals(firstNumber, calculator.getValues()[0], "values[0] is not as expected");
            assertEquals(firstExpectedResult, actualMemoryAtPosition, "Memory value is not as expected");
            assertEquals(previousHistory+NEWLINE+"(MS) "+savedMemory(actualMemoryAtPosition,calculator.getLowestMemoryPosition()),
                    calculator.getHistoryTextPane().getText(),
                    "History textPane should show memory");
        }

    }
    private static Stream<Arguments> invalidMemoryStoreButtonActionProvider()
    {
        /* Input, Operator, MemoryStore, Result of MemoryStore
        String firstNumber, (EMPTY or 1 or more digits, thousands delimiter will be removed)
        String possibleOperator, (EMPTY or ANY Basic operator)
        (MS button pressed, not pass in as it is understood)
        String resultStoredInMemory, (expected result in memories[0] after MS pressed)
         */
        return Stream.of(
                Arguments.of("10", ADDITION, EMPTY),
                Arguments.of(EMPTY, EMPTY, ENTER_A_NUMBER),
                Arguments.of(INFINITY, EMPTY, EMPTY)
        );
    }

    /* Valid MEMORY RECALL */
    @Test
    @DisplayName("Test MemoryRecall Button Action")
    void pressedMemoryRecall()
    {
        postConstructCalculator();
        when(actionEvent.getActionCommand())
                .thenReturn(MEMORY_RECALL);
        calculator.getMemoryValues()[0] = "15";
        calculator.getMemoryValues()[1] = "534";
        calculator.getMemoryValues()[2] = "-9";
        calculator.getMemoryValues()[3] = "75";
        calculator.getMemoryValues()[4] = TWO;
        calculator.getMemoryValues()[5] = "1080";
        calculator.setMemoryPosition(6);
        calculator.setMemoryRecallPosition(0);
        calculator.performMemoryRecallAction(actionEvent);
        assertEquals("15", calculator.getTextPaneValue(), "Expected textPane to show 15");
        assertSame(1, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 1");
        assertSame(6, calculator.getMemoryPosition(), "Expected memoryPosition to be 6");
        calculator.performMemoryRecallAction(actionEvent);
        assertEquals("534", calculator.getTextPaneValue(), "Expected textPane to show 534");
        assertSame(2, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 2");
        assertSame(6, calculator.getMemoryPosition(), "Expected memoryPosition to be 6");
        calculator.performMemoryRecallAction(actionEvent);
        assertEquals("-9", calculator.getTextPaneValue(), "Expected textPane to show -9");
        assertSame(3, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 3");
        assertSame(6, calculator.getMemoryPosition(), "Expected memoryPosition to be 6");
        calculator.setMemoryRecallPosition(10);
        calculator.performMemoryRecallAction(actionEvent);
        assertEquals("15", calculator.getTextPaneValue(), "Expected textPane to show 15");
        assertSame(1, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 1");
        assertSame(6, calculator.getMemoryPosition(), "Expected memoryPosition to be 6");
    }

    /* Valid MEMORY CLEAR */
    @Test
    @DisplayName("Test MemoryClear Button Action")
    void pressedMemoryClear()
    {
        postConstructCalculator();
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_CLEAR);
        calculator.getMemoryValues()[9] = "15";
        calculator.setMemoryPosition(10);
        calculator.performMemoryClearAction(actionEvent);
        assertTrue(calculator.getMemoryValues()[9].isBlank(), "Expected memoryValues[9] to be empty");
        assertSame(0, calculator.getMemoryPosition(), "Expected memoryPosition to be 0");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertFalse(calculator.getButtonMemoryClear().isEnabled(), "Expected memoryClear to be disabled, no more memories");
        assertFalse(calculator.getButtonMemoryRecall().isEnabled(), "Expected memoryRecall to be disabled, no more memories");
        assertFalse(calculator.getButtonMemoryAddition().isEnabled(), "Expected memoryAdd to be disabled, no more memories");
        assertFalse(calculator.getButtonMemorySubtraction().isEnabled(), "Expected memorySubtract to be disabled, no more memories");
    }

    /* Valid MEMORY ADD */
    @ParameterizedTest
    @DisplayName("Test Valid MemoryAddition Button Action")
    @MethodSource("memoryAddButtonCases")
    void testMemoryAdditionButtonAction(ArgumentsAccessor arguments)
    {
        postConstructCalculator();
        String initialMemory = arguments.getString(0);
        String numberToAdd = arguments.getString(1);

        boolean numberToAddEndsWithOperator = calculator.getBasicPanelOperators()
                .stream().anyMatch(op -> numberToAdd.endsWith(op));
        String operator = initialMemory != null && numberToAddEndsWithOperator ?
                String.valueOf(numberToAdd.charAt(initialMemory.length()-1)) : EMPTY;
        String expectedResult = arguments.getString(2);
        when(actionEvent.getActionCommand())
                .thenReturn(MEMORY_ADDITION);
        calculator.getMemoryValues()[0] = initialMemory;
        calculator.appendTextToPane(numberToAdd, true);
        if (!operator.isEmpty()) {
            calculator.setActiveOperator(operator);
            calculator.setValuesPosition(1);
        }
        calculator.setMemoryPosition(1);
        calculator.performMemoryAdditionAction(actionEvent);
        String actualMemoryAtPosition = calculator.getMemoryValues()[0];
        assertEquals(expectedResult, actualMemoryAtPosition, "Memory value is not as expected");
        assertSame(1, calculator.getMemoryPosition(), "Expected memoryPosition to be 0");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
    }
    private static Stream<Arguments> memoryAddButtonCases()
    {
        /* Initial Memory, Number to Add, (optional operator at end), Resulting Memory
        Initial Memory,
        Number to Add, (optional operator at end)
        Resulting Memory
         */
        return Stream.of(
                Arguments.of("10", "5", "15"),
                Arguments.of("0", "0", "0"),
                Arguments.of("-5", "10", "5"),
                Arguments.of("100.5", "99.5", "200"),
                Arguments.of("-12.25", "1", "-11.25"),
                Arguments.of("10", ENTER_A_NUMBER, "10"), // invalid case
                Arguments.of(EMPTY, ENTER_A_NUMBER, EMPTY), // invalid case
                Arguments.of("10", EMPTY, "10"), // invalid case
                Arguments.of("10", "5 +", "10") // invalid case
        );
    }

    /* Valid MEMORY SUBTRACT */
    @ParameterizedTest
    @DisplayName("Test Valid MemorySubtraction Button Action")
    @MethodSource("memorySubtractButtonCases")
    void testMemorySubtractionButtonAction(ArgumentsAccessor arguments)
    {
        postConstructCalculator();
        String initialMemory = arguments.getString(0);
        String numberToSubtract = arguments.getString(1);

        boolean numberToSubtractEndsWithOperator = calculator.getBasicPanelOperators()
                .stream().anyMatch(op -> numberToSubtract.endsWith(op));
        String operator = initialMemory != null && numberToSubtractEndsWithOperator ?
                String.valueOf(numberToSubtract.charAt(initialMemory.length()-1)) : EMPTY;
        String expectedResult = arguments.getString(2);
        when(actionEvent.getActionCommand())
                .thenReturn(MEMORY_SUBTRACTION);
        calculator.getMemoryValues()[0] = initialMemory;
        calculator.appendTextToPane(numberToSubtract, true);
        if (!operator.isEmpty()) {
            calculator.setActiveOperator(operator);
            calculator.setValuesPosition(1);
        }
        calculator.setMemoryPosition(1);
        calculator.performMemorySubtractionAction(actionEvent);
        String actualMemoryAtPosition = calculator.getMemoryValues()[0];
        assertEquals(expectedResult, actualMemoryAtPosition, "Memory value is not as expected");
    }
    private static Stream<Arguments> memorySubtractButtonCases()
    {
        /* Initial Memory, Number to Subtract, (optional operator at end), Resulting Memory
        Initial Memory,
        Number to Subtract, (optional operator at end)
        Resulting Memory
         */
        return Stream.of(
                Arguments.of("10", "5", "5"),
                Arguments.of("0", "0", "0"),
                Arguments.of("-5", "10", "-15"),
                Arguments.of("100.5", "99.5", "1"),
                Arguments.of("-12.25", "1", "-13.25"),
                Arguments.of("10", ENTER_A_NUMBER, "10"), // invalid case
                Arguments.of("10", EMPTY, "10"), // invalid case
                Arguments.of("10", "5 +", "10") // invalid case
        );
    }

    /* Valid PERCENT */
    @ParameterizedTest
    @DisplayName("Test Valid Percent Button Action")
    @MethodSource("validPercentButtonCases")
    void testValidPercentButtonCases(ArgumentsAccessor arguments)
    {
        postConstructCalculator();
        String firstNumber = arguments.getString(0); // firstNumber
        String firstExpectedResult = arguments.getString(1); // firstExpectedResult
        String firstOperator = arguments.size() > 2 ? arguments.getString(2) : EMPTY; // first Op
        String secondNumber = arguments.size() > 2 ? arguments.getString(3) : EMPTY; // secondNumber
        String secondExpectedResult = arguments.size() > 4 ? arguments.getString(4) : EMPTY; // secondExpectedResult
        String secondOperator = arguments.size() >= 5 ? arguments.getString(5) : EMPTY; // secondOperator

        setupWhenThen(actionEvent, PERCENT, firstNumber, firstOperator, secondNumber, secondOperator);

        if (!firstNumber.isEmpty()) performNumberButtonActionForEachCharacter(actionEvent, firstNumber);
        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals(firstExpectedResult, removeThousandsDelimiter(calculator.getTextPaneValue(), calculator.getThousandsDelimiter()), "TextPane value not as expected");
        if (!calculator.textPaneContainsBadText()) assertEquals(firstExpectedResult, calculator.getValueAt(0), "values[0] returned unexpected result");
        assertEquals(!calculator.isDecimalPressed(), firstExpectedResult.contains(DECIMAL), "Decimal state not as expected");

        if (arguments.size() > 2) {
            performNextOperatorAction(calculator, actionEvent, LOGGER, firstOperator);
            if (!secondNumber.isEmpty()) performNumberButtonActionForEachCharacter(actionEvent, secondNumber);
            basicPanel.performPercentButtonAction(actionEvent);
            assertEquals(secondExpectedResult, removeThousandsDelimiter(calculator.getTextPaneValue(), calculator.getThousandsDelimiter()), "Second textPane value not as expected");
            if (!calculator.textPaneContainsBadText()) assertEquals(secondExpectedResult, calculator.getValueAt(1), "values[1] returned unexpected result");

            if (arguments.size() > 4) {
                performNextOperatorAction(calculator, actionEvent, LOGGER, secondOperator);
                assertEquals(arguments.getString(5), calculator.getValueAt(2), "values[2] not equal to " + arguments.getString(5));
            }
        }
    }
    private static Stream<Arguments> validPercentButtonCases()
    {
        /*
        String firstNumber, (PERCENT button pressed) String firstResult,
        String firstOperator, String secondNumber, (PERCENT button pressed) String secondResult,
        String secondOperator
         */
        return Stream.of(
                Arguments.of("0", "0"),
                Arguments.of("5", "0.05"),
                Arguments.of("-5", "-0.05"),
                Arguments.of("0.25", "0.0025"),

                Arguments.of("4", "0.04", ADDITION, "2", "0.02", SUBTRACTION),
                Arguments.of("10", "0.1", ADDITION, "20", "0.2", SUBTRACTION)
        );
    }

    /* Invalid PERCENT */
    @ParameterizedTest
    @DisplayName("Test Invalid Percent Button Action")
    @MethodSource("invalidPercentButtonCases")
    void testInvalidPercentButtonCases(ArgumentsAccessor arguments)
    {
        postConstructCalculator();
        String firstNumber = arguments.getString(0); // firstNumber
        String firstExpectedResult = arguments.getString(1); // firstExpectedResult
        String firstOperator = arguments.size() > 2 ? arguments.getString(2) : EMPTY; // first Op
        String secondNumber = arguments.size() > 2 ? arguments.getString(3) : EMPTY; // secondNumber
        String secondExpectedResult = arguments.size() > 4 ? arguments.getString(4) : EMPTY; // secondExpectedResult

        setupWhenThen(actionEvent, PERCENT, firstNumber, firstOperator, secondNumber, EMPTY);

        if (!calculator.badTexts().contains(firstNumber))
        {
            performNumberButtonActionForEachCharacter(actionEvent, firstNumber);
        }
        else
        {
            calculator.appendTextToPane(firstNumber, true);
        }
        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals(firstExpectedResult, removeThousandsDelimiter(calculator.getTextPaneValue(), calculator.getThousandsDelimiter()), "TextPane value not as expected");
        if (!calculator.textPaneContainsBadText()) assertEquals(firstExpectedResult, calculator.getValueAt(0), "values[0] returned unexpected result");
        assertEquals(calculator.isDecimalPressed(), firstExpectedResult.contains(DECIMAL), "Decimal state not as expected");

        if (arguments.size() > 2) {
            performNextOperatorAction(calculator, actionEvent, LOGGER, firstOperator);
            if (!secondNumber.isEmpty()) performNumberButtonActionForEachCharacter(actionEvent, secondNumber);
            basicPanel.performPercentButtonAction(actionEvent);
            assertEquals(secondExpectedResult, removeThousandsDelimiter(calculator.getTextPaneValue(), calculator.getThousandsDelimiter()), "Second textPane value not as expected");
            if (!secondNumber.isEmpty()) assertEquals(secondExpectedResult, calculator.getValueAt(1), "values[1] returned unexpected result");
            assertEquals(firstOperator, calculator.getValueAt(2), "values[2] not equal to " + firstOperator);
        }
    }
    private static Stream<Arguments> invalidPercentButtonCases()
    {
        /*
        String firstNumber, (PERCENT button pressed) String firstResult,
        String firstOperator, String secondNumber, (PERCENT button pressed) String secondResult,
        String secondOperator
         */
        return Stream.of(
                Arguments.of(EMPTY, ENTER_A_NUMBER),
                Arguments.of(INFINITY, INFINITY),
                Arguments.of(ENTER_A_NUMBER, ENTER_A_NUMBER),
                Arguments.of("5", "0.05", ADDITION, EMPTY, "0.05 +")
        );
    }

    /* Valid SQUARE ROOT */
    /* Invalid SQUARE ROOT */

    /* Valid SQUARED */
    @Test
    void testingMathPow()
    {
        postConstructCalculator();

        double delta = 0.000001d;
        Number num = 8.0;
        assertEquals(num.doubleValue(), Math.pow(2,3), delta);
    }

    /* Valid FRACTION */
    @ParameterizedTest()
    @DisplayName("Test Valid Fraction Button Action")
    @MethodSource("validFractionCases")
    void testValidFractionButtonCases(ArgumentsAccessor arguments)
    {
        postConstructCalculator();
        String firstNumber = arguments.getString(0); // firstNumber
        String firstExpectedResult = arguments.getString(1); // firstExpectedResult
        String firstOperator = arguments.size() > 2 ? arguments.getString(2) : EMPTY; // first Op
        String secondNumber = arguments.size() > 2 ? arguments.getString(3) : EMPTY; // secondNumber
        String secondExpectedResult = arguments.size() > 4 ? arguments.getString(4) : EMPTY; // secondExpectedResult
        String secondOperator = arguments.size() >= 5 ? arguments.getString(5) : EMPTY; // secondOperator

        setupWhenThen(actionEvent, FRACTION, firstNumber, firstOperator, secondNumber, secondOperator);

        if (!firstNumber.isEmpty()) performNumberButtonActionForEachCharacter(actionEvent, firstNumber);
        basicPanel.performFractionButtonAction(actionEvent);
        assertEquals(firstExpectedResult, removeThousandsDelimiter(calculator.getTextPaneValue(), calculator.getThousandsDelimiter()), "TextPane value not as expected");
        if (!calculator.textPaneContainsBadText()) assertEquals(firstExpectedResult, calculator.getValueAt(0), "values[0] returned unexpected result");
        assertEquals(!calculator.isDecimalPressed(), firstExpectedResult.contains(DECIMAL), "Decimal state not as expected");

        if (arguments.size() > 2)
        {
            performNextOperatorAction(calculator, actionEvent, LOGGER, firstOperator);
            if (!secondNumber.isEmpty()) performNumberButtonActionForEachCharacter(actionEvent, secondNumber);
            basicPanel.performFractionButtonAction(actionEvent);
            assertEquals(arguments.getString(4), removeThousandsDelimiter(calculator.getTextPaneValue(), calculator.getThousandsDelimiter()), "TextPane value not as expected");
            if (!calculator.textPaneContainsBadText()) assertEquals(secondExpectedResult, calculator.getValueAt(1), "values[1] returned unexpected result");

            if (arguments.size() > 4)
            {
                performNextOperatorAction(calculator, actionEvent, LOGGER, secondOperator);
                assertEquals(secondOperator, calculator.getValueAt(2), "values[2] not equal to " + arguments.getString(5));
            }
        }
    }
    private static Stream<Arguments> validFractionCases()
    {
        /*
        String firstNumber, (FRACTION button pressed) String firstResult,
        String firstOperator, String secondNumber, (FRACTION button pressed) String secondResult,
        String secondOperator
         */
        return Stream.of(
                Arguments.of("0", INFINITY),
                Arguments.of("5", "0.2"),
                Arguments.of("0.25", "4"),

                Arguments.of("4", "0.25", ADDITION, "2", "0.5", SUBTRACTION)
        );
    }

    /* Invalid FRACTION */
    @ParameterizedTest()
    @DisplayName("Test Invalid Fraction Button Action")
    @MethodSource("getInvalidFractionCases")
    void testInvalidFractionButtonCases(ArgumentsAccessor arguments)
    {
        postConstructCalculator();
        String firstInput = arguments.getString(0); // firstInput
        String textPaneExpectedResult = arguments.getString(1); // firstExpectedResult
        String valuesExpectedResult = arguments.size() > 2 ? arguments.getString(2) : EMPTY; // first Op
        //String secondNumber = arguments.size() > 2 ? arguments.getString(3) : EMPTY; // secondNumber
        //String secondExpectedResult = arguments.size() > 4 ? arguments.getString(4) : EMPTY; // secondExpectedResult
        //String secondOperator = arguments.size() >= 5 ? arguments.getString(5) : EMPTY; // secondOperator

        when(actionEvent.getActionCommand())
                .thenReturn(firstInput) // any button
                .thenReturn(FRACTION);
        calculator.getMemoryValues()[0] = FIVE; // set for memory button scenarios, not from arguments
        if (calculator.getBadText(EMPTY).equals(firstInput))
        {
            calculator.appendTextToPane(firstInput);
        }

        if (firstInput != null) performNextOperatorAction(calculator, actionEvent, LOGGER, firstInput); // any button or no button
        basicPanel.performFractionButtonAction(actionEvent); // press fraction

        assertEquals(textPaneExpectedResult, removeThousandsDelimiter(calculator.getTextPaneValue(), calculator.getThousandsDelimiter()), "TextPane value not as expected");
        assertEquals(valuesExpectedResult, calculator.getValueAt(0), "values[0] returned unexpected result");
    }
    private static Stream<Arguments> getInvalidFractionCases()
    {
        /*
        buttonChoice, textPaneResult, values[0]Result
         */
        return Stream.of(
                Arguments.of(null, ENTER_A_NUMBER, EMPTY),
                Arguments.of(ENTER_A_NUMBER, ENTER_A_NUMBER, EMPTY),
                Arguments.of(MEMORY_STORE, ENTER_A_NUMBER, EMPTY),
                Arguments.of(MEMORY_RECALL, "0.2", "0.2"),
                Arguments.of(MEMORY_CLEAR, INFINITY, INFINITY), // look into
                Arguments.of(PERCENT, ENTER_A_NUMBER, EMPTY),
                Arguments.of(SQUARE_ROOT, ENTER_A_NUMBER, EMPTY),
                Arguments.of(SQUARED, ENTER_A_NUMBER, EMPTY),
                Arguments.of(FRACTION, ENTER_A_NUMBER, EMPTY),
                Arguments.of(CLEAR_ENTRY, ENTER_A_NUMBER, EMPTY),
                Arguments.of(CLEAR, INFINITY, INFINITY)
        );
    }

    /* Valid CLEAR ENTRY */
    @ParameterizedTest
    @DisplayName("Test Valid ClearEntry Button Action")
    @MethodSource("validClearEntryCases")
    void testValidClearEntryButtonCases(ArgumentsAccessor arguments)
    {
        postConstructCalculator();
        String firstNumber = arguments.getString(0); // firstNumber
        String firstOperator = arguments.size() > 1 ? arguments.getString(1) : EMPTY; // first Op
        String secondNumber = arguments.size() > 2 ? arguments.getString(2) : EMPTY; // secondNumber
        String secondOperator = arguments.size() > 3 ? arguments.getString(3) : EMPTY; // secondOperator

        setupWhenThen(actionEvent, CLEAR_ENTRY, firstNumber, firstOperator, secondNumber, secondOperator);

        if (!firstNumber.isEmpty()) performNumberButtonActionForEachCharacter(actionEvent, firstNumber);
        if (!firstOperator.isEmpty()) performNextOperatorAction(calculator, actionEvent, LOGGER, firstOperator);
        calculator.performClearEntryButtonAction(actionEvent);

        assertTrue(calculator.getTextPaneValue().isEmpty(), "textPane was not cleared");
        if (calculator.getValuesPosition() == 0)
        {
            assertEquals(EMPTY, calculator.getValueAt(0), "Expecting values[0] to be empty");
        }
        else
        {
            assertEquals(firstNumber, calculator.getValueAt(0), "Expecting values[0] to be " + firstNumber);
            assertEquals(EMPTY, calculator.getValueAt(1), "Expecting values[1] to be empty");
        }
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting values[2] to be EMPTY");
        assertFalse(calculator.isNegativeNumber(), "Expected isNegativeNumber to be false");
        assertFalse(calculator.isDecimalPressed(), "Expected decimal button to be enabled");
        if (!firstNumber.isEmpty()) assertFalse(calculator.isObtainingFirstNumber(), "Expecting obtainingFirstNumber to be false");
        else assertTrue(calculator.isObtainingFirstNumber(), "Expecting obtainingFirstNumber to be true");
        if (!secondOperator.isEmpty())
        {
            performNextOperatorAction(calculator, actionEvent, LOGGER, secondOperator);
        }
        if (secondOperator.equals(CLEAR_ENTRY))
        {
            assertEquals(EMPTY, calculator.getValueAt(0), "Expecting values[0] to be EMPTY");
            assertEquals(EMPTY, calculator.getValueAt(1), "Expecting values[1] to be EMPTY");
            assertEquals(EMPTY, calculator.getValueAt(2), "Expecting values[2] to be EMPTY");
            assertEquals(EMPTY, calculator.getValueAt(3), "Expecting values[3] to be EMPTY");
            assertFalse(calculator.isNegativeNumber(), "Expected isNegativeNumber to be false");
            assertFalse(calculator.isDecimalPressed(), "Expected decimalPressed to be false");
            assertFalse(calculator.isObtainingFirstNumber(), "Expecting obtainingFirstNumber to be false");
        }
        else
        {
            LOGGER.debug("{} completed, no further assertions. Add other assertions as needed", secondOperator);
        }
    }
    private static Stream<Arguments> validClearEntryCases()
    {
        /*
        String firstNumber, (EMPTY or some value)
        String firstOperator, (EMPTY or any BINARY operator (not any UNARY operator))
        (CLEAR_ENTRY button pressed, not passed in as it is understood)
        String secondNumber, (EMPTY or some value)
        String secondOperator (EMPTY or ANY operator)
         */
        return Stream.of(
                Arguments.of(EMPTY),
                Arguments.of("100"),
                Arguments.of("100", ADDITION),
                Arguments.of("2727", SUBTRACTION, "100"),
                Arguments.of("3.14", MULTIPLICATION, "2727", CLEAR_ENTRY)
        );
    }

    /* Invalid CLEAR ENTRY */

    /* Valid CLEAR */
    @ParameterizedTest
    @DisplayName("Test Valid Clear Button Action")
    @MethodSource("validClearButtonCases")
    void testValidClearButtonCases(ArgumentsAccessor arguments)
    {
        postConstructCalculator();
        String firstNumber = arguments.getString(0); // firstNumber
        String firstOperator = arguments.size() > 1 ? arguments.getString(1) : EMPTY; // first Op
        String secondNumber = arguments.size() > 2 ? arguments.getString(2) : EMPTY; // secondNumber
        String secondOperator = arguments.size() > 3 ? arguments.getString(3) : EMPTY; // secondOperator

        setupWhenThen(actionEvent, CLEAR_ENTRY, firstNumber, firstOperator, secondNumber, secondOperator);
        //setupWhenThenFirstOperatorThenOperatorUnderTest(actionEvent, CLEAR_ENTRY, firstNumber, firstOperator, secondNumber, secondOperator);

        if (!firstNumber.isEmpty()) performNumberButtonActionForEachCharacter(actionEvent, firstNumber);
        if (!firstOperator.isEmpty()) performNextOperatorAction(calculator, actionEvent, LOGGER, firstOperator);
        calculator.performClearButtonAction(actionEvent);

        assertEquals(ZERO, calculator.getTextPaneValue(), "textPane should be set to 0");
        assertEquals(0, calculator.getValuesPosition(), "valuesPosition was not reset to 0");
        assertEquals(0, calculator.getMemoryPosition(), "memoryPosition was not reset to 0");
        assertEquals(EMPTY, calculator.getValueAt(0), "Expecting values[0] to be empty");
        assertEquals(EMPTY, calculator.getValueAt(1), "Expecting values[1] to be empty");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting values[2] to be EMPTY");
        assertEquals(EMPTY, calculator.getValueAt(3), "Expecting values[3] to be EMPTY");
        assertFalse(calculator.isNegativeNumber(), "Expected isNegativeNumber to be false");
        assertFalse(calculator.isDecimalPressed(), "Expected decimal button to be enabled");
        assertTrue(calculator.isObtainingFirstNumber(), "Expecting obtainingFirstNumber to be true");
        for (String memory : calculator.getMemoryValues())
        {
            assertEquals(EMPTY, memory, "Expecting all memory values to be EMPTY");
        }
        assertTrue(calculator.getButtonMemoryStore().isEnabled(), "Button memory store should be enabled");
        assertFalse(calculator.getButtonMemoryClear().isEnabled(), "Expected memoryClear button to be disabled");
        assertFalse(calculator.getButtonMemoryRecall().isEnabled(), "Expected memoryRecall button to be disabled");
        assertFalse(calculator.getButtonMemoryAddition().isEnabled(), "Expected memoryAdd button to be disabled");
        assertFalse(calculator.getButtonMemorySubtraction().isEnabled(), "Expected memorySubtract button to be disabled");
    }
    private static Stream<Arguments> validClearButtonCases()
    {
        /*
        String firstNumber, (EMPTY or some value)
        String firstOperator, (EMPTY or any BINARY operator (not any UNARY operator))
        (CLEAR button pressed, not passed in as it is understood)
        String secondNumber, (EMPTY or some value)
        String secondOperator (EMPTY or ANY operator)
         */
        return Stream.of(
                Arguments.of(EMPTY),
                Arguments.of("100"),
                Arguments.of("100", ADDITION),
                Arguments.of("2727", SUBTRACTION, "100"),
                Arguments.of("3.14", MULTIPLICATION, "2727", CLEAR)
        );
    }

    /* Invalid CLEAR */

    /* Valid DELETE ⌫ */
    @ParameterizedTest
    @DisplayName("Test Valid Delete Button Action")
    @MethodSource("validDeleteButtonCases")
    void pressedDelete(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String previousHistory = calculator.getHistoryTextPaneValue();
        previousHistory = performTest(arguments, previousHistory);

        assertHistory(arguments, previousHistory);
    }
    private static Stream<Arguments> validDeleteButtonCases()
    {
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("35").firstUnaryOperator(DELETE).firstUnaryResult("3")),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("35.12").firstUnaryOperator(DELETE).firstUnaryResult("35.1")),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("35").firstUnaryOperator(EMPTY).firstUnaryResult(EMPTY).firstBinaryOperator(ADDITION).firstBinaryResult("35|35 "+ADDITION).secondUnaryOperator(DELETE).secondUnaryResult("35")),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("1").firstUnaryOperator(EMPTY).firstUnaryResult(EMPTY).firstBinaryOperator(SUBTRACTION).firstBinaryResult("1|1 "+SUBTRACTION).secondUnaryOperator(DELETE).secondUnaryResult("1")),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber(ENTER_A_NUMBER).firstUnaryResult(DELETE).firstUnaryResult(EMPTY)),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber(EMPTY).firstUnaryOperator(DELETE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER))
        );
    }

    @Test
    void enteredANumberThenAddThen6DeleteThen5()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE).thenReturn(FIVE);
        calculator.getTextPane().setText(SIX);
        calculator.getValues()[0] = "15.6";
        calculator.getValues()[1] = SIX;
        calculator.setValuesPosition(1);
        calculator.getButtonDecimal().setEnabled(true);
        calculator.getValues()[2] = ADDITION;
        assertEquals(6, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not 6");

        calculator.performDeleteButtonAction(actionEvent);
        //assertEquals(15.6, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 15.6");
        assertEquals(EMPTY, calculator.getTextPaneValue(), "textPane is not blank");
        assertTrue(calculator.isDecimalPressed(), "Expected decimal button is enabled");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane does not equal blank");
        assertTrue(calculator.isDecimalPressed(), "Expected decimal button is enabled");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");
    }

    /* Invalid DELETE */

    /* Valid ADD */
    @Test
    @DisplayName("+")
    void pressedAddWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.performAddButtonAction(actionEvent);

        assertEquals(EMPTY, calculator.getValues()[calculator.getValuesPosition()], "Values[{}] is not empty");
        assertEquals(ENTER_A_NUMBER, getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should display 'Enter a Number'");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    void pressed1ThenAddThenAddAgain()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAddButtonAction(actionEvent);
        calculator.performAddButtonAction(actionEvent); // no thenReturn necessary. Uses last value sent to method

        assertEquals(ONE, calculator.getValues()[0], "Values[{}] is not 1");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");
        assertFalse(calculator.isObtainingFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    void pressedAddWithErrorInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(ERROR);
        calculator.performAddButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    @Test
    void pressedAddWithNumberTooBigInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(NUMBER_TOO_BIG);
        calculator.performAddButtonAction(actionEvent);
        assertEquals(NUMBER_TOO_BIG, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    /* Invalid ADD */

    /* Valid SUBTRACT */
    @Test
    void pressedSubtractWithErrorInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(ERROR);
        calculator.performSubtractButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    /* Invalid SUBTRACT */

    /* Valid MULTIPLY */
    @Test
    void pressedMultiplyWithErrorInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(ERROR);
        calculator.performMultiplyButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    /* Invalid MULTIPLY */

    /* Valid DIVIDE */
    @Test
    void pressedDivideWithErrorInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(ERROR);
        calculator.performDivideButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    /* Invalid DIVIDE */
    @Test
    @DisplayName("15.5 / 0 =")
    void testDivisionBy0()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = ONE+FIVE+DECIMAL+FIVE;
        calculator.getValues()[1] = ZERO;
        calculator.getValues()[2] = DIVISION;
        calculator.setCalculatorView(VIEW_BASIC);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(EMPTY, calculator.getValues()[0], "Expected values[0] to be empty");
        assertEquals(INFINITY, calculator.getTextPaneValue(), "Expected textPane to show INFINITY");
    }

    /* Valid NUMBERS */
    @Test
    @DisplayName("1")
    void pressed1()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]), "Values[{}] is not 1");
        assertEquals(ONE, getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should be 1");
        assertTrue(isPositiveNumber(getValueWithoutAnyOperator(calculator.getTextPaneValue())), "{} is not positive");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    @DisplayName("15")
    void pressed1Then5()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(FIVE);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]), "Values[{}] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(isPositiveNumber(String.valueOf(calculator.getValues()[calculator.getValuesPosition()])), "{} is not positive");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);

        assertEquals(15, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]), "Values[{}] is not 15");
        assertEquals("15", calculator.getTextPaneValue(), "textPane should be 15");
        assertTrue(isPositiveNumber(String.valueOf(calculator.getValues()[calculator.getValuesPosition()])), "{} is not positive");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    @DisplayName("1 +")
    void pressed1ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performAddButtonAction(actionEvent);
        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals("1 +", calculator.getTextPaneValue(), "textPane should be 1 +");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");
        assertFalse(calculator.isObtainingFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    @DisplayName("15 +")
    void pressed1Then5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(FIVE).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);

        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 15");
        assertEquals("15", calculator.getTextPaneValue(), "textPane should be 15");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performAddButtonAction(actionEvent);

        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals("15 +", calculator.getTextPaneValue(), "textPane should be 15 +");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");
        assertFalse(calculator.isObtainingFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    @DisplayName("1 -")
    void pressed1ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performSubtractButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals("1 -", calculator.getTextPaneValue(), "textPane should be 1 -");
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
        assertFalse(calculator.isObtainingFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    @DisplayName("15 -")
    void pressed1Then5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(FIVE).thenReturn(SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);

        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 15");
        assertEquals("15", calculator.getTextPaneValue(), "textPane should be 15");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performSubtractButtonAction(actionEvent);

        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals("15 -", calculator.getTextPaneValue(), "textPane should be 15 -");
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
        assertFalse(calculator.isObtainingFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    @DisplayName("15 - 5 =")
    void pressed1Then5ThenSubtractThen5ThenEquals()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE).thenReturn(FIVE)
                .thenReturn(SUBTRACTION)
                .thenReturn(FIVE)
                .thenReturn(EQUALS);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 15");
        assertEquals("15", calculator.getTextPaneValue(), "textPane should be 15");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performSubtractButtonAction(actionEvent);
        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 15");
        assertEquals("15 -", calculator.getTextPaneValue(), "textPane should be 15 -");
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
        assertSame(1, calculator.getValuesPosition(), "ValuesPosition should be 1");
        assertFalse(calculator.isObtainingFirstNumber(), "We are still on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane should be 5");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(EMPTY, calculator.getValueAt(0), "Values[0] is not empty");
        assertEquals(EMPTY, calculator.getValueAt(1), "Values[1] is not empty");
        assertEquals("10", calculator.getTextPaneValue(), "textPane should be 10");
        assertFalse(calculator.isObtainingFirstNumber(), "Expected firstNumber to be false");

        verify(actionEvent, times(5)).getActionCommand();
    }

    @Test
    @DisplayName("1 ✕")
    void pressed1ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performMultiplyButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals("1 ✕", calculator.getTextPaneValue(), "textPane should be 1 ✕");
        assertFalse(calculator.isObtainingFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    @DisplayName("15 ✕")
    void pressed1Then5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(FIVE).thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);

        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 15");
        assertEquals("15", calculator.getTextPaneValue(), "textPane should be 15");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performAddButtonAction(actionEvent);

        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals("15 ✕", calculator.getTextPaneValue(), "textPane should be 15 ✕");
        assertSame(1, calculator.getValuesPosition(), "ValuesPosition should be 1");
        assertFalse(calculator.isObtainingFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    @DisplayName("10 ✕ 10 =")
    void pressed1Then0ThenMultiplyThen1Then0ThenEquals()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE).thenReturn(ZERO).thenReturn(MULTIPLICATION)
                .thenReturn(ONE).thenReturn(ZERO).thenReturn(EQUALS);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(10, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 10");
        assertEquals("10", calculator.getTextPaneValue(), "textPane should be 10");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performMultiplyButtonAction(actionEvent);
        assertEquals(10, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 10");
        assertEquals("10 ✕", calculator.getTextPaneValue(), "textPane should be 10 ✕");
        assertEquals(MULTIPLICATION, calculator.getValueAt(2), "Expecting MULTIPLICATION at values[2]");
        assertSame(1, calculator.getValuesPosition(), "ValuesPosition should be 1");
        assertFalse(calculator.isObtainingFirstNumber(), "We are still on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(1, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(10, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not 10");
        assertEquals("10", calculator.getTextPaneValue(), "textPane should be 10");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("100", calculator.getTextPaneValue(), "textPane is not 100");
        assertEquals(EMPTY, calculator.getValues()[1], "Values[1] is not empty");
        assertEquals("100", calculator.getTextPaneValue(), "textPane should be 100");
        assertFalse(calculator.isObtainingFirstNumber(), "Expected firstNumber to be false");

        verify(actionEvent, times(6)).getActionCommand();
    }

    /* Invalid NUMBERS ?? */

    /* Valid NEGATE */
    @Test
    @DisplayName("1 ±")
    void pressed1ThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(NEGATE);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNegateButtonAction(actionEvent);

        assertEquals(-1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]), "Values[{}] is not -1");
        assertEquals("-1", calculator.getTextPaneValue(), "textPane should be -1");
        assertTrue(CalculatorUtility.isNegativeNumber(calculator.getValues()[calculator.getValuesPosition()]), "{} is not negative");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    @DisplayName("1 ± +")
    void pressed1ThenNegateThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(NEGATE).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNegateButtonAction(actionEvent);
        calculator.performAddButtonAction(actionEvent);

        assertEquals(-1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not -1");
        assertEquals("-1 +", calculator.getTextPaneValue(), "textPane should be -1 +");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at position 2");
        assertFalse(calculator.isObtainingFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    @DisplayName("15 ± + 5 ± =")
    void pressed1Then5ThenNegateThenAddThen5ThenNegateThenEquals()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE).thenReturn(FIVE)
                .thenReturn(NEGATE)
                .thenReturn(ADDITION)
                .thenReturn(FIVE)
                .thenReturn(NEGATE)
                .thenReturn(EQUALS);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertSame(0, calculator.getValuesPosition(), "ValuesPosition should be 0");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 15");
        assertEquals("15", calculator.getTextPaneValue(), "textPane should be 15");
        assertSame(0, calculator.getValuesPosition(), "ValuesPosition should be 0");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performNegateButtonAction(actionEvent);
        assertEquals(-15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not -15");
        assertEquals("-15", calculator.getTextPaneValue(), "textPane should be -15");
        assertSame(0, calculator.getValuesPosition(), "ValuesPosition should be 0");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performAddButtonAction(actionEvent);
        assertEquals(-15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not -15");
        assertEquals("-15 +", calculator.getTextPaneValue(), "textPane should be -15 +");
        assertSame(1, calculator.getValuesPosition(), "ValuesPosition should be 1");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at position 2");
        assertFalse(calculator.isObtainingFirstNumber(), "We are still on the firstNumber");
        assertFalse(calculator.isNegativeNumber(), "Expecting isNumberNegative to be false");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane should be 5");
        assertSame(1, calculator.getValuesPosition(), "ValuesPosition should be 1");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performNegateButtonAction(actionEvent);
        assertEquals(-5, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not -5");
        assertEquals("-5", calculator.getTextPaneValue(), "textPane should be -5");
        assertSame(1, calculator.getValuesPosition(), "ValuesPosition should be 1");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("-20", calculator.getTextPaneValue(), "textPane is not -20");
        assertEquals(EMPTY, calculator.getValues()[1], "Values[1] is not empty");
        assertEquals("-20", calculator.getTextPaneValue(), "textPane should be -20");
        assertFalse(calculator.isObtainingFirstNumber(), "Expected firstNumber to be false");
    }

    /* Invalid NEGATE */

    /* Valid DECIMAL */
    @Test
    @DisplayName(".")
    void pressedDecimal()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL);
        calculator.getValues()[0] = EMPTY;
        calculator.getTextPane().setText(EMPTY);
        calculator.performDecimalButtonAction(actionEvent);
        assertEquals("0.", calculator.getTextPaneValue(), "textPane is not as expected");
        assertFalse(calculator.getButtonDecimal().isEnabled(), "buttonDot should be disabled");
    }

    @Test
    @DisplayName("544.")
    void pressed544ThenDecimal()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL);
        calculator.getTextPane().setText("544");
        calculator.getValues()[0] = "544";
        calculator.performDecimalButtonAction(actionEvent);
        assertEquals("544.", calculator.getTextPaneValue(), "textPane is not as expected");
        assertEquals("544.", calculator.getValues()[0], "Values[0]");
        assertFalse(calculator.getButtonDecimal().isEnabled(), "buttonDot should be disabled");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    @DisplayName("1234.")
    void pressed1Then2Then3Then4ThenDecimal()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(TWO).thenReturn(THREE)
                .thenReturn("4").thenReturn(DECIMAL);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDecimalButtonAction(actionEvent);
        assertEquals("1,234.", calculator.getTextPaneValue(), "textPane is not as expected");
        assertEquals("1234.", calculator.getValues()[0], "Expected  v[0] to be 1234.");
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected decimal to be disabled");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
    }


    @Test
    @DisplayName("1.5 + 2")
    void pressed1ThenDecimalThen5ThenAddThen2()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(DECIMAL)
                .thenReturn(FIVE).thenReturn(ADDITION).thenReturn(TWO);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDecimalButtonAction(actionEvent);
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be disabled");
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAddButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be enabled");
    }

    /* Invalid DECIMAL */

    /* EQUALS */

    /* Invalid EQUALS (that's it) */
    @Test
    @DisplayName("6 + =")
    void testPressedEqualsPrematurelyWithAddition()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = SIX;
        calculator.getValues()[2] = ADDITION;
        calculator.setValuesPosition(1);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");
        assertEquals(SIX, calculator.getValues()[0], "Expected textPane to be 6");
        assertFalse(calculator.isNegativeNumber(), "Expected isNegative to now be false");
    }

    @Test
    @DisplayName("6 ✕ =")
    void testPressedEqualsPrematurelyWithMultiplication()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = SIX;
        calculator.getValues()[2] = MULTIPLICATION;
        calculator.setValuesPosition(1);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(MULTIPLICATION, calculator.getValueAt(2), "Expecting MULTIPLICATION at values[2]");
        assertEquals(SIX, calculator.getValues()[0], "Expected textPane to be 6");
        assertFalse(calculator.isNegativeNumber(), "Expected isNegative to now be false");
    }

    @Test
    @DisplayName("6 / =")
    void testPressedEqualsPrematurelyWithDivision()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = SIX;
        calculator.getValues()[2] = DIVISION;
        calculator.setValuesPosition(1);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(DIVISION, calculator.getValueAt(2), "Expecting DIVISION at values[2]");
        assertEquals(SIX, calculator.getValues()[0], "Expected textPane to be 6");
        assertFalse(calculator.isNegativeNumber(), "Expected isNegative to now be false");
    }

    @Test
    void testPressedEqualsPrematurelyWithSubtraction()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = "-6";
        calculator.getValues()[2] = SUBTRACTION;
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
        assertEquals("-6", calculator.getValues()[0], "Expected textPane to be -6");
        assertTrue(calculator.isNegativeNumber(), "Expected isNegative to be false");
    }


    /*############## Test Button Actions ##################*/




    @Test
    void pressed1ThenAddThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(ADDITION)
                .thenReturn(FIVE).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAddButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAddButtonAction(actionEvent);
        assertEquals("6 +", calculator.getTextPaneValue(), "Expected textPane shows 6 +");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");
    }

    @Test
    void pressedSubtractThen5()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION).thenReturn(FIVE);
        calculator.performSubtractButtonAction(actionEvent);
        assertEquals(SUBTRACTION, calculator.getTextPaneValue(), "Expected textPane to show - symbol");
        assertEquals(EMPTY, calculator.getValues()[0], "Expected values[0] to be blank");
        assertTrue(calculator.isNegativeNumber(), "Expected isNumberNegative to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("-5", calculator.getTextPaneValue(), "Expected textPane to show -5");
        assertEquals("-5", calculator.getValues()[0], "Expected values[0] to be -5");
        assertTrue( calculator.isNegativeNumber(), "Expected isNumberNegative to be true");
        assertTrue(calculator.isNegativeNumber(), "Expected isNegative to be true");
    }

    @Test
    void pressed5ThenAddThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE).thenReturn(ADDITION).thenReturn(SUBTRACTION);
        calculator.setNegativeNumber(false);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAddButtonAction(actionEvent);
        calculator.performSubtractButtonAction(actionEvent);
        assertEquals(SUBTRACTION, calculator.getTextPaneValue(), "Expected textPane to -");
        assertEquals(FIVE, calculator.getValues()[0], "Expected values[0] to be 5");
        assertTrue(calculator.isNegativeNumber(), "Expected isNumberNegative is true");
    }

    @Test
    void pressed1ThenSubtractThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performSubtractButtonAction(actionEvent);
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performAddButtonAction(actionEvent);
        assertEquals("-4 +", calculator.getTextPaneValue(), "Expected textPane shows -4 +");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");
    }

    @Test
    void pressed1ThenSubtractThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performSubtractButtonAction(actionEvent);
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performSubtractButtonAction(actionEvent);
        assertEquals("-4 -", calculator.getTextPaneValue(), "Expected textPane shows -4 -");
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
    }

    @Test
    void testSubtractWithDecimalNumbers()
    {
        // 4.5 - -2.3 = 6.8
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = "4.5";
        calculator.getValues()[1] = "-2.3";
        calculator.getValues()[2] = SUBTRACTION;
        calculator.setNegativeNumber(true);
        calculator.setValuesPosition(1);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("6.8", calculator.getTextPaneValue(), "Expected textPane shows 6.8");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");
        assertFalse(calculator.isNegativeNumber(), "Expected isNumberNegative to be false");
        assertTrue(calculator.isDecimalPressed(), "Expected decimal to be disabled");
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

    @Test
    void testPressedSubtractAfterEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(EQUALS).thenReturn(SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        calculator.performSubtractButtonAction(actionEvent);
        assertEquals("-4 -", calculator.getTextPaneValue(), "Expected textPane to be -4 -");
        assertEquals("-4", calculator.getValues()[0], "Expected values[0] to be -4");
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
    }

    @Test
    void testPressedMultiplyAfterEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(EQUALS).thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        calculator.performMultiplyButtonAction(actionEvent);
        assertEquals("-4 ✕", calculator.getTextPaneValue(), "Expected textPane to be -4 ✕");
        assertEquals("-4", calculator.getValues()[0], "Expected values[0] to be -4");
        assertEquals(MULTIPLICATION, calculator.getValueAt(2), "Expecting MULTIPLICATION at values[2]");
    }

    @Test
    void testPressedDivideAfterEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(EQUALS).thenReturn(DIVISION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("-4 ÷", calculator.getTextPaneValue(), "Expected textPane to be -4 ÷");
        assertEquals("-4", calculator.getValues()[0], "Expected values[0] to be -4");
        assertEquals(DIVISION, calculator.getValueAt(2), "Expecting DIVISION at values[2]");
    }

    @Test
    void testContinuedSubtractionWithNegatedNumber()
    {
        // 2 - -5 -  --> 7 -
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION);
        calculator.getValues()[0] = "2";
        calculator.getValues()[2] = SUBTRACTION;
        calculator.setNegativeNumber(true);
        calculator.getValues()[1] = "-5";
        calculator.performSubtractButtonAction(actionEvent);
        assertEquals("7 -", calculator.getTextPaneValue(), "Expected textPane to be 7 -");
        assertEquals("7", calculator.getValues()[0], "Expected values[0] to be 7");
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
        assertFalse(calculator.isNegativeNumber(), "Expected isNumberNegative to be false");
        assertTrue(calculator.isDecimalPressed(), "Expected decimal to be enabled");
    }

    @Test
    void testContinuedSubtractionWithDecimals()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION);
        calculator.getValues()[0] = "2.3";
        calculator.getValues()[2] = SUBTRACTION;
        calculator.setNegativeNumber(true);
        calculator.getValues()[1] = "-2.1";
        calculator.performSubtractButtonAction(actionEvent);
        assertEquals("4.4 -", calculator.getTextPaneValue(), "Expected textPane to be 4.4 -");
        assertEquals("4.4", calculator.getValues()[0], "Expected values[0] to be 4.4");
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
        assertFalse(calculator.isNegativeNumber(), "Expected isNumberNegative to be false");
        assertTrue(calculator.isDecimalPressed(), "Expected decimal to be enabled");
    }

    @Test
    void testContinuedSubtractionWithDecimalsAndNegatedNumber()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(TWO).thenReturn(DECIMAL).thenReturn(THREE)
                .thenReturn(SUBTRACTION)
                .thenReturn(TWO).thenReturn(DECIMAL).thenReturn(ONE)
                .thenReturn(SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDecimalButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);

        calculator.performSubtractButtonAction(actionEvent);

        calculator.performNumberButtonAction(actionEvent);
        calculator.performDecimalButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);

        calculator.performSubtractButtonAction(actionEvent);
        assertEquals("0.2 -", calculator.getTextPaneValue(), "Expected textPane to be 0.2 -");
        assertEquals("0.2", calculator.getValues()[0], "Expected values[0] to be 0.2");
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
        assertFalse(calculator.isNegativeNumber(), "Expected isNumberNegative to be false");
        assertTrue(calculator.isDecimalPressed(), "Expected decimal to be enabled");
    }

    @Test
    void pressed1ThenSubtractThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performSubtractButtonAction(actionEvent);
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performMultiplyButtonAction(actionEvent);
        assertEquals("-4 ✕", calculator.getTextPaneValue(), "Expected textPane shows -4 ✕");
        assertEquals(MULTIPLICATION, calculator.getValueAt(2), "Expecting MULTIPLICATION at values[2]");
    }

    @Test
    @DisplayName("1 / 5 -")
    void pressed1ThenDivideThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE)
                .thenReturn(DIVISION)
                .thenReturn(FIVE)
                .thenReturn(SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");

        calculator.performDivideButtonAction(actionEvent);
        assertEquals(DIVISION, calculator.getValueAt(2), "Expecting DIVISION at values[2]");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");

        calculator.performSubtractButtonAction(actionEvent);
        assertEquals("0.2 -", calculator.getTextPaneValue(), "Expected textPane shows 0.2 -");
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
    }

    @Test
    void pressed1ThenSubtractThen5ThenDivide()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(DIVISION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performSubtractButtonAction(actionEvent);
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("-4 ÷", calculator.getTextPaneValue(), "Expected textPane shows -4 ÷");
        assertEquals(DIVISION, calculator.getValueAt(2), "Expecting DIVISION at values[2]");
    }

    @Test
    void pressed1ThenMultiplyThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(MULTIPLICATION)
                .thenReturn(FIVE).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performMultiplyButtonAction(actionEvent);
        assertEquals(MULTIPLICATION, calculator.getValueAt(2), "Expecting MULTIPLICATION at values[2]");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performAddButtonAction(actionEvent);
        assertEquals("5 +", calculator.getTextPaneValue(), "Expected textPane shows 5 +");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");
    }

    @Test
    void testMultiplyingTwoDecimals()
    {
        when(actionEvent.getActionCommand()).thenReturn(MULTIPLICATION);
        calculator.getValues()[0] = "4.5";
        calculator.getValues()[2] = MULTIPLICATION;
        calculator.getValues()[1] = "2.3";
        calculator.appendTextToPane(calculator.getValueAt(1));
        calculator.performMultiplyButtonAction(actionEvent);
        assertEquals("10.35 ✕", calculator.getTextPaneValue(), "Expected textPane shows 10.35 ✕");
        assertEquals(MULTIPLICATION, calculator.getValueAt(2), "Expecting MULTIPLICATION at values[2]");
        assertFalse(calculator.isNegativeNumber(), "Expected isNumberNegative to be false");
        assertTrue(calculator.isDecimalPressed(), "Expected decimal to be enabled");
    }

    @Test
    void pressed1ThenMultiplyThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(MULTIPLICATION)
                .thenReturn(FIVE).thenReturn(SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performMultiplyButtonAction(actionEvent);
        assertEquals(MULTIPLICATION, calculator.getValueAt(2), "Expecting MULTIPLICATION at values[2]");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performSubtractButtonAction(actionEvent);
        assertEquals("5 -", calculator.getTextPaneValue(), "Expected textPane shows 5 -");
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
    }

    @Test
    void pressed1ThenMultiplyThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(MULTIPLICATION)
                .thenReturn(FIVE).thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performMultiplyButtonAction(actionEvent);
        assertEquals(MULTIPLICATION, calculator.getValueAt(2), "Expecting MULTIPLICATION at values[2]");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performMultiplyButtonAction(actionEvent);
        assertEquals("5 ✕", calculator.getTextPaneValue(), "Expected textPane shows 5 ✕");
        assertEquals(MULTIPLICATION, calculator.getValueAt(2), "Expecting MULTIPLICATION at values[2]");
    }

    @Test
    void pressed1ThenMultiplyThen5ThenDivide()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE).thenReturn(MULTIPLICATION)
                .thenReturn(FIVE).thenReturn(DIVISION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performMultiplyButtonAction(actionEvent);
        assertEquals(MULTIPLICATION, calculator.getValueAt(2), "Expecting MULTIPLICATION at values[2]");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("5 ÷", calculator.getTextPaneValue(), "Expected textPane shows 5 ÷");
        assertEquals(DIVISION, calculator.getValueAt(2), "Expecting DIVISION at values[2]");
    }

    @Test
    @DisplayName("1 / 5 ✕")
    void pressed1ThenDivideThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE)
                .thenReturn(DIVISION)
                .thenReturn(FIVE)
                .thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValueAt(0), "Expected values[0] to be 1");

        calculator.performDivideButtonAction(actionEvent);
        assertEquals(DIVISION, calculator.getValueAt(2), "Expecting DIVISION at values[2]");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValueAt(1), "Expected values[1] to be 5");

        calculator.performMultiplyButtonAction(actionEvent);
        assertEquals("0.2 ✕", calculator.getTextPaneValue(), "Expected textPane shows 0.2 ✕");
        assertEquals(MULTIPLICATION, calculator.getValueAt(2), "Expecting MULTIPLICATION at values[2]");
    }

    @Test
    void pressed1ThenMultiplyThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(MULTIPLICATION).thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performMultiplyButtonAction(actionEvent);
        calculator.performMultiplyButtonAction(actionEvent);
        assertEquals(MULTIPLICATION, calculator.getValueAt(2), "Expecting MULTIPLICATION at values[2]");
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
    }

    @Test
    @DisplayName("1 / 5 +")
    void pressed1ThenDivideThen5ThenAdd()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE)
                .thenReturn(DIVISION)
                .thenReturn(FIVE)
                .thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performDivideButtonAction(actionEvent);
        assertEquals(DIVISION, calculator.getValueAt(2), "Expecting DIVISION at values[2]");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performAddButtonAction(actionEvent);
        assertEquals("0.2 +", calculator.getTextPaneValue(), "Expected textPane shows 0.2 +");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");
    }

    @Test
    void pressed1ThenDivideThen5ThenDivide()
    {
        // 1 ÷ 5 ÷
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(DIVISION)
                .thenReturn(FIVE).thenReturn(DIVISION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performDivideButtonAction(actionEvent);
        assertEquals(DIVISION, calculator.getValueAt(2), "Expecting DIVISION at values[2]");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("0.2 ÷", calculator.getTextPaneValue(), "Expected textPane shows 0.2 ÷");
        assertEquals(DIVISION, calculator.getValueAt(2), "Expecting DIVISION at values[2]");
    }

    @Test
    void pressed1ThenDivideThenDivide()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(DIVISION).thenReturn(DIVISION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDivideButtonAction(actionEvent);
        calculator.performDivideButtonAction(actionEvent);
        assertEquals(DIVISION, calculator.getValueAt(2), "Expecting DIVISION at values[2]");
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
    }
    
    @Test
    void pressed1ThenDivideThen0()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE)
                .thenReturn(DIVISION)
                .thenReturn(ZERO)
                .thenReturn(EQUALS);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDivideButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");
        assertEquals(EMPTY, calculator.getValues()[0], "Expected values[0] to be blank");
    }

    @Test
    void pressed1ThenAddThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(ADDITION)
                .thenReturn(FIVE).thenReturn(SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAddButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractButtonAction(actionEvent);
        calculator.setValuesPosition(1);
        assertEquals("6 -", calculator.getTextPaneValue(), "Expected textPane shows 6 -");
        assertEquals(SUBTRACTION, calculator.getValueAt(2), "Expecting SUBTRACTION at values[2]");
    }

    @Test
    void pressed1ThenAddThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(ADDITION)
                .thenReturn(FIVE).thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAddButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performMultiplyButtonAction(actionEvent);
        calculator.setValuesPosition(1);
        assertEquals("6 ✕", calculator.getTextPaneValue(), "Expected textPane shows 6 ✕");
        assertEquals(MULTIPLICATION, calculator.getValueAt(2), "Expecting MULTIPLICATION at values[2]");
    }

    @Test
    void pressed1ThenAddThen5ThenDivide()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE).thenReturn(ADDITION)
                .thenReturn(FIVE).thenReturn(DIVISION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAddButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDivideButtonAction(actionEvent);
        calculator.setValuesPosition(1);
        assertEquals("6 ÷", calculator.getTextPaneValue(), "Expected textPane shows 6 ÷");
        assertEquals(DIVISION, calculator.getValueAt(2), "Expecting DIVISION at values[2]");
    }

    @Test
    void testAddingTwoNumbersResultsInDecimalNumberThenAdding()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION).thenReturn(FIVE);
        calculator.getValues()[0] = "5.5";
        calculator.getValues()[1] = "2";
        calculator.getValues()[2] = ADDITION;
        calculator.appendTextToPane(calculator.getValueAt(1));
        calculator.performAddButtonAction(actionEvent);
        assertEquals("7.5 +", calculator.getTextPaneValue(), "Expected textPane shows 7.5 +");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");
    }

    // TODO: Double check
    @Test
    void pressedAddThenDivideWhenNumberIsMaximum()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION);
        calculator.getValues()[0] = "9999998"; // 9,999,998
        calculator.getValues()[1] = TWO;       // 2
        calculator.getValues()[2] = ADDITION;
        calculator.setValuesPosition(1);
        calculator.appendTextToPane(calculator.getValueAt(1));
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("10,000,000", calculator.getTextPaneValue(), "Expected textPane shows 10,000,000");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");
    }

    @Test
    void pressedSubtractWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION);
        calculator.performAddButtonAction(actionEvent);

        assertEquals(EMPTY, calculator.getValues()[calculator.getValuesPosition()], "Values[{}] is not empty");
        assertEquals(ENTER_A_NUMBER, getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should display 'Enter a Number'");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    void pressedMultiplyWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(MULTIPLICATION);
        calculator.performMultiplyButtonAction(actionEvent);

        assertEquals(EMPTY, calculator.getValues()[calculator.getValuesPosition()], "Values[{}] is not empty");
        assertEquals(ENTER_A_NUMBER, getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should display 'Enter a Number'");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    void pressedDivideWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION);
        calculator.performDivideButtonAction(actionEvent);

        assertEquals(EMPTY, calculator.getValues()[calculator.getValuesPosition()], "Values[{}] is not empty");
        assertEquals(ENTER_A_NUMBER, getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should display 'Enter a Number'");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    void testAdditionWithWholeNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[2] = ADDITION;
        calculator.getValues()[0] = FIVE;
        calculator.getValues()[1] = "10";
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("15", calculator.getTextPaneValue(), "Did not get back expected result");
    }

    @Test
    void testAdditionWithDecimalNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[2] = ADDITION;
        calculator.getValues()[0] = "5.5";
        calculator.getValues()[1] = "10";
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("15.5", calculator.getTextPaneValue(), "Did not get back expected result");
    }

    @Test
    void testSubtractionFunctionalityWithWholeNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = "10";
        calculator.getValues()[2] = SUBTRACTION;
        calculator.setValuesPosition(1);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getTextPaneValue(), "Did not get back expected result");
    }

    @Test
    void testSubtractionFunctionalityWithDecimalNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = "15.5";
        calculator.getValues()[1] = "10";
        calculator.getValues()[2] = SUBTRACTION;
        calculator.appendTextToPane(calculator.getValueAt(1));
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("5.5", calculator.getTextPaneValue(), "Did not get back expected result");
    }

    @Test
    void testMultiplicationFunctionalityWithWholeNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = "10";
        calculator.getValues()[2] = MULTIPLICATION;
        calculator.setValuesPosition(1);
        calculator.appendTextToPane(calculator.getValueAt(1));
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("150", calculator.getTextPaneValue(), "Did not get back expected result");
    }

    @Test
    @DisplayName("5.5 * 10.2 =")
    void testMultiplicationFunctionalityWithDecimalNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = "5.5";
        calculator.getValues()[1] = "10.2";
        calculator.getValues()[2] = MULTIPLICATION;
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("56.1", calculator.getTextPaneValue(), "Did not get back expected result");
    }

    @Test
    void testDivisionFunctionalityWithWholeNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = FIVE;
        calculator.getValues()[2] = DIVISION;
        calculator.setCalculatorView(VIEW_BASIC);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(THREE, calculator.getTextPaneValue(), "Did not get back expected result");
    }

    @Test
    void testDivisionFunctionalityWithDecimalNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = "15.5";
        calculator.getValues()[1] = FIVE;
        calculator.getValues()[2] = DIVISION;
        calculator.setCalculatorView(VIEW_BASIC);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("3.1", calculator.getTextPaneValue(), "Did not get back expected result");
    }





    @Test
    void pressedSubtractThen5ThenSubtractThenSubtractThen6ThenEquals()
    {
        // -5 - 6 =
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = "-5";
        calculator.getValues()[1] = "-6";
        calculator.getValues()[2] = SUBTRACTION;
        calculator.getTextPane().setText("-6");
        calculator.setNegativeNumber(true);
        calculator.setValuesPosition(1);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(EMPTY, calculator.getValues()[0], "Values[0] is not blank");
        assertEquals("1", calculator.getTextPaneValue(), "textPane does not equal 1");
        assertTrue(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be enabled");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");
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







    @Test
    void pressedSquaredButtonWithNoEntry()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARED);
        basicPanel.performSquaredButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to contain message");
        assertTrue(calculator.getValues()[0].isEmpty(), "Expected values[0] to be empty");
    }

    @Test
    void pressedSquaredButtonWithBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARED);
        calculator.getTextPane().setText(ENTER_A_NUMBER);
        basicPanel.performSquaredButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show bad text");
    }

    @Test
    void pressed5ThenSquaredButton()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE).thenReturn(SQUARED);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane should be 5");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        basicPanel.performSquaredButtonAction(actionEvent);
        assertEquals(25, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 25");
        assertEquals("25", calculator.getTextPaneValue(), "textPane should be 25");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    void pressed5ThenDecimalThen0ThenSquaredButton()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE).thenReturn(DECIMAL)
                .thenReturn(FIVE).thenReturn(SQUARED);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDecimalButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        //assertEquals(5.5, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 5.5"); // 0.0
        assertEquals("5.5", calculator.getTextPaneValue(), "textPane should be 5.5");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        basicPanel.performSquaredButtonAction(actionEvent);
        //assertEquals(30.25, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 30.25");
        assertEquals("30.25", calculator.getTextPaneValue(), "textPane should be 30.25");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
        assertFalse(calculator.isDecimalPressed(), "Expected decimal button to be disabled");
    }

    @Test
    void pressed5ThenNegateThenSquaredThenNegate()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(FIVE).thenReturn(NEGATE)
                .thenReturn(SQUARED).thenReturn(NEGATE);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane should be 5");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performNegateButtonAction(actionEvent);
        assertEquals(-5, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not -5");
        assertEquals("-5", calculator.getTextPaneValue(), "textPane should be -5");
        assertTrue(calculator.isNegativeNumber(), "Expected isNumberNegative to be true");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        basicPanel.performSquaredButtonAction(actionEvent);
        assertEquals(25, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 25");
        assertEquals("25", calculator.getTextPaneValue(), "textPane should be 25");
        assertFalse(calculator.isNegativeNumber(), "Expected isNumberNegative to be false");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performNegateButtonAction(actionEvent);
        assertEquals(-25, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not -25");
        assertEquals("-25", calculator.getTextPaneValue(), "textPane should be -25");
        assertTrue(calculator.isNegativeNumber(), "Expected isNumberNegative to be true");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

    }

    @Test
    void pressed5ThenNegateThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE).thenReturn(NEGATE).thenReturn(NEGATE);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane should be 5");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performNegateButtonAction(actionEvent);
        assertEquals(-5, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not -5");
        assertEquals("-5", calculator.getTextPaneValue(), "textPane should be -5");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
        assertTrue(calculator.isNegativeNumber(), "Expected number to be negative");

        calculator.performNegateButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane should be 5");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
        assertFalse(calculator.isNegativeNumber(), "Expected number to be positive");
    }

    @Test
    void pressedNegateOnTooBigNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(NEGATE);
        calculator.getTextPane().setText(ERROR);
        calculator.performNegateButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    @Test
    void pressedNegateWhenNoValueInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(NEGATE);
        calculator.getTextPane().setText(EMPTY);
        calculator.performNegateButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    @Test
    void pressedSquareRootWithTextPaneShowingE()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT);
        calculator.getTextPane().setText(ERROR);
        calculator.performSquareRootButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected textPane to show error");
    }

    @Test
    void pressedSquareRootWithTextPaneEmpty()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT);
        calculator.getTextPane().setText(EMPTY);
        calculator.performSquareRootButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show error message");
    }

    @Test
    void pressedSquareRootWhenCurrentValueIsNegative()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT);
        calculator.getTextPane().setText("-5");
        calculator.getValues()[0] = "-5;";
        calculator.setValuesPosition(0);
        calculator.performSquareRootButtonAction(actionEvent);
        assertEquals(ONLY_POSITIVES, calculator.getTextPaneValue(), "Expected textPane to show error message");
    }

    @Test
    void pressedSquareRootWithWholeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT);
        calculator.getTextPane().setText("25");
        calculator.getValues()[0] = "25";
        calculator.setValuesPosition(0);
        calculator.performSquareRootButtonAction(actionEvent);
        //assertEquals(5.0, Double.parseDouble(calculator.getTextPaneValue()), delta, "Expected result to be 5.0");
    }

    @Test
    void pressedSquareRootWithDecimalNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT);
        calculator.getValues()[0] = "25.5";
        calculator.setValuesPosition(0);
        calculator.appendTextToPane(calculator.getValueAt(0));
        calculator.performSquareRootButtonAction(actionEvent);
        //assertEquals(5.049752469181039, Double.parseDouble(calculator.getTextPaneValue()), delta, "Expected result to be 5.049752469181039");
        assertFalse(calculator.isDecimalPressed(), "Expected dot to be disable");
    }







    @Test
    void pressed5ThenMemorySub()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_SUBTRACTION);
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(FIVE);
        calculator.setValuesPosition(0);
        calculator.getValues()[0] = FIVE;
        calculator.performMemorySubtractionAction(actionEvent);
        assertEquals("10", calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 10");
        assertSame(1, calculator.getMemoryPosition(), "Expected memoryPosition to be 1");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals(FIVE, calculator.getTextPaneValue(), "Expected textPane to show 5");
    }

    @Test
    void testSavingAWholeNumberWithMemorySubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_SUBTRACTION);
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(EMPTY);
        calculator.setValuesPosition(0);
        calculator.performMemorySubtractionAction(actionEvent);
        assertEquals("15", calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 15");
        assertSame(1, calculator.getMemoryPosition(), "Expected memoryPosition to be 1");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show error");
    }

    @Test
    void testSavingADecimalNumberWithMemorySubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION);
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText("5.5");
        calculator.setValuesPosition(0);
        calculator.getValues()[0] = "5.5";
        calculator.performMemorySubtractionAction(actionEvent);
        assertEquals("4.5", calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 4.5");
        assertSame(1, calculator.getMemoryPosition(), "Expected memoryPosition to be 1");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals("5.5", calculator.getTextPaneValue(), "Expected textPane to show 5.5");
    }

    @Test
    void testMemorySubFailsWhenSavingBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION);
        calculator.getMemoryValues()[0] = EMPTY;
        calculator.setMemoryPosition(0);
        calculator.getTextPane().setText(ENTER_A_NUMBER);
        calculator.setValuesPosition(0);
        calculator.performMemorySubtractionAction(actionEvent);
        assertEquals(EMPTY, calculator.getMemoryValues()[0], "Expected memoryValues[0] to be blank");
        assertSame(0, calculator.getMemoryPosition(), "Expected memoryPosition to be 0");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show error");
    }






    @Test
    void pressed1Then0Then6Then5ThenDecimalThen5Then4Then5Then7()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(ZERO)
                .thenReturn(SIX).thenReturn(FIVE).thenReturn(DECIMAL).thenReturn(FIVE)
                .thenReturn(FOUR).thenReturn(FIVE).thenReturn(SEVEN);
        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.isDecimalPressed(), "Expected decimal to be enabled");
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "Expected textPane to be 1");

        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.isDecimalPressed(), "Expected decimal to be enabled");
        assertEquals("10", calculator.getValues()[0], "Expected values[0] to be 10");
        assertEquals("10", calculator.getTextPaneValue(), "Expected textPane to be 10");

        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.isDecimalPressed(), "Expected decimal to be enabled");
        assertEquals("106", calculator.getValues()[0], "Expected values[0] to be 106");
        assertEquals("106", calculator.getTextPaneValue(), "Expected textPane to be 106");

        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.isDecimalPressed(), "Expected decimal to be enabled");
        assertEquals("1065", calculator.getValues()[0], "Expected values[0] to be 1065");
        assertEquals("1,065", calculator.getTextPaneValue(), "Expected textPane to be 1,065");

        calculator.performDecimalButtonAction(actionEvent);
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be disabled");
        assertEquals("1065.", calculator.getValues()[0], "Expected values[0] to be 1065.");
        assertEquals("1,065.", calculator.getTextPaneValue(), "Expected textPane to be 1,065.");

        calculator.performNumberButtonAction(actionEvent);
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be disabled");
        assertEquals("1065.5", calculator.getValues()[0], "Expected values[0] to be 1065.5");
        assertEquals("1,065.5", calculator.getTextPaneValue(), "Expected textPane to be 1,065.5");

        calculator.performNumberButtonAction(actionEvent);
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be disabled");
        assertEquals("1065.54", calculator.getValues()[0], "Expected values[0] to be 1065.54");
        assertEquals("1,065.54", calculator.getTextPaneValue(), "Expected textPane to be 1,065.54");

        calculator.performNumberButtonAction(actionEvent);
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be disabled");
        assertEquals("1065.545", calculator.getValues()[0], "Expected values[0] to be 1065.545");
        assertEquals("1,065.545", calculator.getTextPaneValue(), "Expected textPane to be 1,065.545");

        calculator.performNumberButtonAction(actionEvent);
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be disabled");
        assertEquals("1065.5457", calculator.getValues()[0], "Expected values[0] to be 1065.5457");
        assertEquals("1,065.5457", calculator.getTextPaneValue(), "Expected textPane to be 1,065.5457");
    }

    @Test
    void pressed0WhenTextPaneHasBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(ZERO);
        calculator.getTextPane().setText(INFINITY);
        calculator.setObtainingFirstNumber(false);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ZERO, calculator.getValues()[0], "Expected values[0] to be 0");
        assertEquals(ZERO, calculator.getTextPaneValue(), "Expected textPane to show 0");
    }

    @Test
    void pressedDecimalWhenTextPaneHasBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL);
        calculator.getTextPane().setText(INFINITY);
        calculator.performDecimalButtonAction(actionEvent);
        assertEquals(EMPTY, calculator.getValues()[0], "Expected values[0] to be blank");
        assertEquals(INFINITY, calculator.getTextPaneValue(), "Expected textPane to show Infinity");
    }

    @Test
    void pressedANumberWhenMaxLengthHasBeenMet()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE);
        calculator.getTextPane().setText("4,500,424");
        calculator.getValues()[0] = "4500424";
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("45,004,245", calculator.getTextPaneValue(), "Expected textPane to be the same");
        assertEquals("45004245", calculator.getValues()[0], "Expected values[0] to be the same");
    }

    @Test
    void pressed1ThenAddThen2ThenEqualsThen5()
    {
        // 1 + 2 = 3
        // Pressing 5 Starts new Equation, new number
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(ADDITION)
                .thenReturn(TWO).thenReturn(EQUALS).thenReturn(FIVE);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "Expected textPane to be 1");

        calculator.performAddButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        assertEquals("1 +", calculator.getTextPaneValue(), "Expected textPane to be 1 +");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");
        assertEquals(1, calculator.getValuesPosition(), "Expected valuesPosition to be 1");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(TWO, calculator.getValues()[1], "Expected values[1] to be 2");
        assertEquals(TWO, calculator.getTextPaneValue(), "Expected textPane to be 2");

        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(EMPTY, calculator.getValues()[0], "Expected values[0] to be blank");
        assertEquals(EMPTY, calculator.getValues()[1], "Expected values[1] to be blank");
        assertEquals(THREE, calculator.getTextPaneValue(), "Expected textPane to be 3");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[0], "Expected values[0] to be 5");
        assertEquals(EMPTY, calculator.getValues()[1], "Expected values[1] to be blank");
        assertEquals(FIVE, calculator.getTextPaneValue(), "Expected textPane to be 5");

    }

    @Test
    void pressed4Then5ThenMSThenClearThen5ThenMAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(FOUR).thenReturn(FIVE)
                .thenReturn(MEMORY_STORE).thenReturn(CLEAR_ENTRY)
                .thenReturn(FIVE).thenReturn(MEMORY_ADDITION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performMemoryStoreAction(actionEvent);
        calculator.performClearEntryButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performMemoryAdditionAction(actionEvent);
        assertEquals(FIVE, calculator.getTextPaneValue(), "Expected textPane to show 5");
        assertEquals("50", calculator.getMemoryValues()[0], "Expected memories[0] to be 50");
    }

    @Test
    void pressed4Then5ThenMSThenClearThen5ThenMSub()
    {
        when(actionEvent.getActionCommand()).thenReturn(FOUR).thenReturn(FIVE)
                .thenReturn(MEMORY_STORE).thenReturn(CLEAR_ENTRY)
                .thenReturn(FIVE).thenReturn(MEMORY_SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performMemoryStoreAction(actionEvent);
        calculator.performClearEntryButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performMemorySubtractionAction(actionEvent);
        assertEquals(FIVE, calculator.getTextPaneValue(), "Expected textPane to show 5");
        assertEquals("40", calculator.getMemoryValues()[0], "Expected memories[0] to be 40");
    }

    @Test
    void pressingNumberEnablesDecimal()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE);
        calculator.getValues()[0] = "10.35";
        calculator.getButtonDecimal().setEnabled(false);
        calculator.getValues()[2] = ADDITION;
        calculator.setObtainingFirstNumber(false);
        calculator.performNumberButtonAction(actionEvent);
        assertFalse(calculator.isDecimalPressed(), "Expected decimal to be enabled");
    }

    @Test
    void testContinuedDivisionAfterAttemptToDivideByZero()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION);
        calculator.getValues()[0] = "1";
        calculator.getValues()[2] = DIVISION;
        calculator.getValues()[1] = "0";
        calculator.getTextPane().setText(calculator.getValues()[1]);
        calculator.performDivideButtonAction(actionEvent);
        assertEquals(INFINITY, calculator.getTextPaneValue(), "Expected textPane to show error");
        assertEquals(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");
        assertTrue(calculator.isDecimalPressed(), "Expected decimal to be enabled");
    }

    @Test
    void testContinuedDivisionWithWholeNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION);
        calculator.getValues()[0] = "10";
        calculator.getValues()[2] = DIVISION;
        calculator.getValues()[1] = "2";
        calculator.setValuesPosition(1);
        calculator.appendTextToPane(calculator.getValueAt(1));
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("5 ÷", calculator.getTextPaneValue(), "Expected textPane to show 5 ÷");
        assertEquals("5", calculator.getValues()[0], "Expected values[0] to be 5");
        assertEquals(DIVISION, calculator.getValueAt(2), "Expecting DIVISION at values[2]");
        assertTrue(calculator.isDecimalPressed(), "Expected decimal to be enabled");
    }





}

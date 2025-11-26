package Panels;

import Calculators.Calculator;
import Parent.TestParent;
import Types.SystemDetector;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

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
@ExtendWith(MockitoExtension.class)
class BasicPanelTest extends TestParent
{
    static { System.setProperty("appName", BasicPanelTest.class.getSimpleName()); }
    private static final Logger LOGGER = LogManager.getLogger(BasicPanelTest.class.getSimpleName());

    BasicPanel basicPanel;
    private final double delta = 0.000001d;

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

    /*############## Test Memory Button Actions ##################*/
    @ParameterizedTest
    @DisplayName("Test MemoryStoreButtonAction()")
    @MethodSource("memoryStoreButtonActionProvider")
    void testMemoryStoreButtonAction(ArgumentsAccessor arguments)
    {
        String firstNumber = calculator.removeThousandsDelimiter(arguments.getString(0));
        int firstNumLength = firstNumber != null ? firstNumber.length() : 0;
        String operatorBeforeMS = arguments.getString(1);
        String expectedResult = calculator.removeThousandsDelimiter(arguments.getString(2));
        AtomicInteger idx = new AtomicInteger(0);
        when(actionEvent.getActionCommand()).thenAnswer(invocation -> {
            int i = idx.getAndIncrement();
            if (i < firstNumLength) return String.valueOf(firstNumber.charAt(i));
            else if (i == firstNumLength) return operatorBeforeMS;
            else return MEMORY_STORE;
        });
        for (int i=0; i<firstNumLength; i++)
        {
            calculator.performNumberButtonAction(actionEvent);
            if ((i+1) == firstNumLength)
                assertEquals(calculator.addThousandsDelimiter(firstNumber), calculator.getTextPaneValue(), "textPane value is not as expected");
            else
                assertEquals(firstNumber.substring(0,(i+1)), calculator.getTextPaneValue(), "textPane value is not as expected");
        }
        if (operatorBeforeMS != null && !operatorBeforeMS.isEmpty())
        {
            performNextOperatorAction(operatorBeforeMS);
            assertEquals(calculator.addThousandsDelimiter(firstNumber) + SPACE + operatorBeforeMS,
                    calculator.getTextPaneValue(),
                    "Expecting textPane to show: " + firstNumber + SPACE + operatorBeforeMS);
        }
        String previousHistory = calculator.getHistoryTextPane().getText();
        calculator.performMemoryStoreAction(actionEvent);

        String actualMemoryAtPosition = calculator.getMemoryValues()[calculator.getLowestMemoryPosition()];
        assertEquals(expectedResult, actualMemoryAtPosition, "Memory value is not as expected");
        if (operatorBeforeMS != null && operatorBeforeMS.isEmpty())
        {
            assertEquals(previousHistory+NEWLINE+"(MS) "+savedMemory(actualMemoryAtPosition,calculator.getLowestMemoryPosition()),
                    calculator.getHistoryTextPane().getText(),
                    "History textPane should show memory");
        }

    }
    private static Stream<Arguments> memoryStoreButtonActionProvider()
    {
        /* Input, Operator, MemoryStore, Result of MemoryStore
        String firstNumber, (null or 0 or more digits, thousands delimiter will be removed)
        String possibleOperator, (any Basic operator or null)
        (MS button pressed, not pass in as it is understood)
        String resultStoredInMemory, (expected result in memories[0] after MS pressed)
         */
        return Stream.of(
                Arguments.of("1,234", null, "1,234"),
                Arguments.of("10", ADDITION, EMPTY),
                Arguments.of(null, null, EMPTY),
                Arguments.of(INFINITY, null, EMPTY)
        );
    }

    @Test
    @DisplayName("Test MemoryStore overwrites memory when memory is full")
    void testMemoryStoreOverwritesMemoryWhenMemoryIsFull()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_STORE);
        calculator.getTextPane().setText(TWO);
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(10);
        assertEquals("15", calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 15");
        calculator.performMemoryStoreAction(actionEvent);
        assertEquals(TWO, calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 2");
    }

    @Test
    void pressedMemoryRecall()
    {
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

    @Test
    void pressedMemoryClear()
    {
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

    @ParameterizedTest
    @DisplayName("Test MemoryAdditionButtonAction()")
    @MethodSource("memoryAddButtonCases")
    void testMemoryAdditionButtonAction(ArgumentsAccessor arguments)
    {
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
    }
    private static Stream<Arguments> memoryAddButtonCases()
    {
        /* Initial Memory,
        Number to Add, (optional operator at end)
        Resulting Memory
         */
        return Stream.of(
                Arguments.of("10", "5", "15"),
                Arguments.of("0", "0", "0"),
                Arguments.of("-5", "10", "5"),
                Arguments.of("100.5", "99.5", "200"),
                Arguments.of("10", ENTER_A_NUMBER, "10"),
                Arguments.of("10", EMPTY, "10"),
                Arguments.of("10", "5 +", "10")
        );
    }

    @ParameterizedTest
    @DisplayName("Test MemorySubtractionButtonAction()")
    @MethodSource("memorySubtractButtonCases")
    void testMemorySubtractionButtonAction(ArgumentsAccessor arguments)
    {
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
        /* Initial Memory,
        Number to Subtract, (optional operator at end)
        Resulting Memory
         */
        return Stream.of(
                Arguments.of("10", "5", "5"),
                Arguments.of("0", "0", "0"),
                Arguments.of("-5", "10", "-15"),
                Arguments.of("100.5", "99.5", "1"),
                Arguments.of("10", ENTER_A_NUMBER, "10"),
                Arguments.of("10", EMPTY, "10"),
                Arguments.of("10", "5 +", "10")
        );
    }

    /*############## Test Unary Operation Button Actions ##################*/
    @ParameterizedTest
    @DisplayName("Test Percent Button Action")
    @MethodSource("getPercentButtonCases")
    void pressedPercentWhenTextPaneContainsBadText(ArgumentsAccessor arguments)
    {
        postConstructCalculator();
        when(actionEvent.getActionCommand())
                .thenReturn(arguments.getString(0)) // firstNumber
                .thenReturn(PERCENT)
                //
                .thenReturn(arguments.size() > 2 ? arguments.getString(2) : EMPTY) // first Op
                .thenReturn(arguments.size() > 2 ? arguments.getString(3) : EMPTY) // secondNumber
                .thenReturn(PERCENT)
                //
                .thenReturn(arguments.size() > 4 ? arguments.getString(5) : EMPTY);

        calculator.performNumberButtonAction(actionEvent);
        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals(arguments.getString(1), calculator.removeThousandsDelimiter(calculator.getTextPaneValue()), "TextPane value not as expected");
        if (!calculator.textPaneContainsBadText()) assertEquals(arguments.getString(1), calculator.getValueAt(0), "values[0] returned unexpected result");
        assertEquals(!calculator.isDotPressed(), arguments.getString(1).contains(DECIMAL), "Decimal state not as expected");

        if (arguments.size() > 2) {
            performNextOperatorAction(arguments.getString(2)); // firstOperator
            calculator.performNumberButtonAction(actionEvent);
            basicPanel.performPercentButtonAction(actionEvent);
            assertEquals(arguments.getString(4), calculator.removeThousandsDelimiter(calculator.getTextPaneValue()), "Second TextPane value not as expected");
            assertEquals(arguments.getString(4), calculator.getValueAt(1), "values[1] returned unexpected result");

            if (arguments.size() > 4) {
                performNextOperatorAction(arguments.getString(5)); // secondOperator
                assertEquals(arguments.getString(5), calculator.getValueAt(2), "values[2] not equal to " + arguments.getString(5));
            }
        }
    }
    private static Stream<Arguments> getPercentButtonCases()
    {
        /*
        String firstNumber, (PERCENT button pressed) String firstResult,
        String firstOperator, String secondNumber, (PERCENT button pressed) String secondResult,
        String secondOperator
         */
        return Stream.of(
                Arguments.of(EMPTY, ENTER_A_NUMBER),
                Arguments.of("0", "0"),
                Arguments.of("5", "0.05"),
                Arguments.of("-5", "-0.05"),
                Arguments.of("0.25", "0.0025"),

                Arguments.of("4", "0.04", ADDITION, "2", "0.02", SUBTRACTION)
        );
    }

    @Test
    void pressedPercentWhenTextPaneIsBlank()
    {
        calculator.getTextPane().setText(EMPTY);
        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show bad text");
    }

    @Test
    void pressedPercentWithPositiveNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE).thenReturn(PERCENT);
        calculator.performNumberButtonAction(actionEvent);
        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals("0.05", calculator.getTextPaneValue(), "Expected textPane to be 0.05");
        assertEquals("0.05", calculator.getValues()[0], "Expected values[0] to be 0.05");
        assertFalse(calculator.isDotPressed(), "Expected decimal to be disabled");
    }

    @Test
    @DisplayName("-5 %")
    void pressedPercentWithNegativeNumber()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(SUBTRACTION)
                .thenReturn(FIVE)
                .thenReturn(PERCENT);
        calculator.performSubtractButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals("-0.05", calculator.getTextPaneValue(), "Expected textPane to be -0.05");
        assertEquals("-0.05", calculator.getValueAt(0), "Expected values[0] to be -0.05");
        assertFalse(calculator.isDotPressed(), "Expected decimal to be disabled");
    }

    @ParameterizedTest()
    @MethodSource("getValidFractionCases")
    @DisplayName("Test Valid Fraction Button")
    void testValidFractionButtonCases(ArgumentsAccessor arguments)
    {
        when(actionEvent.getActionCommand())
                .thenReturn(arguments.getString(0)) // firstNumber
                .thenReturn(FRACTION)
                //
                .thenReturn(arguments.size() > 2 ? arguments.getString(2) : EMPTY) // first Op
                .thenReturn(arguments.size() > 2 ? arguments.getString(3) : EMPTY) // secondNumber
                .thenReturn(FRACTION)
                //
                .thenReturn(arguments.size() > 4 ? arguments.getString(5) : EMPTY);

        calculator.performNumberButtonAction(actionEvent);
        basicPanel.performFractionButtonAction(actionEvent);
        assertEquals(arguments.getString(1), calculator.removeThousandsDelimiter(calculator.getTextPaneValue()), "TextPane value not as expected");
        assertEquals(arguments.getString(1), calculator.getValueAt(0), "values[0] returned unexpected result");
        assertEquals(!calculator.isDotPressed(), arguments.getString(1).contains(DECIMAL), "Decimal state not as expected");

        if (arguments.size() > 2) {
            performNextOperatorAction(arguments.getString(2)); // firstOperator
            calculator.performNumberButtonAction(actionEvent);
            basicPanel.performFractionButtonAction(actionEvent);
            assertEquals(arguments.getString(4), calculator.removeThousandsDelimiter(calculator.getTextPaneValue()), "TextPane value not as expected");
            assertEquals(arguments.getString(4), calculator.getValueAt(1), "values[1] returned unexpected result");

            if (arguments.size() > 4) {
                performNextOperatorAction(arguments.getString(5)); // secondOperator
                assertEquals(arguments.getString(5), calculator.getValueAt(2), "values[2] not equal to " + arguments.getString(5));
            }
        }
    }
    private static Stream<Arguments> getValidFractionCases()
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

    @ParameterizedTest()
    @MethodSource("getInvalidFractionCases")
    @DisplayName("Test Invalid Fraction Button")
    void testInvalidFractionButtonCases(ArgumentsAccessor arguments)
    {
        String firstArg = arguments.getString(0) == null
                ? null : arguments.getString(0);
        when(actionEvent.getActionCommand())
                .thenReturn(firstArg) // any button
                .thenReturn(FRACTION);
        calculator.getMemoryValues()[0] = "5"; // set for memory button scenarios
        if (calculator.getBadText().equals(firstArg))
        {
            calculator.appendTextToPane(firstArg);
        }

        if (firstArg != null) performNextOperatorAction(firstArg); // any button or no button
        basicPanel.performFractionButtonAction(actionEvent); // press fraction

        assertEquals(arguments.getString(1), calculator.removeThousandsDelimiter(calculator.getTextPaneValue()), "TextPane value not as expected");
        assertEquals(arguments.getString(2), calculator.getValueAt(0), "values[0] returned unexpected result");
    }
    private static Stream<Arguments> getInvalidFractionCases()
    {
        /*
        buttonChoice, result, values[0]
         */
        return Stream.of(
                Arguments.of(null, ENTER_A_NUMBER, EMPTY),
                Arguments.of(ENTER_A_NUMBER, ENTER_A_NUMBER, EMPTY),
                Arguments.of(MEMORY_STORE, ENTER_A_NUMBER, EMPTY),
                Arguments.of(MEMORY_RECALL, "0.2", "0.2"),
                Arguments.of(MEMORY_CLEAR, "0", "0"), // look into
                Arguments.of(PERCENT, ENTER_A_NUMBER, EMPTY),
                Arguments.of(SQUARE_ROOT, ENTER_A_NUMBER, EMPTY),
                Arguments.of(SQUARED, ENTER_A_NUMBER, EMPTY),
                Arguments.of(FRACTION, ENTER_A_NUMBER, EMPTY),
                Arguments.of(CLEAR_ENTRY, ENTER_A_NUMBER, EMPTY),
                Arguments.of(CLEAR, INFINITY, INFINITY)
        );
    }


    /*############## Test Binary Button Actions ##################*/
    /*############## Test Button Actions ##################*/

    @Test
    @DisplayName("1")
    void pressed1()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]), "Values[{}] is not 1");
        assertEquals(ONE, calculator.getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should be 1");
        assertTrue(calculator.isPositiveNumber(calculator.getValueWithoutAnyOperator(calculator.getTextPaneValue())), "{} is not positive");
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
        assertTrue(calculator.isPositiveNumber(String.valueOf(calculator.getValues()[calculator.getValuesPosition()])), "{} is not positive");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);

        assertEquals(15, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]), "Values[{}] is not 15");
        assertEquals("15", calculator.getTextPaneValue(), "textPane should be 15");
        assertTrue(calculator.isPositiveNumber(String.valueOf(calculator.getValues()[calculator.getValuesPosition()])), "{} is not positive");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    @DisplayName("1 ±")
    void pressed1ThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(NEGATE);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNegateButtonAction(actionEvent);

        assertEquals(-1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]), "Values[{}] is not -1");
        assertEquals("-1", calculator.getTextPaneValue(), "textPane should be -1");
        assertTrue(calculator.isNegativeNumber(calculator.getValues()[calculator.getValuesPosition()]), "{} is not negative");
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
    @DisplayName(".9")
    void pressedDecimalThen9()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL).thenReturn(NINE);
        calculator.performDecimalButtonAction(actionEvent);

        assertEquals("0.", calculator.getValues()[0], "Values[0] is not 0.");
        assertEquals("0.", calculator.getTextPaneValue(), "textPane should be 0.");
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected dot to be disabled");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);
        if (calculator.isNegativeNumber(calculator.getValues()[0])) fail("Number is negative");

        assertEquals( 0.9f, Double.parseDouble(calculator.getValues()[0]), delta); // 0.8999999761581421
        assertEquals("0.9", calculator.getTextPaneValue(), "textPane should be 0.9");
        assertFalse(calculator.isDotPressed(), "Expecting decimal to be disabled");
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

    @Test
    @DisplayName("+")
    void pressedAddWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.performAddButtonAction(actionEvent);

        assertEquals(EMPTY, calculator.getValues()[calculator.getValuesPosition()], "Values[{}] is not empty");
        assertEquals(ENTER_A_NUMBER, calculator.getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should display 'Enter a Number'");
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
    void pressedSubtractWithErrorInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(ERROR);
        calculator.performSubtractButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    @Test
    void pressedMultiplyWithErrorInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(ERROR);
        calculator.performMultiplyButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    @Test
    void pressedDivideWithErrorInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(ERROR);
        calculator.performDivideButtonAction(actionEvent);
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
        assertTrue(calculator.isDotPressed(), "Expected decimal to be disabled");
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
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
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
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
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
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
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
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
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
        assertEquals(ENTER_A_NUMBER, calculator.getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should display 'Enter a Number'");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    void pressedMultiplyWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(MULTIPLICATION);
        calculator.performMultiplyButtonAction(actionEvent);

        assertEquals(EMPTY, calculator.getValues()[calculator.getValuesPosition()], "Values[{}] is not empty");
        assertEquals(ENTER_A_NUMBER, calculator.getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should display 'Enter a Number'");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    void pressedDivideWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION);
        calculator.performDivideButtonAction(actionEvent);

        assertEquals(EMPTY, calculator.getValues()[calculator.getValuesPosition()], "Values[{}] is not empty");
        assertEquals(ENTER_A_NUMBER, calculator.getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should display 'Enter a Number'");
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

    @Test
    @DisplayName("Pressed ⌫")
    void pressedDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE);
        calculator.getTextPane().setText("35");
        calculator.getValues()[0] = "35";
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(3, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 3");
        assertEquals(THREE, calculator.getTextPaneValue(), "textPane does not equal 3");
    }

    @Test
    @DisplayName("Pressed ⌫ bad text in textPane")
    void pressedDeleteWhenTextPaneHasBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE);
        calculator.getTextPane().setText(ENTER_A_NUMBER);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to say " + ENTER_A_NUMBER);
        assertEquals(EMPTY, calculator.getValues()[0], "Expected values[0] to be blank");
        assertEquals(EMPTY, calculator.getValues()[1], "Expected values[1] to be blank");
        assertEquals(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");
        assertFalse(calculator.isNegativeNumber(), "Expected isNegative to be false");
        assertTrue(calculator.isDotPressed(), "Expected dot to be enabled");
    }

    @Test
    void testDeleteDoesNothingWhenNothingToDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE);
        calculator.getTextPane().setText(EMPTY);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show error");
        assertEquals(EMPTY, calculator.getValues()[0], "Expected values[0] to be blank");
        assertEquals(EMPTY, calculator.getValues()[1], "Expected values[1] to be blank");
        assertEquals(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");
        assertFalse(calculator.isNegativeNumber(), "Expected isNegative to be false");
        assertTrue(calculator.isDotPressed(), "Expected dot to be enabled");
    }

    @Test
    void testDeleteClearsTextPaneAfterPressingEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE).thenReturn(ADDITION)
                .thenReturn(THREE).thenReturn(EQUALS).thenReturn(DELETE);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAddButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(EMPTY, calculator.getTextPaneValue(), "Expected textPane to be blank");
        assertEquals(EMPTY, calculator.getValues()[0], "Expected values[0] to be blank");
        assertEquals(EMPTY, calculator.getValues()[1], "Expected values[1] to be blank");
        assertEquals(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");
        assertFalse(calculator.isNegativeNumber(), "Expected isNegative to be false");
        assertTrue(calculator.isDotPressed(), "Expected dot to be enabled");
    }

    @Test
    void pressed1Then5ThenDecimalThen6ThenAddThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE).thenReturn(DELETE).thenReturn(DELETE);
        calculator.getValues()[0] = "15.6";
        calculator.getValues()[1] = EMPTY;
        calculator.getValues()[2] = ADDITION;
        calculator.appendTextToPane(calculator.getValueAt(0) + SPACE + ADDITION);
        //calculator.setDotPressed(true);
        calculator.getButtonDecimal().setEnabled(false);
        calculator.getValues()[2] = ADDITION;
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(15.6, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 15.6");
        assertEquals("15.6", calculator.getTextPaneValue(), "textPane does not equal 15.6");
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be disabled");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");

        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(15., Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 15.");
        assertEquals("15.", calculator.getTextPaneValue(), "textPane does not equal 15.");
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be disabled");

        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(15, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 15");
        assertEquals("15", calculator.getTextPaneValue(), "textPane does not equal 15");
        assertTrue(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be enabled");
    }

    @Test
    void pressed1ThenSubtractThenDelete()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE)
                .thenReturn(SUBTRACTION)
                .thenReturn(DELETE);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractButtonAction(actionEvent);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(1, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane does not equal 1");
        assertTrue(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be enabled");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");
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
        assertEquals(-5, Double.parseDouble(calculator.getValues()[1]), delta, "Values[1] is not -5");
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(EMPTY, calculator.getValues()[0], "Values[0] is blank");
        assertEquals(ZERO, calculator.getTextPaneValue(), "textPane does not equal 0");
    }

    @Test
    @DisplayName("1 ✕ ⌫")
    void pressed1ThenMultiplyThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE);
        calculator.getTextPane().setText("1 ✕");
        calculator.getValues()[0] = ONE;
        calculator.getButtonDecimal().setEnabled(false);
        calculator.getValues()[2] = MULTIPLICATION;
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(1, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane does not equal 1");
        assertTrue(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be enabled");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");
    }

    @Test
    void pressed1ThenDivideThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE);
        calculator.getTextPane().setText("1 ÷");
        calculator.getValues()[0] = ONE;
        calculator.getButtonDecimal().setEnabled(false);
        calculator.getValues()[2] = DIVISION;
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(1, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane does not equal 1");
        assertTrue(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be enabled");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");
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
        assertEquals(15.6, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 15.6");
        assertEquals(EMPTY, calculator.getTextPaneValue(), "textPane is not blank");
        assertTrue(calculator.isDotPressed(), "Expected decimal button is enabled");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane does not equal blank");
        assertTrue(calculator.isDotPressed(), "Expected decimal button is enabled");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");
    }

    @Test
    void pressingClearRestoresCalculatorToStartFunctionality()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR);
        calculator.performClearButtonAction(actionEvent);
        assertEquals(ZERO, calculator.getValues()[0], "Values[0] should be 0");
        for ( int i=1; i<3; i++) {
            assertTrue(calculator.getValues()[i].isBlank(), "Values["+i+"] is not blank");
        }
        assertEquals(ZERO, calculator.getTextPaneValue(), "textPane is not 0");
        assertEquals(0, calculator.getValuesPosition(), "Values position is not 0");
        assertTrue(calculator.isObtainingFirstNumber(), "FirstNumBool is not true");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
    }

    @Test
    void pressingClearEntryWhenOnValuesPosition0ClearsTextPaneAndMainOperatorsAndDecimal()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR_ENTRY);
        calculator.setValuesPosition(0);
        calculator.getValues()[0] = "1088";
        calculator.appendTextToPane(calculator.getValueAt(0));
        calculator.performClearEntryButtonAction(actionEvent);

        assertTrue(StringUtils.isBlank(calculator.getTextPaneValue()), "textPane was not cleared");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");
        assertTrue(calculator.isDotPressed(), "Expected decimal button to be enabled");
        assertTrue(calculator.isObtainingFirstNumber(), "Expected to be on the firstNumber");
    }

    @Test
    @DisplayName("Pressed CE twice")
    void pressingClearEntry()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(CLEAR_ENTRY)
                .thenReturn(CLEAR_ENTRY);
        calculator.getValues()[0] = ONE+ZERO+ZERO;
        calculator.getValues()[1] = ONE+ZERO+EIGHT+EIGHT;
        calculator.getValues()[2] = ADDITION;
        calculator.setValuesPosition(1);
        calculator.appendTextToPane(calculator.getValueAt(1));

        calculator.performClearEntryButtonAction(actionEvent);
        assertEquals(ONE+ZERO+ZERO, calculator.getValues()[0], "Expected textPane to show values[0]");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting ADDITION at values[2]");
        assertTrue(calculator.isDotPressed(), "Expected decimal button to be enabled");
        assertFalse(calculator.isObtainingFirstNumber(), "Expected not to be on the firstNumber");
        assertEquals(1, calculator.getValuesPosition(), "Expected vP to be 1");

        calculator.performClearEntryButtonAction(actionEvent);
        assertEquals(ONE+ZERO+ZERO, calculator.getValueAt(0), "Expected textPane to show values[0]");
        assertEquals(ADDITION, calculator.getValueAt(2), "Expecting EMPTY at values[2]");
        assertTrue(calculator.isDotPressed(), "Expected decimal button to be enabled");
        assertFalse(calculator.isObtainingFirstNumber(), "Expected to be on the firstNumber");
        assertEquals(1, calculator.getValuesPosition(), "Expected vP to be 1");
    }

    @Test
    void pressingClearEntryAfterPressingAnOperatorResetsTextPaneAndOperator()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR_ENTRY);
        calculator.getTextPane().setText("1088 +");
        calculator.performClearEntryButtonAction(actionEvent);

        assertTrue(StringUtils.isBlank(calculator.getValueWithoutAnyOperator(calculator.getTextPaneValue())), "textPane was not cleared");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");
    }

    @Test
    void pressedClearEntryWhenTextPaneIsEmpty()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR_ENTRY);
        calculator.performClearEntryButtonAction(actionEvent);
        assertEquals(EMPTY, calculator.getValues()[0], "Expected textPane to be blank");
        assertEquals(EMPTY, calculator.getValueAt(2), "Expecting EMPTY at values[2]");
        assertTrue(calculator.isDotPressed(), "Expected decimal button to be enabled");
        assertTrue(calculator.isObtainingFirstNumber(), "Expected to be on the firstNumber");
        assertEquals(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
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
        assertEquals(5.5, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 5.5"); // 0.0
        assertEquals("5.5", calculator.getTextPaneValue(), "textPane should be 5.5");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");

        basicPanel.performSquaredButtonAction(actionEvent);
        assertEquals(30.25, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 30.25");
        assertEquals("30.25", calculator.getTextPaneValue(), "textPane should be 30.25");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
        assertFalse(calculator.isDotPressed(), "Expected decimal button to be disabled");
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
        assertEquals(5.0, Double.parseDouble(calculator.getTextPaneValue()), delta, "Expected result to be 5.0");
    }

    @Test
    void pressedSquareRootWithDecimalNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT);
        calculator.getValues()[0] = "25.5";
        calculator.setValuesPosition(0);
        calculator.appendTextToPane(calculator.getValueAt(0));
        calculator.performSquareRootButtonAction(actionEvent);
        assertEquals(5.049752469181039, Double.parseDouble(calculator.getTextPaneValue()), delta, "Expected result to be 5.049752469181039");
        assertFalse(calculator.isDotPressed(), "Expected dot to be disable");
    }





    @Test
    void pressed5ThenMemoryAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION);
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(FIVE);
        calculator.setValuesPosition(0);
        calculator.getValues()[0] = FIVE;
        calculator.performMemoryAdditionAction(actionEvent);
        assertEquals("15", calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 15");
        assertSame(1, calculator.getMemoryPosition(), "Expected memoryPosition to be 1");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals(FIVE, calculator.getTextPaneValue(), "Expected textPane to show 5");
    }

    @Test
    void testSavingAWholeNumberWithMemoryAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION);
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(EMPTY);
        calculator.setValuesPosition(0);
        calculator.performMemoryAdditionAction(actionEvent);
        assertEquals("10", calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 10");
        assertSame(1, calculator.getMemoryPosition(), "Expected memoryPosition to be 1");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show error");
    }

    @Test
    void testSavingADecimalNumberWithMemoryAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION);
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        calculator.appendTextToPane("5.5");
        calculator.setValuesPosition(0);
        calculator.getValues()[0] = "5.5";
        calculator.performMemoryAdditionAction(actionEvent);
        assertEquals("15.5", calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 15.5");
        assertSame(1, calculator.getMemoryPosition(), "Expected memoryPosition to be 1");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals("5.5", calculator.getTextPaneValue(), "Expected textPane to show 5.5");
    }

    @Test
    void testMemoryAddFailsWhenSavingBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION);
        calculator.getMemoryValues()[0] = EMPTY;
        calculator.setMemoryPosition(0);
        calculator.getTextPane().setText(ENTER_A_NUMBER);
        calculator.setValuesPosition(0);
        calculator.performMemoryAdditionAction(actionEvent);
        assertEquals(EMPTY, calculator.getMemoryValues()[0], "Expected memoryValues[0] to be blank");
        assertSame(0, calculator.getMemoryPosition(), "Expected memoryPosition to be 0");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show error");
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
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "Expected textPane to be 1");

        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
        assertEquals("10", calculator.getValues()[0], "Expected values[0] to be 10");
        assertEquals("10", calculator.getTextPaneValue(), "Expected textPane to be 10");

        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
        assertEquals("106", calculator.getValues()[0], "Expected values[0] to be 106");
        assertEquals("106", calculator.getTextPaneValue(), "Expected textPane to be 106");

        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
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
        assertFalse(calculator.isDotPressed(), "Expected decimal to be enabled");
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
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
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
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
    }

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
        assertFalse(calculator.isNegativeNumber(), "Expected isNegative to now be false");
    }

    @Test
    void testingMathPow()
    {
        double delta = 0.000001d;
        Number num = 8.0;
        assertEquals(num.doubleValue(), Math.pow(2,3), delta);
    }

    /**
     * Performs the next operator action based on the provided operator string.
     * @param nextOperator the operator to perform
     */
    private void performNextOperatorAction(String nextOperator)
    {
        switch (nextOperator)
        {
            case ZERO,ONE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE -> basicPanel.getCalculator().performNumberButtonAction(actionEvent);
            case ADDITION -> basicPanel.getCalculator().performAddButtonAction(actionEvent);
            case CLEAR -> basicPanel.getCalculator().performClearButtonAction(actionEvent);
            case CLEAR_ENTRY -> basicPanel.getCalculator().performClearEntryButtonAction(actionEvent);
            case DIVISION -> basicPanel.getCalculator().performDivideButtonAction(actionEvent);
            case EQUALS -> calculator.performEqualsButtonAction(actionEvent);
            case FRACTION -> basicPanel.performFractionButtonAction(actionEvent);
            case HISTORY_OPEN, HISTORY_CLOSED -> calculator.performHistoryAction(actionEvent);
            case MEMORY_ADDITION -> basicPanel.getCalculator().performMemoryAdditionAction(actionEvent);
            case MEMORY_CLEAR -> basicPanel.getCalculator().performMemoryClearAction(actionEvent);
            case MEMORY_RECALL -> basicPanel.getCalculator().performMemoryRecallAction(actionEvent);
            case MEMORY_STORE -> basicPanel.getCalculator().performMemoryStoreAction(actionEvent);
            case MEMORY_SUBTRACTION -> basicPanel.getCalculator().performMemorySubtractionAction(actionEvent);
            case MULTIPLICATION -> basicPanel.getCalculator().performMultiplyButtonAction(actionEvent);
            case PERCENT -> basicPanel.performPercentButtonAction(actionEvent);
            case SQUARED -> basicPanel.performSquaredButtonAction(actionEvent);
            case SQUARE_ROOT -> basicPanel.getCalculator().performSquareRootButtonAction(actionEvent);
            case SUBTRACTION -> basicPanel.getCalculator().performSubtractButtonAction(actionEvent);
            default -> LOGGER.warn("No operator selected: '{}'", nextOperator);
        }
    }
}

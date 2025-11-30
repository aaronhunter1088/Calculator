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

import static Types.CalculatorUtility.getValueWithoutAnyOperator;
import static Types.CalculatorUtility.isPositiveNumber;
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
class BasicPanelTest extends TestParent
{
    static { System.setProperty("appName", BasicPanelTest.class.getSimpleName()); }
    private static final Logger LOGGER = LogManager.getLogger(BasicPanelTest.class.getSimpleName());

    BasicPanel basicPanel;

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

    /* Valid MEMORY STORE */
    @ParameterizedTest
    @DisplayName("Test Valid MemoryStore Button Action")
    @MethodSource("validMemoryStoreButtonActionProvider")
    void testValidMemoryStoreButtonAction(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<Arguments> validMemoryStoreButtonActionProvider()
    {
        /*
        * case 1: store normal number is successful
        */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(MEMORY_STORE).firstNumber("1,234").firstUnaryOperator(MEMORY_STORE).firstUnaryResult("1,234").build())
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
    void testInvalidMemoryStoreButtonAction(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<Arguments> invalidMemoryStoreButtonActionProvider()
    {
        /*
        * case 1: cannot store value when operator is active
        * case 2: cannot store when textPane is empty
        * case 3: cannot store INFINITY, or badText
        */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(MEMORY_STORE).firstNumber("10").firstBinaryOperator(ADDITION).firstBinaryResult("10|10 "+ADDITION).build()),
                Arguments.of(ArgumentsForTests.builder(MEMORY_STORE).firstUnaryOperator(MEMORY_STORE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(MEMORY_STORE).firstNumber(INFINITY).firstUnaryOperator(MEMORY_STORE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+INFINITY).build())
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
    @DisplayName("Test Valid MemoryAdd Button Action")
    @MethodSource("validMemoryAddButtonCases")
    void testValidMemoryAdditionButtonAction(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<Arguments> validMemoryAddButtonCases()
    {
        /*
        * case 1: Input 10. Store 10. Clear Entry. Input 5. Add to memory. Memory is 15. TextPane shows 5
        * case 2: Input 0. Store 0. Clear Entry. Input 0. Add 0 to memory. Memory is 0. TextPane shows 0
        * case 3: Input -5. Store -5. Clear Entry. Input 10. Add 10 to memory. Memory is 5. TextPane shows 10
        * case 4: Input 100.5. Store 100.5. Clear Entry. Input 99.5. Add 99.5 to memory. Memory is 200. TextPane shows 99.5
        * case 5: Input -12.25. Store -12.25. Clear Entry. Input 1. Add 1 to memory. Memory is -11.25. TextPane shows 1
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(MEMORY_ADD)
                        .firstNumber("10").firstUnaryOperator(MEMORY_STORE).firstUnaryResult("10")
                        .firstBinaryOperator(CLEAR_ENTRY).firstBinaryResult(EMPTY)
                        .secondNumber("5").secondUnaryOperator(MEMORY_ADD).secondUnaryResult("15|5")
                        .build()),
                Arguments.of(ArgumentsForTests.builder(MEMORY_ADD)
                        .firstNumber("0").firstUnaryOperator(MEMORY_STORE).firstUnaryResult("0")
                        .firstBinaryOperator(CLEAR_ENTRY).firstBinaryResult(EMPTY)
                        .secondNumber("0").secondUnaryOperator(MEMORY_ADD).secondUnaryResult("0")
                        .build()),
                Arguments.of(ArgumentsForTests.builder(MEMORY_ADD)
                        .firstNumber("-5").firstUnaryOperator(MEMORY_STORE).firstUnaryResult("-5")
                        .firstBinaryOperator(CLEAR_ENTRY).firstBinaryResult(EMPTY)
                        .secondNumber("10").secondUnaryOperator(MEMORY_ADD).secondUnaryResult("5|10")
                        .build()),
                Arguments.of(ArgumentsForTests.builder(MEMORY_ADD)
                         .firstNumber("100.5").firstUnaryOperator(MEMORY_STORE).firstUnaryResult("100.5")
                        .firstBinaryOperator(CLEAR_ENTRY).secondUnaryResult(EMPTY)
                        .secondNumber("99.5").secondUnaryOperator(MEMORY_ADD).secondUnaryResult("200|99.5")
                        .build()),
                Arguments.of(ArgumentsForTests.builder(MEMORY_ADD)
                        .firstNumber("-12.25").firstUnaryOperator(MEMORY_STORE).firstUnaryResult("-12.25")
                        .firstBinaryOperator(CLEAR_ENTRY).firstBinaryResult(EMPTY)
                        .secondNumber("1").secondUnaryOperator(MEMORY_ADD).secondUnaryResult("-11.25|1")
                        .build())
        );
    }

    /* Invalid MEMORY ADD */
    @ParameterizedTest
    @DisplayName("Test Invalid MemoryAdd Button Action")
    @MethodSource("invalidMemoryAddButtonCases")
    void testInvalidMemoryAdditionButtonAction(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<Arguments> invalidMemoryAddButtonCases()
    {
        /*
        * case 1: cannot add to memory when textPane contains badText
        * case 2: cannot add to memory if memory is present but textPane is empty
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(MEMORY_ADD)
                        .firstNumber(ENTER_A_NUMBER).firstUnaryOperator(MEMORY_STORE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER)
                        .build()),
                Arguments.of(ArgumentsForTests.builder(MEMORY_ADD)
                        .firstNumber("5").firstUnaryOperator(MEMORY_STORE).firstUnaryResult("5")
                        .secondNumber(EMPTY).secondUnaryOperator(ADDITION).secondUnaryResult("5|5 "+ADDITION)
                        .secondBinaryOperator(MEMORY_ADD).secondBinaryResult("5|5 +")
                        .build())
        );
    }

    /* Valid MEMORY SUBTRACT */
    @ParameterizedTest
    @DisplayName("Test Valid MemorySubtract Button Action")
    @MethodSource("validMemorySubtractButtonCases")
    void testMemorySubtractButtonAction(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<Arguments> validMemorySubtractButtonCases()
    {
        /*
         * case 1: Input 10. Store 10. Clear Entry. Input 5. Add to memory. Memory is 15. TextPane shows 5
         * case 2: Input 0. Store 0. Clear Entry. Input 0. Add 0 to memory. Memory is 0. TextPane shows 0
         * case 3: Input -5. Store -5. Clear Entry. Input 10. Add 10 to memory. Memory is 5. TextPane shows 10
         * case 4: Input 100.5. Store 100.5. Clear Entry. Input 99.5. Add 99.5 to memory. Memory is 200. TextPane shows 99.5
         * case 5: Input -12.25. Store -12.25. Clear Entry. Input 1. Add 1 to memory. Memory is -11.25. TextPane shows 1
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(MEMORY_SUBTRACT)
                        .firstNumber("10").firstUnaryOperator(MEMORY_STORE).firstUnaryResult("10")
                        .firstBinaryOperator(CLEAR_ENTRY).firstBinaryResult(EMPTY)
                        .secondNumber("5").secondUnaryOperator(MEMORY_SUBTRACT).secondUnaryResult("5")
                        .build()),
                Arguments.of(ArgumentsForTests.builder(MEMORY_SUBTRACT)
                        .firstNumber("0").firstUnaryOperator(MEMORY_STORE).firstUnaryResult("0")
                        .firstBinaryOperator(CLEAR_ENTRY).firstBinaryResult(EMPTY)
                        .secondNumber("0").secondUnaryOperator(MEMORY_SUBTRACT).secondUnaryResult("0")
                        .build()),
                Arguments.of(ArgumentsForTests.builder(MEMORY_SUBTRACT)
                        .firstNumber("-5").firstUnaryOperator(MEMORY_STORE).firstUnaryResult("-5")
                        .firstBinaryOperator(CLEAR_ENTRY).firstBinaryResult(EMPTY)
                        .secondNumber("10").secondUnaryOperator(MEMORY_SUBTRACT).secondUnaryResult("-15|10")
                        .build()),
                Arguments.of(ArgumentsForTests.builder(MEMORY_SUBTRACT)
                        .firstNumber("100.5").firstUnaryOperator(MEMORY_STORE).firstUnaryResult("100.5")
                        .firstBinaryOperator(CLEAR_ENTRY).secondUnaryResult(EMPTY)
                        .secondNumber("99.5").secondUnaryOperator(MEMORY_SUBTRACT).secondUnaryResult("1|99.5")
                        .build()),
                Arguments.of(ArgumentsForTests.builder(MEMORY_SUBTRACT)
                        .firstNumber("-12.25").firstUnaryOperator(MEMORY_STORE).firstUnaryResult("-12.25")
                        .firstBinaryOperator(CLEAR_ENTRY).firstBinaryResult(EMPTY)
                        .secondNumber("1").secondUnaryOperator(MEMORY_SUBTRACT).secondUnaryResult("-13.25|1")
                        .build())
        );
    }

    /* Invalid MEMORY SUBTRACT */
    @ParameterizedTest
    @DisplayName("Test Invalid MemorySubtract Button Action")
    @MethodSource("invalidMemorySubtractButtonCases")
    void testInvalidMemorySubtractButtonAction(ArgumentsForTests arguments)
    {
        postConstructCalculator();

        setupWhenThen(actionEvent, arguments);

        String calculatorHistory = calculator.getHistoryTextPane().getText();
        calculatorHistory = performTest(arguments, calculatorHistory, LOGGER);

        assertHistory(arguments, calculatorHistory);
    }
    private static Stream<Arguments> invalidMemorySubtractButtonCases()
    {
        /*
         * case 1: cannot subtract from memory when textPane contains badText
         * case 2: cannot subtract from memory if memory is present but textPane is empty
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(MEMORY_SUBTRACT)
                        .firstNumber(ENTER_A_NUMBER).firstUnaryOperator(MEMORY_STORE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER)
                        .build()),
                Arguments.of(ArgumentsForTests.builder(MEMORY_SUBTRACT)
                        .firstNumber("5").firstUnaryOperator(MEMORY_STORE).firstUnaryResult("5")
                        .secondNumber(EMPTY).secondUnaryOperator(ADDITION).secondUnaryResult("5|5 "+ADDITION)
                        .secondBinaryOperator(MEMORY_SUBTRACT).secondBinaryResult("5|5 +")
                        .build())
        );
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
                        .firstNumber("0.05").firstUnaryOperator(ADDITION).firstUnaryResult("0.05|0.05 +")
                        .firstBinaryOperator(PERCENT).firstBinaryResult("0.05|0.05 +").build())
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
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(SQUARE_ROOT)
                        .firstUnaryOperator(SQUARE_ROOT).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(SQUARE_ROOT)
                        .firstNumber(INFINITY).firstUnaryOperator(SQUARE_ROOT).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+INFINITY).build()),
                Arguments.of(ArgumentsForTests.builder(SQUARE_ROOT)
                        .firstNumber(ENTER_A_NUMBER).firstUnaryOperator(SQUARE_ROOT).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(SQUARE_ROOT)
                        .firstNumber("-5").firstUnaryOperator(SQUARE_ROOT).firstUnaryResult("-5|"+ONLY_POSITIVES).build())
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
                Arguments.of(ArgumentsForTests.builder(SQUARED).firstNumber("5").firstUnaryOperator(SQUARED).firstUnaryResult("25").build()),
                Arguments.of(ArgumentsForTests.builder(SQUARED).firstNumber("-4").firstUnaryOperator(SQUARED).firstUnaryResult("16").build()),
                Arguments.of(ArgumentsForTests.builder(SQUARED).firstNumber("0").firstUnaryOperator(SQUARED).firstUnaryResult("0").build()),
                Arguments.of(ArgumentsForTests.builder(SQUARED).firstNumber("-5").firstUnaryOperator(SQUARED).firstUnaryResult("25").build()),
                Arguments.of(ArgumentsForTests.builder(SQUARED).firstNumber("2.5").firstUnaryOperator(SQUARED).firstUnaryResult("6.25").build()),
                Arguments.of(ArgumentsForTests.builder(SQUARED).firstNumber("5.5").firstUnaryOperator(SQUARED).firstUnaryResult("30.25").build())
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
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(SQUARED)
                        .firstUnaryOperator(SQUARED).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(SQUARED)
                        .firstNumber(ENTER_A_NUMBER).firstUnaryOperator(SQUARED).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build())
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
                Arguments.of(ArgumentsForTests.builder(FRACTION).firstNumber("5").firstUnaryOperator(ADDITION).firstUnaryResult("5|5 "+ADDITION).firstBinaryOperator(FRACTION).firstBinaryResult("5|5 "+ADDITION).build())
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
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("35").firstUnaryOperator(DELETE).firstUnaryResult("3")),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("3").firstUnaryOperator(DELETE).firstUnaryResult(EMPTY)),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("35.12").firstUnaryOperator(DELETE).firstUnaryResult("35.1")),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("35.1").firstUnaryOperator(DELETE).firstUnaryResult("35.")),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("3").firstUnaryOperator(DELETE).firstUnaryResult(EMPTY).firstBinaryResult(DELETE).firstBinaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER)),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("35").firstBinaryOperator(ADDITION).firstBinaryResult("35|35 "+ADDITION).secondUnaryOperator(DELETE).secondUnaryResult("35")),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber("1").firstBinaryOperator(SUBTRACTION).firstBinaryResult("1|1 "+SUBTRACTION).secondUnaryOperator(DELETE).secondUnaryResult("1")),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstNumber(ENTER_A_NUMBER).firstUnaryResult(DELETE).firstUnaryResult(EMPTY)),
                Arguments.of(ArgumentsForTests.builder(DELETE).firstUnaryOperator(DELETE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER))
        );
    }

    @Test
    void enteredANumberThenAddThen6DeleteThen5()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(DELETE)
                .thenReturn(FIVE);
        calculator.getValues()[0] = "15.6";
        calculator.getValues()[1] = "6";
        calculator.getValues()[2] = ADDITION;
        calculator.setValuesPosition(1);
        calculator.appendTextToPane(calculator.getValueAt(1));
        calculator.getButtonDecimal().setEnabled(true);

        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(EMPTY, calculator.getTextPaneValue(), "textPane is not blank");
        assertEquals(1, calculator.getValuesPosition(), "Values position is not 1");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane does not equal blank");
        assertFalse(calculator.isDecimalPressed(), "Expected decimal button to be enabled");
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

    /* Valid SUBTRACT */
    @Test
    void pressedSubtractWithErrorInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(ERROR);
        calculator.performSubtractButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
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

    /* Invalid SUBTRACT */
    @Test
    void pressedSubtractWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION);
        calculator.performAddButtonAction(actionEvent);

        assertEquals(EMPTY, calculator.getValues()[calculator.getValuesPosition()], "Values[{}] is not empty");
        assertEquals(ENTER_A_NUMBER, getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should display 'Enter a Number'");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
    }

    /* Valid MULTIPLY */
    @Test
    void pressedMultiplyWithErrorInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(ERROR);
        calculator.performMultiplyButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
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

    /* Invalid MULTIPLY */
    @Test
    void pressedMultiplyWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(MULTIPLICATION);
        calculator.performMultiplyButtonAction(actionEvent);

        assertEquals(EMPTY, calculator.getValues()[calculator.getValuesPosition()], "Values[{}] is not empty");
        assertEquals(ENTER_A_NUMBER, getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should display 'Enter a Number'");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
    }

    /* Valid DIVIDE */
    @Test
    void pressedDivideWithErrorInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(ERROR);
        calculator.performDivideButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
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

    @Test
    void pressedDivideWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION);
        calculator.performDivideButtonAction(actionEvent);

        assertEquals(EMPTY, calculator.getValues()[calculator.getValuesPosition()], "Values[{}] is not empty");
        assertEquals(ENTER_A_NUMBER, getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should display 'Enter a Number'");
        assertTrue(calculator.isObtainingFirstNumber(), "We are not on the firstNumber");
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
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(NEGATE)
                        .firstNumber("1").firstUnaryOperator(NEGATE).firstUnaryResult("-1").build()),
                Arguments.of(ArgumentsForTests.builder(NEGATE)
                        .firstNumber("-1").firstUnaryOperator(NEGATE).firstUnaryResult("1").build()),
                Arguments.of(ArgumentsForTests.builder(NEGATE)
                        .firstNumber("1").firstUnaryOperator(NEGATE).firstUnaryResult("-1")
                        .firstBinaryOperator(NEGATE).firstBinaryResult("1").build()),
                Arguments.of(ArgumentsForTests.builder(NEGATE)
                        .firstNumber("1").firstUnaryOperator(NEGATE).firstUnaryResult("-1")
                        .firstBinaryOperator(ADDITION).firstBinaryResult("-1|-1 "+ADDITION).build()),
                Arguments.of(ArgumentsForTests.builder(NEGATE)
                .firstNumber("-5").firstUnaryOperator(NEGATE).firstUnaryResult("5")
                        .firstBinaryOperator(ADDITION).firstBinaryResult("5|5 "+ADDITION)
                        .secondNumber("2").secondUnaryOperator(NEGATE).secondUnaryResult("-2")
                        .secondBinaryOperator(ADDITION).secondBinaryResult("3|3 "+ADDITION).build())
        );
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
         */
        return Stream.of(
                Arguments.of(ArgumentsForTests.builder(NEGATE).firstUnaryOperator(NEGATE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(NEGATE).firstNumber(ENTER_A_NUMBER).firstUnaryOperator(NEGATE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+ENTER_A_NUMBER).build()),
                Arguments.of(ArgumentsForTests.builder(NEGATE).firstNumber(INFINITY).firstUnaryOperator(NEGATE).firstUnaryResult(EMPTY+ARGUMENT_SEPARATOR+INFINITY).build())
        );
    }

    @Test
    void pressedNegateOnTooBigNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(NEGATE);
        calculator.getTextPane().setText(ERROR);
        calculator.performNegateButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
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

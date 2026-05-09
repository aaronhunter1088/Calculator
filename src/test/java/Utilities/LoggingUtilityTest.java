package Utilities;

import Calculators.Calculator;
import Interfaces.CalculatorType;
import Parent.TestParent;
import Types.CalculatorConverterType;
import Types.CalculatorView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import static Types.Texts.*;
import static org.mockito.Mockito.spy;

/**
 * LoggingUtilityTest
 * <p>
 * This class tests the {@link LoggingUtility} class.
 * <p>
 *
 * @author Michael Ball
 * @version 4.0
 */
public class LoggingUtilityTest extends TestParent
{
    private static final Logger LOGGER = LogManager.getLogger(LoggingUtilityTest.class);

    static {
        System.setProperty("appName", LoggingUtilityTest.class.getSimpleName());
    }

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(() -> {
            try {
                LOGGER.info("Starting Logging Utility Test");
                calculator = spy(new Calculator());
                Preferences.userNodeForPackage(Calculator.class).clear(); // remove keys
                calculator.setSystemDetector(systemDetector);
                calculator.getBasicPanel().setCalculator(calculator); // links spyCalc to BasicPanel
            }
            catch (Exception ignored) {
            }
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(() -> {
            try {
                calculator.setVisible(false);
                calculator.dispose();
            }
            catch (Exception ignored) {}
        });
    }

    // -------------------------------------------------------------------------
    // confirm
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link LoggingUtility#confirm(Calculator, Logger, String)}
     * @param view argument 1, the calculator view/converter type to confirm
     */
    @ParameterizedTest
    @MethodSource("confirmCases")
    @DisplayName("Test confirm")
    public void testConfirm(CalculatorType view)
    {
        if (view instanceof CalculatorConverterType) calculator.setCalculatorView(CalculatorView.VIEW_CONVERTER);
        calculator.performViewMenuAction(actionEvent, view);
        LoggingUtility.confirm(calculator, LOGGER, "Test");
    }

    /**
     * Test cases for the {@link LoggingUtility#confirm(Calculator, Logger, String)} method.
     * @return various calculator views and converter types to confirm
     */
    private static Stream<CalculatorType> confirmCases()
    {
        return Stream.of(
                CalculatorView.VIEW_BASIC,
                CalculatorView.VIEW_PROGRAMMER,
                CalculatorView.VIEW_SCIENTIFIC,
                CalculatorConverterType.ANGLE,
                CalculatorConverterType.AREA,
                CalculatorView.VIEW_DATE
        );
    }

    // -------------------------------------------------------------------------
    // logActionButton(String, Logger)
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link LoggingUtility#logActionButton(String, Logger)}
     * @param buttonChoice argument 1, the button name that was pressed
     */
    @ParameterizedTest
    @MethodSource("logActionButtonStringTestCases")
    @DisplayName("Test logActionButton(String, Logger)")
    void testLogActionButtonString(String buttonChoice)
    {
        LOGGER.info("Testing logActionButton(String) with button: {}", buttonChoice);
        LoggingUtility.logActionButton(buttonChoice, LOGGER);
    }

    /**
     * Test cases for the {@link LoggingUtility#logActionButton(String, Logger)} method.
     * @return various button choices to verify
     */
    private static Stream<Arguments> logActionButtonStringTestCases()
    {
        return Stream.of(
                Arguments.of(ADDITION),
                Arguments.of(SUBTRACTION),
                Arguments.of(MULTIPLICATION),
                Arguments.of(DIVISION),
                Arguments.of(EQUALS),
                Arguments.of(CLEAR),
                Arguments.of(MEMORY_STORE),
                Arguments.of(NEGATE)
        );
    }

    // -------------------------------------------------------------------------
    // logActionButton(ActionEvent, Logger)
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link LoggingUtility#logActionButton(java.awt.event.ActionEvent, Logger)}
     * @param actionCommand argument 1, the command returned by the mocked action event
     */
    @ParameterizedTest
    @MethodSource("logActionButtonActionEventTestCases")
    @DisplayName("Test logActionButton(ActionEvent, Logger)")
    void testLogActionButtonActionEvent(String actionCommand)
    {
        LOGGER.info("Testing logActionButton(ActionEvent) with command: {}", actionCommand);
        org.mockito.Mockito.when(actionEvent.getActionCommand()).thenReturn(actionCommand);
        LoggingUtility.logActionButton(actionEvent, LOGGER);
    }

    /**
     * Test cases for the {@link LoggingUtility#logActionButton(java.awt.event.ActionEvent, Logger)} method.
     * @return various action commands to verify
     */
    private static Stream<Arguments> logActionButtonActionEventTestCases()
    {
        return Stream.of(
                Arguments.of(ADDITION),
                Arguments.of(EQUALS),
                Arguments.of(CLEAR_ENTRY),
                Arguments.of(MODULUS)
        );
    }

    // -------------------------------------------------------------------------
    // logException
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link LoggingUtility#logException(Exception, Logger)}
     * @param exception argument 1, the exception to log
     */
    @ParameterizedTest
    @MethodSource("logExceptionTestCases")
    @DisplayName("Test logException")
    void testLogException(Exception exception)
    {
        LOGGER.info("Testing logException with: {}", exception.getClass().getSimpleName());
        LoggingUtility.logException(exception, LOGGER);
    }

    /**
     * Test cases for the {@link LoggingUtility#logException(Exception, Logger)} method.
     * @return various exception types to verify
     */
    private static Stream<Arguments> logExceptionTestCases()
    {
        return Stream.of(
                Arguments.of(new IllegalArgumentException("bad argument")),
                Arguments.of(new NumberFormatException("not a number")),
                Arguments.of(new ArithmeticException("divide by zero")),
                Arguments.of(new NullPointerException("null reference"))
        );
    }

    // -------------------------------------------------------------------------
    // logWarning
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link LoggingUtility#logWarning(String, Logger)}
     * @param message argument 1, the warning message to log
     */
    @ParameterizedTest
    @MethodSource("logWarningTestCases")
    @DisplayName("Test logWarning")
    void testLogWarning(String message)
    {
        LOGGER.info("Testing logWarning with message: {}", message);
        LoggingUtility.logWarning(message, LOGGER);
    }

    /**
     * Test cases for the {@link LoggingUtility#logWarning(String, Logger)} method.
     * @return various warning messages to verify
     */
    private static Stream<Arguments> logWarningTestCases()
    {
        return Stream.of(
                Arguments.of(NOT_A_NUMBER),
                Arguments.of(CANNOT_DIVIDE_BY_ZERO),
                Arguments.of(ENTER_A_NUMBER),
                Arguments.of(ONLY_POSITIVES)
        );
    }

    // -------------------------------------------------------------------------
    // logOperation
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link LoggingUtility#logOperation(Logger, Calculator)}
     * @param operator argument 1, the operator to place in values[2] before logging
     */
    @ParameterizedTest
    @MethodSource("logOperationTestCases")
    @DisplayName("Test logOperation")
    void testLogOperation(String operator)
    {
        LOGGER.info("Testing logOperation for operator: {}", operator);
        calculator.getValues()[0] = "5";
        calculator.getValues()[1] = "3";
        calculator.getValues()[2] = operator;
        calculator.getValues()[3] = "8";
        calculator.setValuesPosition(0);
        // Pre-populate memory so MEMORY_ADD / MEMORY_SUBTRACT do not throw
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        LoggingUtility.logOperation(LOGGER, calculator);
    }

    /**
     * Test cases for the {@link LoggingUtility#logOperation(Logger, Calculator)} method,
     * covering every branch: debug operators, info operators, and the default binary branch.
     * @return each operator that drives a distinct code path
     */
    private static Stream<Arguments> logOperationTestCases()
    {
        return Stream.of(
                Arguments.of(PERCENT),         // debug – three-arg
                Arguments.of(SQUARE_ROOT),     // debug – two-arg
                Arguments.of(SQUARED),         // debug – two-arg
                Arguments.of(FRACTION),        // info  – three-arg (1 / x = result)
                Arguments.of(NEGATE),          // info  – two-arg
                Arguments.of(MEMORY_ADD),      // info  – three-arg (memory + value)
                Arguments.of(MEMORY_SUBTRACT), // info  – three-arg (memory - value)
                Arguments.of(ADDITION),        // default – four-arg
                Arguments.of(SUBTRACTION),     // default – four-arg
                Arguments.of(MULTIPLICATION),  // default – four-arg
                Arguments.of(DIVISION)         // default – four-arg
        );
    }

    // -------------------------------------------------------------------------
    // logValues
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link LoggingUtility#logValues(Calculator, Logger, int)}
     * @param upToIndex argument 1, the number of values to log
     */
    @ParameterizedTest
    @MethodSource("logValuesTestCases")
    @DisplayName("Test logValues")
    void testLogValues(int upToIndex)
    {
        LOGGER.info("Testing logValues printing {} value(s)", upToIndex);
        calculator.getValues()[0] = "5";
        calculator.getValues()[1] = "3";
        calculator.getValues()[2] = ADDITION;
        calculator.getValues()[3] = "8";
        LoggingUtility.logValues(calculator, LOGGER, upToIndex);
    }

    /**
     * Test cases for the {@link LoggingUtility#logValues(Calculator, Logger, int)} method.
     * @return various upToIndex counts to verify
     */
    private static Stream<Arguments> logValuesTestCases()
    {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(2),
                Arguments.of(3),
                Arguments.of(4)
        );
    }

    // -------------------------------------------------------------------------
    // logValuesAtPosition
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link LoggingUtility#logValuesAtPosition(Calculator, Logger)}
     * @param position argument 1, the valuesPosition to log from
     */
    @ParameterizedTest
    @MethodSource("logValuesAtPositionTestCases")
    @DisplayName("Test logValuesAtPosition")
    void testLogValuesAtPosition(int position)
    {
        LOGGER.info("Testing logValuesAtPosition at position: {}", position);
        calculator.getValues()[0] = "5";
        calculator.getValues()[1] = "10";
        calculator.setValuesPosition(position);
        LoggingUtility.logValuesAtPosition(calculator, LOGGER);
    }

    /**
     * Test cases for the {@link LoggingUtility#logValuesAtPosition(Calculator, Logger)} method.
     * @return various positions to verify
     */
    private static Stream<Arguments> logValuesAtPositionTestCases()
    {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(1)
        );
    }

    // -------------------------------------------------------------------------
    // logValueInTextPane
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link LoggingUtility#logValueInTextPane(Calculator, Logger)}
     * @param textPaneValue argument 1, the value to display in the text pane before logging
     */
    @ParameterizedTest
    @MethodSource("logValueInTextPaneTestCases")
    @DisplayName("Test logValueInTextPane")
    void testLogValueInTextPane(String textPaneValue) throws InterruptedException, InvocationTargetException
    {
        LOGGER.info("Testing logValueInTextPane with value: {}", textPaneValue);
        SwingUtilities.invokeAndWait(() -> calculator.getTextPane().setText(textPaneValue));
        LoggingUtility.logValueInTextPane(calculator, LOGGER);
    }

    /**
     * Test cases for the {@link LoggingUtility#logValueInTextPane(Calculator, Logger)} method.
     * @return various text pane values to verify
     */
    private static Stream<Arguments> logValueInTextPaneTestCases()
    {
        return Stream.of(
                Arguments.of("42"),
                Arguments.of("3.14"),
                Arguments.of("-100"),
                Arguments.of(ZERO)
        );
    }

    // -------------------------------------------------------------------------
    // logEmptyValue
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link LoggingUtility#logEmptyValue(String, Calculator, Logger)}
     * @param operation argument 1, the name of the operation that was attempted
     */
    @ParameterizedTest
    @MethodSource("logEmptyValueTestCases")
    @DisplayName("Test logEmptyValue")
    void testLogEmptyValue(String operation)
    {
        LOGGER.info("Testing logEmptyValue for operation: {}", operation);
        calculator.setValuesPosition(0);
        LoggingUtility.logEmptyValue(operation, calculator, LOGGER);
    }

    /**
     * Test cases for the {@link LoggingUtility#logEmptyValue(String, Calculator, Logger)} method.
     * @return various operation names to verify
     */
    private static Stream<Arguments> logEmptyValueTestCases()
    {
        return Stream.of(
                Arguments.of(ADDITION),
                Arguments.of(SUBTRACTION),
                Arguments.of(MULTIPLICATION),
                Arguments.of(DIVISION),
                Arguments.of(MODULUS)
        );
    }

    // -------------------------------------------------------------------------
    // logUseTextPaneValueWarning
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link LoggingUtility#logUseTextPaneValueWarning(Calculator, Logger, String)}
     * @param buttonChoice argument 1, the button that triggered the warning
     */
    @ParameterizedTest
    @MethodSource("logUseTextPaneValueWarningTestCases")
    @DisplayName("Test logUseTextPaneValueWarning")
    void testLogUseTextPaneValueWarning(String buttonChoice) throws InterruptedException, InvocationTargetException
    {
        LOGGER.info("Testing logUseTextPaneValueWarning for button: {}", buttonChoice);
        SwingUtilities.invokeAndWait(() -> calculator.getTextPane().setText("5"));
        LoggingUtility.logUseTextPaneValueWarning(calculator, LOGGER, buttonChoice);
    }

    /**
     * Test cases for the {@link LoggingUtility#logUseTextPaneValueWarning(Calculator, Logger, String)} method.
     * @return various button choices to verify
     */
    private static Stream<Arguments> logUseTextPaneValueWarningTestCases()
    {
        return Stream.of(
                Arguments.of(ADDITION),
                Arguments.of(EQUALS),
                Arguments.of(FRACTION),
                Arguments.of(PERCENT)
        );
    }

    // -------------------------------------------------------------------------
    // logOperation – logOperationWithNoValues (empty values guard)
    // -------------------------------------------------------------------------

    /**
     * Test that {@link LoggingUtility#logOperation(Logger, Calculator)} does not throw
     * when invoked with an operator that is not explicitly handled by the switch (default branch)
     * and all four value slots are populated.
     */
    @Test
    @DisplayName("Test logOperation with default binary operator (DIVISION)")
    void testLogOperationDivision()
    {
        LOGGER.info("Testing logOperation default branch with DIVISION");
        calculator.getValues()[0] = "10";
        calculator.getValues()[1] = "2";
        calculator.getValues()[2] = DIVISION;
        calculator.getValues()[3] = "5";
        calculator.setValuesPosition(0);
        LoggingUtility.logOperation(LOGGER, calculator);
    }
}

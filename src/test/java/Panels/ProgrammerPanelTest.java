package Panels;

import Calculators.Calculator;
import Parent.ArgumentsForTests;
import Parent.TestParent;
import Types.CalculatorBase;
import Types.SystemDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import static Types.CalculatorBase.BASE_BINARY;
import static Types.CalculatorBase.BASE_DECIMAL;
import static Types.CalculatorByte.BYTE_BYTE;
import static Types.CalculatorView.VIEW_PROGRAMMER;
import static Types.Texts.*;
import static org.junit.jupiter.api.Assertions.*;
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
    @Test
    public void testPushOrWithDecimalInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(OR);
        calculator.setCalculatorBase(BASE_DECIMAL); // BYTE_BYTE default
        calculator.appendTextToPane(FIVE);
        calculator.getValues()[0] = FIVE;
        calculator.getProgrammerPanel().performOrButtonAction(actionEvent);
        // TODO: Uncomment and fix
        assertEquals(TEXT_PANE_WRONG_VALUE, "5 OR", calculator.getTextPaneValue());
        assertEquals("5", calculator.getValues()[0], "Values[0] is not in decimal base form");
    }

    @Test
    public void testPushOrWithBothValuesSetWithDecimalInputReturnsResult()
    {
        when(actionEvent.getActionCommand()).thenReturn(OR);
        calculator.setCalculatorBase(BASE_DECIMAL); // BYTE_BYTE default
        calculator.getValues()[0] = FIVE;
        calculator.getValues()[1] = THREE;
        calculator.appendTextToPane(THREE);
        calculator.getProgrammerPanel().performOrButtonAction(actionEvent);
        assertEquals(SEVEN, calculator.getTextPaneValue(), TEXT_PANE_WRONG_VALUE);
    }

    /* Invalid OR */
    @Test
    public void testPushOrWhenValuesAtZeroNotSet()
    {
        when(actionEvent.getActionCommand()).thenReturn(OR);
        calculator.getValues()[0] = EMPTY;
        calculator.getValues()[1] = "50";
        calculator.getProgrammerPanel().performOrButtonAction(actionEvent);
        assertTrue(calculator.isObtainingFirstNumber(), "Expected isFirstNumber to be true");
        assertEquals(EMPTY, calculator.getValues()[0], "Expected values[0] to be empty");
        assertEquals("50", calculator.getValues()[1], "Expected values[1] to be 50");
    }

    /* Valid XOR */

    /* Invalid XOR */
    @Test
    public void testPushingXorButtonWithBlankInputsDoesNothing()
    {
        when(actionEvent.getActionCommand()).thenReturn(XOR);
        calculator.getValues()[0] = EMPTY;
        calculator.getValues()[1] = EMPTY;
        calculator.setValuesPosition(1);
        calculator.getProgrammerPanel().performXorButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), TEXT_PANE_WRONG_VALUE);
        assertEquals(EMPTY, calculator.getValues()[0], "Values[0] should be empty");
        assertEquals(EMPTY, calculator.getValues()[1], "Values[1] should be empty");
        assertNotEquals(XOR, calculator.getValueAt(2), "Expecting NOT XOR at values[2]");
    }

    /* Valid NOT */
    @Test
    public void pressingNotButtonReversesAllBits()
    {
        when(actionEvent.getActionCommand()).thenReturn(NOT);
        calculator.setCalculatorBase(BASE_BINARY);
        calculator.getProgrammerPanel().appendTextForProgrammerPanel("0000 1011");
        calculator.getProgrammerPanel().performNotButtonAction(actionEvent);
        assertEquals("topQWord not as expected", ""); // lots of 1's
        assertEquals("1111 0100", calculator.getProgrammerPanel().separateBits(calculator.getTextPaneValue()), TEXT_PANE_WRONG_VALUE);
    }

    /* Invalid NOT */

    /* Valid AND */
    @Test
    @DisplayName("Test And Button")
    public void testPerformAdd()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(FIVE).thenReturn(FIVE)
                .thenReturn(AND)
                .thenReturn(SIX).thenReturn(SIX)
                .thenReturn(EQUALS);
        calculator.getProgrammerPanel().getCalculator().performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getTextPaneValue(), "Expected textPane to be 5");

        calculator.getProgrammerPanel().performAndButtonAction(actionEvent);
        assertEquals("5 AND", calculator.getTextPaneValue(), "Expected textPane to be 5 AND");
        assertEquals(AND, calculator.getValueAt(2), "Expecting AND at values[2]");

        calculator.getProgrammerPanel().getCalculator().performNumberButtonAction(actionEvent);
        assertEquals(SIX, calculator.getTextPaneValue(), "Expected textPane to contain 6");
        assertEquals(AND, calculator.getValueAt(2), "Expecting AND at values[2]");

        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(FOUR, calculator.getTextPaneValue(), "Expected textPane to be 4");
        assertNotEquals(AND, calculator.getValueAt(2), "Expecting NOT AND at values[2]");
    }

    @Test
    @DisplayName("Test And Button continued operation")
    public void testPerformAndContinuedOperation()
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

    /* Invalid AND */
    @Test
    public void testClickedButtonAndWhenTextPaneBlank()
    {
        when(actionEvent.getActionCommand()).thenReturn(AND);
        calculator.getProgrammerPanel().performAndButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to say " + ENTER_A_NUMBER);
    }

    @Test
    public void testClickedButtonAndWhenTextPaneContainsBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(AND);
        calculator.appendTextToPane(ENTER_A_NUMBER);
        calculator.getProgrammerPanel().performAndButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to say " + ENTER_A_NUMBER);
    }

    @Test
    public void testClickedButtonAndWhenValuesAtZeroIsEmpty()
    {
        when(actionEvent.getActionCommand()).thenReturn(AND);
        calculator.getProgrammerPanel().performAndButtonAction(actionEvent);
        assertEquals(EMPTY, calculator.getValues()[0], "Expected values[0] to be empty");
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to say " + ENTER_A_NUMBER);
    }

    @Test
    public void testClickedButtonAndWhenValuesAtOneIsEmpty()
    {
        when(actionEvent.getActionCommand()).thenReturn(AND);
        calculator.getValues()[0] = FIVE;
        calculator.appendTextToPane(FIVE);
        calculator.getProgrammerPanel().performAndButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[0], "Expected values[0] to be 5");
        assertEquals("5 AND", calculator.getTextPaneValue(), "Expected textPane to say 5 AND");
    }

    /* Valid SHIFT */

    /* Invalid SHIFT ?? */

    /* Valid MODULUS */
    @ParameterizedTest()
    @MethodSource("getModulusCases")
    @DisplayName("Test Modulus Button")
    public void testModulusButton(CalculatorBase base, String firstNumber, String secondNumber,
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
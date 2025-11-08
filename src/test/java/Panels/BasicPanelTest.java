package Panels;

import Calculators.Calculator;
import Calculators.CalculatorError;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

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
public class BasicPanelTest
{
    static { System.setProperty("appName", BasicPanelTest.class.getSimpleName()); }
    private static final Logger LOGGER = LogManager.getLogger(BasicPanelTest.class.getSimpleName());

    private Calculator calculator;
    private BasicPanel basicPanel;
    private final double delta = 0.000001d;
    @Mock
    ActionEvent actionEvent;

    @BeforeAll
    public static void beforeAll()
    { }

    @BeforeEach
    public void beforeEach() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        LOGGER.info("setting up each before...");
        calculator = new Calculator(VIEW_BASIC);
        calculator.setVisible(true);
        basicPanel = (BasicPanel) calculator.getCurrentPanel();
    }

    @AfterEach
    public void afterEach() throws InterruptedException, InvocationTargetException
    {
        for (Calculator calculator : java.util.List.of(calculator))
        {
            LOGGER.info("Test complete. Closing the calculator...");
            // Create a WindowEvent with WINDOW_CLOSING event type
            WindowEvent windowClosing = new WindowEvent(calculator, WindowEvent.WINDOW_CLOSING);

            EventQueue.invokeAndWait(() -> {
                calculator.setVisible(false);
                calculator.dispose();
            });
            assertFalse(calculator.isVisible());
        }
    }

    @AfterAll
    public static void afterAll()
    { LOGGER.info("Finished running " + BasicPanelTest.class.getSimpleName()); }

    @Test
    public void testCreatingBasicPanel()
    {
        assertEquals(VIEW_BASIC.getValue(), new BasicPanel().getName(), "Expected panel name to be " + VIEW_BASIC);
    }

    @Test
    public void testCreatingBasicPanelWithCalculator()
    {
        assertEquals(VIEW_BASIC.getValue(), basicPanel.getName(), "Expected panel name to be " + VIEW_BASIC);
    }

    @Test
    public void testShowingHelpShowsText()
    {
        calculator.showHelpPanel("This is a test. Close to proceed.");
    }

    @Test
    public void pressed1()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]), "Values[{}] is not 1");
        assertEquals(ONE, calculator.getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should be 1");
        assertTrue(calculator.isPositiveNumber(calculator.getValueWithoutAnyOperator(calculator.getTextPaneValue())), "{} is not positive");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    public void pressed1Then5()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(FIVE);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]), "Values[{}] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isPositiveNumber(String.valueOf(calculator.getValues()[calculator.getValuesPosition()])), "{} is not positive");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);

        assertEquals(15, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]), "Values[{}] is not 15");
        assertEquals("15", calculator.getTextPaneValue(), "textPane should be 15");
        assertTrue(calculator.isPositiveNumber(String.valueOf(calculator.getValues()[calculator.getValuesPosition()])), "{} is not positive");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    public void pressed1ThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(NEGATE);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNegateButtonAction(actionEvent);

        assertEquals(-1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]), "Values[{}] is not -1");
        assertEquals("-1", calculator.getTextPaneValue(), "textPane should be -1");
        assertTrue(calculator.isNegativeNumber(calculator.getValues()[calculator.getValuesPosition()]), "{} is not negative");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    public void pressed1ThenNegateThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(NEGATE).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNegateButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);

        assertEquals(-1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not -1");
        assertEquals("-1 +", calculator.getTextPaneValue(), "textPane should be -1 +");
        assertTrue(calculator.isAdding(), "Expecting isAdding to be set");
        assertFalse(calculator.isFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    public void pressed1Then5ThenNegateThenAddThen5ThenNegateThenEquals()
    {
        // -15 + -5 =
        when(actionEvent.getActionCommand())
                .thenReturn(ONE).thenReturn(FIVE)
                .thenReturn(NEGATE).thenReturn(ADDITION)
                .thenReturn(FIVE).thenReturn(NEGATE)
                .thenReturn(EQUALS);

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertSame(0, calculator.getValuesPosition(), "ValuesPosition should be 0");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 15");
        assertEquals("15", calculator.getTextPaneValue(), "textPane should be 15");
        assertSame(0, calculator.getValuesPosition(), "ValuesPosition should be 0");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performNegateButtonAction(actionEvent);
        assertEquals(-15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not -15");
        assertEquals("-15", calculator.getTextPaneValue(), "textPane should be -15");
        assertSame(0, calculator.getValuesPosition(), "ValuesPosition should be 0");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performAdditionButtonAction(actionEvent);
        assertEquals(-15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not -15");
        assertEquals("-15 +", calculator.getTextPaneValue(), "textPane should be -15 +");
        assertSame(1, calculator.getValuesPosition(), "ValuesPosition should be 1");
        assertTrue(calculator.isAdding(), "Expecting isAdding to be set");
        assertFalse(calculator.isFirstNumber(), "We are still on the firstNumber");
        assertFalse(calculator.isNumberNegative(), "Expecting isNumberNegative to be false");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane should be 5");
        assertSame(1, calculator.getValuesPosition(), "ValuesPosition should be 1");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performNegateButtonAction(actionEvent);
        assertEquals(-5, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not -5");
        assertEquals("-5", calculator.getTextPaneValue(), "textPane should be -5");
        assertSame(1, calculator.getValuesPosition(), "ValuesPosition should be 1");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("-20", calculator.getTextPaneValue(), "textPane is not -20");
        assertEquals(BLANK, calculator.getValues()[1], "Values[1] is not empty");
        assertEquals("-20", calculator.getTextPaneValue(), "textPane should be -20");
        assertFalse(calculator.isFirstNumber(), "Expected firstNumber to be false");
    }

    @Test
    public void pressedDecimal()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL);
        calculator.getValues()[0] = BLANK;
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK);
        calculator.performDecimalButtonAction(actionEvent);
        assertEquals("0.", calculator.getTextPaneValue(), "textPane is not as expected");
        assertFalse(calculator.getButtonDecimal().isEnabled(), "buttonDot should be disabled");
    }

    @Test
    public void pressed544ThenDecimal()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL);
        calculator.getTextPane().setText(calculator.addNewLines() + "544");
        calculator.getValues()[0] = "544";
        calculator.performDecimalButtonAction(actionEvent);
        assertEquals("544.", calculator.getTextPaneValue(), "textPane is not as expected");
        assertEquals("544.", calculator.getValues()[0], "Values[0]");
        assertFalse(calculator.getButtonDecimal().isEnabled(), "buttonDot should be disabled");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    public void pressed1Then2Then3Then4ThenDecimal()
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
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    public void pressedDecimalThen9()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL).thenReturn(NINE);
        calculator.performDecimalButtonAction(actionEvent);

        assertEquals("0.", calculator.getValues()[0], "Values[0] is not 0.");
        assertEquals("0.", calculator.getTextPaneValue(), "textPane should be 0.");
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected dot to be disabled");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);
        if (calculator.isNegativeNumber(calculator.getValues()[0])) fail("Number is negative");

        assertEquals( 0.9f, Double.parseDouble(calculator.getValues()[0]), delta); // 0.8999999761581421
        assertEquals("0.9", calculator.getTextPaneValue(), "textPane should be 0.9");
        assertFalse(calculator.isDotPressed(), "Expecting decimal to be disabled");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    public void pressed1ThenDecimalThen5ThenAddThen2()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(DECIMAL)
                .thenReturn(FIVE).thenReturn(ADDITION).thenReturn(TWO);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDecimalButtonAction(actionEvent);
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be disabled");
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be enabled");
    }

    @Test
    public void pressed1ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performAdditionButtonAction(actionEvent);
        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals("1 +", calculator.getTextPaneValue(), "textPane should be 1 +");
        assertTrue(calculator.isAdding(), "Expecting isAdding to be set");
        assertFalse(calculator.isFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    public void pressed1Then5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(FIVE).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);

        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 15");
        assertEquals("15", calculator.getTextPaneValue(), "textPane should be 15");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performAdditionButtonAction(actionEvent);

        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals("15 +", calculator.getTextPaneValue(), "textPane should be 15 +");
        assertTrue(calculator.isAdding(), "Expecting isAdding to be set");
        assertFalse(calculator.isFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    public void pressed1ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performSubtractionButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals("1 -", calculator.getTextPaneValue(), "textPane should be 1 -");
        assertTrue(calculator.isSubtracting(), "Expecting isSubtracting to be set");
        assertFalse(calculator.isFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    public void pressed1Then5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(FIVE).thenReturn(SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);

        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 15");
        assertEquals("15", calculator.getTextPaneValue(), "textPane should be 15");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performSubtractionButtonAction(actionEvent);

        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals("15 -", calculator.getTextPaneValue(), "textPane should be 15 -");
        assertTrue(calculator.isSubtracting(), "Expecting isSubtracting to be set");
        assertFalse(calculator.isFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    public void pressed1Then5ThenSubtractThen5ThenEquals()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE).thenReturn(FIVE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(EQUALS);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 15");
        assertEquals("15", calculator.getTextPaneValue(), "textPane should be 15");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 15");
        assertEquals("15 -", calculator.getTextPaneValue(), "textPane should be 15 -");
        assertTrue(calculator.isSubtracting(), "Expected isSubtracting to be set");
        assertSame(1, calculator.getValuesPosition(), "ValuesPosition should be 1");
        assertFalse(calculator.isFirstNumber(), "We are still on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane should be 5");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(BLANK, calculator.getValues()[0], "Values[0] is not blank");
        assertEquals(BLANK, calculator.getValues()[1], "Values[1] is not empty");
        assertEquals("10", calculator.getTextPaneValue(), "textPane should be 10");
        assertFalse(calculator.isFirstNumber(), "Expected firstNumber to be false");

        verify(actionEvent, times(5)).getActionCommand();
    }

    @Test
    public void pressed1ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performMultiplicationAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals("1 ✕", calculator.getTextPaneValue(), "textPane should be 1 ✕");
        assertFalse(calculator.isFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    public void pressed1Then5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(FIVE).thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);

        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);

        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 15");
        assertEquals("15", calculator.getTextPaneValue(), "textPane should be 15");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performAdditionButtonAction(actionEvent);

        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals("15 ✕", calculator.getTextPaneValue(), "textPane should be 15 ✕");
        assertSame(1, calculator.getValuesPosition(), "ValuesPosition should be 1");
        assertFalse(calculator.isFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    public void pressed1Then0ThenMultiplyThen1Then0ThenEquals()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE).thenReturn(ZERO).thenReturn(MULTIPLICATION)
                .thenReturn(ONE).thenReturn(ZERO).thenReturn(EQUALS);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(1, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(10, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 10");
        assertEquals("10", calculator.getTextPaneValue(), "textPane should be 10");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performMultiplicationAction(actionEvent);
        assertEquals(10, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 10");
        assertEquals("10 ✕", calculator.getTextPaneValue(), "textPane should be 10 ✕");
        assertTrue(calculator.isMultiplying(), "Expecting isMultiplying to be set");
        assertSame(1, calculator.getValuesPosition(), "ValuesPosition should be 1");
        assertFalse(calculator.isFirstNumber(), "We are still on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(1, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane should be 1");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(10, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not 10");
        assertEquals("10", calculator.getTextPaneValue(), "textPane should be 10");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("100", calculator.getTextPaneValue(), "textPane is not 100");
        assertEquals(BLANK, calculator.getValues()[1], "Values[1] is not empty");
        assertEquals("100", calculator.getTextPaneValue(), "textPane should be 100");
        assertFalse(calculator.isFirstNumber(), "Expected firstNumber to be false");

        verify(actionEvent, times(6)).getActionCommand();
    }

    @Test
    public void pressedAddWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.performAdditionButtonAction(actionEvent);

        assertEquals(BLANK, calculator.getValues()[calculator.getValuesPosition()], "Values[{}] is not empty");
        assertEquals(ENTER_A_NUMBER, calculator.getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should display 'Enter a Number'");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    public void pressed1ThenAddThenAddAgain()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent); // no thenReturn necessary. Uses last value sent to method

        assertEquals(ONE, calculator.getValues()[0], "Values[{}] is not 1");
        assertTrue(calculator.isAdding(), "Expected isAdding to be true");
        assertFalse(calculator.isFirstNumber(), "We are still on the firstNumber");
    }

    @Test
    public void pressedAddWithErrorInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(calculator.addNewLines() + ERROR);
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    @Test
    public void pressedSubtractWithErrorInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(calculator.addNewLines() + ERROR);
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    @Test
    public void pressedMultiplyWithErrorInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(calculator.addNewLines() + ERROR);
        calculator.performMultiplicationAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    @Test
    public void pressedDivideWithErrorInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(calculator.addNewLines() + ERROR);
        calculator.performDivideButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    @Test
    public void pressedAddWithNumberTooBigInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getTextPane().setText(calculator.addNewLines() + NUMBER_TOO_BIG);
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals(NUMBER_TOO_BIG, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    @Test
    public void pressed1ThenAddThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(ADDITION)
                .thenReturn(FIVE).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("6 +", calculator.getTextPaneValue(), "Expected textPane shows 6 +");
        assertTrue(calculator.isAdding(), "Expected isAdd to be true");
    }

    @Test
    public void pressedSubtractThen5()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION).thenReturn(FIVE);
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals(SUBTRACTION, calculator.getTextPaneValue(), "Expected textPane to show - symbol");
        assertEquals(BLANK, calculator.getValues()[0], "Expected values[0] to be blank");
        assertTrue(calculator.isNumberNegative(), "Expected isNumberNegative to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("-5", calculator.getTextPaneValue(), "Expected textPane to show -5");
        assertEquals("-5", calculator.getValues()[0], "Expected values[0] to be -5");
        assertTrue( calculator.isNumberNegative(), "Expected isNumberNegative to be true");
        assertTrue(calculator.isNumberNegative(), "Expected isNegative to be true");
    }

    @Test
    public void pressed5ThenAddThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE).thenReturn(ADDITION).thenReturn(SUBTRACTION);
        calculator.setIsNumberNegative(false);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals(SUBTRACTION, calculator.getTextPaneValue(), "Expected textPane to -");
        assertEquals(FIVE, calculator.getValues()[0], "Expected values[0] to be 5");
        assertTrue(calculator.isNumberNegative(), "Expected isNumberNegative is true");
    }

    @Test
    public void pressed1ThenSubtractThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performSubtractionButtonAction(actionEvent);
        assertTrue(calculator.isSubtracting(), "Expected isSubtract to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("-4 +", calculator.getTextPaneValue(), "Expected textPane shows -4 +");
        assertTrue(calculator.isAdding(), "Expected isAdd to be true");
    }

    @Test
    public void pressed1ThenSubtractThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performSubtractionButtonAction(actionEvent);
        assertTrue(calculator.isSubtracting(), "Expected isSubtract to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("-4 -", calculator.getTextPaneValue(), "Expected textPane shows -4 -");
        assertTrue(calculator.isSubtracting(), "Expected isSubtracting to be true");
    }

    @Test
    public void testSubtractWithDecimalNumbers()
    {
        // 4.5 - -2.3 = 6.8
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = "4.5";
        calculator.setIsSubtracting(true);
        calculator.getValues()[1] = "-2.3";
        calculator.setIsNumberNegative(true);
        calculator.setValuesPosition(1);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("6.8", calculator.getTextPaneValue(), "Expected textPane shows 6.8");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
        assertFalse(calculator.isNumberNegative(), "Expected isNumberNegative to be false");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be disabled");
    }

    @Test
    public void testPressedAdditionAfterEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(EQUALS).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("-4 +", calculator.getTextPaneValue(), "Expected textPane to be -4 +");
        assertEquals("-4", calculator.getValues()[0], "Expected values[0] to be -4");
        assertTrue(calculator.isAdding(), "Expected isAdding to be true");
    }

    @Test
    public void testPressedSubtractAfterEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(EQUALS).thenReturn(SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("-4 -", calculator.getTextPaneValue(), "Expected textPane to be -4 -");
        assertEquals("-4", calculator.getValues()[0], "Expected values[0] to be -4");
        assertTrue(calculator.isSubtracting(), "Expected isSubtracting to be true");
    }

    @Test
    public void testPressedMultiplyAfterEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(EQUALS).thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        calculator.performMultiplicationAction(actionEvent);
        assertEquals("-4 ✕", calculator.getTextPaneValue(), "Expected textPane to be -4 ✕");
        assertEquals("-4", calculator.getValues()[0], "Expected values[0] to be -4");
        assertTrue(calculator.isMultiplying(), "Expected isMultiplying to be true");
    }

    @Test
    public void testPressedDivideAfterEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(EQUALS).thenReturn(DIVISION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("-4 ÷", calculator.getTextPaneValue(), "Expected textPane to be -4 ÷");
        assertEquals("-4", calculator.getValues()[0], "Expected values[0] to be -4");
        assertTrue(calculator.isDividing(), "Expected isDividing to be true");
    }

    @Test
    public void testContinuedSubtractionWithNegatedNumber()
    {
        // 2 - -5 -  --> 7 -
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION);
        calculator.getValues()[0] = "2";
        calculator.setIsSubtracting(true);
        calculator.setIsNumberNegative(true);
        calculator.getValues()[1] = "-5";
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("7 -", calculator.getTextPaneValue(), "Expected textPane to be 7 -");
        assertEquals("7", calculator.getValues()[0], "Expected values[0] to be 7");
        assertTrue(calculator.isSubtracting(), "Expected isSubtracting to be true");
        assertFalse(calculator.isNumberNegative(), "Expected isNumberNegative to be false");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
    }

    @Test
    public void testContinuedSubtractionWithDecimals()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION);
        calculator.getValues()[0] = "2.3";
        calculator.setIsSubtracting(true);
        calculator.setIsNumberNegative(true);
        calculator.getValues()[1] = "-2.1";
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("4.4 -", calculator.getTextPaneValue(), "Expected textPane to be 4.4 -");
        assertEquals("4.4", calculator.getValues()[0], "Expected values[0] to be 4.4");
        assertTrue(calculator.isSubtracting(), "Expected isSubtracting to be true");
        assertFalse(calculator.isNumberNegative(), "Expected isNumberNegative to be false");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
    }

    @Test
    public void testContinuedSubtractionWithDecimalsAndNegatedNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION);
        calculator.getValues()[0] = "2.3";
        calculator.setIsSubtracting(true);
        calculator.setIsNumberNegative(false);
        calculator.getValues()[1] = "2.1";
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("0.2 -", calculator.getTextPaneValue(), "Expected textPane to be 0.2 -");
        assertEquals("0.2", calculator.getValues()[0], "Expected values[0] to be 0.2");
        assertTrue(calculator.isSubtracting(), "Expected isSubtracting to be true");
        assertFalse(calculator.isNumberNegative(), "Expected isNumberNegative to be false");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
    }

    @Test
    public void pressed1ThenSubtractThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performSubtractionButtonAction(actionEvent);
        assertTrue(calculator.isSubtracting(), "Expected isSubtract to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performMultiplicationAction(actionEvent);
        assertEquals("-4 ✕", calculator.getTextPaneValue(), "Expected textPane shows -4 ✕");
        assertTrue(calculator.isMultiplying(), "Expected isMultiplying to be true");
    }

    @Test
    public void pressed1ThenDivideThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(DIVISION)
                .thenReturn(FIVE).thenReturn(SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performDivideButtonAction(actionEvent);
        assertTrue(calculator.isDividing(), "Expected isDividing to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("0.2 -", calculator.getTextPaneValue(), "Expected textPane shows 0.2 -");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
        assertTrue(calculator.isSubtracting(), "Expected isSubtracting to be true");
    }

    @Test
    public void pressed1ThenSubtractThen5ThenDivide()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(DIVISION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performSubtractionButtonAction(actionEvent);
        assertTrue(calculator.isSubtracting(), "Expected isSubtract to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("-4 ÷", calculator.getTextPaneValue(), "Expected textPane shows -4 ÷");
        assertTrue(calculator.isDividing(), "Expected isDividing to be true");
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(MULTIPLICATION)
                .thenReturn(FIVE).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performMultiplicationAction(actionEvent);
        assertTrue(calculator.isMultiplying(), "Expected isMultiplying to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("5 +", calculator.getTextPaneValue(), "Expected textPane shows 5 +");
        assertTrue(calculator.isAdding(), "Expected isAdd to be true");
    }

    @Test
    public void testMultiplyingTwoDecimals()
    {
        when(actionEvent.getActionCommand()).thenReturn(MULTIPLICATION);
        calculator.getValues()[0] = "4.5";
        calculator.setIsMultiplying(true);
        calculator.getValues()[1] = "2.3";
        calculator.performMultiplicationAction(actionEvent);
        assertEquals("10.35 ✕", calculator.getTextPaneValue(), "Expected textPane shows 10.35 ✕");
        assertTrue(calculator.isMultiplying(), "Expected isMultiplying to be true");
        assertFalse(calculator.isNumberNegative(), "Expected isNumberNegative to be false");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(MULTIPLICATION)
                .thenReturn(FIVE).thenReturn(SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performMultiplicationAction(actionEvent);
        assertTrue(calculator.isMultiplying(), "Expected isMultiplying to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("5 -", calculator.getTextPaneValue(), "Expected textPane shows 5 -");
        assertTrue(calculator.isSubtracting(), "Expected isSubtracting to be true");
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(MULTIPLICATION)
                .thenReturn(FIVE).thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performMultiplicationAction(actionEvent);
        assertTrue(calculator.isMultiplying(), "Expected isMultiplying to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performMultiplicationAction(actionEvent);
        assertEquals("5 ✕", calculator.getTextPaneValue(), "Expected textPane shows 5 ✕");
        assertTrue(calculator.isMultiplying(), "Expected isMultiplying to be true");
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenDivide()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE).thenReturn(MULTIPLICATION)
                .thenReturn(FIVE).thenReturn(DIVISION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performMultiplicationAction(actionEvent);
        assertTrue(calculator.isMultiplying(), "Expected isMultiplying to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("5 ÷", calculator.getTextPaneValue(), "Expected textPane shows 5 ÷");
        assertTrue(calculator.isDividing(), "Expected isDividing to be true");
    }

    @Test
    public void pressed1ThenDivideThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(DIVISION)
                .thenReturn(FIVE).thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performDivideButtonAction(actionEvent);
        assertTrue(calculator.isDividing(), "Expected isDividing to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performMultiplicationAction(actionEvent);
        assertEquals("0.2 ✕", calculator.getTextPaneValue(), "Expected textPane shows 0.2 ✕");
        assertTrue(calculator.isMultiplying(), "Expected isMultiplying to be true");
    }

    @Test
    public void pressed1ThenMultiplyThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(MULTIPLICATION).thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performMultiplicationAction(actionEvent);
        calculator.performMultiplicationAction(actionEvent);
        assertTrue(calculator.isMultiplying(), "Expected isMultiplying to be true");
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
    }

    @Test
    public void pressed1ThenDivideThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(DIVISION)
                .thenReturn(FIVE).thenReturn(ADDITION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performDivideButtonAction(actionEvent);
        assertTrue(calculator.isDividing(), "Expected isDividing to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("0.2 +", calculator.getTextPaneValue(), "Expected textPane shows 0.2 +");
        assertTrue(calculator.isAdding(), "Expected isAdd to be true");
    }

    @Test
    public void pressed1ThenDivideThen5ThenDivide()
    {
        // 1 ÷ 5 ÷
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(DIVISION)
                .thenReturn(FIVE).thenReturn(DIVISION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        calculator.performDivideButtonAction(actionEvent);
        assertTrue(calculator.isDividing(), "Expected isDividing to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[1], "Expected values[1] to be 5");
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("0.2 ÷", calculator.getTextPaneValue(), "Expected textPane shows 0.2 ÷");
        assertTrue(calculator.isDividing(), "Expected isDividing to be true");
    }

    @Test
    public void pressed1ThenDivideThenDivide()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(DIVISION).thenReturn(DIVISION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDivideButtonAction(actionEvent);
        calculator.performDivideButtonAction(actionEvent);
        assertTrue(calculator.isDividing(), "Expected isDividing to be true");
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
    }
    
    @Test
    public void pressed1ThenDivideThen0()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(DIVISION).thenReturn(ZERO).thenReturn(EQUALS);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDivideButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
        assertEquals(BLANK, calculator.getValues()[0], "Expected values[0] to be blank");
    }

    @Test
    public void pressed1ThenAddThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(ADDITION)
                .thenReturn(FIVE).thenReturn(SUBTRACTION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractionButtonAction(actionEvent);
        calculator.setValuesPosition(1);
        assertEquals("6 -", calculator.getTextPaneValue(), "Expected textPane shows 6 -");
        assertTrue(calculator.isSubtracting(), "Expected isSubtract to be true");
    }

    @Test
    public void pressed1ThenAddThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(ADDITION)
                .thenReturn(FIVE).thenReturn(MULTIPLICATION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performMultiplicationAction(actionEvent);
        calculator.setValuesPosition(1);
        assertEquals("6 ✕", calculator.getTextPaneValue(), "Expected textPane shows 6 ✕");
        assertTrue(calculator.isMultiplying(), "Expected isMultiplying to be true");
    }

    @Test
    public void pressed1ThenAddThen5ThenDivide()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE).thenReturn(ADDITION)
                .thenReturn(FIVE).thenReturn(DIVISION);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDivideButtonAction(actionEvent);
        calculator.setValuesPosition(1);
        assertEquals("6 ÷", calculator.getTextPaneValue(), "Expected textPane shows 6 ÷");
        assertTrue(calculator.isDividing(), "Expected isDividing to be true");
    }

    @Test
    public void testAddingTwoNumbersResultsInDecimalNumberThenAdding()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION).thenReturn(FIVE);
        calculator.getValues()[0] = "5.5";
        calculator.getValues()[1] = "2";
        calculator.setIsAdding(true);
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("7.5 +", calculator.getTextPaneValue(), "Expected textPane shows 7.5 +");
        assertTrue(calculator.isAdding(), "Expected isAdding to be true");

        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.isAdding(), "Expected isAdding to be true");
    }

    @Test
    public void testAddingTwoNumbersResultsInDecimalNumberThatIsMaxThenAdding()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION);
        calculator.getValues()[0] = "9999998";
        calculator.getValues()[1] = "2.5";
        calculator.setIsAdding(true);
        calculator.setValuesPosition(1);
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("1.00000005E7", calculator.getTextPaneValue(), "Expected textPane shows 1.00000005E7");
        assertFalse(calculator.isAdding(), "Expected isAdding to be false");
        assertEquals("(+) Result: 9,999,998 + 2.5 = 1.00000005E7",
                calculator.getBasicHistoryPaneTextWithoutNewLineCharacters(),
                "Expected history to not show continued operation");
    }

    @Test
    public void pressedAddThenDivideWhenNumberIsMaximum()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION);
        calculator.getValues()[0] = "9999998";
        calculator.getValues()[1] = TWO;
        calculator.setIsAdding(true);
        calculator.setValuesPosition(1);
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("1.0E7", calculator.getTextPaneValue(), "Expected textPane shows 1.0E7");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
    }

    @Test
    public void pressedSubtractWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION);
        calculator.performAdditionButtonAction(actionEvent);

        assertEquals(BLANK, calculator.getValues()[calculator.getValuesPosition()], "Values[{}] is not empty");
        assertEquals(ENTER_A_NUMBER, calculator.getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should display 'Enter a Number'");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    public void pressedMultiplyWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(MULTIPLICATION);
        calculator.performMultiplicationAction(actionEvent);

        assertEquals(BLANK, calculator.getValues()[calculator.getValuesPosition()], "Values[{}] is not empty");
        assertEquals(ENTER_A_NUMBER, calculator.getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should display 'Enter a Number'");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    public void pressedDivideWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION);
        calculator.performDivideButtonAction(actionEvent);

        assertEquals(BLANK, calculator.getValues()[calculator.getValuesPosition()], "Values[{}] is not empty");
        assertEquals(ENTER_A_NUMBER, calculator.getValueWithoutAnyOperator(calculator.getTextPaneValue()), "textPane should display 'Enter a Number'");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    public void testAdditionWithWholeNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.setIsAdding(true);
        calculator.getValues()[0] = FIVE;
        calculator.getValues()[1] = "10";
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("15", calculator.getTextPaneValue(), "Did not get back expected result");
    }

    @Test
    public void testAdditionWithDecimalNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.setIsAdding(true);
        calculator.getValues()[0] = "5.5";
        calculator.getValues()[1] = "10";
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("15.5", calculator.getTextPaneValue(), "Did not get back expected result");
    }

    @Test
    public void testSubtractionFunctionalityWithWholeNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.setIsSubtracting(true);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = "10";
        calculator.setValuesPosition(1);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getTextPaneValue(), "Did not get back expected result");
    }

    @Test
    public void testSubtractionFunctionalityWithDecimalNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.setIsSubtracting(true);
        calculator.getValues()[0] = "15.5";
        calculator.getValues()[1] = "10";
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("5.5", calculator.getTextPaneValue(), "Did not get back expected result");
    }

    @Test
    public void testMultiplicationFunctionalityWithWholeNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.setIsMultiplying(true);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = "10";
        calculator.setValuesPosition(1);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("150", calculator.getTextPaneValue(), "Did not get back expected result");
    }

    @Test
    public void testMultiplicationFunctionalityWithDecimalNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.setIsMultiplying(true);
        calculator.getValues()[0] = "5.5";
        calculator.getValues()[1] = "10.2";
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("56.1", calculator.getTextPaneValue(), "Did not get back expected result");
    }

    @Test
    public void testDivisionFunctionalityWithWholeNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.setIsDividing(true);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = FIVE;
        calculator.setCalculatorView(VIEW_BASIC);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(THREE, calculator.getTextPaneValue(), "Did not get back expected result");
    }

    @Test
    public void testDivisionFunctionalityWithDecimalNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.setIsDividing(true);
        calculator.getValues()[0] = "15.5";
        calculator.getValues()[1] = FIVE;
        calculator.setCalculatorView(VIEW_BASIC);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("3.1", calculator.getTextPaneValue(), "Did not get back expected result");
    }

    @Test
    public void testDivisionBy0() 
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.setIsDividing(true);
        calculator.getValues()[0] = "15.5";
        calculator.getValues()[1] = ZERO;
        calculator.setCalculatorView(VIEW_BASIC);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(BLANK, calculator.getValues()[0], "Expected values[0] to be blank");
        assertEquals(INFINITY, calculator.getTextPaneValue(), "Expected textPane to show error");
    }

    @Test
    public void pressedDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE);
        calculator.getTextPane().setText(calculator.addNewLines() + "35");
        calculator.getValues()[0] = "35";
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(3, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 3");
        assertEquals(THREE, calculator.getTextPaneValue(), "textPane does not equal 3");
    }

    @Test
    public void pressedDeleteWhenTextPaneHasBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE);
        calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to say " + ENTER_A_NUMBER);
        assertEquals(BLANK, calculator.getValues()[0], "Expected values[0] to be blank");
        assertEquals(BLANK, calculator.getValues()[1], "Expected values[1] to be blank");
        assertEquals(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        assertFalse(calculator.isAdding(), "Expected isAdding to be false");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
        assertFalse(calculator.isMultiplying(), "Expected isMultiplying to be false");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
        assertFalse(calculator.isNumberNegative(), "Expected isNegative to be false");
        assertTrue(calculator.isDotPressed(), "Expected dot to be enabled");
    }

    @Test
    public void testDeleteDoesNothingWhenNothingToDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE);
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show error");
        assertEquals(BLANK, calculator.getValues()[0], "Expected values[0] to be blank");
        assertEquals(BLANK, calculator.getValues()[1], "Expected values[1] to be blank");
        assertEquals(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        assertFalse(calculator.isAdding(), "Expected isAdding to be false");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
        assertFalse(calculator.isMultiplying(), "Expected isMultiplying to be false");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
        assertFalse(calculator.isNumberNegative(), "Expected isNegative to be false");
        assertTrue(calculator.isDotPressed(), "Expected dot to be enabled");
    }

    @Test
    public void testDeleteClearsTextPaneAfterPressingEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE).thenReturn(ADDITION)
                .thenReturn(THREE).thenReturn(EQUALS).thenReturn(DELETE);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(BLANK, calculator.getTextPaneValue(), "Expected textPane to be blank");
        assertEquals(BLANK, calculator.getValues()[0], "Expected values[0] to be blank");
        assertEquals(BLANK, calculator.getValues()[1], "Expected values[1] to be blank");
        assertEquals(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        assertFalse(calculator.isAdding(), "Expected isAdding to be false");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
        assertFalse(calculator.isMultiplying(), "Expected isMultiplying to be false");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
        assertFalse(calculator.isNumberNegative(), "Expected isNegative to be false");
        assertTrue(calculator.isDotPressed(), "Expected dot to be enabled");
    }

    @Test
    public void pressed1Then5ThenDecimalThen6ThenAddThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE).thenReturn(DELETE).thenReturn(DELETE);
        calculator.getTextPane().setText(calculator.addNewLines() + "15.6 +");
        calculator.getValues()[0] = "15.6";
        //calculator.setDotPressed(true);
        calculator.getButtonDecimal().setEnabled(false);
        calculator.setIsAdding(true);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(15.6, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 15.6");
        assertEquals("15.6", calculator.getTextPaneValue(), "textPane does not equal 15.6");
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be disabled");
        assertFalse(calculator.isAdding(), "Expected isAdding to be false");

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
    public void pressed1ThenSubtractThenDelete()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE)
                .thenReturn(SUBTRACTION)
                .thenReturn(DELETE);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractionButtonAction(actionEvent);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(1, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane does not equal 1");
        assertTrue(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be enabled");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
    }

    @Test
    public void pressedSubtractThen5ThenSubtractThenSubtractThen6ThenEquals()
    {
        // -5 - 6 =
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = "-5";
        calculator.getValues()[1] = "-6";
        calculator.getTextPane().setText(calculator.addNewLines() + "-6");
        calculator.setIsNumberNegative(true);
        calculator.setIsSubtracting(true);
        calculator.setValuesPosition(1);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(BLANK, calculator.getValues()[0], "Values[0] is not blank");
        assertEquals("1", calculator.getTextPaneValue(), "textPane does not equal 1");
        assertTrue(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be enabled");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
    }

    @Test
    public void pressedSubtractThen5ThenSubtractThen5()
    {
        // -5 - -5 =
        when(actionEvent.getActionCommand())
                .thenReturn(SUBTRACTION).thenReturn(FIVE)
                .thenReturn(SUBTRACTION)
                .thenReturn(SUBTRACTION).thenReturn(FIVE)
                .thenReturn(EQUALS);
        calculator.performSubtractionButtonAction(actionEvent);
        assertTrue(calculator.isNumberNegative(), "Expected isNumberNegative to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("-5", calculator.getValues()[0], "Values[0] is not -5");
        assertEquals("-5", calculator.getTextPaneValue(), "textPane does not equal -5");
        calculator.performSubtractionButtonAction(actionEvent);
        assertFalse(calculator.isNumberNegative(), "Expected isNumberNegative to be false");
        assertTrue(calculator.isSubtracting(), "Expected isSubtracting to be true");
        calculator.performSubtractionButtonAction(actionEvent);
        assertTrue(calculator.isNumberNegative(), "Expected isNumberNegative to be true");
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(-5, Double.parseDouble(calculator.getValues()[1]), delta, "Values[1] is not -5");
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(BLANK, calculator.getValues()[0], "Values[0] is blank");
        assertEquals(ZERO, calculator.getTextPaneValue(), "textPane does not equal 0");
    }

    @Test
    public void pressed1ThenMultiplyThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE);
        calculator.getTextPane().setText(calculator.addNewLines() + "1 ✕");
        calculator.getValues()[0] = ONE;
        calculator.getButtonDecimal().setEnabled(false);
        calculator.setIsMultiplying(true);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(1, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane does not equal 1");
        assertTrue(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be enabled");
        assertFalse(calculator.isMultiplying(), "Expected isMultiplying to be false");
    }

    @Test
    public void pressed1ThenDivideThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE);
        calculator.getTextPane().setText(calculator.addNewLines() + "1 ÷");
        calculator.getValues()[0] = ONE;
        calculator.getButtonDecimal().setEnabled(false);
        calculator.setIsDividing(true);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(1, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "textPane does not equal 1");
        assertTrue(calculator.getButtonDecimal().isEnabled(), "Expected decimal button to be enabled");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
    }

    @Test
    public void enteredANumberThenAddThen6DeleteThen5()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE).thenReturn(FIVE);
        calculator.getTextPane().setText(calculator.addNewLines() + SIX);
        calculator.getValues()[0] = "15.6";
        calculator.getValues()[1] = SIX;
        calculator.setValuesPosition(1);
        calculator.getButtonDecimal().setEnabled(true);
        calculator.setIsAdding(true);
        assertEquals(6, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not 6");

        calculator.performDeleteButtonAction(actionEvent);
        assertEquals(15.6, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 15.6");
        assertEquals(BLANK, calculator.getTextPaneValue(), "textPane is not blank");
        assertTrue(calculator.isDotPressed(), "Expected decimal button is enabled");
        assertTrue(calculator.isAdding(), "Expected isAdding to be true");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[1]), "Values[1] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane does not equal blank");
        assertTrue(calculator.isDotPressed(), "Expected decimal button is enabled");
        assertTrue(calculator.isAdding(), "Expected isAdding to be true");
    }

    @Test
    public void pressingClearRestoresCalculatorToStartFunctionality()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR);
        calculator.performClearButtonAction(actionEvent);
        assertEquals(ZERO, calculator.getValues()[0], "Values[0] should be 0");
        for ( int i=1; i<3; i++) {
            assertTrue(calculator.getValues()[i].isBlank(), "Values["+i+"] is not blank");
        }
        assertEquals(ZERO, calculator.getTextPaneValue(), "textPane is not 0");
        assertFalse(calculator.isAdding(), "isAddBool() is not false");
        assertFalse(calculator.isSubtracting(), "isSubBool() is not false");
        assertFalse(calculator.isMultiplying(), "isMulBool() is not false");
        assertFalse(calculator.isDividing(), "isDivBool() is not false");
        assertEquals(0, calculator.getValuesPosition(), "Values position is not 0");
        assertTrue(calculator.isFirstNumber(), "FirstNumBool is not true");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
    }

    @Test
    public void pressingClearEntryWhenOnValuesPosition0ClearsTextPaneAndMainOperatorsAndDecimal()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR_ENTRY);
        calculator.getTextPane().setText(calculator.addNewLines() + "1088");
        calculator.setValuesPosition(0);
        calculator.getValues()[0] = "1088";
        calculator.performClearEntryButtonAction(actionEvent);

        assertTrue(StringUtils.isBlank(calculator.getTextPaneValue()), "textPane was not cleared");
        assertFalse(calculator.isAdding(), "Expected isAdding to be false");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
        assertFalse(calculator.isMultiplying(), "Expected isMultiplying to be false");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
        assertTrue(calculator.isDotPressed(), "Expected decimal button to be enabled");
        assertTrue(calculator.isFirstNumber(), "Expected to be on the firstNumber");
    }

    @Test
    public void pressingClearEntry()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR_ENTRY).thenReturn(CLEAR_ENTRY);
        calculator.getTextPane().setText(calculator.addNewLines() + "1088");
        calculator.setIsAdding(true);
        calculator.setValuesPosition(1);
        calculator.getValues()[0] = "100";
        calculator.getValues()[1] = "1088";

        calculator.performClearEntryButtonAction(actionEvent);
        assertEquals("100", calculator.getValues()[0], "Expected textPane to show values[0]");
        assertTrue(calculator.isAdding(), "Expected isAdding to be true");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
        assertFalse(calculator.isMultiplying(), "Expected isMultiplying to be false");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
        assertTrue(calculator.isDotPressed(), "Expected decimal button to be enabled");
        assertFalse(calculator.isFirstNumber(), "Expected not to be on the firstNumber");
        assertEquals(1, calculator.getValuesPosition(), "Expected to be at vP[1]");

        calculator.performClearEntryButtonAction(actionEvent);
        assertEquals(BLANK, calculator.getValues()[0], "Expected textPane to show values[0]");
        assertFalse(calculator.isAdding(), "Expected isAdding to be false");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
        assertFalse(calculator.isMultiplying(), "Expected isMultiplying to be false");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
        assertTrue(calculator.isDotPressed(), "Expected decimal button to be enabled");
        assertTrue(calculator.isFirstNumber(), "Expected to be on the firstNumber");
        assertEquals(0, calculator.getValuesPosition(), "Expected to be at vP[0]");

    }

    @Test
    public void pressingClearEntryAfterPressingAnOperatorResetsTextPaneAndOperator()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR_ENTRY);
        calculator.getTextPane().setText(calculator.addNewLines() + "1088 +");
        calculator.performClearEntryButtonAction(actionEvent);

        assertTrue(StringUtils.isBlank(calculator.getValueWithoutAnyOperator(calculator.getTextPaneValue())), "textPane was not cleared");
        assertFalse(calculator.isAdding(), "isAdding() expected to be false");
    }

    @Test
    public void pressedClearEntryWhenTextPaneIsEmpty()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR_ENTRY);
        calculator.performClearEntryButtonAction(actionEvent);
        assertEquals(BLANK, calculator.getValues()[0], "Expected textPane to be blank");
        assertFalse(calculator.isAdding(), "Expected isAdding to be false");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
        assertFalse(calculator.isMultiplying(), "Expected isMultiplying to be false");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
        assertTrue(calculator.isDotPressed(), "Expected decimal button to be enabled");
        assertTrue(calculator.isFirstNumber(), "Expected to be on the firstNumber");
        assertEquals(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
    }

    @Test
    public void pressedSquaredButtonWithNoEntry()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARED);
        basicPanel.performSquaredButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to contain message");
        assertTrue(calculator.getValues()[0].isEmpty(), "Expected values[0] to be empty");
    }

    @Test
    public void pressedSquaredButtonWithBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARED);
        calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER);
        basicPanel.performSquaredButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show bad text");
    }

    @Test
    public void pressed5ThenSquaredButton()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE).thenReturn(SQUARED);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane should be 5");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        basicPanel.performSquaredButtonAction(actionEvent);
        assertEquals(25, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 25");
        assertEquals("25", calculator.getTextPaneValue(), "textPane should be 25");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");
    }

    @Test
    public void pressed5ThenDecimalThen0ThenSquaredButton()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE).thenReturn(DECIMAL)
                .thenReturn(FIVE).thenReturn(SQUARED);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDecimalButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(5.5, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 5.5"); // 0.0
        assertEquals("5.5", calculator.getTextPaneValue(), "textPane should be 5.5");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        basicPanel.performSquaredButtonAction(actionEvent);
        assertEquals(30.25, Double.parseDouble(calculator.getValues()[0]), delta, "Values[0] is not 30.25");
        assertEquals("30.25", calculator.getTextPaneValue(), "textPane should be 30.25");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");
        assertFalse(calculator.isDotPressed(), "Expected decimal button to be disabled");
    }

    @Test
    public void pressed5ThenNegateThenSquaredThenNegate()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(FIVE).thenReturn(NEGATE)
                .thenReturn(SQUARED).thenReturn(NEGATE);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane should be 5");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performNegateButtonAction(actionEvent);
        assertEquals(-5, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not -5");
        assertEquals("-5", calculator.getTextPaneValue(), "textPane should be -5");
        assertTrue(calculator.isNumberNegative(), "Expected isNumberNegative to be true");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        basicPanel.performSquaredButtonAction(actionEvent);
        assertEquals(25, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 25");
        assertEquals("25", calculator.getTextPaneValue(), "textPane should be 25");
        assertFalse(calculator.isNumberNegative(), "Expected isNumberNegative to be false");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performNegateButtonAction(actionEvent);
        assertEquals(-25, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not -25");
        assertEquals("-25", calculator.getTextPaneValue(), "textPane should be -25");
        assertTrue(calculator.isNumberNegative(), "Expected isNumberNegative to be true");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

    }

    @Test
    public void pressed5ThenNegateThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE).thenReturn(NEGATE).thenReturn(NEGATE);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane should be 5");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");

        calculator.performNegateButtonAction(actionEvent);
        assertEquals(-5, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not -5");
        assertEquals("-5", calculator.getTextPaneValue(), "textPane should be -5");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");
        assertTrue(calculator.isNumberNegative(), "Expected number to be negative");

        calculator.performNegateButtonAction(actionEvent);
        assertEquals(5, Integer.parseInt(calculator.getValues()[0]), "Values[0] is not 5");
        assertEquals(FIVE, calculator.getTextPaneValue(), "textPane should be 5");
        assertTrue(calculator.isFirstNumber(), "We are not on the firstNumber");
        assertFalse(calculator.isNumberNegative(), "Expected number to be positive");
    }

    @Test
    public void pressedNegateOnTooBigNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(NEGATE);
        calculator.getTextPane().setText(calculator.addNewLines() + ERROR);
        calculator.performNegateButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    @Test
    public void pressedNegateWhenNoValueInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(NEGATE);
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK);
        calculator.performNegateButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected error message in textPane");
    }

    @Test
    public void pressedSquareRootWithTextPaneShowingE()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT);
        calculator.getTextPane().setText(calculator.addNewLines() + ERROR);
        calculator.performSquareRootButtonAction(actionEvent);
        assertEquals(ERROR, calculator.getTextPaneValue(), "Expected textPane to show error");
    }

    @Test
    public void pressedSquareRootWithTextPaneEmpty()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT);
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK);
        calculator.performSquareRootButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show error message");
    }

    @Test
    public void pressedSquareRootWhenCurrentValueIsNegative()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT);
        calculator.getTextPane().setText(calculator.addNewLines() + "-5");
        calculator.getValues()[0] = "-5;";
        calculator.setValuesPosition(0);
        calculator.performSquareRootButtonAction(actionEvent);
        assertEquals(ONLY_POSITIVES, calculator.getTextPaneValue(), "Expected textPane to show error message");
    }

    @Test
    public void pressedSquareRootWithWholeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT);
        calculator.getTextPane().setText(calculator.addNewLines() + "25");
        calculator.getValues()[0] = "25";
        calculator.setValuesPosition(0);
        calculator.performSquareRootButtonAction(actionEvent);
        assertEquals(5.0, Double.parseDouble(calculator.getTextPaneValue()), delta, "Expected result to be 5.0");
    }

    @Test
    public void pressedSquareRootWithDecimalNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT);
        calculator.getTextPane().setText(calculator.addNewLines() + "25.5");
        calculator.getValues()[0] = "25.5";
        calculator.setValuesPosition(0);
        calculator.performSquareRootButtonAction(actionEvent);
        assertEquals(5.05, Double.parseDouble(calculator.getTextPaneValue()), delta, "Expected result to be 5.05");
        assertFalse(calculator.isDotPressed(), "Expected dot to be disable");
    }

    @Test
    public void pressedMemoryStoreWhenTextPaneIsBlank()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_STORE);
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK);
        calculator.performMemoryStoreAction(actionEvent);
        assertTrue(calculator.getMemoryValues()[0].isBlank(), "Expected MemoryValues[0] to be blank");
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show Enter a Number");
    }

    @Test
    public void pressedMemoryStoreWhenTextPaneContainsValue()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_STORE);
        calculator.getTextPane().setText(calculator.addNewLines() + FIVE);
        calculator.performMemoryStoreAction(actionEvent);
        assertEquals(FIVE, calculator.getTextPaneValue(), "Expected textPane to still show 5");
        assertEquals(FIVE, calculator.getMemoryValues()[0], "Expected MemoryValues[0] to be 5");
        assertSame(1, calculator.getMemoryPosition(), "Expected MemoryPosition to be 1");
    }

    @Test
    public void pressedMemoryStoreWhenMemoryIsFullOverwritesMemory()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_STORE);
        calculator.getTextPane().setText(calculator.addNewLines() + TWO);
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(10);
        assertEquals("15", calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 15");
        calculator.performMemoryStoreAction(actionEvent);
        assertEquals(TWO, calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 2");
    }

    @Test
    public void pressedMemoryStoreWhenTextPaneContainInvalidText()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_STORE);
        calculator.getTextPane().setText(calculator.addNewLines() + ERROR);
        assertTrue(calculator.textPaneContainsBadText(), "Expected textPane to contain bad text");
        calculator.performMemoryStoreAction(actionEvent);
        assertTrue(calculator.getMemoryValues()[0].isBlank(), "Expected MemoryValues[0] to be blank");
    }

    @Test
    public void pressedMemoryRecall()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_RECALL).thenReturn(MEMORY_RECALL)
                .thenReturn(MEMORY_RECALL).thenReturn(MEMORY_RECALL);
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
    public void pressedMemoryClear()
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

    @Test
    public void pressed5ThenMemoryAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION);
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(calculator.addNewLines() + FIVE);
        calculator.setValuesPosition(0);
        calculator.performMemoryAdditionAction(actionEvent);
        assertEquals("15", calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 15");
        assertSame(1, calculator.getMemoryPosition(), "Expected memoryPosition to be 1");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals(FIVE, calculator.getTextPaneValue(), "Expected textPane to show 5");
    }

    @Test
    public void testSavingAWholeNumberWithMemoryAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION);
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK);
        calculator.setValuesPosition(0);
        calculator.performMemoryAdditionAction(actionEvent);
        assertEquals("10", calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 10");
        assertSame(1, calculator.getMemoryPosition(), "Expected memoryPosition to be 1");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show error");
    }

    @Test
    public void testSavingADecimalNumberWithMemoryAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION);
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(calculator.addNewLines() + "5.5");
        calculator.setValuesPosition(0);
        calculator.performMemoryAdditionAction(actionEvent);
        assertEquals("15.5", calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 15.5");
        assertSame(1, calculator.getMemoryPosition(), "Expected memoryPosition to be 1");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals("5.5", calculator.getTextPaneValue(), "Expected textPane to show 5.5");
    }

    @Test
    public void testMemoryAddFailsWhenSavingBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION);
        calculator.getMemoryValues()[0] = BLANK;
        calculator.setMemoryPosition(0);
        calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER);
        calculator.setValuesPosition(0);
        calculator.performMemoryAdditionAction(actionEvent);
        assertEquals(BLANK, calculator.getMemoryValues()[0], "Expected memoryValues[0] to be blank");
        assertSame(0, calculator.getMemoryPosition(), "Expected memoryPosition to be 0");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show error");
    }

    @Test
    public void pressed5ThenMemorySub()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_SUBTRACTION);
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(calculator.addNewLines() + FIVE);
        calculator.setValuesPosition(0);
        calculator.performMemorySubtractionAction(actionEvent);
        assertEquals("10", calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 10");
        assertSame(1, calculator.getMemoryPosition(), "Expected memoryPosition to be 1");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals(FIVE, calculator.getTextPaneValue(), "Expected textPane to show 5");
    }

    @Test
    public void testSavingAWholeNumberWithMemorySubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_SUBTRACTION);
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK);
        calculator.setValuesPosition(0);
        calculator.performMemorySubtractionAction(actionEvent);
        assertEquals("15", calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 15");
        assertSame(1, calculator.getMemoryPosition(), "Expected memoryPosition to be 1");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show error");
    }

    @Test
    public void testSavingADecimalNumberWithMemorySubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION);
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(calculator.addNewLines() + "5.5");
        calculator.setValuesPosition(0);
        calculator.performMemorySubtractionAction(actionEvent);
        assertEquals("4.5", calculator.getMemoryValues()[0], "Expected memoryValues[0] to be 4.5");
        assertSame(1, calculator.getMemoryPosition(), "Expected memoryPosition to be 1");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals("5.5", calculator.getTextPaneValue(), "Expected textPane to show 5.5");
    }

    @Test
    public void testMemorySubFailsWhenSavingBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION);
        calculator.getMemoryValues()[0] = BLANK;
        calculator.setMemoryPosition(0);
        calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER);
        calculator.setValuesPosition(0);
        calculator.performMemorySubtractionAction(actionEvent);
        assertEquals(BLANK, calculator.getMemoryValues()[0], "Expected memoryValues[0] to be blank");
        assertSame(0, calculator.getMemoryPosition(), "Expected memoryPosition to be 0");
        assertSame(0, calculator.getMemoryRecallPosition(), "Expected memoryRecallPosition to be 0");
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show error");
    }

    @Test
    public void testOpeningHistory()
    {
        when(actionEvent.getActionCommand()).thenReturn(HISTORY_OPEN);
        basicPanel.performHistoryAction(actionEvent);
    }

    @Test
    public void testClosingHistory()
    {
        when(actionEvent.getActionCommand()).thenReturn(HISTORY_OPEN);
        when(actionEvent.getActionCommand()).thenReturn(HISTORY_CLOSED);
        basicPanel.performHistoryAction(actionEvent);
        basicPanel.performHistoryAction(actionEvent);
    }

    @Test
    public void pressedFractionWhenTextPaneContainsBadText()
    {
        calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER);
        basicPanel.performFractionButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show bad text");
    }

    @Test
    public void pressed0ThenFraction()
    {
        when(actionEvent.getActionCommand()).thenReturn(ZERO).thenReturn(FRACTION);
        calculator.performNumberButtonAction(actionEvent);
        basicPanel.performFractionButtonAction(actionEvent);
        assertEquals(INFINITY, calculator.getTextPaneValue(), "Expected textPane to be Infinity");
        assertEquals(BLANK, calculator.getValues()[0], "Expected values[0] to be blank");
    }

    @Test
    public void pressedFractionWhenTextPaneIsBlank()
    {
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK);
        basicPanel.performFractionButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show bad text");
    }

    @Test
    public void pressed5ThenFraction()
    {
        when(actionEvent.getActionCommand()).thenReturn(FRACTION);
        calculator.getTextPane().setText(calculator.addNewLines() + FIVE);
        basicPanel.performFractionButtonAction(actionEvent);
        assertEquals("0.2", calculator.getTextPaneValue(), "Expected textPane to be 0.2");
        assertEquals("0.2", calculator.getValues()[0], "Expected values[0] to be 0.2");
        assertFalse(calculator.isDotPressed(), "Expected decimal button to be disabled");
    }

    @Test
    public void pressedPercentWhenTextPaneContainsBadText()
    {
        calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER);
        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show bad text");
    }

    @Test
    public void pressedPercentWhenTextPaneIsBlank()
    {
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK);
        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to show bad text");
    }

    @Test
    public void pressedPercentWithPositiveNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE).thenReturn(PERCENT);
        calculator.performNumberButtonAction(actionEvent);
        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals("0.05", calculator.getTextPaneValue(), "Expected textPane to be 0.05");
        assertEquals("0.05", calculator.getValues()[0], "Expected values[0] to be 0.05");
        assertFalse(calculator.isDotPressed(), "Expected decimal to be disabled");
    }

    @Test
    public void pressedPercentWithNegativeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION)
                .thenReturn(FIVE).thenReturn(PERCENT);
        calculator.performSubtractionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals("-0.05", calculator.getTextPaneValue(), "Expected textPane to be -0.05");
        assertEquals("-0.05", calculator.getValues()[0], "Expected values[0] to be -0.05");
        assertFalse(calculator.isDotPressed(), "Expected decimal to be disabled");
    }

    @Test
    public void pressed1Then0Then6Then5ThenDecimalThen5Then4Then5Then7()
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
    public void pressed0WhenTextPaneHasBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(ZERO);
        calculator.getTextPane().setText(calculator.addNewLines() + INFINITY);
        calculator.setIsFirstNumber(false);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ZERO, calculator.getValues()[0], "Expected values[0] to be 0");
        assertEquals(ZERO, calculator.getTextPaneValue(), "Expected textPane to show 0");
    }

    @Test
    public void pressedDecimalWhenTextPaneHasBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL);
        calculator.getTextPane().setText(calculator.addNewLines() + INFINITY);
        calculator.performDecimalButtonAction(actionEvent);
        assertEquals(BLANK, calculator.getValues()[0], "Expected values[0] to be blank");
        assertEquals(INFINITY, calculator.getTextPaneValue(), "Expected textPane to show Infinity");
    }

    @Test
    public void pressedANumberWhenMaxLengthHasBeenMet()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE);
        calculator.getTextPane().setText(calculator.addNewLines() + "4,500,424");
        calculator.getValues()[0] = "4500424";
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("45,004,245", calculator.getTextPaneValue(), "Expected textPane to be the same");
        assertEquals("45004245", calculator.getValues()[0], "Expected values[0] to be the same");
    }

    @Test
    public void pressedPercentThenFraction()
    {
        // starting panel has 25
        // pressed percent returns 0.25
        // pressed fraction returns 4.0, should show 4
        when(actionEvent.getActionCommand()).thenReturn(TWO).thenReturn(FIVE)
                .thenReturn(PERCENT).thenReturn(FRACTION);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(TWO, calculator.getTextPaneValue(), "Expected textPane to be 2");
        assertEquals(TWO, calculator.getValues()[0], "Expected values[0] to be 2");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals("25", calculator.getTextPaneValue(), "Expected textPane to be 25");
        assertEquals("25", calculator.getValues()[0], "Expected values[0] to be 25");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");

        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals("0.25", calculator.getTextPaneValue(), "Expected textPane to be 0.25");
        assertEquals("0.25", calculator.getValues()[0], "Expected values[0] to be 0.25");
        assertFalse(calculator.isDotPressed(), "Expected decimal to be disabled");

        basicPanel.performFractionButtonAction(actionEvent);
        assertEquals(FOUR, calculator.getTextPaneValue(), "Expected textPane to be 4");
        assertEquals(FOUR, calculator.getValues()[0], "Expected values[0] to be 4");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
    }

    @Test
    public void pressed1ThenAddThen2ThenEqualsThen5()
    {
        // 1 + 2 = 3
        // Pressing 5 Starts new Equation, new number
        when(actionEvent.getActionCommand()).thenReturn(ONE).thenReturn(ADDITION)
                .thenReturn(TWO).thenReturn(EQUALS).thenReturn(FIVE);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        assertEquals(ONE, calculator.getTextPaneValue(), "Expected textPane to be 1");

        calculator.performAdditionButtonAction(actionEvent);
        assertEquals(ONE, calculator.getValues()[0], "Expected values[0] to be 1");
        assertEquals("1 +", calculator.getTextPaneValue(), "Expected textPane to be 1 +");
        assertTrue(calculator.isAdding(), "Expected isAdding to be true");
        assertEquals(1, calculator.getValuesPosition(), "Expected valuesPosition to be 1");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(TWO, calculator.getValues()[1], "Expected values[1] to be 2");
        assertEquals(TWO, calculator.getTextPaneValue(), "Expected textPane to be 2");

        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(BLANK, calculator.getValues()[0], "Expected values[0] to be blank");
        assertEquals(BLANK, calculator.getValues()[1], "Expected values[1] to be blank");
        assertEquals(THREE, calculator.getTextPaneValue(), "Expected textPane to be 3");
        assertFalse(calculator.isAdding(), "Expected isAdding to be false");

        calculator.performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[0], "Expected values[0] to be 5");
        assertEquals(BLANK, calculator.getValues()[1], "Expected values[1] to be blank");
        assertEquals(FIVE, calculator.getTextPaneValue(), "Expected textPane to be 5");

    }

    @Test
    public void pressed4Then5ThenMSThenClearThen5ThenMAdd()
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
    public void pressed4Then5ThenMSThenClearThen5ThenMSub()
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
    public void pressingNumberEnablesDecimal()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE);
        calculator.getValues()[0] = "10.35";
        calculator.getButtonDecimal().setEnabled(false);
        calculator.setIsAdding(true);
        calculator.setIsFirstNumber(false);
        calculator.performNumberButtonAction(actionEvent);
        assertFalse(calculator.isDotPressed(), "Expected decimal to be enabled");
    }

    @Test
    public void testContinuedDivisionAfterAttemptToDivideByZero()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION);
        calculator.getValues()[0] = "1";
        calculator.setIsDividing(true);
        calculator.getValues()[1] = "0";
        calculator.getTextPane().setText(calculator.getValues()[1]);
        calculator.performDivideButtonAction(actionEvent);
        assertEquals(INFINITY, calculator.getTextPaneValue(), "Expected textPane to show error");
        assertEquals(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
    }

    @Test
    public void testContinuedDivisionWithWholeNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION);
        calculator.getValues()[0] = "10";
        calculator.setIsDividing(true);
        calculator.getValues()[1] = "2";
        calculator.setValuesPosition(1);
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("5 ÷", calculator.getTextPaneValue(), "Expected textPane to show 5 ÷");
        assertEquals("5", calculator.getValues()[0], "Expected values[0] to be 5");
        assertTrue(calculator.isDividing(), "Expected isDividing to be true");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
    }

    @Test
    public void testPressedEqualsPrematurelyWithAddition()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = SIX;
        calculator.setIsAdding(true);
        calculator.performEqualsButtonAction(actionEvent);
        assertTrue(calculator.isAdding(), "Expected isAdding to be true");
        assertEquals(SIX, calculator.getValues()[0], "Expected textPane to be 6");
        assertFalse(calculator.isNumberNegative(), "Expected isNegative to now be false");
    }

    @Test
    public void testPressedEqualsPrematurelyWithMultiplication()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = SIX;
        calculator.setIsMultiplying(true);
        calculator.performEqualsButtonAction(actionEvent);
        assertTrue(calculator.isMultiplying(), "Expected isMultiplying to be true");
        assertEquals(SIX, calculator.getValues()[0], "Expected textPane to be 6");
        assertFalse(calculator.isNumberNegative(), "Expected isNegative to now be false");
    }

    @Test
    public void testPressedEqualsPrematurelyWithDivision()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = SIX;
        calculator.setIsDividing(true);
        calculator.performEqualsButtonAction(actionEvent);
        assertTrue(calculator.isDividing(), "Expected isDividing to be true");
        assertEquals(SIX, calculator.getValues()[0], "Expected textPane to be 6");
        assertFalse(calculator.isNumberNegative(), "Expected isNegative to now be false");
    }

    @Test
    public void testPressedEqualsPrematurelyWithSubtraction()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS);
        calculator.getValues()[0] = "-6";
        calculator.setIsSubtracting(true);
        calculator.performEqualsButtonAction(actionEvent);
        assertTrue(calculator.isSubtracting(), "Expected isSubtracting to be true");
        assertEquals("-6", calculator.getValues()[0], "Expected textPane to be -6");
        assertFalse(calculator.isNumberNegative(), "Expected isNegative to now be false");
    }

    @Test
    public void testingMathPow() {
        double delta = 0.000001d;
        Number num = 8.0;
        assertEquals(num.doubleValue(), Math.pow(2,3), delta);
    }
}

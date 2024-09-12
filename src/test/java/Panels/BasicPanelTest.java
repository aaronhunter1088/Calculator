package Panels;

import Calculators.Calculator;
import Calculators.CalculatorError;
import Calculators.CalculatorTests;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.ParseException;

import static Types.CalculatorView.VIEW_BASIC;
import static Types.Texts.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BasicPanelTest
{
    static { System.setProperty("appName", "BasicPanelTest"); }
    private static Logger LOGGER;
    private static Calculator calculator;
    private static BasicPanel basicPanel;
    private final double delta = 0.000001d;

    @Mock
    ActionEvent actionEvent;

    @BeforeClass
    public static void beforeAll()
    { LOGGER = LogManager.getLogger(BasicPanelTest.class.getSimpleName()); }

    @AfterClass
    public static void afterAll()
    { LOGGER.info("Finished running " + CalculatorTests.class.getSimpleName()); }

    @Before
    public void beforeEach() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        LOGGER.info("setting up each before...");
        MockitoAnnotations.initMocks(this);
        calculator = new Calculator(VIEW_BASIC);
        basicPanel = (BasicPanel) calculator.getCurrentPanel();
    }

    @After
    public void afterEach() {}

    @Test
    public void testCreatingBasicPanel()
    {
        assertEquals("Expected panel name to be " + VIEW_BASIC.getValue(), VIEW_BASIC.getValue(), new BasicPanel().getName());
    }

    @Test
    public void testCreatingBasicPanelWithCalculator()
    {
        assertEquals("Expected panel name to be " + VIEW_BASIC.getValue(), VIEW_BASIC.getValue(), basicPanel.getName());
    }

    @Test
    public void testShowingHelpShowsText()
    {
        calculator.showHelpPanel("This is a test. Close to proceed.");
    }

    @Test
    public void pressed1()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue());
        calculator.performNumberButtonAction(actionEvent);

        assertEquals("Values[{}] is not 1", 1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneWithoutAnyOperator());
        assertTrue("{} is not positive", calculator.isPositiveNumber(calculator.getTextPaneWithoutAnyOperator()));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(FIVE.getValue());
        calculator.performNumberButtonAction(actionEvent);

        assertEquals("Values[{}] is not 1", 1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneValue());
        assertTrue("{} is not positive", calculator.isPositiveNumber(String.valueOf(calculator.getValues()[calculator.getValuesPosition()])));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performNumberButtonAction(actionEvent);

        assertEquals("Values[{}] is not 15", 15, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneValue());
        assertTrue("{} is not positive", calculator.isPositiveNumber(String.valueOf(calculator.getValues()[calculator.getValuesPosition()])));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(NEGATE.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNegateButtonAction(actionEvent);

        assertEquals("Values[{}] is not -1", -1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("textPane should be -1", "-1", calculator.getTextPaneValue());
        assertTrue("{} is not negative", calculator.isNegativeNumber(calculator.getValues()[calculator.getValuesPosition()]));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenNegateThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(NEGATE.getValue()).thenReturn(ADDITION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNegateButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);

        assertEquals("Values[0] is not -1", -1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -1 +", "-1 +", calculator.getTextPaneValue());
        assertTrue("Expecting isAdding to be set", calculator.isAdding());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5ThenNegateThenAddThen5ThenNegateThenEquals()
    {
        // -15 + -5 =
        when(actionEvent.getActionCommand())
                .thenReturn(ONE.getValue()).thenReturn(FIVE.getValue())
                .thenReturn(NEGATE.getValue()).thenReturn(ADDITION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(NEGATE.getValue())
                .thenReturn(EQUALS.getValue());

        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneValue());
        assertSame("ValuesPosition should be 0", 0, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneValue());
        assertSame("ValuesPosition should be 0", 0, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performNegateButtonAction(actionEvent);
        assertEquals("Values[0] is not -15", -15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -15", "-15", calculator.getTextPaneValue());
        assertSame("ValuesPosition should be 0", 0, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("Values[0] is not -15", -15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -15 +", "-15 +", calculator.getTextPaneValue());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertTrue("Expecting isAdding to be set", calculator.isAdding());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
        assertFalse("Expecting isNumberNegative to be false", calculator.isNumberNegative());

        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[1] is not 5", 5, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be 5", FIVE.getValue(), calculator.getTextPaneValue());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performNegateButtonAction(actionEvent);
        assertEquals("Values[1] is not -5", -5, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be -5", "-5", calculator.getTextPaneValue());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("textPane is not -20", "-20", calculator.getTextPaneValue());
        assertEquals("Values[1] is not empty", BLANK.getValue(), calculator.getValues()[1]);
        assertEquals("textPane should be -20", "-20", calculator.getTextPaneValue());
        assertFalse("Expected firstNumber to be false", calculator.isFirstNumber());
    }

    @Test
    public void pressedDecimal()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL.getValue());
        calculator.getValues()[0] = BLANK.getValue();
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK.getValue());
        calculator.performDecimalButtonAction(actionEvent);
        assertEquals("textPane is not as expected", "0.", calculator.getTextPaneValue());
        assertFalse("buttonDot should be disabled", calculator.getButtonDecimal().isEnabled());
    }

    @Test
    public void pressed544ThenDecimal()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + "544");
        calculator.getValues()[0] = "544";
        calculator.performDecimalButtonAction(actionEvent);
        assertEquals("textPane is not as expected", "544.", calculator.getTextPaneValue());
        assertEquals("Values[0]", "544.", calculator.getValues()[0]);
        assertFalse("buttonDot should be disabled", calculator.getButtonDecimal().isEnabled());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then2Then3Then4ThenDecimal()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(TWO.getValue()).thenReturn(THREE.getValue())
                .thenReturn("4").thenReturn(DECIMAL.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDecimalButtonAction(actionEvent);
        assertEquals("textPane is not as expected", "1,234.", calculator.getTextPaneValue());
        assertEquals("Values[0]", "1234.", calculator.getValues()[0]);
        assertFalse("Expected decimal to be disabled", calculator.getButtonDecimal().isEnabled());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedDecimalThen9()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL.getValue()).thenReturn(NINE.getValue());
        calculator.performDecimalButtonAction(actionEvent);

        assertEquals("Values[0] is not 0.", "0.", calculator.getValues()[0]);
        assertEquals("textPane should be 0.", "0.", calculator.getTextPaneValue());
        assertFalse("Expected dot to be disabled", calculator.getButtonDecimal().isEnabled());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performNumberButtonAction(actionEvent);
        if (calculator.isNegativeNumber(calculator.getValues()[0])) fail("Number is negative");

        assertEquals( 0.9f, Double.parseDouble(calculator.getValues()[0]), delta); // 0.8999999761581421
        assertEquals("textPane should be 0.9", "0.9", calculator.getTextPaneValue());
        assertFalse("Expecting decimal to be disabled", calculator.isDotPressed());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenDecimalThen5ThenAddThen2()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(DECIMAL.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(ADDITION.getValue()).thenReturn(TWO.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDecimalButtonAction(actionEvent);
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        assertTrue("Expected decimal button to be enabled", calculator.getButtonDecimal().isEnabled());
    }

    @Test
    public void pressed1ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(ADDITION.getValue());
        calculator.performNumberButtonAction(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1 +", "1 +", calculator.getTextPaneValue());
        assertTrue("Expecting isAdding to be set", calculator.isAdding());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(FIVE.getValue()).thenReturn(ADDITION.getValue());
        calculator.performNumberButtonAction(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performNumberButtonAction(actionEvent);

        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performAdditionButtonAction(actionEvent);

        assertEquals("Values[0] is not 1", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15 +", "15 +", calculator.getTextPaneValue());
        assertTrue("Expecting isAdding to be set", calculator.isAdding());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(SUBTRACTION.getValue());
        calculator.performNumberButtonAction(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performSubtractionButtonAction(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1 -", "1 -", calculator.getTextPaneValue());
        assertTrue("Expecting isSubtracting to be set", calculator.isSubtracting());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(FIVE.getValue()).thenReturn(SUBTRACTION.getValue());
        calculator.performNumberButtonAction(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performNumberButtonAction(actionEvent);

        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performSubtractionButtonAction(actionEvent);

        assertEquals("Values[0] is not 1", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15 -", "15 -", calculator.getTextPaneValue());
        assertTrue("Expecting isSubtracting to be set", calculator.isSubtracting());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5ThenSubtractThen5ThenEquals()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE.getValue()).thenReturn(FIVE.getValue()).thenReturn(SUBTRACTION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(EQUALS.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15 -", "15 -", calculator.getTextPaneValue());
        assertTrue("Expected isSubtracting to be set", calculator.isSubtracting());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());

        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[1] is not 5", 5, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be 5", FIVE.getValue(), calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("Values[0] is not blank", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("Values[1] is not empty", BLANK.getValue(), calculator.getValues()[1]);
        assertEquals("textPane should be 10", "10", calculator.getTextPaneValue());
        assertFalse("Expected firstNumber to be false", calculator.isFirstNumber());

        verify(actionEvent, times(5)).getActionCommand();
    }

    @Test
    public void pressed1ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(MULTIPLICATION.getValue());
        calculator.performNumberButtonAction(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performMultiplicationAction(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1 ✕", "1 ✕", calculator.getTextPaneValue());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(FIVE.getValue()).thenReturn(MULTIPLICATION.getValue());
        calculator.performNumberButtonAction(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performNumberButtonAction(actionEvent);

        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performAdditionButtonAction(actionEvent);

        assertEquals("Values[0] is not 1", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15 ✕", "15 ✕", calculator.getTextPaneValue());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then0ThenMultiplyThen1Then0ThenEquals()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE.getValue()).thenReturn(ZERO.getValue()).thenReturn(MULTIPLICATION.getValue())
                .thenReturn(ONE.getValue()).thenReturn(ZERO.getValue()).thenReturn(EQUALS.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[0] is not 10", 10, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 10", "10", calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performMultiplicationAction(actionEvent);
        assertEquals("Values[0] is not 10", 10, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 10 ✕", "10 ✕", calculator.getTextPaneValue());
        assertTrue("Expecting isMultiplying to be set", calculator.isMultiplying());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());

        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[1] is not 1", 1, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[1] is not 10", 10, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be 10", "10", calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("textPane is not 100", "100", calculator.getTextPaneValue());
        assertEquals("Values[1] is not empty", BLANK.getValue(), calculator.getValues()[1]);
        assertEquals("textPane should be 100", "100", calculator.getTextPaneValue());
        assertFalse("Expected firstNumber to be false", calculator.isFirstNumber());

        verify(actionEvent, times(6)).getActionCommand();
    }

    @Test
    public void pressedAddWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION.getValue());
        calculator.performAdditionButtonAction(actionEvent);

        assertEquals("Values[{}] is not empty", BLANK.getValue(), calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenAddThenAddAgain()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(ADDITION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent); // no thenReturn necessary. Uses last value sent to method

        assertEquals("Values[{}] is not 1", ONE.getValue(), calculator.getValues()[0]);
        assertTrue("Expected isAdding to be true", calculator.isAdding());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedAddWithEInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + ERR.getValue());
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("Expected error message in textPane", ERR.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedSubtractWithEInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + ERR.getValue());
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("Expected error message in textPane", ERR.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedMultiplyWithEInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + ERR.getValue());
        calculator.performMultiplicationAction(actionEvent);
        assertEquals("Expected error message in textPane", ERR.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedDivideWithEInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + ERR.getValue());
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("Expected error message in textPane", ERR.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedAddWithNumberTooBigInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + NUMBER_TOO_BIG.getValue());
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("Expected error message in textPane", NUMBER_TOO_BIG.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressed1ThenAddThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(ADDITION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(ADDITION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("Expected textPane shows 6 +", "6 +", calculator.getTextPaneValue());
        assertTrue("Expected isAdd to be true", calculator.isAdding());
    }

    @Test
    public void pressedSubtractThen5()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION.getValue()).thenReturn(FIVE.getValue());
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("Expected textPane to show - symbol", SUBTRACTION.getValue(), calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be blank", BLANK.getValue(), calculator.getValues()[0]);
        assertTrue("Expected isNumberNegative to be true", calculator.isNumberNegative());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected textPane to show -5", "-5", calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be -5", "-5", calculator.getValues()[0]);
        assertTrue("Expected isNumberNegative to be true", calculator.isNumberNegative());
        assertTrue("Expected isNegative to be true", calculator.isNumberNegative());
    }

    @Test
    public void pressed5ThenAddThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue()).thenReturn(ADDITION.getValue()).thenReturn(SUBTRACTION.getValue());
        calculator.setIsNumberNegative(false);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("Expected textPane to -", SUBTRACTION.getValue(), calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be 5", FIVE.getValue(), calculator.getValues()[0]);
        assertTrue("Expected isNumberNegative is true", calculator.isNumberNegative());
    }

    @Test
    public void pressed1ThenSubtractThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(SUBTRACTION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(ADDITION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        calculator.performSubtractionButtonAction(actionEvent);
        assertTrue("Expected isSubtract to be true", calculator.isSubtracting());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("Expected textPane shows -4 +", "-4 +", calculator.getTextPaneValue());
        assertTrue("Expected isAdd to be true", calculator.isAdding());
    }

    @Test
    public void pressed1ThenSubtractThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE.getValue()).thenReturn(SUBTRACTION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(SUBTRACTION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        calculator.performSubtractionButtonAction(actionEvent);
        assertTrue("Expected isSubtract to be true", calculator.isSubtracting());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("Expected textPane shows -4 -", "-4 -", calculator.getTextPaneValue());
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
    }

    @Test
    public void testSubtractWithDecimalNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.getValues()[0] = "4.5";
        calculator.setIsSubtracting(true);
        calculator.getValues()[1] = "-2.3";
        calculator.setIsNumberNegative(true);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("Expected textPane shows 6.8", "6.8", calculator.getTextPaneValue());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertFalse("Expected isNumberNegative to be false", calculator.isNumberNegative());
        assertFalse("Expected decimal to be disabled", calculator.isDotPressed());
    }

    @Test
    public void testPressedAdditionAfterEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(SUBTRACTION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(EQUALS.getValue()).thenReturn(ADDITION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("Expected textPane to be -4 +", "-4 +", calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be -4", "-4", calculator.getValues()[0]);
        assertTrue("Expected isAdding to be true", calculator.isAdding());
    }

    @Test
    public void testPressedSubtractAfterEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(SUBTRACTION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(EQUALS.getValue()).thenReturn(SUBTRACTION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("Expected textPane to be -4 -", "-4 -", calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be -4", "-4", calculator.getValues()[0]);
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
    }

    @Test
    public void testPressedMultiplyAfterEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(SUBTRACTION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(EQUALS.getValue()).thenReturn(MULTIPLICATION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        calculator.performMultiplicationAction(actionEvent);
        assertEquals("Expected textPane to be -4 ✕", "-4 ✕", calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be -4", "-4", calculator.getValues()[0]);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
    }

    @Test
    public void testPressedDivideAfterEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(SUBTRACTION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(EQUALS.getValue()).thenReturn(DIVISION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("Expected textPane to be -4 ÷", "-4 ÷", calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be -4", "-4", calculator.getValues()[0]);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void testContinuedSubtractionWithNegatedNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION.getValue());
        calculator.getValues()[0] = "2";
        calculator.setIsSubtracting(true);
        calculator.setIsNumberNegative(true);
        calculator.getValues()[1] = "-5";
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("Expected textPane to be 7 -", "7 -", calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be 7", "7", calculator.getValues()[0]);
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
        assertFalse("Expected isNumberNegative to be false", calculator.isNumberNegative());
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
    }

    @Test
    public void testContinuedSubtractionWithDecimals()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION.getValue());
        calculator.getValues()[0] = "2.3";
        calculator.setIsSubtracting(true);
        calculator.setIsNumberNegative(true);
        calculator.getValues()[1] = "-2.1";
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("Expected textPane to be 4.4 -", "4.4 -", calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be 4.4", "4.4", calculator.getValues()[0]);
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
        assertFalse("Expected isNumberNegative to be false", calculator.isNumberNegative());
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
    }

    @Test
    public void testContinuedSubtractionWithDecimalsAndNegatedNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION.getValue());
        calculator.getValues()[0] = "2.3";
        calculator.setIsSubtracting(true);
        calculator.setIsNumberNegative(false);
        calculator.getValues()[1] = "2.1";
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("Expected textPane to be 0.2 -", "0.2 -", calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be 0.2", "0.2", calculator.getValues()[0]);
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
        assertFalse("Expected isNumberNegative to be false", calculator.isNumberNegative());
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
    }

    @Test
    public void pressed1ThenSubtractThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(SUBTRACTION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(MULTIPLICATION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        calculator.performSubtractionButtonAction(actionEvent);
        assertTrue("Expected isSubtract to be true", calculator.isSubtracting());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        calculator.performMultiplicationAction(actionEvent);
        assertEquals("Expected textPane shows -4 ✕", "-4 ✕", calculator.getTextPaneValue());
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
    }

    @Test
    public void pressed1ThenDivideThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(DIVISION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(SUBTRACTION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        calculator.performDivideButtonAction(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("Expected textPane shows 0.2 -", "0.2 -", calculator.getTextPaneValue());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
    }

    @Test
    public void pressed1ThenSubtractThen5ThenDivide()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE.getValue()).thenReturn(SUBTRACTION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(DIVISION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        calculator.performSubtractionButtonAction(actionEvent);
        assertTrue("Expected isSubtract to be true", calculator.isSubtracting());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("Expected textPane shows -4 ÷", "-4 ÷", calculator.getTextPaneValue());
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(MULTIPLICATION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(ADDITION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        calculator.performMultiplicationAction(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("Expected textPane shows 5 +", "5 +", calculator.getTextPaneValue());
        assertTrue("Expected isAdd to be true", calculator.isAdding());
    }

    @Test
    public void testMultiplyingTwoDecimals()
    {
        when(actionEvent.getActionCommand()).thenReturn(MULTIPLICATION.getValue());
        calculator.getValues()[0] = "4.5";
        calculator.setIsMultiplying(true);
        calculator.getValues()[1] = "2.3";
        calculator.performMultiplicationAction(actionEvent);
        assertEquals("Expected textPane shows 10.35 ✕", "10.35 ✕", calculator.getTextPaneValue());
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        assertFalse("Expected isNumberNegative to be false", calculator.isNumberNegative());
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(MULTIPLICATION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(SUBTRACTION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        calculator.performMultiplicationAction(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        calculator.performSubtractionButtonAction(actionEvent);
        assertEquals("Expected textPane shows 5 -", "5 -", calculator.getTextPaneValue());
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(MULTIPLICATION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(MULTIPLICATION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        calculator.performMultiplicationAction(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        calculator.performMultiplicationAction(actionEvent);
        assertEquals("Expected textPane shows 5 ✕", "5 ✕", calculator.getTextPaneValue());
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenDivide()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE.getValue()).thenReturn(MULTIPLICATION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(DIVISION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        calculator.performMultiplicationAction(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("Expected textPane shows 5 ÷", "5 ÷", calculator.getTextPaneValue());
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void pressed1ThenDivideThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(DIVISION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(MULTIPLICATION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        calculator.performDivideButtonAction(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        calculator.performMultiplicationAction(actionEvent);
        assertEquals("Expected textPane shows 0.2 ✕", "0.2 ✕", calculator.getTextPaneValue());
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
    }

    @Test
    public void pressed1ThenMultiplyThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(MULTIPLICATION.getValue()).thenReturn(MULTIPLICATION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performMultiplicationAction(actionEvent);
        calculator.performMultiplicationAction(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
    }

    @Test
    public void pressed1ThenDivideThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(DIVISION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(ADDITION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        calculator.performDivideButtonAction(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("Expected textPane shows 0.2 +", "0.2 +", calculator.getTextPaneValue());
        assertTrue("Expected isAdd to be true", calculator.isAdding());
    }

    @Test
    public void pressed1ThenDivideThen5ThenDivide()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(DIVISION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(DIVISION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        calculator.performDivideButtonAction(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("Expected textPane shows 0.2 ÷", "0.2 ÷", calculator.getTextPaneValue());
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void pressed1ThenDivideThenDivide()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(DIVISION.getValue()).thenReturn(DIVISION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDivideButtonAction(actionEvent);
        calculator.performDivideButtonAction(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
    }
    
    @Test
    public void pressed1ThenDivideThen0()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(DIVISION.getValue()).thenReturn(ZERO.getValue()).thenReturn(EQUALS.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDivideButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertEquals("Expected values[0] to be blank", BLANK.getValue(), calculator.getValues()[0]);
    }

    @Test
    public void pressed1ThenAddThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(ADDITION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(SUBTRACTION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performSubtractionButtonAction(actionEvent);
        calculator.setValuesPosition(1);
        assertEquals("Expected textPane shows 6 -", "6 -", calculator.getTextPaneValue());
        assertTrue("Expected isSubtract to be true", calculator.isSubtracting());
    }

    @Test
    public void pressed1ThenAddThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(ADDITION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(MULTIPLICATION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performMultiplicationAction(actionEvent);
        calculator.setValuesPosition(1);
        assertEquals("Expected textPane shows 6 ✕", "6 ✕", calculator.getTextPaneValue());
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
    }

    @Test
    public void pressed1ThenAddThen5ThenDivide()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE.getValue()).thenReturn(ADDITION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(DIVISION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDivideButtonAction(actionEvent);
        calculator.setValuesPosition(1);
        assertEquals("Expected textPane shows 6 ÷", "6 ÷", calculator.getTextPaneValue());
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void testAddingTwoNumbersResultsInDecimalNumberThenAdding()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION.getValue()).thenReturn(FIVE.getValue());
        calculator.getValues()[0] = "5.5";
        calculator.getValues()[1] = "2";
        calculator.setIsAdding(true);
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("Expected textPane shows 7.5 +", "7.5 +", calculator.getTextPaneValue());
        assertTrue("Expected isAdding to be true", calculator.isAdding());

        calculator.performNumberButtonAction(actionEvent);
        assertTrue("Expected isAdding to be true", calculator.isAdding());
    }

    @Test
    public void testAddingTwoNumbersResultsInDecimalNumberThatIsMaxThenAdding()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION.getValue());
        calculator.getValues()[0] = "9999998";
        calculator.getValues()[1] = "2.5";
        calculator.setIsAdding(true);
        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("Expected textPane shows 1.00000005E7", "1.00000005E7", calculator.getTextPaneValue());
        assertFalse("Expected isAdding to be false", calculator.isAdding());
        assertEquals("Expected history to not show continued operation",
                "(+) Result: 9,999,998 + 2.5 = 1.00000005E7",
                calculator.getBasicHistoryPaneTextWithoutNewLineCharacters());
    }

    @Test
    public void pressedAddThenDivideWhenNumberIsMaximum()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION.getValue());
        calculator.getValues()[0] = "9999998";
        calculator.getValues()[1] = TWO.getValue();
        calculator.setIsAdding(true);
        calculator.setValuesPosition(1);
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("Expected textPane shows 1.0E7", "1.0E7", calculator.getTextPaneValue());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
    }

    @Test
    public void pressedSubtractWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION.getValue());
        calculator.performAdditionButtonAction(actionEvent);

        assertEquals("Values[{}] is not empty", BLANK.getValue(), calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedMultiplyWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(MULTIPLICATION.getValue());
        calculator.performMultiplicationAction(actionEvent);

        assertEquals("Values[{}] is not empty", BLANK.getValue(), calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedDivideWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION.getValue());
        calculator.performDivideButtonAction(actionEvent);

        assertEquals("Values[{}] is not empty", BLANK.getValue(), calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void testAdditionWithWholeNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setIsAdding(true);
        calculator.getValues()[0] = FIVE.getValue();
        calculator.getValues()[1] = "10";
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("Did not get back expected result", "15", calculator.getTextPaneValue());
    }

    @Test
    public void testAdditionWithDecimalNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setIsAdding(true);
        calculator.getValues()[0] = "5.5";
        calculator.getValues()[1] = "10";
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("Did not get back expected result", "15.5", calculator.getTextPaneValue());
    }

    @Test
    public void testSubtractionFunctionalityWithWholeNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setIsSubtracting(true);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = "10";
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("Did not get back expected result", FIVE.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void testSubtractionFunctionalityWithDecimalNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setIsSubtracting(true);
        calculator.getValues()[0] = "15.5";
        calculator.getValues()[1] = "10";
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("Did not get back expected result", "5.5", calculator.getTextPaneValue());
    }

    @Test
    public void testMultiplicationFunctionalityWithWholeNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setIsMultiplying(true);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = "10";
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("Did not get back expected result", "150", calculator.getTextPaneValue());
    }

    @Test
    public void testMultiplicationFunctionalityWithDecimalNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setIsMultiplying(true);
        calculator.getValues()[0] = "5.5";
        calculator.getValues()[1] = "10.2";
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("Did not get back expected result", "56.1", calculator.getTextPaneValue());
    }

    @Test
    public void testDivisionFunctionalityWithWholeNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setIsDividing(true);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = FIVE.getValue();
        calculator.setCalculatorView(VIEW_BASIC);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("Did not get back expected result", THREE.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void testDivisionFunctionalityWithDecimalNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setIsDividing(true);
        calculator.getValues()[0] = "15.5";
        calculator.getValues()[1] = FIVE.getValue();
        calculator.setCalculatorView(VIEW_BASIC);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("Did not get back expected result", "3.1", calculator.getTextPaneValue());
    }

    @Test
    public void testDivisionBy0() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setIsDividing(true);
        calculator.getValues()[0] = "15.5";
        calculator.getValues()[1] = ZERO.getValue();
        calculator.setCalculatorView(VIEW_BASIC);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("Expected values[0] to be blank", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("Expected textPane to show error", INFINITY.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + "35");
        calculator.getValues()[0] = "35";
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals("Values[0] is not 3", 3, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane does not equal 3", THREE.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedDeleteWhenTextPaneHasBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER.getValue());
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals("Expected textPane to be blank", BLANK.getValue(), calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be blank", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("Expected values[1] to be blank", BLANK.getValue(), calculator.getValues()[1]);
        assertEquals("Expected valuesPosition to be 0", 0, calculator.getValuesPosition());
        assertFalse("Expected isAdding to be false", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertFalse("Expected isNegative to be false", calculator.isNumberNegative());
        assertTrue("Expected dot to be enabled", calculator.isDotPressed());
    }

    @Test
    public void testDeleteDoesNothingWhenNothingToDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK.getValue());
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals("Expected textPane to show error", ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be blank", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("Expected values[1] to be blank", BLANK.getValue(), calculator.getValues()[1]);
        assertEquals("Expected valuesPosition to be 0", 0, calculator.getValuesPosition());
        assertFalse("Expected isAdding to be false", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertFalse("Expected isNegative to be false", calculator.isNumberNegative());
        assertTrue("Expected dot to be enabled", calculator.isDotPressed());
    }

    @Test
    public void testDeleteClearsTextPaneAfterPressingEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue()).thenReturn(ADDITION.getValue())
                .thenReturn(THREE.getValue()).thenReturn(EQUALS.getValue()).thenReturn(DELETE.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performAdditionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performEqualsButtonAction(actionEvent);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals("Expected textPane to be blank", BLANK.getValue(), calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be blank", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("Expected values[1] to be blank", BLANK.getValue(), calculator.getValues()[1]);
        assertEquals("Expected valuesPosition to be 0", 0, calculator.getValuesPosition());
        assertFalse("Expected isAdding to be false", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertFalse("Expected isNegative to be false", calculator.isNumberNegative());
        assertTrue("Expected dot to be enabled", calculator.isDotPressed());
    }

    @Test
    public void pressed1Then5ThenDecimalThen6ThenAddThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue()).thenReturn(DELETE.getValue()).thenReturn(DELETE.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + "15.6 +");
        calculator.getValues()[0] = "15.6";
        //calculator.setDotPressed(true);
        calculator.getButtonDecimal().setEnabled(false);
        calculator.setIsAdding(true);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals("Values[0] is not 15.6", 15.6, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 15.6", "15.6", calculator.getTextPaneValue());
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());
        assertFalse("Expected isAdding to be false", calculator.isAdding());

        calculator.performDeleteButtonAction(actionEvent);
        assertEquals("Values[0] is not 15.", 15., Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 15.", "15.", calculator.getTextPaneValue());
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());

        calculator.performDeleteButtonAction(actionEvent);
        assertEquals("Values[0] is not 15", 15, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 15", "15", calculator.getTextPaneValue());
        assertTrue("Expected decimal button to be enabled", calculator.getButtonDecimal().isEnabled());
    }

    @Test
    public void pressed1ThenSubtractThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + "1 -");
        calculator.getValues()[0] = ONE.getValue();
        calculator.getButtonDecimal().setEnabled(false);
        calculator.setIsSubtracting(true);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals("Values[0] is not 1", 1, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 1", ONE.getValue(), calculator.getTextPaneValue());
        assertTrue("Expected decimal button to be enabled", calculator.getButtonDecimal().isEnabled());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
    }

    @Test
    public void pressedSubtractThen5ThenSubtractThen6ThenEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + "-6");
        calculator.getValues()[0] = "-5";
        calculator.getValues()[1] = "-6";
        calculator.setIsNumberNegative(true);
        calculator.setIsSubtracting(true);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("Values[0] is not blank", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("textPane does not equal 1", ONE.getValue(), calculator.getTextPaneValue());
        assertTrue("Expected decimal button to be enabled", calculator.getButtonDecimal().isEnabled());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
    }

    @Test
    public void pressedSubtractThen5ThenSubtractThen5()
    {
        // -5 - -5 =
        when(actionEvent.getActionCommand())
                .thenReturn(SUBTRACTION.getValue()).thenReturn(FIVE.getValue())
                .thenReturn(SUBTRACTION.getValue()).thenReturn(SUBTRACTION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(EQUALS.getValue());
        calculator.performSubtractionButtonAction(actionEvent);
        assertTrue("Expected isNumberNegative to be true", calculator.isNumberNegative());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[0] is not -5", "-5", calculator.getValues()[0]);
        assertEquals("textPane does not equal -5", "-5", calculator.getTextPaneValue());
        calculator.performSubtractionButtonAction(actionEvent);
        assertFalse("Expected isNumberNegative to be false", calculator.isNumberNegative());
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
        calculator.performSubtractionButtonAction(actionEvent);
        assertTrue("Expected isNumberNegative to be true", calculator.isNumberNegative());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[1] is not -5", -5, Double.parseDouble(calculator.getValues()[1]), delta);
        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("Values[0] is blank", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("textPane does not equal 0", ZERO.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressed1ThenMultiplyThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + "1 ✕");
        calculator.getValues()[0] = ONE.getValue();
        calculator.getButtonDecimal().setEnabled(false);
        calculator.setIsMultiplying(true);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals("Values[0] is not 1", 1, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 1", ONE.getValue(), calculator.getTextPaneValue());
        assertTrue("Expected decimal button to be enabled", calculator.getButtonDecimal().isEnabled());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
    }

    @Test
    public void pressed1ThenDivideThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + "1 ÷");
        calculator.getValues()[0] = ONE.getValue();
        calculator.getButtonDecimal().setEnabled(false);
        calculator.setIsDividing(true);
        calculator.performDeleteButtonAction(actionEvent);
        assertEquals("Values[0] is not 1", 1, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 1", ONE.getValue(), calculator.getTextPaneValue());
        assertTrue("Expected decimal button to be enabled", calculator.getButtonDecimal().isEnabled());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
    }

    @Test
    public void enteredANumberThenAddThen6DeleteThen5()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue()).thenReturn(FIVE.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + SIX.getValue());
        calculator.getValues()[0] = "15.6";
        calculator.getValues()[1] = SIX.getValue();
        calculator.setValuesPosition(1);
        calculator.getButtonDecimal().setEnabled(true);
        calculator.setIsAdding(true);
        assertEquals("Values[1] is not 6", 6, Integer.parseInt(calculator.getValues()[1]));

        calculator.performDeleteButtonAction(actionEvent);
        assertEquals("Values[0] is not 15.6", 15.6, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane is not blank", BLANK.getValue(), calculator.getTextPaneValue());
        assertTrue("Expected decimal button is enabled", calculator.isDotPressed());
        assertTrue("Expected isAdding to be true", calculator.isAdding());

        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[1] is not 5", 5, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane does not equal blank", FIVE.getValue(), calculator.getTextPaneValue());
        assertTrue("Expected decimal button is enabled", calculator.isDotPressed());
        assertTrue("Expected isAdding to be true", calculator.isAdding());
    }

    @Test
    public void pressingClearRestoresCalculatorToStartFunctionality()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR.getValue());
        calculator.performClearButtonAction(actionEvent);
        assertEquals("Values[0] should be 0", ZERO.getValue(), calculator.getValues()[0]);
        for ( int i=1; i<3; i++) {
            assertTrue("Values["+i+"] is not blank", calculator.getValues()[i].isBlank());
        }
        assertEquals("textPane is not 0", ZERO.getValue(), calculator.getTextPaneValue());
        assertFalse("isAddBool() is not false", calculator.isAdding());
        assertFalse("isSubBool() is not false", calculator.isSubtracting());
        assertFalse("isMulBool() is not false", calculator.isMultiplying());
        assertFalse("isDivBool() is not false", calculator.isDividing());
        assertEquals("Values position is not 0", 0, calculator.getValuesPosition());
        assertTrue("FirstNumBool is not true", calculator.isFirstNumber());
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
    }

    @Test
    public void pressingClearEntryWhenOnValuesPosition0ClearsTextPaneAndMainOperatorsAndDecimal()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR_ENTRY.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + "1088");
        calculator.setValuesPosition(0);
        calculator.getValues()[0] = "1088";
        calculator.performClearEntryButtonAction(actionEvent);

        assertTrue("textPane was not cleared", StringUtils.isBlank(calculator.getTextPaneValue()));
        assertFalse("Expected isAdding to be false", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertTrue("Expected decimal button to be enabled", calculator.isDotPressed());
        assertTrue("Expected to be on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressingClearEntry()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR_ENTRY.getValue()).thenReturn(CLEAR_ENTRY.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + "1088");
        calculator.setIsAdding(true);
        calculator.setValuesPosition(1);
        calculator.getValues()[0] = "100";
        calculator.getValues()[1] = "1088";

        calculator.performClearEntryButtonAction(actionEvent);
        assertEquals("Expected textPane to show values[0]", "100", calculator.getValues()[0]);
        assertTrue("Expected isAdding to be true", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertTrue("Expected decimal button to be enabled", calculator.isDotPressed());
        assertFalse("Expected not to be on the firstNumber", calculator.isFirstNumber());
        assertEquals("Expected to be at valuesPosition:1", 1, calculator.getValuesPosition());

        calculator.performClearEntryButtonAction(actionEvent);
        assertEquals("Expected textPane to show values[0]", BLANK.getValue(), calculator.getValues()[0]);
        assertFalse("Expected isAdding to be false", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertTrue("Expected decimal button to be enabled", calculator.isDotPressed());
        assertTrue("Expected to be on the firstNumber", calculator.isFirstNumber());
        assertEquals("Expected to be at valuesPosition:0", 0, calculator.getValuesPosition());

    }

    @Test
    public void pressingClearEntryAfterPressingAnOperatorResetsTextPaneAndOperator()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR_ENTRY.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + "1088 +");
        calculator.performClearEntryButtonAction(actionEvent);

        assertTrue("textPane was not cleared", StringUtils.isBlank(calculator.getTextPaneWithoutAnyOperator()));
        assertFalse("isAdding() expected to be false", calculator.isAdding());
    }

    @Test
    public void pressedClearEntryWhenTextPaneIsEmpty()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR_ENTRY.getValue());
        calculator.performClearEntryButtonAction(actionEvent);
        assertEquals("Expected textPane to be blank", BLANK.getValue(), calculator.getValues()[0]);
        assertFalse("Expected isAdding to be false", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertTrue("Expected decimal button to be enabled", calculator.isDotPressed());
        assertTrue("Expected to be on the firstNumber", calculator.isFirstNumber());
        assertEquals("Expected valuesPosition to be 0", 0, calculator.getValuesPosition());
    }

    @Test
    public void pressedSquaredButtonWithNoEntry()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARED.getValue());
        basicPanel.performSquaredButtonAction(actionEvent);
        assertEquals("Expected textPane to contain message", ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue());
        assertTrue("Expected values[0] to be empty", calculator.getValues()[0].isEmpty());
    }

    @Test
    public void pressedSquaredButtonWithBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARED.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER.getValue());
        basicPanel.performSquaredButtonAction(actionEvent);
        assertEquals("Expected textPane to show bad text", ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressed5ThenSquaredButton()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue()).thenReturn(SQUARED.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[0] is not 5", 5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 5", FIVE.getValue(), calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performSquaredButtonAction(actionEvent);
        assertEquals("Values[0] is not 25", 25, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 25", "25", calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed5ThenDecimalThen0ThenSquaredButton()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue()).thenReturn(DECIMAL.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(SQUARED.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performDecimalButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[0] is not 5.5", 5.5, Double.parseDouble(calculator.getValues()[0]), delta); // 0.0
        assertEquals("textPane should be 5.5", "5.5", calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performSquaredButtonAction(actionEvent);
        assertEquals("Values[0] is not 30.25", 30.25, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane should be 30.25", "30.25", calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
        assertFalse("Expected decimal button to be disabled", calculator.isDotPressed());
    }

    @Test
    public void pressed5ThenNegateThenSquaredThenNegate()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(FIVE.getValue()).thenReturn(NEGATE.getValue())
                .thenReturn(SQUARED.getValue()).thenReturn(NEGATE.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[0] is not 5", 5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 5", FIVE.getValue(), calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performNegateButtonAction(actionEvent);
        assertEquals("Values[0] is not -5", -5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -5", "-5", calculator.getTextPaneValue());
        assertTrue("Expected isNumberNegative to be true", calculator.isNumberNegative());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performSquaredButtonAction(actionEvent);
        assertEquals("Values[0] is not 25", 25, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 25", "25", calculator.getTextPaneValue());
        assertFalse("Expected isNumberNegative to be false", calculator.isNumberNegative());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performNegateButtonAction(actionEvent);
        assertEquals("Values[0] is not -25", -25, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -25", "-25", calculator.getTextPaneValue());
        assertTrue("Expected isNumberNegative to be true", calculator.isNumberNegative());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

    }

    @Test
    public void pressed5ThenNegateThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue()).thenReturn(NEGATE.getValue()).thenReturn(NEGATE.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Values[0] is not 5", 5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 5", FIVE.getValue(), calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        calculator.performNegateButtonAction(actionEvent);
        assertEquals("Values[0] is not -5", -5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -5", "-5", calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
        assertTrue("Expected number to be negative", calculator.isNumberNegative());

        calculator.performNegateButtonAction(actionEvent);
        assertEquals("Values[0] is not 5", 5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 5", FIVE.getValue(), calculator.getTextPaneValue());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
        assertFalse("Expected number to be positive", calculator.isNumberNegative());
    }

    @Test
    public void pressedNegateOnTooBigNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(NEGATE.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + ERR.getValue());
        calculator.performNegateButtonAction(actionEvent);
        assertEquals("Expected error message in textPane", ERR.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedNegateWhenNoValueInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(NEGATE.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK.getValue());
        calculator.performNegateButtonAction(actionEvent);
        assertEquals("Expected error message in textPane", ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedSquareRootWithTextPaneShowingE()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + ERR.getValue());
        calculator.performSquareRootButtonAction(actionEvent);
        assertEquals("Expected textPane to show error", ERR.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedSquareRootWithTextPaneEmpty()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK.getValue());
        calculator.performSquareRootButtonAction(actionEvent);
        assertEquals("Expected textPane to show error message", ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedSquareRootWhenCurrentValueIsNegative()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + "-5");
        calculator.getValues()[0] = "-5;";
        calculator.setValuesPosition(0);
        calculator.performSquareRootButtonAction(actionEvent);
        assertEquals("Expected textPane to show error message", ERR.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedSquareRootWithWholeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + "25");
        calculator.getValues()[0] = "25";
        calculator.setValuesPosition(0);
        calculator.performSquareRootButtonAction(actionEvent);
        assertEquals("Expected result to be 5.0", 5.0, Double.parseDouble(calculator.getTextPaneValue()), delta);
    }

    @Test
    public void pressedSquareRootWithDecimalNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + "25.5");
        calculator.getValues()[0] = "25.5";
        calculator.setValuesPosition(0);
        calculator.performSquareRootButtonAction(actionEvent);
        assertEquals("Expected result to be 5.05", 5.05, Double.parseDouble(calculator.getTextPaneValue()), delta);
        assertFalse("Expected dot to be disable", calculator.isDotPressed());
    }

    @Test
    public void pressedMemoryStoreWhenTextPaneIsBlank()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_STORE.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK.getValue());
        calculator.performMemoryStoreAction(actionEvent);
        assertTrue("Expected MemoryValues[0] to be blank", calculator.getMemoryValues()[0].isBlank());
        assertEquals("Expected textPane to show Enter a Number", ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedMemoryStoreWhenTextPaneContainsValue()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_STORE.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + FIVE.getValue());
        calculator.performMemoryStoreAction(actionEvent);
        assertEquals("Expected textPane to still show 5", FIVE.getValue(), calculator.getTextPaneValue());
        assertEquals("Expected MemoryValues[0] to be 5", FIVE.getValue(), calculator.getMemoryValues()[0]);
        assertSame("Expected MemoryPosition to be 1", 1, calculator.getMemoryPosition());
    }

    @Test
    public void pressedMemoryStoreWhenMemoryIsFullOverwritesMemory()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_STORE.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + TWO.getValue());
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(10);
        assertEquals("Expected memoryValues[0] to be 15", "15", calculator.getMemoryValues()[0]);
        calculator.performMemoryStoreAction(actionEvent);
        assertEquals("Expected memoryValues[0] to be 2", TWO.getValue(), calculator.getMemoryValues()[0]);
    }

    @Test
    public void pressedMemoryStoreWhenTextPaneContainInvalidText()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_STORE.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + ERR.getValue());
        assertTrue("Expected textPane to contain bad text", calculator.textPaneContainsBadText());
        calculator.performMemoryStoreAction(actionEvent);
        assertTrue("Expected MemoryValues[0] to be blank", calculator.getMemoryValues()[0].isBlank());
    }

    @Test
    public void pressedMemoryRecall()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_RECALL.getValue()).thenReturn(MEMORY_RECALL.getValue())
                .thenReturn(MEMORY_RECALL.getValue()).thenReturn(MEMORY_RECALL.getValue());
        calculator.getMemoryValues()[0] = "15";
        calculator.getMemoryValues()[1] = "534";
        calculator.getMemoryValues()[2] = "-9";
        calculator.getMemoryValues()[3] = "75";
        calculator.getMemoryValues()[4] = TWO.getValue();
        calculator.getMemoryValues()[5] = "1080";
        calculator.setMemoryPosition(6);
        calculator.setMemoryRecallPosition(0);
        calculator.performMemoryRecallAction(actionEvent);
        assertEquals("Expected textPane to show 15", "15", calculator.getTextPaneValue());
        assertSame("Expected memoryRecallPosition to be 1", 1, calculator.getMemoryRecallPosition());
        assertSame("Expected memoryPosition to be 6", 6, calculator.getMemoryPosition());
        calculator.performMemoryRecallAction(actionEvent);
        assertEquals("Expected textPane to show 534", "534", calculator.getTextPaneValue());
        assertSame("Expected memoryRecallPosition to be 2", 2, calculator.getMemoryRecallPosition());
        assertSame("Expected memoryPosition to be 6", 6, calculator.getMemoryPosition());
        calculator.performMemoryRecallAction(actionEvent);
        assertEquals("Expected textPane to show -9", "-9", calculator.getTextPaneValue());
        assertSame("Expected memoryRecallPosition to be 3", 3, calculator.getMemoryRecallPosition());
        assertSame("Expected memoryPosition to be 6", 6, calculator.getMemoryPosition());
        calculator.setMemoryRecallPosition(10);
        calculator.performMemoryRecallAction(actionEvent);
        assertEquals("Expected textPane to show 15", "15", calculator.getTextPaneValue());
        assertSame("Expected memoryRecallPosition to be 1", 1, calculator.getMemoryRecallPosition());
        assertSame("Expected memoryPosition to be 6", 6, calculator.getMemoryPosition());
    }

    @Test
    public void pressedMemoryClear()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_CLEAR.getValue());
        calculator.getMemoryValues()[9] = "15";
        calculator.setMemoryPosition(10);
        calculator.performMemoryClearAction(actionEvent);
        assertTrue("Expected memoryValues[9] to be empty", calculator.getMemoryValues()[9].isBlank());
        assertSame("Expected memoryPosition to be 0",0, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertFalse("Expected memoryClear to be disabled, no more memories", calculator.getButtonMemoryClear().isEnabled());
        assertFalse("Expected memoryRecall to be disabled, no more memories", calculator.getButtonMemoryRecall().isEnabled());
        assertFalse("Expected memoryAdd to be disabled, no more memories", calculator.getButtonMemoryAddition().isEnabled());
        assertFalse("Expected memorySubtract to be disabled, no more memories", calculator.getButtonMemorySubtraction().isEnabled());
    }

    @Test
    public void pressed5ThenMemoryAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION.getValue());
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(calculator.addNewLines() + FIVE.getValue());
        calculator.setValuesPosition(0);
        calculator.performMemoryAdditionAction(actionEvent);
        assertEquals("Expected memoryValues[0] to be 15", "15", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 1", 1, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to show 5", FIVE.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void testSavingAWholeNumberWithMemoryAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION.getValue());
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK.getValue());
        calculator.setValuesPosition(0);
        calculator.performMemoryAdditionAction(actionEvent);
        assertEquals("Expected memoryValues[0] to be 10", "10", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 1", 1, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to show error", ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void testSavingADecimalNumberWithMemoryAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION.getValue());
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(calculator.addNewLines() + "5.5");
        calculator.setValuesPosition(0);
        calculator.performMemoryAdditionAction(actionEvent);
        assertEquals("Expected memoryValues[0] to be 15.5", "15.5", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 1", 1, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to show 5.5", "5.5", calculator.getTextPaneValue());
    }

    @Test
    public void testMemoryAddFailsWhenSavingBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION.getValue());
        calculator.getMemoryValues()[0] = BLANK.getValue();
        calculator.setMemoryPosition(0);
        calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER.getValue());
        calculator.setValuesPosition(0);
        calculator.performMemoryAdditionAction(actionEvent);
        assertEquals("Expected memoryValues[0] to be blank", BLANK.getValue(), calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 0", 0, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to show error", ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressed5ThenMemorySub()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_SUBTRACTION.getValue());
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(calculator.addNewLines() + FIVE.getValue());
        calculator.setValuesPosition(0);
        calculator.performMemorySubtractionAction(actionEvent);
        assertEquals("Expected memoryValues[0] to be 10", "10", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 1", 1, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to show 5", FIVE.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void testSavingAWholeNumberWithMemorySubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_SUBTRACTION.getValue());
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK.getValue());
        calculator.setValuesPosition(0);
        calculator.performMemorySubtractionAction(actionEvent);
        assertEquals("Expected memoryValues[0] to be 15", "15", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 1", 1, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to show error", ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void testSavingADecimalNumberWithMemorySubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION.getValue());
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(calculator.addNewLines() + "5.5");
        calculator.setValuesPosition(0);
        calculator.performMemorySubtractionAction(actionEvent);
        assertEquals("Expected memoryValues[0] to be 4.5", "4.5", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 1", 1, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to show 5.5", "5.5", calculator.getTextPaneValue());
    }

    @Test
    public void testMemorySubFailsWhenSavingBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION.getValue());
        calculator.getMemoryValues()[0] = BLANK.getValue();
        calculator.setMemoryPosition(0);
        calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER.getValue());
        calculator.setValuesPosition(0);
        calculator.performMemorySubtractionAction(actionEvent);
        assertEquals("Expected memoryValues[0] to be blank", BLANK.getValue(), calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 0", 0, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to show error", ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void testOpeningHistory()
    {
        when(actionEvent.getActionCommand()).thenReturn(HISTORY_OPEN.getValue());
        basicPanel.performHistoryAction(actionEvent);
    }

    @Test
    public void testClosingHistory()
    {
        when(actionEvent.getActionCommand()).thenReturn(HISTORY_OPEN.getValue());
        when(actionEvent.getActionCommand()).thenReturn(HISTORY_CLOSED.getValue());
        basicPanel.performHistoryAction(actionEvent);
        basicPanel.performHistoryAction(actionEvent);
    }

    @Test
    public void pressedFractionWhenTextPaneContainsBadText()
    {
        calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER.getValue());
        basicPanel.performFractionButtonAction(actionEvent);
        assertEquals("Expected textPane to show bad text", ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressed0ThenFraction()
    {
        when(actionEvent.getActionCommand()).thenReturn(ZERO.getValue()).thenReturn(FRACTION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        basicPanel.performFractionButtonAction(actionEvent);
        assertEquals("Expected textPane to be Infinity", INFINITY.getValue(), calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be blank", BLANK.getValue(), calculator.getValues()[0]);
    }

    @Test
    public void pressedFractionWhenTextPaneIsBlank()
    {
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK.getValue());
        basicPanel.performFractionButtonAction(actionEvent);
        assertEquals("Expected textPane to show bad text", ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressed5ThenFraction()
    {
        when(actionEvent.getActionCommand()).thenReturn(FRACTION.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + FIVE.getValue());
        basicPanel.performFractionButtonAction(actionEvent);
        assertEquals("Expected textPane to be 0.2", "0.2", calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be 0.2", "0.2", calculator.getValues()[0]);
        assertFalse("Expected decimal button to be disabled", calculator.isDotPressed());
    }

    @Test
    public void pressedPercentWhenTextPaneContainsBadText()
    {
        calculator.getTextPane().setText(calculator.addNewLines() + ENTER_A_NUMBER.getValue());
        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals("Expected textPane to show bad text", ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedPercentWhenTextPaneIsBlank()
    {
        calculator.getTextPane().setText(calculator.addNewLines() + BLANK.getValue());
        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals("Expected textPane to show bad text", ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedPercentWithPositiveNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue()).thenReturn(PERCENT.getValue());
        calculator.performNumberButtonAction(actionEvent);
        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals("Expected textPane to be 0.05", "0.05", calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be 0.05", "0.05", calculator.getValues()[0]);
        assertFalse("Expected decimal to be disabled", calculator.isDotPressed());
    }

    @Test
    public void pressedPercentWithNegativeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION.getValue()).thenReturn(FIVE.getValue()).thenReturn(PERCENT.getValue());
        calculator.performSubtractionButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals("Expected textPane to be -0.05", "-0.05", calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be -0.05", "-0.05", calculator.getValues()[0]);
        assertFalse("Expected decimal to be disabled", calculator.isDotPressed());
    }

    @Test
    public void pressed1Then0Then6Then5ThenDecimalThen5Then4Then5Then7()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(ZERO.getValue())
                .thenReturn(SIX.getValue()).thenReturn(FIVE.getValue()).thenReturn(DECIMAL.getValue()).thenReturn(FIVE.getValue())
                .thenReturn("4").thenReturn(FIVE.getValue()).thenReturn(SEVEN.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1", ONE.getValue(), calculator.getTextPaneValue());
        calculator.performNumberButtonAction(actionEvent);
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
        assertEquals("Expected values[0] to be 10", "10", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 10", "10", calculator.getTextPaneValue());
        calculator.performNumberButtonAction(actionEvent);
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
        assertEquals("Expected values[0] to be 106", "106", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 106", "106", calculator.getTextPaneValue());
        calculator.performNumberButtonAction(actionEvent);
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
        assertEquals("Expected values[0] to be 1065", "1065", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065", "1,065", calculator.getTextPaneValue());
        calculator.performDecimalButtonAction(actionEvent);
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());
        assertEquals("Expected values[0] to be 1065.", "1065.", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065.", "1,065.", calculator.getTextPaneValue());
        calculator.performNumberButtonAction(actionEvent);
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());
        assertEquals("Expected values[0] to be 1065.5", "1065.5", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065.5", "1,065.5", calculator.getTextPaneValue());
        calculator.performNumberButtonAction(actionEvent);
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());
        assertEquals("Expected values[0] to be 1065.54", "1065.54", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065.54", "1,065.54", calculator.getTextPaneValue());
        calculator.performNumberButtonAction(actionEvent);
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());
        assertEquals("Expected values[0] to be 1065.545", "1065.545", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065.545", "1,065.545", calculator.getTextPaneValue());
        calculator.performNumberButtonAction(actionEvent);
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());
        assertEquals("Expected values[0] to be 1065.5457", "1065.5457", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065.5457", "1,065.5457", calculator.getTextPaneValue());
    }

    @Test
    public void pressed0WhenTextPaneHasBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(ZERO.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + INFINITY.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 0", ZERO.getValue(), calculator.getValues()[0]);
        assertEquals("Expected textPane to show 0", ZERO.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedDecimalWhenTextPaneHasBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + INFINITY.getValue());
        calculator.performDecimalButtonAction(actionEvent);
        assertEquals("Expected values[0] to be blank", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("Expected textPane to show Infinity", INFINITY.getValue(), calculator.getTextPaneValue());
    }

    @Test
    public void pressedANumberWhenMaxLengthHasBeenMet()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue());
        calculator.getTextPane().setText(calculator.addNewLines() + "4,500,424");
        calculator.getValues()[0] = "4500424";
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected textPane to be the same", "4,500,424", calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be the same", "4500424", calculator.getValues()[0]);
    }

    @Test
    public void pressedPercentThenFraction()
    {
        // starting panel has 25
        // pressed percent returns 0.25
        // pressed fraction returns 4.0, should show 4
        when(actionEvent.getActionCommand()).thenReturn(TWO.getValue()).thenReturn(FIVE.getValue())
                .thenReturn(PERCENT.getValue()).thenReturn(FRACTION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected textPane to be 2", TWO.getValue(), calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be 2", TWO.getValue(), calculator.getValues()[0]);
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());

        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected textPane to be 25", "25", calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be 25", "25", calculator.getValues()[0]);
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());

        basicPanel.performPercentButtonAction(actionEvent);
        assertEquals("Expected textPane to be 0.25", "0.25", calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be 0.25", "0.25", calculator.getValues()[0]);
        assertFalse("Expected decimal to be disabled", calculator.isDotPressed());

        basicPanel.performFractionButtonAction(actionEvent);
        assertEquals("Expected textPane to be 4", FOUR.getValue(), calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be 4", FOUR.getValue(), calculator.getValues()[0]);
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
    }

    @Test
    public void pressed1ThenAddThen2ThenEqualsThen5()
    {
        // 1 + 2 = 3
        // Pressing 5 Starts new Equation, new number
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(ADDITION.getValue())
                .thenReturn(TWO.getValue()).thenReturn(EQUALS.getValue()).thenReturn(FIVE.getValue());
        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1", ONE.getValue(), calculator.getTextPaneValue());

        calculator.performAdditionButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1 +", "1 +", calculator.getTextPaneValue());
        assertTrue("Expected isAdding to be true", calculator.isAdding());
        assertEquals("Expected valuesPosition to be 1", 1, calculator.getValuesPosition());

        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[1] to be 2", TWO.getValue(), calculator.getValues()[1]);
        assertEquals("Expected textPane to be 2", TWO.getValue(), calculator.getTextPaneValue());

        calculator.performEqualsButtonAction(actionEvent);
        assertEquals("Expected values[0] to be blank", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("Expected values[1] to be blank", BLANK.getValue(), calculator.getValues()[1]);
        assertEquals("Expected textPane to be 3", THREE.getValue(), calculator.getTextPaneValue());
        assertFalse("Expected isAdding to be false", calculator.isAdding());

        calculator.performNumberButtonAction(actionEvent);
        assertEquals("Expected values[0] to be 5", FIVE.getValue(), calculator.getValues()[0]);
        assertEquals("Expected values[1] to be blank", BLANK.getValue(), calculator.getValues()[1]);
        assertEquals("Expected textPane to be 5", FIVE.getValue(), calculator.getTextPaneValue());

    }

    @Test
    public void pressed4Then5ThenMSThenClearThen5ThenMAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(FOUR.getValue()).thenReturn(FIVE.getValue())
                .thenReturn(MEMORY_STORE.getValue()).thenReturn(CLEAR_ENTRY.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(MEMORY_ADDITION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performMemoryStoreAction(actionEvent);
        calculator.performClearEntryButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performMemoryAdditionAction(actionEvent);
        assertEquals("Expected textPane to show 5", FIVE.getValue(), calculator.getTextPaneValue());
        assertEquals("Expected memories[0] to be 50", "50", calculator.getMemoryValues()[0]);
    }

    @Test
    public void pressed4Then5ThenMSThenClearThen5ThenMSub()
    {
        when(actionEvent.getActionCommand()).thenReturn(FOUR.getValue()).thenReturn(FIVE.getValue())
                .thenReturn(MEMORY_STORE.getValue()).thenReturn(CLEAR_ENTRY.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(MEMORY_SUBTRACTION.getValue());
        calculator.performNumberButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performMemoryStoreAction(actionEvent);
        calculator.performClearEntryButtonAction(actionEvent);
        calculator.performNumberButtonAction(actionEvent);
        calculator.performMemorySubtractionAction(actionEvent);
        assertEquals("Expected textPane to show 5", FIVE.getValue(), calculator.getTextPaneValue());
        assertEquals("Expected memories[0] to be 40", "40", calculator.getMemoryValues()[0]);
    }

    @Test
    public void pressingNumberEnablesDecimal()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue());
        calculator.getValues()[0] = "10.35";
        calculator.getButtonDecimal().setEnabled(false);
        calculator.setIsAdding(true);
        calculator.setIsFirstNumber(false);
        calculator.performNumberButtonAction(actionEvent);
        assertFalse("Expected decimal to be enabled", calculator.isDotPressed());
    }

    @Test
    public void testContinuedDivisionAfterAttemptToDivideByZero()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION.getValue());
        calculator.getValues()[0] = "1";
        calculator.setIsDividing(true);
        calculator.getValues()[1] = "0";
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("Expected textPane to show error", INFINITY.getValue(), calculator.getTextPaneValue());
        assertEquals("Expected valuesPosition to be 0", 0, calculator.getValuesPosition());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
    }

    @Test
    public void testContinuedDivisionWithWholeNumbers()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION.getValue());
        calculator.getValues()[0] = "10";
        calculator.setIsDividing(true);
        calculator.getValues()[1] = "2";
        calculator.performDivideButtonAction(actionEvent);
        assertEquals("Expected textPane to show 5 ÷", "5 ÷", calculator.getTextPaneValue());
        assertEquals("Expected values[0] to be 5", "5", calculator.getValues()[0]);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
    }

    @Test
    public void testPressedEqualsPrematurelyWithAddition()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.getValues()[0] = SIX.getValue();
        calculator.setIsAdding(true);
        calculator.performEqualsButtonAction(actionEvent);
        assertTrue("Expected isAdding to be true", calculator.isAdding());
        assertEquals("Expected textPane to be 6", SIX.getValue(), calculator.getValues()[0]);
        assertFalse("Expected isNegative to now be false", calculator.isNumberNegative());
    }

    @Test
    public void testPressedEqualsPrematurelyWithMultiplication()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.getValues()[0] = SIX.getValue();
        calculator.setIsMultiplying(true);
        calculator.performEqualsButtonAction(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        assertEquals("Expected textPane to be 6", SIX.getValue(), calculator.getValues()[0]);
        assertFalse("Expected isNegative to now be false", calculator.isNumberNegative());
    }

    @Test
    public void testPressedEqualsPrematurelyWithDivision()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.getValues()[0] = SIX.getValue();
        calculator.setIsDividing(true);
        calculator.performEqualsButtonAction(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        assertEquals("Expected textPane to be 6", SIX.getValue(), calculator.getValues()[0]);
        assertFalse("Expected isNegative to now be false", calculator.isNumberNegative());
    }

    @Test
    public void testPressedEqualsPrematurelyWithSubtraction()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.getValues()[0] = "-6";
        calculator.setIsSubtracting(true);
        calculator.performEqualsButtonAction(actionEvent);
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
        assertEquals("Expected textPane to be -6", "-6", calculator.getValues()[0]);
        assertFalse("Expected isNegative to now be false", calculator.isNumberNegative());
    }

    @Test
    public void testingMathPow() {
        double delta = 0.000001d;
        Number num = 8.0;
        assertEquals(num.doubleValue(), Math.pow(2,3), delta);
    }
}

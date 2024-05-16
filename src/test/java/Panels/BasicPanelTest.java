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

import static Types.CalculatorType.BASIC;
import static Types.Texts.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BasicPanelTest {

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
        calculator = new Calculator(BASIC);
        basicPanel = (BasicPanel) calculator.getCurrentPanel();
    }

    @After
    public void afterEach() {}

    @Test
    public void testCreatingBasicPanel()
    {
        assertEquals("Expected panel name to be " + BASIC.getValue(), BASIC.getValue(), new BasicPanel().getName());
    }

    @Test
    public void testCreatingBasicPanelWithCalculator()
    {
        assertEquals("Expected panel name to be " + BASIC.getValue(), BASIC.getValue(), new BasicPanel(calculator).getName());
    }

    @Test
    public void testShowingHelpShowsText()
    {
        basicPanel.showHelpPanel("This is a test");
    }

    @Test
    public void pressed1()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue());
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[{}] is not 1", 1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneWithoutAnyOperator());
        assertTrue("{} is not positive", calculator.isPositiveNumber(calculator.getTextPaneWithoutAnyOperator()));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(FIVE.getValue());
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[{}] is not 1", 1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("{} is not positive", calculator.isPositiveNumber(String.valueOf(calculator.getValues()[calculator.getValuesPosition()])));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[{}] is not 15", 15, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("{} is not positive", calculator.isPositiveNumber(String.valueOf(calculator.getValues()[calculator.getValuesPosition()])));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(NEGATE.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performNegateButtonActions(actionEvent);

        assertEquals("Values[{}] is not -1", -1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("textPane should be -1", "-1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("{} is not negative", calculator.isNegativeNumber(calculator.getValues()[calculator.getValuesPosition()]));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenNegateThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(NEGATE.getValue()).thenReturn(ADDITION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performNegateButtonActions(actionEvent);
        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[0] is not -1", -1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -1 +", "-1 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expecting isAdding to be set", calculator.isAdding());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5ThenNegateThenAddThen5ThenNegateThenEquals()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE.getValue()).thenReturn(FIVE.getValue()).thenReturn(NEGATE.getValue())
                .thenReturn(ADDITION.getValue()).thenReturn(FIVE.getValue()).thenReturn(NEGATE.getValue()).thenReturn(EQUALS.getValue());

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 0", 0, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 0", 0, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Values[0] is not -15", -15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -15", "-15", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 0", 0, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Values[0] is not -15", -15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -15 +", "-15 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertTrue("Expecting isAdding to be set", calculator.isAdding());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[1] is not 5", 5, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be 5", FIVE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Values[1] is not -5", -5, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be -5", "-5", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("textPane is not -20", "-20", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Values[1] is not empty", BLANK.getValue(), calculator.getValues()[1]);
        assertEquals("textPane should be -20", "-20", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedDot()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL.getValue());
        calculator.getValues()[0] = BLANK.getValue();
        calculator.getTextPane().setText(BLANK.getValue());
        basicPanel.performDecimalButtonActions(actionEvent);
        assertEquals("textPane is not as expected", "0.", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("buttonDot should be disabled", calculator.getButtonDecimal().isEnabled());
    }

    @Test
    public void pressed544ThenDot()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL.getValue());
        calculator.getTextPane().setText("544");
        calculator.getValues()[0] = "544";
        basicPanel.performDecimalButtonActions(actionEvent);
        assertEquals("textPane is not as expected", "544.", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Values[0]", "544.", calculator.getValues()[0]);
        assertFalse("buttonDot should be disabled", calculator.getButtonDecimal().isEnabled());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then2Then3Then4ThenDot()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(TWO.getValue()).thenReturn(THREE.getValue())
                .thenReturn("4").thenReturn(DECIMAL.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performDecimalButtonActions(actionEvent);
        assertEquals("textPane is not as expected", "1,234.", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Values[0]", "1234.", calculator.getValues()[0]);
        assertFalse("buttonDot should be disabled", calculator.getButtonDecimal().isEnabled());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedDotThen9()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL.getValue()).thenReturn(NINE.getValue());
        basicPanel.performDecimalButtonActions(actionEvent);

        assertEquals("Values[0] is not 0.", "0.", calculator.getValues()[0]);
        assertEquals("textPane should be 0.", "0.", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("buttonDot should be disabled", calculator.getButtonDecimal().isEnabled());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);
        if (calculator.isNegativeNumber(calculator.getValues()[0])) fail("Number is negative");

        assertEquals( 0.9f, Double.parseDouble(calculator.getValues()[0]), delta); // 0.8999999761581421
        assertEquals("textPane should be 0.9", "0.9", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("Expecting decimal to be disabled", calculator.isDotPressed());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenDotThen5ThenAddThen2()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(DECIMAL.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(ADDITION.getValue()).thenReturn(TWO.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performDecimalButtonActions(actionEvent);
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performAdditionButtonActions(actionEvent);
        basicPanel.performNumberButtonActions(actionEvent);
        assertTrue("Expected decimal button to be enabled", calculator.getButtonDecimal().isEnabled());
    }

    @Test
    public void pressed1ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(ADDITION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1 +", "1 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expecting isAdding to be set", calculator.isAdding());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(FIVE.getValue()).thenReturn(ADDITION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15 +", "15 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expecting isAdding to be set", calculator.isAdding());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(SUBTRACTION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performSubtractionButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1 -", "1 -", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expecting isSubtracting to be set", calculator.isSubtracting());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(FIVE.getValue()).thenReturn(SUBTRACTION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performSubtractionButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15 -", "15 -", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expecting isSubtracting to be set", calculator.isSubtracting());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5ThenSubtractThen5ThenEquals()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE.getValue()).thenReturn(FIVE.getValue()).thenReturn(SUBTRACTION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(EQUALS.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performSubtractionButtonActions(actionEvent);
        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15 -", "15 -", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isSubtracting to be set", calculator.isSubtracting());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[1] is not 5", 5, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be 5", FIVE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Values[0] is not blank", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("Values[1] is not empty", BLANK.getValue(), calculator.getValues()[1]);
        assertEquals("textPane should be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        verify(actionEvent, times(5)).getActionCommand();
    }

    @Test
    public void pressed1ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(MULTIPLICATION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performMultiplicationActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1 ✕", "1 ✕", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(FIVE.getValue()).thenReturn(MULTIPLICATION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15 ✕", "15 ✕", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then0ThenMultiplyThen1Then0ThenEquals()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(ONE.getValue()).thenReturn(ZERO.getValue()).thenReturn(MULTIPLICATION.getValue())
                .thenReturn(ONE.getValue()).thenReturn(ZERO.getValue()).thenReturn(EQUALS.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 10", 10, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performMultiplicationActions(actionEvent);
        assertEquals("Values[0] is not 10", 10, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 10 ✕", "10 ✕", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expecting isMultiplying to be set", calculator.isMultiplying());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[1] is not 1", 1, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[1] is not 10", 10, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("textPane is not 100", "100", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Values[1] is not empty", BLANK.getValue(), calculator.getValues()[1]);
        assertEquals("textPane should be 100", "100", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        verify(actionEvent, times(6)).getActionCommand();
    }

    @Test
    public void pressedAddWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION.getValue());
        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[{}] is not empty", BLANK.getValue(), calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenAddThenAddAgain()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(ADDITION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performAdditionButtonActions(actionEvent);
        basicPanel.performAdditionButtonActions(actionEvent); // no thenReturn necessary. Uses last value sent to method

        assertEquals("Values[{}] is not 1", ONE.getValue(), calculator.getValues()[0]);
        assertTrue("Expected isAdding to be true", calculator.isAdding());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedAddWithEInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION.getValue());
        calculator.getTextPane().setText(E.getValue());
        basicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Expected error message in textPane", E.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedSubtractWithEInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION.getValue());
        calculator.getTextPane().setText(E.getValue());
        basicPanel.performSubtractionButtonActions(actionEvent);
        assertEquals("Expected error message in textPane", E.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedMultiplyWithEInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION.getValue());
        calculator.getTextPane().setText(E.getValue());
        basicPanel.performMultiplicationActions(actionEvent);
        assertEquals("Expected error message in textPane", E.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedDivideWithEInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION.getValue());
        calculator.getTextPane().setText(E.getValue());
        basicPanel.performDivideButtonActions(actionEvent);
        assertEquals("Expected error message in textPane", E.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedAddWithNumberTooBigInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(ADDITION.getValue());
        calculator.getTextPane().setText(NUMBER_TOO_BIG.getValue());
        basicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Expected error message in textPane", NUMBER_TOO_BIG.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressed1ThenAddThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(ADDITION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(ADDITION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performAdditionButtonActions(actionEvent);
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Expected textPane shows 6 +", "6 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isAdd to be true", calculator.isAdding());
    }

    @Test
    public void pressedSubtractWithTextPaneBlankThen5()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION.getValue()).thenReturn(FIVE.getValue());
        basicPanel.performSubtractionButtonActions(actionEvent);
        assertEquals("Expected textPane to show - symbol", SUBTRACTION.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be blank", BLANK.getValue(), calculator.getValues()[0]);
        assertTrue("Expected isNegating to be true", calculator.isNegating());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected textPane to show -5", "-5", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be -5", "-5", calculator.getValues()[0]);
        assertFalse("Expected isNegating to be false", calculator.isNegating());
        assertTrue("Expected isNegative to be true", calculator.isNumberNegative());
    }

    @Test
    public void pressed5ThenAddThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue()).thenReturn(ADDITION.getValue()).thenReturn(SUBTRACTION.getValue());
        calculator.setNegating(false);
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performAdditionButtonActions(actionEvent);
        basicPanel.performSubtractionButtonActions(actionEvent);
        assertEquals("Expected textPane to -", SUBTRACTION.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be 5", FIVE.getValue(), calculator.getValues()[0]);
        assertTrue("Expected isNegating is true", calculator.isNegating());
    }

    @Test
    public void pressed1ThenSubtractThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(SUBTRACTION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(ADDITION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        basicPanel.performSubtractionButtonActions(actionEvent);
        assertTrue("Expected isSubtract to be true", calculator.isSubtracting());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        basicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Expected textPane shows -4 +", "-4 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isAdd to be true", calculator.isAdding());
    }

    @Test
    public void pressed1ThenSubtractThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(SUBTRACTION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(SUBTRACTION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        basicPanel.performSubtractionButtonActions(actionEvent);
        assertTrue("Expected isSubtract to be true", calculator.isSubtracting());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        basicPanel.performSubtractionButtonActions(actionEvent);
        assertEquals("Expected textPane shows -4 -", "-4 -", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
    }

    @Test
    public void pressed1ThenSubtractThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(SUBTRACTION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(MULTIPLICATION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        basicPanel.performSubtractionButtonActions(actionEvent);
        assertTrue("Expected isSubtract to be true", calculator.isSubtracting());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        basicPanel.performMultiplicationActions(actionEvent);
        assertEquals("Expected textPane shows -4 ✕", "-4 ✕", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
    }

    @Test
    public void pressed1ThenDivideThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(DIVISION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(SUBTRACTION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        basicPanel.performDivideButtonActions(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        basicPanel.performSubtractionButtonActions(actionEvent);
        assertEquals("Expected textPane shows 0.2 -", "0.2 -", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
    }

    @Test
    public void pressed1ThenSubtractThen5ThenDivide()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(SUBTRACTION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(DIVISION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        basicPanel.performSubtractionButtonActions(actionEvent);
        assertTrue("Expected isSubtract to be true", calculator.isSubtracting());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        basicPanel.performDivideButtonActions(actionEvent);
        assertEquals("Expected textPane shows -4 ÷", "-4 ÷", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(MULTIPLICATION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(ADDITION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        basicPanel.performMultiplicationActions(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        basicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Expected textPane shows 5 +", "5 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isAdd to be true", calculator.isAdding());
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(MULTIPLICATION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(SUBTRACTION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        basicPanel.performMultiplicationActions(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        basicPanel.performSubtractionButtonActions(actionEvent);
        assertEquals("Expected textPane shows 5 -", "5 -", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(MULTIPLICATION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(MULTIPLICATION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        basicPanel.performMultiplicationActions(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        basicPanel.performMultiplicationActions(actionEvent);
        assertEquals("Expected textPane shows 5 ✕", "5 ✕", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenDivide()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(MULTIPLICATION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(DIVISION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        basicPanel.performMultiplicationActions(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        basicPanel.performDivideButtonActions(actionEvent);
        assertEquals("Expected textPane shows 5 ÷", "5 ÷", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void pressed1ThenDivideThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(DIVISION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(MULTIPLICATION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        basicPanel.performDivideButtonActions(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        basicPanel.performMultiplicationActions(actionEvent);
        assertEquals("Expected textPane shows 0.2 ✕", "0.2 ✕", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
    }

    @Test
    public void pressed1ThenMultiplyThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(MULTIPLICATION.getValue()).thenReturn(MULTIPLICATION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performMultiplicationActions(actionEvent);
        basicPanel.performMultiplicationActions(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
    }

    @Test
    public void pressed1ThenDivideThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(DIVISION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(ADDITION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        basicPanel.performDivideButtonActions(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        basicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Expected textPane shows 0.2 +", "0.2 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isAdd to be true", calculator.isAdding());
    }

    @Test
    public void pressed1ThenDivideThen5ThenDivide()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(DIVISION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(DIVISION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        basicPanel.performDivideButtonActions(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", FIVE.getValue(), calculator.getValues()[1]);
        basicPanel.performDivideButtonActions(actionEvent);
        assertEquals("Expected textPane shows 0.2 ÷", "0.2 ÷", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void pressed1ThenDivideThenDivide()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(DIVISION.getValue()).thenReturn(DIVISION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performDivideButtonActions(actionEvent);
        basicPanel.performDivideButtonActions(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
    }
    
    @Test
    public void pressed1ThenDivideThen0()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(DIVISION.getValue()).thenReturn(ZERO.getValue()).thenReturn(EQUALS.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performDivideButtonActions(actionEvent);
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performEqualsButtonActions(actionEvent);
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertEquals("Expected values[0] to be blank", BLANK.getValue(), calculator.getValues()[0]);
    }

    @Test
    public void pressed1ThenAddThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(ADDITION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(SUBTRACTION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performAdditionButtonActions(actionEvent);
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performSubtractionButtonActions(actionEvent);
        calculator.setValuesPosition(1);
        assertEquals("Expected textPane shows 6 -", "6 -", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isSubtract to be true", calculator.isSubtracting());
    }

    @Test
    public void pressed1ThenAddThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(ADDITION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(MULTIPLICATION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performAdditionButtonActions(actionEvent);
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performMultiplicationActions(actionEvent);
        calculator.setValuesPosition(1);
        assertEquals("Expected textPane shows 6 ✕", "6 ✕", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
    }

    @Test
    public void pressed1ThenAddThen5ThenDivide()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(ADDITION.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(DIVISION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performAdditionButtonActions(actionEvent);
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performDivideButtonActions(actionEvent);
        calculator.setValuesPosition(1);
        assertEquals("Expected textPane shows 6 ÷", "6 ÷", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void pressedAddThenDivideWhenNumberIsMaximum()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION.getValue());
        calculator.getValues()[0] = "9999998";
        calculator.getValues()[1] = TWO.getValue();
        calculator.setAdding(true);
        calculator.setValuesPosition(1);
        basicPanel.performDivideButtonActions(actionEvent);
        assertEquals("Expected textPane shows 1.0E7", "1.0E7", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
    }

    @Test
    public void pressedSubtractWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION.getValue());
        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[{}] is not empty", BLANK.getValue(), calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedMultiplyWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(MULTIPLICATION.getValue());
        basicPanel.performMultiplicationActions(actionEvent);

        assertEquals("Values[{}] is not empty", BLANK.getValue(), calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedDivideWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(DIVISION.getValue());
        basicPanel.performDivideButtonActions(actionEvent);

        assertEquals("Values[{}] is not empty", BLANK.getValue(), calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void testAdditionWithWholeNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setAdding(true);
        calculator.getValues()[0] = FIVE.getValue();
        calculator.getValues()[1] = "10";
        basicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", "15", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void testAdditionWithDecimalNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setAdding(true);
        calculator.getValues()[0] = "5.5";
        calculator.getValues()[1] = "10";
        basicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", "15.5", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void testSubtractionFunctionalityWithWholeNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setSubtracting(true);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = "10";
        basicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", FIVE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void testSubtractionFunctionalityWithDecimalNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setSubtracting(true);
        calculator.getValues()[0] = "15.5";
        calculator.getValues()[1] = "10";
        basicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", "5.5", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void testMultiplicationFunctionalityWithWholeNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setMultiplying(true);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = "10";
        basicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", "150", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void testMultiplicationFunctionalityWithDecimalNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setMultiplying(true);
        calculator.getValues()[0] = "5.5";
        calculator.getValues()[1] = "10.2";
        basicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", "56.1", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void testDivisionFunctionalityWithWholeNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setDividing(true);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = FIVE.getValue();
        calculator.setCalculatorType(BASIC);
        basicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", THREE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void testDivisionFunctionalityWithDecimalNumbers() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setDividing(true);
        calculator.getValues()[0] = "15.5";
        calculator.getValues()[1] = FIVE.getValue();
        calculator.setCalculatorType(BASIC);
        basicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", "3.1", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void testDivisionBy0() {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.setDividing(true);
        calculator.getValues()[0] = "15.5";
        calculator.getValues()[1] = ZERO.getValue();
        calculator.setCalculatorType(BASIC);
        basicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Expected values[0] to be blank", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("Expected textPane to show error", INFINITY.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue());
        calculator.getTextPane().setText("35");
        calculator.getValues()[0] = "35";
        basicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 3", 3, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane does not equal 3", THREE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressed1Then5ThenDotThen6ThenAddThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue()).thenReturn(DELETE.getValue()).thenReturn(DELETE.getValue());
        calculator.getTextPane().setText("15.6 +");
        calculator.getValues()[0] = "15.6";
        //calculator.setDotPressed(true);
        calculator.getButtonDecimal().setEnabled(false);
        calculator.setAdding(true);
        basicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 15.6", 15.6, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 15.6", "15.6", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());
        assertFalse("Expected isAdding to be false", calculator.isAdding());

        basicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 15.", 15., Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 15.", "15.", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());

        basicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 15", 15, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected decimal button to be enabled", calculator.getButtonDecimal().isEnabled());
    }

    @Test
    public void pressed1ThenSubtractThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue());
        calculator.getTextPane().setText("1 -");
        calculator.getValues()[0] = ONE.getValue();
        calculator.getButtonDecimal().setEnabled(false);
        calculator.setSubtracting(true);
        basicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected decimal button to be enabled", calculator.getButtonDecimal().isEnabled());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
    }

    @Test
    public void pressedSubtractThen5ThenSubtractThen6ThenEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn(EQUALS.getValue());
        calculator.getTextPane().setText("-6");
        calculator.getValues()[0] = "-5";
        calculator.getValues()[1] = "-6";
        calculator.setNegating(true);
        calculator.setSubtracting(true);
        basicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Values[0] is not blank", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("textPane does not equal 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected decimal button to be enabled", calculator.getButtonDecimal().isEnabled());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
    }

    @Test
    public void pressedSubtractWithBlankTextPaneThen5ThenSubtractThen5()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION.getValue()).thenReturn(FIVE.getValue())
                .thenReturn(SUBTRACTION.getValue()).thenReturn(SUBTRACTION.getValue()).thenReturn(FIVE.getValue()).thenReturn(EQUALS.getValue());
        basicPanel.performSubtractionButtonActions(actionEvent);
        assertTrue("Expected isNegating to be true", calculator.isNegating());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not -5", -5, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal -5", "-5", calculator.getTextPaneWithoutNewLineCharacters());
        basicPanel.performSubtractionButtonActions(actionEvent);
        assertFalse("Expected isNegating to be false", calculator.isNegating());
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
        basicPanel.performSubtractionButtonActions(actionEvent);
        assertTrue("Expected isNegating to be true", calculator.isNegating());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Values[0] is not blank", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("textPane does not equal 0", ZERO.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressed1ThenMultiplyThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue());
        calculator.getTextPane().setText("1 ✕");
        calculator.getValues()[0] = ONE.getValue();
        calculator.getButtonDecimal().setEnabled(false);
        calculator.setMultiplying(true);
        basicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected decimal button to be enabled", calculator.getButtonDecimal().isEnabled());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
    }

    @Test
    public void pressed1ThenDivideThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue());
        calculator.getTextPane().setText("1 ÷");
        calculator.getValues()[0] = ONE.getValue();
        calculator.getButtonDecimal().setEnabled(false);
        calculator.setDividing(true);
        basicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected decimal button to be enabled", calculator.getButtonDecimal().isEnabled());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
    }

    @Test
    public void enteredANumberThenAddThen6DeleteThen5()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue()).thenReturn(FIVE.getValue());
        calculator.getTextPane().setText(SIX.getValue());
        calculator.getValues()[0] = "15.6";
        calculator.getValues()[1] = SIX.getValue();
        calculator.setValuesPosition(1);
        calculator.getButtonDecimal().setEnabled(true);
        calculator.setAdding(true);
        assertEquals("Values[1] is not 6", 6, Integer.parseInt(calculator.getValues()[1]));

        basicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 15.6", 15.6, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane is not blank", BLANK.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected decimal button is enabled", calculator.isDotPressed());
        assertTrue("Expected isAdding to be true", calculator.isAdding());

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[1] is not 5", 5, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane does not equal blank", FIVE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected decimal button is enabled", calculator.isDotPressed());
        assertTrue("Expected isAdding to be true", calculator.isAdding());
    }

    @Test
    public void pressingClearRestoresCalculatorToStartFunctionality()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR.getValue());
        basicPanel.performClearButtonActions(actionEvent);
        assertEquals("Values[0] should be 0", ZERO.getValue(), calculator.getValues()[0]);
        for ( int i=1; i<3; i++) {
            assertTrue("Values["+i+"] is not blank", calculator.getValues()[i].isBlank());
        }
        assertEquals("textPane is not 0", ZERO.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("isAddBool() is not false", calculator.isAdding());
        assertFalse("isSubBool() is not false", calculator.isSubtracting());
        assertFalse("isMulBool() is not false", calculator.isMultiplying());
        assertFalse("isDivBool() is not false", calculator.isDividing());
        assertEquals("Values position is not 0", 0, calculator.getValuesPosition());
        assertTrue("FirstNumBool is not true", calculator.isFirstNumber());
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
    }

    @Test
    public void pressingClearEntryWhenOnValuesPosition0ClearsTextPaneAndMainOperatorsAndDot()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR_ENTRY.getValue());
        calculator.getTextPane().setText("1088");
        calculator.setValuesPosition(0);
        calculator.getValues()[0] = "1088";
        basicPanel.performClearEntryButtonActions(actionEvent);

        assertTrue("textPane was not cleared", StringUtils.isBlank(calculator.getTextPaneWithoutNewLineCharacters()));
        assertFalse("Expected isAdding to be false", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertTrue("Expected decimal button to be enabled", calculator.isDotPressed());
        assertTrue("Expected to be on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressingClearEntryWhenOnValuesPosition1ClearsTextPaneAndValuesAt0AndMainOperatorsAndDotAndMovesValuesPositionTo0()
    {
        when(actionEvent.getActionCommand()).thenReturn(CLEAR_ENTRY.getValue());
        calculator.getTextPane().setText("1088");
        calculator.setAdding(true);
        calculator.setValuesPosition(1);
        calculator.getValues()[0] = "100";
        calculator.getValues()[1] = "1088";
        basicPanel.performClearEntryButtonActions(actionEvent);

        assertEquals("Expected textPane to show values[0]", "100", calculator.getValues()[0]);
        assertTrue("Expected isAdding to be true", calculator.isAdding());
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
        calculator.getTextPane().setText("1088 +");
        basicPanel.performClearEntryButtonActions(actionEvent);

        assertTrue("textPane was not cleared", StringUtils.isBlank(calculator.getTextPaneWithoutAnyOperator()));
        assertFalse("isAdding() expected to be false", calculator.isAdding());
    }

    @Test
    public void pressedSquaredButtonWithNoEntry()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARED.getValue());
        basicPanel.performSquaredButtonActions(actionEvent);
        assertEquals("Expected textPane to contain message", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected values[0] to be empty", calculator.getValues()[0].isEmpty());
    }

    @Test
    public void pressed5ThenSquaredButton()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue()).thenReturn(SQUARED.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 5", 5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 5", FIVE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performSquaredButtonActions(actionEvent);
        assertEquals("Values[0] is not 25", 25, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 25", "25", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed5ThenDotThen0ThenSquaredButton()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue()).thenReturn(DECIMAL.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(SQUARED.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performDecimalButtonActions(actionEvent);
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 5.5", 5.5, Double.parseDouble(calculator.getValues()[0]), delta); // 0.0
        assertEquals("textPane should be 5.5", "5.5", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performSquaredButtonActions(actionEvent);
        assertEquals("Values[0] is not 30.25", 30.25, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane should be 30.25", "30.25", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
        assertFalse("Expected decimal button to be disabled", calculator.isDotPressed());
    }

    @Test
    public void pressed5ThenNegateThenSquaredThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue()).thenReturn(NEGATE.getValue())
                .thenReturn(SQUARED.getValue()).thenReturn(NEGATE.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 5", 5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 5", FIVE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Values[0] is not -5", -5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -5", "-5", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isNumberNegative to be true", calculator.isNumberNegative());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performSquaredButtonActions(actionEvent);
        assertEquals("Values[0] is not 25", 25, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 25", "25", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("Expected isNumberNegative to be false", calculator.isNumberNegative());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Values[0] is not -25", -25, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -25", "-25", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isNumberNegative to be true", calculator.isNumberNegative());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

    }

    @Test
    public void pressed5ThenNegateThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue()).thenReturn(NEGATE.getValue()).thenReturn(NEGATE.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 5", 5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 5", FIVE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Values[0] is not -5", -5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -5", "-5", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
        assertTrue("Expected number to be negative", calculator.isNumberNegative());

        basicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Values[0] is not 5", 5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 5", FIVE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
        assertFalse("Expected number to be positive", calculator.isNumberNegative());
    }

    @Test
    public void pressedNegateOnTooBigNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(NEGATE.getValue());
        calculator.getTextPane().setText(E.getValue());
        basicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Expected error message in textPane", E.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedNegateWhenNoValueInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn(NEGATE.getValue());
        calculator.getTextPane().setText(BLANK.getValue());
        basicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Expected error message in textPane", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedSquareRootWithTextPaneShowingE()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT.getValue());
        calculator.getTextPane().setText(E.getValue());
        basicPanel.performSquareRootButtonActions(actionEvent);
        assertEquals("Expected textPane to show error", E.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedSquareRootWithTextPaneEmpty()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT.getValue());
        calculator.getTextPane().setText(BLANK.getValue());
        basicPanel.performSquareRootButtonActions(actionEvent);
        assertEquals("Expected textPane to show error message", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedSquareRootWhenCurrentValueIsNegative()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT.getValue());
        calculator.getTextPane().setText("-5");
        calculator.getValues()[0] = "-5;";
        calculator.setValuesPosition(0);
        basicPanel.performSquareRootButtonActions(actionEvent);
        assertEquals("Expected textPane to show error message", E.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedSquareRootWithValidCurrentValue()
    {
        when(actionEvent.getActionCommand()).thenReturn(SQUARE_ROOT.getValue());
        calculator.getTextPane().setText("25");
        calculator.getValues()[0] = "25";
        calculator.setValuesPosition(0);
        basicPanel.performSquareRootButtonActions(actionEvent);
        assertEquals("Expected result to be 5.0", 5.0, Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters()), delta);
    }

    @Test
    public void pressedMemoryStoreWhenTextPaneIsBlank()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_STORE.getValue());
        calculator.getTextPane().setText(BLANK.getValue());
        basicPanel.performMemoryStoreActions(actionEvent);
        assertTrue("Expected MemoryValues[0] to be blank", calculator.getMemoryValues()[0].isBlank());
        assertEquals("Expected textPane to show Enter a Number", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedMemoryStoreWhenTextPaneContainsValue()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_STORE.getValue());
        calculator.getTextPane().setText(FIVE.getValue());
        basicPanel.performMemoryStoreActions(actionEvent);
        assertEquals("Expected textPane to still show 5", FIVE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected MemoryValues[0] to be 5", FIVE.getValue(), calculator.getMemoryValues()[0]);
        assertSame("Expected MemoryPosition to be 1", 1, calculator.getMemoryPosition());
    }

    @Test
    public void pressedMemoryStoreWhenMemoryIsFullOverwritesMemory()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_STORE.getValue());
        calculator.getTextPane().setText(TWO.getValue());
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(10);
        assertEquals("Expected memoryValues[0] to be 15", "15", calculator.getMemoryValues()[0]);
        basicPanel.performMemoryStoreActions(actionEvent);
        assertEquals("Expected memoryValues[0] to be 2", TWO.getValue(), calculator.getMemoryValues()[0]);
    }

    @Test
    public void pressedMemoryStoreWhenTextPaneContainInvalidText()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_STORE.getValue());
        calculator.getTextPane().setText(E.getValue());
        assertTrue("Expected textPane to contain bad text", calculator.textPaneContainsBadText());
        basicPanel.performMemoryStoreActions(actionEvent);
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
        basicPanel.performMemoryRecallActions(actionEvent);
        assertEquals("Expected textPane to show 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("Expected memoryRecallPosition to be 1", 1, calculator.getMemoryRecallPosition());
        assertSame("Expected memoryPosition to be 6", 6, calculator.getMemoryPosition());
        basicPanel.performMemoryRecallActions(actionEvent);
        assertEquals("Expected textPane to show 534", "534", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("Expected memoryRecallPosition to be 2", 2, calculator.getMemoryRecallPosition());
        assertSame("Expected memoryPosition to be 6", 6, calculator.getMemoryPosition());
        basicPanel.performMemoryRecallActions(actionEvent);
        assertEquals("Expected textPane to show -9", "-9", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("Expected memoryRecallPosition to be 3", 3, calculator.getMemoryRecallPosition());
        assertSame("Expected memoryPosition to be 6", 6, calculator.getMemoryPosition());
        calculator.setMemoryRecallPosition(10);
        basicPanel.performMemoryRecallActions(actionEvent);
        assertEquals("Expected textPane to show 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("Expected memoryRecallPosition to be 1", 1, calculator.getMemoryRecallPosition());
        assertSame("Expected memoryPosition to be 6", 6, calculator.getMemoryPosition());
    }

    @Test
    public void pressedMemoryClear()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_CLEAR.getValue());
        calculator.getMemoryValues()[9] = "15";
        calculator.setMemoryPosition(10);
        basicPanel.performMemoryClearActions(actionEvent);
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
        calculator.getTextPane().setText(FIVE.getValue());
        calculator.setValuesPosition(0);
        basicPanel.performMemoryAdditionActions(actionEvent);
        assertEquals("Expected memoryValues[0] to be 15", "15", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 1", 1, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to show 5", FIVE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedMemoryAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_ADDITION.getValue());
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(BLANK.getValue());
        calculator.setValuesPosition(0);
        basicPanel.performMemoryAdditionActions(actionEvent);
        assertEquals("Expected memoryValues[0] to be 10", "10", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 1", 1, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to show error", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressed5ThenMemorySub()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_SUBTRACTION.getValue());
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(FIVE.getValue());
        calculator.setValuesPosition(0);
        basicPanel.performMemorySubtractionActions(actionEvent);
        assertEquals("Expected memoryValues[0] to be 10", "10", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 1", 1, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to show 5", FIVE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedMemorySub()
    {
        when(actionEvent.getActionCommand()).thenReturn(MEMORY_SUBTRACTION.getValue());
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText(BLANK.getValue());
        calculator.setValuesPosition(0);
        basicPanel.performMemorySubtractionActions(actionEvent);
        assertEquals("Expected memoryValues[0] to be 15", "15", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 1", 1, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to show error", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedFractionWhenTextPaneContainsBadText()
    {
        calculator.getTextPane().setText(ENTER_A_NUMBER.getValue());
        basicPanel.performFractionButtonActions(actionEvent);
        assertEquals("Expected textPane to show bad text", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressed0ThenFraction()
    {
        when(actionEvent.getActionCommand()).thenReturn(ZERO.getValue()).thenReturn(FRACTION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performFractionButtonActions(actionEvent);
        assertEquals("Expected textPane to be Infinity", INFINITY.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be blank", BLANK.getValue(), calculator.getValues()[0]);
    }

    @Test
    public void pressedFractionWhenTextPaneIsBlank()
    {
        calculator.getTextPane().setText(BLANK.getValue());
        basicPanel.performFractionButtonActions(actionEvent);
        assertEquals("Expected textPane to show bad text", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressed5ThenFraction()
    {
        when(actionEvent.getActionCommand()).thenReturn(FRACTION.getValue());
        calculator.getTextPane().setText(FIVE.getValue());
        basicPanel.performFractionButtonActions(actionEvent);
        assertEquals("Expected textPane to be 0.2", "0.2", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be 0.2", "0.2", calculator.getValues()[0]);
        assertFalse("Expected decimal button to be disabled", calculator.isDotPressed());
    }

    @Test
    public void pressedPercentWhenTextPaneContainsBadText()
    {
        calculator.getTextPane().setText(ENTER_A_NUMBER.getValue());
        basicPanel.performPercentButtonActions(actionEvent);
        assertEquals("Expected textPane to show bad text", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedPercentWhenTextPaneIsBlank()
    {
        calculator.getTextPane().setText(BLANK.getValue());
        basicPanel.performPercentButtonActions(actionEvent);
        assertEquals("Expected textPane to show bad text", ENTER_A_NUMBER.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedPercentWithPositiveNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue()).thenReturn(PERCENT.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performPercentButtonActions(actionEvent);
        assertEquals("Expected textPane to be 0.05", "0.05", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be 0.05", "0.05", calculator.getValues()[0]);
        assertFalse("Expected decimal to be disabled", calculator.isDotPressed());
    }

    @Test
    public void pressedPercentWithNegativeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SUBTRACTION.getValue()).thenReturn(FIVE.getValue()).thenReturn(PERCENT.getValue());
        basicPanel.performSubtractionButtonActions(actionEvent);
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performPercentButtonActions(actionEvent);
        assertEquals("Expected textPane to be -0.05", "-0.05", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be -0.05", "-0.05", calculator.getValues()[0]);
        assertFalse("Expected decimal to be disabled", calculator.isDotPressed());
    }

    @Test
    public void pressed1Then0Then6Then5ThenDotThen5Then4Then5Then7()
    {
        when(actionEvent.getActionCommand()).thenReturn(ONE.getValue()).thenReturn(ZERO.getValue())
                .thenReturn(SIX.getValue()).thenReturn(FIVE.getValue()).thenReturn(DECIMAL.getValue()).thenReturn(FIVE.getValue())
                .thenReturn("4").thenReturn(FIVE.getValue()).thenReturn(SEVEN.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        basicPanel.performNumberButtonActions(actionEvent);
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
        assertEquals("Expected values[0] to be 10", "10", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
        basicPanel.performNumberButtonActions(actionEvent);
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
        assertEquals("Expected values[0] to be 106", "106", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 106", "106", calculator.getTextPaneWithoutNewLineCharacters());
        basicPanel.performNumberButtonActions(actionEvent);
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
        assertEquals("Expected values[0] to be 1065", "1065", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065", "1,065", calculator.getTextPaneWithoutNewLineCharacters());
        basicPanel.performDecimalButtonActions(actionEvent);
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());
        assertEquals("Expected values[0] to be 1065.", "1065.", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065.", "1,065.", calculator.getTextPaneWithoutNewLineCharacters());
        basicPanel.performNumberButtonActions(actionEvent);
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());
        assertEquals("Expected values[0] to be 1065.5", "1065.5", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065.5", "1,065.5", calculator.getTextPaneWithoutNewLineCharacters());
        basicPanel.performNumberButtonActions(actionEvent);
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());
        assertEquals("Expected values[0] to be 1065.54", "1065.54", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065.54", "1,065.54", calculator.getTextPaneWithoutNewLineCharacters());
        basicPanel.performNumberButtonActions(actionEvent);
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());
        assertEquals("Expected values[0] to be 1065.545", "1065.545", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065.545", "1,065.545", calculator.getTextPaneWithoutNewLineCharacters());
        basicPanel.performNumberButtonActions(actionEvent);
        assertFalse("Expected decimal button to be disabled", calculator.getButtonDecimal().isEnabled());
        assertEquals("Expected values[0] to be 1065.5457", "1065.5457", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065.5457", "1,065.5457", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressed0WhenTextPaneHasBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(ZERO.getValue());
        calculator.getTextPane().setText(INFINITY.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 0", ZERO.getValue(), calculator.getValues()[0]);
        assertEquals("Expected textPane to show 0", ZERO.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedANumberWhenMaxLengthHasBeenMet()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue());
        calculator.getTextPane().setText("4,500,424");
        calculator.getValues()[0] = "4500424";
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected textPane to be the same", "4,500,424", calculator.getTextPaneWithoutNewLineCharacters());
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
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected textPane to be 2", TWO.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be 2", TWO.getValue(), calculator.getValues()[0]);
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected textPane to be 25", "25", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be 25", "25", calculator.getValues()[0]);
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());

        basicPanel.performPercentButtonActions(actionEvent);
        assertEquals("Expected textPane to be 0.25", "0.25", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be 0.25", "0.25", calculator.getValues()[0]);
        assertFalse("Expected decimal to be disabled", calculator.isDotPressed());

        basicPanel.performFractionButtonActions(actionEvent);
        assertEquals("Expected textPane to be 4", FOUR.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
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
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1", ONE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());

        basicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", ONE.getValue(), calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1 +", "1 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isAdding to be true", calculator.isAdding());
        assertEquals("Expected valuesPosition to be 1", 1, calculator.getValuesPosition());

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 2", TWO.getValue(), calculator.getValues()[1]);
        assertEquals("Expected textPane to be 2", TWO.getValue(), calculator.getTextPaneWithoutNewLineCharacters());

        basicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Expected values[0] to be blank", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("Expected values[1] to be blank", BLANK.getValue(), calculator.getValues()[1]);
        assertEquals("Expected textPane to be 3", THREE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("Expected isAdding to be false", calculator.isAdding());

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 5", FIVE.getValue(), calculator.getValues()[0]);
        assertEquals("Expected values[1] to be blank", BLANK.getValue(), calculator.getValues()[1]);
        assertEquals("Expected textPane to be 5", FIVE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());

    }

    @Test
    public void pressed4Then5ThenMSThenClearThen5ThenMAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn(FOUR.getValue()).thenReturn(FIVE.getValue())
                .thenReturn(MEMORY_STORE.getValue()).thenReturn(CLEAR_ENTRY.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(MEMORY_ADDITION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performMemoryStoreActions(actionEvent);
        basicPanel.performClearEntryButtonActions(actionEvent);
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performMemoryAdditionActions(actionEvent);
        assertEquals("Expected textPane to show 5", FIVE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected memories[0] to be 50", "50", calculator.getMemoryValues()[0]);
    }

    @Test
    public void pressed4Then5ThenMSThenClearThen5ThenMSub()
    {
        when(actionEvent.getActionCommand()).thenReturn(FOUR.getValue()).thenReturn(FIVE.getValue())
                .thenReturn(MEMORY_STORE.getValue()).thenReturn(CLEAR_ENTRY.getValue())
                .thenReturn(FIVE.getValue()).thenReturn(MEMORY_SUBTRACTION.getValue());
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performMemoryStoreActions(actionEvent);
        basicPanel.performClearEntryButtonActions(actionEvent);
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performMemorySubtractionActions(actionEvent);
        assertEquals("Expected textPane to show 5", FIVE.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected memories[0] to be 40", "40", calculator.getMemoryValues()[0]);
    }

    @Test
    public void testingMathPow() {
        double delta = 0.000001d;
        Number num = 8.0;
        assertEquals(num.doubleValue(), Math.pow(2,3), delta);
    }
}

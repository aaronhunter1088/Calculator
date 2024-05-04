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
    public void pressed1()
    {
        when(actionEvent.getActionCommand()).thenReturn("1");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[{}] is not 1", 1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutAnyOperator());
        assertTrue("{} is not positive", calculator.isPositiveNumber(calculator.getTextPaneWithoutAnyOperator()));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("5");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[{}] is not 1", 1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
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
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("±");
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
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("±").thenReturn("+");
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
                .thenReturn("1").thenReturn("5").thenReturn("±")
                .thenReturn("+").thenReturn("5").thenReturn("±").thenReturn("=");

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
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
        assertEquals("textPane should be 5", "5", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Values[1] is not -5", -5, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be -5", "-5", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performButtonEqualsActions(actionEvent);
        assertEquals("Values[0] is not -20", -20, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("Values[1] is not empty", "", calculator.getValues()[1]);
        assertEquals("textPane should be -20", "-20", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedDot()
    {
        when(actionEvent.getActionCommand()).thenReturn(".");
        calculator.getValues()[0] = "";
        calculator.getTextPane().setText("");
        basicPanel.performDotButtonActions(actionEvent);
        assertEquals("textPane is not as expected", "0.", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("buttonDot should be pressed", calculator.isDotPressed());
        assertFalse("buttonDot should be disabled", calculator.getButtonDot().isEnabled());
    }

    @Test
    public void pressed544ThenDot()
    {
        when(actionEvent.getActionCommand()).thenReturn(".");
        calculator.getTextPane().setText("544");
        calculator.getValues()[0] = "544";
        basicPanel.performDotButtonActions(actionEvent);
        assertEquals("textPane is not as expected", "544.", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Values[0]", "544.", calculator.getValues()[0]);
        assertTrue("Expecting isDotPressed to be set", calculator.isDotPressed());
        assertFalse("buttonDot should be disabled", calculator.getButtonDot().isEnabled());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedDotThen9()
    {
        when(actionEvent.getActionCommand()).thenReturn(".").thenReturn("9");
        basicPanel.performDotButtonActions(actionEvent);

        assertEquals("Values[0] is not 0.", "0.", calculator.getValues()[0]);
        assertEquals("textPane should be 0.", "0.", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("buttonDot should be disabled", calculator.getButtonDot().isEnabled());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);
        if (calculator.isNegativeNumber(calculator.getValues()[0])) fail("Number is negative");

        assertEquals( 0.9f, Double.parseDouble(calculator.getValues()[0]), delta); // 0.8999999761581421
        assertEquals("textPane should be 0.9", "0.9", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expecting isDotPressed to be set", calculator.isDotPressed());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("+");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
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
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("5").thenReturn("+");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
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
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("-");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
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
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("5").thenReturn("-");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
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
                .thenReturn("1").thenReturn("5").thenReturn("-")
                .thenReturn("5").thenReturn("=");
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
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
        assertEquals("textPane should be 5", "5", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performButtonEqualsActions(actionEvent);
        assertEquals("Values[0] is not 10", 10, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("Values[1] is not empty", "", calculator.getValues()[1]);
        assertEquals("textPane should be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        verify(actionEvent, times(5)).getActionCommand();
    }

    @Test
    public void pressed1ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("*");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performMultiplicationActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1 *", "1 *", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("5").thenReturn("*");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15 *", "15 *", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then0ThenMultiplyThen1Then0ThenEquals()
    {
        when(actionEvent.getActionCommand())
                .thenReturn("1").thenReturn("0").thenReturn("*")
                .thenReturn("1").thenReturn("0").thenReturn("=");
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 10", 10, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performMultiplicationActions(actionEvent);
        assertEquals("Values[0] is not 10", 10, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 10 *", "10 *", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expecting isMultiplying to be set", calculator.isMultiplying());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[1] is not 1", 1, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[1] is not 10", 10, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performButtonEqualsActions(actionEvent);
        assertEquals("Values[0] is not 100", 100, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("Values[1] is not empty", "", calculator.getValues()[1]);
        assertEquals("textPane should be 100", "100", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        verify(actionEvent, times(6)).getActionCommand();
    }

    @Test
    public void pressedAddWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn("+");
        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[{}] is not empty", "", calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", "Enter a Number", calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedSubtractWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn("-");
        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[{}] is not empty", "", calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", "Enter a Number", calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedMultiplyWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn("*");
        basicPanel.performMultiplicationActions(actionEvent);

        assertEquals("Values[{}] is not empty", "", calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", "Enter a Number", calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedDivideWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn("/");
        basicPanel.performDivideButtonActions(actionEvent);

        assertEquals("Values[{}] is not empty", "", calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", "Enter a Number", calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void testAdditionFunctionality() {
        when(actionEvent.getActionCommand()).thenReturn("=");
        calculator.setAdding(true);
        calculator.getValues()[0] = "5";
        calculator.getValues()[1] = "10";
        basicPanel.performButtonEqualsActions(actionEvent);
        assertEquals("Did not get back expected result", "15", calculator.getValues()[0]);
    }

    @Test
    public void testSubtractionFunctionality() {
        when(actionEvent.getActionCommand()).thenReturn("=");
        calculator.setSubtracting(true);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = "10";
        basicPanel.performButtonEqualsActions(actionEvent);
        assertEquals("Did not get back expected result", "5", calculator.getValues()[0]);
    }

    @Test
    public void testMultiplicationFunctionality() {
        when(actionEvent.getActionCommand()).thenReturn("=");
        calculator.setMultiplying(true);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = "10";
        basicPanel.performButtonEqualsActions(actionEvent);
        assertEquals("Did not get back expected result", "150", calculator.getValues()[0]);
    }

    @Test
    public void testDivisionFunctionality() {
        when(actionEvent.getActionCommand()).thenReturn("=");
        calculator.setDividing(true);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = "5";
        calculator.setCalculatorType(BASIC);
        basicPanel.performButtonEqualsActions(actionEvent);
        assertEquals("Did not get back expected result", "3", calculator.getValues()[0]);
    }

    @Test
    public void pressedDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn("←");
        calculator.getTextPane().setText("35");
        calculator.getValues()[0] = "35";
        basicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 3", 3, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane does not equal 3", "3", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressingClearRestoresCalculatorToStartFunctionality()
    {
        when(actionEvent.getActionCommand()).thenReturn("calculator");
        basicPanel.performClearButtonActions(actionEvent);
        for ( int i=0; i<3; i++) {
            assertTrue("Values@"+i+" is not blank", StringUtils.isBlank(calculator.getValues()[i]));
        }
        assertEquals("textPane is not 0", "0", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("isAddBool() is not false", calculator.isAdding());
        assertFalse("isSubBool() is not false", calculator.isSubtracting());
        assertFalse("isMulBool() is not false", calculator.isMultiplying());
        assertFalse("isDivBool() is not false", calculator.isDividing());
        assertEquals("Values position is not 0", 0, calculator.getValuesPosition());
        assertTrue("FirstNumBool is not true", calculator.isFirstNumber());
        assertFalse("DotButtonPressed is not false", calculator.isDotPressed());
        assertTrue("DotButton is not enabled", calculator.getButtonDot().isEnabled());
    }

    @Test
    public void pressingClearEntryClearsJustTheTextArea()
    {
        when(actionEvent.getActionCommand()).thenReturn("CE");
        calculator.getTextPane().setText("1088");
        calculator.getValues()[0] = "1088";
        basicPanel.performClearEntryButtonActions(actionEvent);

        assertTrue("textPane was not cleared", StringUtils.isBlank(calculator.getTextPaneWithoutNewLineCharacters()));
    }

    @Test
    public void pressingClearEntryAfterPressingAnOperatorResetsTextAreaAndOperator()
    {
        when(actionEvent.getActionCommand()).thenReturn("CE");
        calculator.getTextPane().setText("1088 +");
        basicPanel.performClearEntryButtonActions(actionEvent);

        assertTrue("textPane was not cleared", StringUtils.isBlank(calculator.getTextPaneWithoutAnyOperator()));
        assertFalse("isAdding() expected to be false", calculator.isAdding());
    }

    @Test
    public void pressedSquaredButtonWithNoEntry()
    {
        when(actionEvent.getActionCommand()).thenReturn("x²");
        basicPanel.performSquaredButtonActions(actionEvent);
        assertTrue("Expected textPane to be blank", calculator.getTextPane().getText().isBlank());
        assertTrue("Expected values[0] to be empty", calculator.getValues()[0].isEmpty());
    }

    @Test
    public void pressed5ThenSquaredButton()
    {
        when(actionEvent.getActionCommand()).thenReturn("5").thenReturn("x²");
        basicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 5", 5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 5", "5", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performSquaredButtonActions(actionEvent);
        assertEquals("Values[0] is not 25", 25, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 25", "25", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void testingMathPow() {
        double delta = 0.000001d;
        Number num = 8.0;
        assertEquals(num.doubleValue(), Math.pow(2,3), delta);
    }
}

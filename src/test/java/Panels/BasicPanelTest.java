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
    public void pressedButton1()
    {
        when(actionEvent.getActionCommand()).thenReturn("1");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[{}] is not 1", 1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("TextArea should be 1", "1", calculator.getTextAreaWithoutAnyOperator());
        assertTrue("{} is not positive", calculator.isPositiveNumber(calculator.getTextAreaWithoutAnyOperator()));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedButton1AndThenButton5()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("5");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[{}] is not 1", 1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("TextArea should be 1", "1", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("{} is not positive", calculator.isPositiveNumber(String.valueOf(calculator.getValues()[calculator.getValuesPosition()])));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[{}] is not 15", 15, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("TextArea should be 15", "15", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("{} is not positive", calculator.isPositiveNumber(String.valueOf(calculator.getValues()[calculator.getValuesPosition()])));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedButton1AndThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("±");
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performNegateButtonActions(actionEvent);

        assertEquals("Values[{}] is not -1", -1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("TextArea should be -1", "-1", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("{} is not negative", calculator.isNegativeNumber(calculator.getValues()[calculator.getValuesPosition()]));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedButton1AndThenNegateAndThenAdditionOperator()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("±").thenReturn("+");
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performNegateButtonActions(actionEvent);
        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[0] is not -1", -1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be -1 +", "-1 +", calculator.getTextAreaWithoutNewLineCharacters());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressingDotButtonOnly()
    {
        when(actionEvent.getActionCommand()).thenReturn(".");
        calculator.getValues()[0] = "";
        calculator.getTextArea().setText("");
        basicPanel.performDotButtonActions(actionEvent);
        assertEquals("textArea is not as expected", "0.", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("buttonDot should be pressed", calculator.isDotPressed());
        assertFalse("buttonDot should be disabled", calculator.getButtonDot().isEnabled());
    }

    @Test
    public void pressingDotButtonAfterNumberButtonReturnsNumberDot()
    {
        when(actionEvent.getActionCommand()).thenReturn(".");
        calculator.getTextArea().setText("544");
        calculator.getValues()[0] = "544";
        basicPanel.performDotButtonActions(actionEvent);
        assertEquals("textArea is not as expected", "544.", calculator.getTextAreaWithoutNewLineCharacters());
        assertEquals("Values[0]", "544.", calculator.getValues()[0]);
    }

    @Test
    public void pressedDotAndThenButton9()
    {
        when(actionEvent.getActionCommand()).thenReturn(".").thenReturn("9");
        basicPanel.performDotButtonActions(actionEvent);

        assertEquals("Values[0] is not 0.", "0.", calculator.getValues()[0]);
        assertEquals("TextArea should be 0.", "0.", calculator.getTextAreaWithoutNewLineCharacters());

        basicPanel.performNumberButtonActions(actionEvent);
        if (calculator.isNegativeNumber(calculator.getValues()[0])) fail("Number is negative");

        assertEquals( 0.9f, Double.parseDouble(calculator.getValues()[0]), delta); // 0.8999999761581421
        assertEquals("TextArea should be 0.9", "0.9", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedButton1AndThenButtonAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("+");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be 1", "1", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be 1 +", "1 +", calculator.getTextAreaWithoutNewLineCharacters());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedButton1AndThen5AndThenButtonAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("5").thenReturn("+");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be 1", "1", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be 15", "15", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be 15 +", "15 +", calculator.getTextAreaWithoutNewLineCharacters());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedButton1AndThenButtonSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("-");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be 1", "1", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be 1 -", "1 -", calculator.getTextAreaWithoutNewLineCharacters());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedButton1AndThen5AndThenButtonSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("5").thenReturn("-");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be 1", "1", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be 15", "15", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be 15 -", "15 -", calculator.getTextAreaWithoutNewLineCharacters());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedButton1AndThenButtonMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("*");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be 1", "1", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be 1 *", "1 *", calculator.getTextAreaWithoutNewLineCharacters());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedButton1AndThen5AndThenButtonMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("5").thenReturn("*");
        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be 1", "1", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be 15", "15", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea should be 15 *", "15 *", calculator.getTextAreaWithoutNewLineCharacters());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedButtonAddWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn("+");
        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[{}] is not empty", "", calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("TextArea should display 'Enter a Number'", "Enter a Number", calculator.getTextAreaWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedButtonSubtractWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn("-");
        basicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[{}] is not empty", "", calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("TextArea should display 'Enter a Number'", "Enter a Number", calculator.getTextAreaWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedButtonMultiplyWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn("*");
        basicPanel.performMultiplicationActions(actionEvent);

        assertEquals("Values[{}] is not empty", "", calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("TextArea should display 'Enter a Number'", "Enter a Number", calculator.getTextAreaWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedButtonDivideWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn("/");
        basicPanel.performDivideButtonActions(actionEvent);

        assertEquals("Values[{}] is not empty", "", calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("TextArea should display 'Enter a Number'", "Enter a Number", calculator.getTextAreaWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    //TODO: Rethink the 'order of operations' in this method, or move test
//    @Test
//    public void switchingFromProgrammerToBasicConvertsTextArea() throws CalculatorError
//    {
//        when(actionEvent.getActionCommand()).thenReturn("Programmer");
//        calculator.getTextArea().setText("00000100");
//        calculator.performTasksWhenChangingJPanels(actionEvent);
//        calculator.convertFromTypeToTypeOnValues(BINARY, DECIMAL, calculator.getTextAreaWithoutAnyOperator());
//        assertEquals("Did not convert from Binary to Decimal", "4", calculator.getTextAreaWithoutNewLineCharacters());
//    }

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
    public void testDeleteButtonFunctionality()
    {
        when(actionEvent.getActionCommand()).thenReturn("←");
        calculator.getTextArea().setText("35");
        calculator.getValues()[0] = "35";
        basicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 3", 3, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("TextArea does not equal 3", "3", calculator.getTextAreaWithoutNewLineCharacters());
    }

    @Test
    public void pressingClearRestoresCalculatorToStartFunctionality()
    {
        when(actionEvent.getActionCommand()).thenReturn("calculator");
        basicPanel.performClearButtonActions(actionEvent);
        for ( int i=0; i<3; i++) {
            assertTrue("Values@"+i+" is not blank", StringUtils.isBlank(calculator.getValues()[i]));
        }
        assertEquals("TextArea is not 0", "0", calculator.getTextAreaWithoutNewLineCharacters());
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
        calculator.getTextArea().setText("1088");
        calculator.getValues()[0] = "1088";
        basicPanel.performClearEntryButtonActions(actionEvent);

        assertTrue("textArea was not cleared", StringUtils.isBlank(calculator.getTextAreaWithoutNewLineCharacters()));
    }

    @Test
    public void pressingClearEntryAfterPressingAnOperatorResetsTextAreaAndOperator()
    {
        when(actionEvent.getActionCommand()).thenReturn("CE");
        calculator.getTextArea().setText("1088 +");
        basicPanel.performClearEntryButtonActions(actionEvent);

        assertTrue("TextArea was not cleared", StringUtils.isBlank(calculator.getTextAreaWithoutAnyOperator()));
        assertFalse("isAdding() expected to be false", calculator.isAdding());
    }

    @Test
    public void testingMathPow() {
        double delta = 0.000001d;
        Number num = 8.0;
        assertEquals(num.doubleValue(), Math.pow(2,3), delta);
    }
}

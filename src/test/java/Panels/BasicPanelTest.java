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
    public void testCreatingBasicPanel()
    {
        assertEquals("Expected panel name to be " + BASIC.getName(), BASIC.getName(), new BasicPanel().getName());
    }

    @Test
    public void testCreatingBasicPanelWithCalculator()
    {
        assertEquals("Expected panel name to be " + BASIC.getName(), BASIC.getName(), new BasicPanel(calculator).getName());
    }

    @Test
    public void testShowingHelpShowsText()
    {
        basicPanel.showHelpPanel("This is a test");
    }

    @Test
    public void pressed1()
    {
        when(actionEvent.getActionCommand()).thenReturn("1");
        BasicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[{}] is not 1", 1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutAnyOperator());
        assertTrue("{} is not positive", calculator.isPositiveNumber(calculator.getTextPaneWithoutAnyOperator()));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("5");
        BasicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[{}] is not 1", 1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("{} is not positive", calculator.isPositiveNumber(String.valueOf(calculator.getValues()[calculator.getValuesPosition()])));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[{}] is not 15", 15, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("{} is not positive", calculator.isPositiveNumber(String.valueOf(calculator.getValues()[calculator.getValuesPosition()])));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("±");
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performNegateButtonActions(actionEvent);

        assertEquals("Values[{}] is not -1", -1, Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]));
        assertEquals("textPane should be -1", "-1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("{} is not negative", calculator.isNegativeNumber(calculator.getValues()[calculator.getValuesPosition()]));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenNegateThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("±").thenReturn("+");
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performNegateButtonActions(actionEvent);
        BasicPanel.performAdditionButtonActions(actionEvent);

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

        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 0", 0, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 0", 0, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Values[0] is not -15", -15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -15", "-15", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 0", 0, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Values[0] is not -15", -15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -15 +", "-15 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertTrue("Expecting isAdding to be set", calculator.isAdding());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[1] is not 5", 5, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be 5", "5", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Values[1] is not -5", -5, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be -5", "-5", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performEqualsButtonActions(actionEvent);
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
        BasicPanel.performDotButtonActions(actionEvent);
        assertEquals("textPane is not as expected", "0.", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("buttonDot should be disabled", calculator.getButtonDot().isEnabled());
    }

    @Test
    public void pressed544ThenDot()
    {
        when(actionEvent.getActionCommand()).thenReturn(".");
        calculator.getTextPane().setText("544");
        calculator.getValues()[0] = "544";
        BasicPanel.performDotButtonActions(actionEvent);
        assertEquals("textPane is not as expected", "544.", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Values[0]", "544.", calculator.getValues()[0]);
        assertFalse("buttonDot should be disabled", calculator.getButtonDot().isEnabled());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then2Then3Then4ThenDot()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("2").thenReturn("3")
                .thenReturn("4").thenReturn(".");
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performDotButtonActions(actionEvent);
        assertEquals("textPane is not as expected", "1,234.", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Values[0]", "1234.", calculator.getValues()[0]);
        assertFalse("buttonDot should be disabled", calculator.getButtonDot().isEnabled());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedDotThen9()
    {
        when(actionEvent.getActionCommand()).thenReturn(".").thenReturn("9");
        BasicPanel.performDotButtonActions(actionEvent);

        assertEquals("Values[0] is not 0.", "0.", calculator.getValues()[0]);
        assertEquals("textPane should be 0.", "0.", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("buttonDot should be disabled", calculator.getButtonDot().isEnabled());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNumberButtonActions(actionEvent);
        if (calculator.isNegativeNumber(calculator.getValues()[0])) fail("Number is negative");

        assertEquals( 0.9f, Double.parseDouble(calculator.getValues()[0]), delta); // 0.8999999761581421
        assertEquals("textPane should be 0.9", "0.9", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expecting isDotPressed to be set", calculator.isDotPressed());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenDotThen5ThenAddThen2()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn(".")
                .thenReturn("5").thenReturn("+").thenReturn("2");
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performDotButtonActions(actionEvent);
        assertFalse("Expected dot button to be disabled", calculator.getButtonDot().isEnabled());
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performAdditionButtonActions(actionEvent);
        BasicPanel.performNumberButtonActions(actionEvent);
        assertTrue("Expected dot button to be enabled", calculator.getButtonDot().isEnabled());
    }

    @Test
    public void pressed1ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("+");
        BasicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1 +", "1 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expecting isAdding to be set", calculator.isAdding());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("5").thenReturn("+");
        BasicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15 +", "15 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expecting isAdding to be set", calculator.isAdding());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("-");
        BasicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performSubtractionButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1 -", "1 -", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expecting isSubtracting to be set", calculator.isSubtracting());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("5").thenReturn("-");
        BasicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performSubtractionButtonActions(actionEvent);

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
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performSubtractionButtonActions(actionEvent);
        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15 -", "15 -", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isSubtracting to be set", calculator.isSubtracting());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[1] is not 5", 5, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be 5", "5", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Values[0] is not 10", 10, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("Values[1] is not empty", "", calculator.getValues()[1]);
        assertEquals("textPane should be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        verify(actionEvent, times(5)).getActionCommand();
    }

    @Test
    public void pressed1ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("✕");
        BasicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performMultiplicationActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1 ✕", "1 ✕", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("5").thenReturn("✕");
        BasicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNumberButtonActions(actionEvent);

        assertEquals("Values[0] is not 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[0] is not 1", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 15 ✕", "15 ✕", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1Then0ThenMultiplyThen1Then0ThenEquals()
    {
        when(actionEvent.getActionCommand())
                .thenReturn("1").thenReturn("0").thenReturn("✕")
                .thenReturn("1").thenReturn("0").thenReturn("=");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 10", 10, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performMultiplicationActions(actionEvent);
        assertEquals("Values[0] is not 10", 10, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 10 ✕", "10 ✕", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expecting isMultiplying to be set", calculator.isMultiplying());
        assertSame("ValuesPosition should be 1", 1, calculator.getValuesPosition());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[1] is not 1", 1, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[1] is not 10", 10, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane should be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performEqualsButtonActions(actionEvent);
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
        BasicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[{}] is not empty", "", calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", "Enter a Number", calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed1ThenAddThenAddAgain()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("+");
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performAdditionButtonActions(actionEvent);
        BasicPanel.performAdditionButtonActions(actionEvent); // no thenReturn necessary. Uses last value sent to method

        assertEquals("Values[{}] is not 1", "1", calculator.getValues()[0]);
        assertTrue("Expected isAdding to be true", calculator.isAdding());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedAddWithEInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn("+");
        calculator.getTextPane().setText("E");
        BasicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Expected error message in textPane", "E", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedSubtractWithEInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn("+");
        calculator.getTextPane().setText("E");
        BasicPanel.performSubtractionButtonActions(actionEvent);
        assertEquals("Expected error message in textPane", "E", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedMultiplyWithEInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn("+");
        calculator.getTextPane().setText("E");
        BasicPanel.performMultiplicationActions(actionEvent);
        assertEquals("Expected error message in textPane", "E", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedDivideWithEInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn("+");
        calculator.getTextPane().setText("E");
        BasicPanel.performDivideButtonActions(actionEvent);
        assertEquals("Expected error message in textPane", "E", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedAddWithNumberTooBigInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn("+");
        calculator.getTextPane().setText("Number too big!");
        BasicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Expected error message in textPane", "Number too big!", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressed1ThenAddThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("+")
                .thenReturn("5").thenReturn("+");
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performAdditionButtonActions(actionEvent);
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Expected textPane shows 6 +", "6 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isAdd to be true", calculator.isAdding());
    }

    @Test
    public void pressedSubtractWithTextPaneBlank()
    {
        when(actionEvent.getActionCommand()).thenReturn("-");
        BasicPanel.performSubtractionButtonActions(actionEvent);
        assertEquals("Expected textPane to show - symbol", "-", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be blank", "", calculator.getValues()[0]);
        assertTrue("Expected isNegating is true", calculator.isNegating());
    }

    @Test
    public void pressed5ThenAddThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn("5").thenReturn("+").thenReturn("-");
        calculator.setNegating(false);
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performAdditionButtonActions(actionEvent);
        BasicPanel.performSubtractionButtonActions(actionEvent);
        assertEquals("Expected textPane to -", "-", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be 5", "5", calculator.getValues()[0]);
        assertTrue("Expected isNegating is true", calculator.isNegating());
    }

    @Test
    public void pressed1ThenSubtractThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("-")
                .thenReturn("5").thenReturn("+");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", "1", calculator.getValues()[0]);
        BasicPanel.performSubtractionButtonActions(actionEvent);
        assertTrue("Expected isSubtract to be true", calculator.isSubtracting());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", "5", calculator.getValues()[1]);
        BasicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Expected textPane shows -4 +", "-4 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isAdd to be true", calculator.isAdding());
    }

    @Test
    public void pressed1ThenSubtractThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("-")
                .thenReturn("5").thenReturn("-");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", "1", calculator.getValues()[0]);
        BasicPanel.performSubtractionButtonActions(actionEvent);
        assertTrue("Expected isSubtract to be true", calculator.isSubtracting());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", "5", calculator.getValues()[1]);
        BasicPanel.performSubtractionButtonActions(actionEvent);
        assertEquals("Expected textPane shows -4 -", "-4 -", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
    }

    @Test
    public void pressed1ThenSubtractThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("-")
                .thenReturn("5").thenReturn("✕");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", "1", calculator.getValues()[0]);
        BasicPanel.performSubtractionButtonActions(actionEvent);
        assertTrue("Expected isSubtract to be true", calculator.isSubtracting());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", "5", calculator.getValues()[1]);
        BasicPanel.performMultiplicationActions(actionEvent);
        assertEquals("Expected textPane shows -4 ✕", "-4 ✕", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
    }

    @Test
    public void pressed1ThenDivideThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("÷")
                .thenReturn("5").thenReturn("-");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", "1", calculator.getValues()[0]);
        BasicPanel.performDivideButtonActions(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", "5", calculator.getValues()[1]);
        BasicPanel.performSubtractionButtonActions(actionEvent);
        assertEquals("Expected textPane shows 0.2 -", "0.2 -", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void pressed1ThenSubtractThen5ThenDivide()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("-")
                .thenReturn("5").thenReturn("÷");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", "1", calculator.getValues()[0]);
        BasicPanel.performSubtractionButtonActions(actionEvent);
        assertTrue("Expected isSubtract to be true", calculator.isSubtracting());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", "5", calculator.getValues()[1]);
        BasicPanel.performDivideButtonActions(actionEvent);
        assertEquals("Expected textPane shows -4 ÷", "-4 ÷", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("✕")
                .thenReturn("5").thenReturn("+");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", "1", calculator.getValues()[0]);
        BasicPanel.performMultiplicationActions(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", "5", calculator.getValues()[1]);
        BasicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Expected textPane shows 5 +", "5 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isAdd to be true", calculator.isAdding());
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("✕")
                .thenReturn("5").thenReturn("-");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", "1", calculator.getValues()[0]);
        BasicPanel.performMultiplicationActions(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", "5", calculator.getValues()[1]);
        BasicPanel.performSubtractionButtonActions(actionEvent);
        assertEquals("Expected textPane shows 5 -", "5 -", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("✕")
                .thenReturn("5").thenReturn("✕");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", "1", calculator.getValues()[0]);
        BasicPanel.performMultiplicationActions(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", "5", calculator.getValues()[1]);
        BasicPanel.performMultiplicationActions(actionEvent);
        assertEquals("Expected textPane shows 5 ✕", "5 ✕", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
    }

    @Test
    public void pressed1ThenMultiplyThen5ThenDivide()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("✕")
                .thenReturn("5").thenReturn("÷");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", "1", calculator.getValues()[0]);
        BasicPanel.performMultiplicationActions(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", "5", calculator.getValues()[1]);
        BasicPanel.performDivideButtonActions(actionEvent);
        assertEquals("Expected textPane shows 5 ÷", "5 ÷", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void pressed1ThenDivideThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("÷")
                .thenReturn("5").thenReturn("✕");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", "1", calculator.getValues()[0]);
        BasicPanel.performDivideButtonActions(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", "5", calculator.getValues()[1]);
        BasicPanel.performMultiplicationActions(actionEvent);
        assertEquals("Expected textPane shows 0.2 ✕", "0.2 ✕", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
    }

    @Test
    public void pressed1ThenMultiplyThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("✕").thenReturn("✕");
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performMultiplicationActions(actionEvent);
        BasicPanel.performMultiplicationActions(actionEvent);
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        assertEquals("Expected values[0] to be 1", "1", calculator.getValues()[0]);
    }

    @Test
    public void pressed1ThenDivideThen5ThenAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("÷")
                .thenReturn("5").thenReturn("+");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", "1", calculator.getValues()[0]);
        BasicPanel.performDivideButtonActions(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", "5", calculator.getValues()[1]);
        BasicPanel.performAdditionButtonActions(actionEvent);
        assertEquals("Expected textPane shows 0.2 +", "0.2 +", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isAdd to be true", calculator.isAdding());
    }

    @Test
    public void pressed1ThenDivideThen5ThenDivide()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("÷")
                .thenReturn("5").thenReturn("÷");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be 1", "1", calculator.getValues()[0]);
        BasicPanel.performDivideButtonActions(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[1] to be 5", "5", calculator.getValues()[1]);
        BasicPanel.performDivideButtonActions(actionEvent);
        assertEquals("Expected textPane shows 0.2 ÷", "0.2 ÷", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void pressed1ThenDivideThenDivide()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("÷").thenReturn("÷");
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performDivideButtonActions(actionEvent);
        BasicPanel.performDivideButtonActions(actionEvent);
        assertTrue("Expected isDividing to be true", calculator.isDividing());
        assertEquals("Expected values[0] to be 1", "1", calculator.getValues()[0]);
    }
    
    @Test
    public void pressed1ThenDivideThen0()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("÷").thenReturn("0").thenReturn("=");
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performDivideButtonActions(actionEvent);
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performEqualsButtonActions(actionEvent);
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertEquals("Expected values[0] to be blank", "", calculator.getValues()[0]);
    }

    @Test
    public void pressed1ThenAddThen5ThenSubtract()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("+")
                .thenReturn("5").thenReturn("-");
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performAdditionButtonActions(actionEvent);
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performSubtractionButtonActions(actionEvent);
        calculator.setValuesPosition(1);
        assertEquals("Expected textPane shows 6 -", "6 -", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isSubtract to be true", calculator.isSubtracting());
    }

    @Test
    public void pressed1ThenAddThen5ThenMultiply()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("+")
                .thenReturn("5").thenReturn("✕");
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performAdditionButtonActions(actionEvent);
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performMultiplicationActions(actionEvent);
        calculator.setValuesPosition(1);
        assertEquals("Expected textPane shows 6 ✕", "6 ✕", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
    }

    @Test
    public void pressed1ThenAddThen5ThenDivide()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("+")
                .thenReturn("5").thenReturn("÷");
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performAdditionButtonActions(actionEvent);
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performDivideButtonActions(actionEvent);
        calculator.setValuesPosition(1);
        assertEquals("Expected textPane shows 6 ÷", "6 ÷", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void pressedSubtractWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn("-");
        BasicPanel.performAdditionButtonActions(actionEvent);

        assertEquals("Values[{}] is not empty", "", calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", "Enter a Number", calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedMultiplyWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn("✕");
        BasicPanel.performMultiplicationActions(actionEvent);

        assertEquals("Values[{}] is not empty", "", calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", "Enter a Number", calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedDivideWithNoNumberInput()
    {
        when(actionEvent.getActionCommand()).thenReturn("÷");
        BasicPanel.performDivideButtonActions(actionEvent);

        assertEquals("Values[{}] is not empty", "", calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("textPane should display 'Enter a Number'", "Enter a Number", calculator.getTextPaneWithoutAnyOperator());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void testAdditionWithWholeNumbers() {
        when(actionEvent.getActionCommand()).thenReturn("=");
        calculator.setAdding(true);
        calculator.getValues()[0] = "5";
        calculator.getValues()[1] = "10";
        BasicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", "15", calculator.getValues()[0]);
    }

    @Test
    public void testAdditionWithDecimalNumbers() {
        when(actionEvent.getActionCommand()).thenReturn("=");
        calculator.setAdding(true);
        calculator.getValues()[0] = "5.5";
        calculator.getValues()[1] = "10";
        BasicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", "15.5", calculator.getValues()[0]);
    }

    @Test
    public void testSubtractionFunctionalityWithWholeNumbers() {
        when(actionEvent.getActionCommand()).thenReturn("=");
        calculator.setSubtracting(true);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = "10";
        BasicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", "5", calculator.getValues()[0]);
    }

    @Test
    public void testSubtractionFunctionalityWithDecimalNumbers() {
        when(actionEvent.getActionCommand()).thenReturn("=");
        calculator.setSubtracting(true);
        calculator.getValues()[0] = "15.5";
        calculator.getValues()[1] = "10";
        BasicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", "5.5", calculator.getValues()[0]);
    }

    @Test
    public void testMultiplicationFunctionalityWithWholeNumbers() {
        when(actionEvent.getActionCommand()).thenReturn("=");
        calculator.setMultiplying(true);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = "10";
        BasicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", "150", calculator.getValues()[0]);
    }

    @Test
    public void testMultiplicationFunctionalityWithDecimalNumbers() {
        when(actionEvent.getActionCommand()).thenReturn("=");
        calculator.setMultiplying(true);
        calculator.getValues()[0] = "5.5";
        calculator.getValues()[1] = "10.2";
        BasicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", "56.1", calculator.getValues()[0]);
    }

    @Test
    public void testDivisionFunctionalityWithWholeNumbers() {
        when(actionEvent.getActionCommand()).thenReturn("=");
        calculator.setDividing(true);
        calculator.getValues()[0] = "15";
        calculator.getValues()[1] = "5";
        calculator.setCalculatorType(BASIC);
        BasicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", "3", calculator.getValues()[0]);
    }

    @Test
    public void testDivisionFunctionalityWithDecimalNumbers() {
        when(actionEvent.getActionCommand()).thenReturn("=");
        calculator.setDividing(true);
        calculator.getValues()[0] = "15.5";
        calculator.getValues()[1] = "5";
        calculator.setCalculatorType(BASIC);
        BasicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Did not get back expected result", "3.1", calculator.getValues()[0]);
    }

    @Test
    public void testDivisionBy0() {
        when(actionEvent.getActionCommand()).thenReturn("=");
        calculator.setDividing(true);
        calculator.getValues()[0] = "15.5";
        calculator.getValues()[1] = "0";
        calculator.setCalculatorType(BASIC);
        BasicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Expected values[0] to be blank", "", calculator.getValues()[0]);
        assertEquals("Expected textPane to show error", "Infinity", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn("⌫");
        calculator.getTextPane().setText("35");
        calculator.getValues()[0] = "35";
        BasicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 3", 3, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane does not equal 3", "3", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressed1Then5ThenDotThen6ThenAddThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn("⌫").thenReturn("⌫").thenReturn("⌫");
        calculator.getTextPane().setText("15.6 +");
        calculator.getValues()[0] = "15.6";
        //calculator.setDotPressed(true);
        calculator.getButtonDot().setEnabled(false);
        calculator.setAdding(true);
        BasicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 15.6", 15.6, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 15.6", "15.6", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("Expected dot button to be disabled", calculator.getButtonDot().isEnabled());
        assertFalse("Expected isAdding to be false", calculator.isAdding());

        BasicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 15.", 15., Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 15.", "15.", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("Expected dot button to be disabled", calculator.getButtonDot().isEnabled());

        BasicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 15", 15, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected dot button to be enabled", calculator.getButtonDot().isEnabled());
    }

    @Test
    public void pressed1ThenSubtractThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn("⌫");
        calculator.getTextPane().setText("1 -");
        calculator.getValues()[0] = "1";
        calculator.getButtonDot().setEnabled(false);
        calculator.setSubtracting(true);
        BasicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected dot button to be enabled", calculator.getButtonDot().isEnabled());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
    }

    @Test
    public void pressedSubtractThen5ThenSubtractThen6ThenEquals()
    {
        when(actionEvent.getActionCommand()).thenReturn("=");
        calculator.getTextPane().setText("-6");
        calculator.getValues()[0] = "-5";
        calculator.getValues()[1] = "-6";
        calculator.setNegating(true);
        calculator.setSubtracting(true);
        BasicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected dot button to be enabled", calculator.getButtonDot().isEnabled());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
    }

    @Test
    public void pressedSubtractWithBlankTextPaneThen5ThenSubtractThen5()
    {
        when(actionEvent.getActionCommand()).thenReturn("-").thenReturn("5").thenReturn("-").thenReturn("-").thenReturn("5").thenReturn("=");
        BasicPanel.performSubtractionButtonActions(actionEvent);
        assertTrue("Expected isNegating to be true", calculator.isNegating());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not -5", -5, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal -5", "-5", calculator.getTextPaneWithoutNewLineCharacters());
        BasicPanel.performSubtractionButtonActions(actionEvent);
        assertFalse("Expected isNegating to be false", calculator.isNegating());
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
        BasicPanel.performSubtractionButtonActions(actionEvent);
        assertTrue("Expected isNegating to be true", calculator.isNegating());
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performEqualsButtonActions(actionEvent);
        assertEquals("Values[0] is not 0", 0, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 0", "0", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressed1ThenMultiplyThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn("⌫");
        calculator.getTextPane().setText("1 ✕");
        calculator.getValues()[0] = "1";
        calculator.getButtonDot().setEnabled(false);
        calculator.setMultiplying(true);
        BasicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected dot button to be enabled", calculator.getButtonDot().isEnabled());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
    }

    @Test
    public void pressed1ThenDivideThenDelete()
    {
        when(actionEvent.getActionCommand()).thenReturn("⌫");
        calculator.getTextPane().setText("1 ÷");
        calculator.getValues()[0] = "1";
        calculator.getButtonDot().setEnabled(false);
        calculator.setDividing(true);
        BasicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 1", 1, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected dot button to be enabled", calculator.getButtonDot().isEnabled());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
    }

    @Test
    public void pressed1Then5ThenDotThen6ThenAddThen6DeleteThen5()
    {
        when(actionEvent.getActionCommand()).thenReturn("⌫").thenReturn("5");
        calculator.getTextPane().setText("6");
        calculator.getValues()[0] = "15.6";
        calculator.getValues()[1] = "6";
        calculator.setValuesPosition(1);
        //calculator.setDotPressed(false);
        calculator.getButtonDot().setEnabled(true);
        calculator.setAdding(true);
        assertEquals("Values[1] is not 6", 6, Integer.parseInt(calculator.getValues()[1]));

        BasicPanel.performDeleteButtonActions(actionEvent);
        assertEquals("Values[0] is not 15.6", 15.6, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane does not equal blank", "", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("Expected dot button not to be pressed", calculator.isDotPressed());
        assertTrue("Expected dot button to be enabled", calculator.getButtonDot().isEnabled());
        assertTrue("Expected isAdding to be true", calculator.isAdding());

        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[1] is not 5", 5, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("textPane does not equal blank", "5", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("Expected dot button not to be pressed", calculator.isDotPressed());
        assertTrue("Expected dot button to be enabled", calculator.getButtonDot().isEnabled());
        assertTrue("Expected isAdding to be true", calculator.isAdding());
    }

    @Test
    public void pressingClearRestoresCalculatorToStartFunctionality()
    {
        when(actionEvent.getActionCommand()).thenReturn("calculator");
        BasicPanel.performClearButtonActions(actionEvent);
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
    public void pressingClearEntryWhenOnValuesPosition0ClearsTextPaneAndMainOperatorsAndDot()
    {
        when(actionEvent.getActionCommand()).thenReturn("CE");
        calculator.getTextPane().setText("1088");
        calculator.setValuesPosition(0);
        calculator.getValues()[0] = "1088";
        BasicPanel.performClearEntryButtonActions(actionEvent);

        assertTrue("textPane was not cleared", StringUtils.isBlank(calculator.getTextPaneWithoutNewLineCharacters()));
        assertFalse("Expected isAdding to be false", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertFalse("Expected dot button to not be pushed", calculator.isDotPressed());
        assertTrue("Expected dot button to be enabled", calculator.getButtonDot().isEnabled());
        assertTrue("Expected to be on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressingClearEntryWhenOnValuesPosition1ClearsTextPaneAndValuesAt0AndMainOperatorsAndDotAndMovesValuesPositionTo0()
    {
        when(actionEvent.getActionCommand()).thenReturn("CE");
        calculator.getTextPane().setText("1088");
        calculator.setAdding(true);
        calculator.setValuesPosition(1);
        calculator.getValues()[0] = "100";
        calculator.getValues()[1] = "1088";
        BasicPanel.performClearEntryButtonActions(actionEvent);

        assertTrue("textPane was not cleared", StringUtils.isBlank(calculator.getTextPaneWithoutNewLineCharacters()));
        assertTrue("Expected isAdding to be true", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
        assertFalse("Expected dot button to not be pushed", calculator.isDotPressed());
        assertTrue("Expected dot button to be enabled", calculator.getButtonDot().isEnabled());
        assertTrue("Expected to be on the firstNumber", calculator.isFirstNumber());
        assertEquals("Expected to be at valuesPosition:0", 0, calculator.getValuesPosition());
    }

    @Test
    public void pressingClearEntryAfterPressingAnOperatorResetsTextPaneAndOperator()
    {
        when(actionEvent.getActionCommand()).thenReturn("CE");
        calculator.getTextPane().setText("1088 +");
        BasicPanel.performClearEntryButtonActions(actionEvent);

        assertTrue("textPane was not cleared", StringUtils.isBlank(calculator.getTextPaneWithoutAnyOperator()));
        assertFalse("isAdding() expected to be false", calculator.isAdding());
    }

    @Test
    public void pressedSquaredButtonWithNoEntry()
    {
        when(actionEvent.getActionCommand()).thenReturn("x²");
        BasicPanel.performSquaredButtonActions(actionEvent);
        assertEquals("Expected textPane to contain message", "Enter a Number", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected values[0] to be empty", calculator.getValues()[0].isEmpty());
    }

    @Test
    public void pressed5ThenSquaredButton()
    {
        when(actionEvent.getActionCommand()).thenReturn("5").thenReturn("x²");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 5", 5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 5", "5", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performSquaredButtonActions(actionEvent);
        assertEquals("Values[0] is not 25", 25, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 25", "25", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressed5ThenDotThen0ThenSquaredButton()
    {
        when(actionEvent.getActionCommand()).thenReturn("5").thenReturn(".")
                .thenReturn("5").thenReturn("x²");
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performDotButtonActions(actionEvent);
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 5.5", 5.5, Double.parseDouble(calculator.getValues()[0]), delta); // 0.0
        assertEquals("textPane should be 5.5", "5.5", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performSquaredButtonActions(actionEvent);
        assertEquals("Values[0] is not 30.25", 30.25, Double.parseDouble(calculator.getValues()[0]), delta);
        assertEquals("textPane should be 30.25", "30.25", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
        assertFalse("Expected dot button to be disabled", calculator.isDotPressed());
    }

    @Test
    public void pressed5ThenNegateThenSquaredThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn("5").thenReturn("±")
                .thenReturn("x²").thenReturn("±");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 5", 5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 5", "5", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Values[0] is not -5", -5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -5", "-5", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isNumberNegative to be true", calculator.isNumberNegative());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performSquaredButtonActions(actionEvent);
        assertEquals("Values[0] is not 25", 25, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 25", "25", calculator.getTextPaneWithoutNewLineCharacters());
        assertFalse("Expected isNumberNegative to be false", calculator.isNumberNegative());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Values[0] is not -25", -25, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -25", "-25", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("Expected isNumberNegative to be true", calculator.isNumberNegative());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

    }

    @Test
    public void pressed5ThenNegateThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn("5").thenReturn("±").thenReturn("±");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Values[0] is not 5", 5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 5", "5", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        BasicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Values[0] is not -5", -5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be -5", "-5", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
        assertTrue("Expected number to be negative", calculator.isNumberNegative());

        BasicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Values[0] is not 5", 5, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("textPane should be 5", "5", calculator.getTextPaneWithoutNewLineCharacters());
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
        assertFalse("Expected number to be positive", calculator.isNumberNegative());
    }

    @Test
    public void pressedNegateOnTooBigNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn("±");
        calculator.getTextPane().setText("E");
        BasicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Expected error message in textPane", "E", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedNegateWhenNoValueInTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn("±");
        calculator.getTextPane().setText("");
        BasicPanel.performNegateButtonActions(actionEvent);
        assertEquals("Expected error message in textPane", "Enter a Number", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedSquareRootWithTextPaneShowingE()
    {
        when(actionEvent.getActionCommand()).thenReturn("√");
        calculator.getTextPane().setText("E");
        BasicPanel.performSquareRootButtonActions(actionEvent);
        assertEquals("Expected textPane to show error", "E", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedSquareRootWithTextPaneEmpty()
    {
        when(actionEvent.getActionCommand()).thenReturn("√");
        calculator.getTextPane().setText("");
        BasicPanel.performSquareRootButtonActions(actionEvent);
        assertEquals("Expected textPane to show error message", "Enter a Number", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedSquareRootWhenCurrentValueIsNegative()
    {
        when(actionEvent.getActionCommand()).thenReturn("√");
        calculator.getTextPane().setText("-5");
        calculator.getValues()[0] = "-5;";
        calculator.setValuesPosition(0);
        BasicPanel.performSquareRootButtonActions(actionEvent);
        assertEquals("Expected textPane to show error message", "E", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedSquareRootWithValidCurrentValue()
    {
        when(actionEvent.getActionCommand()).thenReturn("√");
        calculator.getTextPane().setText("25");
        calculator.getValues()[0] = "25";
        calculator.setValuesPosition(0);
        BasicPanel.performSquareRootButtonActions(actionEvent);
        assertEquals("Expected result to be 5.0", 5.0, Double.parseDouble(calculator.getTextPaneWithoutNewLineCharacters()), delta);
    }

    @Test
    public void pressedMemoryStoreWhenTextPaneIsBlank()
    {
        when(actionEvent.getActionCommand()).thenReturn("MS");
        calculator.getTextPane().setText("");
        BasicPanel.performMemoryStoreActions(actionEvent);
        assertTrue("Expected MemoryValues[0] to be blank", calculator.getMemoryValues()[0].isBlank());
        assertEquals("Expected textPane to show Enter a Number", "Enter a Number", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedMemoryStoreWhenTextPaneContainsValue()
    {
        when(actionEvent.getActionCommand()).thenReturn("MS");
        calculator.getTextPane().setText("5");
        BasicPanel.performMemoryStoreActions(actionEvent);
        assertEquals("Expected textPane to still show 5", "5", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected MemoryValues[0] to be 5", "5", calculator.getMemoryValues()[0]);
        assertSame("Expected MemoryPosition to be 1", 1, calculator.getMemoryPosition());
    }

    @Test
    public void pressedMemoryStoreWhenMemoryIsFullOverwritesMemory()
    {
        when(actionEvent.getActionCommand()).thenReturn("MS");
        calculator.getTextPane().setText("2");
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(10);
        assertEquals("Expected memoryValues[0] to be 15", "15", calculator.getMemoryValues()[0]);
        BasicPanel.performMemoryStoreActions(actionEvent);
        assertEquals("Expected memoryValues[0] to be 2", "2", calculator.getMemoryValues()[0]);
    }

    @Test
    public void pressedMemoryStoreWhenTextPaneContainInvalidText()
    {
        when(actionEvent.getActionCommand()).thenReturn("MS");
        calculator.getTextPane().setText("E");
        assertTrue("Expected textPane to contain bad text", calculator.textPaneContainsBadText());
        BasicPanel.performMemoryStoreActions(actionEvent);
        assertTrue("Expected MemoryValues[0] to be blank", calculator.getMemoryValues()[0].isBlank());
    }

    @Test
    public void pressedMemoryRecall()
    {
        when(actionEvent.getActionCommand()).thenReturn("MR").thenReturn("MR")
                .thenReturn("MR").thenReturn("MR");
        calculator.getMemoryValues()[0] = "15";
        calculator.getMemoryValues()[1] = "534";
        calculator.getMemoryValues()[2] = "-9";
        calculator.getMemoryValues()[3] = "75";
        calculator.getMemoryValues()[4] = "2";
        calculator.getMemoryValues()[5] = "1080";
        calculator.setMemoryPosition(6);
        calculator.setMemoryRecallPosition(0);
        BasicPanel.performMemoryRecallActions(actionEvent);
        assertEquals("Expected textPane to show 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("Expected memoryRecallPosition to be 1", 1, calculator.getMemoryRecallPosition());
        assertSame("Expected memoryPosition to be 6", 6, calculator.getMemoryPosition());
        BasicPanel.performMemoryRecallActions(actionEvent);
        assertEquals("Expected textPane to show 534", "534", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("Expected memoryRecallPosition to be 2", 2, calculator.getMemoryRecallPosition());
        assertSame("Expected memoryPosition to be 6", 6, calculator.getMemoryPosition());
        BasicPanel.performMemoryRecallActions(actionEvent);
        assertEquals("Expected textPane to show -9", "-9", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("Expected memoryRecallPosition to be 3", 3, calculator.getMemoryRecallPosition());
        assertSame("Expected memoryPosition to be 6", 6, calculator.getMemoryPosition());
        calculator.setMemoryRecallPosition(10);
        BasicPanel.performMemoryRecallActions(actionEvent);
        assertEquals("Expected textPane to show 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
        assertSame("Expected memoryRecallPosition to be 1", 1, calculator.getMemoryRecallPosition());
        assertSame("Expected memoryPosition to be 6", 6, calculator.getMemoryPosition());
    }

    @Test
    public void pressedMemoryClear()
    {
        when(actionEvent.getActionCommand()).thenReturn("MC");
        calculator.getMemoryValues()[9] = "15";
        calculator.setMemoryPosition(10);
        BasicPanel.performMemoryClearActions(actionEvent);
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
        when(actionEvent.getActionCommand()).thenReturn("M+");
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText("5");
        calculator.setValuesPosition(0);
        BasicPanel.performMemoryAddActions(actionEvent);
        assertEquals("Expected memoryValues[0] to be 15", "15", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 1", 1, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to show 15", "15", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedMemoryAdd()
    {
        when(actionEvent.getActionCommand()).thenReturn("M+");
        calculator.getMemoryValues()[0] = "10";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText("");
        calculator.setValuesPosition(0);
        BasicPanel.performMemoryAddActions(actionEvent);
        assertEquals("Expected memoryValues[0] to be 10", "10", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 1", 1, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to be blank", "", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedMemoryAddWhenNoMemoriesSaved()
    {
        when(actionEvent.getActionCommand()).thenReturn("M+");
        calculator.getMemoryValues()[0] = "";
        calculator.setMemoryPosition(0);
        calculator.getTextPane().setText("10");
        calculator.setValuesPosition(0);
        BasicPanel.performMemoryAddActions(actionEvent);
        assertEquals("Expected memoryValues[0] to be blank", "", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 0", 0, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressed5ThenMemorySub()
    {
        when(actionEvent.getActionCommand()).thenReturn("M-");
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText("5");
        calculator.setValuesPosition(0);
        BasicPanel.performMemorySubtractionActions(actionEvent);
        assertEquals("Expected memoryValues[0] to be 10", "10", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 1", 1, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to show 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedMemorySubWhenNoMemoriesSaved()
    {
        when(actionEvent.getActionCommand()).thenReturn("M-");
        calculator.getMemoryValues()[0] = "";
        calculator.setMemoryPosition(0);
        calculator.getTextPane().setText("5");
        calculator.setValuesPosition(0);
        BasicPanel.performMemorySubtractionActions(actionEvent);
        assertEquals("Expected memoryValues[0] to be blank", "", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 0", 0, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to show 5", "5", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedMemorySub()
    {
        when(actionEvent.getActionCommand()).thenReturn("M-");
        calculator.getMemoryValues()[0] = "15";
        calculator.setMemoryPosition(1);
        calculator.getTextPane().setText("");
        calculator.setValuesPosition(0);
        BasicPanel.performMemorySubtractionActions(actionEvent);
        assertEquals("Expected memoryValues[0] to be 15", "15", calculator.getMemoryValues()[0]);
        assertSame("Expected memoryPosition to be 1", 1, calculator.getMemoryPosition());
        assertSame("Expected memoryRecallPosition to be 0", 0, calculator.getMemoryRecallPosition());
        assertEquals("Expected textPane to be blank", "", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedFractionWhenTextPaneContainsBadText()
    {
        calculator.getTextPane().setText("Enter a Number");
        BasicPanel.performFractionButtonActions(actionEvent);
        assertEquals("Expected textPane to show bad text", "Enter a Number", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressed0ThenFraction()
    {
        when(actionEvent.getActionCommand()).thenReturn("0").thenReturn("⅟x");
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performFractionButtonActions(actionEvent);
        assertEquals("Expected textPane to be Infinity", "Infinity", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be blank", "", calculator.getValues()[0]);
    }

    @Test
    public void pressedFractionWhenTextPaneIsBlank()
    {
        calculator.getTextPane().setText("");
        BasicPanel.performFractionButtonActions(actionEvent);
        assertEquals("Expected textPane to show bad text", "Enter a Number", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressed5ThenFraction()
    {
        when(actionEvent.getActionCommand()).thenReturn("⅟x");
        calculator.getTextPane().setText("5");
        BasicPanel.performFractionButtonActions(actionEvent);
        assertEquals("Expected textPane to be 0.2", "0.2", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be 0.2", "0.2", calculator.getValues()[0]);
        assertTrue("Expected dot button to be pressed", calculator.isDotPressed());
        assertFalse("Expected dot button to be disabled", calculator.getButtonDot().isEnabled());
    }

    @Test
    public void pressedPercentWhenTextPaneContainsBadText()
    {
        calculator.getTextPane().setText("Enter a Number");
        BasicPanel.performPercentButtonActions(actionEvent);
        assertEquals("Expected textPane to show bad text", "Enter a Number", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedPercentWhenTextPaneIsBlank()
    {
        calculator.getTextPane().setText("");
        BasicPanel.performPercentButtonActions(actionEvent);
        assertEquals("Expected textPane to show bad text", "Enter a Number", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressedPercentWithPositiveNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn("5").thenReturn("%");
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performPercentButtonActions(actionEvent);
        assertEquals("Expected textPane to be 0.05", "0.05", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be 0.05", "0.05", calculator.getValues()[0]);
        assertTrue("Expected dot to be pressed", calculator.isDotPressed());
        assertFalse("Expected dot button to be disabled", calculator.getButtonDot().isEnabled());
    }

    @Test
    public void pressedPercentWithNegativeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn("-").thenReturn("5").thenReturn("%");
        BasicPanel.performSubtractionButtonActions(actionEvent);
        BasicPanel.performNumberButtonActions(actionEvent);
        BasicPanel.performPercentButtonActions(actionEvent);
        assertEquals("Expected textPane to be -0.05", "-0.05", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be -0.05", "-0.05", calculator.getValues()[0]);
        assertTrue("Expected dot to be pressed", calculator.isDotPressed());
        assertFalse("Expected dot button to be disabled", calculator.getButtonDot().isEnabled());
    }

    @Test
    public void pressed1Then0Then6Then5ThenDotThen5Then4Then5Then7()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("0")
                .thenReturn("6").thenReturn("5").thenReturn(".").thenReturn("5")
                .thenReturn("4").thenReturn("5").thenReturn("7");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertFalse("Expected isDotPressed to be false", calculator.isDotPressed());
        assertEquals("Expected values[0] to be 1", "1", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1", "1", calculator.getTextPaneWithoutNewLineCharacters());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertFalse("Expected isDotPressed to be false", calculator.isDotPressed());
        assertEquals("Expected values[0] to be 10", "10", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertFalse("Expected isDotPressed to be false", calculator.isDotPressed());
        assertEquals("Expected values[0] to be 106", "106", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 106", "106", calculator.getTextPaneWithoutNewLineCharacters());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertFalse("Expected isDotPressed to be false", calculator.isDotPressed());
        assertEquals("Expected values[0] to be 1065", "1065", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065", "1,065", calculator.getTextPaneWithoutNewLineCharacters());
        BasicPanel.performDotButtonActions(actionEvent);
        assertTrue("Expected isDotPressed to be true", calculator.isDotPressed());
        assertFalse("Expected dot button to be disabled", calculator.getButtonDot().isEnabled());
        assertEquals("Expected values[0] to be 1065.", "1065.", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065.", "1,065.", calculator.getTextPaneWithoutNewLineCharacters());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertTrue("Expected isDotPressed to be true", calculator.isDotPressed());
        assertFalse("Expected dot button to be disabled", calculator.getButtonDot().isEnabled());
        assertEquals("Expected values[0] to be 1065.5", "1065.5", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065.5", "1,065.5", calculator.getTextPaneWithoutNewLineCharacters());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertTrue("Expected isDotPressed to be true", calculator.isDotPressed());
        assertFalse("Expected dot button to be disabled", calculator.getButtonDot().isEnabled());
        assertEquals("Expected values[0] to be 1065.54", "1065.54", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065.54", "1,065.54", calculator.getTextPaneWithoutNewLineCharacters());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertTrue("Expected isDotPressed to be true", calculator.isDotPressed());
        assertFalse("Expected dot button to be disabled", calculator.getButtonDot().isEnabled());
        assertEquals("Expected values[0] to be 1065.545", "1065.545", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065.545", "1,065.545", calculator.getTextPaneWithoutNewLineCharacters());
        BasicPanel.performNumberButtonActions(actionEvent);
        assertTrue("Expected isDotPressed to be true", calculator.isDotPressed());
        assertFalse("Expected dot button to be disabled", calculator.getButtonDot().isEnabled());
        assertEquals("Expected values[0] to be 1065.5457", "1065.5457", calculator.getValues()[0]);
        assertEquals("Expected textPane to be 1,065.5457", "1,065.5457", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void pressed0WhenTextPaneHasBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn("0");
        calculator.getTextPane().setText("Infinity");
        BasicPanel.performNumberButtonActions(actionEvent);
        assertEquals("Expected values[0] to be blank", "", calculator.getValues()[0]);
        assertEquals("Expected textPane to show Infinity", "Infinity", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void testingMathPow() {
        double delta = 0.000001d;
        Number num = 8.0;
        assertEquals(num.doubleValue(), Math.pow(2,3), delta);
    }
}

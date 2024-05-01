package Panels;

import Calculators.Calculator_v4;
import Calculators.CalculatorError;
import Calculators.Calculator_v4Tests;
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
import static Types.CalculatorBase.BINARY;
import static Types.CalculatorBase.DECIMAL;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BasicPanelTest {

    static { System.setProperty("appName", "BasicPanelTest"); }
    private static Logger LOGGER;
    private static Calculator_v4 calculator;
    private static BasicPanel basicPanel;
    private int intResult;
    private double doubleResult;

    @Mock
    ActionEvent actionEvent;

    @BeforeClass
    public static void beforeAll() throws Exception
    {
        LOGGER = LogManager.getLogger(BasicPanelTest.class.getSimpleName());
    }

    @AfterClass
    public static void afterAll()
    {
        LOGGER.info("Finished running " + Calculator_v4Tests.class.getSimpleName());
    }

    @Before
    public void beforeEach() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException {
        LOGGER.info("setting up each before...");
        MockitoAnnotations.initMocks(this);
        calculator = new Calculator_v4(BASIC);
        basicPanel = (BasicPanel) calculator.getCurrentPanel();
    }

    @After
    public void afterEach() {}

    @Test
    public void pressedButton1()
    {
        when(actionEvent.getActionCommand()).thenReturn("1");
        basicPanel.performNumberButtonActions(actionEvent);
        intResult = Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]);

        assertEquals("Values[{}] is not 1", 1, intResult);
        assertEquals("TextArea should be 1", "1", calculator.getTextAreaWithoutAnyOperator());
        assertTrue("{} is not positive", calculator.isPositiveNumber(calculator.getTextAreaWithoutAnyOperator()));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedButton1AndThenButton5()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("5");
        basicPanel.performNumberButtonActions(actionEvent);
        intResult = Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]);

        assertEquals("Values[{}] is not 1", 1, intResult);
        assertEquals("TextArea should be 1", "1", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("{} is not positive", calculator.isPositiveNumber(String.valueOf(intResult)));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());

        basicPanel.performNumberButtonActions(actionEvent);
        intResult = Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]);

        assertEquals("Values[{}] is not 15", 15, intResult);
        assertEquals("TextArea should be 15", "15", calculator.getTextAreaWithoutNewLineCharacters());
        assertTrue("{} is not positive", calculator.isPositiveNumber(String.valueOf(intResult)));
        assertTrue("We are not on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void pressedButton1AndThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("±");
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performNegateButtonActions(actionEvent);
        intResult = Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]);

        assertEquals("Values[{}] is not -1", -1, intResult);
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
        intResult = Integer.parseInt(calculator.getValues()[0]);

        assertEquals("Values[0] is not -1", -1, intResult);
        assertEquals("TextArea should be -1 +", "-1 +", calculator.getTextAreaWithoutNewLineCharacters());
        assertFalse("We are still on the firstNumber", calculator.isFirstNumber());
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
        //doubleResult = Double.parseDouble(calculator.getValues()[0]);

        //assertEquals( 0.9f, doubleResult, 0.0f); Expected 0.8999999761581421
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


//    @Test
//    public void switchingFromProgrammerToBasicConvertsTextArea() throws CalculatorError
//    {
//        calculator.getTextArea().setText("00000100");
//        calculator.convertFromTypeToTypeOnValues(BINARY, DECIMAL, calculator.getTextAreaWithoutAnyOperator());
//        assertEquals("Did not convert from Binary to Decimal", "4", calculator.getTextAreaValueWithoutAnything().toString());
//    }

    @Test
    public void testingMathPow() {
        double delta = 0.000001d;
        Number num = 8.0;
        assertEquals(num.doubleValue(), Math.pow(2,3), delta);
    }
}

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

import java.awt.event.ActionEvent;

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
    private int numberResult;

    @Mock
    ActionEvent actionEvent;

    @BeforeClass
    public static void beforeAll() throws Exception
    {
        LOGGER = LogManager.getLogger(BasicPanelTest.class.getSimpleName());
        calculator = new Calculator_v4(BASIC);
        basicPanel = (BasicPanel) calculator.getCurrentPanel();
    }

    @AfterClass
    public static void afterAll()
    {
        LOGGER.info("Finished running " + Calculator_v4Tests.class.getSimpleName());
    }

    @Before
    public void beforeEach()
    {
        LOGGER.info("setting up each before...");
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void afterEach() {
        LOGGER.info("Clearing previous values...");
        numberResult = 0;
        LOGGER.info("numberResult zeroed out: " + numberResult);
        calculator.resetValues();
        LOGGER.info("Values[0] cleared: " + calculator.getValues()[0]);
    }

    @Test
    public void pressedButton1()
    {
        when(actionEvent.getActionCommand()).thenReturn("1");
        basicPanel.performNumberButtonActions(actionEvent);
        numberResult = Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]);

        assertEquals("Values[{}] is not 1", 1, numberResult);
        assertEquals("TextArea should be 1", "1", calculator.getTextAreaWithoutAnything());
        assertTrue("{} is not positive", calculator.isPositiveNumber(calculator.getTextAreaWithoutAnything()));
    }

    @Test
    public void pressedButton1AndThenButton5()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("5");
        basicPanel.performNumberButtonActions(actionEvent);
        numberResult = Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]);

        assertEquals("Values[{}] is not 1", 1, numberResult);
        assertEquals("TextArea should be 1", "1", calculator.getTextAreaValue().toString());
        assertTrue("{} is not positive", calculator.isPositiveNumber(String.valueOf(numberResult)));

        basicPanel.performNumberButtonActions(actionEvent);
        numberResult = Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]);

        assertEquals("Values[{}] is not 15", 15, numberResult);
        assertEquals("TextArea should be 15", "15", calculator.getTextAreaValue().toString());
        assertTrue("{} is not positive", calculator.isPositiveNumber(String.valueOf(numberResult)));
    }

    @Test
    public void pressedButton1AndThenNegate()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("±");
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performNegateButtonActions(actionEvent);
        numberResult = Integer.parseInt(calculator.getValues()[calculator.getValuesPosition()]);

        assertEquals("Values[{}] is not -1", -1, numberResult);
        assertEquals("TextArea should be -1", "-1", calculator.getTextAreaValue().toString());
        assertTrue("{} is not negative", calculator.isNegativeNumber(calculator.getValues()[calculator.getValuesPosition()]));
    }

    @Test
    public void pressedButton1AndThenNegateAndThenAdditionOperator()
    {
        when(actionEvent.getActionCommand()).thenReturn("1").thenReturn("±").thenReturn("+");
        basicPanel.performNumberButtonActions(actionEvent);
        basicPanel.performNegateButtonActions(actionEvent);
        basicPanel.performAdditionButtonActions(actionEvent);
        numberResult = Integer.parseInt(calculator.getValues()[0]);

        assertEquals("Values[0] is not -1", -1, numberResult);
        assertEquals("TextArea should be -1 +", "-1 +", calculator.getTextareaValueWithoutAnything().toString());
    }






    @Test
    public void switchingFromProgrammerToBasicConvertsTextArea() throws CalculatorError
    {
        calculator.getTextArea().setText("00000100");
        calculator.convertFromTypeToTypeOnValues(BINARY, DECIMAL, calculator.getTextAreaWithoutAnything());
        assertEquals("Did not convert from Binary to Decimal", "4", calculator.getTextareaValueWithoutAnything().toString());
    }

    @Test
    public void testingMathPow() {
        double delta = 0.000001d;
        Number num = 8.0;
        assertEquals(num.doubleValue(), Math.pow(2,3), delta);
    }
}

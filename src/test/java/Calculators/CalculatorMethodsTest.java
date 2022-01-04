package Calculators;

import Types.CalculatorType;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.event.ActionEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static Types.CalculatorBase.BINARY;
import static Types.CalculatorBase.DECIMAL;

@RunWith(MockitoJUnitRunner.class)
public class CalculatorMethodsTest {

    private static Calculator c;
    private String number;
    private boolean result;

    @Mock
    ActionEvent ae;

    @BeforeClass
    public static void setup() throws Exception
    {
        System.setProperty("appName", "CalculatorMethodsTest");
        c = new Calculator();
    }

    @Before
    public void setupBefore()
    {
        System.out.println("valuesLength: " + c.values.length);
        c.values = new String[]{"","","",""};
    }

    @Test
    public void testAdditionFunctionality() {
        c.setValues(new String[]{"5", "10"});
        c.addition();
        assertEquals("Did not get back expected result", "15", c.values[0]);
    }

    @Test
    public void testSubtractionFunctionality() {
        c.setValues(new String[]{"15", "10"});
        c.subtract();
        assertEquals("Did not get back expected result", "5", c.values[0]);
    }

    @Test
    public void testMultiplicationFunctionality() {
        c.setValues(new String[]{"15", "10"});
        c.multiply();
        assertEquals("Did not get back expected result", "150", c.values[0]);
    }

    @Test
    public void testDivisionFunctionality() {
        c.setValues(new String[]{"15", "5"});
        c.setCalculatorType(CalculatorType.BASIC);
        c.divide();
        assertEquals("Did not get back expected result", "3", c.values[0]);
    }

    @Test
    public void testConvertingToPositive()
    {
        number = "-5.02";
        number = c.convertToPositive(number);
        assertEquals("Number is not positive", "5.02", number);
    }

    @Test
    public void testIsPositiveReturnsTrue()
    {
        number = "6";
        result = c.isPositiveNumber(number);
        assertTrue("IsPositive did not return true", result);
    }

    @Test
    public void testIsPositiveReturnsFalse()
    {
        number = "-6";
        result = c.isPositiveNumber(number);
        assertFalse("IsPositive did not return false", result);
    }

    @Test
    public void testConvertingToNegative()
    {
        number = "5.02";
        number = c.convertToNegative(number);
        assertEquals("Number is not negative", "-5.02", number);
    }

    @Test
    public void testIsNegativeReturnsTrue()
    {
        number = "-6";
        result = c.isNegativeNumber(number);
        assertTrue("IsNegative did not return true", result);
    }

    @Test
    public void testIsNegativeReturnsFalse()
    {
        number = "6";
        result = c.isNegativeNumber(number);
        assertFalse("IsNegative did not return false", result);
    }

    @Test
    public void testIsDecimalReturnsTrue()
    {
        number = "5.02";
        boolean answer = c.isDecimal(number);
        assertTrue("Number is a whole number", answer);
    }

    @Test
    public void testIsDecimalReturnsFalse()
    {
        number = "5";
        boolean answer = c.isDecimal(number);
        assertFalse("Number is a decimal", answer);
    }

    @Test
    public void testDeleteButtonFunctionality()
    {
        c.textArea.setText("\n35");
        c.setTextareaValue(new StringBuffer().append("35"));
        c.values[0] = "35";
//        Calculator.DeleteButtonHandler handler = c.getDeleteButtonHandler();
        when(ae.getActionCommand()).thenReturn("DEL");
        c.performDeleteButtonActions(ae);
        assertEquals("TextArea does not equal 3", "\n3", c.textArea.getText());
    }

    @Test
    public void pressingDotButtonFirstReturns0DotFromTextarea()
    {
        c.values[0] = "";
        when(ae.getActionCommand()).thenReturn(".");
        c.performDotButtonActions(ae);
        assertEquals("textarea is not as expected", "\n0.", "\n"+c.textareaValue);
        assertEquals("textArea is not 0.", "0.", c.getTextAreaWithoutAnything());
        assertTrue("dotButtonPressed is not true", c.dotButtonPressed);
    }

    @Test
    public void pressingDotButtonForNegativeReturnsNegativeNumberDot()
    {
        when(ae.getActionCommand()).thenReturn(".");
        c.textArea.setText("-5"); // should become -5.
        c.numberIsNegative = true;
        c.dotButtonPressed = true;
        c.valuesPosition = 0;
        c.values[0] = "-5";
        c.performDotButtonActions(ae);
        assertEquals("textAreaValue is not as expected", "-5.", c.textareaValue.toString());
        assertEquals("values[0] is not as expected", "-5.", c.values[0]);
        assertTrue("dotButtonPressed is not true", c.dotButtonPressed);
    }

    @Test
    public void pressingDotButtonAfterNumberButtonReturnsNumberDot()
    {
        //c.textArea.setText(c.addNewLineCharacters(1)+"5");
        //c.values[0] = c.getTextAreaWithoutAnything();
        //c.textareaValue = new StringBuffer(c.getTextAreaWithoutAnything());
        //when(ae.getActionCommand()).thenReturn(".");
        //c.performDotButtonActions(ae);
        //assertEquals("Textarea is not as expected", ".5", c.textareaValue.toString());
        //assertTrue("dotButtonPressed is not true", c.dotButtonPressed);
        c.valuesPosition = 0;
        //c.textArea.setText(c.addNewLineCharacters(1)+"54");
        //c.values[c.valuesPosition] = c.getTextAreaWithoutAnything();
        //c.textareaValue = new StringBuffer(c.getTextAreaWithoutAnything());
        //when(ae.getActionCommand()).thenReturn(".");
        //c.performDotButtonActions(ae);
        //assertEquals("Textarea is not as expected", "54.", c.textareaValue.toString());
        //assertEquals("Values[valuesPosition]", "54.", c.values[c.valuesPosition]);

        c.textArea.setText(c.addNewLineCharacters(1)+"544");
        c.values[c.valuesPosition] = c.getTextAreaWithoutAnything();
        c.textareaValue = new StringBuffer(c.getTextAreaWithoutAnything());
        when(ae.getActionCommand()).thenReturn(".");
        c.performDotButtonActions(ae);
        assertEquals("textAreaValue is not as expected", "544.", c.textareaValue.toString());
        assertEquals("Values[0]", "544.", c.values[c.valuesPosition]);
    }

    @Test
    public void pressingClearRestoresCalculatorToStartFunctionality()
    {
        when(ae.getActionCommand()).thenReturn("C");
        c.performClearButtonActions(ae);
        for ( int i=0; i<3; i++) {
            assertTrue("Values@"+i+" is not blank", StringUtils.isBlank(c.values[i]));
        }
        assertEquals("TextArea is not 0", "\n0", c.textArea.getText());
        assertEquals("textarea is not 0", "0", c.textareaValue.toString());
        assertFalse("addBool is not false", c.addBool);
        assertFalse("subBool is not false", c.subBool);
        assertFalse("mulBool is not false", c.mulBool);
        assertFalse("divBool is not false", c.divBool);
        assertEquals("Values position is not 0", 0, c.valuesPosition);
        assertTrue("FirstNumBool is not true", c.firstNumBool);
        assertFalse("DotButtonPressed is not false", c.dotButtonPressed);
        assertTrue("DotButton is not enabled", c.buttonDot.isEnabled());
    }

    @Test
    public void pressingClearEntryClearsJustTheTextArea()
    {
        c.textArea.setText("1088");
        c.values[0] = "1088";
//        Calculator.ClearEntryButtonHandler handler = c.getClearEntryButtonHandler();
        when(ae.getActionCommand()).thenReturn("CE");
        c.performClearEntryButtonActions(ae);
        assertTrue("TextArea was not cleared", StringUtils.isBlank(c.textArea.getText()));
        assertTrue("textarea was not cleared", StringUtils.isBlank(c.textareaValue));
    }

    @Test
    public void pressingClearEntryAfterPressingAnOperatorResetsTextAreaAndOperator()
    {
        c.textArea.setText("1088 +");
//        Calculator.ClearEntryButtonHandler handler = c.getClearEntryButtonHandler();
        when(ae.getActionCommand()).thenReturn("CE");
        c.performClearEntryButtonActions(ae);
        assertTrue("TextArea was not cleared", StringUtils.isBlank(c.getTextAreaWithoutAnything()));
        assertTrue("textarea was not cleared", StringUtils.isBlank(c.textareaValue));
        assertFalse("addBool expected to be false", c.addBool);
    }

    @Test
    public void methodResetOperatorsWithFalseResultsInAllOperatorsBeingFalse()
    {
        c.resetBasicOperators(false);
        assertFalse("addBool is not false", c.addBool);
        assertFalse("subBool is not false", c.subBool);
        assertFalse("mulBool is not false", c.mulBool);
        assertFalse("divBool is not false", c.divBool);
    }

    @Test
    public void methodResetOperatorsWithTrueResultsInAllOperatorsBeingTrue()
    {
        c.resetBasicOperators(true);
        assertTrue("addBool is not true", c.addBool);
        assertTrue("subBool is not true", c.subBool);
        assertTrue("mulBool is not true", c.mulBool);
        assertTrue("divBool is not true", c.divBool);
    }

    @Test
    public void methodConfirmRunsAfterEachButtonPress()
    {
        // TODO: This method requires testing all buttons are pushed and the confirm method runs
    }

    @Test
    public void methodConfirmWithAMessageRunsAfterEachButtonPress()
    {
        // TODO: This method requires testing all buttons are pushed and the confirm method runs
    }

    @Test
    public void testConvertingBinaryToDecimalWorks() throws CalculatorError
    {
        // Test that this work
        //String test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "10000000");
        //assertEquals(Integer.parseInt(test), 128);
        // Test another number
        //test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "11111111");
        //assertEquals(Integer.parseInt(test), 255);
        // Make sure this returns the same as above, although i believe this entry by the user is impossible
        //test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "11111111 ");
        //assertEquals(Integer.parseInt(test), 255);
        // Test to make sure an incomplete number returns appropriately
        //test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "101");
        //assertEquals(Integer.parseInt(test), 5);
        // Test to make sure a WORD BINARY entry returns appropriately
        c.isButtonByteSet = false;
        c.isButtonWordSet = true;
        String test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, new String("00000001 00000000"));
        assertEquals(Integer.parseInt(test), 256);

    }

    @Test
    public void testSetImageIconsWorksAsExpected()
    {
        c.setImageIcons();
    }

}

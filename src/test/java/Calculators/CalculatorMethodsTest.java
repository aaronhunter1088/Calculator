package Calculators;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.event.ActionEvent;

import static Types.CalculatorType.BASIC;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static Types.CalculatorBase.BINARY;
import static Types.CalculatorBase.DECIMAL;

@RunWith(MockitoJUnitRunner.class)
public class CalculatorMethodsTest {

    private static Calculator_v4 c;
    private String number;
    private boolean result;

    @Mock
    ActionEvent ae;

    @BeforeClass
    public static void setup() throws Exception
    {
        System.setProperty("appName", "CalculatorMethodsTest");
        c = new Calculator_v4();
    }

    @Before
    public void setupBefore()
    {
        System.out.println("valuesLength: " + c.getValues().length);
        c.resetValues();
    }

    @Test
    public void testAdditionFunctionality() {
        c.getValues()[0] = "5";
        c.getValues()[1] = "10";
        //c.addition(); IN PANEL
        assertEquals("Did not get back expected result", "15", c.getValues()[0]);
    }

    @Test
    public void testSubtractionFunctionality() {
        c.getValues()[0] = "15";
        c.getValues()[1] = "10";
        //c.subtract(); IN PANEL
        assertEquals("Did not get back expected result", "5", c.getValues()[0]);
    }

    @Test
    public void testMultiplicationFunctionality() {
        c.getValues()[0] = "15";
        c.getValues()[1] = "10";
        //c.multiply(); IN PANEL
        assertEquals("Did not get back expected result", "150", c.getValues()[0]);
    }

    @Test
    public void testDivisionFunctionality() {
        c.getValues()[0] = "15";
        c.getValues()[1] = "5";
        c.setCalculatorType(BASIC);
        //c.divide(); IN PANEL
        assertEquals("Did not get back expected result", "3", c.getValues()[0]);
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
        c.getTextArea().setText("\n35");
        c.setTextAreaValue(new StringBuffer().append("35"));
        c.getValues()[0] = "35";
//        Calculator_v4.DeleteButtonHandler handler = c.getDeleteButtonHandler();
        when(ae.getActionCommand()).thenReturn("DEL");
        //c.performDeleteButtonActions(ae); IN PANEL
        assertEquals("TextArea does not equal 3", "\n3", c.getTextArea().getText());
    }

    @Test
    public void pressingDotButtonFirstReturns0DotFromTextarea()
    {
        c.getValues()[0] = "";
        when(ae.getActionCommand()).thenReturn(".");
        //c.performDotButtonActions(ae); IN PANEL
        assertEquals("textarea is not as expected", "\n0.", "\n"+c.getTextAreaValue());
        assertEquals("getTextArea() is not 0.", "0.", c.getTextAreaWithoutAnything());
        assertTrue("dotButtonPressed is not true", c.isDotPressed());
    }

    @Test
    public void pressingDotButtonForNegativeReturnsNegativeNumberDot()
    {
        when(ae.getActionCommand()).thenReturn(".");
        c.getTextArea().setText("-5"); // should become -5.
        c.setNumberNegative(true);
        c.setDotPressed(true);
        c.setValuesPosition(0);
        c.getValues()[0] = "-5";
        //c.performDotButtonActions(ae); IN PANEL
        assertEquals("textAreaValue is not as expected", "-5.", c.getTextAreaValue().toString());
        assertEquals("getValues()[0] is not as expected", "-5.", c.getValues()[0]);
        assertTrue("dotButtonPressed is not true", c.isDotPressed());
    }

    @Test
    public void pressingDotButtonAfterNumberButtonReturnsNumberDot()
    {
        c.setValuesPosition(0);
        c.setCalculatorType(BASIC);
        c.getTextArea().setText(c.addNewLineCharacters()+"544");
        c.setTextAreaValue(new StringBuffer(c.getTextAreaWithoutAnything()));
        c.setValuesToTextAreaValue();
        when(ae.getActionCommand()).thenReturn(".");
        //c.performDotButtonActions(ae); IN PANEL
        assertEquals("getTextareaValue() is not as expected", "544.", c.getTextAreaValue().toString());
        assertEquals("Values[0]", "544.", c.getValues()[c.getValuesPosition()]);
    }

    @Test
    public void pressingClearRestoresCalculatorToStartFunctionality()
    {
        when(ae.getActionCommand()).thenReturn("C");
        //c.performClearButtonActions(ae); IN PANEL
        for ( int i=0; i<3; i++) {
            assertTrue("Values@"+i+" is not blank", StringUtils.isBlank(c.getValues()[i]));
        }
        assertEquals("TextArea is not 0", "\n0", c.getTextArea().getText());
        assertEquals("textarea is not 0", "0", c.getTextAreaValue().toString());
        assertFalse("isAddBool() is not false", c.isAdding());
        assertFalse("isSubBool() is not false", c.isSubtracting());
        assertFalse("isMulBool() is not false", c.isMultiplying());
        assertFalse("isDivBool() is not false", c.isDividing());
        assertEquals("Values position is not 0", 0, c.getValuesPosition());
        assertTrue("FirstNumBool is not true", c.isFirstNumber());
        assertFalse("DotButtonPressed is not false", c.isDotPressed());
        assertTrue("DotButton is not enabled", c.getButtonDot().isEnabled());
    }

    @Test
    public void pressingClearEntryClearsJustTheTextArea()
    {
        c.getTextArea().setText("1088");
        c.getValues()[0] = "1088";
//        Calculator_v4.ClearEntryButtonHandler handler = c.getClearEntryButtonHandler();
        when(ae.getActionCommand()).thenReturn("CE");
        //c.performClearEntryButtonActions(ae); IN PANEL
        assertTrue("TextArea was not cleared", StringUtils.isBlank(c.getTextArea().getText()));
        assertTrue("textarea was not cleared", StringUtils.isBlank(c.getTextAreaValue()));
    }

    @Test
    public void pressingClearEntryAfterPressingAnOperatorResetsTextAreaAndOperator()
    {
        c.getTextArea().setText("1088 +");
//        Calculator_v4.ClearEntryButtonHandler handler = c.getClearEntryButtonHandler();
        when(ae.getActionCommand()).thenReturn("CE");
//        c.performClearEntryButtonActions(ae); IN PANEL
        assertTrue("TextArea was not cleared", StringUtils.isBlank(c.getTextAreaWithoutAnything()));
        assertTrue("textarea was not cleared", StringUtils.isBlank(c.getTextAreaValue()));
        assertFalse("isAddBool() expected to be false", c.isAdding());
    }

    @Test
    public void methodResetOperatorsWithFalseResultsInAllOperatorsBeingFalse()
    {
        c.resetBasicOperators(false);
        assertFalse("isAddBool() is not false", c.isAdding());
        assertFalse("isSubBool() is not false", c.isSubtracting());
        assertFalse("isMulBool() is not false", c.isMultiplying());
        assertFalse("isDivBool() is not false", c.isDividing());
    }

    @Test
    public void methodResetOperatorsWithTrueResultsInAllOperatorsBeingTrue()
    {
        c.resetBasicOperators(true);
        assertTrue("isAddBool() is not true", c.isAdding());
        assertTrue("isSubBool() is not true", c.isSubtracting());
        assertTrue("isMulBool() is not true", c.isMultiplying());
        assertTrue("isDivBool() is not true", c.isDividing());
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
        //c.setByte(false);
        //c.setWord(false);
        String test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, new String("00000001 00000000"));
        assertEquals(Integer.parseInt(test), 256);

    }

    @Test
    public void testSetImageIconsWorksAsExpected()
    {
        c.setImageIcons();
    }

}

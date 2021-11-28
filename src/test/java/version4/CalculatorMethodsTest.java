package version4;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.event.ActionEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

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
        c.setTextarea(new StringBuffer().append("35"));
        c.values[0] = "35";
//        Calculator_v4.DeleteButtonHandler handler = c.getDeleteButtonHandler();
        when(ae.getActionCommand()).thenReturn("DEL");
        c.performDeleteButtonActions(ae);
        assertEquals("TextArea does not equal 3", "\n3", c.getTextArea().getText());
    }

    @Test
    public void pressingDotButtonFirstReturns0DotFromTextarea()
    {
        when(ae.getActionCommand()).thenReturn(".");
        c.performDotButtonActions(ae);
        assertEquals("textarea is not as expected", "\n0.", "\n"+c.textarea);
        assertEquals("textArea is not \n.0", "\n.0", c.getTextArea().getText());
        assertTrue("dotButtonPressed is not true", c.dotButtonPressed);
    }

    @Test
    public void pressingDotButtonForNegativeReturnsNegativeNumberDot()
    {
        when(ae.getActionCommand()).thenReturn(".");
        c.getTextArea().setText("-5"); // should become -5.
        c.values[0] = c.getTextAreaWithoutNewLineCharacters();
        c.performDotButtonActions(ae);
        assertEquals("Textarea is not as expected", "-5.", c.textarea.toString());
        assertEquals("TextArea is not as expected", "\n.5-", c.getTextArea().getText());
        assertTrue("dotButtonPressed is not true", c.dotButtonPressed);
    }

//    @Test // This should move into Basic panel as this tests number pushes, set up in panel
//    public void pressingNumberButtonForNegativeDecimalNumberReturnsNegativeNumberDotNumber()
//    {
//        when(ae.getActionCommand()).thenReturn("2");
//        c.getTextArea1().setText("-5."); // should become -5.2
//        c.values[0] = c.getTextAreaWithoutNewLineCharacters();
//        c.calcType = CalculatorType_v4.BASIC;
//        c.setNumberIsNegative(true);
//        c.setDotButtonPressed(true);
//        p.performProgrammerCalculatorNumberButtonActions(ae);
//        c.setDotButtonPressed(true);
//        assertEquals("Textarea is not as expected", "-5.2", c.textarea.toString());
//        assertEquals("TextArea is not as expected", "\n5.2-", c.getTextArea1().getText());
//        assertTrue("dotButtonPressed is not true", c.dotButtonPressed);
//    }

    @Test
    public void pressingDotButtonAfterNumberButtonReturnsNumberDot()
    {
        c.getTextArea().setText("5");
        when(ae.getActionCommand()).thenReturn(".");
        c.performDotButtonActions(ae);
        assertEquals("Textarea is not as expected", "5.", c.textarea.toString());
        assertEquals("TextArea is not as expected", "\n.5", c.getTextArea().getText());
        assertTrue("dotButtonPressed is not true", c.dotButtonPressed);
    }

    @Test
    public void pressingClearRestoresCalculatorToStartFunctionality()
    {
        when(ae.getActionCommand()).thenReturn("C");
        c.performClearButtonActions(ae);
        for ( int i=0; i<3; i++) {
            assertTrue("Values@"+i+" is not blank", StringUtils.isBlank(c.values[i]));
        }
        assertEquals("TextArea is not 0", "\n0", c.getTextArea().getText());
        assertEquals("textarea is not 0", "0", c.textarea.toString());
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
        c.getTextArea().setText("1088");
//        Calculator_v4.ClearEntryButtonHandler handler = c.getClearEntryButtonHandler();
        when(ae.getActionCommand()).thenReturn("CE");
        c.performClearEntryButtonActions(ae);
        assertTrue("TextArea was not cleared", StringUtils.isBlank(c.getTextArea().getText()));
        assertTrue("textarea was not cleared", StringUtils.isBlank(c.textarea));
    }

    @Test
    public void pressingClearEntryAfterPressingAnOperatorResetsTextAreaAndOperator()
    {
        c.getTextArea().setText("1088 +");
//        Calculator_v4.ClearEntryButtonHandler handler = c.getClearEntryButtonHandler();
        when(ae.getActionCommand()).thenReturn("CE");
        c.performClearEntryButtonActions(ae);
        assertTrue("TextArea was not cleared", StringUtils.isBlank(c.getTextArea().getText()));
        assertTrue("textarea was not cleared", StringUtils.isBlank(c.textarea));
        assertFalse("addBool expected to be false", c.addBool);
    }

    @Test
    public void methodResetOperatorsWithFalseResultsInAllOperatorsBeingFalse()
    {
        c.resetOperators(false);
        assertFalse("addBool is not false", c.addBool);
        assertFalse("subBool is not false", c.subBool);
        assertFalse("mulBool is not false", c.mulBool);
        assertFalse("divBool is not false", c.divBool);
    }

    @Test
    public void methodResetOperatorsWithTrueResultsInAllOperatorsBeingTrue()
    {
        c.resetOperators(true);
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

}

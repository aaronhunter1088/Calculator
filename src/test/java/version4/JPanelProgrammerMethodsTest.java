package version4;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import version3.Calculator_v3Error;

import java.awt.event.ActionEvent;
import java.math.BigInteger;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static version4.CalculatorBase_v4.BINARY;
import static version4.CalculatorBase_v4.DECIMAL;

@RunWith(MockitoJUnitRunner.class)
public class JPanelProgrammerMethodsTest {

    private static Calculator_v4 c;
    private String number;
    private boolean result;

    private static JPanelProgrammer_v4 p;

    @Mock
    ActionEvent ae;

    @BeforeClass
    public static void setup() throws Exception {
        System.setProperty("appName", "JPanelProgrammerMethodsTest");
        c = new Calculator_v4(CalculatorType_v4.PROGRAMMER);
        c.setCalculatorType(CalculatorType_v4.PROGRAMMER);
        c.firstNumBool = true;
        p = new JPanelProgrammer_v4(c);
        c.setCurrentPanel(p);
    }

    @Before
    public void setupBefore() throws Exception {
        c = new Calculator_v4(CalculatorType_v4.PROGRAMMER);
        c.setCalculatorType(CalculatorType_v4.PROGRAMMER);
        c.firstNumBool = true;
        p = new JPanelProgrammer_v4(c);
        c.setCurrentPanel(p);
    }

    @Test
    public void switchingFromBasicToProgrammerConvertsTextArea() throws CalculatorError_v4
    {
        c.getTextArea().setText("4");
        c.textarea = new StringBuffer().append(c.getTextArea().getText());
        c.values[0] = "4";
        p.getButtonBin().setSelected(true);
        String convertedValue = c.convertFromTypeToTypeOnValues(DECIMAL, BINARY, c.values[c.valuesPosition]);
        c.getTextArea().setText(c.addNewLineCharacters(3) + convertedValue);
        assertEquals("Did not convert from Decimal to Binary", "00000100", c.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
    }

    // Done by BDD/TDD
    @Test
    public void pressingNotButtonReversesAllBits() {
        //Assertions
        when(ae.getActionCommand()).thenReturn("\"\\u00B1\"");
        //Assuming in Bytes form to begin with
        String expected = "11110100";

        c.getTextArea().setText("00001011");
        p.performButtonNotActions(ae);
        //assertEquals("topQWord not as expected", ""); // lots of 1's
        assertEquals("textarea not as expected", expected, c.getTextArea().getText().replaceAll("\n", ""));
    }

    @Test
    public void testProgrammerNumberEntry() throws Calculator_v3Error, CalculatorError_v4 {
        // This method tests the user's ability to
        //1. Input a single, 8 bit number
        //2. Press an operator
        //3. Input another single, 8 bit number
        //4. Press equals.
        //5. Textarea displays proper sum in 8 bit form
        c.getTextArea().setText("");
        c.setTextarea(new StringBuffer());
        c.setCalculatorType(CalculatorType_v4.PROGRAMMER);
        p.getButtonBin().setSelected(true);
        c.setFirstNumBool(true);
        when(ae.getActionCommand()).thenReturn("0").thenReturn("0").thenReturn("0").thenReturn("0")
                                   .thenReturn("0").thenReturn("1").thenReturn("0").thenReturn("1") //5
                                   .thenReturn("+")
                                   .thenReturn("0").thenReturn("0").thenReturn("0").thenReturn("0")
                                   .thenReturn("0").thenReturn("0").thenReturn("1").thenReturn("1") //3
                                   .thenReturn("=");
        c.valuesPosition = 0;
        for(int i=1; i<=8; i++) { p.performProgrammerCalculatorNumberButtonActions(ae); }

        assertEquals("textArea not as expected", "00000101", c.getTextAreaWithoutNewLineCharactersOrWhiteSpace());

        c.performAdditionButtonActions(ae);
        assertTrue("Values[0] should not match "+c.getTextArea(), !(String.valueOf(c.values[0]).equals(c.getTextAreaWithoutNewLineCharactersOrWhiteSpace())));
        assertEquals("plus operator not appended", "\n + 00000101", c.getTextArea().getText());

        for(int i=1; i<=8; i++) { p.performProgrammerCalculatorNumberButtonActions(ae); }
        assertEquals("textArea not as expected", "00000011", c.getTextAreaWithoutNewLineCharactersOrWhiteSpace());

        c.performButtonEqualsActions();
        assertEquals("textArea not as expected", "00001000", c.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        assertEquals("text area not as expected", "00001000", c.getTextarea().toString());
        assertTrue("values[0] did not stay in decimal form",8 == Integer.parseInt(c.values[0]));
    }

    @Test(expected = CalculatorError_v4.class)
    public void testPushingButtonOrWithValuesAtZeroNotSet() throws CalculatorError_v4 {
        when(ae.getActionCommand()).thenReturn("OR");
        c.values[0] = "";
        c.values[1] = "50";

        p.performButtonOrActions(ae);
    }

    @Test
    public void testPushingButtonOrWithOneInput() {
        final String buttonChoice = "OR";
        when(ae.getActionCommand()).thenReturn(buttonChoice);

        c.textArea.setText(c.addNewLineCharacters(1) + "00000101");
        c.values[0] = "5";
        c.values[1] = "";

        try {
            p.performButtonOrActions(ae);
            c.updateTextareaFromTextArea();
        } catch (CalculatorError_v4 ce) {
            ce.printStackTrace();
        }

        assertEquals("TextArea not as expected", "00000101 OR", c.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        assertEquals("Text area not as expected", "00000101 OR", String.valueOf(c.getTextarea()));
        assertEquals("Values[0] is not in decimal base form", Integer.valueOf(5), Integer.valueOf(c.values[0]));
    }

    @Test
    public void testPushingButtonOrWithBothValuesSetReturnsCorrectResult() throws CalculatorError_v4 {
        c.values[0] = "00000101";
        c.values[1] = "00000011";

        c.getTextArea().setText(c.addNewLineCharacters(1)+c.values[1]);

        when(ae.getActionCommand()).thenReturn("OR");
        p.performButtonOrActions(ae);

        assertEquals("TextArea not showing expected result", "00000111", c.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
    }

    @Test
    public void testPushingModulusButtonWithOneInputReturnsZero() {
        when(ae.getActionCommand()).thenReturn("Mod");

        String number = "00000101"; //5
        c.getTextArea().setText(c.addNewLineCharacters(1)+number);
        c.updateTextareaFromTextArea();
        c.values[0] = "5";
        c.values[1] = "";
        c.valuesPosition = 0;
        p.performButtonModActions(ae);

        assertEquals("TextArea not as expected!", number+" Mod", c.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        assertEquals("Textarea not updated!", number+" Mod", String.valueOf(c.getTextarea()));
        assertTrue("Values["+c.getValuesPosition()+"] should not match "+c.getTextArea(), !c.values[0].equals(c.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
    }

    @Test
    public void testPushingModulusButtonWithBothValuesSetReturnsProperResult() throws Calculator_v3Error {
        p.getButtonBin().setSelected(true);
        c.setAddBool(false);
        c.setOrButtonBool(false);
        when(ae.getActionCommand()).thenReturn("Mod").thenReturn("=");

        c.getTextArea().setText("\n00000100"); // 4
        c.updateTextareaFromTextArea();
        c.values[0] = "00000100";
        c.values[1] = "";
        c.confirm("Entered 4");

        p.performButtonModActions(ae);

        String number = "00000011"; //3
        c.getTextArea().setText(c.addNewLineCharacters(1)+number);
        c.updateTextareaFromTextArea();
        c.valuesPosition = 1;
        //below is required
        c.values[1] = "00000011";
        c.confirm("3 Entered");

        c.performButtonEqualsActions();

        verify(ae, times(1)).getActionCommand();
        assertEquals("TextArea not as expected!", "00000001", c.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        assertEquals("Textarea not updated!", "00000001", String.valueOf(c.getTextarea()));
        assertTrue("Values["+0+"] should not match "+c.getTextAreaWithoutNewLineCharactersOrWhiteSpace(), !c.values[0].equals(c.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
        assertEquals("Values["+0+"] not as expected", 1, Integer.parseInt(c.values[0]));
    }

    @Test
    public void testPushingXorButtonWithZeroInputs() {
        when(ae.getActionCommand()).thenReturn("XOR");

        c.values[0] = "";
        c.values[1] = "";
        c.valuesPosition = 0;
        c.getTextArea().setText(c.addNewLineCharacters(1) + "");
        c.setTextarea(new StringBuffer());

        p.performButtonXorActions(ae);

        assertEquals("TextArea not as expected", "", c.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        assertEquals("Textarea not as expected", "", String.valueOf(c.getTextarea()));
        assertEquals("Values[0] should be empty", "", c.values[0]);
        assertEquals("Values[1] should be empty", "", c.values[1]);
        assertEquals("XorButton should be set yet", true, c.isXorButtonPressed());
    }

    @Test
    public void testPushingXorButtonWithOneInput() {

    }

    @Test
    public void testPushingXorButtonWithTwoInputs() {

    }

    @Test
    public void testBigIntegerSignumReturnsIntegerValue()
    {
        BigInteger value = new BigInteger("0");
        c.valuesPosition = 0;
        c.values[c.valuesPosition] = "5";
        try {
            value = new BigInteger(c.getValues()[c.valuesPosition]);
        } catch (NumberFormatException nfe) {
            fail();
        }
        assertEquals(5, value.intValue());
    }

}
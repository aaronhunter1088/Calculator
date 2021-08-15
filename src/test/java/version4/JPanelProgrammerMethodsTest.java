package version4;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import version3.CalcType_v3;
import version3.Calculator_v3Error;
import version3.JPanelProgrammer_v3;
import version3.StandardCalculator_v3;

import java.awt.event.ActionEvent;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JPanelProgrammerMethodsTest {

    private static StandardCalculator_v4 c;
    private String number;
    private boolean result;

    private static JPanelProgrammer_v4 p;

    @Mock
    ActionEvent ae;

    @BeforeClass
    public static void setup() throws Exception {
        System.setProperty("appName", "JPanelProgrammerMethodsTest");
        c = new StandardCalculator_v4(CalcType_v4.PROGRAMMER.getName());
        c.setCalcType(CalcType_v4.PROGRAMMER);
        c.firstNumBool = true;
        p = new JPanelProgrammer_v4(c);
        c.setCurrentPanel(p);
    }

    @Before
    public void setupBefore() throws Exception {
        c = new StandardCalculator_v4(CalcType_v4.PROGRAMMER.getName());
        c.setCalcType(CalcType_v4.PROGRAMMER);
        c.firstNumBool = true;
        p = new JPanelProgrammer_v4(c);
        c.setCurrentPanel(p);
    }

    @Test
    public void switchingFromBasicToProgrammerConvertsTextArea() {
        c.getTextArea().setText("4");
        c.textarea = new StringBuffer().append(c.getTextArea().getText());
        c.values[0] = "4";
        JPanelProgrammer_v4 p = new JPanelProgrammer_v4(c);
        p.getButtonBin().setSelected(true);
        p.convertValues();
        assertEquals("Did not convert from Decimal to Binary", "00000100", c.getTextAreaWithoutNewLineCharacters());
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
    public void testProgrammerNumberEntry() throws Calculator_v3Error {
        // This method tests the user's ability to
        //1. Input a single, 8 bit number
        //2. Press an operator
        //3. Input another single, 8 bit number
        //4. Press equals.
        //5. Textarea displays proper sum in 8 bit form
        c.getTextArea().setText("");
        c.setTextarea(new StringBuffer());
        c.setCalcType(CalcType_v4.PROGRAMMER);
        p.getButtonBin().setSelected(true);
        c.setFirstNumBool(true);
        when(ae.getActionCommand()).thenReturn("0").thenReturn("0").thenReturn("0").thenReturn("0")
                                   .thenReturn("0").thenReturn("1").thenReturn("0").thenReturn("1") //5
                                   .thenReturn("+")
                                   .thenReturn("0").thenReturn("0").thenReturn("0").thenReturn("0")
                                   .thenReturn("0").thenReturn("0").thenReturn("1").thenReturn("1") //3
                                   .thenReturn("=");
        c.valuesPosition = 0;
        for(int i=1; i<=8; i++) { c.performNumberButtonActions(ae); }

        assertEquals("textArea not as expected", "00000101", c.getTextAreaWithoutNewLineCharacters());

        c.performAdditionButtonActions(ae);
        assertTrue("Values[0] should not match "+c.getTextArea(), !(String.valueOf(c.values[0]).equals(c.getTextAreaWithoutNewLineCharacters())));
        assertEquals("plus operator not appended", "\n + 00000101", c.getTextArea().getText());

        for(int i=1; i<=8; i++) { c.performNumberButtonActions(ae); }
        assertEquals("textArea not as expected", "00000011", c.getTextAreaWithoutNewLineCharacters());

        c.performButtonEqualsActions();
        assertEquals("textArea not as expected", "00001000", c.getTextAreaWithoutNewLineCharacters());
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

        assertEquals("TextArea not as expected", "00000101 OR", c.getTextAreaWithoutNewLineCharacters());
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

        assertEquals("TextArea not showing expected result", "00000111", c.getTextAreaWithoutNewLineCharacters());
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

        assertEquals("TextArea not as expected!", number+" Mod", c.getTextAreaWithoutNewLineCharacters());
        assertEquals("Textarea not updated!", number+" Mod", String.valueOf(c.getTextarea()));
        assertTrue("Values["+c.getValuesPosition()+"] should not match "+c.getTextArea(), !c.values[0].equals(c.getTextAreaWithoutNewLineCharacters()));
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
        assertEquals("TextArea not as expected!", "00000001", c.getTextAreaWithoutNewLineCharacters());
        assertEquals("Textarea not updated!", "00000001", String.valueOf(c.getTextarea()));
        assertTrue("Values["+0+"] should not match "+c.getTextAreaWithoutNewLineCharacters(), !c.values[0].equals(c.getTextAreaWithoutNewLineCharacters()));
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

        assertEquals("TextArea not as expected", "", c.getTextAreaWithoutNewLineCharacters());
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


}
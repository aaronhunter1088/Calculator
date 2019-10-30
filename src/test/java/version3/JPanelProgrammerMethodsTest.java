package version3;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.event.ActionEvent;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JPanelProgrammerMethodsTest {

    private static StandardCalculator_v3 c;
    private String number;
    private boolean result;

    private static JPanelProgrammer_v3 p;

    @Mock
    ActionEvent ae;

    @BeforeClass
    public static void setup() throws Exception {
        System.setProperty("appName", "JPanelProgrammerMethodsTest");
        c = new StandardCalculator_v3(CalcType_v3.PROGRAMMER.getName());
        c.setCalcType(CalcType_v3.PROGRAMMER);
        c.firstNumBool = true;
        p = new JPanelProgrammer_v3(c);
        c.setCurrentPanel(p);
    }

    @Test
    public void switchingFromBasicToProgrammerConvertsTextArea() {
        c.getTextArea().setText("4");
        c.textarea = new StringBuffer().append(c.getTextArea().getText());
        JPanelProgrammer_v3 p = new JPanelProgrammer_v3(c);
        p.convertToBinary();
        assertEquals("Did not convert from Decimal to Binary", "00000100", c.getTextArea().getText().replaceAll("\n", ""));
    }

    // Done by BDD/TDD
    @Test
    public void pressingNotButtonReversesAllBits() {
        //Assuming in Bytes form to begin with
        String expected = "11110100";

        c.getTextArea().setText("00001011");
        p.performButtonNotActions();
        assertEquals("textarea not as expected", expected, c.getTextArea().getText().replaceAll("\n", ""));
        //assertEquals("topQWord not as expected", "") // lots of 1's
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
        c.setCalcType(CalcType_v3.PROGRAMMER);
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
        c.clearNewLineFromTextArea();
        assertEquals("plus operator not appended", " + 00000101", c.getTextArea().getText());

        for(int i=1; i<=8; i++) { c.performNumberButtonActions(ae); }
        c.clearNewLineFromTextArea();
        assertEquals("textArea not as expected", "00000011", c.getTextArea().getText());

        c.performButtonEqualsActions(ae);
        c.clearNewLineFromTextArea();
        assertEquals("textArea not as expected", "00001000", c.getTextArea().getText());
        assertTrue("values[0] did not stay as a decimal",8 == Integer.parseInt(c.values[0]));
    }

    @Test(expected = Calculator_v3Error.class)
    public void testPushingButtonOrWithValuesAtZeroNotSet() throws Calculator_v3Error {
        c.values[0] = "";
        c.values[1] = "50";

        p.performButtonOrActions(ae);
    }

    @Test
    public void testPushingButtonOrWithBothValuesSetReturnsCorrectResult() throws Calculator_v3Error {
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
        when(ae.getActionCommand()).thenReturn("Mod").thenReturn("=");

        c.getTextArea().setText("\n00000100");
        c.values[0] = "4";
        c.values[1] = "";
        c.confirm("Entered 4");

        p.performButtonModActions(ae);

        String number = "00000011"; //3
        c.getTextArea().setText(c.addNewLineCharacters(1)+number);
        c.updateTextareaFromTextArea();
        c.valuesPosition = 1;
        //below is required
        c.values[1] = number;

        c.performButtonEqualsActions(ae);

        verify(ae, times(2)).getActionCommand();
        assertEquals("TextArea not as expected!", "00000001", c.getTextAreaWithoutNewLineCharacters());
        assertEquals("Textarea not updated!", "00000001", String.valueOf(c.getTextarea()));
        assertTrue("Values["+0+"] should not match "+c.getTextAreaWithoutNewLineCharacters(), !c.values[0].equals(c.getTextAreaWithoutNewLineCharacters()));
        assertEquals("Values["+0+"] not as expected", 1, Integer.parseInt(c.values[0]));
    }

    @Test
    public void testPushingOrButtonWithOneInput() {

    }


}
package version3;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.event.ActionEvent;
import java.util.Arrays;

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
    public void testProgrammerNumberEntry() {
        // This method tests the user's ability to
        //1. Input a single, 8 bit number
        //2. Press an operator
        //3. Input another single, 8 bit number
        //4. Press equals.
        //5. Textarea displays proper sum in 8 bit form
        when(ae.getActionCommand()).thenReturn("0").thenReturn("0").thenReturn("0").thenReturn("0")
                                   .thenReturn("0").thenReturn("1").thenReturn("0").thenReturn("1") //5
                                   .thenReturn("+").thenReturn("+") // called twice
                                   .thenReturn("0").thenReturn("0").thenReturn("0").thenReturn("0")
                                   .thenReturn("0").thenReturn("0").thenReturn("1").thenReturn("1") //3
                                   .thenReturn("=");
        for(int i=1; i<=8; i++) { c.getNumberButtonHandler_v2().actionPerformed(ae); }

        c.clearNewLineFromTextArea();
        assertEquals("textArea not as expected", "00000101", c.getTextArea().getText());

        c.getAddButtonHandler().actionPerformed(ae);
        c.clearNewLineFromTextArea();
        assertEquals("plus operator not appended", " + 00000101", c.getTextArea().getText());

        for(int i=1; i<=8; i++) { c.getNumberButtonHandler_v2().actionPerformed(ae); }
        c.clearNewLineFromTextArea();
        assertEquals("textArea not as expected", "00000011", c.getTextArea().getText());

        c.getEqualsButtonHandler().actionPerformed(ae);
        c.clearNewLineFromTextArea();
        assertEquals("textArea not as expected", "00001000", c.getTextArea().getText());
    }
}

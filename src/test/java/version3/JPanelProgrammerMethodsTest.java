package version3;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

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
        c = new StandardCalculator_v3();
        p = new JPanelProgrammer_v3(c);
    }

    @Test
    public void switchingFromBasicToProgrammerConvertsTextArea() {
        c.getTextArea().setText("4");
        c.textarea = c.getTextArea().getText();
        JPanelProgrammer_v3 p = new JPanelProgrammer_v3(c);
        p.convertToBinary();
        assertEquals("Did not convert from Decimal to Binary", "00000100", c.getTextArea().getText().replaceAll("\n", ""));
    }

    // Done by BDD/TDD
//    @Test
//    public void pressingNotButtonReversesAllBits() {
//        //Assuming in Bytes form to begin with
//        String notOperator = "notOperator";
//        String expected = "11111010";
//        p.conversion = new String[]{"0", "0", "0", "0", "0", "1", "0", "1"};
//        //convert top QWord
//        p.convertTopQWord(notOperator);
//        //convert top DWord
//        p.convertTopDWord(notOperator);
//        //convert conversion - should be 8 bits long to start - getBytes()
//        p.convertConversion(notOperator);
//        c.getTextArea().setText(Arrays.toString(p.getConversion())); // only show conversion because we are only displaying 8 bits
//        c.textarea = c.getTextArea().getText();
//        assertEquals("textarea not as expected", expected, c.textarea);
//        //assertEquals("topQWord not as expected", "") // lots of 1's
//    }

}

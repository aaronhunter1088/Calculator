package version4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static version4.CalculatorBase_v4.BINARY;
import static version4.CalculatorBase_v4.DECIMAL;

@RunWith(MockitoJUnitRunner.class)
public class JPanelProgrammerMethodsTest
{
    static { System.setProperty("appName", JPanelProgrammerMethodsTest.class.getSimpleName()); }
    protected final static Logger LOGGER = LogManager.getLogger(JPanelProgrammerMethodsTest.class);
    private static Calculator_v4 calculator;
    private String number;
    private boolean result;

    private static JPanelProgrammer_v4 programmerPanel;

    @Mock
    ActionEvent actionEvent;

    @BeforeClass
    public static void setup() throws Exception
    {
        calculator = new Calculator_v4(CalculatorType_v4.PROGRAMMER);
        calculator.setCalculatorType(CalculatorType_v4.PROGRAMMER);
        calculator.firstNumBool = true;
        programmerPanel = new JPanelProgrammer_v4(calculator);
        calculator.setCurrentPanel(programmerPanel);
    }

    @Before
    public void setupBefore() throws Exception {
        calculator = new Calculator_v4(CalculatorType_v4.PROGRAMMER);
        calculator.setCalculatorType(CalculatorType_v4.PROGRAMMER);
        calculator.firstNumBool = true;
        programmerPanel = new JPanelProgrammer_v4(calculator);
        calculator.setCurrentPanel(programmerPanel);
    }

    @Test
    public void switchingFromBasicToProgrammerConvertsTextArea() throws CalculatorError_v4
    {
        calculator.getTextArea().setText("4");
        calculator.textareaValue = new StringBuffer().append(calculator.getTextArea().getText());
        calculator.values[0] = "4";
        programmerPanel.getButtonBin().setSelected(true);
        String convertedValue = calculator.convertFromTypeToTypeOnValues(DECIMAL, BINARY, calculator.values[calculator.valuesPosition]);
        calculator.getTextArea().setText(calculator.addNewLineCharacters(3) + convertedValue);
        assertEquals("Did not convert from Decimal to Binary", "00000100", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
    }

    // Done by BDD/TDD
    @Test
    public void pressingNotButtonReversesAllBits() {
        //Assertions
        when(actionEvent.getActionCommand()).thenReturn("\"\\u00B1\"");
        //Assuming in Bytes form to begin with
        String expected = "11110100";

        calculator.getTextArea().setText("00001011");
        programmerPanel.performButtonNotActions(actionEvent);
        //assertEquals("topQWord not as expected", ""); // lots of 1's
        assertEquals("textarea not as expected", expected, calculator.getTextArea().getText().replaceAll("\n", ""));
    }

    @Test
    public void testProgrammerNumberEntry() throws Calculator_v3Error, CalculatorError_v4 {
        // This method tests the user's ability to
        //1. Input a single, 8 bit number
        //2. Press an operator
        //3. Input another single, 8 bit number
        //4. Press equals.
        //5. Textarea displays proper sum in 8 bit form
        calculator.getTextArea().setText("");
        calculator.setTextareaValue(new StringBuffer());
        calculator.setCalculatorType(CalculatorType_v4.PROGRAMMER);
        programmerPanel.getButtonBin().setSelected(true);
        calculator.setFirstNumBool(true);
        when(actionEvent.getActionCommand()).thenReturn("0").thenReturn("0").thenReturn("0").thenReturn("0")
                                   .thenReturn("0").thenReturn("1").thenReturn("0").thenReturn("1") //5
                                   .thenReturn("+")
                                   .thenReturn("0").thenReturn("0").thenReturn("0").thenReturn("0")
                                   .thenReturn("0").thenReturn("0").thenReturn("1").thenReturn("1") //3
                                   .thenReturn("=");
        calculator.valuesPosition = 0;
        for(int i=1; i<=8; i++) { programmerPanel.performProgrammerCalculatorNumberButtonActions(actionEvent); }

        assertEquals("textArea not as expected", "00000101", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());

        calculator.performAdditionButtonActions(actionEvent);
        assertTrue("Values[0] should not match "+ calculator.getTextArea(), !(String.valueOf(calculator.values[0]).equals(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace())));
        assertEquals("plus operator not appended", "\n + 00000101", calculator.getTextArea().getText());

        for(int i=1; i<=8; i++) { programmerPanel.performProgrammerCalculatorNumberButtonActions(actionEvent); }
        assertEquals("textArea not as expected", "00000011", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());

        calculator.performButtonEqualsActions();
        assertEquals("textArea not as expected", "00001000", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        assertEquals("text area not as expected", "00001000", calculator.getTextareaValue().toString());
        assertTrue("values[0] did not stay in decimal form",8 == Integer.parseInt(calculator.values[0]));
    }

    @Test(expected = CalculatorError_v4.class)
    public void testPushingButtonOrWithValuesAtZeroNotSet() throws CalculatorError_v4 {
        when(actionEvent.getActionCommand()).thenReturn("OR");
        calculator.values[0] = "";
        calculator.values[1] = "50";

        programmerPanel.performButtonOrActions(actionEvent);
    }

    @Test
    public void testPushingButtonOrWithOneInput() {
        final String buttonChoice = "OR";
        when(actionEvent.getActionCommand()).thenReturn(buttonChoice);

        calculator.textArea.setText(calculator.addNewLineCharacters(1) + "00000101");
        calculator.values[0] = "5";
        calculator.values[1] = "";

        try {
            programmerPanel.performButtonOrActions(actionEvent);
            calculator.updateTextareaFromTextArea();
        } catch (CalculatorError_v4 ce) {
            ce.printStackTrace();
        }

        assertEquals("TextArea not as expected", "00000101 OR", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        assertEquals("Text area not as expected", "00000101 OR", String.valueOf(calculator.getTextareaValue()));
        assertEquals("Values[0] is not in decimal base form", Integer.valueOf(5), Integer.valueOf(calculator.values[0]));
    }

    @Test
    public void testPushingButtonOrWithBothValuesSetReturnsCorrectResult() throws CalculatorError_v4 {
        calculator.values[0] = "00000101";
        calculator.values[1] = "00000011";

        calculator.getTextArea().setText(calculator.addNewLineCharacters(1)+ calculator.values[1]);

        when(actionEvent.getActionCommand()).thenReturn("OR");
        programmerPanel.performButtonOrActions(actionEvent);

        assertEquals("TextArea not showing expected result", "00000111", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
    }

    @Test
    public void testPushingModulusButtonWithOneInputReturnsZero() {
        when(actionEvent.getActionCommand()).thenReturn("Mod");

        String number = "00000101"; //5
        calculator.getTextArea().setText(calculator.addNewLineCharacters(1)+number);
        calculator.updateTextareaFromTextArea();
        calculator.values[0] = "5";
        calculator.values[1] = "";
        calculator.valuesPosition = 0;
        programmerPanel.performButtonModActions(actionEvent);

        assertEquals("TextArea not as expected!", number+" Mod", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        assertEquals("Textarea not updated!", number+" Mod", String.valueOf(calculator.getTextareaValue()));
        assertTrue("Values["+ calculator.getValuesPosition()+"] should not match "+ calculator.getTextArea(), !calculator.values[0].equals(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
    }

    @Test
    public void testPushingModulusButtonWithBothValuesSetReturnsProperResult() throws Calculator_v3Error {
        programmerPanel.getButtonBin().setSelected(true);
        calculator.setAddBool(false);
        calculator.setOrButtonBool(false);
        when(actionEvent.getActionCommand()).thenReturn("Mod").thenReturn("=");

        calculator.getTextArea().setText("\n00000100"); // 4
        calculator.updateTextareaFromTextArea();
        calculator.values[0] = "00000100";
        calculator.values[1] = "";
        calculator.confirm("Entered 4");

        programmerPanel.performButtonModActions(actionEvent);

        String number = "00000011"; //3
        calculator.getTextArea().setText(calculator.addNewLineCharacters(1)+number);
        calculator.updateTextareaFromTextArea();
        calculator.valuesPosition = 1;
        //below is required
        calculator.values[1] = "00000011";
        calculator.confirm("3 Entered");

        calculator.performButtonEqualsActions();

        verify(actionEvent, times(1)).getActionCommand();
        assertEquals("TextArea not as expected!", "00000001", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        assertEquals("Textarea not updated!", "00000001", String.valueOf(calculator.getTextareaValue()));
        assertTrue("Values["+0+"] should not match "+ calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace(), !calculator.values[0].equals(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
        assertEquals("Values["+0+"] not as expected", 1, Integer.parseInt(calculator.values[0]));
    }

    @Test
    public void testPushingXorButtonWithZeroInputs() {
        when(actionEvent.getActionCommand()).thenReturn("XOR");

        calculator.values[0] = "";
        calculator.values[1] = "";
        calculator.valuesPosition = 0;
        calculator.getTextArea().setText(calculator.addNewLineCharacters(1) + "");
        calculator.setTextareaValue(new StringBuffer());

        programmerPanel.performButtonXorActions(actionEvent);

        assertEquals("TextArea not as expected", "", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
        assertEquals("Textarea not as expected", "", String.valueOf(calculator.getTextareaValue()));
        assertEquals("Values[0] should be empty", "", calculator.values[0]);
        assertEquals("Values[1] should be empty", "", calculator.values[1]);
        assertEquals("XorButton should be set yet", true, calculator.isXorButtonPressed());
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
        calculator.valuesPosition = 0;
        calculator.values[calculator.valuesPosition] = "5";
        try {
            value = new BigInteger(calculator.getValues()[calculator.valuesPosition]);
        } catch (NumberFormatException nfe) {
            fail();
        }
        assertEquals(5, value.intValue());
    }

    @Test
    public void testSetButtons2To9OnlySetsButtons2To9()
    {
        calculator.setupNumberButtons(true); // they all start enabled
        programmerPanel.setButtons2To9(false); // switched into programmer panel or pressed binary

        assertTrue(calculator.getButton0().isEnabled());
        assertTrue(calculator.getButton1().isEnabled());
        assertFalse(calculator.getButton2().isEnabled());
        assertFalse(calculator.getButton3().isEnabled());
        assertFalse(calculator.getButton4().isEnabled());
        assertFalse(calculator.getButton5().isEnabled());
        assertFalse(calculator.getButton6().isEnabled());
        assertFalse(calculator.getButton7().isEnabled());
        assertFalse(calculator.getButton8().isEnabled());
        assertFalse(calculator.getButton9().isEnabled());
    }

}
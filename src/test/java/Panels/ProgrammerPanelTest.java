//package Panels;
//
//import Calculators.Calculator_v4;
//import Calculators.CalculatorError;
//import Types.CalculatorType;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.awt.event.ActionEvent;
//import java.math.BigInteger;
//
//import static junit.framework.TestCase.assertSame;
//import static junit.framework.TestCase.assertTrue;
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//import static Types.CalculatorBase.BINARY;
//import static Types.CalculatorBase.DECIMAL;
//
//@RunWith(MockitoJUnitRunner.class)
//public class ProgrammerPanelTest
//{
//    static { System.setProperty("appName", ProgrammerPanelTest.class.getSimpleName()); }
//    protected final static Logger LOGGER = LogManager.getLogger(ProgrammerPanelTest.class);
//    private static Calculator_v4 calculator;
//    private String number;
//    private boolean result;
//
//    private static ProgrammerPanel programmerPanel;
//
//    @Mock
//    ActionEvent actionEvent;
//
//    @BeforeClass
//    public static void setup() throws Exception
//    {
//        calculator = new Calculator_v4(CalculatorType.PROGRAMMER);
//        assertSame("ProgrammerCalculator has the wrong type", CalculatorType.PROGRAMMER, calculator.getCalculatorType());
//        calculator.setFirstNumber(true);
//        programmerPanel = new ProgrammerPanel(calculator);
//        calculator.setCurrentPanel(programmerPanel);
//    }
//
//    @Before
//    public void setupBefore() throws Exception {
//        calculator = new Calculator_v4(CalculatorType.PROGRAMMER);
//        calculator.setCalculatorType(CalculatorType.PROGRAMMER);
//        calculator.setFirstNumber(true);
//        programmerPanel = new ProgrammerPanel(calculator);
//        calculator.setCurrentPanel(programmerPanel);
//    }
//
//    @Test
//    public void switchingFromBasicToProgrammerConvertsTextArea() throws CalculatorError
//    {
//        calculator.getTextArea().setText("4");
//        calculator.setTextAreaValue(new StringBuffer().append(calculator.getTextArea().getText()));
//        calculator.getValues()[0] = "4";
//        programmerPanel.buttonBin.setSelected(true);
//        String convertedValue = calculator.convertFromTypeToTypeOnValues(DECIMAL, BINARY, calculator.getValues()[calculator.getValuesPosition()]);
//        calculator.getTextArea().setText(calculator.addNewLineCharacters(3) + convertedValue);
//        assertEquals("Did not convert from Decimal to Binary", "00000100", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
//    }
//
//    @Test
//    public void pressingNotButtonReversesAllBits() {
//        //Assertions
//        when(actionEvent.getActionCommand()).thenReturn("\"\\u00B1\"");
//        //Assuming in Bytes form to begin with
//        String expected = "11110100";
//
//        calculator.getTextArea().setText("00001011");
//        programmerPanel.performButtonNotActions(actionEvent);
//        //assertEquals("topQWord not as expected", ""); // lots of 1's
//        assertEquals("textarea not as expected", expected, calculator.getTextArea().getText().replaceAll("\n", ""));
//    }
//
//    @Test
//    public void testProgrammerNumberEntry() throws CalculatorError {
//        // This method tests the user's ability to
//        //1. Input a single, 8 bit number
//        //2. Press an operator
//        //3. Input another single, 8 bit number
//        //4. Press equals.
//        //5. Textarea displays proper sum in 8 bit form
//        calculator.getTextArea().setText("");
//        calculator.setTextAreaValue(new StringBuffer());
//        calculator.setCalculatorType(CalculatorType.PROGRAMMER);
//        programmerPanel.buttonBin.setSelected(true);
//        calculator.setFirstNumber(true);
//        when(actionEvent.getActionCommand()).thenReturn("0").thenReturn("0").thenReturn("0").thenReturn("0")
//                                   .thenReturn("0").thenReturn("1").thenReturn("0").thenReturn("1") //5
//                                   .thenReturn("+")
//                                   .thenReturn("0").thenReturn("0").thenReturn("0").thenReturn("0")
//                                   .thenReturn("0").thenReturn("0").thenReturn("1").thenReturn("1") //3
//                                   .thenReturn("=");
//        calculator.setValuesPosition(0);
//        for(int i=1; i<=8; i++) { programmerPanel.performProgrammerCalculatorNumberButtonActions(actionEvent); }
//
//        assertEquals("getTextArea() not as expected", "00000101", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
//
//        calculator.performAdditionButtonActions(actionEvent);
//        assertTrue("Values[0] should not match "+ calculator.getTextArea(), !(String.valueOf(calculator.getValues()[0]).equals(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace())));
//        assertEquals("plus operator not appended", "\n + 00000101", calculator.getTextArea().getText());
//
//        for(int i=1; i<=8; i++) { programmerPanel.performProgrammerCalculatorNumberButtonActions(actionEvent); }
//        assertEquals("getTextArea() not as expected", "00000011", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
//
//        calculator.performButtonEqualsActions(actionEvent);
//        assertEquals("getTextArea() not as expected", "00001000", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
//        assertEquals("text area not as expected", "00001000", calculator.getTextAreaValue().toString());
//        assertTrue("getValues()[0] did not stay in decimal form",8 == Integer.parseInt(calculator.getValues()[0]));
//    }
//
//    @Test(expected = CalculatorError.class)
//    public void testPushingButtonOrWithValuesAtZeroNotSet() throws CalculatorError {
//        when(actionEvent.getActionCommand()).thenReturn("OR");
//        calculator.getValues()[0] = "";
//        calculator.getValues()[1] = "50";
//
//        programmerPanel.performButtonOrActions(actionEvent);
//    }
//
//    @Test
//    public void testPushingButtonOrWithOneInput() {
//        final String buttonChoice = "OR";
//        when(actionEvent.getActionCommand()).thenReturn(buttonChoice);
//
//        calculator.getTextArea().setText(calculator.addNewLineCharacters(1) + "00000101");
//        calculator.getValues()[0] = "5";
//        calculator.getValues()[1] = "";
//
//        try {
//            programmerPanel.performButtonOrActions(actionEvent);
//            calculator.updateTextAreaValueFromTextArea();
//        } catch (CalculatorError ce) {
//            ce.printStackTrace();
//        }
//
//        assertEquals("TextArea not as expected", "00000101 OR", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
//        assertEquals("Text area not as expected", "00000101 OR", String.valueOf(calculator.getTextAreaValue()));
//        assertEquals("Values[0] is not in decimal base form", Integer.valueOf(5), Integer.valueOf(calculator.getValues()[0]));
//    }
//
//    @Test
//    public void testPushingButtonOrWithBothValuesSetReturnsCorrectResult() throws CalculatorError {
//        calculator.getValues()[0] = "00000101";
//        calculator.getValues()[1] = "00000011";
//
//        calculator.getTextArea().setText(calculator.addNewLineCharacters(1)+ calculator.getValues()[1]);
//
//        when(actionEvent.getActionCommand()).thenReturn("OR");
//        programmerPanel.performButtonOrActions(actionEvent);
//
//        assertEquals("TextArea not showing expected result", "00000111", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
//    }
//
//    @Test
//    public void testPushingModulusButtonWithOneInputReturnsZero() {
//        when(actionEvent.getActionCommand()).thenReturn("Mod");
//
//        String number = "00000101"; //5
//        calculator.getTextArea().setText(calculator.addNewLineCharacters(1)+number);
//        calculator.updateTextAreaValueFromTextArea();
//        calculator.getValues()[0] = "5";
//        calculator.getValues()[1] = "";
//        calculator.setValuesPosition(0);
//        programmerPanel.performButtonModActions(actionEvent);
//
//        assertEquals("TextArea not as expected!", number+" Mod", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
//        assertEquals("Textarea not updated!", number+" Mod", String.valueOf(calculator.getTextAreaValue()));
//        assertTrue("Values["+ calculator.getValuesPosition()+"] should not match "+ calculator.getTextArea(), !calculator.getValues()[0].equals(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
//    }
//
//    @Test
//    public void testPushingModulusButtonWithBothValuesSetReturnsProperResult() throws CalculatorError {
//        programmerPanel.buttonBin.setSelected(true);
//        calculator.setAdding(false);
//        calculator.setOrPressed(false);
//        when(actionEvent.getActionCommand()).thenReturn("Mod").thenReturn("=");
//
//        calculator.getTextArea().setText("\n00000100"); // 4
//        calculator.updateTextAreaValueFromTextArea();
//        calculator.getValues()[0] = "00000100";
//        calculator.getValues()[1] = "";
//        calculator.confirm("Entered 4");
//
//        programmerPanel.performButtonModActions(actionEvent);
//
//        String number = "00000011"; //3
//        calculator.getTextArea().setText(calculator.addNewLineCharacters(1)+number);
//        calculator.updateTextAreaValueFromTextArea();
//        calculator.setValuesPosition(1);
//        //below is required
//        calculator.getValues()[1] = "00000011";
//        calculator.confirm("3 Entered");
//
//        calculator.performButtonEqualsActions(actionEvent);
//
//        verify(actionEvent, times(1)).getActionCommand();
//        assertEquals("TextArea not as expected!", "00000001", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
//        assertEquals("Textarea not updated!", "00000001", String.valueOf(calculator.getTextAreaValue()));
//        assertTrue("Values["+0+"] should not match "+ calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace(), !calculator.getValues()[0].equals(calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace()));
//        assertEquals("Values["+0+"] not as expected", 1, Integer.parseInt(calculator.getValues()[0]));
//    }
//
//    @Test
//    public void testPushingXorButtonWithZeroInputs() {
//        when(actionEvent.getActionCommand()).thenReturn("XOR");
//
//        calculator.getValues()[0] = "";
//        calculator.getValues()[1] = "";
//        calculator.setValuesPosition(0);
//        calculator.getTextArea().setText(calculator.addNewLineCharacters(1) + "");
//        calculator.setTextAreaValue(new StringBuffer());
//
//        programmerPanel.performButtonXorActions(actionEvent);
//
//        assertEquals("TextArea not as expected", "", calculator.getTextAreaWithoutNewLineCharactersOrWhiteSpace());
//        assertEquals("Textarea not as expected", "", String.valueOf(calculator.getTextAreaValue()));
//        assertEquals("Values[0] should be empty", "", calculator.getValues()[0]);
//        assertEquals("Values[1] should be empty", "", calculator.getValues()[1]);
//        assertEquals("XorButton should be set yet", true, calculator.isXorPressed());
//    }
//
//    @Test
//    public void testPushingXorButtonWithOneInput() {
//
//    }
//
//    @Test
//    public void testPushingXorButtonWithTwoInputs() {
//
//    }
//
//    @Test
//    public void testBigIntegerSignumReturnsIntegerValue()
//    {
//        BigInteger value = new BigInteger("0");
//        calculator.setValuesPosition(0);
//        calculator.getValues()[calculator.getValuesPosition()] = "5";
//        try {
//            value = new BigInteger(calculator.getValues()[calculator.getValuesPosition()]);
//        } catch (NumberFormatException nfe) {
//            fail();
//        }
//        assertEquals(5, value.intValue());
//    }
//
//    @Test
//    public void testSetButtons2To9OnlySetsButtons2To9()
//    {
//        calculator.setupNumberButtons(true); // they all start enabled
//        programmerPanel.setButtons2To9(false); // switched into programmer panel or pressed binary
//
//        assertTrue(calculator.getButton0().isEnabled());
//        assertTrue(calculator.getButton1().isEnabled());
//        assertFalse(calculator.getButton2().isEnabled());
//        assertFalse(calculator.getButton3().isEnabled());
//        assertFalse(calculator.getButton4().isEnabled());
//        assertFalse(calculator.getButton5().isEnabled());
//        assertFalse(calculator.getButton6().isEnabled());
//        assertFalse(calculator.getButton7().isEnabled());
//        assertFalse(calculator.getButton8().isEnabled());
//        assertFalse(calculator.getButton9().isEnabled());
//    }
//
//}
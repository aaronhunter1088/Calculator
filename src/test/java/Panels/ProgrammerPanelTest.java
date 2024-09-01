//package Panels;
//
//import Calculators.Calculator;
//import Calculators.CalculatorError;
//import Types.CalculatorView;
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
//    private static Calculator calculator;
//    private String number;
//    private boolean result;
//
//    private static OLDProgrammerPanel programmerPanel;
//
//    @Mock
//    ActionEvent actionEvent;
//
//    @BeforeClass
//    public static void setup() throws Exception
//    {
//        calculator = new Calculator(CalculatorView.PROGRAMMER);
//        assertSame("ProgrammerCalculator has the wrong type", CalculatorView.PROGRAMMER, calculator.getCalculatorType());
//        calculator.setFirstNumber(true);
//        programmerPanel = new OLDProgrammerPanel(calculator);
//        calculator.setCurrentPanel(programmerPanel);
//    }
//
//    @Before
//    public void setupBefore() throws Exception {
//        calculator = new Calculator(CalculatorView.PROGRAMMER);
//        calculator.setCalculatorType(CalculatorView.PROGRAMMER);
//        calculator.setFirstNumber(true);
//        programmerPanel = new OLDProgrammerPanel(calculator);
//        calculator.setCurrentPanel(programmerPanel);
//    }
//
//    @Test
//    public void switchingFromBasicToProgrammerConvertsTextArea() throws CalculatorError
//    {
//        calculator.getTextPane().setText("4");
//        calculator.getValues()[0] = "4";
//        programmerPanel.getButtonBin().setSelected(true);
//        String convertedValue = calculator.convertFromTypeToTypeOnValues(DECIMAL, BINARY, calculator.getValues()[calculator.getValuesPosition()]);
//        calculator.getTextPane().setText(calculator.addNewLineCharacters(3) + convertedValue);
//        assertEquals("Did not convert from Decimal to Binary", "00000100", calculator.getTextPaneWithoutNewLineCharacters());
//    }
//
//    @Test
//    public void pressingNotButtonReversesAllBits() {
//        //Assertions
//        when(actionEvent.getActionCommand()).thenReturn("\"\\u00B1\"");
//        //Assuming in Bytes form to begin with
//        String expected = "11110100";
//
//        calculator.getTextPane().setText("00001011");
//        programmerPanel.performButtonNotActions(actionEvent);
//        //assertEquals("topQWord not as expected", ""); // lots of 1's
//        assertEquals("textarea not as expected", expected, calculator.getTextPane().getText().replaceAll("\n", ""));
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
//        calculator.getTextPane().setText("");
//        calculator.setCalculatorType(CalculatorView.PROGRAMMER);
//        programmerPanel.getButtonBin().setSelected(true);
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
//        assertEquals("getTextPane() not as expected", "00000101", calculator.getTextPaneWithoutNewLineCharacters());
//
//        programmerPanel.performAdditionButtonActions(actionEvent);
//        assertTrue("Values[0] should not match "+ calculator.getTextPane(), !(String.valueOf(calculator.getValues()[0]).equals(calculator.getTextPaneWithoutNewLineCharacters())));
//        assertEquals("plus operator not appended", "\n + 00000101", calculator.getTextPane().getText());
//
//        for(int i=1; i<=8; i++) { programmerPanel.performProgrammerCalculatorNumberButtonActions(actionEvent); }
//        assertEquals("getTextPane() not as expected", "00000011", calculator.getTextPaneWithoutNewLineCharacters());
//
//        programmerPanel.performButtonEqualsActions(actionEvent);
//        assertEquals("getTextPane() not as expected", "00001000", calculator.getTextPaneWithoutNewLineCharacters());
//        assertEquals("text area not as expected", "00001000", calculator.getTextPane().getText());
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
//        calculator.getTextPane().setText(calculator.addNewLineCharacters(1) + "00000101");
//        calculator.getValues()[0] = "5";
//        calculator.getValues()[1] = "";
//
//        programmerPanel.performButtonOrActions(actionEvent);
//
//        assertEquals("TextArea not as expected", "00000101 OR", calculator.getTextPaneWithoutNewLineCharacters());
//        assertEquals("Values[0] is not in decimal base form", Integer.valueOf(5), Integer.valueOf(calculator.getValues()[0]));
//    }
//
//    @Test
//    public void testPushingButtonOrWithBothValuesSetReturnsCorrectResult() throws CalculatorError {
//        calculator.getValues()[0] = "00000101";
//        calculator.getValues()[1] = "00000011";
//
//        calculator.getTextPane().setText(calculator.addNewLineCharacters(1)+ calculator.getValues()[1]);
//
//        when(actionEvent.getActionCommand()).thenReturn("OR");
//        programmerPanel.performButtonOrActions(actionEvent);
//
//        assertEquals("TextArea not showing expected result", "00000111", calculator.getTextPaneWithoutNewLineCharacters());
//    }
//
//    @Test
//    public void testPushingModulusButtonWithOneInputReturnsZero() {
//        when(actionEvent.getActionCommand()).thenReturn("Mod");
//
//        String number = "00000101"; //5
//        calculator.getTextPane().setText(calculator.addNewLineCharacters() + number);
//        calculator.getValues()[0] = "5";
//        calculator.getValues()[1] = "";
//        calculator.setValuesPosition(0);
//        programmerPanel.performButtonModActions(actionEvent);
//
//        assertEquals("TextArea not as expected!", number+" Mod", calculator.getTextPaneWithoutNewLineCharacters());
//        assertNotEquals("Values[" + calculator.getValuesPosition() + "] should not match ", calculator.getValues()[0], calculator.getTextPaneWithoutNewLineCharacters());
//    }
//
//    @Test
//    public void testPushingModulusButtonWithBothValuesSetReturnsProperResult() throws CalculatorError {
//        programmerPanel.getButtonBin().setSelected(true);
//        calculator.setAdding(false);
//        programmerPanel.setOrPressed(false);
//        when(actionEvent.getActionCommand()).thenReturn("Mod").thenReturn("=");
//
//        calculator.getTextPane().setText("\n00000100"); // 4
//        calculator.getValues()[0] = "00000100";
//        calculator.getValues()[1] = "";
//        calculator.confirm("Entered 4");
//
//        programmerPanel.performButtonModActions(actionEvent);
//
//        String number = "00000011"; //3
//        calculator.getTextPane().setText(calculator.addNewLineCharacters() + number);
//        calculator.setValuesPosition(1);
//        //below is required
//        calculator.getValues()[1] = "00000011";
//        calculator.confirm("3 Entered");
//
//        programmerPanel.performButtonEqualsActions(actionEvent);
//
//        verify(actionEvent, times(1)).getActionCommand();
//        assertEquals("TextArea not as expected!", "00000001", calculator.getTextPaneWithoutNewLineCharacters());
//        assertNotEquals("Values["+0+"] should not match "+ calculator.getTextPaneWithoutNewLineCharacters(), calculator.getValues()[0], calculator.getTextPaneWithoutNewLineCharacters());
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
//        calculator.getTextPane().setText(calculator.addNewLineCharacters());
//
//        programmerPanel.performButtonXorActions(actionEvent);
//
//        assertEquals("TextArea not as expected", "", calculator.getTextPaneWithoutNewLineCharacters());
//        assertEquals("Values[0] should be empty", "", calculator.getValues()[0]);
//        assertEquals("Values[1] should be empty", "", calculator.getValues()[1]);
//        assertFalse("XorButton should be set yet", programmerPanel.isXorPressed());
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
//    //    @Test
////    public void testConvertingBinaryToDecimalWorks() throws CalculatorError
////    {
////        // Test that this work
////        //String test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "10000000");
////        //assertEquals(Integer.parseInt(test), 128);
////        // Test another number
////        //test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "11111111");
////        //assertEquals(Integer.parseInt(test), 255);
////        // Make sure this returns the same as above, although i believe this entry by the user is impossible
////        //test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "11111111 ");
////        //assertEquals(Integer.parseInt(test), 255);
////        // Test to make sure an incomplete number returns appropriately
////        //test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "101");
////        //assertEquals(Integer.parseInt(test), 5);
////        // Test to make sure a WORD BINARY entry returns appropriately
////        //c.setByte(false);
////        //c.setWord(false);
////        String test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, new String("00000001 00000000"));
////        assertEquals(Integer.parseInt(test), 256);
////
////    }
//}
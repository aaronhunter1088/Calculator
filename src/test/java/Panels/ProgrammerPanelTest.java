package Panels;

import Calculators.Calculator;
import Calculators.CalculatorError;
import Types.CalculatorView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.event.ActionEvent;

import static Types.CalculatorBase.*;
import static Types.CalculatorByte.*;
import static Types.Texts.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProgrammerPanelTest
{
    static { System.setProperty("appName", ProgrammerPanelTest.class.getSimpleName()); }
    private static Logger LOGGER;
    private static Calculator calculator;
    private static ProgrammerPanel programmerPanel;
    private final double delta = 0.000001d;

    @Mock
    ActionEvent actionEvent;

    @BeforeClass
    public static void beforeAll()
    { LOGGER = LogManager.getLogger(ProgrammerPanelTest.class); }

    @AfterClass
    public static void afterAll()
    { LOGGER.info("Finished running " + ProgrammerPanel.class.getSimpleName()); }

    @Before
    public void beforeEach() throws Exception 
    {
        calculator = new Calculator(CalculatorView.VIEW_PROGRAMMER);
        programmerPanel = (ProgrammerPanel)calculator.getCurrentPanel();
    }
    
    @After
    public void afterEach() {}

    @Test
    public void switchingFromBasicToProgrammerConvertsTextArea() throws CalculatorError
    {
        calculator.getTextPane().setText(FOUR.getValue());
        calculator.getValues()[0] = FOUR.getValue();
        calculator.setCalculatorByte(BYTE_BYTE);
        String convertedValue = calculator.convertFromBaseToBase(BASE_DECIMAL, BASE_BINARY, calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("Conversion went wrong!", "00000100", convertedValue);
        programmerPanel.appendTextToProgrammerPane(convertedValue);
        assertEquals(TEXT_PANE_WRONG_VALUE.getValue(), "00000100", calculator.getTextPaneValueForProgrammerPanel());
    }

    @Test
    public void pressingNotButtonReversesAllBits()
    {
        when(actionEvent.getActionCommand()).thenReturn(NOT.getValue());
        calculator.setCalculatorBase(BASE_BINARY);
        programmerPanel.appendTextToProgrammerPane("0000 1011");
        programmerPanel.performButtonNotAction(actionEvent);
        //assertEquals("topQWord not as expected", ""); // lots of 1's
        assertEquals(TEXT_PANE_WRONG_VALUE.getValue(), "1111 0100", programmerPanel.separateBits(calculator.getTextPaneValueForProgrammerPanel()));
    }

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

    @Test
    public void testPushOrWhenValuesAtZeroNotSet()
    {
        when(actionEvent.getActionCommand()).thenReturn(OR.getValue());
        calculator.getValues()[0] = BLANK.getValue();
        calculator.getValues()[1] = "50";
        programmerPanel.performButtonOrAction(actionEvent);
        assertTrue("Expected isFirstNumber to be true", calculator.isFirstNumber());
        assertEquals("Expected values[0] to be empty", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("Expected values[1] to be 50", "50", calculator.getValues()[1]);
    }

    @Test
    public void testPushOrWithDecimalInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(OR.getValue());
        calculator.setCalculatorBase(BASE_DECIMAL); // BYTE_BYTE default
        calculator.appendTextToPane(FIVE.getValue());
        calculator.getValues()[0] = FIVE.getValue();
        programmerPanel.performButtonOrAction(actionEvent);
        assertEquals(TEXT_PANE_WRONG_VALUE.getValue(), "5 OR", calculator.getTextPaneValueForProgrammerPanel());
        assertEquals("Values[0] is not in decimal base form", "5", calculator.getValues()[0]);
    }

    @Test
    public void testPushOrWithBothValuesSetWithDecimalInputReturnsResult()
    {
        when(actionEvent.getActionCommand()).thenReturn(OR.getValue());
        calculator.setCalculatorBase(BASE_DECIMAL); // BYTE_BYTE default
        calculator.getValues()[0] = FIVE.getValue();
        calculator.getValues()[1] = THREE.getValue();
        calculator.appendTextToPane(calculator.getValues()[1]);
        programmerPanel.performButtonOrAction(actionEvent);
        assertEquals(TEXT_PANE_WRONG_VALUE.getValue(), SEVEN.getValue(), calculator.getTextPaneValueForProgrammerPanel());
    }

    @Test
    public void testPushingModulusButtonWithOneInputReturnsZero() {
        when(actionEvent.getActionCommand()).thenReturn(MODULUS.getValue());
        String number = "00000101"; //5
        calculator.getTextPane().setText(calculator.addNewLines() + number);
        calculator.getValues()[0] = FIVE.getValue();
        calculator.getValues()[1] = BLANK.getValue();
        calculator.setValuesPosition(0);
        programmerPanel.performButtonModulusAction(actionEvent);
        assertEquals(TEXT_PANE_WRONG_VALUE.getValue(), number+" Mod", calculator.getTextPaneValueForProgrammerPanel());
        assertNotEquals("Values[0] should not match ", calculator.getValues()[0], calculator.getTextPaneValueForProgrammerPanel());
    }

    @Test
    public void testPushingXorButtonWithBlankInputsDoesNothing()
    {
        when(actionEvent.getActionCommand()).thenReturn(XOR.getValue());
        calculator.getValues()[0] = BLANK.getValue();
        calculator.getValues()[1] = BLANK.getValue();
        calculator.setValuesPosition(1);
        programmerPanel.performButtonXorAction(actionEvent);
        assertEquals(TEXT_PANE_WRONG_VALUE.getValue(), BLANK.getValue(), calculator.getTextPaneValueForProgrammerPanel());
        assertEquals("Values[0] should be empty", BLANK.getValue(), calculator.getValues()[0]);
        assertEquals("Values[1] should be empty", BLANK.getValue(), calculator.getValues()[1]);
        assertFalse("XorButton should be set", programmerPanel.isXor());
    }

    @Test
    public void testPushingXorButtonWithOneInput() {

    }

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

//    COMMENTED OUT
//    @Test
//    public void testConvertingBinaryToDecimalWorks() throws CalculatorError
//    {
//        // Test that this work
//        //String test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "10000000");
//        //assertEquals(Integer.parseInt(test), 128);
//        // Test another number
//        //test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "11111111");
//        //assertEquals(Integer.parseInt(test), 255);
//        // Make sure this returns the same as above, although i believe this entry by the user is impossible
//        //test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "11111111 ");
//        //assertEquals(Integer.parseInt(test), 255);
//        // Test to make sure an incomplete number returns appropriately
//        //test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "101");
//        //assertEquals(Integer.parseInt(test), 5);
//        // Test to make sure a WORD BINARY entry returns appropriately
//        //c.setByte(false);
//        //c.setWord(false);
//        String test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, new String("00000001 00000000"));
//        assertEquals(Integer.parseInt(test), 256);
//
//    }

    @Test
    public void testPerformModulusWorksAsExpected()
    {
        when(actionEvent.getActionCommand()).thenReturn(MODULUS.getValue());
        calculator.getValues()[0] = FOUR.getValue();
        calculator.getValues()[1] = THREE.getValue();
        programmerPanel.performButtonModulusAction(actionEvent);
        assertEquals("Modulus not as expected", ONE.getValue(), calculator.getValues()[0]);
    }

    @Test
    public void testPerformModulusWithZeroShouldNotCompute()
    {
        when(actionEvent.getActionCommand()).thenReturn(MODULUS.getValue());
        calculator.getValues()[0] = FOUR.getValue();
        calculator.getValues()[1] = ZERO.getValue();
        programmerPanel.performButtonModulusAction(actionEvent);
        assertEquals("Modulus not as expected", ZERO.getValue(), calculator.getValues()[0]);
    }

}
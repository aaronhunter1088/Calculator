package Panels;

import Calculators.Calculator;
import Types.CalculatorView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import static Types.CalculatorBase.*;
import static Types.CalculatorByte.*;
import static Types.Texts.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProgrammerPanelTest
{
    static { System.setProperty("appName", ProgrammerPanelTest.class.getSimpleName()); }
    private static Logger LOGGER;
    private static Calculator calculator;
    private static ProgrammerPanel programmerPanel;
    private final double delta = 0.000001d;

    @Mock
    ActionEvent actionEvent;

    @BeforeAll
    public static void beforeAll() throws Exception
    {
        LOGGER = LogManager.getLogger(ProgrammerPanelTest.class);
        calculator = new Calculator(CalculatorView.VIEW_PROGRAMMER);
        calculator.pack();
    }

    @AfterAll
    public static void afterAll()
    { LOGGER.info("Finished running {}", ProgrammerPanel.class.getSimpleName()); }

    @BeforeEach
    public void beforeEach() throws Exception 
    {
        MockitoAnnotations.initMocks(this);
        calculator = new Calculator(CalculatorView.VIEW_PROGRAMMER);
        calculator.pack();
        calculator.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        programmerPanel = (ProgrammerPanel)calculator.getCurrentPanel();
    }

    @AfterEach
    public void afterEach() {
        if (calculator != null) {
            LOGGER.info("Test complete. Closing the calculator...");
            // Create a WindowEvent with WINDOW_CLOSING event type
            WindowEvent windowClosing = new WindowEvent(calculator, WindowEvent.WINDOW_CLOSING);

            // Dispatch the event to the JFrame instance
            calculator.dispatchEvent(windowClosing);

            // Ensure the clock is no longer visible
            assertFalse(calculator.isVisible());

            // Dispose of the JFrame to release resources
            calculator.dispose();
        }
    }

    @Test
    public void switchingFromBasicToProgrammerConvertsTextArea()
    {
        calculator.appendTextToPane(FOUR.getValue());
        calculator.getValues()[0] = FOUR.getValue();
        calculator.setCalculatorByte(BYTE_BYTE);
        String convertedValue = calculator.convertFromBaseToBase(BASE_DECIMAL, BASE_BINARY, calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("00000100", convertedValue, "Conversion went wrong!");
        programmerPanel.appendTextToProgrammerPane(convertedValue);
        assertEquals("00000100", calculator.getTextPaneValue(), TEXT_PANE_WRONG_VALUE.getValue());
    }

    @Test
    public void pressingNotButtonReversesAllBits()
    {
        when(actionEvent.getActionCommand()).thenReturn(NOT.getValue());
        calculator.setCalculatorBase(BASE_BINARY);
        programmerPanel.appendTextToProgrammerPane("0000 1011");
        programmerPanel.performNotButtonAction(actionEvent);
        //assertEquals("topQWord not as expected", ""); // lots of 1's
        assertEquals("1111 0100", programmerPanel.separateBits(calculator.getTextPaneValue()), TEXT_PANE_WRONG_VALUE.getValue());
    }

//    @Test
//    public void testProgrammerNumberEntry() throws CalculatorError {
//        // This method tests the user's ability to
//        //1. Input a single, 8 bit number
//        //2. Press an operator
//        //3. Input another single, 8 bit number
//        //4. Press equals.
//        //5. Textarea displays proper sum in 8 bit form
//        calculator.appendTextToPane("");
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
        programmerPanel.performOrButtonAction(actionEvent);
        assertTrue(calculator.isFirstNumber(), "Expected isFirstNumber to be true");
        assertEquals(BLANK.getValue(), calculator.getValues()[0], "Expected values[0] to be empty");
        assertEquals("50", calculator.getValues()[1], "Expected values[1] to be 50");
    }

    @Test
    public void testPushOrWithDecimalInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(OR.getValue());
        calculator.setCalculatorBase(BASE_DECIMAL); // BYTE_BYTE default
        calculator.appendTextToPane(FIVE.getValue());
        calculator.getValues()[0] = FIVE.getValue();
        programmerPanel.performOrButtonAction(actionEvent);
        // TODO: Uncomment and fix
        //assertEquals(TEXT_PANE_WRONG_VALUE.getValue(), "5 OR", calculator.getTextPaneValueForProgrammerPanel());
        assertEquals("5", calculator.getValues()[0], "Values[0] is not in decimal base form");
    }

    @Test
    public void testPushOrWithBothValuesSetWithDecimalInputReturnsResult()
    {
        when(actionEvent.getActionCommand()).thenReturn(OR.getValue());
        calculator.setCalculatorBase(BASE_DECIMAL); // BYTE_BYTE default
        calculator.getValues()[0] = FIVE.getValue();
        calculator.getValues()[1] = THREE.getValue();
        calculator.appendTextToPane(THREE.getValue());
        programmerPanel.performOrButtonAction(actionEvent);
        assertEquals(SEVEN.getValue(), calculator.getTextPaneValue(), TEXT_PANE_WRONG_VALUE.getValue());
    }

    @Test
    public void testPushingModulusButtonWithOneInputReturnsZero()
    {
        when(actionEvent.getActionCommand()).thenReturn(MODULUS.getValue());
        String number = "00000101"; //5
        calculator.appendTextToPane(calculator.addNewLines() + number);
        calculator.getValues()[0] = FIVE.getValue();
        calculator.getValues()[1] = BLANK.getValue();
        calculator.setValuesPosition(0);
        programmerPanel.performButtonModulusAction(actionEvent);
        // TODO: Uncomment and fix
        //assertEquals(TEXT_PANE_WRONG_VALUE.getValue(), number+" Mod", calculator.getTextPaneValueForProgrammerPanel());
        assertNotEquals("Values[0] should not match ", calculator.getValues()[0], calculator.getTextPaneValue());
    }

    @Test
    public void testPushingXorButtonWithBlankInputsDoesNothing()
    {
        when(actionEvent.getActionCommand()).thenReturn(XOR.getValue());
        calculator.getValues()[0] = BLANK.getValue();
        calculator.getValues()[1] = BLANK.getValue();
        calculator.setValuesPosition(1);
        programmerPanel.performXorButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue(), TEXT_PANE_WRONG_VALUE.getValue());
        assertEquals(BLANK.getValue(), calculator.getValues()[0], "Values[0] should be empty");
        assertEquals(BLANK.getValue(), calculator.getValues()[1], "Values[1] should be empty");
        assertFalse(programmerPanel.isXor(), "XorButton should be set");
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
        assertEquals(ONE.getValue(), calculator.getValues()[0], "Modulus not as expected");
    }

    @Test
    public void testPerformModulusWithZeroShouldNotCompute()
    {
        when(actionEvent.getActionCommand()).thenReturn(MODULUS.getValue());
        calculator.getValues()[0] = FOUR.getValue();
        calculator.getValues()[1] = ZERO.getValue();
        programmerPanel.performButtonModulusAction(actionEvent);
        assertEquals(ZERO.getValue(), calculator.getValues()[0], "Modulus not as expected");
    }

    @Test
    public void testClickedButtonAndWhenTextPaneBlank()
    {
        when(actionEvent.getActionCommand()).thenReturn(AND.getValue());
        programmerPanel.performAndButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue(), "Expected textPane to say " + ENTER_A_NUMBER.getValue());
    }

    @Test
    public void testClickedButtonAndWhenTextPaneContainsBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(AND.getValue());
        calculator.appendTextToPane(ENTER_A_NUMBER.getValue());
        programmerPanel.performAndButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue(), "Expected textPane to say " + ENTER_A_NUMBER.getValue());
    }

    @Test
    public void testClickedButtonAndWhenValuesAtZeroIsEmpty()
    {
        when(actionEvent.getActionCommand()).thenReturn(AND.getValue());
        programmerPanel.performAndButtonAction(actionEvent);
        assertEquals(BLANK.getValue(), calculator.getValues()[0], "Expected values[0] to be empty");
        assertEquals(ENTER_A_NUMBER.getValue(), calculator.getTextPaneValue(), "Expected textPane to say " + ENTER_A_NUMBER.getValue());
    }

    @Test
    public void testClickedButtonAndWhenValuesAtOneIsEmpty()
    {
        when(actionEvent.getActionCommand()).thenReturn(AND.getValue());
        calculator.getValues()[0] = FIVE.getValue();
        calculator.appendTextToPane(FIVE.getValue());
        programmerPanel.performAndButtonAction(actionEvent);
        assertEquals(FIVE.getValue(), calculator.getValues()[0], "Expected values[0] to be 5");
        assertEquals("5 AND", calculator.getTextPaneValue(), "Expected textPane to say 5 AND");
    }

    @Test
    @DisplayName("Test And Button")
    public void testPerformAdd()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(FIVE.getValue()).thenReturn(FIVE.getValue())
                .thenReturn(AND.getValue())
                .thenReturn(SIX.getValue()).thenReturn(SIX.getValue())
                .thenReturn(EQUALS.getValue());
        programmerPanel.performNumberButtonActions(actionEvent);
        assertEquals(FIVE.getValue(), calculator.getTextPaneValue(), "Expected textPane to be 5");

        programmerPanel.performAndButtonAction(actionEvent);
        assertEquals("5 AND", calculator.getTextPaneValue(), "Expected textPane to be 5 AND");
        assertTrue(programmerPanel.isAnd(), "Expected isAnd to be true");

        programmerPanel.performNumberButtonActions(actionEvent);
        assertEquals(SIX.getValue(), calculator.getTextPaneValue(), "Expected textPane to contain 6");
        assertTrue(programmerPanel.isAnd(), "Expected isAnd to be true");

        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(FOUR.getValue(), calculator.getTextPaneValue(), "Expected textPane to be 4");
        assertFalse(programmerPanel.isAnd(), "Expected isAnd to be false");
    }

    @Test
    @DisplayName("Test And Button continued operation")
    public void testPerformAndContinuedOperation()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(FIVE.getValue()).thenReturn(FIVE.getValue()) // leave,
                .thenReturn(AND.getValue())
                .thenReturn(SIX.getValue()).thenReturn(SIX.getValue()) // leave
                .thenReturn(AND.getValue());
        programmerPanel.performNumberButtonActions(actionEvent);
        assertEquals(FIVE.getValue(), calculator.getTextPaneValue(), "Expected textPane to be 5");

        programmerPanel.performAndButtonAction(actionEvent);
        assertEquals("5 AND", calculator.getTextPaneValue(), "Expected textPane to be 5 AND");
        assertTrue(programmerPanel.isAnd(), "Expected isAnd to be true");

        programmerPanel.performNumberButtonActions(actionEvent);
        assertEquals(SIX.getValue(), calculator.getTextPaneValue(), "Expected textPane to contain 6");
        assertTrue(programmerPanel.isAnd(), "Expected isAnd to be true");

        programmerPanel.performAndButtonAction(actionEvent);
        assertEquals("4 AND", calculator.getTextPaneValue(), "Expected textPane to be 4 AND");
        assertTrue(programmerPanel.isAnd(), "Expected isAnd to be true");
    }

}
package Panels;

import Calculators.Calculator;
import Parent.TestParent;
import Types.CalculatorBase;
import Types.SystemDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.stream.Stream;

import static Types.CalculatorBase.BASE_BINARY;
import static Types.CalculatorBase.BASE_DECIMAL;
import static Types.CalculatorByte.BYTE_BYTE;
import static Types.CalculatorView.VIEW_PROGRAMMER;
import static Types.Texts.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * ProgrammerPanelTest
 * <p>
 * This class tests the {@link ProgrammerPanel} class.
 *
 * @author Michael Ball
 * @version 4.0
 */
@ExtendWith(MockitoExtension.class)
class ProgrammerPanelTest extends TestParent
{
    static { System.setProperty("appName", ProgrammerPanelTest.class.getSimpleName()); }
    private static final Logger LOGGER = LogManager.getLogger(ProgrammerPanelTest.class);

    private ProgrammerPanel programmerPanel;

    @Mock
    public SystemDetector systemDetector;
    @Mock
    public ActionEvent actionEvent;

    @BeforeAll
    static void beforeAll()
    {}

    @BeforeEach
    void beforeEach() throws Exception
    {
        SwingUtilities.invokeAndWait(() -> {
            try {
                LOGGER.info("Starting test");
                mocks = MockitoAnnotations.openMocks(this);
                calculator = spy(new Calculator(VIEW_PROGRAMMER));
                calculator.setSystemDetector(systemDetector);
                programmerPanel = calculator.getProgrammerPanel();
                programmerPanel.setupProgrammerPanel(calculator);
            } catch (Exception ignored) {}
        });
    }

//    @AfterEach
//    void afterEach() throws InterruptedException, InvocationTargetException
//    {
//        for (Calculator calculator : java.util.List.of(calculator))
//        {
//            LOGGER.info("Test complete. Closing the calculator...");
//            EventQueue.invokeAndWait(() -> {
//                calculator.setVisible(false);
//                calculator.dispose();
//            });
//        }
//    }

    @AfterAll
    static void afterAll()
    { LOGGER.info("Finished running {}", ProgrammerPanel.class.getSimpleName()); }

    @Test
    public void switchingFromBasicToProgrammerConvertsTextArea()
    {
        calculator.appendTextToPane(FOUR);
        calculator.getValues()[0] = FOUR;
        calculator.setCalculatorByte(BYTE_BYTE);
        String convertedValue = calculator.convertFromBaseToBase(BASE_DECIMAL, BASE_BINARY, calculator.getValues()[calculator.getValuesPosition()]);
        assertEquals("00000100", convertedValue, "Conversion went wrong!");
        programmerPanel.appendTextForProgrammerPanel(convertedValue);
        assertEquals("00000100", calculator.getTextPaneValue(), TEXT_PANE_WRONG_VALUE);
    }

    @Test
    public void pressingNotButtonReversesAllBits()
    {
        when(actionEvent.getActionCommand()).thenReturn(NOT);
        calculator.setCalculatorBase(BASE_BINARY);
        programmerPanel.appendTextForProgrammerPanel("0000 1011");
        programmerPanel.performNotButtonAction(actionEvent);
        //assertEquals("topQWord not as expected", ""); // lots of 1's
        assertEquals("1111 0100", programmerPanel.separateBits(calculator.getTextPaneValue()), TEXT_PANE_WRONG_VALUE);
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
        when(actionEvent.getActionCommand()).thenReturn(OR);
        calculator.getValues()[0] = EMPTY;
        calculator.getValues()[1] = "50";
        programmerPanel.performOrButtonAction(actionEvent);
        assertTrue(calculator.isObtainingFirstNumber(), "Expected isFirstNumber to be true");
        assertEquals(EMPTY, calculator.getValues()[0], "Expected values[0] to be empty");
        assertEquals("50", calculator.getValues()[1], "Expected values[1] to be 50");
    }

    @Test
    public void testPushOrWithDecimalInput()
    {
        when(actionEvent.getActionCommand()).thenReturn(OR);
        calculator.setCalculatorBase(BASE_DECIMAL); // BYTE_BYTE default
        calculator.appendTextToPane(FIVE);
        calculator.getValues()[0] = FIVE;
        programmerPanel.performOrButtonAction(actionEvent);
        // TODO: Uncomment and fix
        //assertEquals(TEXT_PANE_WRONG_VALUE, "5 OR", calculator.getTextPaneValueForProgrammerPanel());
        assertEquals("5", calculator.getValues()[0], "Values[0] is not in decimal base form");
    }

    @Test
    public void testPushOrWithBothValuesSetWithDecimalInputReturnsResult()
    {
        when(actionEvent.getActionCommand()).thenReturn(OR);
        calculator.setCalculatorBase(BASE_DECIMAL); // BYTE_BYTE default
        calculator.getValues()[0] = FIVE;
        calculator.getValues()[1] = THREE;
        calculator.appendTextToPane(THREE);
        programmerPanel.performOrButtonAction(actionEvent);
        assertEquals(SEVEN, calculator.getTextPaneValue(), TEXT_PANE_WRONG_VALUE);
    }

    @ParameterizedTest()
    @MethodSource("getModulusCases")
    @DisplayName("Test Modulus Button")
    public void testModulusButton(CalculatorBase base, String firstNumber, String secondNumber,
                                  String nextOperator, String expectedResult)
    {
        calculator.setCalculatorBase(base);
        calculator.getValues()[0] = firstNumber;
        calculator.getValues()[1] = secondNumber;
        calculator.getValues()[2] = MODULUS;
        calculator.setValuesPosition(1);
        calculator.appendTextToPane(calculator.getValueAt());
        when(actionEvent.getActionCommand())
                .thenReturn(nextOperator); // can be any button push
        performNextOperatorAction(calculator, actionEvent, LOGGER, nextOperator);

        assertEquals(expectedResult, calculator.getTextPaneValue(), "Text Pane result not as expected");
        assertEquals(EMPTY, calculator.getValueAt(0), "values[0] result should be EMPTY");
        assertEquals(EMPTY, calculator.getValueAt(1), "values[1] result should be EMPTY");
        assertEquals(EMPTY, calculator.getValueAt(2), "values[2] result should be EMPTY");
    }
    private static Stream<Arguments> getModulusCases()
    {
        return Stream.of(
                Arguments.of(BASE_BINARY, FIVE, THREE, EQUALS, "00000010"),
                Arguments.of(BASE_DECIMAL, FIVE, THREE, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, FOUR, THREE, EQUALS, ONE),
                Arguments.of(BASE_DECIMAL, FOUR, ZERO, EQUALS, INFINITY),
                Arguments.of(BASE_DECIMAL, SUBTRACTION+FOUR, TWO, EQUALS, ZERO),
                Arguments.of(BASE_DECIMAL, ONE+ZERO, THREE, EQUALS, ONE),
                Arguments.of(BASE_DECIMAL, ONE+SEVEN, FIVE, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, THREE, SEVEN, EQUALS, THREE),
                Arguments.of(BASE_DECIMAL, FOUR, ONE+ZERO, EQUALS, FOUR),
                Arguments.of(BASE_DECIMAL, SEVEN, SEVEN, EQUALS, ZERO),
                Arguments.of(BASE_DECIMAL, TWO+FIVE, FOUR, EQUALS, ONE),
                Arguments.of(BASE_DECIMAL, ONE+ZERO+ZERO, THREE+ZERO, EQUALS, ONE+ZERO),
                Arguments.of(BASE_DECIMAL, TWO+FOUR, SIX, EQUALS, ZERO),
                Arguments.of(BASE_DECIMAL, EIGHT+ONE, NINE, EQUALS, ZERO),
                Arguments.of(BASE_DECIMAL, SUBTRACTION+ONE+ZERO, THREE, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, SUBTRACTION+FIVE, SEVEN, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, ONE+ZERO, SUBTRACTION+THREE, EQUALS, ONE),
                Arguments.of(BASE_DECIMAL, SUBTRACTION+TWO+ZERO, SUBTRACTION+SIX, EQUALS, FOUR),
                Arguments.of(BASE_DECIMAL, ONE+ZERO.repeat(5), SEVEN, EQUALS, FOUR),
                Arguments.of(BASE_DECIMAL, TWO+TWO, FIVE, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, TWO+ZERO, SIX, EQUALS, TWO),
                Arguments.of(BASE_DECIMAL, TWO, FOUR, EQUALS, TWO)
        );
    }

    @Test
    public void testPushingXorButtonWithBlankInputsDoesNothing()
    {
        when(actionEvent.getActionCommand()).thenReturn(XOR);
        calculator.getValues()[0] = EMPTY;
        calculator.getValues()[1] = EMPTY;
        calculator.setValuesPosition(1);
        programmerPanel.performXorButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), TEXT_PANE_WRONG_VALUE);
        assertEquals(EMPTY, calculator.getValues()[0], "Values[0] should be empty");
        assertEquals(EMPTY, calculator.getValues()[1], "Values[1] should be empty");
        assertNotEquals(XOR, calculator.getValueAt(2), "Expecting NOT XOR at values[2]");
    }

    @Test
    public void testClickedButtonAndWhenTextPaneBlank()
    {
        when(actionEvent.getActionCommand()).thenReturn(AND);
        programmerPanel.performAndButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to say " + ENTER_A_NUMBER);
    }

    @Test
    public void testClickedButtonAndWhenTextPaneContainsBadText()
    {
        when(actionEvent.getActionCommand()).thenReturn(AND);
        calculator.appendTextToPane(ENTER_A_NUMBER);
        programmerPanel.performAndButtonAction(actionEvent);
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to say " + ENTER_A_NUMBER);
    }

    @Test
    public void testClickedButtonAndWhenValuesAtZeroIsEmpty()
    {
        when(actionEvent.getActionCommand()).thenReturn(AND);
        programmerPanel.performAndButtonAction(actionEvent);
        assertEquals(EMPTY, calculator.getValues()[0], "Expected values[0] to be empty");
        assertEquals(ENTER_A_NUMBER, calculator.getTextPaneValue(), "Expected textPane to say " + ENTER_A_NUMBER);
    }

    @Test
    public void testClickedButtonAndWhenValuesAtOneIsEmpty()
    {
        when(actionEvent.getActionCommand()).thenReturn(AND);
        calculator.getValues()[0] = FIVE;
        calculator.appendTextToPane(FIVE);
        programmerPanel.performAndButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getValues()[0], "Expected values[0] to be 5");
        assertEquals("5 AND", calculator.getTextPaneValue(), "Expected textPane to say 5 AND");
    }

    @Test
    @DisplayName("Test And Button")
    public void testPerformAdd()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(FIVE).thenReturn(FIVE)
                .thenReturn(AND)
                .thenReturn(SIX).thenReturn(SIX)
                .thenReturn(EQUALS);
        programmerPanel.getCalculator().performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getTextPaneValue(), "Expected textPane to be 5");

        programmerPanel.performAndButtonAction(actionEvent);
        assertEquals("5 AND", calculator.getTextPaneValue(), "Expected textPane to be 5 AND");
        assertEquals(AND, calculator.getValueAt(2), "Expecting AND at values[2]");

        programmerPanel.getCalculator().performNumberButtonAction(actionEvent);
        assertEquals(SIX, calculator.getTextPaneValue(), "Expected textPane to contain 6");
        assertEquals(AND, calculator.getValueAt(2), "Expecting AND at values[2]");

        calculator.performEqualsButtonAction(actionEvent);
        assertEquals(FOUR, calculator.getTextPaneValue(), "Expected textPane to be 4");
        assertNotEquals(AND, calculator.getValueAt(2), "Expecting NOT AND at values[2]");
    }

    @Test
    @DisplayName("Test And Button continued operation")
    public void testPerformAndContinuedOperation()
    {
        when(actionEvent.getActionCommand())
                .thenReturn(FIVE).thenReturn(FIVE) // leave,
                .thenReturn(AND)
                .thenReturn(SIX).thenReturn(SIX) // leave
                .thenReturn(AND);
        programmerPanel.getCalculator().performNumberButtonAction(actionEvent);
        assertEquals(FIVE, calculator.getTextPaneValue(), "Expected textPane to be 5");

        programmerPanel.performAndButtonAction(actionEvent);
        assertEquals("5 AND", calculator.getTextPaneValue(), "Expected textPane to be 5 AND");
        assertEquals(AND, calculator.getValueAt(2), "Expecting AND at values[2]");

        programmerPanel.getCalculator().performNumberButtonAction(actionEvent);
        assertEquals(SIX, calculator.getTextPaneValue(), "Expected textPane to contain 6");
        assertEquals(AND, calculator.getValueAt(2), "Expecting AND at values[2]");

        programmerPanel.performAndButtonAction(actionEvent);
        assertEquals("4 AND", calculator.getTextPaneValue(), "Expected textPane to be 4 AND");
        assertEquals(AND, calculator.getValueAt(2), "Expecting AND at values[2]");
    }

}
package Calculators;

import Panels.BasicPanel;
import Panels.ConverterPanel;
import Panels.DatePanel;
import Panels.ProgrammerPanel;
import Types.CalculatorConverterType;
import Types.CalculatorView;
import Types.DateOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.stream.Stream;

import static Types.CalculatorBase.*;
import static Types.CalculatorByte.*;
import static Types.CalculatorConverterType.ANGLE;
import static Types.CalculatorConverterType.AREA;
import static Types.CalculatorView.*;
import static Types.DateOperation.ADD_SUBTRACT_DAYS;
import static Types.DateOperation.DIFFERENCE_BETWEEN_DATES;
import static Types.Texts.*;
import static Utilities.LoggingUtil.confirm;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * CalculatorTests
 * <p>
 * This class tests the {@link Calculator} class.
 *
 * @author Michael Ball
 * @version 4.0
 */
@ExtendWith(MockitoExtension.class)
class CalculatorTests
{
    static { System.setProperty("appName", CalculatorTests.class.getSimpleName()); }
    private static final Logger LOGGER = LogManager.getLogger(CalculatorTests.class.getSimpleName());

    private Calculator calculator;
    @Mock
    ActionEvent actionEvent;
    @Spy
    Calculator calculatorSpy;

    @BeforeAll
    static void beforeAll()
    {

    }

    @BeforeEach
    void beforeEach() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        LOGGER.info("Setting up beforeEach...");
        calculator = new Calculator();
        calculator.setVisible(true);
    }

    @AfterAll
    static void afterAll()
    { LOGGER.info("Finished running {}", CalculatorTests.class.getSimpleName()); }


    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException
    {
        for (Calculator calculator : java.util.List.of(calculator, calculatorSpy))
        {
            LOGGER.info("Test complete. Closing the calculator...");
            EventQueue.invokeAndWait(() -> {
                calculator.setVisible(false);
                calculator.dispose();
            });
            assertFalse(calculator.isVisible());
        }
    }

    /************* Basic Calculator Tests ******************/
    @Test
    void createBasicCalculatorDefault()
    {
        //calculator = new Calculator();
        assertTrue(calculator.isVisible(), "Cannot see basic calculator");
        assertEquals(VIEW_BASIC, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_BASIC);
    }

    @Test
    void createBasicCalculatorEnforced()
    {
        LOGGER.info("createBasicCalculator enforced...");
        //calculator = new Calculator(VIEW_BASIC);
        calculator.setCalculatorView(VIEW_BASIC);
        assertTrue(calculator.isVisible(), "Cannot see basic calculator");
        assertEquals(VIEW_BASIC, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_BASIC);
    }

    /************* Programmer Calculator Tests ******************/
    @Test
    void createProgrammerCalculator()
    {
        LOGGER.info("createProgrammerCalculator...");
        //calculator = new Calculator(VIEW_PROGRAMMER);
        calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.setCalculatorBase(BASE_DECIMAL);
        calculator.setIsMotif(true);
        assertTrue(calculator.isVisible(), "Cannot see programmer calculator");
        assertEquals(VIEW_PROGRAMMER, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_PROGRAMMER);
        assertSame(BASE_DECIMAL, calculator.getCalculatorBase(), "Base is not decimal");
    }

    @Test
    void createProgrammerCalculatorWithBaseAndByte() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        calculator = new Calculator(BASE_BINARY, BYTE_WORD);
        calculator.setVisible(true);
        assertTrue(calculator.isVisible(), "Cannot see programmer calculator");
        assertEquals(VIEW_PROGRAMMER, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_PROGRAMMER);
        assertSame(BASE_BINARY, calculator.getCalculatorBase(), "Base is not binary");
        assertSame(BYTE_WORD, calculator.getCalculatorByte(), "Byte is not WORD");
    }

    @Test
    void createProgrammerCalculatorInBinaryEnforced()
    {
        calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.setCalculatorBase(BASE_BINARY);
        assertTrue(calculator.isVisible(), "Cannot see programmer calculator");
        assertEquals(VIEW_PROGRAMMER, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_PROGRAMMER);
        assertSame(BASE_BINARY, calculator.getCalculatorBase(), "Base is not binary");
    }

    @Test
    void createProgrammerCalculatorInOctalEnforced()
    {
        calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.setCalculatorBase(BASE_OCTAL);
        assertTrue(calculator.isVisible(), "Cannot see programmer calculator");
        assertEquals(BASE_OCTAL, calculator.getCalculatorBase(), "Base is not octal");
    }

    @Test
    void createProgrammerCalculatorInDecimalEnforced()
    {
        calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.setCalculatorBase(BASE_DECIMAL);
        assertTrue(calculator.isVisible(), "Cannot see programmer calculator");
        assertEquals(VIEW_PROGRAMMER, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_PROGRAMMER);
        assertSame(BASE_DECIMAL, calculator.getCalculatorBase(), "Base is not decimal");
    }

    @Test
    void createProgrammerCalculatorInHexadecimalEnforced()
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", BASE_HEXADECIMAL);
        //calculator = new Calculator(BASE_HEXADECIMAL);
        calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.setCalculatorBase(BASE_HEXADECIMAL);
        assertTrue(calculator.isVisible(), "Cannot see programmer calculator");
        assertSame(BASE_HEXADECIMAL, calculator.getCalculatorBase(), "Base is not binary");
    }

    /************* Scientific Calculator Tests ******************/
    @Test
    void createScientificCalculator()
    {
        LOGGER.warn("createScientificCalculator...");
        LOGGER.warn("IMPLEMENT...");
    }

    /************* Date Calculator Tests ******************/
    @Test
    void createDateCalculator()
    {
        LOGGER.info("createDateCalculator...");
        //calculator = new Calculator(VIEW_DATE);
        calculator.setCalculatorView(VIEW_DATE);
        calculator.setDateOperation(DIFFERENCE_BETWEEN_DATES);
        calculator.setCurrentPanel(calculator.getDatePanel());
        calculator.setupPanel(null);
        assertTrue(calculator.isVisible(), "Cannot see date calculator");
        assertEquals(VIEW_DATE, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_DATE);
        assertSame(DIFFERENCE_BETWEEN_DATES, calculator.getDateOperation(), "Date operation is not " + DIFFERENCE_BETWEEN_DATES);
    }

    @ParameterizedTest
    @MethodSource("provideDateOperations")
    void testCreateDateCalculatorWithDateOperationEnforced(DateOperation dateOperation) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        calculator = new Calculator(dateOperation);
        calculator.setVisible(true);
        assertTrue(calculator.isVisible(), "Cannot see date calculator");
        assertEquals(VIEW_DATE, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_DATE);
        assertSame(dateOperation, calculator.getDateOperation(), "Date operation is not " + dateOperation);
    }
    private static Stream<Arguments> provideDateOperations() {
        return Stream.of(
                Arguments.of(DIFFERENCE_BETWEEN_DATES),
                Arguments.of(ADD_SUBTRACT_DAYS)
        );
    }

    @Test
    void createDateCalculatorWithDateOperation1Enforced()
    {
        calculator.setCalculatorView(VIEW_DATE);
        calculator.setDateOperation(DIFFERENCE_BETWEEN_DATES);
        assertTrue(calculator.isVisible(), "Cannot see date calculator");
        assertEquals(VIEW_DATE, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_DATE);
        assertSame(DIFFERENCE_BETWEEN_DATES, calculator.getDateOperation(), "Date operation is not " + DIFFERENCE_BETWEEN_DATES);
    }

    @Test
    void createDateCalculatorWithDateOperation2Enforced()
    {
        LOGGER.info("createDateCalculator with {} enforced...", ADD_SUBTRACT_DAYS);
        //calculator = new Calculator(ADD_SUBTRACT_DAYS);
        calculator.setCalculatorView(VIEW_DATE);
        calculator.setDateOperation(ADD_SUBTRACT_DAYS);
        assertTrue(calculator.isVisible(), "Cannot see date calculator");
        assertEquals(VIEW_DATE, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_DATE);
        assertSame(ADD_SUBTRACT_DAYS, calculator.getDateOperation(), "Date operation is not " + ADD_SUBTRACT_DAYS);
    }

    /************* Converter Calculator Tests ******************/
    @Test
    void createConverterCalculator()
    {
        LOGGER.info("createConverterCalculator...");
        //calculator = new Calculator(VIEW_CONVERTER);
        calculator.setCalculatorView(VIEW_CONVERTER);
        calculator.setConverterType(ANGLE);
        calculator.setCurrentPanel(calculator.getConverterPanel());
        calculator.setupPanel(null);
        assertTrue(calculator.isVisible(), "Cannot see converter calculator");
        assertEquals(VIEW_CONVERTER, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_CONVERTER);
        assertSame(ANGLE, calculator.getConverterType(), "Converter operation is not " + ANGLE);
    }

    @ParameterizedTest
    @MethodSource("provideConverterTypes")
    void testCreateConverterCalculatorWithTypeEnforced(CalculatorConverterType converterType) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        calculator = new Calculator(converterType);
        calculator.setVisible(true);
        assertTrue(calculator.isVisible(), "Cannot see converter calculator");
        assertEquals(VIEW_CONVERTER, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_CONVERTER);
        assertSame(converterType, calculator.getConverterType(), "Converter operation is not " + converterType);
    }
    private static Stream<Arguments> provideConverterTypes() {
        return Stream.of(
                Arguments.of(ANGLE),
                Arguments.of(AREA)
        );
    }

    @Test
    void createConverterCalculatorWithAngleEnforced()
    {
        calculator.setCalculatorView(VIEW_CONVERTER);
        calculator.setConverterType(ANGLE);
        calculator.setCurrentPanel(calculator.getConverterPanel());
        calculator.setupPanel(null);
        assertTrue(calculator.isVisible(), "Cannot see converter calculator");
        assertEquals(VIEW_CONVERTER, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_CONVERTER);
        assertSame(ANGLE, calculator.getConverterType(), "Converter operation is not " + ANGLE);
    }

    @Test
    void createConverterCalculatorWithAreaEnforced()
    {
        LOGGER.info("createConverterCalculator with {} enforced...", AREA);
        //calculator = new Calculator(AREA);
        calculator.setCalculatorView(VIEW_CONVERTER);
        calculator.setConverterType(AREA);
        calculator.setCurrentPanel(calculator.getConverterPanel());
        calculator.setupPanel(null);
        assertTrue(calculator.isVisible(), "Cannot see converter calculator");
        assertEquals(VIEW_CONVERTER, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_CONVERTER);
        assertSame(AREA, calculator.getConverterType(), "Converter operation is not " + AREA);
    }

    /************* Create Calculator By View ******************/
    @ParameterizedTest
    @MethodSource("provideCalculatorViews")
    void createCalculatorByView(CalculatorView calculatorView) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        calculator = new Calculator(calculatorView);
        calculator.setVisible(true);
        assertTrue(calculator.isVisible(), "Cannot see calculator for " + calculatorView.getValue());
        assertEquals(calculatorView, calculator.getCalculatorView(), "Expected CalculatorView to be " + calculatorView.getValue());
    }
    private static Stream<Arguments> provideCalculatorViews() {
        return Stream.of(
                Arguments.of(VIEW_BASIC),
                Arguments.of(VIEW_PROGRAMMER),
                Arguments.of(VIEW_SCIENTIFIC),
                Arguments.of(VIEW_DATE),
                Arguments.of(VIEW_CONVERTER)
        );
    }

    /************* All Calculator Tests ******************/
    @Test
    void testConvertingToPositive()
    {
        assertEquals("5.02", calculator.convertToPositive("-5.02"), "Number is not positive");
    }

    @Test
    void testConvertingToNegative()
    {
        assertEquals("-5.02", calculator.convertToNegative("5.02"), "Number is not negative");
    }

    @Test
    void testIsPositiveReturnsTrue()
    {
        assertTrue(calculator.isPositiveNumber(SIX), "IsPositive did not return true");
    }

    @Test
    void testIsPositiveReturnsFalse()
    {
        assertFalse(calculator.isPositiveNumber("-6"), "IsPositive did not return false");
    }

    @Test
    void testIsNegativeReturnsTrue()
    {
        assertTrue(calculator.isNegativeNumber("-6"), "IsNegative did not return true");
    }

    @Test
    void testIsNegativeReturnsFalse()
    {
        assertFalse(calculator.isNegativeNumber(SIX), "IsNegative did not return false");
    }

    @Test
    void testIsDecimalNumberReturnsTrue()
    {
        assertTrue(calculator.isDecimalNumber("5.02"), "Number should contain the decimal");
    }

    @Test
    void testIsDecimalNumberReturnsFalse()
    {
        assertFalse(calculator.isDecimalNumber(FIVE), "Number should not contain the decimal");
    }

    @Test
    void methodResetCalculatorOperationsWithTrueResultsInAllOperatorsBeingTrue()
    {
        calculator.resetOperators(true);
        assertTrue(calculator.isFirstNumber(), "isFirstNumber() is not true");
        assertTrue(calculator.isPemdasActive(), "isPemdasActive() is not true");
    }

    @Test
    void methodResetCalculatorOperationsWithFalseResultsInAllOperatorsBeingFalse()
    {
        calculator.resetOperators(false);
        assertFalse(calculator.isFirstNumber(), "isFirstNumber() is not false");
        assertFalse(calculator.isPemdasActive(), "isPemdasActive() is not false");
    }

    @Test
    void methodResetCalculatorOperationsWithFalse()
    {
        calculator.getValues()[0] = FIVE;
        boolean resetResult = calculator.resetCalculatorOperations(false);
        assertTrue(resetResult, "Expected result to be true");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be disabled");
    }

    @Test
    void methResetCalculatorOperationsWithTrue()
    {
        calculator.getValues()[0] = FIVE;
        boolean resetResult = calculator.resetCalculatorOperations(true);
        assertFalse(resetResult, "Expected result to be false");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
    }

    @Test
    void testSetImageIconsWorksAsExpected()
    {
        assertNotNull(calculator.getCalculatorIcon(), "Expected calculator image");
        assertNotNull( calculator.getMacIcon(), "Expected mac icon");
        assertNotNull(calculator.getWindowsIcon(), "Expected windows icon");
        assertNull(calculator.getBlankIcon(), "Expected no icon");
    }

    @Test
    void switchingFromProgrammerToBasicSwitchesPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_BASIC.getValue());
        //calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.performViewMenuAction(actionEvent, VIEW_PROGRAMMER);
        //calculator.updateJPanel(new ProgrammerPanel());
        calculator.appendTextToPane("00000100");
        calculator.getValues()[0] = FOUR;
        assertEquals("00000100", calculator.getTextPaneValue(), "Expected textPane to show Binary representation");

        calculator.performViewMenuAction(actionEvent, VIEW_BASIC);
        assertEquals(FOUR, calculator.getTextPaneValue(), "Expected textPane to show Decimal representation");
        assertEquals(VIEW_BASIC.getValue(), calculator.getTitle(), "Expected name to be Basic");
        assertInstanceOf(BasicPanel.class, calculator.getCurrentPanel(), "Expected BasicPanel");
    }

    @Test
    void switchingFromBasicToProgrammerSwitchesPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_PROGRAMMER.getValue());
        calculator.getTextPane().setText(FOUR);
        calculator.getValues()[0] = FOUR;
        assertEquals(FOUR, calculator.getTextPaneValue(), "Expected textPane to show Decimal representation");

        calculator.performViewMenuAction(actionEvent, VIEW_PROGRAMMER);
        assertEquals(FOUR, calculator.getTextPaneValue(), "Expected textPane to show Decimal representation");
        assertEquals(VIEW_PROGRAMMER.getValue(), calculator.getTitle(), "Expected name to be Programmer");
        assertInstanceOf(ProgrammerPanel.class, calculator.getCurrentPanel(), "Expected ProgrammerPanel");
    }

    @Test
    void switchingFromBasicToDateSwitchesPanels()
    {
        //when(actionEvent.getActionCommand()).thenReturn(VIEW_DATE.getValue());
        assertEquals(VIEW_BASIC, calculator.getCalculatorView(), "Expected BASIC CalculatorView");
        calculator.performViewMenuAction(actionEvent, VIEW_DATE);
        assertEquals(VIEW_DATE, calculator.getCalculatorView(), "Expected DATE CalculatorView");
        assertEquals(VIEW_DATE.getValue(), calculator.getTitle(), "Expected name to be Date");
        assertInstanceOf(DatePanel.class, calculator.getCurrentPanel(), "Expected DatePanel");
    }

    @Test
    void switchingFromBasicToAngleConverterSwitchesPanels()
    {
        //when(actionEvent.getActionCommand()).thenReturn(ANGLE.getValue());
        assertEquals(VIEW_BASIC, calculator.getCalculatorView(), "Expected BASIC CalculatorView");

        calculator.performViewMenuAction(actionEvent, ANGLE);
        assertEquals(VIEW_CONVERTER, calculator.getCalculatorView(), "Expected CONVERTER CalculatorView");
        assertEquals(VIEW_CONVERTER.getValue(), calculator.getTitle(), "Expected name to be CONVERTER");
        assertInstanceOf(ConverterPanel.class, calculator.getCurrentPanel(), "Expected ConverterPanel");
    }

    @Test
    void switchingFromBasicToAreaConverterSwitchesPanels()
    {
        //when(actionEvent.getActionCommand()).thenReturn(AREA.getValue());
        assertEquals(VIEW_BASIC, calculator.getCalculatorView(), "Expected BASIC CalculatorView");

        calculator.performViewMenuAction(actionEvent, AREA);
        assertEquals(VIEW_CONVERTER, calculator.getCalculatorView(), "Expected CONVERTER CalculatorView");
        assertEquals(VIEW_CONVERTER.getValue(), calculator.getTitle(), "Expected name to be CONVERTER");
        assertInstanceOf(ConverterPanel.class, calculator.getCurrentPanel(), "Expected ConverterPanel");
    }

    @Test
    void switchingFromSomePanelToSamePanelDoesNotPerformViewMenuAction()
    {
        //when(actionEvent.getActionCommand()).thenReturn(VIEW_BASIC.getValue());
        BasicPanel panel = (BasicPanel) calculator.getCurrentPanel();
        calculator.getTextPane().setText(FOUR);
        calculator.getValues()[0]= FOUR;
        assertEquals(FOUR, calculator.getTextPaneValue(), "Expected textPane to show Decimal representation");

        calculator.performViewMenuAction(actionEvent, VIEW_BASIC);
        assertEquals(panel.getClass(), calculator.getCurrentPanel().getClass(), "Expected the same panel");
        assertEquals(FOUR, calculator.getTextPaneValue(), "Expected textPane to show Decimal representation");
    }

    @Test
    void switchingFromSomeConverterToSameConverterDoesNotPerformViewMenuAction()
    {
        //when(actionEvent.getActionCommand()).thenReturn(ANGLE.getValue());
        calculator.setCalculatorView(VIEW_CONVERTER);
        calculator.setConverterType(ANGLE);
        calculator.setCurrentPanel(calculator.getConverterPanel());
        calculator.setupPanel(null);
        calculator.performViewMenuAction(actionEvent, VIEW_CONVERTER);
        //calculator.updateJPanel(new ConverterPanel());
        ConverterPanel panel = (ConverterPanel) calculator.getCurrentPanel();
        assertEquals(ANGLE, calculator.getConverterType(), "Expected converterType to be ANGLE");

        calculator.performViewMenuAction(actionEvent, VIEW_CONVERTER);
        assertEquals(panel, calculator.getCurrentPanel(), "Expected the same panel");
        assertEquals(ANGLE, calculator.getConverterType(), "Expected converterType to be ANGLE");
    }

    @Test
    void switchingToMetalLookAndFeel()
    {
        when(actionEvent.getSource()).thenReturn(calculator.getStyleMenu());
        var lookMenuItems = calculator.getStyleMenu().getMenuComponents();
        for(Component menuItem : lookMenuItems)
        {
            if (METAL.equals(menuItem.getName()))
            {
                Arrays.stream(menuItem.getListeners(ActionListener.class))
                        .forEach(listener -> listener.actionPerformed(actionEvent));
            }
        }
        if (calculator.isOSMac())
            assertEquals(UIManager.getSystemLookAndFeelClassName(), "com.apple.laf.AquaLookAndFeel");
        else assertEquals(UIManager.getSystemLookAndFeelClassName(), "javax.swing.plaf.metal.MetalLookAndFeel");
    }

    @Test
    void switchingToSystemLookAndFeel()
    {
        var lookMenuItems = calculator.getStyleMenu().getMenuComponents();
        for (Component menuItem : lookMenuItems)
        {
            if (SYSTEM.equals(menuItem.getName()))
            {
                Arrays.stream(menuItem.getListeners(ActionListener.class))
                        .forEach(listener -> listener.actionPerformed(actionEvent));
            }
        }
        if (!calculator.isOSMac())
        {
            assertEquals(UIManager.getSystemLookAndFeelClassName(), "javax.swing.plaf.system.SystemLookAndFeel");
        }

    }

    @Test
    void switchingToWindowsLookAndFeel()
    {
        var lookMenuItems = calculator.getStyleMenu().getMenuComponents();
        for (Component menuItem : lookMenuItems)
        {
            if (WINDOWS.equals(menuItem.getName()))
            {
                Arrays.stream(menuItem.getListeners(ActionListener.class))
                        .forEach(listener -> listener.actionPerformed(actionEvent));
            }
        }
        if (!calculator.isOSMac())
        {
            assertEquals(UIManager.getSystemLookAndFeelClassName(), "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
    }

    @Test
    void switchingToMotifLookAndFeel()
    {
        when(actionEvent.getSource()).thenReturn(calculator.getStyleMenu());
        var lookMenuItems = calculator.getStyleMenu().getMenuComponents();
        for(Component menuItem : lookMenuItems)
        {
            if (MOTIF.equals(menuItem.getName()))
            {
                Arrays.stream(menuItem.getListeners(ActionListener.class))
                        .forEach(listener -> listener.actionPerformed(actionEvent));
            }
        }
        if (calculator.isOSMac())
            assertEquals(UIManager.getSystemLookAndFeelClassName(), "com.apple.laf.AquaLookAndFeel");
        else assertEquals(UIManager.getSystemLookAndFeelClassName(), "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    }

    @Test
    void switchingToGTKLookAndFeel()
    {
        var lookMenuItems = calculator.getStyleMenu().getMenuComponents();
        for (Component menuItem : lookMenuItems)
        {
            if (GTK.equals(menuItem.getName()))
            {
                Arrays.stream(menuItem.getListeners(ActionListener.class))
                        .forEach(listener -> listener.actionPerformed(actionEvent));
            }
        }
        if (!calculator.isOSMac())
        {
            assertEquals(UIManager.getSystemLookAndFeelClassName(), "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        }
    }

    @Test
    void testResetValues()
    {
        calculator.values[0] = ONE + FIVE;
        calculator.values[1] = TWO + SIX;
        calculator.values[2] = FIVE;

        assertEquals(15, Integer.parseInt(calculator.getValues()[0]), "Expected values[0] to be 15");
        assertEquals(26, Integer.parseInt(calculator.getValues()[1]), "Expected values[1] to be 26");
        assertEquals(5, Integer.parseInt(calculator.getValues()[2]), "Expected values[2] to be 5");

        calculator.resetValues();

        assertTrue(calculator.getValues()[0].isEmpty(), "Expected values[0] to be blank");
        assertTrue(calculator.getValues()[1].isEmpty(), "Expected values[1] to be blank");
        assertTrue(calculator.getValues()[2].isEmpty(), "Expected values[2] to be blank");
    }

    @Test
    void testClearNumberButtonActions()
    {
        calculator.getNumberButtons()
                .forEach(numberButton ->
                        assertSame(1, numberButton.getActionListeners().length, "Expecting only 1 action on " + numberButton.getName()));

        calculator.clearNumberButtonActions();

        calculator.getNumberButtons()
                .forEach(numberButton ->
                        assertSame(0, numberButton.getActionListeners().length, "Expecting no actions on " + numberButton.getName()));
    }

    @Test
    void testClearAllCommonButtons()
    {
        calculator.getCommonButtons()
                .forEach(otherButton ->
                        assertSame(1, otherButton.getActionListeners().length, "Expecting only 1 action on " + otherButton.getName()));

        calculator.clearAllCommonButtons();

        calculator.getCommonButtons()
                .forEach(otherButton ->
                        assertSame(0, otherButton.getActionListeners().length, "Expecting no actions on " + otherButton.getName()));
    }

    @Test
    void testResetOperatorIfClause()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL);
        calculator.performDecimalButtonAction(actionEvent);
        assertSame(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        assertFalse(calculator.isDotPressed(), "Expected dot button to be disabled");
        assertTrue(calculator.isFirstNumber(), "Expected to be on the firstNumber");

        calculator.resetCalculatorOperations(true);

        assertSame(1, calculator.getValuesPosition(), "Expected valuesPosition to be 1");
        assertTrue(calculator.isDotPressed(), "Expected dot button to be enabled");
        assertFalse(calculator.isFirstNumber(), "Expected to not be on the firstNumber");
    }

    @Test
    void testResetOperatorElseClause()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL);
        calculator.performDecimalButtonAction(actionEvent);
        assertSame(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected dot button to be disabled");
        assertTrue(calculator.isFirstNumber(), "Expected to be on the firstNumber");

        calculator.resetCalculatorOperations(false);

        assertSame(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        assertFalse(calculator.isDotPressed(), "Expected dot button to be disabled");
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected dot button to not be enabled");
        assertTrue(calculator.isFirstNumber(), "Expected to be on the firstNumber");
    }

    @Test
    void testIsMemoryValuesEmptyIsTrue()
    {
        assertTrue(calculator.isMemoryValuesEmpty());
    }

    @Test
    void testIsMemoryValuesEmptyIsFalseForBasicCalculator()
    {
        calculator.getTextPane().setText(FIVE);
        calculator.getMemoryValues()[0] = FIVE;
        calculator.setMemoryPosition(1);
        assertFalse(calculator.isMemoryValuesEmpty());
        confirm(calculator, LOGGER, "Test: isMemoryValuesEmpty -> False");
    }

    @Test
    void testIsMemoryValuesEmptyIsFalseForProgrammerCalculator()
    {
        calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.getTextPane().setText(FIVE);
        calculator.getMemoryValues()[0] = FIVE;
        calculator.setMemoryPosition(1);
        assertFalse(calculator.isMemoryValuesEmpty());
    }

    @Test
    void testInitialChecksIfClause()
    {
        calculator.getValues()[0] = NOT_A_NUMBER;
        calculator.getTextPane().setText(NOT_A_NUMBER);
        assertEquals(NOT_A_NUMBER, calculator.getTextPaneValue(), "Expected error message in textPane");

        calculator.performInitialChecks();

        assertFalse(calculator.getTextPaneValue().isEmpty(), "Expected textPane to show error");
        assertSame(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        assertTrue(calculator.isFirstNumber(), "Expected to be firstNumber");
        assertTrue(calculator.isDotPressed(), "Expected dot button to be enabled");
        assertTrue(calculator.getButtonDecimal().isEnabled(), "Expected dot button to be enabled");
        assertFalse(calculator.isNegativeNumber(), "Expecting isNumberNegative to be false");
    }

    @Test
    void testInitialChecksElseIf1Clause()
    {
        calculator.values[0] = ZERO;
        calculator.getTextPane().setText(calculator.addNewLines() + ZERO);

        assertEquals(ZERO, calculator.getTextPaneValue(), "Expected textPane to contain 0");
        assertEquals(VIEW_BASIC.getValue(), calculator.getCalculatorView().getValue(), "Expected BASIC CalculatorView");
        assertEquals(ZERO, calculator.getValues()[0], "Expected values[0] to be 0");

        calculator.performInitialChecks();

        assertTrue(calculator.getTextPaneValue().isBlank(), "Expected textPane to be blank");
        assertTrue(calculator.getValues()[0].isBlank(), "Expected values[0] to be blank");
        assertTrue(calculator.isFirstNumber(), "Expected to be on the firstNumber");
        assertTrue(calculator.isDotPressed(), "Expected dot button to be enabled");
        assertTrue(calculator.getButtonDecimal().isEnabled(), "Expected dot button to be enabled");
    }

    @Test
    void testInitialChecksElseIf2Clause()
    {
        calculator.values[0] = EMPTY;
        calculator.values[1] = ONE + FIVE; // 15
        calculator.setValuesPosition(1);

        assertTrue(calculator.getValues()[0].isBlank(), "Expected values[0] to be blank");
        assertSame(ONE + FIVE, calculator.getValues()[1], "Expected values[1] to be 15");
        assertSame(1, calculator.getValuesPosition(), "Expected valuesPosition to be 1");

        calculator.performInitialChecks();

        assertSame(ONE+FIVE, calculator.getValues()[0], "Expected values[0] to be 15");
        assertTrue(calculator.getValues()[1].isBlank(), "Expected values[1] to be blank");
    }

    @Test
    void testInitialChecksDoesNotEnterAnyClause()
    {
        calculator.getTextPane().setText(FIVE);
        calculator.performInitialChecks();
    }

    @Test
    void testTextPaneContainsBadTextTrue()
    {
        calculator.getTextPane().setText(NOT_A_NUMBER);
        assertTrue(calculator.textPaneContainsBadText(), "Expected textPane to contain error");
    }

    @Test
    void testTextPaneContainsBadTextFalse()
    {
        calculator.getTextPane().setText(FIVE);
        assertFalse(calculator.textPaneContainsBadText(), "Expected textPane to contain valid text");
    }

    @Test
    void testConfirmCalledForDatePanelWithDateOperation2()
    {
        calculator.setCalculatorView(VIEW_DATE);
        calculator.setDateOperation(ADD_SUBTRACT_DAYS);
        calculator.performViewMenuAction(actionEvent, VIEW_PROGRAMMER);
        //calculator.updateJPanel(new ProgrammerPanel());
        confirm(calculator, LOGGER,"Test: Confirm called");
    }

    @Test
    void testAddNewLineCharactersForBasicPanelAdds1NewLine()
    {
        String newLines = calculator.addNewLines();
        assertSame(1, newLines.split(EMPTY).length);
    }

    @Test
    void testAddNewLineCharactersForProgrammerPanelAdds3NewLines()
    {
        calculator.performViewMenuAction(actionEvent, VIEW_PROGRAMMER);
        //calculator.updateJPanel(new ProgrammerPanel());
        String newLines = calculator.addNewLines();
        assertSame(1, newLines.split(EMPTY).length);
    }

    @Test
    void testDynamicAddNewLineCharacters()
    {
        String newLines = calculator.addNewLines(10);
        assertSame(10, newLines.split(EMPTY).length);
    }

    // TODO: See if we can close the panel in the test
    @Test
    void testAboutCalculatorOpensAboutCalculatorPanel()
    {
        calculator.performAboutCalculatorAction(actionEvent);
    }

    @Test
    void testGetTheNumberToTheLeftOfTheDecimal()
    {
        assertEquals("123", calculator.getNumberOnLeftSideOfDecimal("123.456"));
    }

    @Test
    void testGetTheNumberToTheRightOfTheDecimal()
    {
        assertEquals("456", calculator.getNumberOnRightSideOfDecimal("123.456"));
    }

    @Test
    void testCopy()
    {
        calculator.getTextPane().setText(FIVE);
        assertTrue(calculator.getValues()[2].isBlank(), "Expected values[2] to be blank");

        calculator.performCopyAction(actionEvent);
        assertFalse(calculator.getValues()[2].isBlank(), "Expected values[2] to be 5");
        assertEquals(FIVE, calculator.getValues()[2], "Expected values[2] to be 5");
    }

    @Test
    void testPaste()
    {
        calculator.performPasteAction(actionEvent);
        assertTrue(calculator.getValues()[2].isBlank(), "Expected values[2] to be blank");

        calculator.values[2] = "10";
        calculator.getTextPane().setText(calculator.addNewLines() + FIVE);

        calculator.performPasteAction(actionEvent);
        assertEquals("10", calculator.getTextPaneValue(), "Expected textPane to be 10");
        assertEquals("10", calculator.getValues()[0], "Expected values[0] to be 10");

        calculator.setValuesPosition(1);
        calculator.performPasteAction(actionEvent);
        assertEquals("10", calculator.getTextPaneValue(), "Expected textPane to be 10");
        assertEquals("10", calculator.getValues()[0], "Expected values[1] to be 10");

    }

    @Test
    void testClearingZeroesAtTheEnd()
    {
        calculator.getValues()[0] = "15.0";
        assertEquals("15", calculator.clearZeroesAndDecimalAtEnd(calculator.getValues()[0]), "Expected values[0] to be 15");
    }

    @Test
    void testAddCourtesyCommasAddsNoCommas()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE);
        calculator.getValues()[0] = "2";
        calculator.performNumberButtonAction(actionEvent);
        assertFalse(calculator.getValues()[0].contains("_"), "Expected no commas");
        assertEquals("25", calculator.getValues()[0], "Expected values[0] to be 25");
    }

    @Test
    void testAddCourtesyCommasAdds1Comma4DigitsWholeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE);
        calculator.setCalculatorBase(BASE_DECIMAL);
        calculator.valuesPosition = 0;
        calculator.values[calculator.valuesPosition] = "999";
        calculator.getTextPane().setText(calculator.values[calculator.valuesPosition]);
        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.getTextPaneValue().contains(","), "Expected textPane to be 9,995");
        assertEquals("9995", calculator.values[calculator.valuesPosition], "Expected values[0] to be 9995");
    }

    @Test
    void testAddCourtesyCommasReturnsResultWithOneComma5DigitsWholeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE);
        calculator.valuesPosition = 0;
        calculator.values[calculator.valuesPosition] = "1234";
        calculator.getTextPane().setText(calculator.values[calculator.valuesPosition]);
        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.getTextPaneValue().contains(","), "Expected textPane to be 12,345");
        assertEquals("12345", calculator.getValues()[0], "Expected values[0] to be 12345");
    }

    @Test
    void testAddCourtesyCommasReturnsResultWithOneComma6DigitsWholeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SIX);
        calculator.valuesPosition = 0;
        calculator.values[calculator.valuesPosition] = "12345";
        calculator.getTextPane().setText(calculator.values[calculator.valuesPosition]);
        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.getTextPaneValue().contains(","), "Expected textPane to be 123,456");
        assertEquals("123456", calculator.getValues()[0], "Expected values[0] to be 123456");
    }

    @Test
    void testDeletingADigitAdjustsCourtesyCommas()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE);
        calculator.getValues()[0] = "12345";
        calculator.getTextPane().setText("12345");
        calculator.performDeleteButtonAction(actionEvent);
        assertTrue(calculator.getTextPaneValue().contains(","), "Expected textPane to be 1,234");
        assertEquals("1234", calculator.getValues()[0], "Expected values[0] to be 1234");
    }

    @ParameterizedTest()
    @CsvSource({
        // No Commas Added
        "1.25, 1.25",
        "123, 123",

        // Commas Added
        "1234,'1,234'",
        "-1234, '-1,234'",
        "1234.56, '1,234.56'",
        "12345.67, '12,345.67'",
        "123456.78, '123,456.78'",
        "1234567.89, '1,234,567.89'",
        "123456, '123,456'"
    })
    @DisplayName("Test addThousandsDelimiter")
    void testAddThousandsDelimiter(String input, String expecting)
    {
        when(actionEvent.getActionCommand()).thenReturn(String.valueOf(input.charAt(input.length()-1)));
        calculator.valuesPosition = 0;
        calculator.setIsNumberNegative(calculator.isNegativeNumber(input));
        calculator.getValues()[calculator.valuesPosition] = input.substring(0, input.length()-1);
        calculator.appendTextToPane(calculator.values[calculator.valuesPosition]);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(expecting, calculator.getTextPaneValue(), "Expected textPane to be " + expecting);
        assertEquals(input, calculator.getValueAtPosition(), "Expected values[0] to be " + expecting);
    }

    @Test
    void testValueAt0IsMinimumNumber()
    {
        calculator.values[0] = "-9999999"; //"0.0000001";
        assertTrue(calculator.isMinimumValue(), "Expected maximum number to be met");
    }

    @Test
    void testValueAt1IsMinimumNumber()
    {
        calculator.valuesPosition = 1;
        calculator.values[1] = "-9999999"; //"0.0000001";
        assertTrue(calculator.isMinimumValue(), "Expected maximum number to be met");
    }

    @Test
    void testValueIsMinimumNumber()
    {
        calculator.getValues()[0] = "-9999999"; //"0.0000001";
        assertTrue(calculator.isMinimumValue(calculator.getValues()[0]), "Expected maximum number to be met");
    }

    @Test
    void testValuesAt0IsMaximumNumber()
    {
        calculator.values[0] = "9999999";
        assertTrue(calculator.isMaximumValue(), "Expected maximum number to be met");
    }

    @Test
    void testValuesAt1IsMaximumNumber()
    {
        calculator.valuesPosition = 1;
        calculator.values[calculator.valuesPosition] = "9999999";
        assertTrue(calculator.isMaximumValue(), "Expected maximum number to be met");
    }

    @Test
    void testValueIsMaximumNumber()
    {
        calculator.values[0] = "9999999";
        assertTrue(calculator.isMaximumValue(calculator.getValueAtPosition(0)), "Expected maximum number to be met");
    }

    @Test
    void testMenuOptionsForViewMenu()
    {
         assertEquals(VIEW_BASIC.getValue(), calculator.getViewMenu().getMenuComponents()[0].getName(), "Expected Basic View Menu Item");
         assertEquals(VIEW_PROGRAMMER.getValue(), calculator.getViewMenu().getMenuComponents()[1].getName(), "Expected Programmer View Menu Item");
         assertEquals(VIEW_DATE.getValue(), calculator.getViewMenu().getMenuComponents()[2].getName(), "Expected Date View Menu Item");
         assertEquals(VIEW_CONVERTER.getValue(), calculator.getViewMenu().getMenuComponents()[4].getName(), "Expected Converter View Menu Item");
         assertEquals(ANGLE.getValue(), ((JMenu)calculator.getViewMenu().getMenuComponents()[4]).getMenuComponents()[0].getName(), "Expected Angle Converter Menu Item");
         assertEquals(AREA.getValue(), ((JMenu)calculator.getViewMenu().getMenuComponents()[4]).getMenuComponents()[1].getName(), "Expected Area Converter Menu Item");
    }

    @Test
    void testAboutCalculatorShowsWindowsInText()
    {
        doReturn(false).when(calculatorSpy).isOSMac();
        assertTrue(calculatorSpy.getAboutCalculatorString().contains(WINDOWS), "Expected About Calculator Text to have Windows");
        assertFalse(calculatorSpy.getAboutCalculatorString().contains(APPLE), "Expected About Calculator Text to not have Apple");
    }

    @Test
    void testGetLowestMemoryValuePosition()
    {
        calculator.getMemoryValues()[0] = ONE;
        calculator.getMemoryValues()[1] = TWO;
        calculator.getMemoryValues()[2] = THREE;
        calculator.getMemoryValues()[3] = FOUR;
        assertEquals(0, calculator.getLowestMemoryPosition(), "Expected lowest memoryPosition to be 0");
    }

    @Test
    void testShowMemoryValuesShowsNoMemoriesSaved()
    {
        when(actionEvent.getActionCommand()).thenReturn("Show Memory Values");
        calculator.performShowMemoriesAction(actionEvent);
        assertEquals("No Memories Stored",
                calculator.getHistoryPaneTextWithoutNewLineCharacters(),
                "Expected HistoryTextPane to say 'No Memories Stored'");
    }

    @Test
    void testClearBasicHistoryTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn("Clearing BasicHistoryTextPane");
        calculator.performClearHistoryTextPaneAction(actionEvent);
        assertEquals(EMPTY,
                calculator.getHistoryPaneTextWithoutNewLineCharacters(),
                "Expected HistoryTextPane to be blank");
    }

    @Test
    void testShowMemoryValuesInHistoryPane()
    {
        when(actionEvent.getActionCommand()).thenReturn("Show Memory Values");
        calculator.getMemoryValues()[0] = ONE;
        calculator.getMemoryValues()[1] = TWO;
        calculator.getMemoryValues()[2] = THREE;
        calculator.getMemoryValues()[3] = FOUR;
        calculator.setMemoryPosition(4);
        calculator.performShowMemoriesAction(actionEvent);
        assertEquals("Memories: [1], [2], [3], [4]",
                calculator.getHistoryPaneTextWithoutNewLineCharacters(),
                "Expected memories to be in HistoryTextPane");
    }

    @Test
    @DisplayName("Convert Byte Binary 3 to Decimal 3")
    void testConvertFromBaseToBase()
    {
        String binary = "00000011";
        calculator.setCalculatorBase(BASE_BINARY);
        calculator.setCalculatorByte(BYTE_BYTE);
        calculator.appendTextToPane(binary);
        calculator.getValues()[0] = THREE;
        String converted = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, calculator.getTextPaneValue());
        assertEquals(THREE, converted, "Expected 3");
    }

    @Test
    @DisplayName("Convert Word Binary 3 to Decimal 3")
    void test2ConvertFromBaseToBase()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_PROGRAMMER.getValue());
        calculator.performViewMenuAction(actionEvent, VIEW_PROGRAMMER);
        String eight0s = ZERO.repeat(8);
        String binary = eight0s+"00000011"; // still 3
        calculator.setCalculatorBase(BASE_BINARY);
        calculator.setCalculatorByte(BYTE_WORD);
        calculator.appendTextToPane(binary);
        calculator.getValues()[0] = THREE;
        String converted = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, binary);
        assertEquals(THREE, converted, "Expected 3");
    }

    @Test
    @DisplayName("Convert DWord Binary 3 to Decimal 3")
    void test3ConvertFromBaseToBase()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_PROGRAMMER.getValue());
        calculator.performViewMenuAction(actionEvent, VIEW_PROGRAMMER);
        String sixteen0s = ZERO.repeat(16);
        String eight0s = ZERO.repeat(8);
        String binary = sixteen0s+eight0s+"00000011"; // still 3
        calculator.setCalculatorBase(BASE_BINARY);
        calculator.setCalculatorByte(BYTE_DWORD);
        calculator.appendTextToPane(binary);
        calculator.getValues()[0] = THREE;
        String converted = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, binary);
        assertEquals(THREE, converted, "Expected 3");
    }

    @Test
    @DisplayName("Convert QWord Binary 3 to Decimal 3")
    void test4ConvertFromBaseToBase()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_PROGRAMMER.getValue());
        calculator.performViewMenuAction(actionEvent, VIEW_PROGRAMMER);
        String forty8zeroes = ZERO.repeat(48);
        String eight0s = ZERO.repeat(8);
        String binary = forty8zeroes+eight0s+"00000011"; // still 3
        calculator.setCalculatorBase(BASE_BINARY);
        calculator.setCalculatorByte(BYTE_QWORD);
        calculator.appendTextToPane(binary);
        calculator.getValues()[0] = THREE;
        String converted = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, binary);
        assertEquals(THREE, converted, "Expected 3");
    }

    @Test
    @DisplayName("Convert QWord Binary # to Decimal #")
    void test5ConvertFromBaseToBase()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_PROGRAMMER.getValue());
        calculator.performViewMenuAction(actionEvent, VIEW_PROGRAMMER);
        String forty7zeroes = ZERO.repeat(47);
        String eight0s = ZERO.repeat(8);
        String binary = ONE+forty7zeroes+eight0s+"00000011"; // still 3
        calculator.setCalculatorBase(BASE_BINARY);
        calculator.setCalculatorByte(BYTE_QWORD);
        calculator.appendTextToPane(binary);
        calculator.valuesPosition = 0;
        calculator.values[calculator.valuesPosition] = "9223372036854775811";
        String converted = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, binary);
        assertEquals("9223372036854775811", converted, "Expected big number");
    }

    @Test
    @DisplayName("Convert Largest Binary # to Decimal #")
    void test6ConvertFromBaseToBase()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_PROGRAMMER.getValue());
        calculator.performViewMenuAction(actionEvent, VIEW_PROGRAMMER);
        String binary = ONE.repeat(64);
        calculator.setCalculatorBase(BASE_BINARY);
        calculator.setCalculatorByte(BYTE_QWORD);
        calculator.appendTextToPane(binary);
        calculator.valuesPosition = 0;
        calculator.values[calculator.valuesPosition] = "18446744073709551615";
        String converted = calculator.convertFromBaseToBase(BASE_BINARY, BASE_DECIMAL, binary);
        assertEquals("18446744073709551615", converted, "Expected big number");
        assertEquals("18,446,744,073,709,551,615", calculator.addCommas(converted), "Expected big number");
    }
}

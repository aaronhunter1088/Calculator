package Calculators;

import Panels.BasicPanel;
import Panels.ConverterPanel;
import Panels.DatePanel;
import Panels.ProgrammerPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import static Types.CalculatorByte.*;
import static Types.CalculatorView.*;
import static Types.CalculatorBase.*;
import static Types.CalculatorConverterType.*;
import static Types.DateOperation.*;
import static Types.Texts.*;
import static Utilities.LoggingUtil.confirm;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * CalculatorTests
 * <p>
 * This class tests the Calculator class.
 *
 * @author Michael Ball
 * @version 4.0
 */
public class CalculatorTests
{
    private static Logger LOGGER;
    private Calculator calculator;

    @Mock
    ActionEvent actionEvent;
    @Spy
    Calculator calculatorSpy;

    @BeforeAll
    public static void beforeAll()
    {
        LOGGER = LogManager.getLogger(CalculatorTests.class.getSimpleName());
    }

    @BeforeEach
    public void beforeEach() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        LOGGER.info("Setting up beforeEach...");
        MockitoAnnotations.initMocks(this);
        calculator = new Calculator();
        calculator.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        calculatorSpy = spy(Calculator.class);
    }

    @AfterAll
    public static void afterAll()
    { LOGGER.info("Finished running {}", CalculatorTests.class.getSimpleName()); }


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

    /************* Basic Calculator Tests ******************/
    @Test
    public void createBasicCalculatorDefault() throws Exception
    {
        //calculator = new Calculator();
        assertTrue(calculator.isVisible(), "Cannot see basic calculator");
        assertEquals(VIEW_BASIC, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_BASIC);
    }

    @Test
    public void createBasicCalculatorEnforced() throws Exception
    {
        LOGGER.info("createBasicCalculator enforced...");
        //calculator = new Calculator(VIEW_BASIC);
        calculator.setCalculatorView(VIEW_BASIC);
        assertTrue(calculator.isVisible(), "Cannot see basic calculator");
        assertEquals(VIEW_BASIC, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_BASIC);
    }

    /************* Programmer Calculator Tests ******************/
    @Test
    public void createProgrammerCalculator() throws Exception
    {
        LOGGER.info("createProgrammerCalculator...");
        //calculator = new Calculator(VIEW_PROGRAMMER);
        calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.setCalculatorBase(BASE_DECIMAL);
        calculator.setIsMotif(true);
        assertTrue(calculator.isVisible(), "Cannot see programmer calculator");
        assertEquals(VIEW_PROGRAMMER, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_PROGRAMMER);
        assertSame(calculator.getCalculatorBase(), BASE_DECIMAL, "Base is not decimal");
    }

    @Test
    public void createProgrammerCalculatorInBinaryEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", BASE_BINARY);
        //calculator = new Calculator(BASE_BINARY);
        calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.setCalculatorBase(BASE_BINARY);
        assertTrue(calculator.isVisible(), "Cannot see programmer calculator");
        assertEquals(VIEW_PROGRAMMER, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_PROGRAMMER);
        assertSame(calculator.getCalculatorBase(), BASE_BINARY, "Base is not binary");
    }

    @Test
    public void createProgrammerCalculatorInOctalEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", BASE_OCTAL);
        //calculator = new Calculator(BASE_OCTAL);
        calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.setCalculatorBase(BASE_OCTAL);
        assertTrue(calculator.isVisible(), "Cannot see programmer calculator");
        assertSame(calculator.getCalculatorBase(), BASE_OCTAL, "Base is not octal");
    }

    @Test
    public void createProgrammerCalculatorInDecimalEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", BASE_DECIMAL);
        //calculator = new Calculator(BASE_DECIMAL);
        calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.setCalculatorBase(BASE_DECIMAL);
        assertTrue(calculator.isVisible(), "Cannot see programmer calculator");
        assertEquals(VIEW_PROGRAMMER, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_PROGRAMMER);
        assertSame(calculator.getCalculatorBase(), BASE_DECIMAL, "Base is not decimal");
    }

    @Test
    public void createProgrammerCalculatorInHexadecimalEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", BASE_HEXADECIMAL);
        //calculator = new Calculator(BASE_HEXADECIMAL);
        calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.setCalculatorBase(BASE_HEXADECIMAL);
        assertTrue(calculator.isVisible(), "Cannot see programmer calculator");
        assertSame(calculator.getCalculatorBase(), BASE_HEXADECIMAL, "Base is not binary");
    }

    /************* Scientific Calculator Tests ******************/
    @Test
    public void createScientificCalculator()
    {
        LOGGER.warn("createScientificCalculator...");
        LOGGER.warn("IMPLEMENT...");
    }

    /************* Date Calculator Tests ******************/
    @Test
    public void createDateCalculator() throws Exception
    {
        LOGGER.info("createDateCalculator...");
        //calculator = new Calculator(VIEW_DATE);
        calculator.setCalculatorView(VIEW_DATE);
        calculator.setDateOperation(DIFFERENCE_BETWEEN_DATES);
        calculator.setCurrentPanel(calculator.getDatePanel());
        calculator.setupPanel(null);
        assertTrue(calculator.isVisible(), "Cannot see date calculator");
        assertEquals(VIEW_DATE, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_DATE);
        assertSame(calculator.getDateOperation(), DIFFERENCE_BETWEEN_DATES, "Date operation is not " + DIFFERENCE_BETWEEN_DATES);
    }

    @Test
    public void createDateCalculatorWithDateOperation1Enforced() throws Exception
    {
        LOGGER.info("createDateCalculator with {} enforced...", DIFFERENCE_BETWEEN_DATES);
        //calculator = new Calculator(DIFFERENCE_BETWEEN_DATES);
        calculator.setCalculatorView(VIEW_DATE);
        calculator.setDateOperation(DIFFERENCE_BETWEEN_DATES);
        assertTrue(calculator.isVisible(), "Cannot see date calculator");
        assertEquals(VIEW_DATE, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_DATE);
        assertSame(calculator.getDateOperation(), DIFFERENCE_BETWEEN_DATES, "Date operation is not " + DIFFERENCE_BETWEEN_DATES);
    }

    @Test
    public void createDateCalculatorWithDateOperation2Enforced() throws Exception
    {
        LOGGER.info("createDateCalculator with {} enforced...", ADD_SUBTRACT_DAYS);
        //calculator = new Calculator(ADD_SUBTRACT_DAYS);
        calculator.setCalculatorView(VIEW_DATE);
        calculator.setDateOperation(ADD_SUBTRACT_DAYS);
        assertTrue(calculator.isVisible(), "Cannot see date calculator");
        assertEquals(VIEW_DATE, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_DATE);
        assertSame(calculator.getDateOperation(), ADD_SUBTRACT_DAYS, "Date operation is not " + ADD_SUBTRACT_DAYS);
    }

    /************* Converter Calculator Tests ******************/
    @Test
    public void createConverterCalculator() throws Exception
    {
        LOGGER.info("createConverterCalculator...");
        //calculator = new Calculator(VIEW_CONVERTER);
        calculator.setCalculatorView(VIEW_CONVERTER);
        calculator.setConverterType(ANGLE);
        calculator.setCurrentPanel(calculator.getConverterPanel());
        calculator.setupPanel(null);
        assertTrue(calculator.isVisible(), "Cannot see converter calculator");
        assertEquals(VIEW_CONVERTER, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_CONVERTER);
        assertSame(((ConverterPanel)calculator.getCurrentPanel()).getConverterType(), ANGLE, "Converter operation is not " + ANGLE);
    }

    @Test
    public void createConverterCalculatorWithAngleEnforced() throws Exception
    {
        LOGGER.info("createConverterCalculator with {} enforced...", ANGLE);
        //calculator = new Calculator(ANGLE);
        calculator.setCalculatorView(VIEW_CONVERTER);
        calculator.setConverterType(ANGLE);
        calculator.setCurrentPanel(calculator.getConverterPanel());
        calculator.setupPanel(null);
        assertTrue(calculator.isVisible(), "Cannot see converter calculator");
        assertEquals(VIEW_CONVERTER, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_CONVERTER);
        assertSame(((ConverterPanel)calculator.getCurrentPanel()).getConverterType(), ANGLE, "Converter operation is not " + ANGLE);
    }

    @Test
    public void createConverterCalculatorWithAreaEnforced() throws Exception
    {
        LOGGER.info("createConverterCalculator with {} enforced...", AREA);
        //calculator = new Calculator(AREA);
        calculator.setCalculatorView(VIEW_CONVERTER);
        calculator.setConverterType(AREA);
        calculator.setCurrentPanel(calculator.getConverterPanel());
        calculator.setupPanel(null);
        assertTrue(calculator.isVisible(), "Cannot see converter calculator");
        assertEquals(VIEW_CONVERTER, calculator.getCalculatorView(), "Expected CalculatorView to be " + VIEW_CONVERTER);
        assertSame(((ConverterPanel)calculator.getCurrentPanel()).getConverterType(), AREA, "Converter operation is not " + AREA);
    }

    /************* All Calculator Tests ******************/
    @Test
    public void testConvertingToPositive()
    {
        assertEquals("5.02", calculator.convertToPositive("-5.02"), "Number is not positive");
    }

    @Test
    public void testConvertingToNegative()
    {
        assertEquals("-5.02", calculator.convertToNegative("5.02"), "Number is not negative");
    }

    @Test
    public void testIsPositiveReturnsTrue()
    {
        assertTrue(calculator.isPositiveNumber(SIX), "IsPositive did not return true");
    }

    @Test
    public void testIsPositiveReturnsFalse()
    {
        assertFalse(calculator.isPositiveNumber("-6"), "IsPositive did not return false");
    }

    @Test
    public void testIsNegativeReturnsTrue()
    {
        assertTrue(calculator.isNegativeNumber("-6"), "IsNegative did not return true");
    }

    @Test
    public void testIsNegativeReturnsFalse()
    {
        assertFalse(calculator.isNegativeNumber(SIX), "IsNegative did not return false");
    }

    @Test
    public void testIsDecimalNumberReturnsTrue()
    {
        assertTrue(calculator.isDecimalNumber("5.02"), "Number should contain the decimal");
    }

    @Test
    public void testIsDecimalNumberReturnsFalse()
    {
        assertFalse(calculator.isDecimalNumber(FIVE), "Number should not contain the decimal");
    }

    @Test
    public void methodResetCalculatorOperationsWithTrueResultsInAllOperatorsBeingTrue()
    {
        calculator.resetOperators(true);
        assertTrue(calculator.isAdding(), "isAdding() is not true");
        assertTrue(calculator.isSubtracting(), "isSubtracting() is not true");
        assertTrue(calculator.isMultiplying(), "isMultiplying() is not true");
        assertTrue(calculator.isDividing(), "isDividing() is not true");
    }

    @Test
    public void methodResetCalculatorOperationsWithFalseResultsInAllOperatorsBeingFalse()
    {
        calculator.resetOperators(false);
        assertFalse(calculator.isAdding(), "isAdding() is not false");
        assertFalse(calculator.isSubtracting(), "isSubtracting() is not false");
        assertFalse(calculator.isMultiplying(), "isMultiplying() is not false");
        assertFalse(calculator.isDividing(), "isDividing() is not false");
    }

    @Test
    public void methodResetCalculatorOperationsWithFalse()
    {
        calculator.getValues()[0] = FIVE;
        boolean resetResult = calculator.resetCalculatorOperations(false);
        assertTrue(resetResult, "Expected result to be true");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be disabled");
    }

    @Test
    public void methResetCalculatorOperationsWithTrue()
    {
        calculator.getValues()[0] = FIVE;
        boolean resetResult = calculator.resetCalculatorOperations(true);
        assertFalse(resetResult, "Expected result to be false");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be enabled");
    }

    @Test
    public void testSetImageIconsWorksAsExpected()
    {
        assertNotNull(calculator.getCalculatorIcon(), "Expected calculator image");
        assertNotNull( calculator.getMacIcon(), "Expected mac icon");
        assertNotNull(calculator.getWindowsIcon(), "Expected windows icon");
        assertNull(calculator.getBlankIcon(), "Expected no icon");
    }

    @Test
    public void switchingFromProgrammerToBasicSwitchesPanels()
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
    public void switchingFromBasicToProgrammerSwitchesPanels()
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
    public void switchingFromBasicToDateSwitchesPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_DATE.getValue());
        assertEquals(VIEW_BASIC, calculator.getCalculatorView(), "Expected BASIC CalculatorView");
        calculator.performViewMenuAction(actionEvent, VIEW_DATE);
        assertEquals(VIEW_DATE, calculator.getCalculatorView(), "Expected DATE CalculatorView");
        assertEquals(VIEW_DATE.getValue(), calculator.getTitle(), "Expected name to be Date");
        assertInstanceOf(DatePanel.class, calculator.getCurrentPanel(), "Expected DatePanel");
    }

    @Test
    public void switchingFromBasicToAngleConverterSwitchesPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(ANGLE.getValue());
        assertEquals(VIEW_BASIC, calculator.getCalculatorView(), "Expected BASIC CalculatorView");

        calculator.performViewMenuAction(actionEvent, ANGLE);
        assertEquals(VIEW_CONVERTER, calculator.getCalculatorView(), "Expected CONVERTER CalculatorView");
        assertEquals(VIEW_CONVERTER.getValue(), calculator.getTitle(), "Expected name to be CONVERTER");
        assertInstanceOf(ConverterPanel.class, calculator.getCurrentPanel(), "Expected ConverterPanel");
    }

    @Test
    public void switchingFromBasicToAreaConverterSwitchesPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(AREA.getValue());
        assertEquals(VIEW_BASIC, calculator.getCalculatorView(), "Expected BASIC CalculatorView");

        calculator.performViewMenuAction(actionEvent, AREA);
        assertEquals(VIEW_CONVERTER, calculator.getCalculatorView(), "Expected CONVERTER CalculatorView");
        assertEquals(VIEW_CONVERTER.getValue(), calculator.getTitle(), "Expected name to be CONVERTER");
        assertInstanceOf(ConverterPanel.class, calculator.getCurrentPanel(), "Expected ConverterPanel");
    }

    @Test
    public void switchingFromSomePanelToSamePanelDoesNotPerformViewMenuAction()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_BASIC.getValue());
        BasicPanel panel = (BasicPanel) calculator.getCurrentPanel();
        calculator.getTextPane().setText(FOUR);
        calculator.getValues()[0]= FOUR;
        assertEquals(FOUR, calculator.getTextPaneValue(), "Expected textPane to show Decimal representation");

        calculator.performViewMenuAction(actionEvent, VIEW_BASIC);
        assertEquals(panel.getClass(), calculator.getCurrentPanel().getClass(), "Expected the same panel");
        assertEquals(FOUR, calculator.getTextPaneValue(), "Expected textPane to show Decimal representation");
    }

    @Test
    public void switchingFromSomeConverterToSameConverterDoesNotPerformViewMenuAction()
    {
        when(actionEvent.getActionCommand()).thenReturn(ANGLE.getValue());
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
    public void switchingToMetalLookAndFeel()
    {
        when(actionEvent.getSource()).thenReturn(calculator.getLookMenu());
        var lookMenuItems = calculator.getLookMenu().getMenuComponents();
        for(Component menuItem : lookMenuItems)
        {
            if (METAL.equals(menuItem.getName()))
            {
                Arrays.stream(menuItem.getListeners(ActionListener.class)).forEach(listener -> {
                    listener.actionPerformed(actionEvent);
                });
            }
        }
        if (calculator.isOSMac())
            assertEquals(UIManager.getSystemLookAndFeelClassName(), "com.apple.laf.AquaLookAndFeel");
        else assertEquals(UIManager.getSystemLookAndFeelClassName(), "javax.swing.plaf.metal.MetalLookAndFeel");
    }

    @Test
    public void switchingToSystemLookAndFeel()
    {
        var lookMenuItems = calculator.getLookMenu().getMenuComponents();
        for (Component menuItem : lookMenuItems)
        {
            if (SYSTEM.equals(menuItem.getName()))
            {
                Arrays.stream(menuItem.getListeners(ActionListener.class)).forEach(listener -> {
                    listener.actionPerformed(actionEvent);
                });
            }
        }
        if (!calculator.isOSMac())
        {
            assertEquals(UIManager.getSystemLookAndFeelClassName(), "javax.swing.plaf.system.SystemLookAndFeel");
        }

    }

    @Test
    public void switchingToWindowsLookAndFeel()
    {
        var lookMenuItems = calculator.getLookMenu().getMenuComponents();
        for (Component menuItem : lookMenuItems)
        {
            if (WINDOWS.equals(menuItem.getName()))
            {
                Arrays.stream(menuItem.getListeners(ActionListener.class)).forEach(listener -> {
                    listener.actionPerformed(actionEvent);
                });
            }
        }
        if (!calculator.isOSMac())
        {
            assertEquals(UIManager.getSystemLookAndFeelClassName(), "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
    }

    @Test
    public void switchingToMotifLookAndFeel()
    {
        when(actionEvent.getSource()).thenReturn(calculator.getLookMenu());
        var lookMenuItems = calculator.getLookMenu().getMenuComponents();
        for(Component menuItem : lookMenuItems)
        {
            if (MOTIF.equals(menuItem.getName()))
            {
                Arrays.stream(menuItem.getListeners(ActionListener.class)).forEach(listener -> {
                    listener.actionPerformed(actionEvent);
                });
            }
        }
        if (calculator.isOSMac())
            assertEquals(UIManager.getSystemLookAndFeelClassName(), "com.apple.laf.AquaLookAndFeel");
        else assertEquals(UIManager.getSystemLookAndFeelClassName(), "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    }

    @Test
    public void switchingToGTKLookAndFeel()
    {
        var lookMenuItems = calculator.getLookMenu().getMenuComponents();
        for (Component menuItem : lookMenuItems)
        {
            if (GTK.equals(menuItem.getName()))
            {
                Arrays.stream(menuItem.getListeners(ActionListener.class)).forEach(listener -> {
                    listener.actionPerformed(actionEvent);
                });
            }
        }
        if (!calculator.isOSMac())
        {
            assertEquals(UIManager.getSystemLookAndFeelClassName(), "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        }
    }

    @Test
    public void testResetValues()
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
    public void testClearNumberButtonActions()
    {
        calculator.getNumberButtons().forEach(numberButton -> {
            assertSame(1, numberButton.getActionListeners().length, "Expecting only 1 action on " + numberButton.getName());
        });

        calculator.clearNumberButtonActions();

        calculator.getNumberButtons().forEach(numberButton -> {
            assertSame(0, numberButton.getActionListeners().length, "Expecting no actions on " + numberButton.getName());
        });
    }

    @Test
    public void testClearAllCommonButtons()
    {
        calculator.getCommonButtons().forEach(otherButton -> {
            assertSame(1, otherButton.getActionListeners().length, "Expecting only 1 action on " + otherButton.getName());
        });

        calculator.clearAllCommonButtons();

        calculator.getCommonButtons().forEach(otherButton -> {
            assertSame(0, otherButton.getActionListeners().length, "Expecting no actions on " + otherButton.getName());
        });
    }

    @Test
    public void testDetermineIfAddingOperatorWasPushed()
    {
        assertFalse(calculator.isOperatorActive(), "Did not expect any operator to be pushed");
        calculator.setIsAdding(true);
        assertTrue(calculator.isOperatorActive(), "Expected any operator to be pushed");
        assertTrue(calculator.isAdding(), "Expected isAdding to be true");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
        assertFalse(calculator.isMultiplying(), "Expected isMultiplying to be false");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
    }

    @Test
    public void testDetermineIfSubtractingOperatorWasPushed()
    {
        assertFalse(calculator.isOperatorActive(), "Did not expect any operator to be pushed");
        calculator.setIsSubtracting(true);
        assertTrue(calculator.isOperatorActive(), "Expected any operator to be pushed");
        assertFalse(calculator.isAdding(), "Expected isAdding to be false");
        assertTrue(calculator.isSubtracting(), "Expected isSubtracting to be true");
        assertFalse(calculator.isMultiplying(), "Expected isMultiplying to be false");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
    }

    @Test
    public void testDetermineIfMultiplyingOperatorWasPushed()
    {
        assertFalse(calculator.isOperatorActive(), "Did not expect any operator to be pushed");
        calculator.setIsMultiplying(true);
        assertTrue(calculator.isOperatorActive(), "Expected any operator to be pushed");
        assertFalse(calculator.isAdding(), "Expected isAdding to be false");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
        assertTrue(calculator.isMultiplying(), "Expected isMultiplying to be true");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
    }

    @Test
    public void testDetermineIfDividingOperatorWasPushed()
    {
        assertFalse(calculator.isOperatorActive(), "Did not expect any operator to be pushed");
        calculator.setIsDividing(true);
        assertTrue(calculator.isOperatorActive(), "Expected any operator to be pushed");
        assertFalse(calculator.isAdding(), "Expected isAdding to be false");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
        assertFalse(calculator.isMultiplying(), "Expected isMultiplying to be false");
        assertTrue(calculator.isDividing(), "Expected isDividing to be true");
    }

    @Test
    public void testResetOperatorIfClause()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL);
        calculator.setIsAdding(true);
        calculator.performDecimalButtonAction(actionEvent);
        assertSame(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        assertFalse(calculator.isDotPressed(), "Expected dot button to be disabled");
        assertTrue(calculator.isFirstNumber(), "Expected to be on the firstNumber");

        calculator.resetCalculatorOperations(calculator.isAdding());

        assertSame(1, calculator.getValuesPosition(), "Expected valuesPosition to be 1");
        assertTrue(calculator.isDotPressed(), "Expected dot button to be enabled");
        assertFalse(calculator.isFirstNumber(), "Expected to not be on the firstNumber");
    }

    @Test
    public void testResetOperatorElseClause()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL);
        calculator.setIsAdding(false);
        calculator.performDecimalButtonAction(actionEvent);
        assertSame(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected dot button to be disabled");
        assertTrue(calculator.isFirstNumber(), "Expected to be on the firstNumber");

        calculator.resetCalculatorOperations(calculator.isAdding());

        assertSame(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        assertFalse(calculator.isDotPressed(), "Expected dot button to be disabled");
        assertFalse(calculator.getButtonDecimal().isEnabled(), "Expected dot button to not be enabled");
        assertTrue(calculator.isFirstNumber(), "Expected to be on the firstNumber");
    }

    @Test
    public void testIsMemoryValuesEmptyIsTrue() throws Exception
    {
        assertTrue(calculator.isMemoryValuesEmpty());
    }

    @Test
    public void testIsMemoryValuesEmptyIsFalseForBasicCalculator()
    {
        calculator.getTextPane().setText(FIVE);
        calculator.getMemoryValues()[0] = FIVE;
        calculator.setMemoryPosition(1);
        assertFalse(calculator.isMemoryValuesEmpty());
        confirm(calculator, LOGGER, "Test: isMemoryValuesEmpty -> False");
    }

    @Test
    public void testIsMemoryValuesEmptyIsFalseForProgrammerCalculator()
    {
        calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.getTextPane().setText(FIVE);
        calculator.getMemoryValues()[0] = FIVE;
        calculator.setMemoryPosition(1);
        assertFalse(calculator.isMemoryValuesEmpty());
    }

    @Test
    public void testInitialChecksIfClause()
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
        assertFalse(calculator.isNumberNegative(), "Expecting isNumberNegative to be false");
    }

    @Test
    public void testInitialChecksElseIf1Clause()
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
    public void testInitialChecksElseIf2Clause()
    {
        calculator.values[0] = BLANK;
        calculator.values[1] = ONE + FIVE; // 15
        calculator.setValuesPosition(1);

        assertTrue(calculator.getValues()[0].isBlank(), "Expected values[0] to be blank");
        assertSame(ONE + FIVE, calculator.getValues()[1], "Expected values[1] to be 15");
        assertSame(1, calculator.getValuesPosition(), "Expected valuesPosition to be 1");

        calculator.performInitialChecks();

        assertSame("15", calculator.getValues()[0], "Expected values[0] to be 15");
        assertTrue(calculator.getValues()[1].isBlank(), "Expected values[1] to be blank");
    }

    @Test
    public void testInitialChecksDoesNotEnterAnyClause()
    {
        calculator.getTextPane().setText(FIVE);
        calculator.performInitialChecks();
    }

    @Test
    public void testTextPaneContainsBadTextTrue()
    {
        calculator.getTextPane().setText(NOT_A_NUMBER);
        assertTrue(calculator.textPaneContainsBadText(), "Expected textPane to contain error");
    }

    @Test
    public void testTextPaneContainsBadTextFalse()
    {
        calculator.getTextPane().setText(FIVE);
        assertFalse(calculator.textPaneContainsBadText(), "Expected textPane to contain valid text");
    }

    @Test
    public void testConfirmCalledForDatePanelWithDateOperation2()
    {
        calculator.setCalculatorView(VIEW_DATE);
        calculator.setDateOperation(ADD_SUBTRACT_DAYS);
        calculator.performViewMenuAction(actionEvent, VIEW_PROGRAMMER);
        //calculator.updateJPanel(new ProgrammerPanel());
        confirm(calculator, LOGGER,"Test: Confirm called");
    }

    @Test
    public void testAddNewLineCharactersForBasicPanelAdds1NewLine()
    {
        String newLines = calculator.addNewLines();
        assertSame(1, newLines.split(BLANK).length);
    }

    @Test
    public void testAddNewLineCharactersForProgrammerPanelAdds3NewLines()
    {
        calculator.performViewMenuAction(actionEvent, VIEW_PROGRAMMER);
        //calculator.updateJPanel(new ProgrammerPanel());
        String newLines = calculator.addNewLines();
        assertSame(1, newLines.split(BLANK).length);
    }

    @Test
    public void testDynamicAddNewLineCharacters()
    {
        String newLines = calculator.addNewLines(10);
        assertSame(10, newLines.split(BLANK).length);
    }

    @Test
    public void testGetAboutCalculatorStringReturnsText() throws Exception
    {
        // Later
    }

    @Test
    public void testAboutCalculatorOpensAboutCalculatorPanel()
    {
        calculator.performAboutCalculatorAction(actionEvent);
    }

    @Test
    public void testGetTheNumberToTheLeftOfTheDecimal()
    {
        assertEquals("123", calculator.getNumberOnLeftSideOfDecimal("123.456"));
    }

    @Test
    public void testGetTheNumberToTheRightOfTheDecimal()
    {
        assertEquals("456", calculator.getNumberOnRightSideOfDecimal("123.456"));
    }

    @Test
    public void testCopy()
    {
        calculator.getTextPane().setText(FIVE);
        assertTrue(calculator.getValues()[2].isBlank(), "Expected values[2] to be blank");

        calculator.performCopyAction(actionEvent);
        assertFalse(calculator.getValues()[2].isBlank(), "Expected values[2] to be 5");
        assertEquals(FIVE, calculator.getValues()[2], "Expected values[2] to be 5");
    }

    @Test
    public void testPaste()
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
    public void testClearingZeroesAtTheEnd() throws Exception
    {
        calculator.getValues()[0] = "15.0";
        assertEquals("15", calculator.clearZeroesAndDecimalAtEnd(calculator.getValues()[0]), "Expected values[0] to be 15");
    }

    @Test
    public void testAddCourtesyCommasAddsNoCommas()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE);
        calculator.getValues()[0] = "2";
        calculator.performNumberButtonAction(actionEvent);
        assertFalse(calculator.getValues()[0].contains("_"), "Expected no commas");
        assertEquals("25", calculator.getValues()[0], "Expected values[0] to be 25");
    }

    @Test
    public void testAddCourtesyCommasAdds1Comma4DigitsWholeNumber()
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
    public void testAddCourtesyCommasReturnsResultWithOneComma5DigitsWholeNumber()
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
    public void testAddCourtesyCommasReturnsResultWithOneComma6DigitsWholeNumber()
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
    public void testDeletingADigitAdjustsCourtesyCommas()
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
    public void testAddThousandsDelimiter(String input, String expecting)
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

//    @Test
//    public void testCheckValueLength()
//    {
//        calculator.values[0] = "9999998";
//        calculator.performNumberButtonAction();
//        // TODO: Not able to assert anything yet
//    }

    @Test
    public void testValueAt0IsMinimumNumber()
    {
        calculator.values[0] = "-9999999"; //"0.0000001";
        assertTrue(calculator.isMinimumValue(), "Expected maximum number to be met");
    }

    @Test
    public void testValueAt1IsMinimumNumber()
    {
        calculator.valuesPosition = 1;
        calculator.values[1] = "-9999999"; //"0.0000001";
        assertTrue(calculator.isMinimumValue(), "Expected maximum number to be met");
    }

    @Test
    public void testValueIsMinimumNumber()
    {
        calculator.getValues()[0] = "-9999999"; //"0.0000001";
        assertTrue(calculator.isMinimumValue(calculator.getValues()[0]), "Expected maximum number to be met");
    }

    @Test
    public void testValuesAt0IsMaximumNumber()
    {
        calculator.values[0] = "9999999";
        assertTrue(calculator.isMaximumValue(), "Expected maximum number to be met");
    }

    @Test
    public void testValuesAt1IsMaximumNumber()
    {
        calculator.valuesPosition = 1;
        calculator.values[calculator.valuesPosition] = "9999999";
        assertTrue(calculator.isMaximumValue(), "Expected maximum number to be met");
    }

    @Test
    public void testValueIsMaximumNumber()
    {
        calculator.values[0] = "9999999";
        assertTrue(calculator.isMaximumValue(calculator.getValues()[0]), "Expected maximum number to be met");
    }

    @Test
    public void testMenuOptionsForWindowsAdded()
    {
         doAnswer((InvocationOnMock invocationOnMock) ->
                 calculator.getRootPane()
         ).when(calculatorSpy).getRootPane();
         doReturn(false).when(calculatorSpy).isOSMac();
         calculatorSpy.configureMenuBar();
         assertEquals(5, calculator.getViewMenu().getMenuComponents().length, "Expected View menu to have 3 options");
    }

    @Test
    public void testAboutCalculatorShowsWindowsInText()
    {
        doReturn(false).when(calculatorSpy).isOSMac();
        assertTrue(calculatorSpy.getAboutCalculatorString().contains(WINDOWS), "Expected About Calculator Text to have Windows");
        assertFalse(calculatorSpy.getAboutCalculatorString().contains(APPLE), "Expected About Calculator Text to not have Apple");
    }

    @Test
    public void testGetLowestMemoryValuePosition()
    {
        calculator.getMemoryValues()[0] = ONE;
        calculator.getMemoryValues()[1] = TWO;
        calculator.getMemoryValues()[2] = THREE;
        calculator.getMemoryValues()[3] = FOUR;
        assertEquals(0, calculator.getLowestMemoryPosition(), "Expected lowest memoryPosition to be 0");
    }

    @Test
    public void testShowMemoryValuesShowsNoMemoriesSaved()
    {
        when(actionEvent.getActionCommand()).thenReturn("Show Memory Values");
        calculator.performShowMemoriesAction(actionEvent);
        assertEquals("No Memories Stored",
                calculator.getBasicHistoryPaneTextWithoutNewLineCharacters(),
                "Expected basicHistoryTextPane to say No Memories Stored");
    }

    @Test
    public void testClearBasicHistoryTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn("Clearing BasicHistoryTextPane");
        calculator.performClearHistoryTextPaneAction(actionEvent);
        assertEquals(BLANK,
                calculator.getBasicHistoryPaneTextWithoutNewLineCharacters(),
                "Expected basicHistoryTextPane to be blank");
    }

    @Test
    public void testShowMemoryValuesInHistoryPane()
    {
        when(actionEvent.getActionCommand()).thenReturn("Show Memory Values");
        calculator.getMemoryValues()[0] = ONE;
        calculator.getMemoryValues()[1] = TWO;
        calculator.getMemoryValues()[2] = THREE;
        calculator.getMemoryValues()[3] = FOUR;
        calculator.setMemoryPosition(4);
        calculator.performShowMemoriesAction(actionEvent);
        assertEquals("Memories: [1], [2], [3], [4]",
                calculator.getBasicHistoryPaneTextWithoutNewLineCharacters(),
                "Expected memories to be in basicHistoryTextPane");
    }

    @Test
    @DisplayName("Convert Byte Binary 3 to Decimal 3")
    public void testConvertFromBaseToBase()
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
    public void test2ConvertFromBaseToBase() throws InterruptedException
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
    public void test3ConvertFromBaseToBase() throws InterruptedException
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
    public void test4ConvertFromBaseToBase() throws InterruptedException
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
    public void test5ConvertFromBaseToBase() throws InterruptedException
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
    public void test6ConvertFromBaseToBase() throws InterruptedException
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

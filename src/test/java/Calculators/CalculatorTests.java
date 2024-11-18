package Calculators;

import Panels.BasicPanel;
import Panels.ConverterPanel;
import Panels.DatePanel;
import Panels.ProgrammerPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
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

import static Types.CalculatorView.*;
import static Types.CalculatorBase.*;
import static Types.ConverterType.*;
import static Types.DateOperation.*;
import static Types.Texts.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CalculatorTests
{
    static { System.setProperty("appName", "CalculatorTests"); }
    private static Logger LOGGER;
    private static Calculator calculator;

    @Mock
    ActionEvent actionEvent;
    @Spy
    static Calculator calculatorSpy;

    @BeforeAll
    public static void beforeAll() throws Exception
    {
        LOGGER = LogManager.getLogger(CalculatorTests.class.getSimpleName());
    }

    @AfterAll
    public static void afterAll()
    { LOGGER.info("Finished running {}", CalculatorTests.class.getSimpleName()); }

    @BeforeEach
    public void beforeEach() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        LOGGER.info("setting up each before...");
        MockitoAnnotations.initMocks(this);
        calculator = new Calculator();
        calculator.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        calculatorSpy = spy(calculator);
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
        calculator.setCurrentPanel(calculator.determinePanel());
        calculator.setupPanel();
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
        calculator.setCurrentPanel(calculator.determinePanel());
        calculator.setupPanel();
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
        calculator.setCurrentPanel(calculator.determinePanel());
        calculator.setupPanel();
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
        calculator.setCurrentPanel(calculator.determinePanel());
        calculator.setupPanel();
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
        assertTrue(calculator.isPositiveNumber(SIX.getValue()), "IsPositive did not return true");
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
        assertFalse(calculator.isNegativeNumber(SIX.getValue()), "IsNegative did not return false");
    }

    @Test
    public void testIsDecimalReturnsTrue()
    {
        assertTrue(calculator.isDecimal("5.02"), "Number should contain the decimal");
    }

    @Test
    public void testIsDecimalReturnsFalse()
    {
        assertFalse(calculator.isDecimal(FIVE.getValue()), "Number should not contain the decimal");
    }

    @Test
    public void methodResetCalculatorOperationsWithTrueResultsInAllOperatorsBeingTrue()
    {
        calculator.resetBasicOperators(true);
        assertTrue(calculator.isAdding(), "isAdding() is not true");
        assertTrue(calculator.isSubtracting(), "isSubtracting() is not true");
        assertTrue(calculator.isMultiplying(), "isMultiplying() is not true");
        assertTrue(calculator.isDividing(), "isDividing() is not true");
    }

    @Test
    public void methodResetCalculatorOperationsWithFalseResultsInAllOperatorsBeingFalse()
    {
        calculator.resetBasicOperators(false);
        assertFalse(calculator.isAdding(), "isAdding() is not false");
        assertFalse(calculator.isSubtracting(), "isSubtracting() is not false");
        assertFalse(calculator.isMultiplying(), "isMultiplying() is not false");
        assertFalse(calculator.isDividing(), "isDividing() is not false");
    }

    @Test
    public void methodResetCalculatorOperationsWithFalse()
    {
        calculator.getValues()[0] = FIVE.getValue();
        boolean resetResult = calculator.resetCalculatorOperations(false);
        assertTrue(resetResult, "Expected result to be true");
        assertTrue(calculator.isDotPressed(), "Expected decimal to be disabled");
    }

    @Test
    public void methResetCalculatorOperationsWithTrue()
    {
        calculator.getValues()[0] = FIVE.getValue();
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
        calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.updateJPanel(new ProgrammerPanel());
        calculator.appendTextToPane("00000100");
        calculator.getValues()[0] = FOUR.getValue();
        assertEquals("00000100", calculator.getTextPaneValue(), "Expected textPane to show Binary representation");

        calculator.switchPanels(actionEvent, VIEW_BASIC);
        assertEquals(FOUR.getValue(), calculator.getTextPaneValue(), "Expected textPane to show Decimal representation");
        assertEquals(VIEW_BASIC.getValue(), calculator.getTitle(), "Expected name to be Basic");
        assertInstanceOf(BasicPanel.class, calculator.getCurrentPanel(), "Expected BasicPanel");
    }

    @Test
    public void switchingFromBasicToProgrammerSwitchesPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_PROGRAMMER.getValue());
        calculator.getTextPane().setText(FOUR.getValue());
        calculator.getValues()[0] = FOUR.getValue();
        assertEquals(FOUR.getValue(), calculator.getTextPaneValue(), "Expected textPane to show Decimal representation");

        calculator.switchPanels(actionEvent, VIEW_PROGRAMMER);
        assertEquals(FOUR.getValue(), calculator.getTextPaneValueForProgrammerPanel(), "Expected textPane to show Decimal representation");
        assertEquals(VIEW_PROGRAMMER.getValue(), calculator.getTitle(), "Expected name to be Programmer");
        assertInstanceOf(ProgrammerPanel.class, calculator.getCurrentPanel(), "Expected ProgrammerPanel");
    }

    @Test
    public void switchingFromBasicToDateSwitchesPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_DATE.getValue());
        assertEquals(VIEW_BASIC, calculator.getCalculatorView(), "Expected BASIC CalculatorView");
        calculator.switchPanels(actionEvent, VIEW_DATE);
        assertEquals(VIEW_DATE, calculator.getCalculatorView(), "Expected DATE CalculatorView");
        assertEquals(VIEW_DATE.getValue(), calculator.getTitle(), "Expected name to be Date");
        assertInstanceOf(DatePanel.class, calculator.getCurrentPanel(), "Expected DatePanel");
    }

    @Test
    public void switchingFromBasicToAngleConverterSwitchesPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(ANGLE.getValue());
        assertEquals(VIEW_BASIC, calculator.getCalculatorView(), "Expected BASIC CalculatorView");

        calculator.switchPanels(actionEvent, ANGLE);
        assertEquals(VIEW_CONVERTER, calculator.getCalculatorView(), "Expected CONVERTER CalculatorView");
        assertEquals(VIEW_CONVERTER.getValue(), calculator.getTitle(), "Expected name to be CONVERTER");
        assertInstanceOf(ConverterPanel.class, calculator.getCurrentPanel(), "Expected ConverterPanel");
    }

    @Test
    public void switchingFromBasicToAreaConverterSwitchesPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(AREA.getValue());
        assertEquals(VIEW_BASIC, calculator.getCalculatorView(), "Expected BASIC CalculatorView");

        calculator.switchPanels(actionEvent, AREA);
        assertEquals(VIEW_CONVERTER, calculator.getCalculatorView(), "Expected CONVERTER CalculatorView");
        assertEquals(VIEW_CONVERTER.getValue(), calculator.getTitle(), "Expected name to be CONVERTER");
        assertInstanceOf(ConverterPanel.class, calculator.getCurrentPanel(), "Expected ConverterPanel");
    }

    @Test
    public void switchingFromSomePanelToSamePanelDoesNotSwitchPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_BASIC.getValue());
        BasicPanel panel = (BasicPanel) calculator.getCurrentPanel();
        calculator.getTextPane().setText(FOUR.getValue());
        calculator.getValues()[0]= FOUR.getValue();
        assertEquals(FOUR.getValue(), calculator.getTextPaneValue(), "Expected textPane to show Decimal representation");

        calculator.switchPanels(actionEvent, VIEW_BASIC);
        assertEquals(panel.getClass(), calculator.getCurrentPanel().getClass(), "Expected the same panel");
        assertEquals(FOUR.getValue(), calculator.getTextPaneValue(), "Expected textPane to show Decimal representation");
    }

    @Test
    public void switchingFromSomeConverterToSameConverterDoesNotSwitchPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(ANGLE.getValue());
        calculator.setCalculatorView(VIEW_CONVERTER);
        calculator.setConverterType(ANGLE);
        calculator.setCurrentPanel(calculator.determinePanel());
        calculator.setupPanel();
        calculator.updateJPanel(new ConverterPanel());
        ConverterPanel panel = (ConverterPanel) calculator.getCurrentPanel();
        assertEquals(ANGLE, calculator.getConverterType(), "Expected converterType to be ANGLE");

        calculator.switchPanels(actionEvent, VIEW_CONVERTER);
        assertEquals(panel, calculator.getCurrentPanel(), "Expected the same panel");
        assertEquals(ANGLE, calculator.getConverterType(), "Expected converterType to be ANGLE");
    }

    @Test
    public void switchingToMetalLookAndFeel()
    {
        var lookMenuItems = calculator.getLookMenu().getMenuComponents();
        for(Component menuItem : lookMenuItems)
        {
            if (METAL.getValue().equals(menuItem.getName()))
            {
                Arrays.stream(menuItem.getListeners(ActionListener.class)).forEach(listener -> {
                    listener.actionPerformed(actionEvent);
                });
            }
        }
        if (calculator.isMacOperatingSystem())
            assertEquals(UIManager.getSystemLookAndFeelClassName(), "com.apple.laf.AquaLookAndFeel");
        else assertEquals(UIManager.getSystemLookAndFeelClassName(), "javax.swing.plaf.metal.MetalLookAndFeel");
    }

    @Test
    public void switchingToSystemLookAndFeel()
    {
        var lookMenuItems = calculator.getLookMenu().getMenuComponents();
        for (Component menuItem : lookMenuItems)
        {
            if (SYSTEM.getValue().equals(menuItem.getName()))
            {
                Arrays.stream(menuItem.getListeners(ActionListener.class)).forEach(listener -> {
                    listener.actionPerformed(actionEvent);
                });
            }
        }
        if (!calculator.isMacOperatingSystem())
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
            if (WINDOWS.getValue().equals(menuItem.getName()))
            {
                Arrays.stream(menuItem.getListeners(ActionListener.class)).forEach(listener -> {
                    listener.actionPerformed(actionEvent);
                });
            }
        }
        if (!calculator.isMacOperatingSystem())
        {
            assertEquals(UIManager.getSystemLookAndFeelClassName(), "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
    }

    @Test
    public void switchingToMotifLookAndFeel()
    {
        var lookMenuItems = calculator.getLookMenu().getMenuComponents();
        for(Component menuItem : lookMenuItems)
        {
            if (MOTIF.getValue().equals(menuItem.getName()))
            {
                Arrays.stream(menuItem.getListeners(ActionListener.class)).forEach(listener -> {
                    listener.actionPerformed(actionEvent);
                });
            }
        }
        if (calculator.isMacOperatingSystem())
            assertEquals(UIManager.getSystemLookAndFeelClassName(), "com.apple.laf.AquaLookAndFeel");
        else assertEquals(UIManager.getSystemLookAndFeelClassName(), "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    }

    @Test
    public void switchingToGTKLookAndFeel()
    {
        var lookMenuItems = calculator.getLookMenu().getMenuComponents();
        for (Component menuItem : lookMenuItems)
        {
            if (GTK.getValue().equals(menuItem.getName()))
            {
                Arrays.stream(menuItem.getListeners(ActionListener.class)).forEach(listener -> {
                    listener.actionPerformed(actionEvent);
                });
            }
        }
        if (!calculator.isMacOperatingSystem())
        {
            assertEquals(UIManager.getSystemLookAndFeelClassName(), "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        }
    }

    @Test
    public void testResetValues()
    {
        calculator.values[0] = "15";
        calculator.values[1] = "26";
        calculator.values[2] = FIVE.getValue();

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
        calculator.getAllNumberButtons().forEach(numberButton -> {
            assertSame(1, numberButton.getActionListeners().length, "Expecting only 1 action on " + numberButton.getName());
        });

        calculator.clearNumberButtonActions();

        calculator.getAllNumberButtons().forEach(numberButton -> {
            assertSame(0, numberButton.getActionListeners().length, "Expecting no actions on " + numberButton.getName());
        });
    }

    @Test
    public void testClearAllOtherBasicCalculatorButtons()
    {
        calculator.getAllBasicPanelButtons().forEach(otherButton -> {
            assertSame(1, otherButton.getActionListeners().length, "Expecting only 1 action on " + otherButton.getName());
        });

        calculator.clearAllOtherBasicCalculatorButtons();

        calculator.getAllBasicPanelButtons().forEach(otherButton -> {
            assertSame(0, otherButton.getActionListeners().length, "Expecting no actions on " + otherButton.getName());
        });
    }

    @Test
    public void testDetermineIfAddingOperatorWasPushed()
    {
        assertFalse(calculator.determineIfAnyBasicOperatorWasPushed(), "Did not expect any operator to be pushed");
        calculator.setIsAdding(true);
        assertTrue(calculator.determineIfAnyBasicOperatorWasPushed(), "Expected any operator to be pushed");
        assertTrue(calculator.isAdding(), "Expected isAdding to be true");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
        assertFalse(calculator.isMultiplying(), "Expected isMultiplying to be false");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
    }

    @Test
    public void testDetermineIfSubtractingOperatorWasPushed()
    {
        assertFalse(calculator.determineIfAnyBasicOperatorWasPushed(), "Did not expect any operator to be pushed");
        calculator.setIsSubtracting(true);
        assertTrue(calculator.determineIfAnyBasicOperatorWasPushed(), "Expected any operator to be pushed");
        assertFalse(calculator.isAdding(), "Expected isAdding to be false");
        assertTrue(calculator.isSubtracting(), "Expected isSubtracting to be true");
        assertFalse(calculator.isMultiplying(), "Expected isMultiplying to be false");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
    }

    @Test
    public void testDetermineIfMultiplyingOperatorWasPushed()
    {
        assertFalse(calculator.determineIfAnyBasicOperatorWasPushed(), "Did not expect any operator to be pushed");
        calculator.setIsMultiplying(true);
        assertTrue(calculator.determineIfAnyBasicOperatorWasPushed(), "Expected any operator to be pushed");
        assertFalse(calculator.isAdding(), "Expected isAdding to be false");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
        assertTrue(calculator.isMultiplying(), "Expected isMultiplying to be true");
        assertFalse(calculator.isDividing(), "Expected isDividing to be false");
    }

    @Test
    public void testDetermineIfDividingOperatorWasPushed()
    {
        assertFalse(calculator.determineIfAnyBasicOperatorWasPushed(), "Did not expect any operator to be pushed");
        calculator.setIsDividing(true);
        assertTrue(calculator.determineIfAnyBasicOperatorWasPushed(), "Expected any operator to be pushed");
        assertFalse(calculator.isAdding(), "Expected isAdding to be false");
        assertFalse(calculator.isSubtracting(), "Expected isSubtracting to be false");
        assertFalse(calculator.isMultiplying(), "Expected isMultiplying to be false");
        assertTrue(calculator.isDividing(), "Expected isDividing to be true");
    }

    @Test
    public void testResetOperatorIfClause()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL.getValue());
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
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL.getValue());
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
        calculator.getTextPane().setText(FIVE.getValue());
        calculator.getMemoryValues()[0] = FIVE.getValue();
        calculator.setMemoryPosition(1);
        assertFalse(calculator.isMemoryValuesEmpty());
        calculator.confirm("Test: isMemoryValuesEmpty -> False");
    }

    @Test
    public void testIsMemoryValuesEmptyIsFalseForProgrammerCalculator()
    {
        calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.getTextPane().setText(FIVE.getValue());
        calculator.getMemoryValues()[0] = FIVE.getValue();
        calculator.setMemoryPosition(1);
        assertFalse(calculator.isMemoryValuesEmpty());
    }

    @Test
    public void testInitialChecksIfClause()
    {
        calculator.getValues()[0] = NOT_A_NUMBER.getValue();
        calculator.getTextPane().setText(NOT_A_NUMBER.getValue());
        assertEquals(NOT_A_NUMBER.getValue(), calculator.getTextPaneValue(), "Expected error message in textPane");

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
        calculator.values[0] = ZERO.getValue();
        calculator.getTextPane().setText(calculator.addNewLines() + ZERO.getValue());

        assertEquals(ZERO.getValue(), calculator.getTextPaneWithoutAnyOperator(), "Expected textPane to contain 0");
        assertEquals(VIEW_BASIC.getValue(), calculator.getCalculatorView().getValue(), "Expected BASIC CalculatorView");
        assertEquals(ZERO.getValue(), calculator.getValues()[0], "Expected values[0] to be 0");

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
        calculator.values[0] = BLANK.getValue();
        calculator.values[1] = "15";
        calculator.setValuesPosition(1);

        assertTrue(calculator.getValues()[0].isBlank(), "Expected values[0] to be blank");
        assertSame("15", calculator.getValues()[1], "Expected values[1] to be 15");
        assertSame(1, calculator.getValuesPosition(), "Expected valuesPosition to be 1");

        calculator.performInitialChecks();

        assertSame("15", calculator.getValues()[0], "Expected values[0] to be 15");
        assertTrue(calculator.getValues()[1].isBlank(), "Expected values[1] to be blank");
        assertSame(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
    }

    @Test
    public void testInitialChecksDoesNotEnterAnyClause()
    {
        calculator.getTextPane().setText(FIVE.getValue());
        calculator.performInitialChecks();
    }

    @Test
    public void testTextPaneContainsBadTextTrue()
    {
        calculator.getTextPane().setText(NOT_A_NUMBER.getValue());
        assertTrue(calculator.textPaneContainsBadText(), "Expected textPane to contain error");
    }

    @Test
    public void testTextPaneContainsBadTextFalse()
    {
        calculator.getTextPane().setText(FIVE.getValue());
        assertFalse(calculator.textPaneContainsBadText(), "Expected textPane to contain valid text");
    }

    @Test
    public void testConfirmCalledForDatePanelWithDateOperation2()
    {
        calculator.setCalculatorView(VIEW_DATE);
        calculator.setDateOperation(ADD_SUBTRACT_DAYS);
        calculator.updateJPanel(new ProgrammerPanel());
        calculator.confirm("Test: Confirm called");
    }

    @Test
    public void testAddNewLineCharactersForBasicPanelAdds1NewLine()
    {
        String newLines = calculator.addNewLines();
        assertSame(1, newLines.split(BLANK.getValue()).length);
    }

    @Test
    public void testAddNewLineCharactersForProgrammerPanelAdds3NewLines()
    {
        calculator.updateJPanel(new ProgrammerPanel());
        String newLines = calculator.addNewLines();
        assertSame(1, newLines.split(BLANK.getValue()).length);
    }

    @Test
    public void testDynamicAddNewLineCharacters()
    {
        String newLines = calculator.addNewLines(10);
        assertSame(10, newLines.split(BLANK.getValue()).length);
    }

    @Test
    public void testGetAboutCalculatorStringReturnsText() throws Exception
    {
        String helpMe = new Calculator().getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Basic Calculator", helpMe);

        helpMe = new Calculator(VIEW_PROGRAMMER).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Programmer Calculator Type:"+ BASE_BINARY.getValue(), helpMe);

        helpMe = new Calculator(BASE_OCTAL).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Programmer Calculator Type:"+ BASE_OCTAL.getValue(), helpMe);

        helpMe = new Calculator(BASE_DECIMAL).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Programmer Calculator Type:"+ BASE_DECIMAL.getValue(), helpMe);

        helpMe = new Calculator(BASE_HEXADECIMAL).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Programmer Calculator Type:"+ BASE_HEXADECIMAL.getValue(), helpMe);

        helpMe = new Calculator(DIFFERENCE_BETWEEN_DATES).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Date Calculator Type:"+DIFFERENCE_BETWEEN_DATES, helpMe);

        helpMe = new Calculator(ADD_SUBTRACT_DAYS).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Date Calculator Type:"+ADD_SUBTRACT_DAYS, helpMe);

        helpMe = new Calculator(ANGLE).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Converter Calculator Type:"+ANGLE, helpMe);

        helpMe = new Calculator(AREA).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Converter Calculator Type:"+AREA.getValue(), helpMe);
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
        calculator.getTextPane().setText(FIVE.getValue());
        assertTrue(calculator.getValues()[2].isBlank(), "Expected values[2] to be blank");

        calculator.performCopyAction(actionEvent);
        assertFalse(calculator.getValues()[2].isBlank(), "Expected values[2] to be 5");
        assertEquals(FIVE.getValue(), calculator.getValues()[2], "Expected values[2] to be 5");
    }

    @Test
    public void testPaste()
    {
        calculator.performPasteAction(actionEvent);
        assertTrue(calculator.getValues()[2].isBlank(), "Expected values[2] to be blank");

        calculator.values[2] = "10";
        calculator.getTextPane().setText(calculator.addNewLines() + FIVE.getValue());

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
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue());
        calculator.getValues()[0] = "2";
        calculator.performNumberButtonAction(actionEvent);
        assertFalse(calculator.getValues()[0].contains("_"), "Expected no commas");
        assertEquals("25", calculator.getValues()[0], "Expected values[0] to be 25");
    }

    @Test
    public void testAddCourtesyCommasAdds1Comma4DigitsWholeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue());
        calculator.getValues()[0] = "999";
        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.getTextPaneValue().contains(","), "Expected textPane to be 9,995");
        assertEquals("9995", calculator.getValues()[0], "Expected values[0] to be 9995");
    }

    @Test
    public void testAddCourtesyCommasReturnsResultWithOneComma5DigitsWholeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue());
        calculator.getValues()[0] = "1234";
        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.getTextPaneValue().contains(","), "Expected textPane to be 12,345");
        assertEquals("12345", calculator.getValues()[0], "Expected values[0] to be 12345");
    }

    @Test
    public void testAddCourtesyCommasReturnsResultWithOneComma6DigitsWholeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SIX.getValue());
        calculator.getValues()[0] = "12345";
        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.getTextPaneValue().contains(","), "Expected textPane to be 123,456");
        assertEquals("123456", calculator.getValues()[0], "Expected values[0] to be 123456");
    }

    @Test
    public void testDeletingADigitAdjustsCourtesyCommas()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue());
        calculator.getValues()[0] = "12345";
        calculator.getTextPane().setText("12345");
        calculator.performDeleteButtonAction(actionEvent);
        assertTrue(calculator.getTextPaneValue().contains(","), "Expected textPane to be 1,234");
        assertEquals("1234", calculator.getValues()[0], "Expected values[0] to be 1234");
    }

    @Test
    public void testAddCourtesyCommasReturnsResultWithTwoCommas7DigitsWholeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SEVEN.getValue());
        calculator.getValues()[0] = "123456";
        calculator.performNumberButtonAction(actionEvent);
        assertTrue(calculator.getTextPaneValue().contains(","), "Expected textPane to be 1,234,567");
        assertEquals("1234567", calculator.getValues()[0], "Expected values[0] to be 1234567");
    }

    @Test
    public void testCheckValueLength()
    {
        calculator.values[0] = "9999998";
        assertTrue(calculator.checkValueLength(), "Expected max length to be met");
        assertFalse(calculator.isMaximumValue(), "Expected max number to not be met");
    }

    @Test
    public void testValueAt0IsMinimumNumber()
    {
        calculator.values[0] = "0.0000001";
        assertTrue(calculator.isMinimumValue(), "Expected maximum number to be met");
    }

    @Test
    public void testValueAt1IsMinimumNumber()
    {
        calculator.values[1] = "0.0000001";
        assertTrue(calculator.isMinimumValue(), "Expected maximum number to be met");
    }

    @Test
    public void testValueIsMinimumNumber()
    {
        calculator.getValues()[0] = "0.0000001";
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
        calculator.values[1] = "9999999";
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
         doReturn(false).when(calculatorSpy).isMacOperatingSystem();
         calculatorSpy.setupMenuBar();
         assertEquals(5, calculator.getViewMenu().getMenuComponents().length, "Expected View menu to have 3 options");
    }

    @Test
    public void testAboutCalculatorShowsWindowsInText()
    {
        doReturn(false).when(calculatorSpy).isMacOperatingSystem();
        String aboutCalculatorText = calculatorSpy.getAboutCalculatorString();
        assertTrue(aboutCalculatorText.contains(WINDOWS.getValue()), "Expected About Calculator Text to have Windows");
        assertFalse(aboutCalculatorText.contains(APPLE.getValue()), "Expected About Calculator Text to not have Apple");
    }

    @Test
    public void testGetLowestMemoryValuePosition()
    {
        calculator.getMemoryValues()[0] = ONE.getValue();
        calculator.getMemoryValues()[1] = TWO.getValue();
        calculator.getMemoryValues()[2] = THREE.getValue();
        calculator.getMemoryValues()[3] = FOUR.getValue();
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
        assertEquals(BLANK.getValue(),
                calculator.getBasicHistoryPaneTextWithoutNewLineCharacters(),
                "Expected basicHistoryTextPane to be blank");
    }

    @Test
    public void testShowMemoryValuesInHistoryPane()
    {
        when(actionEvent.getActionCommand()).thenReturn("Show Memory Values");
        calculator.getMemoryValues()[0] = ONE.getValue();
        calculator.getMemoryValues()[1] = TWO.getValue();
        calculator.getMemoryValues()[2] = THREE.getValue();
        calculator.getMemoryValues()[3] = FOUR.getValue();
        calculator.setMemoryPosition(4);
        calculator.performShowMemoriesAction(actionEvent);
        assertEquals("Memories: [1], [2], [3], [4]",
                calculator.getBasicHistoryPaneTextWithoutNewLineCharacters(),
                "Expected memories to be in basicHistoryTextPane");
    }

}

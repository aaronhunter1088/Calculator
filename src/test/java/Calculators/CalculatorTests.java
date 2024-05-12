package Calculators;

import Panels.BasicPanel;
import Panels.ConverterPanel;
import Panels.DatePanel;
import Panels.ProgrammerPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import static Types.CalculatorType.*;
import static Types.CalculatorBase.*;
import static Types.ConverterType.*;
import static Types.DateOperation.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CalculatorTests
{
    static { System.setProperty("appName", "CalculatorTests"); }
    private static Logger LOGGER;
    private static Calculator calculator;

    @Mock
    ActionEvent actionEvent;

    @BeforeClass
    public static void beforeAll()
    { LOGGER = LogManager.getLogger(CalculatorTests.class.getSimpleName()); }

    @AfterClass
    public static void afterAll()
    { LOGGER.info("Finished running " + CalculatorTests.class.getSimpleName()); }

    @Before
    public void beforeEach() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        LOGGER.info("setting up each before...");
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void afterEach() {}

    /************* Basic Calculator Tests ******************/
    @Test
    public void createBasicCalculatorDefault() throws Exception
    {
        LOGGER.info("createDefaultCalculator...");
        calculator = new Calculator();
        assertTrue("Cannot see basic calculator", calculator.isVisible());
        assertEquals("Expected CalculatorType to be " + BASIC, BASIC, calculator.getCalculatorType());
    }

    @Test
    public void createBasicCalculatorEnforced() throws Exception
    {
        LOGGER.info("createBasicCalculator enforced...");
        calculator = new Calculator(BASIC);
        assertTrue("Cannot see basic calculator", calculator.isVisible());
        assertEquals("Expected CalculatorType to be " + BASIC, BASIC, calculator.getCalculatorType());
    }

    /************* Programmer Calculator Tests ******************/
    @Test
    public void createProgrammerCalculator() throws Exception
    {
        LOGGER.info("createProgrammerCalculator...");
        calculator = new Calculator(PROGRAMMER);
        calculator.setMotif(true);
        calculator.setCurrentPanel(new ProgrammerPanel(calculator, BINARY));
        assertTrue("Cannot see programmer calculator", calculator.isVisible());
        assertEquals("Expected CalculatorType to be " + PROGRAMMER, PROGRAMMER, calculator.getCalculatorType());
        assertSame("Base is not binary", calculator.getCalculatorBase(), BINARY);
    }

    @Test
    public void createProgrammerCalculatorInBinaryEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", BINARY);
        calculator = new Calculator(PROGRAMMER, BINARY);
        assertTrue("Cannot see programmer calculator", calculator.isVisible());
        assertEquals("Expected CalculatorType to be " + PROGRAMMER, PROGRAMMER, calculator.getCalculatorType());
        assertSame("Base is not binary", calculator.getCalculatorBase(), BINARY);
    }

    @Test
    public void createProgrammerCalculatorInDecimalEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", DECIMAL);
        calculator = new Calculator(PROGRAMMER, DECIMAL);
        assertTrue("Cannot see programmer calculator", calculator.isVisible());
        assertEquals("Expected CalculatorType to be " + PROGRAMMER, PROGRAMMER, calculator.getCalculatorType());
        assertSame("Base is not decimal", calculator.getCalculatorBase(), DECIMAL);
    }

    @Test
    public void createProgrammerCalculatorInOctalEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", OCTAL);
        calculator = new Calculator(PROGRAMMER, OCTAL);
        assertTrue("Cannot see programmer calculator", calculator.isVisible());
        assertSame("Base is not octal", calculator.getCalculatorBase(), OCTAL);
    }

    @Test
    public void createProgrammerCalculatorInHexadecimalEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", HEXADECIMAL);
        calculator = new Calculator(PROGRAMMER, HEXADECIMAL);
        assertTrue("Cannot see programmer calculator", calculator.isVisible());
        assertSame("Base is not binary", calculator.getCalculatorBase(), HEXADECIMAL);
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
        calculator = new Calculator(DATE);
        assertTrue("Cannot see date calculator", calculator.isVisible());
        assertEquals("Expected CalculatorType to be " + DATE, DATE, calculator.getCalculatorType());
        assertSame("Date operation is not " + DIFFERENCE_BETWEEN_DATES, ((DatePanel)calculator.getCurrentPanel()).getDateOperation(), DIFFERENCE_BETWEEN_DATES);
    }

    @Test
    public void createDateCalculatorWithDateOperation1Enforced() throws Exception
    {
        LOGGER.info("createDateCalculator with {} enforced...", DIFFERENCE_BETWEEN_DATES);
        calculator = new Calculator(DATE, DIFFERENCE_BETWEEN_DATES);
        assertTrue("Cannot see date calculator", calculator.isVisible());
        assertEquals("Expected CalculatorType to be " + DATE, DATE, calculator.getCalculatorType());
        assertSame("Date operation is not " + DIFFERENCE_BETWEEN_DATES, ((DatePanel)calculator.getCurrentPanel()).getDateOperation(), DIFFERENCE_BETWEEN_DATES);
    }

    @Test
    public void createDateCalculatorWithDateOperation2Enforced() throws Exception
    {
        LOGGER.info("createDateCalculator with {} enforced...", ADD_SUBTRACT_DAYS);
        calculator = new Calculator(DATE, ADD_SUBTRACT_DAYS);
        assertTrue("Cannot see date calculator", calculator.isVisible());
        assertEquals("Expected CalculatorType to be " + DATE, DATE, calculator.getCalculatorType());
        assertSame("Date operation is not " + ADD_SUBTRACT_DAYS, ((DatePanel)calculator.getCurrentPanel()).getDateOperation(), ADD_SUBTRACT_DAYS);
    }

    /************* Converter Calculator Tests ******************/
    @Test
    public void createConverterCalculator() throws Exception
    {
        LOGGER.info("createConverterCalculator...");
        calculator = new Calculator(CONVERTER);
        assertTrue("Cannot see converter calculator", calculator.isVisible());
        assertEquals("Expected CalculatorType to be " + CONVERTER, CONVERTER, calculator.getCalculatorType());
        assertSame("Converter operation is not " + ANGLE, ((ConverterPanel)calculator.getCurrentPanel()).getConverterType(), ANGLE);
    }

    @Test
    public void createConverterCalculatorWithAngleEnforced() throws Exception
    {
        LOGGER.info("createConverterCalculator with {} enforced...", ANGLE);
        calculator = new Calculator(CONVERTER, ANGLE);
        assertTrue("Cannot see converter calculator", calculator.isVisible());
        assertEquals("Expected CalculatorType to be " + CONVERTER, CONVERTER, calculator.getCalculatorType());
        assertSame("Converter operation is not " + ANGLE, ((ConverterPanel)calculator.getCurrentPanel()).getConverterType(), ANGLE);
    }

    @Test
    public void createConverterCalculatorWithAreaEnforced() throws Exception
    {
        LOGGER.info("createConverterCalculator with {} enforced...", AREA);
        calculator = new Calculator(CONVERTER, AREA);
        assertTrue("Cannot see converter calculator", calculator.isVisible());
        assertEquals("Expected CalculatorType to be " + CONVERTER, CONVERTER, calculator.getCalculatorType());
        assertSame("Converter operation is not " + AREA, ((ConverterPanel)calculator.getCurrentPanel()).getConverterType(), AREA);
    }

    /************* All Calculator Tests ******************/
    @Test
    public void testConvertingToPositive()
    {
        assertEquals("Number is not positive", "5.02", calculator.convertToPositive("-5.02"));
    }

    @Test
    public void testConvertingToNegative()
    {
        assertEquals("Number is not negative", "-5.02", calculator.convertToNegative("5.02"));
    }

    @Test
    public void testIsPositiveReturnsTrue()
    {
        assertTrue("IsPositive did not return true", calculator.isPositiveNumber("6"));
    }

    @Test
    public void testIsPositiveReturnsFalse()
    {
        assertFalse("IsPositive did not return false", calculator.isPositiveNumber("-6"));
    }

    @Test
    public void testIsNegativeReturnsTrue()
    {
        assertTrue("IsNegative did not return true", calculator.isNegativeNumber("-6"));
    }

    @Test
    public void testIsNegativeReturnsFalse()
    {
        assertFalse("IsNegative did not return false", calculator.isNegativeNumber("6"));
    }

    @Test
    public void testIsDecimalReturnsTrue()
    {
        assertTrue("Number should contain the '.'", calculator.isDecimal("5.02"));
    }

    @Test
    public void testIsDecimalReturnsFalse()
    {
        assertFalse("Number should not contain the '.'", calculator.isDecimal("5"));
    }

    @Test
    public void methodResetOperatorsWithTrueResultsInAllOperatorsBeingTrue()
    {
        calculator.resetBasicOperators(true);
        assertTrue("isAdding() is not true", calculator.isAdding());
        assertTrue("isSubtracting() is not true", calculator.isSubtracting());
        assertTrue("isMultiplying() is not true", calculator.isMultiplying());
        assertTrue("isDividing() is not true", calculator.isDividing());
    }

    @Test
    public void methodResetOperatorsWithFalseResultsInAllOperatorsBeingFalse()
    {
        calculator.resetBasicOperators(false);
        assertFalse("isAdding() is not false", calculator.isAdding());
        assertFalse("isSubtracting() is not false", calculator.isSubtracting());
        assertFalse("isMultiplying() is not false", calculator.isMultiplying());
        assertFalse("isDividing() is not false", calculator.isDividing());
    }

    @Test
    public void methodResetOperatorsWhenValuesAt0IsNotADecimal() throws Exception
    {
        calculator = new Calculator();
        calculator.getValues()[0] = "5";
        boolean resetResult = calculator.resetOperator(false);
        assertTrue("Expected result to be true", resetResult);
        assertFalse("Expected dot not to be pressed", calculator.isDotPressed());
        assertTrue("Expected dot button to be enabled", calculator.getButtonDot().isEnabled());
    }

    @Test
    public void testSetImageIconsWorksAsExpected() throws Exception
    {
        calculator = new Calculator();
        assertNotNull("Expected calculator image", calculator.getCalculatorIcon());
        assertNotNull("Expected mac icon", calculator.getMacIcon());
        assertNotNull("Expected windows icon", calculator.getWindowsIcon());
        assertNull("Expected no icon", calculator.getBlankIcon());
    }

    @Test
    public void switchingFromProgrammerToBasicSwitchesPanels() throws Exception
    {
        when(actionEvent.getActionCommand()).thenReturn(BASIC.getName());
        calculator = new Calculator(PROGRAMMER);
        calculator.getTextPane().setText("00000100");
        calculator.getValues()[0]= "4";
        assertEquals("Expected textPane to show Binary representation", "00000100", calculator.getTextPaneWithoutNewLineCharacters());

        calculator.switchPanels(actionEvent);
        assertEquals("Expected textPane to show Decimal representation", "4", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected name to be Basic", BASIC.getName(), calculator.getTitle());
        assertTrue("Expected BasicPanel", calculator.getCurrentPanel() instanceof BasicPanel);
    }

    @Test
    public void switchingFromBasicToProgrammerSwitchesPanels() throws Exception
    {
        when(actionEvent.getActionCommand()).thenReturn(PROGRAMMER.getName());
        calculator = new Calculator();
        calculator.getTextPane().setText("4");
        calculator.getValues()[0]= "4";
        assertEquals("Expected textPane to show Decimal representation", "4", calculator.getTextPaneWithoutNewLineCharacters());

        calculator.switchPanels(actionEvent);
        assertEquals("Expected textPane to show Decimal representation", "4", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected name to be Programmer", PROGRAMMER.getName(), calculator.getTitle());
        assertTrue("Expected ProgrammerPanel", calculator.getCurrentPanel() instanceof ProgrammerPanel);
    }

    @Test
    public void switchingFromBasicToDateSwitchesPanels() throws Exception
    {
        when(actionEvent.getActionCommand()).thenReturn(DATE.getName());
        calculator = new Calculator();
        assertEquals("Expected BASIC CalculatorType", BASIC, calculator.getCalculatorType());

        calculator.switchPanels(actionEvent);
        assertEquals("Expected DATE CalculatorType", DATE, calculator.getCalculatorType());
        assertEquals("Expected name to be Date", DATE.getName(), calculator.getTitle());
        assertTrue("Expected DatePanel", calculator.getCurrentPanel() instanceof DatePanel);
    }

    @Test
    public void switchingFromBasicToAngleConverterSwitchesPanels() throws Exception
    {
        when(actionEvent.getActionCommand()).thenReturn(ANGLE.getName());
        calculator = new Calculator();
        assertEquals("Expected BASIC CalculatorType", BASIC, calculator.getCalculatorType());

        calculator.switchPanels(actionEvent);
        assertEquals("Expected CONVERTER CalculatorType", CONVERTER, calculator.getCalculatorType());
        assertEquals("Expected name to be CONVERTER", CONVERTER.getName(), calculator.getTitle());
        assertTrue("Expected ConverterPanel", calculator.getCurrentPanel() instanceof ConverterPanel);
    }

    @Test
    public void switchingFromBasicToAreaConverterSwitchesPanels() throws Exception
    {
        when(actionEvent.getActionCommand()).thenReturn(AREA.getName());
        calculator = new Calculator();
        assertEquals("Expected BASIC CalculatorType", BASIC, calculator.getCalculatorType());

        calculator.switchPanels(actionEvent);
        assertEquals("Expected CONVERTER CalculatorType", CONVERTER, calculator.getCalculatorType());
        assertEquals("Expected name to be CONVERTER", CONVERTER.getName(), calculator.getTitle());
        assertTrue("Expected ConverterPanel", calculator.getCurrentPanel() instanceof ConverterPanel);
    }

    @Test
    public void switchingFromSomePanelToSamePanelDoesNotSwitchPanels() throws Exception
    {
        when(actionEvent.getActionCommand()).thenReturn("Basic");
        calculator = new Calculator();
        BasicPanel panel = (BasicPanel) calculator.getCurrentPanel();
        calculator.getTextPane().setText("4");
        calculator.getValues()[0]= "4";
        assertEquals("Expected textPane to show Decimal representation", "4", calculator.getTextPaneWithoutNewLineCharacters());

        calculator.switchPanels(actionEvent);
        assertEquals("Expected the same panel", panel, calculator.getCurrentPanel());
        assertEquals("Expected textPane to show Decimal representation", "4", calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void switchingFromSomeConverterToSameConverterDoesNotSwitchPanels() throws Exception
    {
        when(actionEvent.getActionCommand()).thenReturn("Angle");
        calculator = new Calculator(CONVERTER);
        ConverterPanel panel = (ConverterPanel) calculator.getCurrentPanel();
        assertEquals("Expected converterType to be ANGLE", ANGLE, calculator.getConverterType());

        calculator.switchPanels(actionEvent);
        assertEquals("Expected the same panel", panel, calculator.getCurrentPanel());
        assertEquals("Expected converterType to be ANGLE", ANGLE, calculator.getConverterType());
    }

    @Test
    public void switchingToMetalLookAndFeel() throws Exception
    {
        calculator = new Calculator();
        var lookMenuItems = calculator.getLookMenu().getMenuComponents();
        for(Component menuItem : lookMenuItems)
        {
            if ("Metal".equals(menuItem.getName()))
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
    public void switchingToSystemLookAndFeel() throws Exception {
        calculator = new Calculator();
        var lookMenuItems = calculator.getLookMenu().getMenuComponents();
        for (Component menuItem : lookMenuItems)
        {
            if ("System".equals(menuItem.getName()))
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
    public void switchingToWindowsLookAndFeel() throws Exception {
        calculator = new Calculator();
        var lookMenuItems = calculator.getLookMenu().getMenuComponents();
        for (Component menuItem : lookMenuItems)
        {
            if ("Windows".equals(menuItem.getName()))
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
    public void switchingToMotifLookAndFeel() throws Exception
    {
        calculator = new Calculator();
        var lookMenuItems = calculator.getLookMenu().getMenuComponents();
        for(Component menuItem : lookMenuItems)
        {
            if ("Motif".equals(menuItem.getName()))
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
    public void switchingToGTKLookAndFeel() throws Exception
    {
        calculator = new Calculator();
        var lookMenuItems = calculator.getLookMenu().getMenuComponents();
        for (Component menuItem : lookMenuItems)
        {
            if ("GTK".equals(menuItem.getName()))
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
    public void testResetValues() throws Exception
    {
        calculator = new Calculator();
        calculator.values[0] = "15";
        calculator.values[1] = "26";
        calculator.values[2] = "5";

        assertEquals("Expected values[0] to be 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("Expected values[1] to be 26", 26, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("Expected values[2] to be 5", 5, Integer.parseInt(calculator.getValues()[2]));

        calculator.resetValues();

        assertTrue("Expected values[0] to be blank", calculator.getValues()[0].isEmpty());
        assertTrue("Expected values[1] to be blank", calculator.getValues()[1].isEmpty());
        assertTrue("Expected values[2] to be blank", calculator.getValues()[2].isEmpty());
    }

    @Test
    public void testClearNumberButtonActions() throws Exception
    {
        calculator = new Calculator();
        calculator.getNumberButtons().forEach(numberButton -> {
            assertSame("Expecting only 1 action on " + numberButton.getName(), 1, numberButton.getActionListeners().length);
        });

        calculator.clearNumberButtonActions();

        calculator.getNumberButtons().forEach(numberButton -> {
            assertSame("Expecting no actions on " + numberButton.getName(), 0, numberButton.getActionListeners().length);
        });
    }

    @Test
    public void testClearAllOtherBasicCalculatorButtons() throws Exception
    {
        calculator = new Calculator();
        calculator.getAllOtherBasicCalculatorButtons().forEach(otherButton -> {
            assertSame("Expecting only 1 action on " + otherButton.getName(), 1, otherButton.getActionListeners().length);
        });

        calculator.clearAllOtherBasicCalculatorButtons();

        calculator.getAllOtherBasicCalculatorButtons().forEach(otherButton -> {
            assertSame("Expecting no actions on " + otherButton.getName(), 0, otherButton.getActionListeners().length);
        });
    }

    @Test
    public void testDetermineIfAddingOperatorWasPushed() throws Exception
    {
        calculator = new Calculator();
        assertFalse("Did not expect any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        calculator.setAdding(true);
        assertTrue("Expected any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        assertTrue("Expected isAdding to be true", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
    }

    @Test
    public void testDetermineIfSubtractingOperatorWasPushed() throws Exception
    {
        calculator = new Calculator();
        assertFalse("Did not expect any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        calculator.setSubtracting(true);
        assertTrue("Expected any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        assertFalse("Expected isAdding to be false", calculator.isAdding());
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
    }

    @Test
    public void testDetermineIfMultiplyingOperatorWasPushed() throws Exception
    {
        calculator = new Calculator();
        assertFalse("Did not expect any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        calculator.setMultiplying(true);
        assertTrue("Expected any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        assertFalse("Expected isAdding to be false", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
    }

    @Test
    public void testDetermineIfDividingOperatorWasPushed() throws Exception
    {
        calculator = new Calculator();
        assertFalse("Did not expect any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        calculator.setDividing(true);
        assertTrue("Expected any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        assertFalse("Expected isAdding to be false", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void testResetOperatorIfClause() throws Exception
    {
        when(actionEvent.getActionCommand()).thenReturn(".");
        calculator = new Calculator();
        calculator.setAdding(true);
        BasicPanel.performDotButtonActions(actionEvent);
        assertSame("Expected valuesPosition to be 0",0, calculator.getValuesPosition());
        assertFalse("Expected dot button to be disabled", calculator.getButtonDot().isEnabled());
        assertTrue("Expected to be on the firstNumber", calculator.isFirstNumber());

        calculator.resetOperator(calculator.isAdding());

        assertSame("Expected valuesPosition to be 1",1, calculator.getValuesPosition());
        assertTrue("Expected dot button to be pressed", calculator.isDotPressed());
        assertFalse("Expected dot button to not be enabled", calculator.getButtonDot().isEnabled());
        assertFalse("Expected to not be on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void testResetOperatorElseClause() throws Exception
    {
        when(actionEvent.getActionCommand()).thenReturn(".");
        calculator = new Calculator();
        calculator.setAdding(false);
        BasicPanel.performDotButtonActions(actionEvent);
        assertSame("Expected valuesPosition to be 0",0, calculator.getValuesPosition());
        assertFalse("Expected dot button to be disabled", calculator.getButtonDot().isEnabled());
        assertTrue("Expected to be on the firstNumber", calculator.isFirstNumber());

        calculator.resetOperator(calculator.isAdding());

        assertSame("Expected valuesPosition to be 0",0, calculator.getValuesPosition());
        assertTrue("Expected dot button to be pressed", calculator.isDotPressed());
        assertFalse("Expected dot button to not be enabled", calculator.getButtonDot().isEnabled());
        assertTrue("Expected to be on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void testIsMemoryValuesEmptyIsTrue() throws Exception
    {
        calculator = new Calculator();
        assertTrue(calculator.isMemoryValuesEmpty());
    }

    @Test
    public void testIsMemoryValuesEmptyIsFalseForBasicCalculator() throws Exception
    {
        calculator = new Calculator();
        calculator.getTextPane().setText("5");
        calculator.getMemoryValues()[0] = "5";
        calculator.setMemoryPosition(1);
        assertFalse(calculator.isMemoryValuesEmpty());
        calculator.confirm("Test: isMemoryValuesEmpty -> False");
    }

    @Test
    public void testIsMemoryValuesEmptyIsFalseForProgrammerCalculator() throws Exception
    {
        calculator = new Calculator(PROGRAMMER);
        calculator.getTextPane().setText("5");
        calculator.getMemoryValues()[0] = "5";
        calculator.setMemoryPosition(1);
        assertFalse(calculator.isMemoryValuesEmpty());
        calculator.confirm("Test: isMemoryValuesEmpty -> False");
    }

    @Test
    public void testInitialChecksIfClause() throws Exception
    {
        calculator = new Calculator();
        calculator.getValues()[0] = "Not a Number";
        calculator.getTextPane().setText("Not a Number");
        assertEquals("Expected error message in textPane", "Not a Number", calculator.getTextPaneWithoutNewLineCharacters());

        calculator.performInitialChecks();

        assertFalse("Expected textPane to show error", calculator.getTextPaneWithoutNewLineCharacters().isEmpty());
        assertSame("Expected valuesPosition to be 0", 0, calculator.getValuesPosition());
        assertTrue("Expected to be firstNumber", calculator.isFirstNumber());
        assertFalse("Expected dot button to not be pushed", calculator.isDotPressed());
        assertTrue("Expected dot button to be enabled", calculator.getButtonDot().isEnabled());
        assertFalse("Expecting isNumberNegative to be false", calculator.isNumberNegative());
    }

    @Test
    public void testInitialChecksElseIf1Clause() throws Exception
    {
        calculator = new Calculator();
        calculator.values[0] = "0";
        calculator.getTextPane().setText(calculator.addNewLineCharacters() + "0");

        assertEquals("Expected textPane to contain 0", "0", calculator.getTextPaneWithoutAnyOperator());
        assertEquals("Expected BASIC CalculatorType", "Basic", calculator.getCalculatorType().getName());
        assertEquals("Expected values[0] to be 0", "0", calculator.getValues()[0]);

        calculator.performInitialChecks();

        assertTrue("Expected textPane to be blank", calculator.getTextPaneWithoutNewLineCharacters().isBlank());
        assertTrue("Expected values[0] to be blank", calculator.getValues()[0].isBlank());
        assertTrue("Expected to be on the firstNumber", calculator.isFirstNumber());
        assertFalse("Expected dot button to not be pressed", calculator.isDotPressed());
        assertTrue("Expected dot button to be enabled", calculator.getButtonDot().isEnabled());
    }

    @Test
    public void testInitialChecksElseIf2Clause() throws Exception
    {
        calculator = new Calculator();
        calculator.values[0] = "";
        calculator.values[1] = "15";
        calculator.setValuesPosition(1);

        assertTrue("Expected values[0] to be blank", calculator.getValues()[0].isBlank());
        assertSame("Expected values[1] to be 15", "15", calculator.getValues()[1]);
        assertSame("Expected valuesPosition to be 1", 1, calculator.getValuesPosition());

        calculator.performInitialChecks();

        assertSame("Expected values[0] to be 15", "15", calculator.getValues()[0]);
        assertTrue("Expected values[1] to be blank", calculator.getValues()[1].isBlank());
        assertSame("Expected valuesPosition to be 0", 0, calculator.getValuesPosition());
    }

    @Test
    public void testInitialChecksDoesNotEnterAnyClause() throws Exception
    {
        calculator = new Calculator();
        calculator.getTextPane().setText("5");
        calculator.performInitialChecks();
    }

    @Test
    public void testTextPaneContainsBadTextTrue() throws Exception
    {
        calculator = new Calculator();
        calculator.getTextPane().setText("Not a Number");
        assertTrue("Expected textPane to contain error", calculator.textPaneContainsBadText());
    }

    @Test
    public void testTextPaneContainsBadTextFalse() throws Exception
    {
        calculator = new Calculator();
        calculator.getTextPane().setText("5");
        assertFalse("Expected textPane to contain valid text", calculator.textPaneContainsBadText());
    }

    @Test
    public void testConfirmCalledForDatePanelWithDateOperation2() throws Exception
    {
        calculator = new Calculator(DATE, ADD_SUBTRACT_DAYS);
        calculator.confirm("Test: Confirm called");
    }

    @Test
    public void testAddNewLineCharactersForBasicPanelAdds1NewLine() throws Exception
    {
        calculator = new Calculator();
        String newLines = calculator.addNewLineCharacters();
        assertSame(1, newLines.split("").length);
    }

    @Test
    public void testAddNewLineCharactersForProgrammerPanelAdds3NewLines() throws Exception
    {
        calculator = new Calculator(PROGRAMMER);
        String newLines = calculator.addNewLineCharacters();
        assertSame(3, newLines.split("").length);
    }

    @Test
    public void testDynamicAddNewLineCharacters() throws Exception
    {
        calculator = new Calculator();
        String newLines = calculator.addNewLineCharacters(10);
        assertSame(10, newLines.split("").length);
    }

    @Test
    public void testGetAboutCalculatorStringReturnsText() throws Exception
    {
        String helpMe = new Calculator().getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Basic Calculator", helpMe);

        helpMe = new Calculator(PROGRAMMER).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Programmer Calculator Type:"+BINARY.getName(), helpMe);

        helpMe = new Calculator(PROGRAMMER, OCTAL).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Programmer Calculator Type:"+OCTAL.getName(), helpMe);

        helpMe = new Calculator(PROGRAMMER, DECIMAL).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Programmer Calculator Type:"+DECIMAL.getName(), helpMe);

        helpMe = new Calculator(PROGRAMMER, HEXADECIMAL).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Programmer Calculator Type:"+HEXADECIMAL.getName(), helpMe);

        helpMe = new Calculator(DATE).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Date Calculator Type:"+DIFFERENCE_BETWEEN_DATES, helpMe);

        helpMe = new Calculator(DATE, ADD_SUBTRACT_DAYS).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Date Calculator Type:"+ADD_SUBTRACT_DAYS, helpMe);

        helpMe = new Calculator(CONVERTER).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Converter Calculator Type:"+ANGLE, helpMe);

        helpMe = new Calculator(CONVERTER, AREA).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Converter Calculator Type:"+AREA.getName(), helpMe);
    }

    @Test
    public void testAboutCalculatorOpensAboutCalculatorPanel() throws Exception
    {
        calculator = new Calculator();
        calculator.performAboutCalculatorFunctionality(actionEvent);
    }

    @Test
    public void testGetTheNumberToTheLeftOfTheDecimal() throws Exception
    {
        calculator = new Calculator();
        assertEquals("123", calculator.getNumberOnLeftSideOfDecimal("123.456"));
    }

    @Test
    public void testGetTheNumberToTheRightOfTheDecimal() throws Exception
    {
        calculator = new Calculator();
        assertEquals("456", calculator.getNumberOnRightSideOfDecimal("123.456"));
    }

    @Test
    public void testCopy() throws Exception
    {
        calculator = new Calculator();
        calculator.getTextPane().setText("5");
        assertTrue("Expected values[2] to be blank", calculator.getValues()[2].isBlank());

        calculator.performCopyFunctionality(actionEvent);
        assertFalse("Expected values[2] to be 5", calculator.getValues()[2].isBlank());
        assertEquals("Expected values[2] to be 5", "5", calculator.getValues()[2]);
    }

    @Test
    public void testPaste() throws Exception
    {
        calculator = new Calculator();
        calculator.performPasteFunctionality(actionEvent);
        assertTrue("Expected values[2] to be blank", calculator.getValues()[2].isBlank());

        calculator.values[2] = "10";
        calculator.getTextPane().setText(calculator.addNewLineCharacters() + "5");

        calculator.performPasteFunctionality(actionEvent);
        assertEquals("Expected textPane to be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be 10", "10", calculator.getValues()[0]);

        calculator.setValuesPosition(1);
        calculator.performPasteFunctionality(actionEvent);
        assertEquals("Expected textPane to be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[1] to be 10", "10", calculator.getValues()[0]);

    }

    @Test
    public void testClearingZeroesAtTheEnd() throws Exception
    {
        calculator = new Calculator();
        calculator.getValues()[0] = "15.0";
        assertEquals("Expected values[0] to be 15", "15", calculator.clearZeroesAndDecimalAtEnd(calculator.getValues()[0]));
    }

    @Test
    public void testAddCourtesyCommasAddsNoCommas() throws Exception
    {
        when(actionEvent.getActionCommand()).thenReturn("5");
        calculator = new Calculator();
        calculator.getValues()[0] = "2";
        BasicPanel.performNumberButtonActions(actionEvent);
        assertFalse("Expected no commas", calculator.getValues()[0].contains("_"));
        assertEquals("Expected values[0] to be 25", "25", calculator.getValues()[0]);
    }

    @Test
    public void testAddCourtesyCommasAdds1Comma4DigitsWholeNumber() throws Exception
    {
        when(actionEvent.getActionCommand()).thenReturn("5");
        calculator = new Calculator();
        calculator.getValues()[0] = "999";
        ((BasicPanel)calculator.getCurrentPanel()).performNumberButtonActions(actionEvent);
        assertTrue("Expected textPane to be 9,995", calculator.getTextPaneWithoutNewLineCharacters().contains(","));
        assertEquals("Expected values[0] to be 9995", "9995", calculator.getValues()[0]);
    }

    @Test
    public void testAddCourtesyCommasReturnsResultWithOneComma5DigitsWholeNumber() throws Exception
    {
        when(actionEvent.getActionCommand()).thenReturn("5");
        calculator = new Calculator();
        calculator.getValues()[0] = "1234";
        ((BasicPanel)calculator.getCurrentPanel()).performNumberButtonActions(actionEvent);
        assertTrue("Expected textPane to be 12,345", calculator.getTextPaneWithoutNewLineCharacters().contains(","));
        assertEquals("Expected values[0] to be 12345", "12345", calculator.getValues()[0]);
    }

    @Test
    public void testAddCourtesyCommasReturnsResultWithOneComma6DigitsWholeNumber() throws Exception
    {
        when(actionEvent.getActionCommand()).thenReturn("6");
        calculator = new Calculator();
        calculator.getValues()[0] = "12345";
        ((BasicPanel)calculator.getCurrentPanel()).performNumberButtonActions(actionEvent);
        assertTrue("Expected textPane to be 123,456", calculator.getTextPaneWithoutNewLineCharacters().contains(","));
        assertEquals("Expected values[0] to be 123456", "123456", calculator.getValues()[0]);
    }

    @Test
    public void testDeletingADigitAdjustsCourtesyCommas() throws Exception
    {
        when(actionEvent.getActionCommand()).thenReturn("←");
        calculator = new Calculator();
        calculator.getValues()[0] = "12345";
        calculator.getTextPane().setText("12345");
        ((BasicPanel)calculator.getCurrentPanel()).performDeleteButtonActions(actionEvent);
        assertTrue("Expected textPane to be 1,234", calculator.getTextPaneWithoutNewLineCharacters().contains(","));
        assertEquals("Expected values[0] to be 1234", "1234", calculator.getValues()[0]);
    }

    @Test
    public void testAddCourtesyCommasReturnsResultWithTwoCommas7DigitsWholeNumber() throws Exception
    {
        when(actionEvent.getActionCommand()).thenReturn("7");
        calculator = new Calculator();
        calculator.getValues()[0] = "123456";
        ((BasicPanel)calculator.getCurrentPanel()).performNumberButtonActions(actionEvent);
        assertTrue("Expected textPane to be 1,234,567", calculator.getTextPaneWithoutNewLineCharacters().contains(","));
        assertEquals("Expected values[0] to be 1234567", "1234567", calculator.getValues()[0]);
    }

}

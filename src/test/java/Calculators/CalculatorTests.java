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
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import static Types.CalculatorView.*;
import static Types.CalculatorBase.*;
import static Types.ConverterType.*;
import static Types.DateOperation.*;
import static Types.Texts.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CalculatorTests
{
    static { System.setProperty("appName", "CalculatorTests"); }
    private static Logger LOGGER;
    private Calculator calculator;

    @Mock
    ActionEvent actionEvent;
    @Spy
    Calculator calculatorSpy;

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
        calculator = new Calculator();
        calculatorSpy = spy(calculator);
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
        assertEquals("Expected CalculatorView to be " + VIEW_BASIC, VIEW_BASIC, calculator.getCalculatorView());
    }

    @Test
    public void createBasicCalculatorEnforced() throws Exception
    {
        LOGGER.info("createBasicCalculator enforced...");
        calculator = new Calculator(VIEW_BASIC);
        assertTrue("Cannot see basic calculator", calculator.isVisible());
        assertEquals("Expected CalculatorView to be " + VIEW_BASIC, VIEW_BASIC, calculator.getCalculatorView());
    }

    /************* Programmer Calculator Tests ******************/
    @Test
    public void createProgrammerCalculator() throws Exception
    {
        LOGGER.info("createProgrammerCalculator...");
        calculator = new Calculator(VIEW_PROGRAMMER);
        calculator.setMotif(true);
        assertTrue("Cannot see programmer calculator", calculator.isVisible());
        assertEquals("Expected CalculatorView to be " + VIEW_PROGRAMMER, VIEW_PROGRAMMER, calculator.getCalculatorView());
        assertSame("Base is not decimal", calculator.getCalculatorBase(), BASE_DECIMAL);
    }

    @Test
    public void createProgrammerCalculatorInBinaryEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", BASE_BINARY);
        calculator = new Calculator(BASE_BINARY);
        assertTrue("Cannot see programmer calculator", calculator.isVisible());
        assertEquals("Expected CalculatorView to be " + VIEW_PROGRAMMER, VIEW_PROGRAMMER, calculator.getCalculatorView());
        assertSame("Base is not binary", calculator.getCalculatorBase(), BASE_BINARY);
    }

    @Test
    public void createProgrammerCalculatorInDecimalEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", BASE_DECIMAL);
        calculator = new Calculator(BASE_DECIMAL);
        assertTrue("Cannot see programmer calculator", calculator.isVisible());
        assertEquals("Expected CalculatorView to be " + VIEW_PROGRAMMER, VIEW_PROGRAMMER, calculator.getCalculatorView());
        assertSame("Base is not decimal", calculator.getCalculatorBase(), BASE_DECIMAL);
    }

    @Test
    public void createProgrammerCalculatorInOctalEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", BASE_OCTAL);
        calculator = new Calculator(BASE_OCTAL);
        assertTrue("Cannot see programmer calculator", calculator.isVisible());
        assertSame("Base is not octal", calculator.getCalculatorBase(), BASE_OCTAL);
    }

    @Test
    public void createProgrammerCalculatorInHexadecimalEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", BASE_HEXADECIMAL);
        calculator = new Calculator(BASE_HEXADECIMAL);
        assertTrue("Cannot see programmer calculator", calculator.isVisible());
        assertSame("Base is not binary", calculator.getCalculatorBase(), BASE_HEXADECIMAL);
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
        calculator = new Calculator(VIEW_DATE);
        assertTrue("Cannot see date calculator", calculator.isVisible());
        assertEquals("Expected CalculatorView to be " + VIEW_DATE, VIEW_DATE, calculator.getCalculatorView());
        assertSame("Date operation is not " + DIFFERENCE_BETWEEN_DATES, ((DatePanel)calculator.getCurrentPanel()).getDateOperation(), DIFFERENCE_BETWEEN_DATES);
    }

    @Test
    public void createDateCalculatorWithDateOperation1Enforced() throws Exception
    {
        LOGGER.info("createDateCalculator with {} enforced...", DIFFERENCE_BETWEEN_DATES);
        calculator = new Calculator(DIFFERENCE_BETWEEN_DATES);
        assertTrue("Cannot see date calculator", calculator.isVisible());
        assertEquals("Expected CalculatorView to be " + VIEW_DATE, VIEW_DATE, calculator.getCalculatorView());
        assertSame("Date operation is not " + DIFFERENCE_BETWEEN_DATES, ((DatePanel)calculator.getCurrentPanel()).getDateOperation(), DIFFERENCE_BETWEEN_DATES);
    }

    @Test
    public void createDateCalculatorWithDateOperation2Enforced() throws Exception
    {
        LOGGER.info("createDateCalculator with {} enforced...", ADD_SUBTRACT_DAYS);
        calculator = new Calculator(ADD_SUBTRACT_DAYS);
        assertTrue("Cannot see date calculator", calculator.isVisible());
        assertEquals("Expected CalculatorView to be " + VIEW_DATE, VIEW_DATE, calculator.getCalculatorView());
        assertSame("Date operation is not " + ADD_SUBTRACT_DAYS, ((DatePanel)calculator.getCurrentPanel()).getDateOperation(), ADD_SUBTRACT_DAYS);
    }

    /************* Converter Calculator Tests ******************/
    @Test
    public void createConverterCalculator() throws Exception
    {
        LOGGER.info("createConverterCalculator...");
        calculator = new Calculator(VIEW_CONVERTER);
        assertTrue("Cannot see converter calculator", calculator.isVisible());
        assertEquals("Expected CalculatorView to be " + VIEW_CONVERTER, VIEW_CONVERTER, calculator.getCalculatorView());
        assertSame("Converter operation is not " + ANGLE, ((ConverterPanel)calculator.getCurrentPanel()).getConverterType(), ANGLE);
    }

    @Test
    public void createConverterCalculatorWithAngleEnforced() throws Exception
    {
        LOGGER.info("createConverterCalculator with {} enforced...", ANGLE);
        calculator = new Calculator(ANGLE);
        assertTrue("Cannot see converter calculator", calculator.isVisible());
        assertEquals("Expected CalculatorView to be " + VIEW_CONVERTER, VIEW_CONVERTER, calculator.getCalculatorView());
        assertSame("Converter operation is not " + ANGLE, ((ConverterPanel)calculator.getCurrentPanel()).getConverterType(), ANGLE);
    }

    @Test
    public void createConverterCalculatorWithAreaEnforced() throws Exception
    {
        LOGGER.info("createConverterCalculator with {} enforced...", AREA);
        calculator = new Calculator(AREA);
        assertTrue("Cannot see converter calculator", calculator.isVisible());
        assertEquals("Expected CalculatorView to be " + VIEW_CONVERTER, VIEW_CONVERTER, calculator.getCalculatorView());
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
        assertTrue("IsPositive did not return true", calculator.isPositiveNumber(SIX.getValue()));
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
        assertFalse("IsNegative did not return false", calculator.isNegativeNumber(SIX.getValue()));
    }

    @Test
    public void testIsDecimalReturnsTrue()
    {
        assertTrue("Number should contain the decimal", calculator.isDecimal("5.02"));
    }

    @Test
    public void testIsDecimalReturnsFalse()
    {
        assertFalse("Number should not contain the decimal", calculator.isDecimal(FIVE.getValue()));
    }

    @Test
    public void methodResetCalculatorOperationsWithTrueResultsInAllOperatorsBeingTrue()
    {
        calculator.resetBasicOperators(true);
        assertTrue("isAdding() is not true", calculator.isAdding());
        assertTrue("isSubtracting() is not true", calculator.isSubtracting());
        assertTrue("isMultiplying() is not true", calculator.isMultiplying());
        assertTrue("isDividing() is not true", calculator.isDividing());
    }

    @Test
    public void methodResetCalculatorOperationsWithFalseResultsInAllOperatorsBeingFalse()
    {
        calculator.resetBasicOperators(false);
        assertFalse("isAdding() is not false", calculator.isAdding());
        assertFalse("isSubtracting() is not false", calculator.isSubtracting());
        assertFalse("isMultiplying() is not false", calculator.isMultiplying());
        assertFalse("isDividing() is not false", calculator.isDividing());
    }

    @Test
    public void methodResetCalculatorOperationsWithFalse()
    {
        calculator.getValues()[0] = FIVE.getValue();
        boolean resetResult = calculator.resetCalculatorOperations(false);
        assertTrue("Expected result to be true", resetResult);
        assertTrue("Expected decimal to be disabled", calculator.isDotPressed());
    }

    @Test
    public void methResetCalculatorOperationsWithTrue()
    {
        calculator.getValues()[0] = FIVE.getValue();
        boolean resetResult = calculator.resetCalculatorOperations(true);
        assertFalse("Expected result to be false", resetResult);
        assertTrue("Expected decimal to be enabled", calculator.isDotPressed());
    }

    @Test
    public void testSetImageIconsWorksAsExpected()
    {
        assertNotNull("Expected calculator image", calculator.getCalculatorIcon());
        assertNotNull("Expected mac icon", calculator.getMacIcon());
        assertNotNull("Expected windows icon", calculator.getWindowsIcon());
        assertNull("Expected no icon", calculator.getBlankIcon());
    }

    @Test
    public void switchingFromProgrammerToBasicSwitchesPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_BASIC.getValue());
        calculator.setCalculatorView(VIEW_PROGRAMMER);
        calculator.updateJPanel(new ProgrammerPanel());
        calculator.getTextPane().setText("00000100");
        calculator.getValues()[0]= FOUR.getValue();
        assertEquals("Expected textPane to show Binary representation", "00000100", calculator.getTextPaneWithoutNewLineCharacters());

        calculator.switchPanels(actionEvent);
        assertEquals("Expected textPane to show Decimal representation", FOUR.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected name to be Basic", VIEW_BASIC.getValue(), calculator.getTitle());
        assertTrue("Expected BasicPanel", calculator.getCurrentPanel() instanceof BasicPanel);
    }

    @Test
    public void switchingFromBasicToProgrammerSwitchesPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_PROGRAMMER.getValue());
        calculator.getTextPane().setText(FOUR.getValue());
        calculator.getValues()[0] = FOUR.getValue();
        assertEquals("Expected textPane to show Decimal representation", FOUR.getValue(), calculator.getTextPaneWithoutNewLineCharacters());

        calculator.switchPanels(actionEvent);
        assertEquals("Expected textPane to show Decimal representation", FOUR.getValue(), calculator.getValueFromTextPaneForProgrammerPanel());
        assertEquals("Expected name to be Programmer", VIEW_PROGRAMMER.getValue(), calculator.getTitle());
        assertTrue("Expected ProgrammerPanel", calculator.getCurrentPanel() instanceof ProgrammerPanel);
    }

    @Test
    public void switchingFromBasicToDateSwitchesPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_DATE.getValue());
        assertEquals("Expected BASIC CalculatorView", VIEW_BASIC, calculator.getCalculatorView());
        calculator.switchPanels(actionEvent);
        assertEquals("Expected DATE CalculatorView", VIEW_DATE, calculator.getCalculatorView());
        assertEquals("Expected name to be Date", VIEW_DATE.getValue(), calculator.getTitle());
        assertTrue("Expected DatePanel", calculator.getCurrentPanel() instanceof DatePanel);
    }

    @Test
    public void switchingFromBasicToAngleConverterSwitchesPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(ANGLE.getValue());
        assertEquals("Expected BASIC CalculatorView", VIEW_BASIC, calculator.getCalculatorView());

        calculator.switchPanels(actionEvent);
        assertEquals("Expected CONVERTER CalculatorView", VIEW_CONVERTER, calculator.getCalculatorView());
        assertEquals("Expected name to be CONVERTER", VIEW_CONVERTER.getValue(), calculator.getTitle());
        assertTrue("Expected ConverterPanel", calculator.getCurrentPanel() instanceof ConverterPanel);
    }

    @Test
    public void switchingFromBasicToAreaConverterSwitchesPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(AREA.getValue());
        assertEquals("Expected BASIC CalculatorView", VIEW_BASIC, calculator.getCalculatorView());

        calculator.switchPanels(actionEvent);
        assertEquals("Expected CONVERTER CalculatorView", VIEW_CONVERTER, calculator.getCalculatorView());
        assertEquals("Expected name to be CONVERTER", VIEW_CONVERTER.getValue(), calculator.getTitle());
        assertTrue("Expected ConverterPanel", calculator.getCurrentPanel() instanceof ConverterPanel);
    }

    @Test
    public void switchingFromSomePanelToSamePanelDoesNotSwitchPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(VIEW_BASIC.getValue());
        BasicPanel panel = (BasicPanel) calculator.getCurrentPanel();
        calculator.getTextPane().setText(FOUR.getValue());
        calculator.getValues()[0]= FOUR.getValue();
        assertEquals("Expected textPane to show Decimal representation", FOUR.getValue(), calculator.getTextPaneWithoutNewLineCharacters());

        calculator.switchPanels(actionEvent);
        assertEquals("Expected the same panel", panel, calculator.getCurrentPanel());
        assertEquals("Expected textPane to show Decimal representation", FOUR.getValue(), calculator.getTextPaneWithoutNewLineCharacters());
    }

    @Test
    public void switchingFromSomeConverterToSameConverterDoesNotSwitchPanels()
    {
        when(actionEvent.getActionCommand()).thenReturn(ANGLE.getValue());
        calculator.setCalculatorView(VIEW_CONVERTER);
        calculator.updateJPanel(new ConverterPanel());
        ConverterPanel panel = (ConverterPanel) calculator.getCurrentPanel();
        assertEquals("Expected converterType to be ANGLE", ANGLE, calculator.getConverterType());

        calculator.switchPanels(actionEvent);
        assertEquals("Expected the same panel", panel, calculator.getCurrentPanel());
        assertEquals("Expected converterType to be ANGLE", ANGLE, calculator.getConverterType());
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

        assertEquals("Expected values[0] to be 15", 15, Integer.parseInt(calculator.getValues()[0]));
        assertEquals("Expected values[1] to be 26", 26, Integer.parseInt(calculator.getValues()[1]));
        assertEquals("Expected values[2] to be 5", 5, Integer.parseInt(calculator.getValues()[2]));

        calculator.resetValues();

        assertTrue("Expected values[0] to be blank", calculator.getValues()[0].isEmpty());
        assertTrue("Expected values[1] to be blank", calculator.getValues()[1].isEmpty());
        assertTrue("Expected values[2] to be blank", calculator.getValues()[2].isEmpty());
    }

    @Test
    public void testClearNumberButtonActions()
    {
        calculator.getAllNumberButtons().forEach(numberButton -> {
            assertSame("Expecting only 1 action on " + numberButton.getName(), 1, numberButton.getActionListeners().length);
        });

        calculator.clearNumberButtonActions();

        calculator.getAllNumberButtons().forEach(numberButton -> {
            assertSame("Expecting no actions on " + numberButton.getName(), 0, numberButton.getActionListeners().length);
        });
    }

    @Test
    public void testClearAllOtherBasicCalculatorButtons()
    {
        calculator.getAllBasicPanelButtons().forEach(otherButton -> {
            assertSame("Expecting only 1 action on " + otherButton.getName(), 1, otherButton.getActionListeners().length);
        });

        calculator.clearAllOtherBasicCalculatorButtons();

        calculator.getAllBasicPanelButtons().forEach(otherButton -> {
            assertSame("Expecting no actions on " + otherButton.getName(), 0, otherButton.getActionListeners().length);
        });
    }

    @Test
    public void testDetermineIfAddingOperatorWasPushed()
    {
        assertFalse("Did not expect any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        calculator.setIsAdding(true);
        assertTrue("Expected any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        assertTrue("Expected isAdding to be true", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
    }

    @Test
    public void testDetermineIfSubtractingOperatorWasPushed()
    {
        assertFalse("Did not expect any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        calculator.setIsSubtracting(true);
        assertTrue("Expected any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        assertFalse("Expected isAdding to be false", calculator.isAdding());
        assertTrue("Expected isSubtracting to be true", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
    }

    @Test
    public void testDetermineIfMultiplyingOperatorWasPushed()
    {
        assertFalse("Did not expect any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        calculator.setIsMultiplying(true);
        assertTrue("Expected any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        assertFalse("Expected isAdding to be false", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertTrue("Expected isMultiplying to be true", calculator.isMultiplying());
        assertFalse("Expected isDividing to be false", calculator.isDividing());
    }

    @Test
    public void testDetermineIfDividingOperatorWasPushed()
    {
        assertFalse("Did not expect any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        calculator.setIsDividing(true);
        assertTrue("Expected any operator to be pushed", calculator.determineIfAnyBasicOperatorWasPushed());
        assertFalse("Expected isAdding to be false", calculator.isAdding());
        assertFalse("Expected isSubtracting to be false", calculator.isSubtracting());
        assertFalse("Expected isMultiplying to be false", calculator.isMultiplying());
        assertTrue("Expected isDividing to be true", calculator.isDividing());
    }

    @Test
    public void testResetOperatorIfClause()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL.getValue());
        calculator.setIsAdding(true);
        calculator.performDecimalButtonAction(actionEvent);
        assertSame("Expected valuesPosition to be 0",0, calculator.getValuesPosition());
        assertFalse("Expected dot button to be disabled", calculator.isDotPressed());
        assertTrue("Expected to be on the firstNumber", calculator.isFirstNumber());

        calculator.resetCalculatorOperations(calculator.isAdding());

        assertSame("Expected valuesPosition to be 1",1, calculator.getValuesPosition());
        assertFalse("Expected dot button to be disabled", calculator.isDotPressed());
        assertFalse("Expected to not be on the firstNumber", calculator.isFirstNumber());
    }

    @Test
    public void testResetOperatorElseClause()
    {
        when(actionEvent.getActionCommand()).thenReturn(DECIMAL.getValue());
        calculator.setIsAdding(false);
        calculator.performDecimalButtonAction(actionEvent);
        assertSame("Expected valuesPosition to be 0",0, calculator.getValuesPosition());
        assertFalse("Expected dot button to be disabled", calculator.getButtonDecimal().isEnabled());
        assertTrue("Expected to be on the firstNumber", calculator.isFirstNumber());

        calculator.resetCalculatorOperations(calculator.isAdding());

        assertSame("Expected valuesPosition to be 0",0, calculator.getValuesPosition());
        assertFalse("Expected dot button to be disabled", calculator.isDotPressed());
        assertFalse("Expected dot button to not be enabled", calculator.getButtonDecimal().isEnabled());
        assertTrue("Expected to be on the firstNumber", calculator.isFirstNumber());
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
        assertEquals("Expected error message in textPane", NOT_A_NUMBER.getValue(), calculator.getTextPaneWithoutNewLineCharacters());

        calculator.performInitialChecks();

        assertFalse("Expected textPane to show error", calculator.getTextPaneWithoutNewLineCharacters().isEmpty());
        assertSame("Expected valuesPosition to be 0", 0, calculator.getValuesPosition());
        assertTrue("Expected to be firstNumber", calculator.isFirstNumber());
        assertTrue("Expected dot button to be enabled", calculator.isDotPressed());
        assertTrue("Expected dot button to be enabled", calculator.getButtonDecimal().isEnabled());
        assertFalse("Expecting isNumberNegative to be false", calculator.isNumberNegative());
    }

    @Test
    public void testInitialChecksElseIf1Clause()
    {
        calculator.values[0] = ZERO.getValue();
        calculator.getTextPane().setText(calculator.addNewLines() + ZERO.getValue());

        assertEquals("Expected textPane to contain 0", ZERO.getValue(), calculator.getTextPaneWithoutAnyOperator());
        assertEquals("Expected BASIC CalculatorView", VIEW_BASIC.getValue(), calculator.getCalculatorView().getValue());
        assertEquals("Expected values[0] to be 0", ZERO.getValue(), calculator.getValues()[0]);

        calculator.performInitialChecks();

        assertTrue("Expected textPane to be blank", calculator.getTextPaneWithoutNewLineCharacters().isBlank());
        assertTrue("Expected values[0] to be blank", calculator.getValues()[0].isBlank());
        assertTrue("Expected to be on the firstNumber", calculator.isFirstNumber());
        assertTrue("Expected dot button to be enabled", calculator.isDotPressed());
        assertTrue("Expected dot button to be enabled", calculator.getButtonDecimal().isEnabled());
    }

    @Test
    public void testInitialChecksElseIf2Clause()
    {
        calculator.values[0] = BLANK.getValue();
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
    public void testInitialChecksDoesNotEnterAnyClause()
    {
        calculator.getTextPane().setText(FIVE.getValue());
        calculator.performInitialChecks();
    }

    @Test
    public void testTextPaneContainsBadTextTrue()
    {
        calculator.getTextPane().setText(NOT_A_NUMBER.getValue());
        assertTrue("Expected textPane to contain error", calculator.textPaneContainsBadText());
    }

    @Test
    public void testTextPaneContainsBadTextFalse()
    {
        calculator.getTextPane().setText(FIVE.getValue());
        assertFalse("Expected textPane to contain valid text", calculator.textPaneContainsBadText());
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

        helpMe = new Calculator(VIEW_DATE).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Date Calculator Type:"+DIFFERENCE_BETWEEN_DATES, helpMe);

        helpMe = new Calculator(ADD_SUBTRACT_DAYS).getAboutCalculatorString();
        assertNotNull("About Calculator is not set on Date Calculator Type:"+ADD_SUBTRACT_DAYS, helpMe);

        helpMe = new Calculator(VIEW_CONVERTER).getAboutCalculatorString();
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
        assertTrue("Expected values[2] to be blank", calculator.getValues()[2].isBlank());

        calculator.performCopyAction(actionEvent);
        assertFalse("Expected values[2] to be 5", calculator.getValues()[2].isBlank());
        assertEquals("Expected values[2] to be 5", FIVE.getValue(), calculator.getValues()[2]);
    }

    @Test
    public void testPaste()
    {
        calculator.performPasteAction(actionEvent);
        assertTrue("Expected values[2] to be blank", calculator.getValues()[2].isBlank());

        calculator.values[2] = "10";
        calculator.getTextPane().setText(calculator.addNewLines() + FIVE.getValue());

        calculator.performPasteAction(actionEvent);
        assertEquals("Expected textPane to be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[0] to be 10", "10", calculator.getValues()[0]);

        calculator.setValuesPosition(1);
        calculator.performPasteAction(actionEvent);
        assertEquals("Expected textPane to be 10", "10", calculator.getTextPaneWithoutNewLineCharacters());
        assertEquals("Expected values[1] to be 10", "10", calculator.getValues()[0]);

    }

    @Test
    public void testClearingZeroesAtTheEnd() throws Exception
    {
        calculator.getValues()[0] = "15.0";
        assertEquals("Expected values[0] to be 15", "15", calculator.clearZeroesAndDecimalAtEnd(calculator.getValues()[0]));
    }

    @Test
    public void testAddCourtesyCommasAddsNoCommas()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue());
        calculator.getValues()[0] = "2";
        calculator.performNumberButtonAction(actionEvent);
        assertFalse("Expected no commas", calculator.getValues()[0].contains("_"));
        assertEquals("Expected values[0] to be 25", "25", calculator.getValues()[0]);
    }

    @Test
    public void testAddCourtesyCommasAdds1Comma4DigitsWholeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue());
        calculator.getValues()[0] = "999";
        calculator.performNumberButtonAction(actionEvent);
        assertTrue("Expected textPane to be 9,995", calculator.getTextPaneWithoutNewLineCharacters().contains(","));
        assertEquals("Expected values[0] to be 9995", "9995", calculator.getValues()[0]);
    }

    @Test
    public void testAddCourtesyCommasReturnsResultWithOneComma5DigitsWholeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(FIVE.getValue());
        calculator.getValues()[0] = "1234";
        calculator.performNumberButtonAction(actionEvent);
        assertTrue("Expected textPane to be 12,345", calculator.getTextPaneWithoutNewLineCharacters().contains(","));
        assertEquals("Expected values[0] to be 12345", "12345", calculator.getValues()[0]);
    }

    @Test
    public void testAddCourtesyCommasReturnsResultWithOneComma6DigitsWholeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SIX.getValue());
        calculator.getValues()[0] = "12345";
        calculator.performNumberButtonAction(actionEvent);
        assertTrue("Expected textPane to be 123,456", calculator.getTextPaneWithoutNewLineCharacters().contains(","));
        assertEquals("Expected values[0] to be 123456", "123456", calculator.getValues()[0]);
    }

    @Test
    public void testDeletingADigitAdjustsCourtesyCommas()
    {
        when(actionEvent.getActionCommand()).thenReturn(DELETE.getValue());
        calculator.getValues()[0] = "12345";
        calculator.getTextPane().setText("12345");
        calculator.performDeleteButtonAction(actionEvent);
        assertTrue("Expected textPane to be 1,234", calculator.getTextPaneWithoutNewLineCharacters().contains(","));
        assertEquals("Expected values[0] to be 1234", "1234", calculator.getValues()[0]);
    }

    @Test
    public void testAddCourtesyCommasReturnsResultWithTwoCommas7DigitsWholeNumber()
    {
        when(actionEvent.getActionCommand()).thenReturn(SEVEN.getValue());
        calculator.getValues()[0] = "123456";
        calculator.performNumberButtonAction(actionEvent);
        assertTrue("Expected textPane to be 1,234,567", calculator.getTextPaneWithoutNewLineCharacters().contains(","));
        assertEquals("Expected values[0] to be 1234567", "1234567", calculator.getValues()[0]);
    }

    @Test
    public void testCheckValueLength()
    {
        calculator.values[0] = "9999998";
        assertTrue("Expected max length to be met", calculator.checkValueLength());
        assertFalse("Expected max number to not be met", calculator.isMaximumValue());
    }

    @Test
    public void testValueAt0IsMinimumNumber()
    {
        calculator.values[0] = "0.0000001";
        assertTrue("Expected maximum number to be met", calculator.isMinimumValue());
    }

    @Test
    public void testValueAt1IsMinimumNumber()
    {
        calculator.values[1] = "0.0000001";
        assertTrue("Expected maximum number to be met", calculator.isMinimumValue());
    }

    @Test
    public void testValueIsMinimumNumber()
    {
        calculator.getValues()[0] = "0.0000001";
        assertTrue("Expected maximum number to be met", calculator.isMinimumValue(calculator.getValues()[0]));
    }

    @Test
    public void testValuesAt0IsMaximumNumber()
    {
        calculator.values[0] = "9999999";
        assertTrue("Expected maximum number to be met", calculator.isMaximumValue());
    }

    @Test
    public void testValuesAt1IsMaximumNumber()
    {
        calculator.values[1] = "9999999";
        assertTrue("Expected maximum number to be met", calculator.isMaximumValue());
    }

    @Test
    public void testValueIsMaximumNumber()
    {
        calculator.values[0] = "9999999";
        assertTrue("Expected maximum number to be met", calculator.isMaximumValue(calculator.getValues()[0]));
    }

    @Test
    public void testMenuOptionsForWindowsAdded()
    {
         doAnswer((InvocationOnMock invocationOnMock) ->
                 calculator.getRootPane()
         ).when(calculatorSpy).getRootPane();
         doReturn(false).when(calculatorSpy).isMacOperatingSystem();
         calculatorSpy.setupMenuBar();
         assertEquals("Expected View menu to have 3 options", 5, calculator.getViewMenu().getMenuComponents().length);
    }

    @Test
    public void testAboutCalculatorShowsWindowsInText()
    {
        doReturn(false).when(calculatorSpy).isMacOperatingSystem();
        String aboutCalculatorText = calculatorSpy.getAboutCalculatorString();
        assertTrue("Expected About Calculator Text to have Windows", aboutCalculatorText.contains(WINDOWS.getValue()));
        assertFalse("Expected About Calculator Text to not have Apple", aboutCalculatorText.contains(APPLE.getValue()));
    }

    @Test
    public void testGetLowestMemoryValuePosition()
    {
        calculator.getMemoryValues()[0] = ONE.getValue();
        calculator.getMemoryValues()[1] = TWO.getValue();
        calculator.getMemoryValues()[2] = THREE.getValue();
        calculator.getMemoryValues()[3] = FOUR.getValue();
        assertEquals("Expected lowest memoryPosition to be 0", 0, calculator.getLowestMemoryPosition());
    }

    @Test
    public void testShowMemoryValuesShowsNoMemoriesSaved()
    {
        when(actionEvent.getActionCommand()).thenReturn("Show Memory Values");
        calculator.performShowMemoriesAction(actionEvent);
        assertEquals("Expected basicHistoryTextPane to say No Memories Stored",
                "No Memories Stored",
                calculator.getBasicHistoryPaneTextWithoutNewLineCharacters());
    }

    @Test
    public void testClearBasicHistoryTextPane()
    {
        when(actionEvent.getActionCommand()).thenReturn("Clearing BasicHistoryTextPane");
        calculator.performClearHistoryTextPaneAction(actionEvent);
        assertEquals("Expected basicHistoryTextPane to be blank",
                BLANK.getValue(),
                calculator.getBasicHistoryPaneTextWithoutNewLineCharacters());
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
        assertEquals("Expected memories to be in basicHistoryTextPane",
                "Memories: [1], [2], [3], [4]",
                calculator.getBasicHistoryPaneTextWithoutNewLineCharacters()
                );
    }

}

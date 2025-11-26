package Calculators;

import Interfaces.CalculatorType;
import Interfaces.OSDetector;
import Panels.*;
import Parent.TestParent;
import Types.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import static Types.CalculatorBase.*;
import static Types.CalculatorByte.*;
import static Types.CalculatorConverterType.*;
import static Types.CalculatorView.*;
import static Types.DateOperation.*;
import static Types.Texts.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * CalculatorTests
 * <p>
 * This class tests the {@link Calculator} class.
 *
 * @author Michael Ball
 * @version 4.0
 */
@ExtendWith(MockitoExtension.class)
class CalculatorTests extends TestParent
{
    static { System.setProperty("appName", CalculatorTests.class.getSimpleName()); }
    private static final Logger LOGGER = LogManager.getLogger(CalculatorTests.class.getSimpleName());

    @Mock
    public OSDetector systemDetector;
    @Mock
    public ActionEvent actionEvent;

    @BeforeAll
    static void beforeAll()
    { mocks = MockitoAnnotations.openMocks(CalculatorTests.class); }

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(() -> {
            try
            {
                LOGGER.info("Starting test");
                calculator = spy(new Calculator());
                Preferences.userNodeForPackage(Calculator.class).clear(); // remove keys
                calculator.setSystemDetector(systemDetector);
            }
            catch (Exception ignored) {}
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(() -> {
            try
            {
                LOGGER.info("Test complete. Closing the calculator...");
                calculator.setVisible(false);
                calculator.dispose();
            }
            catch (Exception e)
            {
                LOGGER.error("Error closing calculator: {}", e.getMessage(), e);
            }
        });
    }

    @AfterAll
    static void afterAll()
    {
        LOGGER.info("Finished running {}", CalculatorTests.class.getSimpleName());
        try
        {
            if (mocks != null)
            { mocks.close(); }
        }
        catch (Exception e)
        { LOGGER.error("Error closing mocks: {}", e.getMessage()); }
    }

    /*############## Test Constructors ##################*/
    @ParameterizedTest
    @DisplayName("Create A Specific Calculator")
    @MethodSource("createCalculatorCases")
    void createACalculator(Class<?> panelInstance, CalculatorView calculatorView, CalculatorBase calculatorBase,
                           CalculatorByte calculatorByte, CalculatorConverterType converterType, DateOperation dateOperation)
            throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        switch (calculatorView)
        {
            case VIEW_BASIC -> calculator = new Calculator(calculatorView);
            case VIEW_PROGRAMMER,
                 VIEW_SCIENTIFIC -> calculator = new Calculator(calculatorView, calculatorBase, calculatorByte);
            case VIEW_DATE -> calculator = new Calculator(dateOperation);
            case VIEW_CONVERTER -> calculator = new Calculator(converterType);
            default -> calculator = new Calculator();
        }
        postConstructCalculator();
        assertTrue(calculator.isVisible(), "Cannot see calculator for " + calculatorView);
        assertInstanceOf(panelInstance, calculator.getCurrentPanel(), "Expected currentPanel to be instanceOf " + panelInstance.getSimpleName());
        assertEquals(calculatorView, calculator.getCalculatorView(), "Expected CalculatorView to be " + calculatorView);
        assertEquals(calculatorBase, calculator.getCalculatorBase(), "Expected CalculatorBase to be " + calculatorBase);
        assertEquals(calculatorByte, calculator.getCalculatorByte(), "Expected CalculatorByte to be " + calculatorByte);
        assertEquals(converterType, calculator.getConverterType(), "Expected ConverterType to be " + converterType);
        assertEquals(dateOperation, calculator.getDateOperation(), "Expected DateOperation to be " + dateOperation);
        assertNotNull(calculator.getCalculatorIcon(), "Expected calculator image");
        assertNotNull( calculator.getMacIcon(), "Expected mac icon");
        assertNotNull(calculator.getWindowsIcon(), "Expected windows icon");
        assertNull(calculator.getBlankIcon(), "Expected no icon");
    }
    private static Stream<Arguments> createCalculatorCases()
    {
        return Stream.of(
                Arguments.of(BasicPanel.class, VIEW_BASIC, BASE_DECIMAL, BYTE_BYTE, AREA, DIFFERENCE_BETWEEN_DATES),

                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_BINARY, BYTE_BYTE, AREA, DIFFERENCE_BETWEEN_DATES),
                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_BINARY, BYTE_WORD, AREA, DIFFERENCE_BETWEEN_DATES),
                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_BINARY, BYTE_DWORD, AREA, DIFFERENCE_BETWEEN_DATES),
                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_BINARY, BYTE_QWORD, AREA, DIFFERENCE_BETWEEN_DATES),

                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_OCTAL, BYTE_BYTE, AREA, DIFFERENCE_BETWEEN_DATES),
                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_OCTAL, BYTE_WORD, AREA, DIFFERENCE_BETWEEN_DATES),
                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_OCTAL, BYTE_DWORD, AREA, DIFFERENCE_BETWEEN_DATES),
                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_OCTAL, BYTE_QWORD, AREA, DIFFERENCE_BETWEEN_DATES),

                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_DECIMAL, BYTE_BYTE, AREA, DIFFERENCE_BETWEEN_DATES),
                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_DECIMAL, BYTE_WORD, AREA, DIFFERENCE_BETWEEN_DATES),
                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_DECIMAL, BYTE_DWORD, AREA, DIFFERENCE_BETWEEN_DATES),
                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_DECIMAL, BYTE_QWORD, AREA, DIFFERENCE_BETWEEN_DATES),

                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_HEXADECIMAL, BYTE_BYTE, AREA, DIFFERENCE_BETWEEN_DATES),
                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_HEXADECIMAL, BYTE_WORD, AREA, DIFFERENCE_BETWEEN_DATES),
                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_HEXADECIMAL, BYTE_DWORD, AREA, DIFFERENCE_BETWEEN_DATES),
                Arguments.of(ProgrammerPanel.class, VIEW_PROGRAMMER, BASE_HEXADECIMAL, BYTE_QWORD, AREA, DIFFERENCE_BETWEEN_DATES),

                Arguments.of(ScientificPanel.class, VIEW_SCIENTIFIC, BASE_DECIMAL, BYTE_BYTE, AREA, DIFFERENCE_BETWEEN_DATES),

                Arguments.of(DatePanel.class, VIEW_DATE, BASE_DECIMAL, BYTE_BYTE, AREA, DIFFERENCE_BETWEEN_DATES),
                Arguments.of(DatePanel.class, VIEW_DATE, BASE_DECIMAL, BYTE_BYTE, AREA, ADD_SUBTRACT_DAYS),

                Arguments.of(ConverterPanel.class, VIEW_CONVERTER, BASE_DECIMAL, BYTE_BYTE, AREA, DIFFERENCE_BETWEEN_DATES),
                Arguments.of(ConverterPanel.class, VIEW_CONVERTER, BASE_DECIMAL, BYTE_BYTE, ANGLE, DIFFERENCE_BETWEEN_DATES)
        );
    }

    @ParameterizedTest
    @DisplayName("Create A Calculator By View")
    @MethodSource("provideCalculatorViews")
    void createACalculatorByView(CalculatorView calculatorView) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        if (calculatorView != null)
            calculator = new Calculator(calculatorView);
        else {
            calculator = new Calculator();
            calculatorView = calculator.getCalculatorView();
        }
        postConstructCalculator();
        assertTrue(calculator.isVisible(), "Cannot see calculator for " + calculatorView.getValue());
        assertEquals(calculatorView, calculator.getCalculatorView(), "Expected CalculatorView to be " + calculatorView.getValue());
        assertNotNull(calculator.getCalculatorIcon(), "Expected calculator image");
        assertNotNull( calculator.getMacIcon(), "Expected mac icon");
        assertNotNull(calculator.getWindowsIcon(), "Expected windows icon");
        assertNull(calculator.getBlankIcon(), "Expected no icon");
    }
    private static Stream<Arguments> provideCalculatorViews()
    {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(VIEW_BASIC),
                Arguments.of(VIEW_PROGRAMMER),
                Arguments.of(VIEW_SCIENTIFIC),
                Arguments.of(VIEW_DATE),
                Arguments.of(VIEW_CONVERTER)
        );
    }

    /*############## Test Menu Actions ##################*/
    @ParameterizedTest
    @DisplayName("Test Changing Styles")
    @MethodSource("changeStyleCases")
    void changeCalculatorStyle(String style, boolean isOSWindows) throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        when(systemDetector.isWindows()).thenReturn(isOSWindows);
        Calculator calculator = new Calculator(VIEW_BASIC, BASE_DECIMAL, BYTE_BYTE,
                AREA, DIFFERENCE_BETWEEN_DATES, systemDetector);
        postConstructCalculator();
        // find the matching menu item and ensure actionEvent returns it as the source
        JMenuItem styleMenuItem = (JMenuItem) Arrays.stream(calculator.getStyleMenu()
                        .getMenuComponents())
                .filter(menuItem -> style.equals(menuItem.getName()))
                .findFirst().orElse(null);
        if (styleMenuItem != null)
        {
            when(actionEvent.getSource()).thenReturn(styleMenuItem);

            calculator.performStyleMenuAction(actionEvent);
            assertEquals(style, calculator.getCalculatorStyle(), "Expected style to be " + style);
        } else {
            assertEquals(METAL, calculator.getCalculatorStyle(), "Expected style to be " + METAL);
        }
    }
    private static Stream<Arguments> changeStyleCases()
    {
        return Stream.of(
                Arguments.of(APPLE, true),
                Arguments.of(APPLE, false),
                Arguments.of(GTK, true),
                Arguments.of(GTK, false),
                Arguments.of(METAL, true),
                Arguments.of(METAL, false),
                Arguments.of(MOTIF, true),
                Arguments.of(MOTIF, false),
                Arguments.of(SYSTEM, true),
                Arguments.of(SYSTEM, false),
                Arguments.of(WINDOWS, true),
                Arguments.of(WINDOWS, false)
        );
    }

    @ParameterizedTest
    @DisplayName("Test Changing Views")
    @MethodSource("changeViewsCases")
    void changeCalculatorViews(Calculator calcInTest, CalculatorType selectedView)
    {
        calculator = calcInTest;
        postConstructCalculator();
        calcInTest.performViewMenuAction(actionEvent, selectedView);

        String calculatorView = selectedView.getName();
        if (selectedView.getClass().isInstance(CalculatorConverterType.class))
        {
            calculatorView = selectedView.getName();
        }
        assertEquals(selectedView.getName(), calculatorView, "Expected CalculatorView to be " + selectedView);
    }
    private static Stream<Arguments> changeViewsCases() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        return Stream.of(
                // From BASIC
                Arguments.of(new Calculator(VIEW_BASIC), VIEW_BASIC),
                Arguments.of(new Calculator(VIEW_BASIC), VIEW_PROGRAMMER),
                Arguments.of(new Calculator(VIEW_BASIC), VIEW_SCIENTIFIC),
                Arguments.of(new Calculator(VIEW_BASIC), VIEW_DATE),
                Arguments.of(new Calculator(VIEW_BASIC), AREA),
                Arguments.of(new Calculator(VIEW_BASIC), ANGLE),
                // From PROGRAMMER
                Arguments.of(new Calculator(VIEW_PROGRAMMER), VIEW_BASIC),
                Arguments.of(new Calculator(VIEW_PROGRAMMER), VIEW_PROGRAMMER),
                Arguments.of(new Calculator(VIEW_PROGRAMMER), VIEW_SCIENTIFIC),
                Arguments.of(new Calculator(VIEW_PROGRAMMER), VIEW_DATE),
                Arguments.of(new Calculator(VIEW_PROGRAMMER), AREA),
                Arguments.of(new Calculator(VIEW_PROGRAMMER), ANGLE),
                // From SCIENTIFIC
                Arguments.of(new Calculator(VIEW_SCIENTIFIC), VIEW_BASIC),
                Arguments.of(new Calculator(VIEW_SCIENTIFIC), VIEW_PROGRAMMER),
                Arguments.of(new Calculator(VIEW_SCIENTIFIC), VIEW_SCIENTIFIC),
                Arguments.of(new Calculator(VIEW_SCIENTIFIC), VIEW_DATE),
                Arguments.of(new Calculator(VIEW_SCIENTIFIC), AREA),
                Arguments.of(new Calculator(VIEW_SCIENTIFIC), ANGLE),
                // From DATE
                Arguments.of(new Calculator(VIEW_DATE), VIEW_BASIC),
                Arguments.of(new Calculator(VIEW_DATE), VIEW_PROGRAMMER),
                Arguments.of(new Calculator(VIEW_DATE), VIEW_SCIENTIFIC),
                Arguments.of(new Calculator(VIEW_DATE), VIEW_DATE),
                Arguments.of(new Calculator(VIEW_DATE), AREA),
                Arguments.of(new Calculator(VIEW_DATE), ANGLE),
                // From CONVERTER
                Arguments.of(new Calculator(VIEW_CONVERTER), VIEW_BASIC),
                Arguments.of(new Calculator(VIEW_CONVERTER), VIEW_PROGRAMMER),
                Arguments.of(new Calculator(VIEW_CONVERTER), VIEW_SCIENTIFIC),
                Arguments.of(new Calculator(VIEW_CONVERTER), VIEW_DATE),
                Arguments.of(new Calculator(VIEW_CONVERTER), AREA),
                Arguments.of(new Calculator(VIEW_CONVERTER), ANGLE)
        );
    }

    @ParameterizedTest
    @DisplayName("Test Copy Action")
    @MethodSource("copyCases")
    void testCopyMenuAction(String currentTextPaneValue)
    {
        when(actionEvent.getActionCommand())
                .thenReturn(COPY);
        postConstructCalculator();
        calculator.appendTextToPane(currentTextPaneValue);

        calculator.performCopyAction(actionEvent);

        // Get clipboard copy value
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        String clipboardText = EMPTY;
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                Object data = contents.getTransferData(DataFlavor.stringFlavor);
                if (data instanceof String) {
                    clipboardText = (String) data;
                } else {
                    throw new CalculatorError("Clipboard data is not a string");
                }
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
        assertEquals(currentTextPaneValue, clipboardText, "Clipboard value is incorrect");
    }
    private static Stream<Arguments> copyCases()
    {
        return Stream.of(
                Arguments.of(EMPTY),
                Arguments.of("1,234.56")
        );
    }

    @ParameterizedTest
    @DisplayName("Test Paste Action")
    @MethodSource("pasteCases")
    void testPasteMenuAction(String currentTextPaneValue)
    {
        when(actionEvent.getActionCommand())
                .thenReturn(COPY)
                .thenReturn(PASTE);
        postConstructCalculator();
        calculator.appendTextToPane(currentTextPaneValue);
        calculator.performCopyAction(actionEvent);
        calculator.clearTextInTextPane();
        calculator.performPasteAction(actionEvent);

        assertEquals(currentTextPaneValue, calculator.getTextPaneValue(), "TextPane value is incorrect");
    }
    private static Stream<Arguments> pasteCases()
    {
        return Stream.of(
                Arguments.of(EMPTY),
                Arguments.of("1,234.56")
        );
    }

    @ParameterizedTest
    @DisplayName("Test Clear History Action")
    @MethodSource("clearHistoryCases")
    void testClearHistoryMenuAction(String historyCase)
    {
        when(actionEvent.getActionCommand())
                .thenReturn(CLEAR_HISTORY);
        postConstructCalculator();
        calculator.getHistoryTextPane().setText(historyCase);

        calculator.performClearHistoryAction(actionEvent);

        assertTrue(calculator.getHistoryTextPane().getText().isEmpty(), "History text is incorrect");
    }
    private static Stream<Arguments> clearHistoryCases()
    {
        return Stream.of(
                Arguments.of(EMPTY),
                Arguments.of("(1) Result: 1\n(2) Result: 2\n(3) Result: 3")
        );
    }

    @ParameterizedTest
    @DisplayName("Test Show Memories Action")
    @MethodSource("showMemoriesCases")
    void testShowMemories(String memoryValue1, String memoryValue2)
    {
        when(actionEvent.getActionCommand())
                .thenReturn(SHOW_MEMORIES);
        postConstructCalculator();
        calculator.storeValueInMemory(memoryValue1);
        calculator.storeValueInMemory(memoryValue2);

        String previousHistory = calculator.getHistoryTextPane().getText();
        calculator.performShowMemoriesAction(actionEvent);

        String actualHistory = calculator.getHistoryTextPane().getText();
        if (calculator.isMemoryValuesEmpty())
        {
            assertEquals(previousHistory+NEWLINE+NO_MEMORIES_STORED, actualHistory,
                    "History text is incorrect when no memories stored");
        }
        else
        {
            assertEquals(previousHistory+NEWLINE+MEMORIES+" ["+memoryValue1+"], ["+memoryValue2+"]", actualHistory,
                    "History text is incorrect when memories are stored");
        }
    }
    private static Stream<Arguments> showMemoriesCases()
    {
        return Stream.of(
                Arguments.of(EMPTY, EMPTY),
                Arguments.of("1", "2"),
                Arguments.of("1.2", "3")
        );
    }

    // Requires manual intervention to close help panel
    @ParameterizedTest
    @DisplayName("Test Calculator's Help Action")
    @MethodSource("getShowHelpCases")
    void testShowingHelpShowsText(Calculator calcInTest)
    {
        calculator = calcInTest;
        postConstructCalculator();
        calculator.setHelpString(calculator.getHelpString());
        assertDoesNotThrow(() -> calculator.performShowHelpAction(actionEvent));
    }
    private static Stream<Arguments> getShowHelpCases() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        return Stream.of(
                Arguments.of(new Calculator(VIEW_BASIC)),
                Arguments.of(new Calculator(VIEW_PROGRAMMER)),
                Arguments.of(new Calculator(VIEW_SCIENTIFIC)),
                Arguments.of(new Calculator(DIFFERENCE_BETWEEN_DATES)),
                Arguments.of(new Calculator(ADD_SUBTRACT_DAYS)),
                Arguments.of(new Calculator(AREA)),
                Arguments.of(new Calculator(ANGLE))
        );
    }

    // Requires manual intervention to close about calculator panel
    @ParameterizedTest
    @DisplayName("Test About Calculator Action")
    @MethodSource("getAboutCalculatorCases")
    void testAboutCalculatorOpensAboutCalculatorPanel(boolean isOSMac)
    {
        postConstructCalculator();
        when(systemDetector.isMac()).thenReturn(isOSMac);
        calculator.performAboutCalculatorAction(actionEvent);
        if (isOSMac)
            assertTrue(calculator.getAboutCalculatorString().startsWith("<html> "+APPLE), "Expected About Calculator Text to have Apple icon");
        else
            assertTrue(calculator.getAboutCalculatorString().startsWith("<html> "+WINDOWS), "Expected About Calculator Text to have Window icon");
    }
    private static Stream<Arguments> getAboutCalculatorCases()
    {
        return Stream.of(
                Arguments.of(true),
                Arguments.of(false)
        );
    }

    /*############## Test Button Actions ##################*/
    @Test
    void testDeletingADigitAdjustsCourtesyCommas()
    {
        postConstructCalculator();
        when(actionEvent.getActionCommand()).thenReturn(DELETE);
        calculator.values[0] = "12345";
        calculator.getTextPane().setText("12345");
        calculator.performDeleteButtonAction(actionEvent);
        assertTrue(calculator.getTextPaneValue().contains(","), "Expected textPane to be 1,234");
        assertEquals("1234", calculator.values[0], "Expected values[0] to be 1234");
    }

    @Test
    void testOpeningHistory()
    {
        postConstructCalculator();
        when(actionEvent.getActionCommand()).thenReturn(HISTORY_OPEN);
        calculator.performHistoryAction(actionEvent);

        assertEquals(HISTORY_OPEN, calculator.getButtonHistory().getText(), "Expected History Button to show " + HISTORY_OPEN);
    }

    @Test
    void testClosingHistory()
    {
        postConstructCalculator();
        when(actionEvent.getActionCommand()).thenReturn(HISTORY_OPEN);
        when(actionEvent.getActionCommand()).thenReturn(HISTORY_CLOSED);
        calculator.performHistoryAction(actionEvent);
        calculator.performHistoryAction(actionEvent);

        assertEquals(HISTORY_CLOSED, calculator.getButtonHistory().getText(), "Expected History Button to show " + HISTORY_CLOSED);
    }

    /*############## Test Helper Methods ##################*/
    @ParameterizedTest
    @DisplayName("Test ConvertToPositive()")
    @CsvSource({
            "-5.02, 5.02",
            "-123456, 123456",
            "-0.0001, 0.0001",
            "-6, 6"
    })
    void testConvertToPositive(String valueToTest, String expectedValue)
    {
        postConstructCalculator();
        String actualValue = calculator.convertToPositive(valueToTest);
        assertEquals(expectedValue, actualValue, "Number is not positive");
        assertTrue(calculator.isPositiveNumber(actualValue), "Number is not positive");
        assertFalse(calculator.isNegativeNumber(actualValue), "Number is still negative");
    }

    @ParameterizedTest
    @DisplayName("Test ConvertToNegative()")
    @CsvSource({
            "5.02, -5.02",
            "123456, -123456",
            "0.0001, -0.0001",
            "6, -6"
    })
    void testConvertToNegative(String valueToTest, String expectedValue)
    {
        postConstructCalculator();
        String actualValue = calculator.convertToNegative(valueToTest);
        assertEquals(expectedValue, actualValue, "Number is not negative");
        assertTrue(calculator.isNegativeNumber(actualValue), "Number is not negative");
        assertFalse(calculator.isPositiveNumber(actualValue), "Number is still positive");
    }

    @ParameterizedTest
    @DisplayName("Test IsFractionalNumber()")
    @CsvSource({
            "-5.02, true",
            "0.0001, true",

            "-123456, false",
            "123, false"
    })
    void testIsFractionalNumber(String valueToTest, boolean expectedValue)
    {
        postConstructCalculator();
        boolean actualValue = calculator.isFractionalNumber(valueToTest);
        assertEquals(expectedValue, actualValue, "Expected fractional check to be " + expectedValue);
    }

    @ParameterizedTest
    @DisplayName("Test FinishedObtainingFirstNumber()")
    @MethodSource("firstNumberCases")
    void testFinishedObtainingFirstNumber(boolean finishedCase)
    {
        postConstructCalculator();
        calculator.values[calculator.valuesPosition] = FIVE; // has a value currently
        calculator.finishedObtainingFirstNumber(finishedCase);

        if (finishedCase)
        {
            assertEquals(1, calculator.getValuesPosition(), "Expected valuesPosition to be 1");
        }
        else
        {
            assertEquals(0, calculator.getValuesPosition(), "Expected valuesPosition to be 0");
        }
    }
    private static Stream<Arguments> firstNumberCases()
    {
        return Stream.of(
                Arguments.of(false),
                Arguments.of(true)
        );
    }

    @Test
    @DisplayName("Test ResetValues()")
    void testResetValues()
    {
        postConstructCalculator();
        calculator.values[0] = ONE + FIVE;
        calculator.values[1] = TWO + SIX;
        calculator.values[2] = FIVE;
        // values[3] could or could not already have a value

        assertEquals("15", calculator.values[0], "Expected values[0] to be 15");
        assertEquals("26", calculator.values[1], "Expected values[1] to be 26");
        assertEquals("5", calculator.values[2], "Expected values[2] to be 5");

        calculator.resetValues();

        assertTrue(calculator.values[0].isEmpty(), "Expected first number to be blank");
        assertTrue(calculator.values[1].isEmpty(), "Expected second number to be blank");
        assertTrue(calculator.values[2].isEmpty(), "Expected operator to be blank");
        assertTrue(calculator.values[3].isEmpty(), "Expected result to be blank");
    }

    @ParameterizedTest
    @DisplayName("Test ClearButtonActions()")
    @MethodSource("clearButtonActionsCases")
    void testClearNumberButtonActions(java.util.List<JButton> buttonsToTest)
    {
        postConstructCalculator();
        buttonsToTest.forEach(btn ->
            assertSame(1, btn.getActionListeners().length, "Expecting only 1 action on " + btn.getName()));

        calculator.clearButtonActions(buttonsToTest);

        buttonsToTest.forEach(numberButton ->
            assertSame(0, numberButton.getActionListeners().length, "Expecting no actions on " + numberButton.getName()));
    }
    private static Stream<Arguments> clearButtonActionsCases() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        Calculator testCalc = new Calculator();
        return Stream.of(
                Arguments.of(testCalc.getNumberButtons()),
                Arguments.of(testCalc.getCommonButtons()),
                Arguments.of(testCalc.getAllMemoryPanelButtons())
        );
    }

    @ParameterizedTest
    @DisplayName("Test IsMemoryValuesEmpty()")
    @MethodSource("memoryValuesCases")
    void testIsMemoryValuesEmpty(String memoryValues)
    {
        postConstructCalculator();
        if (memoryValues.equals(EMPTY)) assertTrue(calculator.isMemoryValuesEmpty());
        else
        {
            when(actionEvent.getActionCommand())
                    .thenReturn(FIVE)
                    .thenReturn(MEMORY_STORE);
            calculator.performNumberButtonAction(actionEvent);
            calculator.performMemoryStoreAction(actionEvent);
            assertFalse(calculator.isMemoryValuesEmpty());
        }
    }
    private static Stream<Arguments> memoryValuesCases()
    {
        return Stream.of(
                Arguments.of(EMPTY),
                Arguments.of(FIVE)
        );
    }

    @Test
    void testGetLowestMemoryValuePosition()
    {
        postConstructCalculator();
        calculator.getMemoryValues()[0] = ONE;
        calculator.getMemoryValues()[1] = TWO;
        calculator.getMemoryValues()[2] = THREE;
        calculator.getMemoryValues()[3] = FOUR;
        assertEquals(0, calculator.getLowestMemoryPosition(), "Expected lowest memoryPosition to be 0");
    }

    @ParameterizedTest
    @DisplayName("Test InitialChecks()")
    @MethodSource("initialChecksCases")
    void testInitialChecksIfClause(String textPaneValue, boolean expectedIssues)
    {
        postConstructCalculator();
        calculator.appendTextToPane(textPaneValue, true);
        assertEquals(textPaneValue, calculator.getTextPaneValue(), "Expected error message in textPane");

        boolean issuesFound = calculator.performInitialChecks();

        assertEquals(expectedIssues, issuesFound, "Expected issues found to be true");
    }
    private static Stream<Arguments> initialChecksCases()
    {
        return Stream.of(
                Arguments.of(NOT_A_NUMBER, true),
                Arguments.of(FIVE, false)
        );
    }

    @ParameterizedTest
    @DisplayName("Test TextPaneContainsBadText()")
    @MethodSource("textPaneBadTextCases")
    void testTextPaneContainsBadTextTrue(String textPaneValue, boolean expectedContainsBadText)
    {
        postConstructCalculator();
        calculator.appendTextToPane(textPaneValue);
        assertEquals(expectedContainsBadText, calculator.textPaneContainsBadText(),"Expected textPane to contain error");
    }
    private static Stream<Arguments> textPaneBadTextCases()
    {
        return Stream.of(
                // Bad text
                Arguments.of(NOT_A_NUMBER, true),
                Arguments.of(NUMBER_TOO_BIG, true),
                Arguments.of(ENTER_A_NUMBER, true),
                Arguments.of(ONLY_POSITIVES, true),
                Arguments.of(ERROR, true),
                Arguments.of(INFINITY, true),
                // No bad text
                Arguments.of(FIVE, false)
        );
    }

    @ParameterizedTest
    @DisplayName("Test GetNumberOnLeftSideOfDecimal() and GetNumberOnRightSideOfDecimal()")
    @CsvSource({
            "123.456, 123, 456",
            "0.001, 0, 001",
            "9999.0001, 9999, 0001",
            "100, 100, ''",
            "0, 0, ''"
    })
    void testGetTheNumberToTheLeftAndRightOfTheDecimal(String numberToTest, String expectedLeftSide, String expectedRightSide)
    {
        postConstructCalculator();
        assertEquals(expectedLeftSide, calculator.getNumberOnLeftSideOfDecimal(numberToTest));
        assertEquals(expectedRightSide, calculator.getNumberOnRightSideOfDecimal(numberToTest));
    }

    @Test
    void testClearBasicHistoryTextPane()
    {
        postConstructCalculator();
        when(actionEvent.getActionCommand()).thenReturn("Clearing BasicHistoryTextPane");
        calculator.performClearHistoryAction(actionEvent);
        assertEquals(EMPTY,
                calculator.getHistoryPaneTextWithoutNewLineCharacters(),
                "Expected HistoryTextPane to be blank");
    }

    @ParameterizedTest()
    @CsvSource({
        // No Commas Added
        "1.25, 1.25",
        "123, 123",
        "-123, -123",
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
        postConstructCalculator();
        when(actionEvent.getActionCommand())
                .thenReturn(String.valueOf(input.charAt(input.length()-1)));
        calculator.valuesPosition = 0;
        calculator.setNegativeNumber(calculator.isNegativeNumber(input));
        calculator.values[calculator.valuesPosition] = input.substring(0, input.length()-1);
        calculator.appendTextToPane(calculator.values[calculator.valuesPosition]);
        calculator.performNumberButtonAction(actionEvent);
        assertEquals(expecting, calculator.getTextPaneValue(), "Expected textPane to be " + expecting);
        assertEquals(input, calculator.getValueAt(), "Expected values[0] to be " + expecting);
    }

    @ParameterizedTest
    @DisplayName("Convert FromBaseToBase()")
    @CsvSource({
            "00000011, 3, BASE_BINARY, BYTE_BYTE, BASE_DECIMAL",
            "0000000000000011, 3, BASE_BINARY, BYTE_WORD, BASE_DECIMAL",
            "00000000000000000000000000000011, 3, BASE_BINARY, BYTE_DWORD, BASE_DECIMAL",
            "0000000000000000000000000000000000000000000000000000000000000011, 3, BASE_BINARY, BYTE_QWORD, BASE_DECIMAL",
    })
    void testConvertFromBaseToBase(String convertValue, String expect, CalculatorBase fromBase,
                                   CalculatorByte calculatorByte, CalculatorBase toBase)
    {
        calculator.setCalculatorBase(fromBase);
        calculator.setCalculatorByte(calculatorByte);

        calculator.appendTextToPane(convertValue);
        String converted = calculator.convertFromBaseToBase(fromBase, toBase, calculator.getTextPaneValue());
        assertEquals(expect, converted, "Expected " + expect);
    }

    @ParameterizedTest
    @DisplayName("Test EndsWithOperator()")
    @MethodSource("endsWithOperatorCases")
    void testEndsWithOperator(String valueToTest, boolean expectedEndsWithOperator)
    {
        postConstructCalculator();
        // Because the endsWithOperator method will use the current panels buttons,
        // setting to a different view will default to check all operators
        calculator.performViewMenuAction(actionEvent, VIEW_DATE); // any view should do

        boolean actualEndsWithOperator = calculator.endsWithOperator(valueToTest);
        assertEquals(expectedEndsWithOperator, actualEndsWithOperator,
                "Expected " + valueToTest + " to be " + expectedEndsWithOperator);
    }
    private static Stream<Arguments> endsWithOperatorCases()
    {
        return Stream.of(
                Arguments.of("123 "+ADDITION, true),
                Arguments.of("456 "+SUBTRACTION, true),
                Arguments.of("789 "+MULTIPLICATION, true),
                Arguments.of("100 "+DIVISION, true),
                Arguments.of("543 "+OR, true),
                Arguments.of("382 "+XOR, true),
                Arguments.of("321 "+AND, true),
                Arguments.of("123.45 "+MODULUS, true),
                Arguments.of("123", false),
                Arguments.of("123.", false),
                Arguments.of("456.78", false),
                Arguments.of("-999", false)
        );
    }

    // TODO: These min/max tests could be improved to test edge cases better
    @Test
    void testValueAt0IsMinimumNumber()
    {
        postConstructCalculator();
        calculator.values[0] = "-9999999";
        assertTrue(calculator.isMinimumValue(), "Expected maximum number to be met");
    }

    @Test
    void testValueAt1IsMinimumNumber()
    {
        postConstructCalculator();
        calculator.valuesPosition = 1;
        calculator.values[1] = "-9999999"; //"0.0000001";
        assertTrue(calculator.isMinimumValue(), "Expected maximum number to be met");
    }

    @Test
    void testValueIsMinimumNumber()
    {
        postConstructCalculator();
        calculator.values[0] = "-9999999"; //"0.0000001";
        assertTrue(calculator.isMinimumValue(calculator.values[0]), "Expected maximum number to be met");
    }

    @Test
    void testValuesAt0IsMaximumNumber()
    {
        postConstructCalculator();
        calculator.values[0] = "9999999";
        assertTrue(calculator.isMaximumValue(), "Expected maximum number to be met");
    }

    @Test
    void testValuesAt1IsMaximumNumber()
    {
        postConstructCalculator();
        calculator.valuesPosition = 1;
        calculator.values[calculator.valuesPosition] = "9999999";
        assertTrue(calculator.isMaximumValue(), "Expected maximum number to be met");
    }

    @Test
    void testValueIsMaximumNumber()
    {
        postConstructCalculator();
        calculator.values[0] = "9999999";
        assertTrue(calculator.isMaximumValue(calculator.getValueAt(0)), "Expected maximum number to be met");
    }

}

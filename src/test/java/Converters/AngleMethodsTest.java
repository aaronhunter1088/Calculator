package Converters;

import Calculators.Calculator;
import Parent.TestParent;
import Types.CalculatorConverterType;
import Types.CalculatorConverterUnits;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

/**
 * AngleMethodsTest
 * <p>
 * This class tests the {@link AngleMethods} class.
 *
 * @author Michael Ball
 * @version 4.0
 */
public class AngleMethodsTest extends TestParent
{
    private static final Logger LOGGER = LogManager.getLogger(AngleMethodsTest.class);

    static {
        System.setProperty("appName", AngleMethodsTest.class.getSimpleName());
    }

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(() -> {
            try {
                calculator = spy(new Calculator());
                Preferences.userNodeForPackage(Calculator.class).clear();
                calculator.setSystemDetector(systemDetector);
                calculator.performViewMenuAction(actionEvent, CalculatorConverterType.ANGLE);
            }
            catch (Exception ignored) {}
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(() -> {
            try {
                calculator.setVisible(false);
                calculator.dispose();
            }
            catch (Exception ignored) {}
        });
    }

    // -------------------------------------------------------------------------
    // convertingDegreesToRadians
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link AngleMethods#convertingDegreesToRadians(Calculator)}
     * @param input    argument 1, the degree value to convert (set in textField1)
     * @param expected argument 2, the expected result in radians
     */
    @ParameterizedTest
    @MethodSource("convertingDegreesToRadiansTestCases")
    @DisplayName("Test convertingDegreesToRadians")
    void testConvertingDegreesToRadians(String input, double expected)
            throws InterruptedException, InvocationTargetException
    {
        LOGGER.info("Testing convertingDegreesToRadians with input: {}", input);
        SwingUtilities.invokeAndWait(() -> {
            calculator.getConverterPanel().getTextField1().setText(input);
            calculator.getConverterPanel().setIsTextField1Selected(true);
        });
        double result = AngleMethods.convertingDegreesToRadians(calculator);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link AngleMethods#convertingDegreesToRadians(Calculator)} method.
     * Expected values are computed with the same BigDecimal rounding used by the method.
     * @return various degree inputs and their expected radian outputs
     */
    private static Stream<Arguments> convertingDegreesToRadiansTestCases()
    {
        return Stream.of(
                Arguments.of("0",   rad(0)),
                Arguments.of("45",  rad(45)),
                Arguments.of("90",  rad(90)),
                Arguments.of("180", rad(180)),
                Arguments.of("270", rad(270)),
                Arguments.of("360", rad(360))
        );
    }

    // -------------------------------------------------------------------------
    // convertingRadiansToDegrees
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link AngleMethods#convertingRadiansToDegrees(Calculator)}
     * @param input    argument 1, the radian value to convert (set in textField1)
     * @param expected argument 2, the expected result in degrees
     */
    @ParameterizedTest
    @MethodSource("convertingRadiansToDegreesTestCases")
    @DisplayName("Test convertingRadiansToDegrees")
    void testConvertingRadiansToDegrees(String input, double expected)
            throws InterruptedException, InvocationTargetException
    {
        LOGGER.info("Testing convertingRadiansToDegrees with input: {}", input);
        SwingUtilities.invokeAndWait(() -> {
            calculator.getConverterPanel().getTextField1().setText(input);
            calculator.getConverterPanel().setIsTextField1Selected(true);
        });
        double result = AngleMethods.convertingRadiansToDegrees(calculator);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link AngleMethods#convertingRadiansToDegrees(Calculator)} method.
     * @return various radian inputs and their expected degree outputs
     */
    private static Stream<Arguments> convertingRadiansToDegreesTestCases()
    {
        return Stream.of(
                Arguments.of("0",      deg(0)),
                Arguments.of("0.5",    deg(0.5)),
                Arguments.of("1",      deg(1)),
                Arguments.of("1.5708", deg(1.5708)),
                Arguments.of("3.1416", deg(3.1416))
        );
    }

    // -------------------------------------------------------------------------
    // convertingDegreesToGradians
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link AngleMethods#convertingDegreesToGradians(Calculator)}
     * @param input    argument 1, the degree value to convert (set in textField1)
     * @param expected argument 2, the expected result in gradians
     */
    @ParameterizedTest
    @MethodSource("convertingDegreesToGradiansTestCases")
    @DisplayName("Test convertingDegreesToGradians")
    void testConvertingDegreesToGradians(String input, double expected)
            throws InterruptedException, InvocationTargetException
    {
        LOGGER.info("Testing convertingDegreesToGradians with input: {}", input);
        SwingUtilities.invokeAndWait(() -> {
            calculator.getConverterPanel().getTextField1().setText(input);
            calculator.getConverterPanel().setIsTextField1Selected(true);
        });
        double result = AngleMethods.convertingDegreesToGradians(calculator);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link AngleMethods#convertingDegreesToGradians(Calculator)} method.
     * @return various degree inputs and their expected gradian outputs
     */
    private static Stream<Arguments> convertingDegreesToGradiansTestCases()
    {
        return Stream.of(
                Arguments.of("0",   degToGrad(0)),
                Arguments.of("45",  degToGrad(45)),
                Arguments.of("90",  degToGrad(90)),
                Arguments.of("180", degToGrad(180)),
                Arguments.of("360", degToGrad(360))
        );
    }

    // -------------------------------------------------------------------------
    // convertingGradiansToDegrees
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link AngleMethods#convertingGradiansToDegrees(Calculator)}
     * @param input    argument 1, the gradian value to convert (set in textField1)
     * @param expected argument 2, the expected result in degrees
     */
    @ParameterizedTest
    @MethodSource("convertingGradiansToDegreesTestCases")
    @DisplayName("Test convertingGradiansToDegrees")
    void testConvertingGradiansToDegrees(String input, double expected)
            throws InterruptedException, InvocationTargetException
    {
        LOGGER.info("Testing convertingGradiansToDegrees with input: {}", input);
        SwingUtilities.invokeAndWait(() -> {
            calculator.getConverterPanel().getTextField1().setText(input);
            calculator.getConverterPanel().setIsTextField1Selected(true);
        });
        double result = AngleMethods.convertingGradiansToDegrees(calculator);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link AngleMethods#convertingGradiansToDegrees(Calculator)} method.
     * @return various gradian inputs and their expected degree outputs
     */
    private static Stream<Arguments> convertingGradiansToDegreesTestCases()
    {
        return Stream.of(
                Arguments.of("0",   gradToDeg(0)),
                Arguments.of("50",  gradToDeg(50)),
                Arguments.of("100", gradToDeg(100)),
                Arguments.of("200", gradToDeg(200)),
                Arguments.of("400", gradToDeg(400))
        );
    }

    // -------------------------------------------------------------------------
    // convertingRadiansToGradians
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link AngleMethods#convertingRadiansToGradians(Calculator)}
     * @param input    argument 1, the radian value to convert (set in textField1)
     * @param expected argument 2, the expected result in gradians
     */
    @ParameterizedTest
    @MethodSource("convertingRadiansToGradiansTestCases")
    @DisplayName("Test convertingRadiansToGradians")
    void testConvertingRadiansToGradians(String input, double expected)
            throws InterruptedException, InvocationTargetException
    {
        LOGGER.info("Testing convertingRadiansToGradians with input: {}", input);
        SwingUtilities.invokeAndWait(() -> {
            calculator.getConverterPanel().getTextField1().setText(input);
            calculator.getConverterPanel().setIsTextField1Selected(true);
        });
        double result = AngleMethods.convertingRadiansToGradians(calculator);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link AngleMethods#convertingRadiansToGradians(Calculator)} method.
     * @return various radian inputs and their expected gradian outputs
     */
    private static Stream<Arguments> convertingRadiansToGradiansTestCases()
    {
        return Stream.of(
                Arguments.of("0",      radToGrad(0)),
                Arguments.of("0.5",    radToGrad(0.5)),
                Arguments.of("1",      radToGrad(1)),
                Arguments.of("1.5708", radToGrad(1.5708)),
                Arguments.of("3.1416", radToGrad(3.1416))
        );
    }

    // -------------------------------------------------------------------------
    // convertingGradiansToRadians
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link AngleMethods#convertingGradiansToRadians(Calculator)}
     * @param input    argument 1, the gradian value to convert (set in textField1)
     * @param expected argument 2, the expected result in radians
     */
    @ParameterizedTest
    @MethodSource("convertingGradiansToRadiansTestCases")
    @DisplayName("Test convertingGradiansToRadians")
    void testConvertingGradiansToRadians(String input, double expected)
            throws InterruptedException, InvocationTargetException
    {
        LOGGER.info("Testing convertingGradiansToRadians with input: {}", input);
        SwingUtilities.invokeAndWait(() -> {
            calculator.getConverterPanel().getTextField1().setText(input);
            calculator.getConverterPanel().setIsTextField1Selected(true);
        });
        double result = AngleMethods.convertingGradiansToRadians(calculator);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link AngleMethods#convertingGradiansToRadians(Calculator)} method.
     * @return various gradian inputs and their expected radian outputs
     */
    private static Stream<Arguments> convertingGradiansToRadiansTestCases()
    {
        return Stream.of(
                Arguments.of("0",   gradToRad(0)),
                Arguments.of("50",  gradToRad(50)),
                Arguments.of("100", gradToRad(100)),
                Arguments.of("200", gradToRad(200)),
                Arguments.of("400", gradToRad(400))
        );
    }

    // -------------------------------------------------------------------------
    // convertValues – integration (unit-to-unit dispatch)
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link AngleMethods#convertValues(Calculator)}
     * Verifies the full dispatch path: sets both unit dropdowns and textField1, then
     * asserts the text written into textField2.
     * @param unit1    argument 1, the source unit (set in unitOptions1)
     * @param unit2    argument 2, the target unit (set in unitOptions2)
     * @param input    argument 3, the value entered in textField1
     * @param expected argument 4, the expected string value shown in textField2
     */
    @ParameterizedTest
    @MethodSource("convertValuesTestCases")
    @DisplayName("Test convertValues")
    void testConvertValues(CalculatorConverterUnits unit1, CalculatorConverterUnits unit2,
                           String input, String expected)
            throws InterruptedException, InvocationTargetException
    {
        LOGGER.info("Testing convertValues: {} {} → {} {}", input, unit1, unit2, expected);
        SwingUtilities.invokeAndWait(() -> {
            calculator.getConverterPanel().getUnitOptions1().setSelectedItem(unit1);
            calculator.getConverterPanel().getUnitOptions2().setSelectedItem(unit2);
            calculator.getConverterPanel().getTextField1().setText(input);
            calculator.getConverterPanel().setIsTextField1Selected(true);
        });
        AngleMethods.convertValues(calculator);
        String result = calculator.getConverterPanel().getTextField2().getText();
        LOGGER.info("Expected: '{}', Actual: '{}'", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link AngleMethods#convertValues(Calculator)} method.
     * Same-unit cases copy the text directly; cross-unit cases apply the conversion
     * and format with the same double-to-string logic the method uses (.0 → int string).
     * @return unit pairs, inputs, and expected textField2 strings
     */
    private static Stream<Arguments> convertValuesTestCases()
    {
        return Stream.of(
                // Same-unit: direct copy
                Arguments.of(CalculatorConverterUnits.DEGREES,  CalculatorConverterUnits.DEGREES,  "90",  "90"),
                Arguments.of(CalculatorConverterUnits.RADIANS,  CalculatorConverterUnits.RADIANS,  "1.5", "1.5"),
                Arguments.of(CalculatorConverterUnits.GRADIANS, CalculatorConverterUnits.GRADIANS, "100", "100"),

                // Cross-unit: result ending in .0 is formatted as integer string
                Arguments.of(CalculatorConverterUnits.DEGREES,  CalculatorConverterUnits.GRADIANS, "90",  formatResult(degToGrad(90))),
                Arguments.of(CalculatorConverterUnits.DEGREES,  CalculatorConverterUnits.RADIANS,  "90",  formatResult(rad(90))),
                Arguments.of(CalculatorConverterUnits.RADIANS,  CalculatorConverterUnits.DEGREES,  "1",   formatResult(deg(1))),
                Arguments.of(CalculatorConverterUnits.RADIANS,  CalculatorConverterUnits.GRADIANS, "1",   formatResult(radToGrad(1))),
                Arguments.of(CalculatorConverterUnits.GRADIANS, CalculatorConverterUnits.DEGREES,  "100", formatResult(gradToDeg(100))),
                Arguments.of(CalculatorConverterUnits.GRADIANS, CalculatorConverterUnits.RADIANS,  "100", formatResult(gradToRad(100)))
        );
    }

    // -------------------------------------------------------------------------
    // Private helpers – mirror the BigDecimal rounding used by AngleMethods
    // -------------------------------------------------------------------------

    /** Degrees → Radians: {@code BigDecimal.valueOf(Math.toRadians(d)).setScale(4, UP)} */
    private static double rad(double degrees)
    {
        return BigDecimal.valueOf(Math.toRadians(degrees)).setScale(4, RoundingMode.UP).doubleValue();
    }

    /** Radians → Degrees: {@code BigDecimal.valueOf(Math.toDegrees(r)).setScale(4, UP)} */
    private static double deg(double radians)
    {
        return BigDecimal.valueOf(Math.toDegrees(radians)).setScale(4, RoundingMode.UP).doubleValue();
    }

    /** Degrees → Gradians: {@code new BigDecimal(d * 1.111111).setScale(4, UP)} */
    private static double degToGrad(double degrees)
    {
        return new BigDecimal(degrees * 1.111111).setScale(4, RoundingMode.UP).doubleValue();
    }

    /** Gradians → Degrees: {@code new BigDecimal(g / 1.111111).setScale(4, UP)} */
    private static double gradToDeg(double gradians)
    {
        return new BigDecimal(gradians / 1.111111).setScale(4, RoundingMode.UP).doubleValue();
    }

    /** Radians → Gradians: {@code BigDecimal.valueOf(r * 63.662).setScale(4, UP)} */
    private static double radToGrad(double radians)
    {
        return BigDecimal.valueOf(radians * 63.662).setScale(4, RoundingMode.UP).doubleValue();
    }

    /** Gradians → Radians: {@code new BigDecimal(g / 63.662).setScale(4, UP)} */
    private static double gradToRad(double gradians)
    {
        return new BigDecimal(gradians / 63.662).setScale(4, RoundingMode.UP).doubleValue();
    }

    /**
     * Mirrors the string-formatting logic in {@link AngleMethods#convertValues(Calculator)}:
     * if the double ends with ".0", format as an integer string; otherwise use
     * {@link String#valueOf(double)}.
     * @param value the converted double value
     * @return the string the method would write into the text field
     */
    private static String formatResult(double value)
    {
        return String.valueOf(value).endsWith(".0")
                ? String.valueOf((int) value)
                : String.valueOf(value);
    }
}

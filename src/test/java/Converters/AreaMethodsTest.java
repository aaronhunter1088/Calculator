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
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import static Types.CalculatorConverterUnits.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

/**
 * AreaMethodsTest
 * <p>
 * This class tests the {@link AreaMethods} class.
 *
 * @author Michael Ball
 * @version 4.0
 */
public class AreaMethodsTest extends TestParent
{
    private static final Logger LOGGER = LogManager.getLogger(AreaMethodsTest.class);

    static {
        System.setProperty("appName", AreaMethodsTest.class.getSimpleName());
    }

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(() -> {
            try {
                calculator = spy(new Calculator());
                Preferences.userNodeForPackage(Calculator.class).clear();
                calculator.setSystemDetector(systemDetector);
                calculator.performViewMenuAction(actionEvent, CalculatorConverterType.AREA);
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
    // convert – pure math (no calculator required)
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link AreaMethods#convert(double, CalculatorConverterUnits, CalculatorConverterUnits)}
     * @param input    argument 1, the value to convert
     * @param from     argument 2, the source unit
     * @param to       argument 3, the target unit
     * @param expected argument 4, the expected converted value
     */
    @ParameterizedTest
    @MethodSource("convertTestCases")
    @DisplayName("Test convert")
    void testConvert(double input, CalculatorConverterUnits from,
                     CalculatorConverterUnits to, double expected)
    {
        LOGGER.info("Testing convert: {} {} → {}", input, from, to);
        double result = AreaMethods.convert(input, from, to);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result, 1e-9);
    }

    /**
     * Test cases for the {@link AreaMethods#convert(double, CalculatorConverterUnits, CalculatorConverterUnits)} method.
     * Exact integer conversions use hardcoded expected values; cross-system conversions
     * use private helper methods that mirror the implementation's conversion factors.
     * @return input value, source unit, target unit, and expected result
     */
    private static Stream<Arguments> convertTestCases()
    {
        return Stream.of(
                // Identity – same unit always returns the original value
                Arguments.of(5.0,          SQUARE_METERS,      SQUARE_METERS,      5.0),
                Arguments.of(100.0,        SQUARE_KILOMETERS,  SQUARE_KILOMETERS,  100.0),
                Arguments.of(9.0,          SQUARE_FEET,        SQUARE_FEET,        9.0),

                // Metric – exact by construction
                Arguments.of(1_000_000.0,  SQUARE_MILLIMETERS, SQUARE_METERS,      1.0),
                Arguments.of(1.0,          SQUARE_METERS,      SQUARE_MILLIMETERS, 1_000_000.0),
                Arguments.of(10_000.0,     SQUARE_CENTIMETERS, SQUARE_METERS,      1.0),
                Arguments.of(1.0,          SQUARE_METERS,      SQUARE_CENTIMETERS, 10_000.0),
                Arguments.of(10_000.0,     SQUARE_METERS,      HECTARES,           1.0),
                Arguments.of(1.0,          HECTARES,           SQUARE_METERS,      10_000.0),
                Arguments.of(100.0,        HECTARES,           SQUARE_KILOMETERS,  1.0),
                Arguments.of(1.0,          SQUARE_KILOMETERS,  HECTARES,           100.0),
                Arguments.of(1.0,          SQUARE_KILOMETERS,  SQUARE_METERS,      1_000_000.0),

                // Imperial – exact by construction (factors chosen so round-trips are clean)
                Arguments.of(144.0,        SQUARE_INCHES,      SQUARE_FEET,        1.0),
                Arguments.of(1.0,          SQUARE_FEET,        SQUARE_INCHES,      144.0),
                Arguments.of(9.0,          SQUARE_FEET,        SQUARE_YARD_ACRES,  1.0),
                Arguments.of(1.0,          SQUARE_YARD_ACRES,  SQUARE_FEET,        9.0),

                // Cross-system: metric ↔ imperial (formula-derived helpers)
                Arguments.of(1.0,          SQUARE_METERS,      SQUARE_FEET,        sqMToSqFt(1.0)),
                Arguments.of(1.0,          SQUARE_FEET,        SQUARE_METERS,      sqFtToSqM(1.0)),
                Arguments.of(1.0,          SQUARE_KILOMETERS,  SQUARE_MILES,       sqKmToSqMi(1.0)),
                Arguments.of(1.0,          SQUARE_MILES,       SQUARE_KILOMETERS,  sqMiToSqKm(1.0)),
                Arguments.of(1.0,          HECTARES,           SQUARE_FEET,        haToSqFt(1.0)),
                Arguments.of(1.0,          SQUARE_METERS,      SQUARE_INCHES,      sqMToSqIn(1.0))
        );
    }

    // -------------------------------------------------------------------------
    // convertValues – textField1 → textField2  (isTextField1Selected = true)
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link AreaMethods#convertValues(Calculator)} when
     * textField1 is the source of input.
     * Verifies that the converted result is formatted and written to textField2.
     *
     * @param unit1    argument 1, the source unit (set in unitOptions1)
     * @param unit2    argument 2, the target unit (set in unitOptions2)
     * @param input    argument 3, the value entered in textField1
     * @param expected argument 4, the expected string shown in textField2
     */
    @ParameterizedTest
    @MethodSource("convertValuesFromTextField1TestCases")
    @DisplayName("Test convertValues – textField1 to textField2")
    void testConvertValuesFromTextField1(CalculatorConverterUnits unit1,
                                         CalculatorConverterUnits unit2,
                                         String input, String expected)
            throws InterruptedException, InvocationTargetException
    {
        LOGGER.info("Testing convertValues (tf1→tf2): {} {} → {}", input, unit1, unit2);
        SwingUtilities.invokeAndWait(() -> {
            calculator.getConverterPanel().getUnitOptions1().setSelectedItem(unit1);
            calculator.getConverterPanel().getUnitOptions2().setSelectedItem(unit2);
            calculator.getConverterPanel().getTextField1().setText(input);
            calculator.getConverterPanel().setIsTextField1Selected(true);
        });
        AreaMethods.convertValues(calculator);
        String result = calculator.getConverterPanel().getTextField2().getText();
        LOGGER.info("Expected: '{}', Actual: '{}'", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the textField1-as-source path of
     * {@link AreaMethods#convertValues(Calculator)}.
     * Same-unit cases expect a direct copy; cross-unit cases use
     * {@link #formatResult(double)} to mirror the method's own formatting logic.
     *
     * @return unit1, unit2, input string, and expected textField2 string
     */
    private static Stream<Arguments> convertValuesFromTextField1TestCases()
    {
        return Stream.of(
                // Same-unit: direct copy
                Arguments.of(SQUARE_METERS,     SQUARE_METERS,     "1",        "1"),
                Arguments.of(SQUARE_KILOMETERS, SQUARE_KILOMETERS, "5",        "5"),

                // Metric cross-unit (results are clean integers)
                Arguments.of(SQUARE_MILLIMETERS, SQUARE_METERS,     "1000000", formatResult(AreaMethods.convert(1_000_000, SQUARE_MILLIMETERS, SQUARE_METERS))),
                Arguments.of(SQUARE_METERS,      SQUARE_MILLIMETERS,"1",        formatResult(AreaMethods.convert(1,         SQUARE_METERS,      SQUARE_MILLIMETERS))),
                Arguments.of(SQUARE_CENTIMETERS, SQUARE_METERS,     "10000",   formatResult(AreaMethods.convert(10_000,    SQUARE_CENTIMETERS, SQUARE_METERS))),
                Arguments.of(SQUARE_METERS,      HECTARES,          "10000",   formatResult(AreaMethods.convert(10_000,    SQUARE_METERS,      HECTARES))),
                Arguments.of(HECTARES,           SQUARE_KILOMETERS, "100",     formatResult(AreaMethods.convert(100,       HECTARES,           SQUARE_KILOMETERS))),

                // Imperial cross-unit
                Arguments.of(SQUARE_INCHES,     SQUARE_FEET,        "144",     formatResult(AreaMethods.convert(144,       SQUARE_INCHES,      SQUARE_FEET))),
                Arguments.of(SQUARE_FEET,        SQUARE_YARD_ACRES, "9",        formatResult(AreaMethods.convert(9,         SQUARE_FEET,        SQUARE_YARD_ACRES))),

                // Cross-system
                Arguments.of(SQUARE_METERS,     SQUARE_FEET,        "1",        formatResult(AreaMethods.convert(1,         SQUARE_METERS,      SQUARE_FEET))),
                Arguments.of(SQUARE_FEET,        SQUARE_METERS,     "1",        formatResult(AreaMethods.convert(1,         SQUARE_FEET,        SQUARE_METERS))),
                Arguments.of(SQUARE_KILOMETERS, SQUARE_MILES,       "1",        formatResult(AreaMethods.convert(1,         SQUARE_KILOMETERS,  SQUARE_MILES)))
        );
    }

    // -------------------------------------------------------------------------
    // convertValues – textField2 → textField1  (isTextField1Selected = false)
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link AreaMethods#convertValues(Calculator)} when
     * textField2 is the source of input.
     * Verifies that the converted result is formatted and written to textField1.
     *
     * @param unit1    argument 1, the target unit (set in unitOptions1)
     * @param unit2    argument 2, the source unit (set in unitOptions2)
     * @param input    argument 3, the value entered in textField2
     * @param expected argument 4, the expected string shown in textField1
     */
    @ParameterizedTest
    @MethodSource("convertValuesFromTextField2TestCases")
    @DisplayName("Test convertValues – textField2 to textField1")
    void testConvertValuesFromTextField2(CalculatorConverterUnits unit1,
                                         CalculatorConverterUnits unit2,
                                         String input, String expected)
            throws InterruptedException, InvocationTargetException
    {
        LOGGER.info("Testing convertValues (tf2→tf1): {} {} ← {}", unit1, unit2, input);
        SwingUtilities.invokeAndWait(() -> {
            calculator.getConverterPanel().getUnitOptions1().setSelectedItem(unit1);
            calculator.getConverterPanel().getUnitOptions2().setSelectedItem(unit2);
            calculator.getConverterPanel().getTextField2().setText(input);
            calculator.getConverterPanel().setIsTextField1Selected(false);
        });
        AreaMethods.convertValues(calculator);
        String result = calculator.getConverterPanel().getTextField1().getText();
        LOGGER.info("Expected: '{}', Actual: '{}'", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the textField2-as-source path of
     * {@link AreaMethods#convertValues(Calculator)}.
     * The conversion direction is unit2 → unit1 (reverse of the tf1 path).
     *
     * @return unit1, unit2, input string, and expected textField1 string
     */
    private static Stream<Arguments> convertValuesFromTextField2TestCases()
    {
        return Stream.of(
                // Same-unit: direct copy
                Arguments.of(SQUARE_METERS,     SQUARE_METERS,     "1",    "1"),

                // Metric cross-unit – direction is unit2 → unit1
                Arguments.of(SQUARE_METERS,      SQUARE_MILLIMETERS, "1000000", formatResult(AreaMethods.convert(1_000_000, SQUARE_MILLIMETERS, SQUARE_METERS))),
                Arguments.of(SQUARE_MILLIMETERS, SQUARE_METERS,      "1",       formatResult(AreaMethods.convert(1,         SQUARE_METERS,      SQUARE_MILLIMETERS))),
                Arguments.of(SQUARE_METERS,      SQUARE_CENTIMETERS, "10000",   formatResult(AreaMethods.convert(10_000,    SQUARE_CENTIMETERS, SQUARE_METERS))),
                Arguments.of(HECTARES,           SQUARE_METERS,      "10000",   formatResult(AreaMethods.convert(10_000,    SQUARE_METERS,      HECTARES))),
                Arguments.of(SQUARE_KILOMETERS,  HECTARES,           "100",     formatResult(AreaMethods.convert(100,       HECTARES,           SQUARE_KILOMETERS))),

                // Imperial cross-unit
                Arguments.of(SQUARE_FEET,        SQUARE_INCHES,     "144",  formatResult(AreaMethods.convert(144, SQUARE_INCHES, SQUARE_FEET))),
                Arguments.of(SQUARE_YARD_ACRES,  SQUARE_FEET,       "9",    formatResult(AreaMethods.convert(9,   SQUARE_FEET,   SQUARE_YARD_ACRES))),

                // Cross-system
                Arguments.of(SQUARE_FEET,        SQUARE_METERS,     "1",    formatResult(AreaMethods.convert(1, SQUARE_METERS, SQUARE_FEET))),
                Arguments.of(SQUARE_METERS,      SQUARE_FEET,       "1",    formatResult(AreaMethods.convert(1, SQUARE_FEET,   SQUARE_METERS))),
                Arguments.of(SQUARE_MILES,       SQUARE_KILOMETERS, "1",    formatResult(AreaMethods.convert(1, SQUARE_KILOMETERS, SQUARE_MILES)))
        );
    }

    // -------------------------------------------------------------------------
    // Private helpers – mirror the conversion factors used by AreaMethods
    // -------------------------------------------------------------------------

    /** Square meters → square feet */
    private static double sqMToSqFt(double sqM)   { return sqM / 9.290304e-2; }

    /** Square feet → square meters */
    private static double sqFtToSqM(double sqFt)  { return sqFt * 9.290304e-2; }

    /** Square kilometers → square miles */
    private static double sqKmToSqMi(double sqKm) { return sqKm * 1e6 / 2_589_988.110336; }

    /** Square miles → square kilometers */
    private static double sqMiToSqKm(double sqMi) { return sqMi * 2_589_988.110336 / 1e6; }

    /** Hectares → square feet */
    private static double haToSqFt(double ha)      { return ha * 1e4 / 9.290304e-2; }

    /** Square meters → square inches */
    private static double sqMToSqIn(double sqM)    { return sqM / 6.4516e-4; }

    /**
     * Mirrors the string-formatting logic in {@link AreaMethods#convertValues(Calculator)}:
     * if the double value ends with ".0", format as an integer string; otherwise use
     * {@link String#valueOf(double)}.
     *
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

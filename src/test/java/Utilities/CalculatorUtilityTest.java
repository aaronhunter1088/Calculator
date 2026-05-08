package Utilities;

import Parent.TestParent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static Types.Texts.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * CalculatorUtilityTest
 * <p>
 * This class tests the {@link CalculatorUtility} class.
 * <p>
 *
 * @author Michael Ball
 * @version 4.0
 */
public class CalculatorUtilityTest extends TestParent
{
    private static final Logger LOGGER = LogManager.getLogger(CalculatorUtilityTest.class.getSimpleName());

    static {
        System.setProperty("appName", CalculatorUtilityTest.class.getSimpleName());
    }

    // -------------------------------------------------------------------------
    // addThousandsDelimiter
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link CalculatorUtility#addThousandsDelimiter(String input, String delimiter)}
     * @param input argument 1, the number to add the thousands delimiter to
     * @param expected argument 2, the expected output
     * @param delimiter argument 3, the delimiter to enforce
     */
    @ParameterizedTest
    @MethodSource("addThousandsDelimiterTestCases")
    @DisplayName("Test addThousandsDelimiter")
    void testAddThousandsDelimiter(String input, String expected, String delimiter)
    {
        LOGGER.info("Testing addThousandsDelimiter with input: {}", input);
        String result = CalculatorUtility.addThousandsDelimiter(input, delimiter);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link CalculatorUtility#addThousandsDelimiter(String input, String delimiter)} method.
     * @return various test cases to check
     */
    private static Stream<Arguments> addThousandsDelimiterTestCases()
    {
        return Stream.of(
                // Comma
                Arguments.of("1000", "1,000", COMMA),
                Arguments.of("1000000", "1,000,000", COMMA),
                Arguments.of("123456789", "123,456,789", COMMA),
                Arguments.of("0", "0", COMMA),
                Arguments.of("-1000", "-1,000", COMMA),
                Arguments.of("-1234567.89", "-1,234,567.89", COMMA),
                Arguments.of("12345 ()", "12,345 ()", COMMA),
                Arguments.of("1234 (1234)", "1,234 (1,234)", COMMA),

                // Decimal
                Arguments.of("1000", "1.000", DECIMAL),
                Arguments.of("1000000", "1.000.000", DECIMAL),
                Arguments.of("123456789", "123.456.789", DECIMAL),
                Arguments.of("0", "0", DECIMAL),
                Arguments.of("-1000", "-1.000", DECIMAL)
                //Arguments.of("-1234567.89", "-1.234.567,89", DECIMAL)
        );
    }

    // -------------------------------------------------------------------------
    // removeThousandsDelimiter
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link CalculatorUtility#removeThousandsDelimiter(String input, String delimiter)}
     * @param input     argument 1, the number to remove the delimiter from
     * @param expected  argument 2, the expected output
     * @param delimiter argument 3, the delimiter to remove
     */
    @ParameterizedTest
    @MethodSource("removeThousandsDelimiterTestCases")
    @DisplayName("Test removeThousandsDelimiter")
    void testRemoveThousandsDelimiter(String input, String expected, String delimiter)
    {
        LOGGER.info("Testing removeThousandsDelimiter with input: {}", input);
        String result = CalculatorUtility.removeThousandsDelimiter(input, delimiter);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link CalculatorUtility#removeThousandsDelimiter(String input, String delimiter)} method.
     * @return various test cases to check
     */
    private static Stream<Arguments> removeThousandsDelimiterTestCases()
    {
        return Stream.of(
                // Comma delimiter
                Arguments.of("1,000",       "1000",      COMMA),
                Arguments.of("1,000,000",   "1000000",   COMMA),
                Arguments.of("123,456,789", "123456789", COMMA),
                Arguments.of("-1,000",      "-1000",     COMMA),
                Arguments.of("0",           "0",         COMMA),  // no delimiter present
                Arguments.of(EMPTY,         EMPTY,       COMMA),  // empty string

                // Decimal delimiter
                Arguments.of("1.000",       "1000",      DECIMAL),
                Arguments.of("1.000.000",   "1000000",   DECIMAL),
                Arguments.of("123.456.789", "123456789", DECIMAL),
                Arguments.of("-1.000",      "-1000",     DECIMAL),
                Arguments.of("0",           "0",         DECIMAL)
        );
    }

    // -------------------------------------------------------------------------
    // convertToNegative
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link CalculatorUtility#convertToNegative(String inputToConvert)}
     * @param input    argument 1, the number to convert to negative
     * @param expected argument 2, the expected output
     */
    @ParameterizedTest
    @MethodSource("convertToNegativeTestCases")
    @DisplayName("Test convertToNegative")
    void testConvertToNegative(String input, String expected)
    {
        LOGGER.info("Testing convertToNegative with input: {}", input);
        String result = CalculatorUtility.convertToNegative(input);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link CalculatorUtility#convertToNegative(String inputToConvert)} method.
     * @return various test cases to check
     */
    private static Stream<Arguments> convertToNegativeTestCases()
    {
        return Stream.of(
                Arguments.of("5",       "-5"),
                Arguments.of("100",     "-100"),
                Arguments.of("0",       "-0"),
                Arguments.of("3.14",    "-3.14"),
                Arguments.of("-5",      "-5"),   // already negative – should not double-negate
                Arguments.of("-100",    "-100")
        );
    }

    // -------------------------------------------------------------------------
    // convertToPositive
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link CalculatorUtility#convertToPositive(String inputToConvert)}
     * @param input    argument 1, the number to convert to positive
     * @param expected argument 2, the expected output
     */
    @ParameterizedTest
    @MethodSource("convertToPositiveTestCases")
    @DisplayName("Test convertToPositive")
    void testConvertToPositive(String input, String expected)
    {
        LOGGER.info("Testing convertToPositive with input: {}", input);
        String result = CalculatorUtility.convertToPositive(input);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link CalculatorUtility#convertToPositive(String inputToConvert)} method.
     * @return various test cases to check
     */
    private static Stream<Arguments> convertToPositiveTestCases()
    {
        return Stream.of(
                Arguments.of("-5",   "5"),
                Arguments.of("-100", "100"),
                Arguments.of("-3.14","3.14"),
                Arguments.of("5",    "5"),    // already positive
                Arguments.of("0",    "0")
        );
    }

    // -------------------------------------------------------------------------
    // getNumberOnLeftSideOfDecimal
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link CalculatorUtility#getNumberOnLeftSideOfDecimal(String value)}
     * @param input    argument 1, the number to split on the decimal
     * @param expected argument 2, the expected left-side value
     */
    @ParameterizedTest
    @MethodSource("getNumberOnLeftSideOfDecimalTestCases")
    @DisplayName("Test getNumberOnLeftSideOfDecimal")
    void testGetNumberOnLeftSideOfDecimal(String input, String expected)
    {
        LOGGER.info("Testing getNumberOnLeftSideOfDecimal with input: {}", input);
        String result = CalculatorUtility.getNumberOnLeftSideOfDecimal(input);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link CalculatorUtility#getNumberOnLeftSideOfDecimal(String value)} method.
     * @return various test cases to check
     */
    private static Stream<Arguments> getNumberOnLeftSideOfDecimalTestCases()
    {
        return Stream.of(
                Arguments.of("3.14",   "3"),
                Arguments.of("100.0",  "100"),
                Arguments.of("-5.75",  "-5"),
                Arguments.of("0.5",    "0"),
                Arguments.of("42",     "42"),  // no decimal – returns whole number
                Arguments.of("-7",     "-7")   // no decimal – returns whole number
        );
    }

    // -------------------------------------------------------------------------
    // getNumberOnRightSideOfDecimal
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link CalculatorUtility#getNumberOnRightSideOfDecimal(String input)}
     * @param input    argument 1, the number to split on the decimal
     * @param expected argument 2, the expected right-side value
     */
    @ParameterizedTest
    @MethodSource("getNumberOnRightSideOfDecimalTestCases")
    @DisplayName("Test getNumberOnRightSideOfDecimal")
    void testGetNumberOnRightSideOfDecimal(String input, String expected)
    {
        LOGGER.info("Testing getNumberOnRightSideOfDecimal with input: {}", input);
        String result = CalculatorUtility.getNumberOnRightSideOfDecimal(input);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link CalculatorUtility#getNumberOnRightSideOfDecimal(String input)} method.
     * @return various test cases to check
     */
    private static Stream<Arguments> getNumberOnRightSideOfDecimalTestCases()
    {
        return Stream.of(
                Arguments.of("3.14",   "14"),
                Arguments.of("100.0",  "0"),
                Arguments.of("-5.75",  "75"),
                Arguments.of("0.5",    "5"),
                Arguments.of("42",     EMPTY),  // no decimal – returns empty
                Arguments.of("3.1415", "1415")
        );
    }

    // -------------------------------------------------------------------------
    // getValueWithoutAnyOperator
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link CalculatorUtility#getValueWithoutAnyOperator(String input)}
     * @param input    argument 1, the value to strip operators from
     * @param expected argument 2, the expected plain number
     */
    @ParameterizedTest
    @MethodSource("getValueWithoutAnyOperatorTestCases")
    @DisplayName("Test getValueWithoutAnyOperator")
    void testGetValueWithoutAnyOperator(String input, String expected)
    {
        LOGGER.info("Testing getValueWithoutAnyOperator with input: {}", input);
        String result = CalculatorUtility.getValueWithoutAnyOperator(input);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link CalculatorUtility#getValueWithoutAnyOperator(String input)} method.
     * @return various test cases to check
     */
    private static Stream<Arguments> getValueWithoutAnyOperatorTestCases()
    {
        return Stream.of(
                Arguments.of("5",          "5"),
                Arguments.of("-5",         "-5"),    // negative sign is preserved
                Arguments.of("5" + ADDITION,         "5"),
                Arguments.of("5" + MULTIPLICATION,   "5"),
                Arguments.of("5" + DIVISION,         "5"),
                Arguments.of("5" + MODULUS,          "5"),
                Arguments.of("5" + MEMORY_STORE,     "5"),
                Arguments.of("5" + AND,              "5"),
                Arguments.of("5" + OR,               "5"),
                Arguments.of("5" + XOR,              "5"),
                Arguments.of("5" + ROL,              "5"),
                Arguments.of("5" + ROR,              "5")
        );
    }

    // -------------------------------------------------------------------------
    // isFractionalNumber
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link CalculatorUtility#isFractionalNumber(String value)}
     * @param input    argument 1, the number to test
     * @param expected argument 2, whether the number is fractional
     */
    @ParameterizedTest
    @MethodSource("isFractionalNumberTestCases")
    @DisplayName("Test isFractionalNumber")
    void testIsFractionalNumber(String input, boolean expected)
    {
        LOGGER.info("Testing isFractionalNumber with input: {}", input);
        boolean result = CalculatorUtility.isFractionalNumber(input);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link CalculatorUtility#isFractionalNumber(String value)} method.
     * @return various test cases to check
     */
    private static Stream<Arguments> isFractionalNumberTestCases()
    {
        return Stream.of(
                Arguments.of("3.14",  true),
                Arguments.of("0.5",   true),
                Arguments.of("-1.0",  true),
                Arguments.of("5",     false),
                Arguments.of("-5",    false),
                Arguments.of("0",     false)
        );
    }

    // -------------------------------------------------------------------------
    // isPositiveNumber
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link CalculatorUtility#isPositiveNumber(String value)}
     * @param input    argument 1, the number to test
     * @param expected argument 2, whether the number is positive
     */
    @ParameterizedTest
    @MethodSource("isPositiveNumberTestCases")
    @DisplayName("Test isPositiveNumber")
    void testIsPositiveNumber(String input, boolean expected)
    {
        LOGGER.info("Testing isPositiveNumber with input: {}", input);
        boolean result = CalculatorUtility.isPositiveNumber(input);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link CalculatorUtility#isPositiveNumber(String value)} method.
     * @return various test cases to check
     */
    private static Stream<Arguments> isPositiveNumberTestCases()
    {
        return Stream.of(
                Arguments.of("5",     true),
                Arguments.of("100",   true),
                Arguments.of("0",     true),
                Arguments.of("3.14",  true),
                Arguments.of("-5",    false),
                Arguments.of("-3.14", false),
                Arguments.of("-100",  false)
        );
    }

    // -------------------------------------------------------------------------
    // isNegativeNumber
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link CalculatorUtility#isNegativeNumber(String value)}
     * @param input    argument 1, the number to test
     * @param expected argument 2, whether the number is negative
     */
    @ParameterizedTest
    @MethodSource("isNegativeNumberTestCases")
    @DisplayName("Test isNegativeNumber")
    void testIsNegativeNumber(String input, boolean expected)
    {
        LOGGER.info("Testing isNegativeNumber with input: {}", input);
        boolean result = CalculatorUtility.isNegativeNumber(input);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link CalculatorUtility#isNegativeNumber(String value)} method.
     * @return various test cases to check
     */
    private static Stream<Arguments> isNegativeNumberTestCases()
    {
        return Stream.of(
                Arguments.of("-5",    true),
                Arguments.of("-100",  true),
                Arguments.of("-3.14", true),
                Arguments.of("5",     false),
                Arguments.of("0",     false),
                Arguments.of("3.14",  false)
        );
    }

    // -------------------------------------------------------------------------
    // isMinimumValue
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link CalculatorUtility#isMinimumValue(String value)}
     * @param input    argument 1, the value to check against the minimum
     * @param expected argument 2, whether the minimum value has been met
     */
    @ParameterizedTest
    @MethodSource("isMinimumValueTestCases")
    @DisplayName("Test isMinimumValue")
    void testIsMinimumValue(String input, boolean expected)
    {
        LOGGER.info("Testing isMinimumValue with input: {}", input);
        boolean result = CalculatorUtility.isMinimumValue(input);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link CalculatorUtility#isMinimumValue(String value)} method.
     * @return various test cases to check
     */
    private static Stream<Arguments> isMinimumValueTestCases()
    {
        return Stream.of(
                Arguments.of(MIN_VALUE,    true),   // exactly -9999999
                Arguments.of("-10000000",  true),   // below minimum
                Arguments.of("0",          false),
                Arguments.of("1",          false),
                Arguments.of("-1000000",   false)   // above minimum
        );
    }

    // -------------------------------------------------------------------------
    // isMaximumValue
    // -------------------------------------------------------------------------

    /**
     * Test cases for method: {@link CalculatorUtility#isMaximumValue(String value)}
     * @param input    argument 1, the value to check against the maximum
     * @param expected argument 2, whether the maximum value has been met
     */
    @ParameterizedTest
    @MethodSource("isMaximumValueTestCases")
    @DisplayName("Test isMaximumValue")
    void testIsMaximumValue(String input, boolean expected)
    {
        LOGGER.info("Testing isMaximumValue with input: {}", input);
        boolean result = CalculatorUtility.isMaximumValue(input);
        LOGGER.info("Expected: {}, Actual: {}", expected, result);
        assertEquals(expected, result);
    }

    /**
     * Test cases for the {@link CalculatorUtility#isMaximumValue(String value)} method.
     * @return various test cases to check
     */
    private static Stream<Arguments> isMaximumValueTestCases()
    {
        return Stream.of(
                Arguments.of(MAX_VALUE,   true),   // exactly 9999999
                Arguments.of("10000000",  true),   // above maximum
                Arguments.of("0",         false),
                Arguments.of("-1",        false),
                Arguments.of("1000000",   false)   // below maximum
        );
    }

}

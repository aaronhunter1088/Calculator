package Utilities;

import Parent.TestParent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static Types.Texts.COMMA;
import static Types.Texts.DECIMAL;
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
    private static final Logger LOGGER = LogManager.getLogger(PemdasUtiltyTest.class.getSimpleName());

    static {
        System.setProperty("appName", PemdasUtiltyTest.class.getSimpleName());
    }

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

                // Decimal
                Arguments.of("1000", "1.000", DECIMAL),
                Arguments.of("1000000", "1.000.000", DECIMAL),
                Arguments.of("123456789", "123.456.789", DECIMAL),
                Arguments.of("0", "0", DECIMAL),
                Arguments.of("-1000", "-1.000", DECIMAL)
                //Arguments.of("-1234567.89", "-1.234.567,89", DECIMAL)
        );
    }
}

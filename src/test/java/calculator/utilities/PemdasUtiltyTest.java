package calculator.utilities;

import calculator.test.TestParent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static calculator.utilities.PemdasUtility.*;

/**
 * PemdasUtilityTest
 * <p>
 * This class tests the {@link PemdasUtility} class.
 * <p>
 *
 * @author Michael Ball
 * @version 4.0
 */
public class PemdasUtiltyTest extends TestParent
{
    private static final Logger LOGGER = LogManager.getLogger(PemdasUtiltyTest.class.getSimpleName());

    static {
        System.setProperty("appName", PemdasUtiltyTest.class.getSimpleName());
    }

    @ParameterizedTest
    @DisplayName("Test Pemdas Equations")
    @MethodSource("pemdasEquations")
    public void testPemdasEquations(String equation, String expectedResult)
    {
        LOGGER.info("Testing Pemdas Equations");
        LOGGER.info("Parsing equation {} = {}", equation, parse(equation));
        double result = parse(equation);
        BigDecimal actualResult = convertToBigDecimal(result);
        assertEquals(
                new BigDecimal(expectedResult).doubleValue(),
                actualResult.setScale(3, RoundingMode.UP).doubleValue()
        );
        LOGGER.info("Expected equals actual result: {}", expectedResult);
    }

    /**
     * List of equations to test Pemdas against.
     * @return stream of arguments to test
     */
    private static Stream<Arguments> pemdasEquations()
    {
        return Stream.of(
                Arguments.of("5(4 ✕ 2)", "40"),
                Arguments.of("(4 ✕ 2) + (8 ÷ 3)", "10.667"),
                Arguments.of("-(3 + 2) ✕ 4", "-20")
                // More
        );
    }
}

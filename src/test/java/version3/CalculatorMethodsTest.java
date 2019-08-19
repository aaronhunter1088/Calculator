package version3;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class CalculatorMethodsTest {

    private static Calculator_v3 c;
    private String number;
    private boolean result;

    @BeforeClass
    public static void setup() throws Exception {
        c = new StandardCalculator_v3();
    }

    @Test
    public void testConvertingToPositive() {
        number = "-5.02";
        number = c.convertToPositive(number);
        assertEquals("Number is not positive", "5.02", number);
    }

    @Test
    public void testIsPositiveReturnsTrue() {
        number = "6";
        result = c.isPositiveNumber(number);
        assertTrue("IsPositive did not return true", result);
    }

    @Test
    public void testIsPositiveReturnsFalse() {
        number = "-6";
        result = c.isPositiveNumber(number);
        assertFalse("IsPositive did not return false", result);
    }

    @Test
    public void testConvertingToNegative() {
        number = "5.02";
        number = c.convertToNegative(number);
        assertEquals("Number is not negative", "-5.02", number);
    }
    @Test
    public void testIsNegativeReturnsTrue() {
        number = "-6";
        result = c.isNegativeNumber(number);
        assertTrue("IsNegative did not return true", result);
    }

    @Test
    public void testIsNegativeReturnsFalse() {
        number = "6";
        result = c.isNegativeNumber(number);
        assertFalse("IsNegative did not return false", result);
    }


    @Test
    public void testIsDecimalReturnsTrue() {
        number = "5.02";
        boolean answer = c.isDecimal(number);
        assertTrue("Number is a whole number", answer);
    }

    @Test
    public void testIsDecimalReturnsFalse() {
        number = "5";
        boolean answer = c.isDecimal(number);
        assertFalse("Number is a decimal", answer);
    }
}

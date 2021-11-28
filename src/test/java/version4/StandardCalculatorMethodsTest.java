package version4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class StandardCalculatorMethodsTest {

    private static Calculator_v4 c;
    private String number;
    private boolean result;

    @BeforeClass
    public static void setup() throws Exception {
        System.setProperty("appName", "StandardCalculatorMethodsTest");
        c = new Calculator_v4();
    }

    @Test
    public void testAdditionFunctionality() {
        c.setValues(new String[]{"5", "10"});
        c.addition();
        assertEquals("Did not get back expected result", "15", c.getValues()[0]);
    }

    @Test
    public void testSubtractionFunctionality() {
        c.setValues(new String[]{"15", "10"});
        c.subtract();
        assertEquals("Did not get back expected result", "5", c.getValues()[0]);
    }

    @Test
    public void testMultiplicationFunctionality() {
        c.setValues(new String[]{"15", "10"});
        c.multiply();
        assertEquals("Did not get back expected result", "150", c.getValues()[0]);
    }

    @Test
    public void testDivisionFunctionality() {
        c.setValues(new String[]{"15", "5"});
        c.setCalculatorType(CalculatorType_v4.BASIC);
        c.divide();
        assertEquals("Did not get back expected result", "3", c.getValues()[0]);
    }
}

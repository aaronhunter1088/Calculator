package version3;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class StandardCalculatorMethodsTest {

    private static StandardCalculator_v3 c;
    private String number;
    private boolean result;

    @BeforeClass
    public static void setup() throws Exception {
        System.setProperty("appName", "StandardCalculatorMethodsTest");
        c = new StandardCalculator_v3();
    }

    @Test
    public void testAdditionFunctionality() {
        c.values[0] = "5";
        c.values[1] = "10";
        c.addition();
        assertEquals("Did not get back expected result", "15", c.values[0]);
    }

    @Test
    public void testSubtractionFunctionality() {
        c.values[0] = "15";
        c.values[1] = "10";
        c.subtract();
        assertEquals("Did not get back expected result", "5", c.values[0]);
    }

    @Test
    public void testMultiplicationFunctionality() {
        c.values[0] = "15";
        c.values[1] = "10";
        c.multiply();
        assertEquals("Did not get back expected result", "150", c.values[0]);
    }

    @Test
    public void testDivisionFunctionality() {
        c.values[0] = "15";
        c.values[1] = "5";
        c.calcType = CalcType_v3.BASIC;
        c.divide();
        assertEquals("Did not get back expected result", "3", c.values[0]);
    }
}

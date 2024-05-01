package Calculators;

import Panels.ConverterPanel;
import Panels.DatePanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;

import javax.swing.*;
import java.io.IOException;
import java.text.ParseException;

import static Types.CalculatorType.*;
import static Types.CalculatorBase.*;
import static Types.ConverterType.*;
import static Types.DateOperation.*;
import static org.junit.Assert.*;

public class CalculatorTests
{
    static { System.setProperty("appName", "CalculatorTests"); }
    private static Logger LOGGER;
    private static Calculator calculator;

    @BeforeClass
    public static void beforeAll()
    { LOGGER = LogManager.getLogger(CalculatorTests.class.getSimpleName()); }

    @AfterClass
    public static void afterAll()
    { LOGGER.info("Finished running " + CalculatorTests.class.getSimpleName()); }

    @Before
    public void beforeEach() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException {
        LOGGER.info("setting up each before...");
        if (null == calculator) calculator = new Calculator();
    }

    @After
    public void afterEach() {}

    @Test
    public void createBasicCalculatorDefault() throws Exception
    {
        LOGGER.info("createDefaultCalculator...");
        calculator = new Calculator();
        assertTrue("Cannot see basic calculator", calculator.isVisible());
    }

    @Test
    public void createBasicCalculatorEnforced() throws Exception
    {
        LOGGER.info("createBasicCalculator enforced...");
        calculator = new Calculator(BASIC);
        assertTrue("Cannot see basic calculator", calculator.isVisible());
    }

    @Test
    public void createProgrammerCalculator() throws Exception
    {
        LOGGER.info("createProgrammerCalculator...");
        calculator = new Calculator(PROGRAMMER);
        assertTrue("Cannot see programmer calculator", calculator.isVisible());
        assertSame("Base is not binary", calculator.getCalculatorBase(), BINARY);
    }

    @Test
    public void createProgrammerCalculatorInBinaryEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", BINARY);
        calculator = new Calculator(PROGRAMMER, BINARY);
        assertTrue("Cannot see programmer calculator", calculator.isVisible());
        assertSame("Base is not binary", calculator.getCalculatorBase(), BINARY);
    }

    @Test
    public void createProgrammerCalculatorInDecimalEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", DECIMAL);
        calculator = new Calculator(PROGRAMMER, DECIMAL);
        assertTrue("Cannot see programmer calculator", calculator.isVisible());
        assertSame("Base is not decimal", calculator.getCalculatorBase(), DECIMAL);
    }

    @Test
    public void createProgrammerCalculatorInOctalEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", OCTAL);
        calculator = new Calculator(PROGRAMMER, OCTAL);
        assertTrue("Cannot see programmer calculator", calculator.isVisible());
        assertSame("Base is not octal", calculator.getCalculatorBase(), OCTAL);
    }

    @Test
    public void createProgrammerCalculatorInHexadecimalEnforced() throws Exception
    {
        LOGGER.info("createProgrammerCalculator in {} enforced...", HEXADECIMAL);
        calculator = new Calculator(PROGRAMMER, HEXADECIMAL);
        assertTrue("Cannot see programmer calculator", calculator.isVisible());
        assertSame("Base is not binary", calculator.getCalculatorBase(), HEXADECIMAL);
    }

    @Test
    public void createScientificCalculator()
    {
        LOGGER.info("createScientificCalculator...");
        LOGGER.info("IMPLEMENT...");
    }

    @Test
    public void createDateCalculator() throws Exception
    {
        LOGGER.info("createDateCalculator...");
        calculator = new Calculator(DATE);
        assertTrue("Cannot see date calculator", calculator.isVisible());
        assertSame("Date operation is not " + DIFFERENCE_BETWEEN_DATES, ((DatePanel)calculator.getCurrentPanel()).getDateOperation(), DIFFERENCE_BETWEEN_DATES);
    }

    @Test
    public void createDateCalculatorWithDateOperation1Enforced() throws Exception
    {
        LOGGER.info("createDateCalculator with {} enforced...", DIFFERENCE_BETWEEN_DATES);
        calculator = new Calculator(DATE, DIFFERENCE_BETWEEN_DATES);
        assertTrue("Cannot see date calculator", calculator.isVisible());
        assertSame("Date operation is not " + DIFFERENCE_BETWEEN_DATES, ((DatePanel)calculator.getCurrentPanel()).getDateOperation(), DIFFERENCE_BETWEEN_DATES);
    }

    @Test
    public void createDateCalculatorWithDateOperation2Enforced() throws Exception
    {
        LOGGER.info("createDateCalculator with {} enforced...", ADD_SUBTRACT_DAYS);
        calculator = new Calculator(DATE, ADD_SUBTRACT_DAYS);
        assertTrue("Cannot see date calculator", calculator.isVisible());
        assertSame("Date operation is not " + ADD_SUBTRACT_DAYS, ((DatePanel)calculator.getCurrentPanel()).getDateOperation(), ADD_SUBTRACT_DAYS);
    }

    @Test
    public void createConverterCalculator() throws Exception
    {
        LOGGER.info("createConverterCalculator...");
        calculator = new Calculator(CONVERTER);
        assertTrue("Cannot see converter calculator", calculator.isVisible());
        assertSame("Converter operation is not " + ANGLE, ((ConverterPanel)calculator.getCurrentPanel()).getConverterType(), ANGLE);
    }

    @Test
    public void createConverterCalculatorWithAngleEnforced() throws Exception
    {
        LOGGER.info("createConverterCalculator with {} enforced...", ANGLE);
        calculator = new Calculator(CONVERTER, ANGLE);
        assertTrue("Cannot see converter calculator", calculator.isVisible());
        assertSame("Converter operation is not " + ANGLE, ((ConverterPanel)calculator.getCurrentPanel()).getConverterType(), ANGLE);
    }

    @Test
    public void createConverterCalculatorWithAreaEnforced() throws Exception
    {
        LOGGER.info("createConverterCalculator with {} enforced...", AREA);
        calculator = new Calculator(CONVERTER, AREA);
        assertTrue("Cannot see converter calculator", calculator.isVisible());
        assertSame("Converter operation is not " + AREA, ((ConverterPanel)calculator.getCurrentPanel()).getConverterType(), AREA);
    }

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
        assertTrue("IsPositive did not return true", calculator.isPositiveNumber("6"));
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
        assertFalse("IsNegative did not return false", calculator.isNegativeNumber("6"));
    }

    @Test
    public void testIsDecimalReturnsTrue()
    {
        assertTrue("Number should contain the '.'", calculator.isDecimal("5.02"));
    }

    @Test
    public void testIsDecimalReturnsFalse()
    {
        assertFalse("Number should not contain the '.'", calculator.isDecimal("5"));
    }

    @Test
    public void methodResetOperatorsWithTrueResultsInAllOperatorsBeingTrue()
    {
        calculator.resetBasicOperators(true);
        assertTrue("isAdding() is not true", calculator.isAdding());
        assertTrue("isSubtracting() is not true", calculator.isSubtracting());
        assertTrue("isMultiplying() is not true", calculator.isMultiplying());
        assertTrue("isDividing() is not true", calculator.isDividing());
    }

    @Test
    public void methodResetOperatorsWithFalseResultsInAllOperatorsBeingFalse()
    {
        calculator.resetBasicOperators(false);
        assertFalse("isAdding() is not false", calculator.isAdding());
        assertFalse("isSubtracting() is not false", calculator.isSubtracting());
        assertFalse("isMultiplying() is not false", calculator.isMultiplying());
        assertFalse("isDividing() is not false", calculator.isDividing());
    }

    @Test
    public void testSetImageIconsWorksAsExpected()
    {
        calculator.setImageIcons();
    }

//    @Test
//    public void testConvertingBinaryToDecimalWorks() throws CalculatorError
//    {
//        // Test that this work
//        //String test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "10000000");
//        //assertEquals(Integer.parseInt(test), 128);
//        // Test another number
//        //test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "11111111");
//        //assertEquals(Integer.parseInt(test), 255);
//        // Make sure this returns the same as above, although i believe this entry by the user is impossible
//        //test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "11111111 ");
//        //assertEquals(Integer.parseInt(test), 255);
//        // Test to make sure an incomplete number returns appropriately
//        //test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, "101");
//        //assertEquals(Integer.parseInt(test), 5);
//        // Test to make sure a WORD BINARY entry returns appropriately
//        //c.setByte(false);
//        //c.setWord(false);
//        String test = c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, new String("00000001 00000000"));
//        assertEquals(Integer.parseInt(test), 256);
//
//    }

}

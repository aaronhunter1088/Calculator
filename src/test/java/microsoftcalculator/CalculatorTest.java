/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microsoftcalculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Component;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Michael Ball
 */
public class CalculatorTest {
    
    public CalculatorTest() {
    }
    
    public void setUp() {
    }
    
    public void tearDown() {
    }
    
    @Test
    public void acceptPositiveSingleDigitNumber() {
        
    }
    
    @Test
    public void acceptPositiveMultipleDigitNumber() {
        
    }
    
    @Test
    public void acceptNegativeSingleDigitNumber() {
        
    }
    
    @Test
    public void acceptNegativeMultipleDigitNumber() {
        
    }

    /**
     * Test of setMenuBar method, of class Calculator.
     */
    @Test
    public void testSetMenuBar() {
        System.out.println("setMenuBar");
        Calculator instance = new Calculator();
        instance.setMenuBar();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setButtonStandardMode method, of class Calculator.
     */
    @Test
    public void testSetButtonStandardMode() {
        System.out.println("setStandardMode");
        Calculator instance = new Calculator();
        instance.setStandardMode();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setButtonBinaryMode method, of class Calculator.
     */
    @Test
    public void testSetButtonBinaryMode() {
        System.out.println("setButtonBinaryMode");
        Calculator instance = new Calculator();
        instance.setBinaryMode();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setMode method, of class Calculator.
     */
    @Test
    public void testSetInitialMode() {
        System.out.println("setInitialMode");
        Boolean mode = null;
        Calculator instance = new Calculator();
        Boolean expResult = null;
        instance.setMode(mode);
        Mode result = instance.getMode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMode method, of class Calculator.
     */
    @Test
    public void testGetMode() {
        System.out.println("getMode");
        Boolean mode = null;
        Calculator instance = new Calculator();
        Boolean expResult = null;
        Boolean result = instance.getMode(mode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBinaryMode method, of class Calculator.
     */
    @Test
    public void testGetBinaryMode() {
        System.out.println("getBinaryMode");
        Calculator instance = new Calculator();
        Boolean expResult = null;
        Boolean result = instance.getBinaryMode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of convertToNegative method, of class Calculator.
     */
    @Test
    public void testConvertToNegative() {
        System.out.println("convertToNegative");
        String number = "";
        Calculator instance = new Calculator();
        String expResult = "";
        String result = instance.convertToNegative(number);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of convertToPositive method, of class Calculator.
     */
    @Test
    public void testConvertToPositive() {
        System.out.println("convertToPositive");
        String number = "";
        Calculator instance = new Calculator();
        String expResult = "";
        String result = instance.convertToPositive(number);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isDecimal method, of class Calculator.
     */
    @Test
    public void testIsDecimal() {
        System.out.println("isDecimal");
        String number = "";
        Calculator instance = new Calculator();
        boolean expResult = false;
        boolean result = instance.isDecimal(number);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isNegativeNumber method, of class Calculator.
     */
    @Test
    public void testIsNegativeNumber() {
        System.out.println("isNegativeNumber");
        String result_2 = "";
        Calculator instance = new Calculator();
        boolean expResult = false;
        boolean result = instance.isNegativeNumber(result_2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addComponent method, of class Calculator.
     */
    @Test
    public void testAddComponent() {
        System.out.println("addComponent");
        Component c = null;
        int row = 0;
        int column = 0;
        int width = 0;
        int height = 0;
        Calculator instance = new Calculator();
        instance.addComponent(c, row, column, width, height);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clear method, of class Calculator.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        Calculator instance = new Calculator();
        instance.clear();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addition method, of class Calculator.
     */
    @Test
    public void testAddition() {
        System.out.println("addition");
        Calculator instance = new Calculator();
        instance.addition();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of subtract method, of class Calculator.
     */
    @Test
    public void testSubtract() {
        System.out.println("subtract");
        Calculator instance = new Calculator();
        instance.subtract();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of multiply method, of class Calculator.
     */
    @Test
    public void testMultiply() {
        System.out.println("multiply");
        Calculator instance = new Calculator();
        instance.multiply();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of divide method, of class Calculator.
     */
    @Test
    public void testDivide() {
        System.out.println("divide");
        Calculator instance = new Calculator();
        instance.divide();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of resetOperator method, of class Calculator.
     */
    @Test
    public void testResetOperator() {
        System.out.println("resetOperator");
        boolean operatorBool = false;
        Calculator instance = new Calculator();
        boolean expResult = false;
        boolean result = instance.resetOperator(operatorBool);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of formatNumber method, of class Calculator.
     */
    @Test
    public void testFormatNumber() {
        System.out.println("formatNumber");
        String num = "";
        Calculator instance = new Calculator();
        String expResult = "";
        String result = instance.formatNumber(num);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of confirm method, of class Calculator.
     */
    @Test
    public void testConfirm() {
        System.out.println("confirm");
        Calculator instance = new Calculator();
        instance.confirm();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTemp method, of class Calculator.
     */
    @Test
    public void testSetTemp() {
        System.out.println("setTemp");
        String input = "";
        Calculator instance = new Calculator();
        instance.setTemp(input);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFinishedText method, of class Calculator.
     */
    @Test
    public void testSetFinishedText() {
        System.out.println("setFinishedText");
        Calculator instance = new Calculator();
        instance.setFinishedText();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

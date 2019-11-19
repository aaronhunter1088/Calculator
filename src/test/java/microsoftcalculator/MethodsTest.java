///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package microsoftcalculator;
//
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.fail;
//
//import org.junit.jupiter.api.Test;
//
///**
// *
// * @author aaron
// */
//public class MethodsTest {
//
//    public MethodsTest() {
//    }
//
//    public static void setUpClass() {
//    }
//
//    public static void tearDownClass() {
//    }
//
//    // passed
//    @Test
//    public void testConvertToNegative() {
//        System.out.println("convertToNegative");
//        String currentNumber = "5";
//        Methods instance = new Methods();
//        String expResult = "-5";
//        String result = instance.convertToNegative(currentNumber);
//        assertEquals(expResult, result);
//        //fail("The test case is a prototype.");
//    }
//
//    // passed
//    @Test
//    public void testConvertToPositive() {
//        System.out.println("convertToPositive");
//        String currentNumber = "-5";
//        Methods instance = new Methods();
//        String expResult = "5";
//        String result = instance.convertToPositive(currentNumber);
//        assertEquals(expResult, result);
//        //fail("The test case is a prototype.");
//    }
//
//    // passed
//    @Test
//    public void testIsDecimal() {
//        System.out.println("isDecimal");
//        String currentNumber = "0.42"; // 0.0, .0, ###.###
//        Methods instance = new Methods();
//        boolean expResult = true;
//        boolean result = instance.isDecimal(currentNumber);
//        assertEquals(expResult, result);
//        //fail("The test case is a prototype.");
//    }
//
//    // passed
//    @Test
//    public void testIsNegativeNumber() {
//        System.out.println("isNegativeNumber test running");
//        String currentNumber = "0.52-";
//        Methods instance = new Methods();
//        boolean expResult = true;
//        boolean result = instance.isNegativeNumber(currentNumber);
//        assertEquals(expResult, result);
//        //fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of clear method, of class Methods.
//     */
//    @Test
//    public void testClear() {
//        System.out.println("clear");
//        Methods instance = new Methods();
//        boolean expResult = false;
//        boolean result = true;
//        instance.clear();
//        if (!instance.getTemp()[0].equals("")) {
//            expResult = true;
//        }
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of addition method, of class Methods.
//     */
//    @Test
//    public void testAddition() {
//        System.out.println("addition");
//        Methods instance = new Methods();
//        instance.addition();
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of subtraction method, of class Methods.
//     */
//    @Test
//    public void testSubtraction() {
//        System.out.println("subtraction");
//        Methods instance = new Methods();
//        instance.subtraction();
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of multiply method, of class Methods.
//     */
//    @Test
//    public void testMultiply() {
//        System.out.println("multiply");
//        Methods instance = new Methods();
//        instance.multiply();
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of divide method, of class Methods.
//     */
//    @Test
//    public void testDivide() {
//        System.out.println("divide");
//        Methods instance = new Methods();
//        instance.divide();
//        fail("The test case is a prototype.");
//    }
//
//    // passed
//    @Test
//    public void testResetOperator() {
//        System.out.println("resetOperator");
//        boolean currentOperator = true;
//        Methods instance = new Methods();
//        boolean expResult = false;
//        boolean result = instance.resetOperator(currentOperator);
//        assertEquals(expResult, result);
//        //fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of formatNumber method, of class Methods.
//     */
//    @Test
//    public void testFormatNumber() {
//        System.out.println("formatNumber");
//        String number = "";
//        Methods instance = new Methods();
//        String expResult = "";
//        String result = instance.formatNumber(number);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of confirm method, of class Methods.
//     */
//    @Test
//    public void testConfirm() {
//        System.out.println("confirm");
//        Methods instance = new Methods();
//        instance.confirm();
//        fail("The test case is a prototype.");
//    }
//
//}

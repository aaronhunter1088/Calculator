package Panels;

import Calculators.Calculator;
import Calculators.CalculatorError;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.event.ActionEvent;

import static org.junit.Assert.*;
import static Types.CalculatorBase.BINARY;
import static Types.CalculatorBase.DECIMAL;

@RunWith(MockitoJUnitRunner.class)
public class JPanelBasicMethodsTest {

    private static Calculator c;
    private static BasicPanel testTheBasicPanel;
    private String number;
    private boolean result;

    @Mock
    ActionEvent ae;

    @BeforeClass
    public static void setup() throws Exception {
        System.setProperty("appName", "JPanelBasicMethodsTest");
        c = new Calculator();
        testTheBasicPanel = new BasicPanel(c);
    }



    @Test
    public void switchingFromProgrammerToBasicConvertsTextArea() throws CalculatorError
    {
        c.textArea.setText("00000100");
        c.convertFromTypeToTypeOnValues(BINARY, DECIMAL, c.getTextAreaWithoutAnything());
        assertEquals("Did not convert from Binary to Decimal", "4", c.getTextareaValueWithoutAnything().toString());
    }

    @Test
    public void testingMathPow() {
        double delta = 0.000001d;
        Number num = 8.0;
        assertEquals(num.doubleValue(), Math.pow(2,3), delta);
    }
}

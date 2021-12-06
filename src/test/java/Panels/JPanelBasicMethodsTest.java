package Panels;

import Calculators.CalculatorError_v4;
import Calculators.Calculator_v4;
import Panels.JPanelBasic_v4;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.event.ActionEvent;

import static org.junit.Assert.*;
import static Enums.CalculatorBase_v4.BINARY;
import static Enums.CalculatorBase_v4.DECIMAL;

@RunWith(MockitoJUnitRunner.class)
public class JPanelBasicMethodsTest {

    private static Calculators.Calculator_v4 c;
    private static Panels.JPanelBasic_v4 testTheBasicPanel;
    private String number;
    private boolean result;

    @Mock
    ActionEvent ae;

    @BeforeClass
    public static void setup() throws Exception {
        System.setProperty("appName", "JPanelBasicMethodsTest");
        c = new Calculator_v4();
        testTheBasicPanel = new JPanelBasic_v4(c);
    }



    @Test
    public void switchingFromProgrammerToBasicConvertsTextArea() throws CalculatorError_v4
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

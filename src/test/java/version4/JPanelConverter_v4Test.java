package version4;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.junit.Assert.assertEquals;
import static version4.ConverterType_v4.*;

public class JPanelConverter_v4Test {

    private static StandardCalculator_v4 testCalculator;
    private static JPanelConverter_v4 testTheConverterPanel;

    @Mock
    ActionEvent ae;

    @BeforeClass
    public static void setup() throws Exception {
        System.setProperty("appName", "JPanelBasicMethodsTest");
        testCalculator = new StandardCalculator_v4();
        testTheConverterPanel = new JPanelConverter_v4(testCalculator, AREA);
        testCalculator.setCurrentPanel(testTheConverterPanel);
    }
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testConvertingFromDegreesToGradiansWorks()
    {
        // 0 grad = 0 degrees
        // 100 grad = 90 degrees
        testTheConverterPanel.setIsTextField1Selected(true);
        testTheConverterPanel.setTextField1(new JTextField("90"));
        testTheConverterPanel.setTextField2(new JTextField("0"));
        assertEquals(100, AngleMethods.convertingDegreesToGradians(testCalculator), 1);
    }

    @Test
    public void testConvertingFromGradiansToDegreesWorks()
    {
        // 0 degrees = 0 grad
        // 90 degrees = 100 grad
        testTheConverterPanel.setIsTextField1Selected(true);
        testTheConverterPanel.setTextField1(new JTextField("100"));
        testTheConverterPanel.setTextField2(new JTextField("0"));
        assertEquals(90, AngleMethods.convertingGradiansToDegrees(testCalculator), 1);
    }

    @Test
    public void testConvertingFromGradiansToRadiansWorks()
    {
        // 636.62 = 10 rad
        testTheConverterPanel.setIsTextField1Selected(true);
        testTheConverterPanel.setTextField1(new JTextField("636.62"));
        testTheConverterPanel.setTextField2(new JTextField("0"));
        assertEquals(10, AngleMethods.convertingGradiansToRadians(testCalculator), 1);
    }

    @Test
    public void testConvertingFromRadiansToGradiansWorks()
    {
        // 10 rad = 636.62 grad
        testTheConverterPanel.setIsTextField1Selected(true);
        testTheConverterPanel.setTextField1(new JTextField("10"));
        testTheConverterPanel.setTextField2(new JTextField("0"));
        assertEquals(636.62, AngleMethods.convertingRadiansToGradians(testCalculator), 1);
    }
}
package Panels;

import Calculators.Calculator;
import Converters.AngleMethods;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import static Types.CalculatorConverterType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ConverterPanelTest
{
    static { System.setProperty("appName", "ConverterPanelTest"); }
    private static Logger LOGGER;
    private static Calculator calculator;
    private static ConverterPanel converterPanel;

    @Mock
    ActionEvent ae;

    @BeforeAll
    public static void beforeClass() throws Exception {
        LOGGER = LogManager.getLogger(ConverterPanelTest.class.getSimpleName());
        calculator = new Calculator();
        calculator.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        converterPanel = new ConverterPanel(calculator, AREA);
        calculator.setCurrentPanel(converterPanel);
    }
    @BeforeEach
    public void setUp() throws Exception {
    }
    @AfterEach
    public void afterEach() {
        if (calculator != null) {
            LOGGER.info("Test complete. Closing the calculator...");
            // Create a WindowEvent with WINDOW_CLOSING event type
            WindowEvent windowClosing = new WindowEvent(calculator, WindowEvent.WINDOW_CLOSING);

            // Dispatch the event to the JFrame instance
            calculator.dispatchEvent(windowClosing);

            // Ensure the clock is no longer visible
            assertFalse(calculator.isVisible());

            // Dispose of the JFrame to release resources
            calculator.dispose();
        }
    }

    @Test
    public void testConvertingFromDegreesToGradiansWorks()
    {
        // 0 grad = 0 degrees
        // 100 grad = 90 degrees
        converterPanel.setIsTextField1Selected(true);
        converterPanel.setTextField1(new JTextField("90"));
        converterPanel.setTextField2(new JTextField("0"));
        assertEquals(100, Converters.AngleMethods.convertingDegreesToGradians(calculator), 1);
    }

    @Test
    public void testConvertingFromGradiansToDegreesWorks()
    {
        // 0 degrees = 0 grad
        // 90 degrees = 100 grad
        converterPanel.setIsTextField1Selected(true);
        converterPanel.setTextField1(new JTextField("100"));
        converterPanel.setTextField2(new JTextField("0"));
        assertEquals(90, Converters.AngleMethods.convertingGradiansToDegrees(calculator), 1);
    }

    @Test
    public void testConvertingFromGradiansToRadiansWorks()
    {
        // 636.62 = 10 rad
        converterPanel.setIsTextField1Selected(true);
        converterPanel.setTextField1(new JTextField("636.62"));
        converterPanel.setTextField2(new JTextField("0"));
        assertEquals(10, Converters.AngleMethods.convertingGradiansToRadians(calculator), 1);
    }

    @Test
    public void testConvertingFromRadiansToGradiansWorks()
    {
        // 10 rad = 636.62 grad
        converterPanel.setIsTextField1Selected(true);
        converterPanel.setTextField1(new JTextField("10"));
        converterPanel.setTextField2(new JTextField("0"));
        assertEquals(636.62, AngleMethods.convertingRadiansToGradians(calculator), 1);
    }
}
package Panels;

import Calculators.Calculator;
import Calculators.CalculatorError;
import Converters.AngleMethods;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import static Types.CalculatorConverterType.ANGLE;
import static Types.Texts.CLEAR_ENTRY;
import static Types.Texts.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

/**
 * ConverterPanelTest
 * <p>
 * This class tests the {@link ConverterPanel} class.
 *
 * @author Michael Ball
 * @version 4.0
 */
@ExtendWith(MockitoExtension.class)
class ConverterPanelTest
{
    private static final Logger LOGGER = LogManager.getLogger(ConverterPanelTest.class.getSimpleName());

    static {
        System.setProperty("appName", ConverterPanelTest.class.getSimpleName());
    }

    @Mock
    ActionEvent ae;
    private Calculator calculator;
    private ConverterPanel converterPanel;

    @BeforeAll
    static void beforeAll()
    {
    }

    @AfterAll
    static void afterAll()
    {
        LOGGER.info("Finished running {}", ConverterPanelTest.class.getSimpleName());
    }

    @BeforeEach
    void beforeEach() throws CalculatorError, UnsupportedLookAndFeelException, ParseException, IOException
    {
        LOGGER.info("setting up each before...");
        calculator = new Calculator();
        converterPanel = calculator.getConverterPanel();
        converterPanel.setupConverterPanel(calculator, ANGLE);
        calculator.setCurrentPanel(converterPanel);
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException
    {
        for (Calculator calculator : java.util.List.of(calculator)) {
            LOGGER.info("Test complete. Closing the calculator...");
            EventQueue.invokeAndWait(() -> {
                calculator.setVisible(false);
                calculator.dispose();
            });
            assertFalse(calculator.isVisible());
        }
    }

    @Test
    void testConvertingFromDegreesToGradiansWorks()
    {
        // 0 grad = 0 degrees
        // 100 grad = 90 degrees
        converterPanel.setIsTextField1Selected(true);
        converterPanel.setTextField1(new JTextField("90"));
        converterPanel.setTextField2(new JTextField("0"));
        assertEquals(100, Converters.AngleMethods.convertingDegreesToGradians(calculator), 1);
    }

    @Test
    void testConvertingFromGradiansToDegreesWorks()
    {
        // 0 degrees = 0 grad
        // 90 degrees = 100 grad
        converterPanel.setIsTextField1Selected(true);
        converterPanel.setTextField1(new JTextField("100"));
        converterPanel.setTextField2(new JTextField("0"));
        assertEquals(90, Converters.AngleMethods.convertingGradiansToDegrees(calculator), 1);
    }

    @Test
    void testConvertingFromGradiansToRadiansWorks()
    {
        // 636.62 = 10 rad
        converterPanel.setIsTextField1Selected(true);
        converterPanel.setTextField1(new JTextField("636.62"));
        converterPanel.setTextField2(new JTextField("0"));
        assertEquals(10, Converters.AngleMethods.convertingGradiansToRadians(calculator), 1);
    }

    @Test
    void testConvertingFromRadiansToGradiansWorks()
    {
        // 10 rad = 636.62 grad
        converterPanel.setIsTextField1Selected(true);
        converterPanel.setTextField1(new JTextField("10"));
        converterPanel.setTextField2(new JTextField("0"));
        assertEquals(636.62, AngleMethods.convertingRadiansToGradians(calculator), 1);
    }

    @Test
    void testPerformClearEntryButtonAction()
    {
        when(ae.getActionCommand()).thenReturn(CLEAR_ENTRY);
        converterPanel.performClearEntryButtonActions(ae);
        assertEquals(ZERO, converterPanel.getTextField1().getText());
        assertEquals(ZERO, converterPanel.getTextField2().getText());
    }
}
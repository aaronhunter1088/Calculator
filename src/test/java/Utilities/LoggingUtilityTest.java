package Utilities;

import Calculators.Calculator;
import Interfaces.CalculatorType;
import Parent.TestParent;
import Types.CalculatorConverterType;
import Types.CalculatorView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import static org.mockito.Mockito.spy;

/**
 * LoggingUtilityTest
 * <p>
 * This class tests the {@link LoggingUtilityTest} class.
 * <p>
 *
 * @author Michael Ball
 * @version 4.0
 */
public class LoggingUtilityTest extends TestParent
{
    private static final Logger LOGGER = LogManager.getLogger(LoggingUtilityTest.class);

    static {
        System.setProperty("appName", LoggingUtilityTest.class.getSimpleName());
    }

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(() -> {
            try {
                LOGGER.info("Starting Logging Utility Test");
                calculator = spy(new Calculator());
                Preferences.userNodeForPackage(Calculator.class).clear(); // remove keys
                calculator.setSystemDetector(systemDetector);
                calculator.getBasicPanel().setCalculator(calculator); // links spyCalc to ProgrammerPanel
            }
            catch (Exception ignored) {
            }
        });
    }

    @ParameterizedTest
    @MethodSource("confirmCases")
    @DisplayName("Test confirm")
    public void testConfirm(CalculatorType view)
    {
        if (view instanceof CalculatorConverterType) calculator.setCalculatorView(CalculatorView.VIEW_CONVERTER);
        calculator.performViewMenuAction(actionEvent, view);
        LoggingUtility.confirm(calculator, LOGGER, "Test");
    }

    private static Stream<CalculatorType> confirmCases()
    {
        return Stream.of(
                CalculatorView.VIEW_BASIC,
                CalculatorView.VIEW_PROGRAMMER,
                CalculatorView.VIEW_SCIENTIFIC,
                CalculatorConverterType.ANGLE,
                CalculatorConverterType.AREA,
                CalculatorView.VIEW_DATE
        );
    }
}

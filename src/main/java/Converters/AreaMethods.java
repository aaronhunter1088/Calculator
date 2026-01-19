package Converters;

import Calculators.Calculator;
import Panels.ConverterPanel;
import Types.CalculatorConverterUnits;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AreaMethods
 * <p>
 * This class contains static methods used
 * for quick area conversions
 *
 * @author Michael Ball
 * @version 4.0
 */
public class AreaMethods
{
    private final static Logger LOGGER = LogManager.getLogger(AreaMethods.class.getSimpleName());

    /**
     * The main method used to determine from
     * what unit to convert to
     *
     * @param calculator the Calculator object
     */
    public static void convertValues(Calculator calculator)
    {
        LOGGER.debug("starting conversion");
        CalculatorConverterUnits unit1 = (CalculatorConverterUnits) ((ConverterPanel) calculator.getCurrentPanel()).getUnitOptions1().getSelectedItem();
        CalculatorConverterUnits unit2 = (CalculatorConverterUnits) ((ConverterPanel) calculator.getCurrentPanel()).getUnitOptions2().getSelectedItem();
        LOGGER.info("unit1: " + unit1);
        LOGGER.info("unit2: " + unit2);
        double number;
        if (calculator.getConverterPanel().isTextField1Selected()) {
            LOGGER.warn("Implement AREA methods from textArea1 to textArea2");
        } else // going from unit2 to unit1
        {
            LOGGER.warn("Implement AREA methods from textAres2 to textArea1");
        }
    }
}
package calculator.entities;

import calculator.panels.ConverterPanel;
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
        LOGGER.info("unit1: {}", unit1);
        LOGGER.info("unit2: {}", unit2);

        if (calculator.getConverterPanel().isTextField1Selected()) {
            double input = Double.parseDouble(((ConverterPanel) calculator.getCurrentPanel()).getTextField1().getText());
            double result = convert(input, unit1, unit2);
            String text = String.valueOf(result).endsWith(".0")
                    ? String.valueOf((int) result)
                    : String.valueOf(result);
            ((ConverterPanel) calculator.getCurrentPanel()).getTextField2().setText(text);
        } else {
            double input = Double.parseDouble(((ConverterPanel) calculator.getCurrentPanel()).getTextField2().getText());
            double result = convert(input, unit2, unit1);
            String text = String.valueOf(result).endsWith(".0")
                    ? String.valueOf((int) result)
                    : String.valueOf(result);
            ((ConverterPanel) calculator.getCurrentPanel()).getTextField1().setText(text);
        }
    }

    /**
     * Converts the given value from one area unit to another.
     * Uses square meters as the intermediate unit for all conversions.
     * {@code SQUARE_YARD_ACRES} is treated as square yards.
     *
     * @param value the input value to convert
     * @param from  the source {@link CalculatorConverterUnits}
     * @param to    the target {@link CalculatorConverterUnits}
     * @return the converted value
     */
    public static double convert(double value, CalculatorConverterUnits from, CalculatorConverterUnits to)
    {
        if (from == to) return value;
        double squareMeters = toSquareMeters(value, from);
        return fromSquareMeters(squareMeters, to);
    }

    /**
     * Converts a value in the given unit to square meters.
     *
     * @param value the input value
     * @param unit  the source unit
     * @return the equivalent value in square meters
     */
    private static double toSquareMeters(double value, CalculatorConverterUnits unit)
    {
        return switch (unit) {
            case SQUARE_MILLIMETERS -> value * 1e-6;
            case SQUARE_CENTIMETERS -> value * 1e-4;
            case SQUARE_METERS      -> value;
            case HECTARES           -> value * 1e4;
            case SQUARE_KILOMETERS  -> value * 1e6;
            case SQUARE_INCHES      -> value * 6.4516e-4;
            case SQUARE_FEET        -> value * 9.290304e-2;
            case SQUARE_YARD_ACRES  -> value * 8.3612736e-1;   // square yards
            case SQUARE_MILES       -> value * 2_589_988.110336;
            default                 -> value;
        };
    }

    /**
     * Converts a value in square meters to the given unit.
     *
     * @param sqM  the value in square meters
     * @param unit the target unit
     * @return the equivalent value in the target unit
     */
    private static double fromSquareMeters(double sqM, CalculatorConverterUnits unit)
    {
        return switch (unit) {
            case SQUARE_MILLIMETERS -> sqM * 1e6;
            case SQUARE_CENTIMETERS -> sqM * 1e4;
            case SQUARE_METERS      -> sqM;
            case HECTARES           -> sqM / 1e4;
            case SQUARE_KILOMETERS  -> sqM / 1e6;
            case SQUARE_INCHES      -> sqM / 6.4516e-4;
            case SQUARE_FEET        -> sqM / 9.290304e-2;
            case SQUARE_YARD_ACRES  -> sqM / 8.3612736e-1;    // square yards
            case SQUARE_MILES       -> sqM / 2_589_988.110336;
            default                 -> sqM;
        };
    }
}
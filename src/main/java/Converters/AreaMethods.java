package Converters;

import Calculators.Calculator_v4;
import Panels.ConverterPanel;
import Types.ConverterUnits;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("Duplicates")
public abstract class AreaMethods
{
    private final static Logger LOGGER = LogManager.getLogger(AreaMethods.class.getSimpleName());

    public static void convertValues(Calculator_v4 calculator)
    {
        LOGGER.debug("starting conversion");
        ConverterUnits unit1 = (ConverterUnits) ((ConverterPanel)calculator.getCurrentPanel()).getUnitOptions1().getSelectedItem();
        ConverterUnits unit2 = (ConverterUnits) ((ConverterPanel)calculator.getCurrentPanel()).getUnitOptions2().getSelectedItem();
        LOGGER.info("unit1: " + unit1);
        LOGGER.info("unit2: " + unit2);
        double number;
        if (((ConverterPanel)calculator.getCurrentPanel()).isTextField1Selected())
        {
            LOGGER.warn("Implement AREA methods from textArea1 to textArea2");
        }
        else // going from unit2 to unit1
        {
            LOGGER.warn("Implement AREA methods from textAres2 to textArea1");
        }
    }
}
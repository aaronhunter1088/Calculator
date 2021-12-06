package Converters;

import Enums.ConverterUnits;
import Panels.JPanelConverter_v4;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Calculators.Calculator_v4;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public abstract class AngleMethods
{
    private final static Logger LOGGER = LogManager.getLogger(AngleMethods.class);
    public static final String DEGREES = "Degrees";
    public static final String RADIANS = "Radians";
    public static final String GRADIANS = "Gradians";

    public static void convertValues(Calculator_v4 calculator)
    {
        LOGGER.debug("starting conversion");
        Enums.ConverterUnits unit1 = ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).convertStringUnitToConverterUnits((String) Objects.requireNonNull(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getUnitOptions1().getSelectedItem()));
        Enums.ConverterUnits unit2 = ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).convertStringUnitToConverterUnits((String)Objects.requireNonNull(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getUnitOptions2().getSelectedItem()));
        double number;
        if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected())
        {
            if (unit1 == Enums.ConverterUnits.DEGREES) {
                if (unit2 == Enums.ConverterUnits.DEGREES) {
                    ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
                } // WORKING!
                else if (unit2 == Enums.ConverterUnits.RADIANS) {
                    number = AngleMethods.convertingDegreesToRadians(calculator);
                    //number = convertingDegreesToRadians();
                    if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                } // WORKING!
                else { // unit2 is ConverterUnits.GRADIANS
                    number = AngleMethods.convertingDegreesToGradians(calculator);
                    //number = convertingDegreesToGradians();
                    if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
            }
            else if (unit1 == Enums.ConverterUnits.RADIANS) {
                if (unit2 == Enums.ConverterUnits.DEGREES) {
                    number = AngleMethods.convertingRadiansToDegrees(calculator);
                    //number = convertingRadiansToDegrees();
                    if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                } // WORKING!
                else if (unit2 == Enums.ConverterUnits.RADIANS) {
                    ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
                } // WORKING!
                else { // unit2 is ConverterUnits.GRADIANS
                    number = AngleMethods.convertingRadiansToGradians(calculator);
                    //number = convertingRadiansToGradians();
                    if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
            }
            else { // unit1 is ConverterUnits.GRADIANS
                if (unit2 == Enums.ConverterUnits.DEGREES) {
                    number = AngleMethods.convertingGradiansToDegrees(calculator);
                    //number = convertingGradiansToDegrees();
                    if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
                else if (unit2 == Enums.ConverterUnits.RADIANS) {
                    number = AngleMethods.convertingGradiansToRadians(calculator);
                    //number = convertingGradiansToRadians();
                    if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
                else { // unit2 is ConverterUnits.GRADIANS
                    ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
                }
            }
        }
        else // going from unit2 to unit1
        {
            if (unit1 == Enums.ConverterUnits.DEGREES) {
                if (unit2 == Enums.ConverterUnits.DEGREES) {
                    // best case scenario. get to copy one to the other
                    ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
                } // WORKING!
                else if (unit2 == Enums.ConverterUnits.RADIANS) {
                    number = AngleMethods.convertingRadiansToDegrees(calculator);
                    //number = convertingRadiansToDegrees();
                    if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                } // WORKING!
                else { // unit2 is ConverterUnits.GRADIANS
                    number = AngleMethods.convertingGradiansToDegrees(calculator);
                    //number = convertingGradiansToDegrees();
                    if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
            }
            else if (unit1 == Enums.ConverterUnits.RADIANS) {
                if (unit2 == Enums.ConverterUnits.DEGREES) {
                    number = AngleMethods.convertingDegreesToRadians(calculator);
                    //number = convertingDegreesToRadians();
                    if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                } // WORKING!
                else if (unit2 == Enums.ConverterUnits.RADIANS) {
                    ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
                } // WORKING!
                else { // unit2 is ConverterUnits.GRADIANS
                    number = AngleMethods.convertingRadiansToGradians(calculator);
                    //number = convertingRadiansToGradians();
                    if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
            }
            else { // unit1 is ConverterUnits.GRADIANS
                if (unit2 == Enums.ConverterUnits.DEGREES) {
                    number = AngleMethods.convertingGradiansToDegrees(calculator);
                    //number = convertingGradiansToDegrees();
                    if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
                else if (unit2 == ConverterUnits.RADIANS) {
                    number = AngleMethods.convertingGradiansToRadians(calculator);
                    //number = convertingGradiansToRadians();
                    if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
                else { // unit2 is ConverterUnits.GRADIANS
                    ((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
                }
            }
        }
    }

    public static double convertingRadiansToGradians(Calculator_v4 calculator)
    {
        double radians;
        if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected())
        {
            radians = Double.parseDouble(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
        }
        else
        {
            radians = Double.parseDouble(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
        }
        LOGGER.info("Radians to Degrees");
        LOGGER.debug("radians: " + radians);
        BigDecimal gradians = BigDecimal.valueOf(radians * 63.662);
        double x = gradians.setScale(4, RoundingMode.UP).doubleValue();
        LOGGER.info("gradians: " + x);
        return x;
    }

    public static double convertingGradiansToRadians(Calculator_v4 calculator)
    {
        double gradians;
        if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected())
        {
            gradians = Double.parseDouble(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
        }
        else
        {
            gradians = Double.parseDouble(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
        }
        LOGGER.info("Gradians to Radians");
        LOGGER.debug("gradians: " + gradians);
        BigDecimal radians = new BigDecimal(gradians / 63.662);
        double x = radians.setScale(4, RoundingMode.UP).doubleValue();
        LOGGER.debug("radians: " + x);
        return x;
    }

    public static double convertingDegreesToGradians(Calculator_v4 calculator)
    {
        double degrees;
        if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected())
        {
            degrees = Double.parseDouble(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
        }
        else
        {
            degrees = Double.parseDouble(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
        }
        LOGGER.info("Degrees to Gradians");
        LOGGER.debug("degrees: " + degrees);
        BigDecimal gradians = new BigDecimal(degrees * 1.111111);
        double x = gradians.setScale(4, RoundingMode.UP).doubleValue();
        LOGGER.debug("gradians: " + x);
        return x;
    }

    public static double convertingGradiansToDegrees(Calculator_v4 calculator)
    {
        double gradians;
        if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected())
        {
            gradians = Double.parseDouble(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
        } else
        {
            gradians = Double.parseDouble(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
        }
        LOGGER.info("Gradians to Degrees");
        LOGGER.debug("gradians: " + gradians);
        BigDecimal degrees = new BigDecimal(gradians / 1.111111);
        double x = degrees.setScale(4, RoundingMode.UP).doubleValue();
        LOGGER.debug("degrees: " + x);
        return x;
    }

    public static double convertingDegreesToRadians(Calculator_v4 calculator)
    {
        double degrees;
        if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected())
        {
            degrees = Double.parseDouble(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
        }
        else {
            degrees = Double.parseDouble(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
        }
        LOGGER.info("Degrees to Radians");
        LOGGER.debug("degrees: " + degrees);
        BigDecimal radians = BigDecimal.valueOf(Math.toRadians(degrees));
        double x = radians.setScale(4, RoundingMode.UP).doubleValue();
        LOGGER.info("radians: " + x);
        return x;
    }

    public static double convertingRadiansToDegrees(Calculator_v4 calculator)
    {
        double radians;
        if (((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected())
        {
            radians = Double.parseDouble(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
        }
        else
        {
            radians = Double.parseDouble(((Panels.JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
        }
        LOGGER.info("Radians to Degrees");
        LOGGER.debug("radians: " + radians);
        BigDecimal degrees = BigDecimal.valueOf(Math.toDegrees(radians));
        double x = degrees.setScale(4, RoundingMode.UP).doubleValue();
        LOGGER.info("degrees: " + x);
        return x;
    }
}

package version4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public abstract class AngleMethods
{
    private final static Logger LOGGER = LogManager.getLogger(AngleMethods.class);
    static final String DEGREES = "Degrees";
    static final String RADIANS = "Radians";
    static final String GRADIANS = "Gradians";

    static void convertValues(Calculator_v4 calculator)
    {
        LOGGER.debug("starting conversion");
        ConverterUnits unit1 = ((JPanelConverter_v4)calculator.getCurrentPanel()).convertStringUnitToConverterUnits((String) Objects.requireNonNull(((JPanelConverter_v4)calculator.getCurrentPanel()).getUnitOptions1().getSelectedItem()));
        ConverterUnits unit2 = ((JPanelConverter_v4)calculator.getCurrentPanel()).convertStringUnitToConverterUnits((String)Objects.requireNonNull(((JPanelConverter_v4)calculator.getCurrentPanel()).getUnitOptions2().getSelectedItem()));
        double number;
        if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected())
        {
            if (unit1 == ConverterUnits.DEGREES) {
                if (unit2 == ConverterUnits.DEGREES) {
                    ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
                } // WORKING!
                else if (unit2 == ConverterUnits.RADIANS) {
                    number = AngleMethods.convertingDegreesToRadians(calculator);
                    //number = convertingDegreesToRadians();
                    if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                } // WORKING!
                else { // unit2 is ConverterUnits.GRADIANS
                    number = AngleMethods.convertingDegreesToGradians(calculator);
                    //number = convertingDegreesToGradians();
                    if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
            }
            else if (unit1 == ConverterUnits.RADIANS) {
                if (unit2 == ConverterUnits.DEGREES) {
                    number = AngleMethods.convertingRadiansToDegrees(calculator);
                    //number = convertingRadiansToDegrees();
                    if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                } // WORKING!
                else if (unit2 == ConverterUnits.RADIANS) {
                    ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
                } // WORKING!
                else { // unit2 is ConverterUnits.GRADIANS
                    number = AngleMethods.convertingRadiansToGradians(calculator);
                    //number = convertingRadiansToGradians();
                    if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
            }
            else { // unit1 is ConverterUnits.GRADIANS
                if (unit2 == ConverterUnits.DEGREES) {
                    number = AngleMethods.convertingGradiansToDegrees(calculator);
                    //number = convertingGradiansToDegrees();
                    if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
                else if (unit2 == ConverterUnits.RADIANS) {
                    number = AngleMethods.convertingGradiansToRadians(calculator);
                    //number = convertingGradiansToRadians();
                    if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
                else { // unit2 is ConverterUnits.GRADIANS
                    ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
                }
            }
        }
        else // going from unit2 to unit1
        {
            if (unit1 == ConverterUnits.DEGREES) {
                if (unit2 == ConverterUnits.DEGREES) {
                    // best case scenario. get to copy one to the other
                    ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
                } // WORKING!
                else if (unit2 == ConverterUnits.RADIANS) {
                    number = AngleMethods.convertingRadiansToDegrees(calculator);
                    //number = convertingRadiansToDegrees();
                    if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                } // WORKING!
                else { // unit2 is ConverterUnits.GRADIANS
                    number = AngleMethods.convertingGradiansToDegrees(calculator);
                    //number = convertingGradiansToDegrees();
                    if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
            }
            else if (unit1 == ConverterUnits.RADIANS) {
                if (unit2 == ConverterUnits.DEGREES) {
                    number = AngleMethods.convertingDegreesToRadians(calculator);
                    //number = convertingDegreesToRadians();
                    if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                } // WORKING!
                else if (unit2 == ConverterUnits.RADIANS) {
                    ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
                } // WORKING!
                else { // unit2 is ConverterUnits.GRADIANS
                    number = AngleMethods.convertingRadiansToGradians(calculator);
                    //number = convertingRadiansToGradians();
                    if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
            }
            else { // unit1 is ConverterUnits.GRADIANS
                if (unit2 == ConverterUnits.DEGREES) {
                    number = AngleMethods.convertingGradiansToDegrees(calculator);
                    //number = convertingGradiansToDegrees();
                    if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
                else if (unit2 == ConverterUnits.RADIANS) {
                    number = AngleMethods.convertingGradiansToRadians(calculator);
                    //number = convertingGradiansToRadians();
                    if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf((int) number));
                    } else if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && !String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(String.valueOf(number));
                    } else if (!((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected() && String.valueOf(number).endsWith(".0")) {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf((int) number));
                    } else {
                        ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().setText(String.valueOf(number));
                    }
                }
                else { // unit2 is ConverterUnits.GRADIANS
                    ((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().setText(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
                }
            }
        }
    }

    static double convertingRadiansToGradians(Calculator_v4 calculator)
    {
        double radians;
        if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected())
        {
            radians = Double.parseDouble(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
        }
        else
        {
            radians = Double.parseDouble(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
        }
        LOGGER.info("Radians to Degrees");
        LOGGER.debug("radians: " + radians);
        BigDecimal gradians = BigDecimal.valueOf(radians * 63.662);
        double x = gradians.setScale(4, RoundingMode.UP).doubleValue();
        LOGGER.info("gradians: " + x);
        return x;
    }

    static double convertingGradiansToRadians(Calculator_v4 calculator)
    {
        double gradians;
        if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected())
        {
            gradians = Double.parseDouble(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
        }
        else
        {
            gradians = Double.parseDouble(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
        }
        LOGGER.info("Gradians to Radians");
        LOGGER.debug("gradians: " + gradians);
        BigDecimal radians = new BigDecimal(gradians / 63.662);
        double x = radians.setScale(4, RoundingMode.UP).doubleValue();
        LOGGER.debug("radians: " + x);
        return x;
    }

    static double convertingDegreesToGradians(Calculator_v4 calculator)
    {
        double degrees;
        if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected())
        {
            degrees = Double.parseDouble(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
        }
        else
        {
            degrees = Double.parseDouble(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
        }
        LOGGER.info("Degrees to Gradians");
        LOGGER.debug("degrees: " + degrees);
        BigDecimal gradians = new BigDecimal(degrees * 1.111111);
        double x = gradians.setScale(4, RoundingMode.UP).doubleValue();
        LOGGER.debug("gradians: " + x);
        return x;
    }

    static double convertingGradiansToDegrees(Calculator_v4 calculator)
    {
        double gradians;
        if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected())
        {
            gradians = Double.parseDouble(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
        } else
        {
            gradians = Double.parseDouble(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
        }
        LOGGER.info("Gradians to Degrees");
        LOGGER.debug("gradians: " + gradians);
        BigDecimal degrees = new BigDecimal(gradians / 1.111111);
        double x = degrees.setScale(4, RoundingMode.UP).doubleValue();
        LOGGER.debug("degrees: " + x);
        return x;
    }

    static double convertingDegreesToRadians(Calculator_v4 calculator)
    {
        double degrees;
        if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected())
        {
            degrees = Double.parseDouble(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
        }
        else {
            degrees = Double.parseDouble(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
        }
        LOGGER.info("Degrees to Radians");
        LOGGER.debug("degrees: " + degrees);
        BigDecimal radians = BigDecimal.valueOf(Math.toRadians(degrees));
        double x = radians.setScale(4, RoundingMode.UP).doubleValue();
        LOGGER.info("radians: " + x);
        return x;
    }

    static double convertingRadiansToDegrees(Calculator_v4 calculator)
    {
        double radians;
        if (((JPanelConverter_v4)calculator.getCurrentPanel()).isTextField1Selected())
        {
            radians = Double.parseDouble(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField1().getText());
        }
        else
        {
            radians = Double.parseDouble(((JPanelConverter_v4)calculator.getCurrentPanel()).getTextField2().getText());
        }
        LOGGER.info("Radians to Degrees");
        LOGGER.debug("radians: " + radians);
        BigDecimal degrees = BigDecimal.valueOf(Math.toDegrees(radians));
        double x = degrees.setScale(4, RoundingMode.UP).doubleValue();
        LOGGER.info("degrees: " + x);
        return x;
    }
}

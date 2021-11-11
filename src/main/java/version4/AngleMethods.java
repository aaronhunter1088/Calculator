package version4;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class AngleMethods
{
    static final String DEGREES = "Degrees";
    static final String RADIANS = "Radians";
    static final String GRADIANS = "Gradians";

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
        calculator.getLogger().info("Radians to Degrees");
        calculator.getLogger().debug("radians: " + radians);
        BigDecimal gradians = BigDecimal.valueOf(radians * 63.662);
        double x = gradians.setScale(4, RoundingMode.UP).doubleValue();
        calculator.getLogger().info("gradians: " + x);
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
        calculator.getLogger().info("Gradians to Radians");
        calculator.getLogger().debug("gradians: " + gradians);
        BigDecimal radians = new BigDecimal(gradians / 63.662);
        double x = radians.setScale(4, RoundingMode.UP).doubleValue();
        calculator.getLogger().debug("radians: " + x);
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
        calculator.getLogger().info("Degrees to Gradians");
        calculator.getLogger().debug("degrees: " + degrees);
        BigDecimal gradians = new BigDecimal(degrees * 1.111111);
        double x = gradians.setScale(4, RoundingMode.UP).doubleValue();
        calculator.getLogger().debug("gradians: " + x);
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
        calculator.getLogger().info("Gradians to Degrees");
        calculator.getLogger().debug("gradians: " + gradians);
        BigDecimal degrees = new BigDecimal(gradians / 1.111111);
        double x = degrees.setScale(4, RoundingMode.UP).doubleValue();
        calculator.getLogger().debug("degrees: " + x);
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
        calculator.getLogger().info("Degrees to Radians");
        calculator.getLogger().debug("degrees: " + degrees);
        BigDecimal radians = BigDecimal.valueOf(Math.toRadians(degrees));
        double x = radians.setScale(4, RoundingMode.UP).doubleValue();
        calculator.getLogger().info("radians: " + x);
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
        calculator.getLogger().info("Radians to Degrees");
        calculator.getLogger().debug("radians: " + radians);
        BigDecimal degrees = BigDecimal.valueOf(Math.toDegrees(radians));
        double x = degrees.setScale(4, RoundingMode.UP).doubleValue();
        calculator.getLogger().info("degrees: " + x);
        return x;
    }
}

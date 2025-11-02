package Types;

/**
 * CalculatorConverterType
 * <p>
 * This enum contains the different
 * converter types for the calculator
 *
 * @author Michael Ball
 * @version 4.0
 */
public enum CalculatorConverterType implements CalculatorType
{
    ANGLE("Angle"),
    AREA("Area");

    private final String name;
    CalculatorConverterType(String name) {
        this.name = name;
    }
    public String getValue() {
        return name;
    }
    public String getName() { return this.name(); }
}

package Types;

/**
 * DateOperation
 * <p>
 * This enum contains the different
 * date operations for the calculator
 *
 * @author Michael Ball
 * @version 4.0
 */
public enum DateOperation implements CalculatorType
{
    DIFFERENCE_BETWEEN_DATES("Difference between dates"),
    ADD_SUBTRACT_DAYS("Add or subtract days");

    private final String value;
    DateOperation(String value) { this.value = value; }
    public String getValue() {
        return value;
    }
    public String getName() { return this.name(); }
}

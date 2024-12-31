package Types;

/**
 * The different types of DatePanels
 * DateOperation.name() returns ENUM value
 * DateOperation.getValue() returns ENUM("thisValue")
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

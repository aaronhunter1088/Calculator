package Types;

/**
 * The different types of DatePanels
 * DateOperation.name() returns ENUM value
 * DateOperation.getValue() returns ENUM("thisValue")
 */
public enum DateOperation
{
    DIFFERENCE_BETWEEN_DATES("Difference between dates"),
    ADD_SUBTRACT_DAYS("Add or subtract days");

    private final String name;
    DateOperation(String name) {
        this.name = name;
    }
    public String getValue() {
        return this.name;
    }
}

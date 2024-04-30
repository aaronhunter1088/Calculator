package Types;

public enum DateOperation
{
    DIFFERENCE_BETWEEN_DATES("Difference between dates"),
    ADD_SUBTRACT_DAYS("Add or subtract days");

    private final String name;
    DateOperation(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
}

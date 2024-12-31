package Types;

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

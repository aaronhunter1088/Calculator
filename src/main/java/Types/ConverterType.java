package Types;

/**
 * The different types of Conversions possible when
 * using the ConverterPanel
 * ConverterType.name() returns ENUM value
 * ConverterType.getValue() returns ENUM("thisValue")
 */
public enum ConverterType
{
    ANGLE("Angle"),
    AREA("Area");

    private final String name;
    ConverterType(String name) {
        this.name = name;
    }
    public String getValue() {
        return this.name;
    }
}

package Types;

/**
 * The different types of Conversions possible when
 * using the ConverterPanel
 */
public enum ConverterType
{
    ANGLE("Angle"),
    AREA("Area");

    private final String name;
    ConverterType(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
}

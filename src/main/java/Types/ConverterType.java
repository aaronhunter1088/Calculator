package Types;

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

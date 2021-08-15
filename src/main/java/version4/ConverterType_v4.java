package version4;

public enum ConverterType_v4
{
    // Types
    ANGLE("Angle"),
    AREA("Area"),


    CONVERTER("Converter");

    // TODO: Add converter types
    private String name;
    ConverterType_v4(String s1) {
        setName(s1);
    }

    private void setName(String name) {
        this.name = name;
    }
    protected String getName() {
        return this.name;
    }
}

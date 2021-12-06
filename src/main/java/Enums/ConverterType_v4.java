package Enums;

public enum ConverterType_v4
{
    ANGLE("Angle"),
    AREA("Area");

    private String name;
    ConverterType_v4(String s1) {
        setName(s1);
    }

    private void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
}

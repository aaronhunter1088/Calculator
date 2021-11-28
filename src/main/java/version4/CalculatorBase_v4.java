package version4;

public enum CalculatorBase_v4 {

    BINARY("Binary"),
    OCTAL("Octal"),
    DECIMAL("Decimal"),
    HEXIDECIMAL("Hexidecimal");

    private String name;
    CalculatorBase_v4(String s1) {
        setName(s1);
    }

    private void setName(String name) {
        this.name = name;
    }
    protected String getName() {
        return this.name;
    }
}

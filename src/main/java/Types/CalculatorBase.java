package Types;

/**
 * The CalculatorBases to use primarily in OLDProgrammerPanel
 * CalculatorBase.name() returns ENUM value
 * CalculatorBase.getValue() returns ENUM("thisValue")
 */
public enum CalculatorBase
{
    BASE_BINARY("Binary"),
    BASE_OCTAL("Octal"),
    BASE_DECIMAL("Decimal"),
    BASE_HEXADECIMAL("Hexadecimal");

    private final String name;
    CalculatorBase(String name) {
        this.name = name;
    }
    public String getValue() {
        return this.name;
    }
}

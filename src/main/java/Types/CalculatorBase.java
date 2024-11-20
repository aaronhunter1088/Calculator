package Types;

/**
 * The CalculatorBases to use primarily in OLDProgrammerPanel
 * CalculatorBase.name() returns ENUM value
 * CalculatorBase.getValue() returns ENUM("thisValue")
 */
public enum CalculatorBase
{
    BASE_BINARY("Binary", 2),
    BASE_OCTAL("Octal", 8),
    BASE_DECIMAL("Decimal", 10),
    BASE_HEXADECIMAL("Hexadecimal", 16);

    private final String name;
    private final int radix;
    CalculatorBase(String name, int radix) { this.name = name; this.radix = radix; }
    public String getValue() {
        return this.name;
    }
    public int getRadix() {
        return this.radix;
    }
}

package Types;

/**
 * The CalculatorBases to use primarily in ProgrammerPanel
 */
public enum CalculatorBase
{
    BINARY("Binary"),
    OCTAL("Octal"),
    DECIMAL("Decimal"),
    HEXADECIMAL("Hexadecimal");

    private final String name;
    CalculatorBase(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
}

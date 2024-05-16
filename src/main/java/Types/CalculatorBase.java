package Types;

/**
 * The CalculatorBases to use primarily in ProgrammerPanel
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
    public String getName() {
        return this.name;
    }
}

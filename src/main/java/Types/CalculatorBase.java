package Types;

import Interfaces.CalculatorType;

/**
 * CalculatorBase
 * <p>
 * This enum contains the different
 * calculator bases
 *
 * @author Michael Ball
 * @version 4.0
 */
public enum CalculatorBase implements CalculatorType
{
    BASE_BINARY("Binary", 2),
    BASE_OCTAL("Octal", 8),
    BASE_DECIMAL("Decimal", 10),
    BASE_HEXADECIMAL("Hexadecimal", 16);

    private final String name;
    private final int radix;

    CalculatorBase(String name, int radix)
    {
        this.name = name;
        this.radix = radix;
    }

    public String getValue()
    {
        return name;
    }

    public String getName()
    {
        return this.name();
    }

    public int getRadix()
    {
        return this.radix;
    }
}

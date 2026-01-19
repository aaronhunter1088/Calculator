package Types;

import Interfaces.CalculatorType;

/**
 * CalculatorByte
 * <p>
 * This enum contains the different
 * byte units for the calculator
 *
 * @author Michael Ball
 * @version 4.0
 */
public enum CalculatorByte implements CalculatorType
{
    BYTE_BYTE("Byte"),
    BYTE_WORD("Word"),
    BYTE_DWORD("DWord"),
    BYTE_QWORD("QWord");

    private final String value;

    CalculatorByte(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public String getName()
    {
        return this.name();
    }
}

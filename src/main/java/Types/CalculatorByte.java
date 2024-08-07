package Types;

/**
 * The different types of Calculators available
 * CalculatorByte.name() returns ENUM value
 * CalculatorByte.getValue() returns ENUM("thisValue")
 */
public enum CalculatorByte
{
    BYTE_BYTE("Byte"),
    BYTE_WORD("Word"),
    BYTE_DWORD("DWord"),
    BYTE_QWORD("QWord");

    private final String value;
    CalculatorByte(String value) {
        this.value = value;
    }
    public String getValue() { return this.value; }
}

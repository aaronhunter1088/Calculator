package Types;

public enum CalculatorByte implements CalculatorType
{
    BYTE_BYTE("Byte"),
    BYTE_WORD("Word"),
    BYTE_DWORD("DWord"),
    BYTE_QWORD("QWord");

    private final String value;
    CalculatorByte(String value) {
        this.value = value;
    }
    public String getValue() { return value; }
    public String getName() { return this.name(); }
}

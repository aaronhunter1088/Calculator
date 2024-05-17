package Types;

/**
 * The different types of Calculators available
 * CalculatorType.name() returns ENUM value
 * CalculatorType.getValue() returns ENUM("thisValue")
 */
public enum CalculatorType
{
	BASIC("Basic"),
	SCIENTIFIC("Scientific"),
	PROGRAMMER("Programmer"),
	DATE("Date"),
	CONVERTER("Converter");

	private final String value;
	CalculatorType(String value) {
		this.value = value;
	}
	public String getValue() { return this.value; }
}

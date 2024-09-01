package Types;

/**
 * The different types of Calculators available
 * CalculatorView.name() returns ENUM value
 * CalculatorView.getValue() returns ENUM("thisValue")
 */
public enum CalculatorView
{
	VIEW_BASIC("Basic"),
	VIEW_PROGRAMMER("Programmer"),
	VIEW_SCIENTIFIC("Scientific"),
	VIEW_DATE("Date"),
	VIEW_CONVERTER("Converter");

	private final String value;
	CalculatorView(String value) {
		this.value = value;
	}
	public String getValue() { return this.value; }
}

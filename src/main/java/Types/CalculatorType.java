package Types;

public enum CalculatorType
{
	BASIC("Basic"),
	SCIENTIFIC("Scientific"),
	PROGRAMMER("Programmer"),
	DATE("Date"),
	CONVERTER("Converter");

	private final String name;
	CalculatorType(String name) {
		this.name = name;
	}
	public String getName() { return this.name; }
}

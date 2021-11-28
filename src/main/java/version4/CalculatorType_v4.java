package version4;

public enum CalculatorType_v4
{
	// Types
	BASIC("Basic"),
	SCIENTIFIC("Scientific"),
	PROGRAMMER("Programmer"),
	DATE("Date"),
	// NEW
	CONVERTER("Converter");

	private String name;
	CalculatorType_v4(String s1) {
		setName(s1);
	}

	private void setName(String name) {
		this.name = name;
	}
	protected String getName() {
		return this.name;
	}
}

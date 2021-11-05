package version4;

public enum CalcType_v4
{
	// Types
	BASIC("Basic"),
	SCIENTIFIC("Scientific"),
	PROGRAMMER("Programmer"),
	DATE("Date"),
	// NEW
	CONVERTER("Converter"),

	// Bases for Programmer mode
	BINARY("Binary"),
	OCTAL("Octal"),
	DECIMAL("Decimal"),
	HEXIDECIMAL("Hexidecimal");

	// TODO: Add converter types
	private String name;
	CalcType_v4(String s1) {
		setName(s1);
	}

	private void setName(String name) {
		this.name = name;
	}
	protected String getName() {
		return this.name;
	}
}

package version3;

public enum CalcType_v3 {
	BASIC("Basic"),
	SCIENTIFIC("Scientific Calculator"),
	PROGRAMMER("Programmer Calculator"),
	DATE("Date Calculator"),

	//Bases for Programmer mode
	BINARY("Binary"),
	OCTAL("Octal"),
	DECIMAL("Decimal"),
	HEXIDECIMAL("Hexidecimal"); // binary already exists

	// TODO: Add converter types
	private String name;
	CalcType_v3(String name) {
		setName(name);
	}
	private void setName(String name) {
		this.name = name;
	}
	protected String getName() {
		return this.name;
	}
}

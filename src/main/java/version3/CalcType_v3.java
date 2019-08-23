package version3;

public enum CalcType_v3 {
	BASE("Base"), BASIC("Basic"), BINARY("Binary Calcualtor"), 
	SCIENTIFIC("Scientific Calculator"), PROGRAMMER("Programmer Calculator"), 
	DATE("Date Calculator");
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

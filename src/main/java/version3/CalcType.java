package version3;

public enum CalcType {
	BASE("Base"), STANDARD("Standard"), BINARY("Binary Calcualtor"), 
	SCIENTIFIC("Scientific Calculator"), PROGRAMMER("Programmer Calculator"), 
	DATE("Date Calculator");
	// TODO: Add converter types
	private String name;
	CalcType(String name) {
		setName(name);
	}
	private void setName(String name) {
		this.name = name;
	}
	protected String getName() {
		return this.name;
	}
}

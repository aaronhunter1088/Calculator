package version3;

public enum CalcType {
	Base("Base"), Standard("Standard"), Basic("Basic Calcualtor"), 
	Scientific("Scientific Calculator"), Programmer("Programmer Calculator"), 
	Date("Date Calculator");
	// TODO: Add converter types
	
	protected String name;
	
	CalcType(String name) {
		this.name = name;
	}
	
	protected String getName() {
		return this.name;
	}
}

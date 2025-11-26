package Types;

import Interfaces.CalculatorType;

/**
 * CalculatorView
 * <p>
 * This enum contains the different
 * calculator views for the calculator
 *
 * @author Michael Ball
 * @version 4.0
 */
public enum CalculatorView implements CalculatorType
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
	public String getValue() { return value; }
	public String getName() { return this.name(); }
}

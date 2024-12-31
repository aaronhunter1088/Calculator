package Types;

/**
 * The different types of Calculators available
 * CalculatorType.getValue() returns ENUM("thisValue")
 * CalculatorType.getName() returns the literal ENUM value
 */
public interface CalculatorType {

    String getValue();
    String getName();
}

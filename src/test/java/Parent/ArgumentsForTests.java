package Parent;

import java.util.HashMap;
import java.util.Map;

import static Types.Texts.*;

/**
 * ArgumentsForTests
 * <p>
 * This class defines the arguments for tests in a builder pattern.
 * <p>
 * First Number: The first number to be used in the test. Commas can
 * be provided but will be removed when necessary.
 * First Unary Operator: The first unary operator to be used in the test.
 * This can be any operator but typically is one of the unary operators.
 * First Unary Result: The expected result of the first unary operation.
 * If the values[vP] is different to the textPane display, separate the
 * two by using the ARGUMENT_SEPARATOR constant.
 * First Binary Operator: The first binary operator to be used in the test.
 * This can be any operator but typically is one of the binary operators.
 * First Binary Result: The expected result of the first binary operation.
 * If the values[vP] is different to the textPane display, separate the
 * two by using the ARGUMENT_SEPARATOR constant.
 * Second Number: The second number to be used in the test. Commas can
 * be provided but will be removed when necessary.
 * Second Unary Operator: The second unary operator to be used in the test.
 * This can be any operator but typically is one of the unary operators.
 * Second Unary Result: The expected result of the second unary operation.
 * If the values[vP] is different to the textPane display, separate the
 * two by using the ARGUMENT_SEPARATOR constant.
 * Second Binary Operator: The second binary operator to be used in the test.
 * This can be any operator but typically is one of the binary operators.
 * Second Binary Result: The expected result of the second binary operation.
 * If the values[vP] is different to the textPane display, separate the
 * two by using the ARGUMENT_SEPARATOR constant.
 * Operator Under Test: The operator that is being tested in this test case.
 *
 * @author Michael Ball
 * @version 4.0
 */
public class ArgumentsForTests
{
    public static ArgumentsForTests builder(String operatorUnderTest)
    {
        ArgumentsForTests test = new ArgumentsForTests();
        test.argumentsMap = createArgumentsMap();
        test.argumentsMap.put(OPERATOR_UNDER_TEST, operatorUnderTest);
        return test;
    }

    /**
     * Sets the first number
     * @param firstNumber the first number to set
     * @return this test object
     */
    public ArgumentsForTests firstNumber(String firstNumber)
    {
        this.argumentsMap.put(FIRST_NUMBER, firstNumber);
        return this;
    }

    /**
     * Gets the first number for this test
     * @return the first number
     */
    public String getFirstNumber()
    {
        return this.argumentsMap.get(FIRST_NUMBER);
    }

    public ArgumentsForTests secondNumber(String secondNumber)
    {
        this.argumentsMap.put(SECOND_NUMBER, secondNumber);
        return this;
    }
    public String getSecondNumber()
    {
        return this.argumentsMap.get(SECOND_NUMBER);
    }

    public ArgumentsForTests firstUnaryOperator(String firstUnaryOperator)
    {
        this.argumentsMap.put(FIRST_UNARY_OPERATOR, firstUnaryOperator);
        return this;
    }
    public String getFirstUnaryOperator()
    {
        return this.argumentsMap.get(FIRST_UNARY_OPERATOR);
    }

    public ArgumentsForTests secondUnaryOperator(String secondUnaryOperator)
    {
        this.argumentsMap.put(SECOND_UNARY_OPERATOR, secondUnaryOperator);
        return this;
    }
    public String getSecondUnaryOperator()
    {
        return this.argumentsMap.get(SECOND_UNARY_OPERATOR);
    }

    /**
     * Sets the result of the first unary operation.
     * @param firstUnaryResult the expected result of the first unary operation
     * @return this test object
     */
    public ArgumentsForTests firstUnaryResult(String firstUnaryResult)
    {
        this.argumentsMap.put(FIRST_UNARY_RESULT, firstUnaryResult);
        return this;
    }

    /**
     * Gets the expected result of the first unary operation.
     * If your operation returns different results to the
     * values[valuesPosition] and textPane, you can separate
     * them by using the ARGUMENT_SEPARATOR constant, in that
     * order. They will be checked accordingly.
     * @return the expected result of the first unary operation
     */
    public String getFirstUnaryResult()
    {
        return this.argumentsMap.get(FIRST_UNARY_RESULT);
    }

    public ArgumentsForTests secondUnaryResult(String secondUnaryResult)
    {
        this.argumentsMap.put(SECOND_UNARY_RESULT, secondUnaryResult);
        return this;
    }
    public String getSecondBinaryResult()
    {
        return this.argumentsMap.get(SECOND_BINARY_RESULT);
    }

    /**
     * Sets the result of the first binary operation.
     * @param firstBinaryOperator the expected result of the first binary operation
     * @return this test object
     */
    public ArgumentsForTests firstBinaryOperator(String firstBinaryOperator)
    {
        this.argumentsMap.put(FIRST_BINARY_OPERATOR, firstBinaryOperator);
        return this;
    }
    public String getFirstBinaryOperator()
    {
        return this.argumentsMap.get(FIRST_BINARY_OPERATOR);
    }

    public ArgumentsForTests secondBinaryOperator(String secondBinaryOperator)
    {
        this.argumentsMap.put(SECOND_BINARY_OPERATOR, secondBinaryOperator);
        return this;
    }
    public String getSecondBinaryOperator()
    {
        return this.argumentsMap.get(SECOND_BINARY_OPERATOR);
    }

    public ArgumentsForTests firstBinaryResult(String firstBinaryResult)
    {
        this.argumentsMap.put(FIRST_BINARY_RESULT, firstBinaryResult);
        return this;
    }

    /**
     * Gets the expected result of the first binary operation.
     * If your operation returns different results to the
     * values[valuesPosition] and textPane, you can separate
     * them by using the ARGUMENT_SEPARATOR constant, in that
     * order. They will be checked accordingly.
     * @return the expected result of the first binary operation
     */
    public String getFirstBinaryResult()
    {
        return this.argumentsMap.get(FIRST_BINARY_RESULT);
    }

    public ArgumentsForTests secondBinaryResult(String secondBinaryResult)
    {
        this.argumentsMap.put(SECOND_BINARY_RESULT, secondBinaryResult);
        return this;
    }

    /**
     * Gets the expected result of the second unary operation.
     * If your operation returns different results to the
     * values[valuesPosition] and textPane, you can separate
     * them by using the ARGUMENT_SEPARATOR constant, in that
     * order. They will be checked accordingly.
     * @return the expected result of the second unary operation
     */
    public String getSecondUnaryResult()
    {
        return this.argumentsMap.get(SECOND_UNARY_RESULT);
    }

    public ArgumentsForTests operatorUnderTest(String operatorUnderTest)
    {
        this.argumentsMap.put(OPERATOR_UNDER_TEST, operatorUnderTest);
        return this;
    }
    public String getOperatorUnderTest()
    {
        return this.argumentsMap.get(OPERATOR_UNDER_TEST);
    }

    public ArgumentsForTests build()
    {
        return this;
    }

    public Map<String, String> argumentsMap;
    public static final String FIRST_NUMBER = "firstNumber";
    public static final String SECOND_NUMBER = "secondNumber";
    public static final String FIRST_UNARY_OPERATOR = "firstUnaryOperator";
    public static final String SECOND_UNARY_OPERATOR = "secondUnaryOperator";
    public static final String FIRST_BINARY_OPERATOR = "firstBinaryOperator";
    public static final String SECOND_BINARY_OPERATOR = "secondBinaryOperator";
    public static final String FIRST_UNARY_RESULT = "firstUnaryResult";
    public static final String SECOND_UNARY_RESULT = "secondUnaryResult";
    public static final String FIRST_BINARY_RESULT = "firstBinaryResult";
    public static final String SECOND_BINARY_RESULT = "secondBinaryResult";
    public static final String OPERATOR_UNDER_TEST = "operatorUnderTest";

    private static Map<String, String> createArgumentsMap()
    {
        Map<String, String> args = new HashMap<>();
        args.put(FIRST_NUMBER, EMPTY);
        args.put(SECOND_NUMBER, EMPTY);
        args.put(FIRST_UNARY_OPERATOR, EMPTY);
        args.put(SECOND_UNARY_OPERATOR, EMPTY);
        args.put(FIRST_BINARY_OPERATOR, EMPTY);
        args.put(SECOND_BINARY_OPERATOR, EMPTY);
        args.put(FIRST_UNARY_RESULT, EMPTY);
        args.put(SECOND_UNARY_RESULT, EMPTY);
        args.put(FIRST_BINARY_RESULT, EMPTY);
        args.put(SECOND_BINARY_RESULT, EMPTY);
        args.put(OPERATOR_UNDER_TEST, EMPTY);
        return args;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{");
        //sb.append("OperatorUnderTest='").append(argumentsMap.get(OPERATOR_UNDER_TEST)).append('\'').append(COMMA).append(SPACE);
        if (!argumentsMap.get(FIRST_NUMBER).isEmpty())
            sb.append("firstNumber='").append(argumentsMap.get(FIRST_NUMBER)).append('\'').append(COMMA).append(SPACE);
        if (!argumentsMap.get(FIRST_UNARY_OPERATOR).isEmpty())
            sb.append("firstUOp='").append(argumentsMap.get(FIRST_UNARY_OPERATOR)).append('\'').append(COMMA).append(SPACE);
        if (!argumentsMap.get(FIRST_UNARY_RESULT).isEmpty())
            sb.append("firstUnaryResult='").append(argumentsMap.get(FIRST_UNARY_RESULT)).append('\'').append(COMMA).append(SPACE);
        if (!argumentsMap.get(FIRST_BINARY_OPERATOR).isEmpty())
            sb.append("firstBOp='").append(argumentsMap.get(FIRST_BINARY_OPERATOR)).append('\'').append(COMMA).append(SPACE);
        if (!argumentsMap.get(FIRST_BINARY_RESULT).isEmpty())
            sb.append("firstBinaryResult='").append(argumentsMap.get(FIRST_BINARY_RESULT)).append('\'').append(COMMA).append(SPACE);
        if (!argumentsMap.get(SECOND_NUMBER).isEmpty())
            sb.append("secondNumber='").append(argumentsMap.get(SECOND_NUMBER)).append('\'').append(COMMA).append(SPACE);
        if (!argumentsMap.get(SECOND_UNARY_OPERATOR).isEmpty())
            sb.append("secondUOp='").append(argumentsMap.get(SECOND_UNARY_OPERATOR)).append('\'').append(COMMA).append(SPACE);
        if (!argumentsMap.get(SECOND_UNARY_RESULT).isEmpty())
            sb.append("secondUnaryResult='").append(argumentsMap.get(SECOND_UNARY_RESULT)).append('\'').append(COMMA).append(SPACE);
        if (!argumentsMap.get(SECOND_BINARY_OPERATOR).isEmpty())
            sb.append("secondBOp='").append(argumentsMap.get(SECOND_BINARY_OPERATOR)).append('\'').append(COMMA).append(SPACE);
        if (!argumentsMap.get(SECOND_BINARY_RESULT).isEmpty())
            sb.append("secondBinaryResult='").append(argumentsMap.get(SECOND_BINARY_RESULT)).append('\'').append(COMMA).append(SPACE);
        sb.append('}');
        return sb.toString();
    }
}

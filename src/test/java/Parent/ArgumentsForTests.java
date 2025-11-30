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
    private String firstNumber = EMPTY;
    private String secondNumber = EMPTY;
    private String firstUnaryOperator = EMPTY;
    private String secondUnaryOperator = EMPTY;
    private String firstBinaryOperator = EMPTY;
    private String secondBinaryOperator = EMPTY;
    private String firstUnaryResult = EMPTY;
    private String secondUnaryResult = EMPTY;
    private String firstBinaryResult = EMPTY;
    private String secondBinaryResult = EMPTY;
    private String operatorUnderTest = EMPTY;
    private ArgumentsForTests initialState = null;

    /**
     * Begins building a test argument object
     * @param operatorUnderTest the operator under test
     * @return the test argument object
     */
    public static ArgumentsForTests builder(String operatorUnderTest)
    {
        ArgumentsForTests test = new ArgumentsForTests();
        test.operatorUnderTest(operatorUnderTest);
        return test;
    }

    /**
     * Sets the first number
     * @param firstNumber the first number to set
     * @return this test object
     */
    public ArgumentsForTests firstNumber(String firstNumber)
    {
        this.firstNumber = firstNumber;
        return this;
    }

    /**
     * Gets the first number for this test
     * @return the first number
     */
    public String getFirstNumber() { return firstNumber; }

    /**
     * Sets the second number
     * @param secondNumber the second number to set
     * @return this test object
     */
    public ArgumentsForTests secondNumber(String secondNumber)
    {
        this.secondNumber = secondNumber;
        return this;
    }

    /**
     * Gets the second number for this test
     * @return the second number
     */
    public String getSecondNumber() { return secondNumber; }

    /**
     * Sets the first unary operator
     * @param firstUnaryOperator the first unary operator to set
     * @return this test object
     */
    public ArgumentsForTests firstUnaryOperator(String firstUnaryOperator)
    {
        this.firstUnaryOperator = firstUnaryOperator;
        return this;
    }

    /**
     * Gets the first unary operator for this test
     * @return the first unary operator
     */
    public String getFirstUnaryOperator() { return firstUnaryOperator; }

    /**
     * Sets the second unary operator
     * @param secondUnaryOperator the second unary operator to set
     * @return this test object
     */
    public ArgumentsForTests secondUnaryOperator(String secondUnaryOperator)
    {
        this.secondUnaryOperator = secondUnaryOperator;
        return this;
    }
    public String getSecondUnaryOperator() { return secondUnaryOperator; }

    /**
     * Sets the result of the first unary operation.
     * @param firstUnaryResult the expected result of the first unary operation
     * @return this test object
     */
    public ArgumentsForTests firstUnaryResult(String firstUnaryResult)
    {
        this.firstUnaryResult = firstUnaryResult;
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
    public String getFirstUnaryResult() { return firstUnaryResult; }

    /**
     * Sets the result of the second unary operation.
     * @param secondUnaryResult the expected result of the second unary operation
     * @return this test object
     */
    public ArgumentsForTests secondUnaryResult(String secondUnaryResult)
    {
        this.secondUnaryResult = secondUnaryResult;
        return this;
    }

    /**
     * Gets the expected result of the second binary operation.
     * @return the expected result of the second binary operation
     */
    public String getSecondBinaryResult() { return secondBinaryResult; }

    /**
     * Sets the result of the first binary operation.
     * @param firstBinaryOperator the expected result of the first binary operation
     * @return this test object
     */
    public ArgumentsForTests firstBinaryOperator(String firstBinaryOperator)
    {
        this.firstBinaryOperator = firstBinaryOperator;
        return this;
    }

    /**
     * Gets the first binary operator for this test
     * @return the first binary operator
     */
    public String getFirstBinaryOperator() { return firstBinaryOperator; }

    /**
     * Sets the second binary operator
     * @param secondBinaryOperator the second binary operator to set
     * @return this test object
     */
    public ArgumentsForTests secondBinaryOperator(String secondBinaryOperator)
    {
        this.secondBinaryOperator = secondBinaryOperator;
        return this;
    }

    /**
     * Gets the second binary operator for this test
     * @return the second binary operator
     */
    public String getSecondBinaryOperator() { return secondBinaryOperator; }

    /**
     * Sets the result of the first binary operation.
     * @param firstBinaryResult the expected result of the first binary operation
     * @return this test object
     */
    public ArgumentsForTests firstBinaryResult(String firstBinaryResult)
    {
        this.firstBinaryResult = firstBinaryResult;
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
    public String getFirstBinaryResult() { return firstBinaryResult; }

    /**
     * Sets the result of the second binary operation.
     * @param secondBinaryResult the expected result of the second binary operation
     * @return this test object
     */
    public ArgumentsForTests secondBinaryResult(String secondBinaryResult)
    {
        this.secondBinaryResult = secondBinaryResult;
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
    public String getSecondUnaryResult() { return secondUnaryResult; }

    /**
     * Sets the operator under test
     * @param operatorUnderTest the operator under test
     * @return this test object
     */
    public ArgumentsForTests operatorUnderTest(String operatorUnderTest)
    {
        this.operatorUnderTest = operatorUnderTest;
        return this;
    }

    /**
     * Gets the operator under test
     * @return the operator under test
     */
    public String getOperatorUnderTest() { return operatorUnderTest; }

    /**
     * Sets the values to use in the
     * initial state for this test.
     * @param initialState the initial state
     * @return this test object
     */
    public ArgumentsForTests initialState(ArgumentsForTests initialState)
    {
        this.initialState = initialState;
        return this;
    }

    /**
     * Gets the initial state for this test
     * @return the initial state
     */
    public ArgumentsForTests getInitialState()
    {
        return initialState;
    }

    /**
     * Mainly used to signal the end of
     * passing in arguments for the test
     * @return this test object
     */
    public ArgumentsForTests build()
    {
        return this;
    }

    //public Map<String, String> argumentsMap;
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
    public static final String INITIAL_STATE = "initialState";

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
        args.put(INITIAL_STATE, EMPTY);
        args.put(OPERATOR_UNDER_TEST, EMPTY);
        return args;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{");
        //sb.append("OperatorUnderTest='").append(argumentsMap.get(OPERATOR_UNDER_TEST)).append('\'').append(COMMA).append(SPACE);
        if (!firstNumber.isEmpty())
            sb.append("firstNumber='").append(firstNumber).append('\'').append(COMMA).append(SPACE);
        if (!firstUnaryOperator.isEmpty())
            sb.append("firstUOp='").append(firstUnaryOperator).append('\'').append(COMMA).append(SPACE);
        if (!firstUnaryResult.isEmpty())
            sb.append("firstUnaryResult='").append(firstUnaryResult).append('\'').append(COMMA).append(SPACE);
        if (!firstBinaryOperator.isEmpty())
            sb.append("firstBOp='").append(firstBinaryOperator).append('\'').append(COMMA).append(SPACE);
        if (!firstBinaryResult.isEmpty())
            sb.append("firstBinaryResult='").append(firstBinaryResult).append('\'').append(COMMA).append(SPACE);
        if (!secondNumber.isEmpty())
            sb.append("secondNumber='").append(secondNumber).append('\'').append(COMMA).append(SPACE);
        if (!secondUnaryOperator.isEmpty())
            sb.append("secondUOp='").append(secondUnaryOperator).append('\'').append(COMMA).append(SPACE);
        if (!secondUnaryResult.isEmpty())
            sb.append("secondUnaryResult='").append(secondUnaryResult).append('\'').append(COMMA).append(SPACE);
        if (!secondBinaryOperator.isEmpty())
            sb.append("secondBOp='").append(secondBinaryOperator).append('\'').append(COMMA).append(SPACE);
        if (!secondBinaryResult.isEmpty())
            sb.append("secondBinaryResult='").append(secondBinaryResult).append('\'').append(COMMA).append(SPACE);
        sb.append('}');
        return sb.toString();
    }
}

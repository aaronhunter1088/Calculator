package Parent;

import Calculators.Calculator;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static Parent.ArgumentsForTests.*;
import static Parent.ArgumentsForTests.SECOND_BINARY_OPERATOR;
import static Parent.ArgumentsForTests.SECOND_NUMBER;
import static Parent.ArgumentsForTests.SECOND_UNARY_OPERATOR;
import static Types.CalculatorUtility.addThousandsDelimiter;
import static Types.CalculatorUtility.removeThousandsDelimiter;
import static Types.Texts.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class TestParent
{
    public Calculator calculator;

    @Mock
    public ActionEvent actionEvent;

    public static AutoCloseable mocks;

    public void postConstructCalculator()
    {
        try
        {
            SwingUtilities.invokeAndWait(() -> {
                calculator.pack();
                calculator.setVisible(true);
            });
        }
        catch (Exception ignored) {}
    }

    /**
     * Sets up the when-then for an invalid operator test.
     * Only supports a number and an optional unary operator.
     * Used in: InvalidMemoryStoreTests
     * @param actionEvent the action event
     * @param operatorUnderTest the operator under test
     */
    public void setupWhenThenFirstOperatorThenOperatorUnderTest(ActionEvent actionEvent, String operatorUnderTest, String firstNumber, String firstOperator)
    {
        AtomicInteger idx = new AtomicInteger(0);
        int firstNumberLength = firstNumber.length();
        if (firstNumberLength == 0) // no first number BUT could have first operator
        {
            if (firstOperator.isEmpty()) // no first operator
            {
                when(actionEvent.getActionCommand())
                        .thenReturn(operatorUnderTest);
            }
            else // has first operator
            {
                when(actionEvent.getActionCommand()).thenAnswer(invocation -> {
                    int i = idx.getAndIncrement();
                    if (i == 0) return firstOperator;
                    else return operatorUnderTest;
                });
            }
        }
        else // first number BUT could have first operator
        {
            if (firstOperator.isEmpty()) // only have first number
            {
                when(actionEvent.getActionCommand()).thenAnswer(invocation -> {
                    int i = idx.getAndIncrement();
                    if (i < firstNumberLength) return String.valueOf(firstNumber.charAt(i));
                    else return operatorUnderTest;
                });
            }
            else // has first number and have first operator
            {
                when(actionEvent.getActionCommand()).thenAnswer(invocation -> {
                    int i = idx.getAndIncrement();
                    if (i < firstNumberLength) return String.valueOf(firstNumber.charAt(i));
                    else if (i == firstNumberLength) return firstOperator;
                    else return operatorUnderTest;
                });
            }
        }
    }

    /**
     * Sets up the when-then for the provided operator test.
     * Supports up to one number and one operator before the operator under test.
     * @param actionEvent the action event
     * @param operatorInTest the operator under test
     */
    public void setupWhenThen(ActionEvent actionEvent, String operatorInTest, String firstNumber, String firstOperator)
    { setupWhenThen(actionEvent, operatorInTest, firstNumber, firstOperator, EMPTY, EMPTY); }

    /**
     * Sets up the when-then for the provided operator test.
     * Supports up to two numbers and two operators before the operator under test.
     * @param actionEvent the action event
     * @param operatorInTest the operator under test
     */
    public void setupWhenThen(ActionEvent actionEvent, String operatorInTest,
                              String firstNumber, String firstOperator, String secondNumber, String secondOperator)
    {
        AtomicInteger idx = new AtomicInteger(0);
        int firstNumberLength = firstNumber.length();
        int secondNumberLength = secondNumber.length();
        int totalLength = firstNumberLength + firstOperator.length() + secondNumberLength + secondOperator.length();

        if (secondNumberLength == 0) // no second number OR second operator
        {
            if (firstNumberLength == 0) // no first number BUT could have first operator
            {
                if (firstOperator.isEmpty()) // no first operator
                {
                    when(actionEvent.getActionCommand())
                            .thenReturn(operatorInTest);
                }
                else // has first operator
                {
                    when(actionEvent.getActionCommand()).thenAnswer(invocation -> {
                        int i = idx.getAndIncrement();
                        if (i == 0) return firstOperator;
                        else return operatorInTest;
                    });
                }
            }
            else // first number BUT could have first operator
            {
                if (firstOperator.isEmpty()) // only have first number
                {
                    when(actionEvent.getActionCommand()).thenAnswer(invocation -> {
                        int i = idx.getAndIncrement();
                        if (i < firstNumberLength) return String.valueOf(firstNumber.charAt(i));
                        else return operatorInTest;
                    });
                }
                else // has first number and have first operator
                {
                    AtomicBoolean isFirstNumber = new AtomicBoolean(true);
                    when(actionEvent.getActionCommand()).thenAnswer(invocation -> {
                        int i = idx.getAndIncrement();
                        if (i < firstNumberLength) return String.valueOf(firstNumber.charAt(i));
                        else if (i == firstNumberLength) return operatorInTest;
                        else if (i == (firstNumberLength+1) && isFirstNumber.get())
                        {
                            idx.set(firstNumberLength+1);
                            isFirstNumber.set(false);
                            return firstOperator;
                        }
                        else return operatorInTest;
                    });
                }
            }
        }
        else
        {
            AtomicBoolean isFirstNumber = new AtomicBoolean(true);
            AtomicInteger secondNumIdx = new AtomicInteger(0);
            if (secondOperator.isEmpty()) // has second number but no second operator
            {
                when(actionEvent.getActionCommand()).thenAnswer(invocation -> {
                    int i = idx.getAndIncrement();
                    if (i < firstNumberLength) return String.valueOf(firstNumber.charAt(i));
                    else if (i == firstNumberLength) return operatorInTest;
                    else if (i == (firstNumberLength+1) && isFirstNumber.get()) {
                        idx.set(firstNumberLength+1);
                        isFirstNumber.set(false);
                        return firstOperator;
                    }
                    else if (i < (firstNumberLength + firstOperator.length() + secondNumberLength))
                        return String.valueOf(secondNumber.charAt(secondNumIdx.getAndIncrement()));
                    else return operatorInTest;
                });
            }
            else // has second number and second operator before operator in test
            {
                when(actionEvent.getActionCommand()).thenAnswer(invocation -> {
                    int i = idx.getAndIncrement();
                    if (i < firstNumberLength) return String.valueOf(firstNumber.charAt(i));
                    else if (i == firstNumberLength) return operatorInTest;
                    else if (i == (firstNumberLength+1) && isFirstNumber.get()) {
                        idx.set(firstNumberLength+1);
                        isFirstNumber.set(false);
                        return firstOperator;
                    }
                    else if (i < (firstNumberLength + firstOperator.length() + secondNumberLength))
                        return String.valueOf(secondNumber.charAt(secondNumIdx.getAndIncrement()));
                    else if (i == (firstNumberLength + firstOperator.length() + secondNumberLength))
                        return operatorInTest;
                    else return secondOperator;
                });
            }
        }

//        when(actionEvent.getActionCommand())
//                .thenReturn(firstNumber)
//                .thenReturn(operatorInTest)
//                //
//                .thenReturn(firstOperator)
//                .thenReturn(secondNumber)
//                .thenReturn(operatorInTest)
//                //
//                .thenReturn(secondOperator);
    }

    /**
     * Sets up the when-then for the provided arguments.
     * @param actionEvent the action event to mock
     * @param arguments the arguments for the mock to use
     */
    public void setupWhenThen(ActionEvent actionEvent, ArgumentsForTests arguments)
    {
        List<String> characters = setupCharacters(arguments);
        AtomicInteger idx = new AtomicInteger(0);
        when(actionEvent.getActionCommand()).thenAnswer(invocation -> {
            int i = idx.getAndIncrement();
            return characters.get(i);
        });
    }

    private List<String> setupCharacters(ArgumentsForTests arguments)
    {
        List<String> characters = new ArrayList<>();
        // First Number, can be empty
        String firstNumber = removeThousandsDelimiter(arguments.getFirstNumber(), calculator.getThousandsDelimiter());
        int firstNumberLength = firstNumber.length();
        if (firstNumberLength == 0) characters.add(EMPTY);
        else if (calculator.getBadText(firstNumber).equals(firstNumber)) {
            characters.add(EMPTY);
        }
        else characters.addAll(firstNumber.chars().mapToObj(c -> String.valueOf((char) c)).toList());

        // First Unary Operator, can be empty
        String firstUnaryOperator = arguments.getFirstUnaryOperator();
        if (firstUnaryOperator.isEmpty()) characters.add(EMPTY);
        else characters.add(firstUnaryOperator);

        // First Binary Operator, can be empty
        String firstBinaryOperator = arguments.getFirstBinaryOperator();
        if (firstBinaryOperator.isEmpty()) characters.add(EMPTY);
        else characters.add(firstBinaryOperator);

        // Second Number, can be empty
        String secondNumber = removeThousandsDelimiter(arguments.getSecondNumber(), calculator.getThousandsDelimiter());
        int secondNumberLength = secondNumber.length();
        if (secondNumberLength == 0) characters.add(EMPTY);
        else if (calculator.getBadText(secondNumber).equals(secondNumber)) {
            characters.add(EMPTY);
        }
        else characters.addAll(secondNumber.chars().mapToObj(c -> String.valueOf((char) c)).toList());

        // Second Unary Operator, can be empty
        String secondUnaryOperator = arguments.getSecondUnaryOperator();
        if (secondUnaryOperator.isEmpty()) characters.add(EMPTY);
        else characters.add(secondUnaryOperator);

        // Second Binary Operator, can be empty
        String secondBinaryOperator = arguments.getSecondBinaryOperator();
        if (secondBinaryOperator.isEmpty()) characters.add(EMPTY);
        else characters.add(secondBinaryOperator);

        return characters;
    }

    /**
     * Performs the number button action for each
     * character in the given number string.
     * Works with any positive, negative, whole,
     * or fractional numbers. It then asserts that
     * the textPane and values[valuesPosition] are
     * as expected after each character input.
     * @param actionEvent the actionEvent
     * @param number the number
     */
    public void performNumberButtonActionForEachCharacter(ActionEvent actionEvent, String number)
    {
        int firstNumLength = number.length();
        if (firstNumLength == 0)
        {
            assertEquals(number, calculator.getTextPaneValue(), "textPane value is not as expected");
        }
        else
        {
            for (int i=0; i<number.length(); i++)
            {
                String currentDigit = String.valueOf(number.charAt(i));
                if (currentDigit.equals(SUBTRACTION))
                    calculator.performSubtractButtonAction(actionEvent);
                else if (currentDigit.equals(DECIMAL))
                    calculator.performDecimalButtonAction(actionEvent);
                else
                    calculator.performNumberButtonAction(actionEvent);
                if ((i+1) == firstNumLength) {
                    assertEquals(addThousandsDelimiter(number, calculator.getThousandsDelimiter()), calculator.getTextPaneValue(), "textPane value is not as expected");
                    assertEquals(number, calculator.getValues()[calculator.getValuesPosition()], "value[valuesPosition] is not as expected");
                }
                else {
                    assertEquals(addThousandsDelimiter(number.substring(0,(i+1)), calculator.getThousandsDelimiter()), calculator.getTextPaneValue(), "textPane value is not as expected");
                    if (calculator.isNegativeNumber() && !calculator.getValueAt().contains(SUBTRACTION))
                    {
                        assertEquals(number.substring(0,(i+1)), calculator.getValueAt()+SUBTRACTION, "value[valuesPosition] is not as expected");
                    }
                    else
                    {
                        assertEquals(number.substring(0,(i+1)), calculator.getValueAt(), "value[valuesPosition] is not as expected");
                    }
                }
            }
        }
    }

    /**
     * Performs the next operator action based on the provided operator string.
     * @param nextOperator the operator to perform
     */
    public void performNextOperatorAction(Calculator calculator, ActionEvent actionEvent, Logger logger,
                                           String nextOperator)
    {
        switch (nextOperator)
        {
            case ZERO,ONE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE -> calculator.performNumberButtonAction(actionEvent);
            case ADDITION -> calculator.performAddButtonAction(actionEvent);
            case AND -> calculator.getProgrammerPanel().performAndButtonAction(actionEvent);
            case CLEAR -> calculator.performClearButtonAction(actionEvent);
            case CLEAR_ENTRY -> calculator.performClearEntryButtonAction(actionEvent);
            case DECIMAL -> calculator.performDecimalButtonAction(actionEvent);
            case DELETE -> calculator.performDeleteButtonAction(actionEvent);
            case DIVISION -> calculator.performDivideButtonAction(actionEvent);
            case EQUALS -> calculator.performEqualsButtonAction(actionEvent);
            case FRACTION -> calculator.getBasicPanel().performFractionButtonAction(actionEvent);
            case HISTORY_OPEN, HISTORY_CLOSED -> calculator.performHistoryAction(actionEvent);
            case LEFT_PARENTHESIS -> calculator.getProgrammerPanel().performLeftParenthesisButtonAction(actionEvent);
            case LSH -> calculator.getProgrammerPanel().performLeftShiftButtonAction(actionEvent);
            case MEMORY_ADD -> calculator.performMemoryAdditionAction(actionEvent);
            case MEMORY_CLEAR -> calculator.performMemoryClearAction(actionEvent);
            case MEMORY_RECALL -> calculator.performMemoryRecallAction(actionEvent);
            case MEMORY_STORE -> calculator.performMemoryStoreAction(actionEvent);
            case MEMORY_SUBTRACT -> calculator.performMemorySubtractionAction(actionEvent);
            case MODULUS -> calculator.getProgrammerPanel().performModulusButtonAction(actionEvent);
            case MULTIPLICATION -> calculator.performMultiplyButtonAction(actionEvent);
            case NEGATE -> calculator.performNegateButtonAction(actionEvent);
            case NOT -> calculator.getProgrammerPanel().performNotButtonAction(actionEvent);
            case OR -> calculator.getProgrammerPanel().performOrButtonAction(actionEvent);
            case PERCENT -> calculator.getBasicPanel().performPercentButtonAction(actionEvent);
            case RIGHT_PARENTHESIS -> calculator.getProgrammerPanel().performRightParenthesisButtonAction(actionEvent);
            case RSH -> calculator.getProgrammerPanel().performRightShiftButtonAction(actionEvent);
            case SQUARED -> calculator.getBasicPanel().performSquaredButtonAction(actionEvent);
            case SQUARE_ROOT -> calculator.performSquareRootButtonAction(actionEvent);
            case SUBTRACTION -> calculator.performSubtractButtonAction(actionEvent);
            case XOR -> calculator.getProgrammerPanel().performXorButtonAction(actionEvent);
            default -> logger.warn("Unknown operator selected: '{}'. Add it here.", nextOperator);
        }
    }

    public void exhaustActionEvent(String value, ActionEvent actionEvent, Logger logger)
    {
        String exhausted = actionEvent.getActionCommand();
        logger.info("Exhausted ActionEvent with button choice: '{}:{}'", exhausted, value);
    }

    /**
     * Sets up the values from the arguments and performs the test.
     * If the firstNumber is not empty, it performs the number button actions
     * for each character in the firstNumber. Commas are removed beforehand.
     * If the firstNumber is badText, it exhausts the actionEvent for the
     * first number. This exhaustion is required to keep the actionEvent
     * in sync.<br>
     * If the firstUnaryOperator is not empty, it performs that operator
     * action, asserts the textPane and values[vP] and updates the
     * previousHistory. Otherwise the operator is exhausted.<br>
     * If the firstBinaryOperator is not empty, it performs that operator
     * action, asserts the textPane and values[vP] and updates the
     * previousHistory. Otherwise the operator is exhausted.<br>
     * If the secondNumber is not empty, it performs the number button actions
     * for each character in the secondNumber. Commas are removed beforehand.
     * If the secondNumber is badText, it is exhausted.<br>
     * If the secondUnaryOperator is not empty, it performs that operator
     * action, asserts the textPane and values[vP] and updates the
     * previousHistory. Otherwise the operator is exhausted.<br>
     * If the secondBinaryOperator is not empty, it performs that operator
     * action, asserts the textPane and values[vP] and updates the
     * previousHistory. Otherwise the operator is exhausted.
     * @param arguments for the test
     * @param previousHistory the previous history before the button action
     */
    public String performTest(ArgumentsForTests arguments, String previousHistory, Logger logger)
    {
        // Extract arguments, place in variables
        String firstNumber = removeThousandsDelimiter(arguments.getFirstNumber(), calculator.getThousandsDelimiter());
        String firstUnaryOperator = arguments.getFirstUnaryOperator();
        String firstUnaryTextPaneResult = arguments.getFirstUnaryResult();
        String firstUnaryValuesResult = removeThousandsDelimiter(firstUnaryTextPaneResult, calculator.getThousandsDelimiter());
        if (firstUnaryTextPaneResult.split(ARGUMENT_REGEX).length > 1) // "0|INFINITY or 35|35 +"
        {
            firstUnaryValuesResult = removeThousandsDelimiter(firstUnaryTextPaneResult.split(ARGUMENT_REGEX)[0], calculator.getThousandsDelimiter());
            firstUnaryTextPaneResult = firstUnaryTextPaneResult.split(ARGUMENT_REGEX)[1];
        }
        String firstBinaryOperator = arguments.getFirstBinaryOperator();
        String firstBinaryTextPaneResult = arguments.getFirstBinaryResult();
        String firstBinaryValuesResult = removeThousandsDelimiter(firstBinaryTextPaneResult, calculator.getThousandsDelimiter());
        if (firstBinaryTextPaneResult.split(ARGUMENT_REGEX).length > 1)
        {
            firstBinaryValuesResult = removeThousandsDelimiter(firstBinaryTextPaneResult.split(ARGUMENT_REGEX)[0], calculator.getThousandsDelimiter());
            firstBinaryTextPaneResult = firstBinaryTextPaneResult.split(ARGUMENT_REGEX)[1];
        }
        String secondNumber = removeThousandsDelimiter(arguments.getSecondNumber(), calculator.getThousandsDelimiter());
        String secondUnaryOperator = arguments.getSecondUnaryOperator();
        String secondUnaryTextPaneResult = arguments.getSecondUnaryResult();
        String secondUnaryValuesResult = removeThousandsDelimiter(secondUnaryTextPaneResult, calculator.getThousandsDelimiter());
        if (secondUnaryTextPaneResult.split(ARGUMENT_REGEX).length > 1)
        {
            secondUnaryValuesResult = removeThousandsDelimiter(secondUnaryTextPaneResult.split(ARGUMENT_REGEX)[0], calculator.getThousandsDelimiter());
            secondUnaryTextPaneResult = secondUnaryTextPaneResult.split(ARGUMENT_REGEX)[1];
        }
        String secondBinaryOperator = arguments.getSecondBinaryOperator();
        String secondBinaryTextPaneResult = arguments.getSecondBinaryResult();
        String secondBinaryValuesResult = removeThousandsDelimiter(secondBinaryTextPaneResult, calculator.getThousandsDelimiter());
        if (secondBinaryTextPaneResult.split(ARGUMENT_REGEX).length > 1)
        {
            secondBinaryValuesResult = removeThousandsDelimiter(secondBinaryTextPaneResult.split(ARGUMENT_REGEX)[0], calculator.getThousandsDelimiter());
            secondBinaryTextPaneResult = secondBinaryTextPaneResult.split(ARGUMENT_REGEX)[1];
        }

        // Start test
        if (!firstNumber.isEmpty() && !calculator.getBadText(firstNumber).equals(firstNumber))
        {
            performNumberButtonActionForEachCharacter(actionEvent, firstNumber);
            previousHistory = calculator.getHistoryTextPane().getText();
        }
        else if (!firstNumber.isEmpty() && calculator.getBadText(firstNumber).equals(firstNumber))
        {
            calculator.appendTextToPane(firstNumber);
            exhaustActionEvent(FIRST_NUMBER, actionEvent, logger);
        }
        else exhaustActionEvent(FIRST_NUMBER, actionEvent, logger);

        if (!firstUnaryOperator.isEmpty())
        {
            performNextOperatorAction(calculator, actionEvent, logger, firstUnaryOperator);
            if (List.of(MEMORY_STORE, MEMORY_ADD, MEMORY_SUBTRACT).contains(firstUnaryOperator) && !calculator.textPaneContainsBadText())
            { assertEquals(firstUnaryValuesResult, calculator.getMemoryValues()[calculator.getMemoryPosition()-1], "Memories[mP] is not as expected"); }
            else if (List.of(ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, EQUALS).contains(firstUnaryOperator))
            { assertEquals(firstUnaryValuesResult, calculator.getValues()[0], "Values[0] is not as expected"); }
            else
            { assertEquals(firstUnaryValuesResult, calculator.getValues()[0], "Values[0] is not as expected"); }
            assertEquals(firstUnaryTextPaneResult, calculator.getTextPaneValue(), "textPane is not as expected");
            previousHistory = calculator.getHistoryTextPane().getText();
        }
        else exhaustActionEvent(FIRST_UNARY_OPERATOR, actionEvent, logger);

        if (!firstBinaryOperator.isEmpty())
        {
            performNextOperatorAction(calculator, actionEvent, logger, firstBinaryOperator);
            if (List.of(MEMORY_STORE, MEMORY_ADD, MEMORY_SUBTRACT).contains(firstBinaryOperator))
            { assertEquals(firstBinaryValuesResult, calculator.getMemoryValues()[calculator.getMemoryPosition()-1], "Memories[mP] is not as expected"); }
            else
            { assertEquals(firstBinaryValuesResult, calculator.getValues()[0], "Values[0] is not as expected"); }
            assertEquals(firstBinaryTextPaneResult, calculator.getTextPaneValue(), "textPane is not as expected");
            previousHistory = calculator.getHistoryTextPane().getText();
        }
        else exhaustActionEvent(FIRST_BINARY_OPERATOR, actionEvent, logger);

        if (!secondNumber.isEmpty() && !calculator.getBadText(secondNumber).equals(secondNumber))
        {
            performNumberButtonActionForEachCharacter(actionEvent, secondNumber);
            previousHistory = calculator.getHistoryTextPane().getText();
        }
        else if (!secondNumber.isEmpty() && calculator.getBadText(secondNumber).equals(secondNumber))
        {
            calculator.appendTextToPane(secondNumber);
            exhaustActionEvent(SECOND_NUMBER, actionEvent, logger);
        }
        else exhaustActionEvent(SECOND_NUMBER, actionEvent, logger);

        if (!secondUnaryOperator.isEmpty())
        {
            performNextOperatorAction(calculator, actionEvent, logger, secondUnaryOperator);
            if (List.of(MEMORY_STORE, MEMORY_ADD, MEMORY_SUBTRACT).contains(secondUnaryOperator) && !calculator.textPaneContainsBadText())
            { assertEquals(secondUnaryValuesResult, calculator.getMemoryValues()[calculator.getMemoryPosition()-1], "Memories[mP] is not as expected"); }
            else if (List.of(ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, EQUALS).contains(secondUnaryOperator))
            { assertEquals(secondUnaryValuesResult, calculator.getValues()[0], "Values[0] is not as expected"); }
            else
            { assertEquals(secondUnaryValuesResult, calculator.getValues()[calculator.getValuesPosition()], "Values[vP] is not as expected"); }
            assertEquals(secondUnaryTextPaneResult, calculator.getTextPaneValue(), "textPane is not as expected");
            previousHistory = calculator.getHistoryTextPane().getText();
        }
        else exhaustActionEvent(SECOND_UNARY_OPERATOR, actionEvent, logger);

        if (!secondBinaryOperator.isEmpty())
        {
            performNextOperatorAction(calculator, actionEvent, logger, secondBinaryOperator);
            if (List.of(MEMORY_STORE, MEMORY_ADD, MEMORY_SUBTRACT).contains(secondBinaryOperator))
            { assertEquals(secondBinaryValuesResult, calculator.getMemoryValues()[calculator.getMemoryPosition()-1], "Memories[mP] is not as expected"); }
            else
            { assertEquals(secondBinaryValuesResult, calculator.getValues()[1], "Values[1] is not as expected"); }
            assertEquals(secondBinaryTextPaneResult, calculator.getTextPaneValue(), "textPane is not as expected");
            previousHistory = calculator.getHistoryTextPane().getText();
        }
        else exhaustActionEvent(SECOND_BINARY_OPERATOR, actionEvent, logger);

        return previousHistory;
    }

    /**
     * Tests that the history is as expected after
     * pushing a button.
     * Method writeHistory calls writeHistoryWithMessage.
     *
     * @param arguments for the test
     * @param calculatorHistory the previous history before the button was pushed
     */
    public void assertHistory(ArgumentsForTests arguments, String calculatorHistory)
    {
        String firstNumber = arguments.getFirstNumber();
        String firstUnaryTextPaneResult = arguments.getFirstUnaryResult();
        String firstUnaryValuesResult = firstUnaryTextPaneResult;
        if (firstUnaryTextPaneResult.split(ARGUMENT_REGEX).length > 1) // "0|INFINITY or 35|35 +"
        {
            firstUnaryValuesResult = firstUnaryTextPaneResult.split(ARGUMENT_REGEX)[0];
            firstUnaryTextPaneResult = firstUnaryTextPaneResult.split(ARGUMENT_REGEX)[1];
        }
        String buttonText = LEFT_PARENTHESIS + arguments.getOperatorUnderTest() + RIGHT_PARENTHESIS + SPACE;

        assertEquals(calculatorHistory, calculator.getHistoryTextPaneValue(),
                "History textPane not as expected");
    }
    
    /**
     * Returns a list of unary operators. Unary operators
     * are operators that requires a valid value in the
     * text pane to perform that operation.
     * @return list of basic panel unary operators
     */
    public List<String> getBasicPanelUnaryOperators()
    { return List.of(PERCENT, SQUARE_ROOT, SQUARED, FRACTION,
            CLEAR_ENTRY, CLEAR,   DELETE,  NEGATE,
            DECIMAL, MEMORY_STORE, MEMORY_ADD, MEMORY_SUBTRACT); }

    /**
     * Returns a list of binary operators. Binary operators
     * are operators that requires values[0] and values[1]
     * to perform an operation.
     * @return list of basic panel binary operators
     */
    public List<String> getBasicPanelBinaryOperators()
    { return List.of(ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, EQUALS); }
}

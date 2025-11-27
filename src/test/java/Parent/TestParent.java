package Parent;

import Calculators.Calculator;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static Types.Texts.*;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestParent
{
    public Calculator calculator;

    public static AutoCloseable mocks;

    public void postConstructCalculator()
    {
        calculator.pack();
        calculator.setVisible(true);
    }

    /**
     * Sets up the when-then for an invalid operator test.
     * Only supports a number and an optional unary operator.
     * Used in: InvalidMemoryStoreTests
     * @param actionEvent the action event
     * @param operatorUnderTest the operator under test
     */
    public void setupInvalidWhenThen(ActionEvent actionEvent, String operatorUnderTest, String firstNumber, String firstOperator)
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
     * Performs the number button action for each
     * character in the given number string.
     * Works with any positive, negative, whole,
     * or fractional numbers.
     * @param actionEvent the actionEvent
     * @param number the number
     */
    public void performNumberButtonActionForEachCharacter(ActionEvent actionEvent, String number)
    {
        int firstNumLength = number.length();
        if (firstNumLength == 0 || calculator.getBadText(EMPTY).contains(number))
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
                if ((i+1) == firstNumLength)
                    assertEquals(calculator.addThousandsDelimiter(number), calculator.getTextPaneValue(), "textPane value is not as expected");
                else
                    assertEquals(number.substring(0,(i+1)), calculator.getTextPaneValue(), "textPane value is not as expected");
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
            case DIVISION -> calculator.performDivideButtonAction(actionEvent);
            case EQUALS -> calculator.performEqualsButtonAction(actionEvent);
            case FRACTION -> calculator.getBasicPanel().performFractionButtonAction(actionEvent);
            case HISTORY_OPEN, HISTORY_CLOSED -> calculator.performHistoryAction(actionEvent);
            case LEFT_PARENTHESIS -> calculator.getProgrammerPanel().performLeftParenthesisButtonAction(actionEvent);
            case LSH -> calculator.getProgrammerPanel().performLeftShiftButtonAction(actionEvent);
            case MEMORY_ADDITION -> calculator.performMemoryAdditionAction(actionEvent);
            case MEMORY_CLEAR -> calculator.performMemoryClearAction(actionEvent);
            case MEMORY_RECALL -> calculator.performMemoryRecallAction(actionEvent);
            case MEMORY_STORE -> calculator.performMemoryStoreAction(actionEvent);
            case MEMORY_SUBTRACTION -> calculator.performMemorySubtractionAction(actionEvent);
            case MODULUS -> calculator.getProgrammerPanel().performModulusButtonAction(actionEvent);
            case MULTIPLICATION -> calculator.performMultiplyButtonAction(actionEvent);
            case NOT -> calculator.getProgrammerPanel().performNotButtonAction(actionEvent);
            case OR -> calculator.getProgrammerPanel().performOrButtonAction(actionEvent);
            case PERCENT -> calculator.getBasicPanel().performPercentButtonAction(actionEvent);
            case RIGHT_PARENTHESIS -> calculator.getProgrammerPanel().performRightParenthesisButtonAction(actionEvent);
            case RSH -> calculator.getProgrammerPanel().performRightShiftButtonAction(actionEvent);
            case SQUARED -> calculator.getBasicPanel().performSquaredButtonAction(actionEvent);
            case SQUARE_ROOT -> calculator.performSquareRootButtonAction(actionEvent);
            case SUBTRACTION -> calculator.performSubtractButtonAction(actionEvent);
            case XOR -> calculator.getProgrammerPanel().performXorButtonAction(actionEvent);
            default -> logger.warn("Unknown operator selected: '{}'", nextOperator);
        }
    }
}

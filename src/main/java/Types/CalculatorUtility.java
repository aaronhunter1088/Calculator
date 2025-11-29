package Types;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static Types.Texts.*;
import static Types.Texts.COMMA;
import static Types.Texts.DECIMAL;
import static Types.Texts.EMPTY;
import static Types.Texts.SUBTRACTION;

/**
 * CalculatorUtility
 * <p>
 * This class provides utility functions for
 * the Calculator application. Any commonly
 * used methods that are needed in multiple
 * locations can be added here.
 *
 * @author Michael Ball
 * @version 4.0
 */
public class CalculatorUtility
{
    private static final Logger LOGGER = LogManager.getLogger(CalculatorUtility.class);
    /**
     * Adds the delimiter to the number if appropriate
     * Works by reversing the string value. Finding the
     * appropriate decimal index, it will then add a
     * delimiter every three characters.
     * @param valueToAdjust the passed in value
     * @return the value with the delimiter chosen
     */
    public static String addThousandsDelimiter(String valueToAdjust, String delimiter)
    {
        if (valueToAdjust.isEmpty()) return valueToAdjust;
        //String delimiter = getThousandsDelimiter();
        try { new BigDecimal(valueToAdjust); }
        catch (NumberFormatException nfe) { return valueToAdjust; }
        boolean negativeControl = isNegativeNumber(valueToAdjust);
        if (valueToAdjust.contains(delimiter)) valueToAdjust = removeThousandsDelimiter(valueToAdjust, delimiter);
        valueToAdjust = getValueWithoutAnyOperator(valueToAdjust);
        // Keep negative sign just for length checks
        if (!isFractionalNumber(valueToAdjust) && valueToAdjust.length() <= 3) {
            return valueToAdjust;
        }
        else if (!isFractionalNumber(valueToAdjust) && negativeControl && valueToAdjust.length() <= 4) {
            return valueToAdjust;
        }
        else {
            String numberOnLeft = getNumberOnLeftSideOfDecimal(valueToAdjust);
            if (numberOnLeft.length() <= 3) {
                return valueToAdjust;
            } else if (negativeControl && numberOnLeft.length() <= 4) {
                return valueToAdjust;
            }
        }
        LOGGER.debug("Adding delimiter: '{}' to '{}'", delimiter, valueToAdjust);
        // Possible decimal number: 05.4529 or 4529 or -123. Remove negative sign for processing
        valueToAdjust = valueToAdjust.replace(SUBTRACTION, EMPTY);
        StringBuffer adjusted = new StringBuffer();
        StringBuffer reversed = new StringBuffer(valueToAdjust).reverse();
        int indexOfDecimal = reversed.indexOf(DECIMAL);
        //if (indexOfDecimal == -1) indexOfDecimal = 0;
        LOGGER.debug("index of decimal: {}", indexOfDecimal);
        if (indexOfDecimal > 0) adjusted.append(reversed.substring(0, indexOfDecimal+1));
        if (indexOfDecimal == 0) adjusted.append(DECIMAL);
        for (int i=indexOfDecimal+1; i<reversed.length(); i+=3)
        {
            if (i % 3 == 0 && i != 0)
            {
                if (!adjusted.toString().endsWith(DECIMAL)) adjusted.append(delimiter);
                if (i+1 > reversed.length() || i+2 > reversed.length() || i+3 > reversed.length())
                {
                    adjusted.append(reversed.substring(i));
                }
                else
                {
                    adjusted.append(reversed.substring(i, i+3));
                }
            }
            else
            {
                if (!adjusted.toString().endsWith(DECIMAL)) adjusted.append(delimiter);
                if (i+1 > reversed.length() || i+2 > reversed.length() || i+3 > reversed.length())
                {
                    adjusted.append(reversed.substring(i));
                }
                else
                {
                    adjusted.append(reversed.substring(i, i+3));
                }
            }
        }
        adjusted = new StringBuffer(adjusted).reverse();
        if (adjusted.toString().endsWith(COMMA)) adjusted = new StringBuffer(adjusted.substring(0, adjusted.length() - 1));
        if (negativeControl) adjusted = new StringBuffer(SUBTRACTION).append(adjusted);
        LOGGER.debug("commas added: {}", adjusted);
        return adjusted.toString();
    }

    /**
     * Converts a number to its negative equivalent
     * @param number the value to convert
     * @return Fully tested
     */
    public static String convertToNegative(String number)
    {
        LOGGER.debug("Converting {} to negative number", number.replaceAll(NEWLINE, EMPTY));
        if (!number.startsWith(SUBTRACTION))
            number = SUBTRACTION + number.replace(NEWLINE, EMPTY);
        return number;
    }

    /**
     * Converts a number to its positive equivalent
     * @param number the value to convert
     * @return Fully tested
     */
    public static String convertToPositive(String number)
    {
        LOGGER.debug("Converting {} to positive number", number.replaceAll(NEWLINE, EMPTY));
        number = number.replaceAll(SUBTRACTION, EMPTY).trim();
        return number;
    }



    /**
     * Returns the number representation to the left of the decimal
     *
     * @param currentNumber the value to split on
     * @return the value to the left of the decimal
     */
    public static String getNumberOnLeftSideOfDecimal(String currentNumber)
    {
        String leftSide;
        if (!isFractionalNumber(currentNumber)) return currentNumber;
        int index = currentNumber.indexOf(DECIMAL);
        if (index <= 0 || (index + 1) > currentNumber.length()) leftSide = EMPTY;
        else
        {
            leftSide = currentNumber.substring(0, index);
            if (StringUtils.isEmpty(leftSide)) {
                LOGGER.debug("Number to the left of the decimal is empty. Setting to {}", ZERO);
                leftSide = ZERO;
            }
        }
        return leftSide;
    }

    /**
     * Returns the number representation to the right of the decimal
     *
     * @param currentNumber the value to split on
     * @return the value to the right of the decimal
     */
    public static String getNumberOnRightSideOfDecimal(String currentNumber)
    {
        String rightSide;
        int index = currentNumber.indexOf(DECIMAL);
        if (index < 0 || (index + 1) >= currentNumber.length()) rightSide = EMPTY;
        else
        {
            rightSide = currentNumber.substring(index + 1);
            if (StringUtils.isEmpty(rightSide)) {
                LOGGER.debug("Number to the right of the decimal is empty. Setting to {}", ZERO);
                rightSide = ZERO;
            }
        }
        return rightSide;
    }

    /**
     * Returns the text in the textPane without
     * any operator.
     * @return the plain textPane text
     */
    public static String getValueWithoutAnyOperator(String valueToAdjust)
    {
        boolean negativeControl = isNegativeNumber(valueToAdjust);
        valueToAdjust = valueToAdjust
                .replace(ADDITION, EMPTY) // target, replacement
                .replace(SUBTRACTION, EMPTY)
                .replace(MULTIPLICATION, EMPTY)
                .replace(DIVISION, EMPTY)
                .replace(MODULUS, EMPTY)
                //.replace(LEFT_PARENTHESIS, BLANK)
                //.replace(RIGHT_PARENTHESIS, BLANK)
                .replace(ROL, EMPTY)
                .replace(ROR, EMPTY)
                .replace(XOR, EMPTY) // XOR must be checked before OR, otherwise OR will be replaced when XOR is there, leaving X, which is not desired
                .replace(OR, EMPTY)
                .replace(AND, EMPTY)
                .replace(MEMORY_STORE, EMPTY)
                .strip();
        if (negativeControl) valueToAdjust = SUBTRACTION + valueToAdjust;
        return valueToAdjust;
    }

    /**
     * Tests whether a number is a fractional number. A
     * fractional number contains a decimal point.
     * @param number the number to test
     * @return true if the number contains a decimal point,
     * false otherwise
     */
    public static boolean isFractionalNumber(String number)
    { return number.contains(DECIMAL); }

    /**
     * Determines whether a number is positive
     *
     * @param number the value to test
     * @return either true or false based on result
     */
    public static boolean isPositiveNumber(String number)
    { return !number.startsWith(SUBTRACTION); }

    /**
     * Determines whether a number is negative
     *
     * @param number the value to test
     * @return either true or false based on result
     */
    public static boolean isNegativeNumber(String number)
    { return number.startsWith(SUBTRACTION); }

//    /**
//     * Returns true if the minimum value has
//     * been met or false otherwise
//     * 0.0000001 or 10^-7
//     * @return boolean is minimum value has been met
//     */
//    public static boolean isMinimumValue()
//    {
//        boolean v1 = false, v2 = false;
//        if (valuesPosition == 1)
//        {
//            v1 = !values[0].isEmpty() && Double.parseDouble(values[0]) <= Double.parseDouble(MIN_VALUE);
//            LOGGER.debug("is v[0]: '{}' <= {}: {}", values[0], MIN_VALUE, v1);
//            v2 = !values[1].isEmpty() && Double.parseDouble(values[1]) <= Double.parseDouble(MIN_VALUE);
//            LOGGER.debug("is v[1]: '{}' <= {}: {}", values[1], MIN_VALUE, v2);
//
//        }
//        else
//        {
//            v1 = !values[0].isEmpty() && Double.parseDouble(values[0]) <= Double.parseDouble(MIN_VALUE);
//            LOGGER.debug("is v[0]: '{}' <= {}: {}", values[0], MIN_VALUE, v1);
//        }
//        return v1 || v2;
//    }

    /**
     * Checks if the resulting answer has met the minimum value
     * @param valueToCheck String the value to check
     * @return boolean true if the minimum value has been met
     */
    public static boolean isMinimumValue(String valueToCheck)
    {
        boolean v1 = !valueToCheck.isEmpty() && new BigDecimal(valueToCheck).doubleValue() <= new BigDecimal(MIN_VALUE).doubleValue();
        LOGGER.debug("is value: '{}' <= {}: {}", valueToCheck, MIN_VALUE, v1);
        return v1;
    }

//    /**
//     * Returns true if the maximum value has
//     * been met or false otherwise
//     * 9,999,999 or (10^8) -1
//     * @return boolean if maximum value has been met
//     */
//    public static boolean isMaximumValue()
//    {
//        boolean v1 = false, v2 = false;
//        if (valuesPosition == 1) {
//            v1 = !values[0].isEmpty() && new BigDecimal(values[0]).compareTo(new BigDecimal(MAX_VALUE)) >= 0;
//            LOGGER.debug("is v[0]: '{}' >= {}: {}", values[0], MAX_VALUE, v1);
//            v2 = !values[1].isEmpty() && new BigDecimal(values[1]).compareTo(new BigDecimal(MAX_VALUE)) >= 0;
//            LOGGER.debug("is v[1]: '{}' >= {}: {}", values[1], MAX_VALUE, v2);
//        }
//        else {
//            v1 = !values[0].isEmpty() && new BigDecimal(values[0]).compareTo(new BigDecimal(MAX_VALUE)) >= 0;
//            LOGGER.debug("is v[0]: '{}' >= {}: {}", values[0], MAX_VALUE, v1);
//
//        }
//        return v1 || v2;
//    }

    /**
     * Checks if the resulting answer has met the maximum value
     * @param valueToCheck String the value to check in its purest form
     * @return boolean true if the maximum value has been met
     */
    public static boolean isMaximumValue(String valueToCheck)
    {
        boolean v1 = !valueToCheck.isEmpty() && new BigDecimal(valueToCheck).compareTo(new BigDecimal(MAX_VALUE)) >= 0;
        LOGGER.debug("is value: '{}' >= {}: {}", valueToCheck, MAX_VALUE, v1);
        return v1;
    }

    /**
     * Removes commas from the number if appropriate
     * @param valueToAdjust the number to adjust
     * @return the value without commas
     */
    public static String removeThousandsDelimiter(String valueToAdjust, String delimiter)
    {
        if (valueToAdjust == null || valueToAdjust.isEmpty()) return valueToAdjust;
        return valueToAdjust.replace(delimiter, EMPTY);
    }
}

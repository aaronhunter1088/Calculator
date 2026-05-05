package Utilities;

import Types.Texts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static Types.Texts.*;

public class PemdasUtility {

    private static final Logger LOGGER = LogManager.getLogger(PemdasUtility.class);
    private static List<String> tokens;
    private static int pos;

    /**
     * Converts the result to a BigDecimal.
     * @param result the result to convert
     * @return the converted result
     */
    public static BigDecimal convertToBigDecimal(double result)
    {
        BigDecimal updated = BigDecimal.valueOf(result);
        if (updated.setScale(3, RoundingMode.UP).toString().endsWith(".000")) {
            updated = updated.setScale(0, RoundingMode.UP);
        }
        LOGGER.info("Converted result: {}", updated);
        return updated;
    }

    /**
     * Takes the equation as input and evaluates it.
     * @param equation the equation to evaluate
     * @return the result of the equation
     */
    public static double parse(String equation)
    {
        LOGGER.debug("Parsing equation {}", equation);
        tokens = tokenize(equation);
        pos = 0;
        double result = parseExpression();
        if (pos != tokens.size()) {
            throw new RuntimeException("Unexpected token: " + tokens.get(pos));
        }
        return result;
    }

    // Level 1 — addition and subtraction (lowest precedence)
    public static double parseExpression()
    {
        double left = parseTerm();
        while (pos < tokens.size()) {
            String op = tokens.get(pos);
            if (!op.equals(ADDITION) && !op.equals(SUBTRACTION)) break;
            pos++;
            double right = parseTerm();
            left = op.equals(ADDITION) ? left + right : left - right;
        }
        return left;
    }

    // Level 2 — multiplication and division
    public static double parseTerm()
    {
        double left = parseFactor();
        while (pos < tokens.size()) {
            String op = tokens.get(pos);
            if (!op.equals("*") && !op.equals("/")) break;
            pos++;
            double right = parseFactor();
            if (op.equals("/")) {
                if (right == 0) throw new ArithmeticException("Division by zero");
                left /= right;
            } else {
                left *= right;
            }
        }
        return left;
    }

    // Level 3 — exponentiation, right-associative (2^3^2 = 2^(3^2) = 512)
    public static double parseFactor()
    {
        double base = parseBase();
        if (pos < tokens.size() && tokens.get(pos).equals("^")) {
            pos++;
            double exp = parseFactor();   // right-recursive, not left
            return Math.pow(base, exp);
        }
        return base;
    }

    // Level 4 — atoms: numbers, parenthesised groups, implicit multiply
    public static double parseBase()
    {
        if (pos >= tokens.size()) {
            throw new RuntimeException("Unexpected end of expression");
        }

        String token = tokens.get(pos);

        // Unary minus  e.g. -(3+2) or -5
        if (token.equals(SUBTRACTION)) {
            pos++;
            return -parseBase();
        }

        // Parenthesised group  e.g. (4 × 2)
        if (token.equals(LEFT_PARENTHESIS)) {
            return parseParen();
        }

        // Number — may be followed by '(' for implicit multiply: 5(4×2) or 2((…))
        if (isNumber(token)) {
            pos++;
            double val = Double.parseDouble(token);
            // Implicit multiply: keep consuming '(' groups
            while (pos < tokens.size() && tokens.get(pos).equals(LEFT_PARENTHESIS)) {
                val *= parseParen();
            }
            return val;
        }

        throw new RuntimeException("Unexpected token: " + token);
    }

    /**
     * Consumes a '(' … ')' block and return the value
     * @return the result of the equation between the
     * '(' and ')' parentheses.
     */
    private static double parseParen()
    {
        expect(LEFT_PARENTHESIS);
        double val = parseExpression();
        expect(RIGHT_PARENTHESIS);
        return val;
    }

    // Tokenizer
    private static List<String> tokenize(String input)
    {
        LOGGER.debug("Tokenizing input {}", input);
        List<String> result = new ArrayList<>();
        int i = 0;
        while (i < input.length())
        {
            char c = input.charAt(i);

            if (Character.isWhitespace(c))
            {
                i++;
                continue;
            }

            // Number (integer or decimal)
            char charC = Texts.DECIMAL.charAt(0);
            if (Character.isDigit(c) || c == charC)
            {
                StringBuilder num = new StringBuilder();
                while (i < input.length() &&
                        (Character.isDigit(input.charAt(i)) || input.charAt(i) == charC)) {
                    num.append(input.charAt(i++));
                }
                result.add(num.toString());
                continue;
            }

            // Single-character operator or paren
            // Normalise common alternate symbols
            String charCAsString = String.valueOf(c);
            switch (charCAsString) {
                case Texts.ADDITION:
                case SUBTRACTION:
                case Texts.LEFT_PARENTHESIS:
                case Texts.RIGHT_PARENTHESIS:
                    result.add(String.valueOf(c)); break;
                case Texts.MULTIPLICATION:
                    result.add("*"); break;
                case Texts.DIVISION:
                    result.add("/"); break;
                default:
                    throw new RuntimeException("Unknown character: " + c);
            }
            i++;
        }
        return result;
    }

    // Helpers

    private static void expect(String expected)
    {
        if (pos >= tokens.size() || !tokens.get(pos).equals(expected)) {
            throw new RuntimeException("Expected '" + expected + "'");
        }
        pos++;
    }

    private static boolean isNumber(String s)
    {
        try { Double.parseDouble(s); return true; }
        catch (NumberFormatException e) { return false; }
    }

}
package Types;

/**
 * Texts
 * <p>
 * This class defines all texts used within the Calculator application.
 *
 * Unicode values from <a href="https://www.compart.com/en/unicode/html">Unicodes</a>
 *
 * @author Michael Ball
 * @version 4.0
 */
public class Texts {
    // Calculator
    public static final String SEGOE_UI = "Segoe UI";
    public static final String VERDANA = "Verdana";
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final String TWO = "2";
    public static final String THREE = "3";
    public static final String FOUR = "4";
    public static final String FIVE = "5";
    public static final String SIX = "6";
    public static final String SEVEN = "7";
    public static final String EIGHT = "8";
    public static final String NINE = "9";
    public static final String CLEAR = "C";
    public static final String CLEAR_ENTRY = "CE";
    public static final String DELETE = "⌫";
    public static final String DECIMAL = ".";
    public static final String FRACTION = "⅟x";
    public static final String PERCENT = "%";
    public static final String SQUARE_ROOT = "√";
    public static final String MEMORY_CLEAR = "MC";
    public static final String MEMORY_RECALL = "MR";
    public static final String MEMORY_STORE = "MS";
    public static final String MEMORY_ADDITION = "M+";
    public static final String MEMORY_SUBTRACTION = "M-";
    public static final String HISTORY_CLOSED = "H▼";
    public static final String HISTORY_OPEN = "H▲";
    public static final String HISTORY = "History";
    public static final String ADDITION = "+";
    public static final String SUBTRACTION = "-";
    public static final String MULTIPLICATION = "✕";
    public static final String DIVISION = "÷";
    public static final String EQUALS = "=";
    public static final String NEGATE = "±";
    public static final String BLANK = "";
    public static final String SPACE = " ";
    public static final String SQUARED = "x²";
    public static final String YES = "YES";
    public static final String NO = "NO";
    // Menu Choices
    public static final String STYLE = "Style";
    public static final String VIEW = "View";
    public static final String EDIT = "Edit";
    public static final String HELP = "Help";
    // Look Menu Options
    public static final String METAL = "Metal";
    public static final String SYSTEM = "System";
    public static final String WINDOWS = "Windows";
    public static final String MOTIF = "Motif";
    public static final String GTK = "Gtk";
    public static final String APPLE = "Apple";
    // Edit Menu Options
    public static final String COPY = "Copy";
    public static final String PASTE = "Paste";
    public static final String CLEAR_HISTORY = "Clear History";
    public static final String SHOW_MEMORIES = "Show Memories";
    // Help Menu Options
    public static final String VIEW_HELP = "View Help";
    public static final String ABOUT_CALCULATOR = "About Calculator";
    // Error Messages
    public static final String CANNOT_DIVIDE_BY_ZERO = "Cannot divide by 0";
    public static final String NOT_A_NUMBER = "Not a Number";
    public static final String NUMBER_TOO_BIG = "Number too big";
    public static final String ENTER_A_NUMBER = "Enter a Number";
    public static final String ONLY_POSITIVES = "Only positive numbers";
    public static final String ERROR = "Error";
    public static final String INFINITY = "Infinity";
    public static final String CANNOT_PERFORM_OPERATION = "Cannot perform %s operation";
    public static String cannotPerformOperation(String operation) {
        return String.format(CANNOT_PERFORM_OPERATION, operation);
    }
    public static final String PERFORMED_OPERATION = "Performed %s operation";
    public static String performedOperation(String operation) {
        return String.format(PERFORMED_OPERATION, operation);
    }
    // Test Error Messages
    public static final String TEXT_PANE_WRONG_VALUE = "Text Pane has the wrong value";

    // Basic
    public static String pressedButton(String buttonChoice) {
        return String.format("%s %s", PRESSED, buttonChoice);
    }
    public static final String PRESSED = "Pressed";
    public static final String COMMA = ",";

    // Programmer
    public static final String MODULUS = "MOD";
    public static final String LEFT_PARENTHESIS = "(";
    public static final String RIGHT_PARENTHESIS = ")";
    public static final String ROL = "ROL";
    public static final String ROR = "ROR";
    public static final String OR = "OR";
    public static final String XOR = "XOR";
    public static final String AND = "AND";
    public static final String LSH = "LSH";
    public static final String RSH = "RSH";
    public static final String NOT = "NOT";
    public static final String A = "A";
    public static final String B = "B";
    public static final String C = "C";
    public static final String D = "D";
    public static final String E = "E";
    public static final String F = "F";
    public static final String BYTE = "Byte";
    public static final String WORD = "Word";
    public static final String DWORD = "DWord";
    public static final String QWORD = "QWord";
    public static final String BASE = "Base";
    public static final String BIN = "Bin";
    public static final String DEC = "Dec";
    public static final String OCT = "Oct";
    public static final String HEX = "Hex";
    public static final String SHIFT = "↑";

    // Scientific

    // Date
    public static final String SAME = "Same";
    public static final String YEAR = "Year";
    public static final String MONTH = "Month";
    public static final String WEEK = "Week";
    public static final String DAY = "Day";
    public static final String ADD_OR_SUB_RESULT = "Result";
    public static final String LOWER_CASE_S = "s";
    public static final String PUSHED_CLEAR = "Pushed clear";
    public static final String NEWLINE = "\n";
    public static final String UNDERSCORE = "_";
    public static final String MAX_VALUE = "9999999";
    public static final String MIN_VALUE = "-9999999";

    // Converter

}

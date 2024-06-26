package Types;

/**
 * This class defines all texts used within
 * the Calculator application
 * Texts.name() returns ENUM value
 * Texts.getValue() returns ENUM("thisValue")
 */
public enum Texts {
    // Calculator
    SEGOE_UI("Segoe UI"),
    VERDANA("Verdana"),
    ZERO("0"),
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    CLEAR("C"),
    CLEAR_ENTRY("CE"),
    DELETE("⌫"),
    DECIMAL("."),
    FRACTION("⅟x"),
    PERCENT("%"),
    SQUARE_ROOT("√"),
    MEMORY_CLEAR("MC"),
    MEMORY_RECALL("MR"),
    MEMORY_STORE("MS"),
    MEMORY_ADDITION("M+"),
    MEMORY_SUBTRACTION("M-"),
    HISTORY_CLOSED("H▼"),
    HISTORY_OPEN("H▲"),
    HISTORY("History"),
    ADDITION("+"),
    SUBTRACTION("-"),
    MULTIPLICATION("✕"),
    DIVISION("÷"),
    EQUALS("="),
    NEGATE("±"),
    BLANK(""),
    SQUARED("x²"),
    // Menu Choices
    LOOK("Look"),
    VIEW("View"),
    EDIT("Edit"),
    HELP("Help"),
    // Look Menu Options
    METAL("Metal"),
    SYSTEM("System"),
    WINDOWS("Windows"),
    MOTIF("Motif"),
    GTK("Gtk"),
    APPLE("Apple"),
    // Edit Menu Options
    COPY("Copy"),
    PASTE("Paste"),
    CLEAR_HISTORY("Clear History"),
    SHOW_MEMORIES("Show Memories"),
    // Help Menu Options
    VIEW_HELP("View Help"),
    ABOUT_CALCULATOR("About Calculator"),
    // Error Messages
    CANNOT_DIVIDE_BY_ZERO("Cannot divide by " + ZERO.getValue()),
    NOT_A_NUMBER("Not a Number"),
    NUMBER_TOO_BIG("Number too big"),
    ENTER_A_NUMBER("Enter a Number"),
    ONLY_POSITIVES("Only positive numbers"),
    E("E"),
    INFINITY("Infinity"),
    COMMA(","),

    // Basic

    // Programmer
    MODULUS("MOD"),
    LEFT_PARENTHESIS("("),
    RIGHT_PARENTHESIS(")"),
    RoL("RoL"),
    RoR("RoR"),
    OR("OR"),
    XOR("XOR"),
    AND("AND");

    // Scientific

    // Date

    // Converter

    final String value;
    Texts(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

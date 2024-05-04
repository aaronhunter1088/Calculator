package Types;

/**
 * The different units available when using the
 * ConverterPanel in a specific ConverterType
 */
public enum ConverterUnits
{
    // Angle converter units
    DEGREES("Degrees"),
    RADIANS("Radians"),
    GRADIANS("Gradians"),

    // Area converter units
    SQUARE_MILLIMETERS("Square Millimeters"),
    SQUARE_CENTIMETERS("Square Centimeters"),
    SQUARE_METERS("Square Meters"),
    HECTARES("Hectares"),
    SQUARE_KILOMETERS("Square Kilometers"),
    SQUARE_INCHES("Square Inches"),
    SQUARE_FEET("Square Feet"),
    SQUARE_YARD_ACRES("Square Yard Acres"),
    SQUARE_MILES("Square Miles");

    private final String name;
    ConverterUnits(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
}

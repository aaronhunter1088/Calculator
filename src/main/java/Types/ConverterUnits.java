package Types;

public enum ConverterUnits
{
    DEGREES("Degrees"), // Angle converter units
    RADIANS("Radians"),
    GRADIANS("Gradians"), // Angle converter units
    SQUARE_MILLIMETERS("Square Millimeters"), // Area converter units
    SQUARE_CENTIMETERS("Square Centimeters"),
    SQUARE_METERS("Square Meters"),
    HECTARES("Hectares"),
    SQUARE_KILOMETERS("Square Kilometers"),
    SQUARE_INCHES("Square Inches"),
    SQUARE_FEET("Square Feet"),
    SQUARE_YARD_ACRES("Square Yard Acres"),
    SQUARE_MILES("Square Miles"); // Area converter units

    private final String name;
    ConverterUnits(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
}

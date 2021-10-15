package version4;

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

    private String name;
    ConverterUnits(String s1) {
        setName(s1);
    }

    private void setName(String name) {
        this.name = name;
    }
    protected String getName() {
        return this.name;
    }

}

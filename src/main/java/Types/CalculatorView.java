package Types;

import Interfaces.CalculatorType;

/**
 * CalculatorView
 * <p>
 * This enum contains the different
 * calculator views for the calculator
 *
 * @author Michael Ball
 * @version 4.0
 */
public enum CalculatorView implements CalculatorType
{
    VIEW_BASIC("Basic"),
    VIEW_PROGRAMMER("Programmer"),
    VIEW_SCIENTIFIC("Scientific"),
    VIEW_DATE("Date"),
    VIEW_CONVERTER("Converter");

    private final String view;

    CalculatorView(String view)
    {
        this.view = view;
    }

    @Override
    public String getValue() { return getView(); }

    public String getView()
    {
        return view;
    }

    public String getName()
    {
        return this.name();
    }
}

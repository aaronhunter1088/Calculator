package Calculators;

import java.io.Serial;

public class CalculatorError extends Exception
{
    @Serial
    final static private long serialVersionUID = 4L;
    private String message;
    private Exception exception;

    /**
     * A zero argument constructor to create
     * a CalculatorError
     */
    public CalculatorError() { super(); }

    /**
     * Create a CalculatorError with a custom message
     * @param message the error that occurred
     */
    public CalculatorError(String message)
    {
        super(message);
        setMessage(message);
    }

    /**
     * Create a CalculatorError with a specific Exception
     * @param exception the Exception that occurred
     */
    public CalculatorError(Exception exception)
    {
        super(exception);
        setException(exception);
    }
    /**
     * The main constructor used when throwing
     * a CalculatorError
     * @param message the error message
     * @param exception the exception
     */
    public CalculatorError(String message, Exception exception)
    {
        super(message, exception);
        setMessage(message);
        setException(exception);
    }

    /**
     * Returns the message
     * @return String the error message
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Sets the error message
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets the exception
     * @param exception the exception
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }

    /**
     * Returns a String representation of the CalculatorError
     * @return the CalculatorError as a String
     */
    @Override
    public String toString()
    {
        return "CalculatorError: '" + message + "' '"
                + exception.getClass().getSimpleName() + '\'';
    }
}

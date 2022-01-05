package Calculators;

public class CalculatorError extends Exception
{
    final static private long serialVersionUID = 1L;
    private String message;
    private Exception exception;
    public CalculatorError() { super(); }
    public CalculatorError(String message)
    {
        super(message);
        setMessage(message);
    }
    public CalculatorError(Exception e)
    {
        super(e);
        setException(e);
    }
    /**
     * Constructor for an exception which occurs in the calculator
     * @param message the error message
     * @param exception the exception
     */
    public CalculatorError(String message, Exception exception)
    {
        super(message, exception);
        setMessage(message);
        setException(exception);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString()
    {
        return this.message + " " + this.exception.getClass().getSimpleName();
    }
}

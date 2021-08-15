package version4;

public class CalculatorError_v4 extends Exception {

    private String message;
    private Exception exception;
    public CalculatorError_v4() { super(); }
    public CalculatorError_v4(String message) {
        super(message);
    }
    public CalculatorError_v4(Exception e) {
        super(e);
    }

    @Override
    public String getMessage() {
        return message;
    }
    public Exception getException() {
        return exception;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    /**
     * Constructor for an exception which occurs in the calculator_v3 version
     * @param message
     * @param exception
     */
    public CalculatorError_v4(String message, Exception exception)
    {
        super(message, exception);

    }


}

package version3;

public class Calculator_v3Error extends Exception {

    private String message;
    private Exception exception;
    public Calculator_v3Error() { super(); }
    public Calculator_v3Error(String message) {
        super(message);
    }
    public Calculator_v3Error(Exception e) {
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
    public Calculator_v3Error(String message, Exception exception)
    {
        super(message, exception);

    }


}

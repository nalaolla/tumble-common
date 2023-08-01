package kr.co.tumble.common.exception;

/**
 * ValidationException Class
 * UserDefinedException 상속
 */
@SuppressWarnings("serial")
public class ValidationException extends UserDefinedException {

    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(Throwable t) {
        super(t);
    }

    public ValidationException(String message, Throwable t) {
        super(message, t);
    }
}

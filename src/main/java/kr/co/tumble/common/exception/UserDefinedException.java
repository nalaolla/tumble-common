package kr.co.tumble.common.exception;

/**
 * UserDefinedException Class
 * RuntimeException 상속
 */
public class UserDefinedException extends RuntimeException {

    private static final long serialVersionUID = -1267763299523125793L;
    
    public UserDefinedException() {
        super();
    }

    public UserDefinedException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UserDefinedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDefinedException(String message) {
        super(message);
    }

    public UserDefinedException(Throwable cause) {
        super(cause);
    }

}

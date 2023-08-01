package kr.co.tumble.common.exception;

/**
 * FrameworkException Class
 * UserDefinedException 상속
 */
public class FrameworkException extends UserDefinedException {

	private static final long serialVersionUID = -2030431741813535458L;

	public FrameworkException() {
		super();
	}

	public FrameworkException(String message, Throwable cause) {
		super(message, cause);
	}

	public FrameworkException(String message) {
		super(message);
	}

	public FrameworkException(Throwable cause) {
		super(cause);
	}
}
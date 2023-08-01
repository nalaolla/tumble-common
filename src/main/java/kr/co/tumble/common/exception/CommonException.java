package kr.co.tumble.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * CommonException Class
 * UserDefinedException 상속
 */
@Getter
@Setter
public class CommonException extends UserDefinedException {

	private static final long serialVersionUID = 1L;

    private final String errorCode;
    private final String errorMessage;

	public CommonException(String message, Throwable cause) {
		super(message, cause);

		this.errorCode = "500";
		this.errorMessage = message;
	}

	public CommonException(String message) {
		super(message);

		this.errorCode = "500";
		this.errorMessage = message;
	}

	public CommonException(Throwable cause) {
		super(cause);

		this.errorCode = "500";
		this.errorMessage = cause.getMessage();
	}
	
	public CommonException(String errorCode, String errorMessage) {
		super(errorMessage);

		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

}

package kr.co.tumble.common.exception;

import lombok.Getter;

/**
 * InvalidTokenException Class
 * CommonException 상속
 */
@Getter
public class InvalidTokenException extends CommonException {

	private static final long serialVersionUID = 6044478616579898320L;

	private final String errorCode;
	private final String errorMessage;

	public static InvalidTokenException exception(AppError appError) {
		throw new InvalidTokenException(appError.getCode(), MessageResolver.getMessage(appError));
	}

	public static InvalidTokenException exception(AppError appError, Object[] args) {
		throw new InvalidTokenException(appError.getCode(), MessageResolver.getMessage(appError, args));
	}

	public InvalidTokenException(String errorCode, String errorMessage) {
		super(errorMessage);

		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}

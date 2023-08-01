package kr.co.tumble.common.exception;

import lombok.Getter;

/**
 * AppException Class
 * CommonException 상속
 */
@Getter
public class AppException extends CommonException {

	private static final long serialVersionUID = 6044478616579898320L;

	private final String errorCode;
	private final String errorMessage;
	private final Boolean isProcess;

	public static AppException exception(AppError appError) {
		throw new AppException(appError.getCode(), MessageResolver.getMessage(appError), appError.getIsProcess());
	}

	public static AppException exception(AppError appError, Object[] args) {
		throw new AppException(appError.getCode(), MessageResolver.getMessage(appError, args), appError.getIsProcess());
	}

	public static AppException exception(AppError appError, Object[] args1, Object[] args2) {
		throw new AppException(appError.getCode(), MessageResolver.getMessage(appError, args1, args2), appError.getIsProcess());
	}

	public AppException(String errorCode, String errorMessage, Boolean isProcess) {
		super(errorMessage);

		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.isProcess = isProcess;
	}

	public AppException(String errorCode, String errorMessage) {
		super(errorMessage);

		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.isProcess = false;
	}
}

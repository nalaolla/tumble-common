package kr.co.tumble.common.rest;


import kr.co.tumble.common.exception.AppError;
import kr.co.tumble.common.exception.CommonException;
import kr.co.tumble.common.exception.MessageResolver;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Optional;

/**
 * HttpException Exception 모듈
 */
@Getter
public class HttpException extends CommonException {

	private static final long serialVersionUID = 6044478616579898320L;

	private final String errorCode;
	private final String errorMessage;
	private final HttpStatus httpStatus;

	public static HttpException exception(AppError appError) {
		throw new HttpException(appError.getCode(), MessageResolver.getMessage(appError));
	}

	public static HttpException exception(AppError appError, Object[] args) {
		throw new HttpException(appError.getCode(), MessageResolver.getMessage(appError, args));
	}

	public HttpException(String errorCode, String errorMessage) {
		super(errorMessage);

		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		String code = Optional.ofNullable(errorCode).orElse("500");
		httpStatus = HttpStatus.resolve(Integer.valueOf(String.valueOf(code)));
	}

}

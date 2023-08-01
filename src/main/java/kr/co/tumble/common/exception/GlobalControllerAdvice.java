package kr.co.tumble.common.exception;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.apache.commons.collections.CollectionUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import kr.co.tumble.common.context.ConfigProperties;
import kr.co.tumble.common.filter.RequestLoggingFilter;
import kr.co.tumble.common.rest.HttpException;
import kr.co.tumble.common.rest.Response;
import kr.co.tumble.common.rest.ValidationError;
import kr.co.tumble.common.util.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

/**
 * GlobalControllerAdvice
 */
@Slf4j
public class GlobalControllerAdvice {

	private String attributeError = "error";

	@ExceptionHandler(HttpInterfaceResponseException.class)
	protected ResponseEntity<Object> handleHttpInterfaceException(HttpInterfaceResponseException e, WebRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);

		String code = Optional.ofNullable(e.getErrorCode()).orElse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		String message = e.getErrorMessage();
		HttpStatus httpStatus = HttpStatus.resolve(Integer.valueOf(code));

		if (httpStatus == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		// HttpInterfaceResponseException의 경우 message값에 json 데이터가 넘어오기 때문에
		// ErrorCode로 반환하지 않고 Object형태로 그대로 반환함.
		return new ResponseEntity<>(message, httpStatus);
	}

	@ExceptionHandler(HttpException.class)
	protected ResponseEntity<Object> handleHttpException(HttpException e, WebRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);

		String code = Optional.ofNullable(e.getErrorCode()).orElse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		String message = e.getErrorMessage();
		int httpStatus = Integer.parseInt(code);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(WebClientResponseException.class)
	protected ResponseEntity<Object> handleWebClientResponseException(WebClientResponseException e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.error("", e);

		String code = Optional.ofNullable(String.valueOf(e.getStatusCode().value())).orElse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		String message = e.getMessage();
		int httpStatus = Integer.parseInt(code);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(WebClientRequestException.class)
	protected ResponseEntity<Object> handleWebClientRequestException(WebClientRequestException e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.error("", e);

		String code = String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value());
		String message = e.getMessage();
		int httpStatus = Integer.parseInt(code);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(AsyncRequestTimeoutException.class)
	protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.error("", e);

		String code = String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value());
		String message = e.getMessage();
		int httpStatus = Integer.parseInt(code);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleException(Exception e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.error("", e);

		String code = String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value());
		String message = e.getMessage();
		int httpStatus = Integer.parseInt(code);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(value = {
			NullPointerException.class,
			IOException.class,
			ArrayIndexOutOfBoundsException.class,
			EntityNotFoundException.class,
			StringIndexOutOfBoundsException.class,
			IndexOutOfBoundsException.class,
			UnsupportedEncodingException.class
	})
	protected ResponseEntity<Object> handleErrorException(Exception e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.error("handleErrorException {}", e);

		String code = String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value());
		String message = e.getMessage();
		int httpStatus = Integer.parseInt(code);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(value = {
			IllegalArgumentException.class,
			IllegalStateException.class,
			ConstraintViolationException.class,
			JsonParseException.class,
			com.fasterxml.jackson.core.JsonParseException.class,
			HttpMessageNotReadableException.class,
			MethodArgumentTypeMismatchException.class,
			MissingServletRequestParameterException.class,
			MultipartException.class
	})
	protected ResponseEntity<Object> handleIllegalException(Exception e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.error("handleIllegalException {}", e);

		String code = String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value());
		String message = e.getMessage();
		int httpStatus = Integer.parseInt(code);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(ValidationException.class)
	protected ResponseEntity<Object> handleValidationException(ValidationException e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.warn(e.getMessage(), e);

		String code = CommonAppError.VALIDATION_EXCEPTION.getCode();
		String message = e.getMessage();
		String httpStatusCode = String.valueOf(HttpStatus.BAD_REQUEST.value());
		int httpStatus = getBadRequestHttpStatusCode(httpStatusCode);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(BindException.class)
	protected ResponseEntity<Object> handleBindException(BindException e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.warn("", e);

		String code = CommonAppError.BINDING_ERROR.getCode();
		String message = getBindingErrorMessage(e.getBindingResult());
		String httpStatusCode = String.valueOf(HttpStatus.BAD_REQUEST.value());
		int httpStatus = getBadRequestHttpStatusCode(httpStatusCode);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(e, errorCode);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.warn("", e);

		String code = CommonAppError.BINDING_ERROR.getCode();
		String message = getBindingErrorMessage(e.getBindingResult());
		String httpStatusCode = String.valueOf(HttpStatus.BAD_REQUEST.value());
		int httpStatus = getBadRequestHttpStatusCode(httpStatusCode);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(e, errorCode);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<Object> handleNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.warn("", e);

		String code = String.valueOf(HttpStatus.METHOD_NOT_ALLOWED.value());
		String message = e.getMessage();
		int httpStatus = getBadRequestHttpStatusCode(code);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.warn("", e);

		String code = String.valueOf(HttpStatus.NOT_FOUND.value());
		String message = e.getMessage();
		int httpStatus = getBadRequestHttpStatusCode(code);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	protected ResponseEntity<Object> handleMaxSizeException(MaxUploadSizeExceededException e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.warn("", e);

		String code = CommonAppError.INVALID_FILE.getCode();
		String message = e.getMessage();
		String httpStatusCode = String.valueOf(HttpStatus.PAYLOAD_TOO_LARGE.value());
		int httpStatus = getBadRequestHttpStatusCode(httpStatusCode);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(AuthenticationException.class)
	protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.warn("AuthenticationException: {}", e.getMessage());
		log.warn("", e);

		String code = String.valueOf(HttpStatus.UNAUTHORIZED.value());
		String message = e.getMessage();
		int httpStatus = getBadRequestHttpStatusCode(code);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(JwtException.class)
	protected ResponseEntity<Object> handleJwtException(JwtException e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.warn("JwtException: {}", e.getMessage());
		log.warn("", e);

		String code = String.valueOf(HttpStatus.UNAUTHORIZED.value());
		String message = e.getMessage();
		int httpStatus = getBadRequestHttpStatusCode(code);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(AccessDeniedException.class)
	protected ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.warn("AccessDeniedException: {}", e.getMessage());
		log.warn("", e);

		String code = String.valueOf(HttpStatus.FORBIDDEN.value());
		String message = e.getMessage();
		int httpStatus = getBadRequestHttpStatusCode(code);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(ExpiredJwtException.class)
	protected ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException e, HttpServletRequest request) {
		RequestUtils.setAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL, this.attributeError);
		log.warn("ExpiredJwtException: {}", e.getMessage());
		log.warn("", e);

		String code = String.valueOf(HttpStatus.FORBIDDEN.value());
		String message = e.getMessage();
		int httpStatus = getBadRequestHttpStatusCode(code);

		ErrorCode errorCode = ErrorCode.builder()
				.code(code)
				.message(message)
				.httpStatus(httpStatus)
				.build();

		return handleExceptionInternal(errorCode);
	}

	private String getBindingErrorMessage(BindingResult bindingResult) {
		if (bindingResult.hasFieldErrors()) {
			List<FieldError> errors = bindingResult.getFieldErrors();
			if (CollectionUtils.isNotEmpty(errors)) {
				FieldError error = errors.get(0);
				return error.getDefaultMessage();
			} else {
				return MessageResolver.getMessage(CommonAppError.BINDING_ERROR);
			}
		}
		else {
			return MessageResolver.getMessage(CommonAppError.BINDING_ERROR);
		}
	}

	private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
		ResponseEntity<Object> body = ResponseEntity.status(errorCode.getHttpStatus())
				.body(makeErrorResponse(errorCode));
		return body;
	}

	private Response<Object> makeErrorResponse(ErrorCode errorCode) {
		return Response.builder()
				.code(errorCode.getCode())
				.message(errorCode.getMessage())
				.error(true)
				.build();
	}

	private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getHttpStatus())
				.body(makeErrorResponse(e, errorCode));
	}

	private Response<Object> makeErrorResponse(BindException e, ErrorCode errorCode) {
		List<ValidationError> validationErrorList = e.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(ValidationError::of)
				.toList();

		return Response.builder()
				.code(errorCode.getCode())
				.message(errorCode.getMessage())
				.error(true)
				.errors(validationErrorList)
				.build();
	}

	private int getBadRequestStartPrefix() {
		Integer badRequestStartPrefix = null;
		try {
			badRequestStartPrefix = ConfigProperties.getInstance().getIntValue("http-status-bad-request-start-prefix");
		} catch (Exception ex) {
			badRequestStartPrefix = null;
		}
		if (badRequestStartPrefix == null) {
			badRequestStartPrefix = 400;
		}

		return badRequestStartPrefix;
	}

	private int getBadRequestHttpStatusCode(String httpStatusCode) {
		int badRequestStartPrefix = getBadRequestStartPrefix();
		String lastCode = httpStatusCode.substring(httpStatusCode.length() - 2);
		int httpStatus = badRequestStartPrefix + Integer.valueOf(lastCode);
		return httpStatus;
	}

}

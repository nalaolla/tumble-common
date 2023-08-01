package kr.co.tumble.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.tumble.common.messageconverter.CustomObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * HttpInterfaceResponse Exception 모듈
 */
@Slf4j
@Getter
public class HttpInterfaceResponseException extends CommonException {

	private static final long serialVersionUID = 6044478616579898320L;

	private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

	private final String errorCode;
	private final String errorMessage;
	private final HttpStatus httpStatus;

	public static HttpInterfaceResponseException exception(AppError appError) {
		throw new HttpInterfaceResponseException(appError.getCode(), MessageResolver.getMessage(appError));
	}

	public static HttpInterfaceResponseException exception(AppError appError, Object[] args) {
		throw new HttpInterfaceResponseException(appError.getCode(), MessageResolver.getMessage(appError, args));
	}

	public HttpInterfaceResponseException(String errorCode, String errorMessage) {
		super(errorMessage);

		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		String code = Optional.ofNullable(errorCode).orElse("500");
		httpStatus = HttpStatus.resolve(Integer.valueOf(String.valueOf(code)));
	}

	public <T> T getBody(TypeReference<T> toValueTypeRef) throws JsonProcessingException {
		T body = null;

		try {
			boolean isWrapperType = isWrapperTypeCheck(toValueTypeRef.getType());
			if (isWrapperType) {
				body = getWrapperTypeValue(errorMessage, toValueTypeRef.getType());
			} else {
				// WrapperType이 아닌 경우에만 json 변환함.
				ObjectMapper objectMapper = CustomObjectMapper.get();
				body = objectMapper.readValue(errorMessage, toValueTypeRef);
			}
		} catch (Exception ex) {
			body = null;
		}

		return body;
	}

	public <T> ResponseEntity<T> getResponseEntity(TypeReference<T> toValueTypeRef) throws JsonProcessingException {
		T body = getBody(toValueTypeRef);
		return new ResponseEntity<>(body, httpStatus);
	}

	private static boolean isWrapperTypeCheck(Type type) {
		return WRAPPER_TYPES.contains(type);
	}

	private static Set<Class<?>> getWrapperTypes() {
		Set<Class<?>> ret = new HashSet<>();
		ret.add(Boolean.class);
		ret.add(Character.class);
		ret.add(Byte.class);
		ret.add(Short.class);
		ret.add(Integer.class);
		ret.add(Long.class);
		ret.add(Float.class);
		ret.add(Double.class);
		ret.add(Void.class);
		ret.add(String.class);
		return ret;
	}

	private <T> T getWrapperTypeValue(String errorMessage, Type type) {
		T data = null;

		try {
			if (type == Boolean.class) {
				Boolean value = Boolean.valueOf(errorMessage);
				data = (T) value;
			} else if (type == Character.class) {
				data = (T) errorMessage;
			} else if (type == Byte.class) {
				Byte value = Byte.valueOf(errorMessage);
				data = (T) value;
			} else if (type == Short.class) {
				Short value = Short.valueOf(errorMessage);
				data = (T) value;
			} else if (type == Integer.class) {
				Integer value = Integer.valueOf(errorMessage);
				data = (T) value;
			} else if (type == Long.class) {
				Long value = Long.valueOf(errorMessage);
				data = (T) value;
			} else if (type == Float.class) {
				Float value = Float.valueOf(errorMessage);
				data = (T) value;
			} else if (type == Double.class) {
				Double value = Double.valueOf(errorMessage);
				data = (T) value;
			} else if (type == String.class) {
				String value = String.valueOf(errorMessage);
				data = (T) value;
			}
		} catch (Exception ex) {
			log.debug("HttpInterfaceResponseException getWrapperTypeValue : {}", ex);
			data = null;
		}

		return data;
	}

}

package kr.co.tumble.common.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;

import java.util.Objects;

public class RestResponse<T> {
	private final ResponseEntity<T> responseEntity;
	private final Exception exception;
	private final RestClientResponseException responseException;

	public RestResponse(ResponseEntity<T> responseEntity) {
		this(responseEntity, null);
	}

	public RestResponse(Exception exception) {
		this(null, exception);
	}

	public RestResponse(RestClientResponseException exception) {
		this(null, exception);
	}

	public RestResponse(ResponseEntity<T> responseEntity, Exception exception) {
		this.responseEntity = responseEntity;
		this.exception = exception;
		this.responseException = null;
	}

	public RestResponse(ResponseEntity<T> responseEntity, RestClientResponseException exception) {
		this.responseEntity = responseEntity;
		this.exception = null;
		this.responseException = exception;
	}

	public MultiValueMap<String, String> getHeaders() {
		MultiValueMap<String, String> result = null;

		if (hasResponseError() && responseException.getResponseHeaders() != null) {
			result = responseException.getResponseHeaders();
		} else if (!responseEntity.getHeaders().isEmpty()) {
			result = responseEntity.getHeaders();
		}

		return result;
	}

	public int getStatusCode() {
		if (Objects.nonNull(this.responseEntity)) {
			return this.responseEntity.getStatusCode().value();
		} else if (hasResponseError()) {
			return this.responseException.getStatusCode().value();
		}
		return -1;
	}

	public String getStatusText() {
		if (Objects.nonNull(this.responseEntity)) {
			return this.responseEntity.getStatusCode().toString();
		} else if (hasResponseError()) {
			return this.responseException.getStatusText();
		}
		return null;
	}

	public T getBody() {
		return Objects.nonNull(this.responseEntity) ? this.responseEntity.getBody() : null;
	}

	public String getBodyAsErrorString() {
		return hasResponseError() ? responseException.getResponseBodyAsString() : null;
	}

	public boolean hasError() {
		return hasUnkownError() || hasResponseError();
	}

	public boolean hasUnkownError() {
		return Objects.nonNull(exception);
	}

	public boolean hasResponseError() {
		return Objects.nonNull(responseException);
	}

	public Exception getException() {
		return Objects.nonNull(this.exception) ? this.exception : this.responseException;
	}

}

package kr.co.tumble.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;
import java.util.Objects;

/**
 * RequestUtils Class
 */
public abstract class RequestUtils {

	private RequestUtils() {}

	private static final String REQUEST_ATTRIBUTES_ERROR_MSG = "Could not find current request via RequestAttributes";
	
	public static String requestQueryString() {
		HttpServletRequest request = request();
		return Objects.nonNull(request) ? request.getQueryString() : null;
	}
	
	public static HttpHeaders requestHeaders() {
		HttpServletRequest request = request();
		HttpHeaders headers = new HttpHeaders();
		if (request != null) {
			Enumeration<String> names = request.getHeaderNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				if (StringUtils.hasText(name)) {
					Enumeration<String> values = request.getHeaders(name);
					while (values.hasMoreElements()) {
						String value = values.nextElement();
						headers.add(name, value);
					}
				}
			}
		}
		return HttpHeaders.readOnlyHttpHeaders(headers);
	}
	
	public static HttpServletRequest request() {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
			return servletRequestAttributes.getRequest();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getAttribute(String key) {
        if (RequestContextHolder.getRequestAttributes() != null) {
			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			Assert.notNull(requestAttributes, REQUEST_ATTRIBUTES_ERROR_MSG);
			return (T) requestAttributes.getAttribute(key, RequestAttributes.SCOPE_REQUEST);
		} else {
			return null;
		}
    }

    public static <T> void setAttribute(String key, T attribute) {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		Assert.notNull(requestAttributes, REQUEST_ATTRIBUTES_ERROR_MSG);
		requestAttributes.setAttribute(key, attribute, RequestAttributes.SCOPE_REQUEST);
    }

    public static void removeAttribute(String key) {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		Assert.notNull(requestAttributes, REQUEST_ATTRIBUTES_ERROR_MSG);
		requestAttributes.removeAttribute(key, RequestAttributes.SCOPE_REQUEST);
    }

}
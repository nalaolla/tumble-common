package kr.co.tumble.common.filter;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.tumble.common.util.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

/**
 * RequestLoggingFilter
 */
@Slf4j
public class RequestLoggingFilter extends AbstractRequestLoggingFilter {
	public static final String REQUEST_LOG_LEVEL = "requestLogLevel";
	
	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {
		// do nothing
	}

	@Override
	protected void afterRequest(HttpServletRequest request, String message) {
		String logLevel = RequestUtils.getAttribute(RequestLoggingFilter.REQUEST_LOG_LEVEL);

		if (StringUtils.equals(logLevel, "warn")) {
			log.warn(message);
		} else if (StringUtils.equals(logLevel, "error")) {
			log.error(message);
		} else {
			log.debug(message);
		}
	}

}

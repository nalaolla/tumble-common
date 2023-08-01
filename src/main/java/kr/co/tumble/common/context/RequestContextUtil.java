package kr.co.tumble.common.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * RequestContextUtil
 */
public class RequestContextUtil {

    private static final String BO_VIEW = "-bo";
    private static final String BO_API = "-api-bo";

    private static final String REQUEST_ATTRIBUTES_ERROR_MSG = "Could not find current request via RequestAttributes";

    private RequestContextUtil() {}

    public static Object getAttribute(String name) {
        RequestAttributes attributes = null;
        try {
            attributes = RequestContextHolder.getRequestAttributes();
            Assert.notNull(attributes, REQUEST_ATTRIBUTES_ERROR_MSG);
        } catch (Exception ex) {
            attributes = null;
        }

        if (attributes != null) {
            return attributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
        } else {
            return null;
        }
    }

    public static void setAttribute(String name, Object object) {
        RequestAttributes attributes = null;
        try {
            attributes = RequestContextHolder.getRequestAttributes();
            Assert.notNull(attributes, REQUEST_ATTRIBUTES_ERROR_MSG);
        } catch (Exception ex) {
            attributes = null;
        }

        if (attributes != null) {
            attributes.setAttribute(name, object, RequestAttributes.SCOPE_REQUEST);
        }
    }

    public static void removeAttribute(String name) {
        RequestAttributes attributes = null;
        try {
            attributes = RequestContextHolder.getRequestAttributes();
            Assert.notNull(attributes, REQUEST_ATTRIBUTES_ERROR_MSG);
        } catch (Exception ex) {
            attributes = null;
        }

        if (attributes != null) {
            attributes.removeAttribute(name, RequestAttributes.SCOPE_REQUEST);
        }
    }

    public static HttpServletRequest getHttpServletRequest() {
        HttpServletRequest request = null;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Assert.notNull(attributes, REQUEST_ATTRIBUTES_ERROR_MSG);
            request = attributes.getRequest();
        } catch (Exception e) {
            request = null;
        }

        return request;
    }

    public static HttpServletResponse getHttpServletResponse() {
        HttpServletResponse response = null;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Assert.notNull(attributes, REQUEST_ATTRIBUTES_ERROR_MSG);
            response = attributes.getResponse();
        } catch (Exception e) {
            response = null;
        }

        return response;
    }

    public static boolean isCallServerBo() {
        boolean result = false;

        String callAppName = "";
        try {
            HttpServletRequest request = getHttpServletRequest();
            if (request != null) {
                callAppName = request.getHeader("app_name");
            }
        } catch (Exception e) {
            callAppName = "";
        }
        if (StringUtils.isBlank(callAppName)) {
            callAppName = Optional.ofNullable(ConfigProperties.getInstance().getValue("spring.application.name")).orElse("");
        }

        if (callAppName.contains(BO_VIEW) || callAppName.contains(BO_API)) {
            result = true;
        }

        return result;
    }

    public static String callServerName() {
        String callAppName = "";
        try {
            HttpServletRequest request = getHttpServletRequest();
            if (request != null) {
                callAppName = request.getHeader("Appname");
            }
        } catch (Exception e) {
            callAppName = "";
        }
        if (StringUtils.isBlank(callAppName)) {
            callAppName = Optional.ofNullable(ConfigProperties.getInstance().getValue("spring.application.name")).orElse("");
        }

        return callAppName;
    }

}
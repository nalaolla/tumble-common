package kr.co.tumble.common.context;

import jakarta.servlet.http.HttpSession;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * SessionContextHolder
 */
public class SessionContextHolder {

    private static final String REQUEST_ATTRIBUTES_ERROR_MSG = "Could not find current request via RequestAttributes";

    private SessionContextHolder() {}

    public static Object getAttribute(String name) {
        RequestAttributes attributes = null;
        try {
            attributes = RequestContextHolder.getRequestAttributes();
            Assert.notNull(attributes, REQUEST_ATTRIBUTES_ERROR_MSG);
        } catch (Exception ex) {
            attributes = null;
        }

        if (attributes != null) {
            return attributes.getAttribute(name, RequestAttributes.SCOPE_SESSION);
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
            attributes.setAttribute(name, object, RequestAttributes.SCOPE_SESSION);
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
            attributes.removeAttribute(name, RequestAttributes.SCOPE_SESSION);
        }
    }

    public static HttpSession getSession() {
        HttpSession session = null;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Assert.notNull(attributes, REQUEST_ATTRIBUTES_ERROR_MSG);
            session = attributes.getRequest().getSession();
        } catch (Exception e) {
            session = null;
        }

        return session;
    }

    public static String getSessionId() {
        String sessionId = "";
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            Assert.notNull(attributes, REQUEST_ATTRIBUTES_ERROR_MSG);
            sessionId = attributes.getSessionId();
        } catch (Exception e) {
            sessionId = "";
        }

        return sessionId;
    }

}
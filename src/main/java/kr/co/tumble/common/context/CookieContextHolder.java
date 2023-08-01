package kr.co.tumble.common.context;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.tumble.common.util.CookieMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Optional;

/**
 * CookieContextHolder
 */
@Slf4j
public class CookieContextHolder {

    private static final String DOMAIN_CHECK_EXPRESSION = "[a-zA-Z]+\\.[a-zA-Z0-9]+\\.[a-zA-Z]+\\.*[a-zA-Z]*";
    private static final int SECONDS_OF_DAY = 60 * 60 * 24;
    private static final int SECONDS_OF_MINUTE = 60;
    private static final String DEFAULT_PATH = "/";

    private static final String COOKIE_ERROR_CHECK_KEY = "_cookie_error_checked";

    private static final String REFERER = "Referer";

    private static final String REQUEST_ATTRIBUTES_ERROR_MSG = "Could not find current request via RequestAttributes";

    private CookieContextHolder () {}

    public static String getCookieValue(String name) {
        String cookieValue = null;

        HttpServletRequest request = null;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Assert.notNull(attributes, REQUEST_ATTRIBUTES_ERROR_MSG);
            request = attributes.getRequest();
        } catch (Exception e) {
            request = null;
        }

        if (request != null) {
            Cookie[] cookies = request.getCookies();

            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if (name.equals(cookie.getName())) {
                        cookieValue = cookie.getValue();
                        break;
                    }
                }
            }
        }

        return cookieValue;
    }

    public static boolean isExistsCookieValue(String name) {
        boolean isCookieValue = false;

        HttpServletRequest request = null;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Assert.notNull(attributes, REQUEST_ATTRIBUTES_ERROR_MSG);
            request = attributes.getRequest();
        } catch (Exception e) {
            request = null;
        }

        if (request != null) {
            Cookie[] cookies = request.getCookies();

            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if (name.equals(cookie.getName())) {
                        isCookieValue = true;
                        break;
                    }
                }
            }
        }

        return isCookieValue;
    }

    public static String getDomain() {
        String domain = "";

        HttpServletRequest request = null;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Assert.notNull(attributes, REQUEST_ATTRIBUTES_ERROR_MSG);
            request = attributes.getRequest();
        } catch (Exception e) {
            request = null;
        }

        if (request != null) {
            String referer = StringUtils.trimToNull(request.getHeader(REFERER));
            if (StringUtils.isBlank(referer)) {
                domain = request.getServerName();
            } else {
                try {
                    domain = new URL(referer).getHost();
                } catch (Exception e) {
                    domain = referer;
                }
            }
        }

        return domain;
    }

    public static void addCookie(String name, String value) {
        String domain = getDomain();

        addCookie(name, value, domain, DEFAULT_PATH, null, true, false);
    }

    public static void addCookieDay(String name, String value, int expireDay) {
        addCookie(name, value, null, DEFAULT_PATH, expireDay, true, false);
    }

    public static void addCookieDay(String name, String value, String domain, int expireDay) {
        addCookie(name, value, domain, DEFAULT_PATH, expireDay * SECONDS_OF_DAY, true, false);
    }

    public static void addCookieMinute(String name, String value, int expireMin) {
        addCookie(name, value, null, DEFAULT_PATH, expireMin, true, false);
    }

    public static void addCookieMinute(String name, String value, String domain, int expireMin) {
        addCookie(name, value, domain, DEFAULT_PATH, expireMin * SECONDS_OF_MINUTE, true, false);
    }

    public static void addCookie(String name, String value, String domain, String path, Integer seconds, boolean isSecure, boolean isHttpOnly) {
        HttpServletResponse response = null;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Assert.notNull(attributes, REQUEST_ATTRIBUTES_ERROR_MSG);
            response = attributes.getResponse();
        } catch (Exception e) {
            response = null;
        }

        if (response != null) {
            try {
                Cookie cookie = new Cookie(name, URLEncoder.encode(value, "UTF-8"));

                if (seconds != null) {
                    cookie.setMaxAge(seconds);
                }

                if (StringUtils.isNotBlank(domain)) {
                    cookie.setDomain(domain);
                }

                if (StringUtils.isNotBlank(path)) {
                    cookie.setPath(path);
                }

                cookie.setSecure(isSecure);
                cookie.setHttpOnly(isHttpOnly);

                response.addCookie(cookie);
            } catch (Exception e) {
                log.debug("addCookie {}", e);
            }

        }
    }

    public static void deleteCookie(String name) {
        String domain = getDomain();

        addCookie(name, "", domain, DEFAULT_PATH, 0, true, false);
    }

    public static void deleteCookie(String name, String domain) {
        addCookie(name, "", domain, DEFAULT_PATH, 0, true, false);
    }

    public static CookieMap getCookieMap() {
        CookieMap cookieMap = new CookieMap();

        HttpServletRequest request = null;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Assert.notNull(attributes, REQUEST_ATTRIBUTES_ERROR_MSG);
            request = attributes.getRequest();
        } catch (Exception e) {
            request = null;
        }

        if (request != null) {
            Cookie[] cookies = request.getCookies();

            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    cookieMap.put(cookie.getName(), cookie.getValue());
                }
            }

            String reqCookie = request.getHeader("Cookie");

            // 잘못된 몰번호 디버깅용 로그
            if (request.getAttribute(COOKIE_ERROR_CHECK_KEY) == null) {
                String mallNo = cookieMap.get("mall_no");

                if (StringUtils.isNotEmpty(mallNo)) {
                    try {
                        Integer.parseInt(mallNo);
                    } catch (NumberFormatException e) {
                        log.warn("[GET] Invalid MallNO " + mallNo + " from cookie header " + reqCookie + " with referer " + request.getHeader("referer"), e);
                    }
                }
                request.setAttribute(COOKIE_ERROR_CHECK_KEY, "checked");
            }
        }

        return cookieMap;
    }


    public static String getCookieDomain(String domain) {
        String cookieDomain = null;
        if (domain.matches(DOMAIN_CHECK_EXPRESSION)) {
            cookieDomain = domain.substring(domain.indexOf('.') + 1);
        }
        return cookieDomain;
    }

    public static String getSiteNo() {
        String siteNo = CookieContextHolder.getCookieMap().getMap().getOrDefault("site_no", "");

        if (StringUtils.isBlank(siteNo)) {
            siteNo = (String) Optional.ofNullable(RequestContextUtil.getAttribute("site_no")).orElse("");
        }

        if (StringUtils.isBlank(siteNo)) {
            siteNo = "1";
        }

        return siteNo;
    }

    public static String getMallNo() {
        String mallNo = CookieContextHolder.getCookieMap().getMap().getOrDefault("mall_no", "10001");
        return mallNo;
    }

    public static String getLangCd() {
        String langCd = CookieContextHolder.getCookieMap().getMap().getOrDefault("lang_cd", "");

        if (StringUtils.isBlank(langCd)) {
            langCd = ConfigProperties.getInstance().getValue("app.lang.defaultSystemLanguage");

            if (StringUtils.isBlank(langCd)) {
                langCd = "ko";
            }
        }

        return langCd;
    }
    
    public static String getDataLangCd() {
        String dataLangCd = CookieContextHolder.getCookieMap().getMap().getOrDefault("lang_cd", getLangCd());
        return dataLangCd;
    }

    public static String getChlNo() {
        String chlNo = CookieContextHolder.getCookieMap().getMap().getOrDefault("chl_no", "");
        if (StringUtils.isBlank(chlNo)) {
            chlNo = ConfigProperties.getInstance().getValue("chl_no");

            if (StringUtils.isBlank(chlNo)) {
                chlNo = "1000001";
            }
        }
        return chlNo;
    }

    public static String getSessNo() {
        String sessNo = CookieContextHolder.getCookieMap().getMap().getOrDefault("sessNo", "");
        return sessNo;
    }

}
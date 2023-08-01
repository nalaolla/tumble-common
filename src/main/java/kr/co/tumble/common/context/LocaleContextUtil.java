package kr.co.tumble.common.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Locale;

/**
 * LocaleContextUtil
 */
public class LocaleContextUtil {

    private static final String DEFAULT_LANGUAGE_KEY = "app.lang.defaultSystemLanguage";

    private LocaleContextUtil() {}

    public static void setLocale(String localeCode) {
        if (StringUtils.isBlank(localeCode)) {
            localeCode = ConfigProperties.getInstance().getValue(DEFAULT_LANGUAGE_KEY);

            if (StringUtils.isBlank(localeCode)) {
                localeCode = "ko";
            }
        }

        Locale locale = new Locale(localeCode);
        Locale currentLocale = getLocale();

        if (!locale.equals(currentLocale)) {
            LocaleContextHolder.setLocale(locale);

            HttpServletRequest request = RequestContextUtil.getHttpServletRequest();
            if (request != null) {
                LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
                if (localeResolver != null) {
                    HttpServletResponse response = RequestContextUtil.getHttpServletResponse();
                    localeResolver.setLocale(request, response, locale);
                }
            }
        }
    }

    public static Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

}
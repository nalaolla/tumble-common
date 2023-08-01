package kr.co.tumble.common.exception;

import kr.co.tumble.common.context.RequestContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * MessageResolver
 */
@Component
public class MessageResolver {

    private static MessageSource messageSource;

    @Autowired
    public void init(MessageSource messageSource) {
        setMessageSource(messageSource);
    }

    private static void setMessageSource(MessageSource initMessageSource) {
        messageSource = initMessageSource;
    }

    /**
     * FallMessage
     */
    private static String getFallMessage(String messageKey, Locale locale) {
        String message = "";
        try {
            message = "There is no " + locale + " locale code value of " + messageKey;
        } catch (Exception e) {
            message = "There is no locale code value of " + messageKey;
        }

        return message;
    }

    /**
     * defaultMessage가 없는 경우
     */
    private static String getLocaleMessage(AppError appError, Object[] args, Locale locale) {
        
        String message = "";

        if (RequestContextUtil.isCallServerBo()) {
            // 호출한 서버가 BO인 경우
            message = getMessageKeyToMessageValue(appError.getBoMessageKey(), args, locale, false);

            if (StringUtils.isBlank(message)) {
                // BO 메시지 코드값이 없을 경우 기본 메시지 코드값 가져옴.
                message = getMessageKeyToMessageValue(appError.getMessageKey(), args, locale, true);
            }
        } else {
            // 호출한 서버가 FO인 경우
            message = getMessageKeyToMessageValue(appError.getMessageKey(), args, locale, true);
        }

        return message;
    }

    /**
     * defaultMessage가 있는 경우
     */
    private static String getLocaleMessage(AppError appError, Object[] args, String defaultMessage, Locale locale) {

        String message = "";

        if (RequestContextUtil.isCallServerBo() && StringUtils.isNotBlank(appError.getBoMessageKey())) {
            // 호출한 서버가 BO인 경우
            message = getMessageKeyToMessageValue(appError.getBoMessageKey(), args, locale, false);

            if (StringUtils.isBlank(message)) {
                // BO 메시지 코드값이 없을 경우 기본 메시지 코드값 가져옴.
                message = getMessageKeyToMessageValue(appError.getMessageKey(), args, defaultMessage, locale);
            }
        } else {
            // 호출한 서버가 FO인 경우
            message = getMessageKeyToMessageValue(appError.getMessageKey(), args, defaultMessage, locale);
        }

        return message;
    }

    /**
     * defaultMessage가 없는 경우
     * args값이 2개인 경우
     */
    private static String getLocaleMessage(AppError appError, Object[] args1, Object[] args2, Locale locale) {
        
        String message = "";

        if (RequestContextUtil.isCallServerBo() && StringUtils.isNotBlank(appError.getBoMessageKey())) {
            // 호출한 서버가 BO인 경우
            message = getMessageKeyToMessageValue(appError.getBoMessageKey(), args2, locale, false);

            if (StringUtils.isBlank(message)) {
                // BO 메시지 코드값이 없을 경우 기본 메시지 코드값 가져옴.
                message = getMessageKeyToMessageValue(appError.getMessageKey(), args1, locale, true);
            }
        } else {
            // 호출한 서버가 FO인 경우
            message = getMessageKeyToMessageValue(appError.getMessageKey(), args1, locale, true);
        }

        return message;
    }

    /**
     * defaultMessage가 있는 경우
     * args값이 2개인 경우
     */
    private static String getLocaleMessage(AppError appError, Object[] args1, Object[] args2, String defaultMessage, Locale locale) {

        String message = "";

        if (RequestContextUtil.isCallServerBo() && StringUtils.isNotBlank(appError.getBoMessageKey())) {
            // 호출한 서버가 BO인 경우
            message = getMessageKeyToMessageValue(appError.getBoMessageKey(), args2, locale, false);

            if (StringUtils.isBlank(message)) {
                // BO 메시지 코드값이 없을 경우 기본 메시지 코드값 가져옴.
                message = getMessageKeyToMessageValue(appError.getMessageKey(), args1, defaultMessage, locale);
            }
        } else {
            // 호출한 서버가 FO인 경우
            message = getMessageKeyToMessageValue(appError.getMessageKey(), args1, defaultMessage, locale);
        }

        return message;
    }

    private static String getMessageKeyToMessageValue(String messageKey, Object[] args, Locale locale, boolean isFallMessage) {
        String message = "";

        try {
            message = messageSource.getMessage(messageKey, args, locale);
        } catch (Exception e) {
            message = "";

            if (isFallMessage) {
                message = getFallMessage(messageKey, locale);
            }
        }

        return message;
    }
    private static String getMessageKeyToMessageValue(String messageKey, Object[] args, String defaultMessage, Locale locale) {
        String message = "";

        try {
            message = messageSource.getMessage(messageKey, args, defaultMessage, locale);
        } catch (Exception e) {
            message = defaultMessage;
        }

        return message;
    }

    public static String getMessage(String messageKey) {
        return getMessageKeyToMessageValue(messageKey, new String[] {}, LocaleContextHolder.getLocale(), true);
    }

    public static String getMessage(String messageKey, Object[] args) {
        if (args == null || args.length == 0) {
            return getMessage(messageKey);
        }
        return getMessageKeyToMessageValue(messageKey, args, LocaleContextHolder.getLocale(), true);
    }

    public static String getMessage(String messageKey, Locale locale) {
        return getMessageKeyToMessageValue(messageKey, new String[] {}, locale, true);
    }

    public static String getMessage(String messageKey, Object[] args, Locale locale) {
        if (args == null || args.length == 0) {
            return getMessage(messageKey, locale);
        }
        return getMessageKeyToMessageValue(messageKey, args, LocaleContextHolder.getLocale(), true);
    }

    public static String getMessage(AppError appError, Locale locale) {
        return getLocaleMessage(appError, new String[] {}, locale);
    }

    public static String getMessage(AppError appError, Object[] args, Locale locale) {
        if (args == null || args.length == 0) {
            return getMessage(appError, locale);
        }
        return getLocaleMessage(appError, args, locale);
    }

    public static String getMessage(AppError appError, Object[] args, String defaultMessage) {
        return getMessage(appError, args, defaultMessage, LocaleContextHolder.getLocale());
    }

    public static String getMessage(AppError appError, Object[] args, String defaultMessage, Locale locale) {
        return getLocaleMessage(appError, args, defaultMessage, locale);
    }

    public static String getMessage(AppError appError) {
        return getLocaleMessage(appError, new String[] {}, LocaleContextHolder.getLocale());
    }

    public static String getMessage(AppError appError, Object[] args) {
        if (args == null || args.length == 0) {
            return getLocaleMessage(appError, new String[] {}, LocaleContextHolder.getLocale());
        }
        return getLocaleMessage(appError, args, LocaleContextHolder.getLocale());
    }

    public static String getMessage(AppError appError, Object[] args1, Object[] args2) {
        if (args1 == null || args1.length == 0) {
            return getLocaleMessage(appError, new String[] {}, args2, LocaleContextHolder.getLocale());
        } else if (args2 == null || args2.length == 0) {
            return getLocaleMessage(appError, args1, new String[] {}, LocaleContextHolder.getLocale());
        } else {
            return getLocaleMessage(appError, args1 , args2, LocaleContextHolder.getLocale());
        }
    }

    public static String getMessage(AppError appError, Object[] args1, Object[] args2, String defaultMessage) {
        if (args1 == null || args1.length == 0) {
            return getLocaleMessage(appError, new String[] {}, args2, defaultMessage, LocaleContextHolder.getLocale());
        } else if (args2 == null || args2.length == 0) {
            return getLocaleMessage(appError, args1,new String[] {}, defaultMessage, LocaleContextHolder.getLocale());
        } else {
            return getLocaleMessage(appError, args1 , args2, defaultMessage, LocaleContextHolder.getLocale());
        }
    }

    public static String getMessage(AppError appError, Object[] args1, Object[] args2, String defaultMessage, Locale locale) {
        if (args1 == null || args1.length == 0) {
            return getLocaleMessage(appError, new String[] {}, args2, defaultMessage, locale);
        } else if (args2 == null || args2.length == 0) {
            return getLocaleMessage(appError, args1,new String[] {}, defaultMessage, locale);
        } else {
            return getLocaleMessage(appError, args1 , args2, defaultMessage, locale);
        }
    }

}
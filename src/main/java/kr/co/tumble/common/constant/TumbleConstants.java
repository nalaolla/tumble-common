package kr.co.tumble.common.constant;

import java.text.SimpleDateFormat;

public class TumbleConstants {

    private TumbleConstants() {}

    public static final String SPRING_APP_NAME_KEY = "spring.application.name";

    public static final String DEFAULT_LANGUAGE_KEY = "app.lang.defaultSystemLanguage";

    public static final String HEADER_RESTAPI_CALL_APP_NAME_KEY = "X2bee-Restapi-Call-Appname";
    public static final String HEADER_AUTH_PREFIX_KEY = "Bearer ";
    public static final String HEADER_AUTH_KEY = "Authorization";
    public static final String HEADER_COOKIE_KEY = "Cookie";

    public static final String COOKIE_SITE_NO_KEY = "site_no";  // 공통 - 사이트번호
    public static final String COOKIE_LANG_CD_KEY = "lang_cd"; // 공통 - 언어코드
    public static final String COOKIE_DATA_LANG_CD_KEY = "data_lang_cd"; // 공통 - 데이터언어코드
    public static final String COOKIE_MALL_NO_KEY = "mall_no"; // 공통 - 몰번호
    public static final String COOKIE_CHL_NO_KEY = "chl_no"; // 공통 - 채널번호

    public static final String COOKIE_SESS_NO_KEY = "sessNo"; // 특정 API

    public static final String DEFAULT_SITE_NO_VALUE = "1";
    public static final String DEFAULT_LANG_CD_VALUE = "ko";
    public static final String DEFAULT_MALL_NO_VALUE = "10001";
    public static final String DEFAULT_CHL_NO_VALUE = "1000001";
    
    public static final String DEFAULT_SYSTEM_ID_VALUE = "SYSTEM";

    public static final String X2_CORE_LOGGER_CATEGORY = "x2.spring.core";

    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String CHARSET_ISO_8859_1 = "ISO-8859-1";

    public static final String Y = "Y";
    public static final String N = "N";

    public static final String COMMA = ",";
    public static final String PERIOD = ".";
    public static final String EMPTY = "";
    public static final String ASTERISK = "*";
    public static final String SLASH = "/";
    public static final String DASH = "-";
    public static final String DLOUBLE_BACK_SLASH = "\\";
    public static final String AND = "&";
    public static final String EQUAL = "=";
    public static final String EMPTY_STRING = "EMPTY";

    public static final String HTTPS = "https";
    public static final String HTTP = "http";

    public static final String NEWLINE = "\n";

    //for redirect.jsp
    public static final String JSP_FOR_REDIRECT = "common/redirect";
    public static final String REDIRECT_URL = "redirectUrl";
    public static final String SHOW_MESSAGE = "showMessage";

    //for DateFormat
    public static final String YYYYMM = "yyyyMM";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMMDD_WITH_KOREAN = "yyyy년 MM월 dd일";
    public static final String YYYYMMDD_WITH_DELIM = "yyyy/MM/dd";
    public static final String YYYYMMDD_WITH_DASH_DELIM = "yyyy-MM-dd";
    public static final String YYYYMMDDHHMISS = "yyyyMMddHHmmss";
    public static final String YYYYMMDDHHMISS_WITH_DELIM = "yyyy/MM/dd HH:mm:ss";
    public static final String YYYYMMDDHHMISS_WITH_DASH_DELIM = "yyyy-MM-dd HH:mm:ss";

    // Paging 상수
    public static final int DEFAULT_BLOCK_COUNT = 10;
    public static final int BLOCK_COUNT_5 = 5;
    public static final int DEFAULT_PAGE_INDEX = 1;
    public static final int ROWS_PER_PAGE_20 = 20;

    public static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_YYYYMMDD_WITH_DELIM = ThreadLocal.withInitial(() -> new SimpleDateFormat(YYYYMMDD_WITH_DELIM));
    public static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_YYYYMMDD_WITH_DASH_DELIM = ThreadLocal.withInitial(() -> new SimpleDateFormat(YYYYMMDD_WITH_DASH_DELIM));
    public static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_YYYYMMDDHHMISS_WITH_DELIM = ThreadLocal.withInitial(() -> new SimpleDateFormat(YYYYMMDDHHMISS_WITH_DELIM));
    public static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_YYYYMMDD = ThreadLocal.withInitial(() -> new SimpleDateFormat(YYYYMMDD));
    public static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_YYYYMMDDHHMISS = ThreadLocal.withInitial(() -> new SimpleDateFormat(YYYYMMDDHHMISS));
    public static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_YYYYMMDDHHMISS_WITH_DASH_DELIM = ThreadLocal.withInitial(() -> new SimpleDateFormat(YYYYMMDDHHMISS_WITH_DASH_DELIM));

}
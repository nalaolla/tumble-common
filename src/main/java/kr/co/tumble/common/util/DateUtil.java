package kr.co.tumble.common.util;


import jakarta.xml.bind.DatatypeConverter;
import kr.co.tumble.common.constant.TumbleConstants;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * DateUtil Class
 */
@Slf4j
public class DateUtil {

	private DateUtil() {}

	/**
	 * <pre>
	 * 1. 개요 : 오늘 날짜 받아오기
	 * 2. 처리내용 :
	 * </pre>
	 * @Method Name : getToday
	 * @return
	 */
	public static String getToday(){
		return getToday(TumbleConstants.YYYYMMDD_WITH_DASH_DELIM);
	}

	/**
	 * yyyy-MM-dd HH:mm:ss 형식의 오늘 날짜 조회
	 * @author wonnew@plateer.com
	 * @return
	 */
	public static String getTodayDateTime() {
		return getToday(TumbleConstants.YYYYMMDDHHMISS_WITH_DASH_DELIM);
	}

	/**
	 * 넘어온 포멧 형태로 현재 날짜 출력
	 * @param format - 출력받을 날짜 포멧
	 * @return String - 현재 날짜
	 */
	public static String getToday(String format) {
		if(format == null){
			throw new IllegalArgumentException();
		}

        return new SimpleDateFormat(format).format(new Date());
	}

	/**
	 * 넘어온 포멧 형태로 현재 날짜 출력
	 * @param format - 출력받을 날짜 포멧
	 * @return String - 현재 날짜
	 */
	public static String getTodayLocale(String format, Locale locale) {
		if(format == null){
			throw new IllegalArgumentException();
		}

        return new SimpleDateFormat(format, locale).format(new Date());
	}

	/**
	 * 원데이터 날짜 포맷을 원하는 날짜 포맷으로 변환한다.
	 * <br> Ex)
	 * <br> orgDateStr : 20221128
	 * <br> orgPattern : yyyyMMdd
	 * <br> destPattern : yyyy년 MM월 dd일
	 *
	 * @author wonnew@plateer.com
	 * @param orgDateStr : 문자열의 원데이터
	 * @param orgPattern : 원데이터의 날짜 포맷
	 * @param destPattern : 변환을 원하는 날짜 포맷
	 * @return
	 * @throws ParseException
	 */
	public static String convFormatDate(String orgDateStr, String orgPattern, String destPattern) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(orgPattern);
		Date orgDate = simpleDateFormat.parse(orgDateStr);

		return new SimpleDateFormat(destPattern).format(orgDate);

	}

	/**
	 * <pre>
	 * 1. 개요 : 오늘부터 넘어온 날짜료 계산된 날짜 리턴
	 * 2. 처리내용 :
	 * </pre>
	 * @Method Name : getBetweenToday
	 * @param term
	 * @return
	 */
	public static String getBetweenToday(int term){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, term);

		return format(cal.getTime(), TumbleConstants.YYYYMMDD_WITH_DASH_DELIM);
	}

	/**
	 * 현재날짜의 월 가져오기
	 * @return
	 */
	public static int getThisMonth(){
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	/**
	 * 현재날짜의 일 가져오기
	 * @return
	 */
	public static int getThisDay(){
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 원하는 날짜형식으로 오늘 날짜를 리턴한다.
	 *
	 * @see SimpleDateFormat
	 * @param pattern
	 *            패턴
	 * @return string 패턴화 된 날짜 문자열
	 */
	public static String today(String pattern) {
		if(pattern == null){
			throw new IllegalArgumentException();
		}

		return format(new Date(), pattern);
	}

	/**
     * 현재 날짜를 기준으로 원하는 시점의 날짜를 구함  Default Date Format "yyyyMMdd"
     * @param field      Calendar Field - 일: Calendar.DATE, 주: Calendar.WEEK_OF_MONTH, 월: Calendar.MONTH,  년: Calendar.YEAR
     * @param amount   원하는 날짜 시점 (10일 후를 원하면 10, 10일 전을 원하면 -10)
     * @return 날짜 String
     */
	public static String addDateFromNow(int field, int amount) {
        return addDateFromNow(field, amount, "yyyyMMdd");
    }

	/**
     * 현재 날짜를 기준으로 원하는 시점의 날짜를 구함
     * @param field      Calendar Field - 일: Calendar.DATE, 주: Calendar.WEEK_OF_MONTH, 월: Calendar.MONTH,  년: Calendar.YEAR
     * @param amount     원하는 날짜 시점 (10일 후를 원하면 10, 10일 전을 원하면 -10)
     * @param formatstr  날짜 format
     * @return 날짜 String
     */
	public static String addDateFromNow(int field, int amount, String formatstr) {
        Calendar cal = Calendar.getInstance();

        cal.add(field, amount);
        Date date = cal.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat(formatstr);
        return formatter.format(date);

    }

	/**
     * 특정 날짜를 기준으로 원하는 시점의 날짜를 구함
     * @param field      Calendar Field - 일: Calendar.DATE, 주: Calendar.WEEK_OF_MONTH, 월: Calendar.MONTH,  년: Calendar.YEAR
     * @param amount     원하는 날짜 시점 (10일 후를 원하면 10, 10일 전을 원하면 -10)
     * @param formatstr  날짜 format
	 * @param strDate         변환을 위한 기준 날짜(yyyyMMdd 포맷 기준이어야 함)
     * @return 날짜 String
     */
	public static String addDateFromStr(int field, int amount, String formatstr, String strDate) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("yyyyMMdd").parse(strDate));

        cal.add(field, amount);
        Date date = cal.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat(formatstr);
        return formatter.format(date);

    }

	/**
	 * 주어진 Date를 pattern화 된 문자열로 반환한다.
	 *
	 * @param date
	 *            패턴화할 날짜
	 * @param pattern
	 *            string 패턴
	 * @return string 패턴화된 날짜 문자열
	 */
	public static String format(Date date, String pattern) {
		return new SimpleDateFormat(pattern).format(date.getTime());
	}

	/**
	 * 현재날짜의 년도 가져오기
	 * @return
	 */
	public static int getThisYear(){
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	/**
	 * @param datestr
	 * @return
	 */
	public static Timestamp stamp(String datestr, String pattern){
		return new Timestamp(parse(datestr, pattern).getTime());
	}

	/**
	 * pattern형식으로 포맷된 날짜 문자열을 java.util.Date 형태로 반환한다.
	 *
	 * @param str
	 *            date string you want to check.
	 * @param pattern
	 *            string representation of the date format. For example,
	 *            "yyyy-MM-dd".
	 * @return date java.util.Date
	 */
	public static Date parse(String str, String pattern) {
		if (str == null) {
			throw new IllegalArgumentException("date string to check is null");
		}

		if (pattern == null) {
			throw new IllegalArgumentException("format string to check date is null");
		}

		SimpleDateFormat formatter = new SimpleDateFormat(pattern,
				Locale.KOREA);
		try {
			return formatter.parse(str);
		} catch (ParseException e) {
			throw new IllegalArgumentException(" wrong date:\"" + str
					+ "\" with format \"" + pattern + "\"");
		}

	}

	/**
	 * <pre>
	 * 1. 개요 : 현재월의 첫날
	 * 2. 처리내용 : 현재월의 첫날을 되돌려 준다.
	 * </pre>
	 * @Method Name : getStartOfTheMonth
	 * @return
	 */
	public static String getStartOfTheMonth(){
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return sf.format(cal.getTime());
	}

    /**
     * 현재 시간이 이 시간에 해당하는지의 여부를 반환한다.
     * ex) 201006261800-201006271830
     * @param nonAvailableTimeStr
     * @return 현재 시간이 주어진 시간대에 포함되면 true, 주어진 시간대에 포함되지 않으면 false.
     */
	public static boolean isAvailableTime( String nonAvailableTimeStr ) {
       boolean result = false;
       SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddHHmm" );
       try {
		   Date nonAvailableTime1 = sdf.parse( nonAvailableTimeStr.substring( 0, nonAvailableTimeStr.indexOf( "-" ) ) );
		   Date nonAvailableTime2 = sdf.parse( nonAvailableTimeStr.substring( nonAvailableTimeStr.indexOf( "-" ) + 1, nonAvailableTimeStr.length() ) );
		   Date now = new Date();
		   if ( now.after( nonAvailableTime1 ) && now.before( nonAvailableTime2 ) ) {
			   result = true;
		   }
       } catch (Exception e) {
		   log.trace(e.getMessage(), e);
       }
       return result;
   }

	public static Timestamp parseValue(String value){
        Timestamp timestamp = parseValueWithTimestamp(value, TumbleConstants.SIMPLE_DATE_FORMAT_YYYYMMDDHHMISS_WITH_DASH_DELIM.get());
        timestamp = timestamp == null ? parseValueWithTimestamp(value, TumbleConstants.SIMPLE_DATE_FORMAT_YYYYMMDDHHMISS_WITH_DELIM.get()) : timestamp;
		timestamp = timestamp == null ? parseValueWithTimestamp(value, TumbleConstants.SIMPLE_DATE_FORMAT_YYYYMMDD_WITH_DELIM.get()) : timestamp;
		timestamp = timestamp == null ? parseValueWithTimestamp(value, TumbleConstants.SIMPLE_DATE_FORMAT_YYYYMMDD_WITH_DASH_DELIM.get()) : timestamp;
		timestamp = timestamp == null ? parseValueWithTimestamp(value, TumbleConstants.SIMPLE_DATE_FORMAT_YYYYMMDDHHMISS.get()) : timestamp;
		timestamp = timestamp == null ? parseValueWithTimestamp(value, TumbleConstants.SIMPLE_DATE_FORMAT_YYYYMMDD.get()) : timestamp;
		timestamp = timestamp == null ? parseValueWithISODateFormat(value) : timestamp;

		return timestamp;
	}

	private static Timestamp parseValueWithTimestamp(String value, SimpleDateFormat simpleDateFormat){
		Timestamp timestamp = null;
		try{
			Date parsedDate = simpleDateFormat.parse(value);
			timestamp = new Timestamp(parsedDate.getTime());
		}catch(Exception e){
			log.trace(e.getMessage(),e);
		}

		return timestamp;
	}

	private static Timestamp parseValueWithISODateFormat(String value) {
		Timestamp timestamp = null;
		try {
			timestamp = new Timestamp(DatatypeConverter.parseDateTime(value).getTimeInMillis());
		} catch (Exception e) {
			log.trace(e.getMessage(), e);
		}

		return timestamp;
	}

	/**
	 * fromDate와 toDate의 날짜 차이를 일자 계산 하는 Util
	 * fromDate와 toDate의 날짜 포맷은 동일해야하며, formatString 문자열에 해당 포맷을 주입해줘야 함
	 *
	 * @author wonnew@plateer.com
	 * @param fromDate
	 * @param toDate
	 * @param formatString
	 * @return
	 * @throws Exception
	 */
	public static long calcDiffDay(String fromDate, String toDate, String formatString) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(formatString);

		Date startDate = format.parse(fromDate);
		Date endDate = format.parse(toDate);

		long diff = endDate.getTime() - startDate.getTime();

		return diff / (24 * 60 * 60 * 1000);
	}

	public static Integer compare(String date1, String date2) {
		Integer result = null;
		try {
			Timestamp value = parseValue(date1);
			Timestamp value2 = parseValue(date2);

			if (value != null && value2 != null) {
				result = value.compareTo(value2);
			}
		} catch (Exception e) {
			return null;
		}

		return result;
	}

	/**
	 * 오늘 날짜와 비교한다.
	 *
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static Integer compareWithToday(String date) {
		Integer result = null;
		try {
			Timestamp value = parseValue(date);
			Timestamp value2 = parseValue(getToday());

			if (value != null && value2 != null) {
				result = value.compareTo(value2);
			}
		} catch (Exception e) {
			return null;
		}

		return result;
	}

	/**
	 * 날짜 포맷 유효성 체크
	 * date에는 날짜 타입의 문자열, formatString에는 검증하고자 하는 포맷 입력
	 * ex)
	 * date : "20221021"
	 * formatString : "yyyyMMdd"
	 * @author wonnew@plateer.com
	 * @param date
	 * @param formatString
	 * @return
	 * @throws Exception
	 */
	public static boolean checkDateValid(String date, String formatString) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(formatString);
			format.setLenient(false);
			format.parse(date);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

    public static String getTodayEnFormat(String langCd) {
		if("ko".equals(langCd)) {
			return "yyyy.MM.dd";
		}

		LocalDate localDate = LocalDate.now();
		return localDate.getMonthValue() == 5 ? "dd MMM yyyy" : "dd MMM. yyyy";
    }
    
    public static String getParamDateEnFormat(String dateStr, String langCd) {
		if("ko".equals(langCd)) {
			return "yyyy.MM.dd HH:mm";
		}

		String month = dateStr.substring(6, 8);
		return "05".equals(month) ? "dd MMM yyyy HH:mm" : "dd MMM. yyyy HH:mm";
    }

	/**
	 * 만나이 계산
	 * @param tgtDt
	 * @return
	 * @author wonnew @plateer.com
	 */
	public static boolean compareAge(String tgtDt) {
		boolean result = false;
		try{
			String str = DateUtil.addDateFromStr(Calendar.YEAR, 14, "yyyyMMdd", tgtDt);
			if(compareWithToday(str) <= 0) {
				result = true;
			}

		} catch (ParseException pe) {

		}

		return result;
	}

}
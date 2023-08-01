package kr.co.tumble.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * NumUtil Class
 */
public class NumUtil {

	private NumUtil() {}
	
	/**
	 * 천단위 표기법
	 *
	 * @param dbl
	 * @return 천단위 표기법(0,000,000) 형식의 문자열
	 */
	public static String notation(double dbl) {
		return NumberFormat.getInstance().format(dbl);
	}
	
	/**
	 * 천 단위 콤마 int to string
	 * @param number
	 * @return
	 */
	public static String commaIntToStr(int number) {
		DecimalFormat comma = new DecimalFormat("#,###");
		return comma.format(number);
	}
	
	/**
	 * 천단위 표기법 제거한다.<br>
	 *
	 * @param str
	 *            12,345,678 형식의 문자열
	 * @return 12345678 ParseException발생 시 0 을 리턴한다.
	 */
	public static double normal(String str) {
		double returnValue;

		try {
			returnValue = NumberFormat.getInstance().parse(str).doubleValue();
		} catch (ParseException e) {
			returnValue = 0;
		}

		return returnValue;
	}
	
	public static int toInt(String param) {
		return (int)toDouble(param);
	}
	
	public static long toLong(String param) {
		return (long) toDouble(param);
	}
	
	public static float toFloat(String param) {
		return (float)toDouble(param);
	}
	
	public static double toDouble(String param) {
		
		double value = 0.0;

		try {

            if (param == null || param.isEmpty()) {
				return value;
			}

			if (param.indexOf(',') > -1) {
				value = normal(param);
			} else {
				value = Double.parseDouble(param);
			}

		} catch (NumberFormatException e) {
			value = 0.0;
		}

		return value;
	}
	
	/**
	 * range 범위에 따라 올림 예) ceil(12342,10) == > 12350
	 *
	 * @param dbl
	 * @param range
	 * @return
	 */
	public static double ceil(double dbl, int range) {
		return Math.ceil(dbl / range) * range;
	}
	
	/**
	 * range 범위에 따라 내림 예)floor(12346,10) == > 12340
	 *
	 * @param dbl
	 * @param range
	 * @return
	 */
	public static double floor(double dbl, int range) {
		return Math.floor(dbl / range) * range;
	}
	
	/**
	 * range 범위에 따라 반올림 예)round(12345,10) == > 12350
	 *
	 * @param dbl
	 * @param range
	 * @return
	 */
	public static double round(double dbl, int range) {
		long value = Math.round(dbl / range) * range;
		return Double.valueOf(value);
	}
	
	/**
	 * 자리수 올림 scale이 2이면 52.2329 -> 52.24
	 * @param number
	 * @param scale
	 * @return
	 */
	public static double ceilPoint(double number, int scale) {
		BigDecimal bigDecimal = BigDecimal.valueOf(number);
		bigDecimal = new BigDecimal(bigDecimal.toString()).setScale(scale, RoundingMode.UP);
		return bigDecimal.doubleValue();
	}
	
	/**
	 * 자리수 내림 scale이 2이면 52.2369 -> 52.23
	 *
	 * @param number
	 * @param scale
	 * @return double
	 */
	public static double floorPoint(double number, int scale) {
		BigDecimal bigDecimal = BigDecimal.valueOf(number);
		bigDecimal = new BigDecimal(bigDecimal.toString()).setScale(scale, RoundingMode.DOWN);
		return bigDecimal.doubleValue();
	}
	
	/**
	 * 자리수 반올림 scale이 2이면 52.2369 -> 52.24
	 *
	 * @param number
	 * @param scale
	 * @return double
	 */
	public static double roundPoint(double number, int scale) {
		BigDecimal bigDecimal = BigDecimal.valueOf(number);
		bigDecimal = new BigDecimal(bigDecimal.toString()).setScale(scale, RoundingMode.HALF_EVEN);
		return bigDecimal.doubleValue();
	}
	
	/**
	 * 주어진 패턴에 따라 숫자를 변환한다.
	 *
	 * @see DecimalFormat
	 * @param value
	 *            double
	 * @param pattern
	 *            String
	 * @return String if value is 12 and pattern "000" return "012"
	 */
	public static String format(double value, String pattern) {
		DecimalFormat decimalFormat = new DecimalFormat(pattern);
		return decimalFormat.format(value);
	}
	
	/**
	 * Long to Int
	 * @param l
	 * @return
	 */
	public static int safeLongToInt(long l) {
	    int i = (int)l;
	    if (i != l) {
	        throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
	    }
	    return i;
	}

	public static boolean integerIsNull(Integer num) {
		return num == null;
	}

	public static boolean doubleIsNull(Double num) {
		return num == null;
	}

}
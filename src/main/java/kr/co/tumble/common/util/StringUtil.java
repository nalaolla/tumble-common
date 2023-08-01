package kr.co.tumble.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.tumble.common.messageconverter.CustomObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * StringUtil Class
 */
@Component
@Slf4j
public class StringUtil {

	private static final String REQUEST_ATTRIBUTES_ERROR_MSG = "Could not find current request via RequestAttributes";
	private static final String UNKNOWN = "unknown";

	private StringUtil() {}

	/**
	 * 접속자 아이피 추출
	 * @return String
	 */
	public static String getRemoteIP() {

		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    	Assert.notNull(attributes, REQUEST_ATTRIBUTES_ERROR_MSG);
		HttpServletRequest request = attributes.getRequest();
		String ip = request.getHeader("X-Real-IP");

		if (StringUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}

		//proxy 환경일경우
		if (StringUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		
		//웹로직 서버일 경우
		if (StringUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		
		//HTTP_CLIENT일 경우
		if (StringUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		
		//웹로직 서버일 경우
		if (StringUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		
		if (StringUtil.isBlank(ip)) {
			ip = request.getRemoteAddr();
		}

		return ip;
	}

	/**
	 * 랜덤번호생성
	 * @param len
	 * @return String
	 */
	public static String generateKey(int len) {
		SecureRandom rand = new SecureRandom();  // SecureRandom is preferred to Random
		StringBuilder key = new StringBuilder();

		while (key.length() < len) {
			int iKey = Math.abs(rand.nextInt() % 74) + 48;
			if (iKey >= 48 && iKey <= 57)
				key.append((char) iKey);
		}
		return key.toString();
	}
	
	/**
	 * 랜덤비밀번호생성
	 * @return String
	 */
	public static String randomPwd() {
		SecureRandom rand = new SecureRandom();  // SecureRandom is preferred to Random
		StringBuilder key = new StringBuilder();

		while (key.length() < 6) {
			int iKey = Math.abs(rand.nextInt() % 74) + 48;
			if (iKey >= 48 && iKey <= 57)
				key.append((char) iKey);
		}
		return key.toString();
	}
	
	/**
	 * 랜덤 비밀번호 발생
	 * @return String
	 */
	public static String randomPwdHard() {
		SecureRandom rand = new SecureRandom();  // SecureRandom is preferred to Random
		String  pswd = "";
		StringBuilder sb = new StringBuilder();
		StringBuilder sc = new StringBuilder("!@#$%^&*-=?~");  // 특수문자 모음, {}[] 같은 비호감문자는 뺌
		
		// 대문자 4개를 임의 발생 
		sb.append((char)((rand.nextInt() * 26)+65));  // 첫글자는 대문자, 첫글자부터 특수문자 나오면 안 이쁨
		
		for (int i = 0; i<3; i++) {
			sb.append((char)((rand.nextInt() * 26)+65));  // 아스키번호 65(A) 부터 26글자 중에서 택일
		} 
		
		// 소문자 4개를 임의발생
		for (int i = 0; i<4; i++) {
			sb.append((char)((rand.nextInt() * 26)+97)); // 아스키번호 97(a) 부터 26글자 중에서 택일
		}  

		// 숫자 2개를 임의 발생
		for (int i = 0; i<2; i++) {
			sb.append((char)((rand.nextInt() * 10)+48)); //아스키번호 48(1) 부터 10글자 중에서 택일
		}

		// 특수문자를 두개  발생시켜 랜덤하게 중간에 끼워 넣는다 
		sb.setCharAt(((rand.nextInt()*3) +1), sc.charAt(rand.nextInt()*sc.length()-1)); //대문자3개중 하나
		sb.setCharAt(((rand.nextInt()*4) +4), sc.charAt(rand.nextInt()*sc.length()-1)); //소문자4개중 하나
		
		pswd = sb.toString();
		
		return pswd;
	}

	/**
	 * nvl (empty 대체)
	 */
	public static String nvl(String str) {
		if (str == null)
			return "";
		return str;
	}

	/**
	 * nvlt (trim 적용)
	 */
	public static String nvlt(String str) {
		if (str == null)
			return "";
		return str.trim();
	}

	/**
	 * nvl (replace대체)
	 */
	public static String nvl(String str, String replace) {
		if (isEmpty(str)) {
			return replace;
		} else {
			return str;
		}
	}

	/**
	 * 문자열이 널(null)이거나 공백문자열("")인지 검사한다.
	 */
	public static boolean isNullOrBlank(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * 문자열을 일정길이 만큼만 보여주고,
	 * 그 길이에 초과되는 문자열일 경우 특정문자를 덧붙여 보여준다.
	 * 
	 * @param input String 변환할 문자열
	 * @param limit int 문자열의 제한 길이
	 * @param postfix String 덧붙일 문자열
	 * @return String the translated string.
	 */
	public static String fixLength(String input, int limit, String postfix) {
		StringBuilder stringBuilder = new StringBuilder();
		char[] charArray = input.toCharArray();
		if (limit >= charArray.length) {
			return input;
		}
		
		for (int j = 0; j < limit; j++) {
			stringBuilder.append(charArray[j]);
		}

		stringBuilder.append(postfix);

		return stringBuilder.toString();
	}

	/**
	 * 문자열에서 특정 문자열을 치환한다.
	 * 
	 * @param source String 변환할 문자열
	 * @param keyStr String 치환 대상 문자열
	 * @param toStr String 치환될 문자열
	 * @return String the translated string.
	 * 
	 * <b>Example)</b> 
	 * 123456-7890123라는 문자열 str을 1234567890123 형식으로 바꾸고 싶다면,
	 * replaceStr( str, "-", "") 로 호출한다.
	 */
	public static String replaceStr(String source, String keyStr, String toStr) {
		if (source == null) {
			return null;
		}
		int startIndex = 0;
		int curIndex = 0;
		StringBuilder result = new StringBuilder();

		while ((curIndex = source.indexOf(keyStr, startIndex)) >= 0) {
			result.append(source, startIndex, curIndex).append(toStr);
			startIndex = curIndex + keyStr.length();
		}

		if (startIndex <= source.length()) {
			result.append(source.substring(startIndex));

		}
		return result.toString();
	}

	/**
	 * 문자열에서 특정 자리수를 치환한다.
	 * 
	 * @param source
	 * @param start int 치환할 시작 인덱스
	 * @param end int 치환할 끝 인덱스
	 * @param toStr String 치환될 문자열
	 * @return String the translated string.
	 * 
	 * <b>Example)</b> 
	 * StringUtil.replaceStr("abcde", 3, 4, "55"); 의 결과는 "abc55e" 이다.
	 */
	public static String replaceStr(String source, int start, int end, String toStr) {
		if (source == null) {
			return null;
		}
		StringBuilder result = new StringBuilder(source);

		int len = source.length();
		if (start > len || end < start) {
			return result.toString();
		}

		result.replace(start, end, toStr);
		return result.toString();
	}

	/**
	 * 문자열에서 특정 문자열을 치환한다.
	 * 문자열 배열의 차례대로 치환하되
	 * 더 이상 배열 값이 없으면 space 1칸으로 치환한다.
	 * 
	 * @param source String 변환할 문자열
	 * @param keyStr String 치환 대상 문자열
	 * @param toStr String[] 치환될 문자열 배열
	 * @return String the translated string.
	 */
	public static String replaceStr(String source, String keyStr, String[] toStr) {
		if (source == null) {
			return null;
		}
		int startIndex = 0;
		int curIndex = 0;
		int i = 0;
		StringBuilder result = new StringBuilder();
		String specialString = null;

		while ((curIndex = source.indexOf(keyStr, startIndex)) >= 0) {
			if (i < toStr.length) {
				specialString = toStr[i++];
			} else {
				specialString = " ";
			}
			result.append(source, startIndex, curIndex).append(specialString);
			startIndex = curIndex + keyStr.length();
		}

		if (startIndex <= source.length()) {
			result.append(source.substring(startIndex));

		}
		return result.toString();
	}

	/**
	 * 문자열을 특정 형식으로 출력한다.
	 * 단, source가 null이라면 빈 문자열(empty string)을 리턴한다.
	 * 형식: #은 문자열을 나타낸다. # 외의 문자는 그대로 출력된다.
	 * 
	 * @param source String 변환할 문자열
	 * @param format String 형식
	 * @return String the translated string.
	 * 
	 * <b>Example)</b> 
	 * 1234567890123라는 문자열 str을 123456-7890123 형식으로 바꾸고 싶다면,
	 * printStr( str, "######-#######") 로 호출한다.
	 */
	public static String formatStr(String source, String format) {
		if (source == null) {
			return "";
		}

		StringBuilder buf = new StringBuilder();
		char[] f = format.toCharArray();
		char[] s = source.toCharArray();

		int len = f.length;
		int h = 0;
		for (int i = 0; i < len; i++) {
			if (h >= s.length) {
				break;
			}
			if (f[i] == '#') {
				buf.append(s[h++]);
			} else {
				buf.append(f[i]);
			}
		}
		return buf.toString();
	}

	/**
	 * LPAD
	 * 
	 * @param str
	 *            입력 및 반환
	 * @param addStr
	 *            add할 문자
	 * @param len
	 *            전체 길이
	 * @return String
	 */
	public static String getLpad(String str, String addStr, int len) {
		if (addStr == null || addStr.length() == 0)
			return str;

		if (str == null)
			str = "";

		if (len < str.length())
			return str;

		int tmpLen = len - str.length();

		StringBuilder bld = new StringBuilder();
		for (int i = 0; i < tmpLen; i++) {
			bld.append(addStr + str);
		}

		str = bld.toString();

		return str;
	}

	/**
	 * RPAD
	 * 
	 * @param str
	 *            입력 및 반환
	 * @param addStr
	 *            add할 문자
	 * @param len
	 *            전체 길이
	 * @return String
	 */
	public static String getRpad(String str, String addStr, int len) {
		StringBuilder stringBuilder = new StringBuilder();

		if (addStr == null || addStr.length() == 0)
			return str;

		if (str == null)
			str = "";

		if (len < str.length())
			return str;

		int tmpLen = len - str.length();

		for (int i = 0; i < tmpLen; i++)
			stringBuilder.append(addStr);

		return stringBuilder.toString();
	}

	/**
	 * 문자열에서 숫자만 반환
	 * @param input
	 * @return String
	 */
	public static String getDigit(String input) {
		if (input == null || input.trim().length() == 0) {

			return "";
		}

		StringBuilder sb = new StringBuilder();
		int length = input.length();

		for (int i = 0; i < length; i++) {

			char curChar = input.charAt(i);
			if (Character.isDigit(curChar))
				sb.append(curChar);
		}
		return sb.toString();
	}

	/**
	 * int형 데이터를 특정 길이에 맞춰 구성한다.
	 * filler는 "0"이며 앞에 붙는다.
	 * 데이터의 길이가  size보다 짧으면 앞을 filler로 채우고, 길면 왼쪽부터 size자리까지만 자른다.
	 * 
	 * @param src int 형 데이터
	 * @param size 맞춤 길이
	 * @return String 맞춤 문자열
	 */
	public static String lpad(int src, int size) {
		StringBuilder builder = new StringBuilder(String.valueOf(src));
		while (builder.length() < size) {
			builder.insert(0, "0");
		}
		return builder.toString();
	}

	/**
	 * long형 데이터를 특정 길이에 맞춰 구성한다.
	 * filler는 "0"이며 앞에 붙는다.
	 * 데이터의 길이가  size보다 짧으면 앞을 filler로 채우고, 길면 왼쪽부터 size자리까지만 자른다.
	 * 
	 * @param src long 형 데이터
	 * @param size 맞춤 길이
	 * @return String 맞춤 문자열
	 */
	public static String lpad(long src, int size) {
		StringBuilder builder = new StringBuilder(String.valueOf(src));
		while (builder.length() < size) {
			builder.insert(0, "0");
		}
		return builder.toString();
	}

	/**
	 * double형 데이터를 특정 길이에 맞춰 구성한다.
	 * filler는 "0"이며 앞에 붙는다.
	 * 데이터의 길이가 size보다 짧으면 앞을 filler로 채우고, 길면 왼쪽부터 size자리까지만 자른다.
	 * 
	 * @param src double 형 데이터
	 * @param size 맞춤 길이
	 * @return String 맞춤 문자열
	 */
	public static String lpad(double src, int size) {
		StringBuilder pattern = new StringBuilder();
		String srcStr = String.valueOf(src);
		int len = srcStr.length();
		for (int i = size; i > len; i--) {
			pattern.append("0");
		}
		return pattern.append(srcStr).toString();
	}

	/**
	 * 문자열을 특정 길이에 맞춰 구성한다.
	 * filler는 "0"이며 앞에 붙는다.
	 * 문자열이 size보다 짧으면 앞을 filler로 채우고, 길면 왼쪽부터 size자리까지만 자른다.
	 * 
	 * @param src 원본 문자열
	 * @param size 맞춤 길이
	 * @return String 맞춤 문자열
	 */
	public static String lpad(String src, int size) {
		return lpad(src, size, "0"); // default:0
	}

	/**
	 * 문자열을 특정 길이에 맞춰 구성한다.
	 * filler는 앞에 붙는다.
	 * 문자열이 size보다 짧으면 앞을 filler로 채우고, 길면 왼쪽부터 size자리까지만 자른다.
	 * 
	 * @param src 원본 문자열
	 * @param size 맞춤 길이
	 * @param filler 빈자리에 채울 문자
	 * @return String 맞춤 문자열
	 */
	public static String lpad(String src, int size, String filler) {
		int len = nvl(src).length();
		if (src != null && len > size) {
			return src.substring(len - size);
		}
		
		StringBuilder pattern = new StringBuilder();
		for (int i = len; i < size; i++) {
			pattern.append(filler);
		}
		return pattern.toString() + nvl(src);
	}

	/**
	 * 소수점 이하 자리수의 길이를 맞춰 문자열로 리턴한다.
	 * 
	 * @param aPrecisionSize 소수점 이하 자리수
	 * @return String 맞춤 문자열
	 * 
	 * <b>Example)</b> 
	 * StringUtil.fixPrecision(5000, 5) 의 결과는 5000.00000
	 * StringUtil.fixPrecision(500.123456, 2) 의 결과는 500.12
	 */
	public static String fixPrecision2(int aPrecisionSize) {
		String result = "";

		try {
			double d = 0;

			result = new DecimalFormat("#################.############").format(d);
		} catch (Exception e) {
			return null;
		}

		log.debug("fixPrecision2 aPrecisionSize : {}", aPrecisionSize);

		return result;
	}

	/**
	 * 소수점 이하 자리수의 길이를 맞춰 문자열로 리턴한다.
	 * 
	 * @param aSrc 입력 문자열
	 * @param aPrecisionSize 소수점 이하 자리수
	 * @return String 맞춤 문자열
	 * 
	 * <b>Example)</b> 
	 * StringUtil.fixPrecision("500.2",5) 의 결과는 500.20000
	 * StringUtil.fixPrecision("500.2",0) 의 결과는 500
	 * StringUtil.fixPrecision(".02",0) 의 결과는 빈 문자열(empty String)
	 */
	public static String fixPrecision(String aSrc, int aPrecisionSize) {
		StringBuilder result = new StringBuilder();
		
		if (aSrc == null) {
			return null;
		}
		
		int index = aSrc.lastIndexOf(".");
		if (index >= 0) {
			int len = aSrc.length();
			if (aPrecisionSize > (len - index)) {
				for (int i = (len - index); i <= aPrecisionSize; i++) {
					result.append('0');
				}
			} else {
				result = new StringBuilder(result.substring(0, index));
			}
		} else {
			if (aPrecisionSize > 0) {
				result.append('.');
				for (int i = 0; i < aPrecisionSize; i++) {
					result.append('0');
				}
			}
		}
		return result.toString();
	}

	/**

	 * 소수점 자리수를 포맷에 맞게 표현하여 반환한다.
	 * 
	 * @param aSrc 입력 수
	 * @param aFormat 포맷
	 * @return String 맞춤 문자열
	 * 
	 * <b>Example)</b> 
	 * StringUtil.fixPrecision(500.2,"#####.000")의 결과는 500.200

	 */
	public static String fixPrecision(double aSrc, String aFormat) {
		if (aFormat == null || aFormat.equals("")) {
			return null;
		}
		DecimalFormat newFormat = new DecimalFormat(aFormat);
		return newFormat.format(aSrc);
	}

	/**
	 * 문자열을 특정 길이에 맞춰 구성한다.
	 * filler는 " "이며, 뒤에 붙는다.
	 * 문자열이 size보다 짧으면 filler로 채우고, 길면 오른쪽부터 size자리까지만 자른다.
	 * 
	 * @param src 원본 문자열
	 * @param size 맞춤 길이
	 * @return String 맞춤 문자열
	 */
	public static String rpad(String src, int size) {
		return rpad(src, size, " ");
	}

	/**
	 * 문자열을 특정 길이에 맞춰 구성한다.
	 * filler는 뒤에 붙는다.
	 * 문자열이 size보다 짧으면 filler로 채우고, 길면 오른쪽부터 size자리까지만 자른다.
	 * 
	 * @param src 원본 문자열
	 * @param size 맞춤 길이
	 * @param filler 빈자리에 채울 문자
	 * @return String 맞춤 문자열
	 */
	public static String rpad(String src, int size, String filler) {
		StringBuilder sb = new StringBuilder();
		if (src == null) {
			for (int i = 0; i < size; i++) {
				sb.append(filler);
			}
		} else {
			int len = src.length();
			if (len > size) {
				sb.append(src, 0, size);
			} else {
				sb.append(src);
			}

			for (int i = len; i < size; i++) {
				sb.append(filler);
			}
		}
		return sb.toString();
	}

	/**
	 * 구분자 있는 문자열에 대해 java.util.List 형태로 변환하여 반환한다.
	 * 
	 * @param sourceString 구분자 포함 문자열
	 * @param delim 구분자
	 * @return java.util.List
	 */
	public static List<String> stringToList(String sourceString, String delim) {
		List<String> destinationList = new ArrayList<>();

		if (sourceString != null) {
			int index = -1;
			int oldIndex = -1;

			while (true) {
				oldIndex = index + 1;
				index = sourceString.indexOf(delim, oldIndex);
				if (index != -1) {
					destinationList.add(sourceString.substring(oldIndex, index));
				} else {
					destinationList.add(sourceString.substring(oldIndex));
					break;
				}
			}
		}
		return destinationList;
	}

	/**
	 * java.util.List<String>의 내용을 구분자(,) 있는 문자열 형태로 변환하여 반환한다.
	 * 
	 * @param sourceList List
	 * @return String
	 */
	public static String listToString(List<String> sourceList) {
		if(sourceList==null || sourceList.isEmpty())return "";
		
		StringBuilder rtn = new StringBuilder();
		for(String str: sourceList) {
			rtn.append("'");
			rtn.append(str);
			rtn.append("',");
		}
		return rtn.substring(0, rtn.lastIndexOf(","));
	}

	/**
	 * java.util.List에 대해 구분자 있는 문자열의 형태로 변환하여 반환한다.
	 * 
	 * @param lst List
	 * @param delim 구분자
	 * @return String 구분자 있는 문자열
	 */
	@SuppressWarnings("rawtypes")
	public static String listToString(List lst, String delim) {
		StringBuilder buf = new StringBuilder();

		for (int i = 0; i < lst.size(); i++) {
			if (i != 0) {
				buf.append(delim);
				buf.append(lst.get(i));
			} else {
				buf.append(lst.get(i));
			}
		}

		return buf.toString();
	}

	/**
	 * isEmpty
	 *
	 * StringUtil.isEmpty(null)      = true
	 * StringUtil.isEmpty("")        = true
	 * StringUtil.isEmpty(" ")       = false
	 * StringUtil.isEmpty("bob")     = false
	 * StringUtil.isEmpty("  bob  ") = false
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * isNotEmpty
	 *
	 * StringUtil.isNotEmpty(null)      = false
	 * StringUtil.isNotEmpty("")        = false
	 * StringUtil.isNotEmpty(" ")       = true
	 * StringUtil.isNotEmpty("bob")     = true
	 * StringUtil.isNotEmpty("  bob  ") = true
	 */
	public static boolean isNotEmpty(String str) {
		return !StringUtil.isEmpty(str);
	}

	/**
	 * isBlank
	 *
	 * StringUtil.isBlank(null)      = true
	 * StringUtil.isBlank("")        = true
	 * StringUtil.isBlank(" ")       = true
	 * StringUtil.isBlank("bob")     = false
	 * StringUtil.isBlank("  bob  ") = false
	 */
	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Boolean.FALSE.equals(Character.isWhitespace(str.charAt(i)))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * isNotBlank
	 *
	 * StringUtil.isNotBlank(null)      = false
	 * StringUtil.isNotBlank("")        = false
	 * StringUtil.isNotBlank(" ")       = false
	 * StringUtil.isNotBlank("bob")     = true
	 * StringUtil.isNotBlank("  bob  ") = true
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	/**
	 * OR 조건으로 값이 일치하는지 비교한다.
	 * 
	 * @param  var 비교할 변수
	 * @param  val 비교할 값
	 * @return boolean 일치하는값이 있으면 true를 리턴한다.
	 * 
	 * <b>Example)</b>
	 *     StringUtil.equalsOr("CF", "CF|CG")
	 */
	private static final Map<String, String[]> splitedArrs = new ConcurrentHashMap<>();

	public static boolean equalsOr(String var1, String val) {
		if (var1 == null || "".equals(var1))
			return false;
		if (val == null || "".equals(val))
			return false;

		boolean rtnVal = false;
		String[] valArr = Optional.ofNullable(splitedArrs.get(val)).orElseGet(() -> {
			String[] value = val.split("\\|");
			splitedArrs.put(val, value);
			return value;
		});

		for (String _val : valArr) {
			if (_val.equals(var1)) {
				rtnVal = true;
				break;
			}
		}

		return rtnVal;
	}

	/**
	 * 입력된 값이 널이면, "" 값으로 대체하고, 널이 아니면 입력값을 trim()후 리턴한다.
	 * 
	 * @param str 문자열
	 * @return String 체크된 문자열
	 */
	public static String trimAll(String str) {
		str = nvl(str);
		return str.replaceAll("\\p{Z}", "");
	}

	/**
	 * 범주 패턴으로 index를 가져온다.
	 * <p>
	 * ex) 0-1,3,7-13 => [0, 1, 3, 7, 8, 9, 10, 11, 12, 13] 0부터 1까지 , 3 , 7부터
	 * 13까지
	 * </p>
	 * @param readRange
	 *            : 범주 패턴
	 * @return List
	 */
	public static List<Integer> getRange(String readRange)  {
		if ("".equals(nvl(readRange))) {
			return new ArrayList<>();
		}
		readRange = trimAll(readRange);

		List<Integer> rangeList = new ArrayList<>();

		String[] rangeTypes = readRange.split(",");
		for (String rangeType : rangeTypes) {
			if (rangeType.contains("-")) {
				String[] ranges = rangeType.split("-");
				int startWith = Integer.parseInt(ranges[0]);
				int endWith = Integer.parseInt(ranges[1]);

				// ex) 범주가 1-3 상황일때
				for (int i = startWith; i <= endWith; i++) {
					rangeList.add(i);
				}
			} else {
				rangeList.add(Integer.parseInt(rangeType));
			}
		}

		// 중복제거(List>Hash>list)
		List<Integer> rtnList = new ArrayList<>(new HashSet<>(rangeList));
		// List 정렬
		Collections.sort(rtnList);

		return rtnList;
	}

	/**
	 * 이동경로를 Data 형변환 한다.
	 * 
	 * @param strRout String (위도,경도|위도,경도)
	 * @return LinkedList
	 */
	public static List<Map<String, String>> decodeRout(String strRout)  {
		List<Map<String, String>> routList = new LinkedList<>();
				
		String[] arryLocation = strRout.split("\\|");//위도,경도
		Map<String, String> mapPoint = null;
		for(String location :arryLocation){
			String[] arryPoint = location.split(",");	//arryPoint[0]:위도, arryPoint[1]:경도
			if(arryPoint!=null && arryPoint.length>1){
				mapPoint =  new HashMap<>();
				mapPoint.put("MV_LAT", arryPoint[0]);
				mapPoint.put("MV_LONG", arryPoint[1]);
				routList.add(mapPoint);	//이동경로 List에 arryPoint({위도, 경도}배열)를 입력
			}
		}
		return routList;
	}

	/**
	 * XML String을 JSON String으로 변환
	 * 
	 * @param xml String
	 * @return String
	 */
	public static String xmlToJson(String xml) throws IOException {
		XmlMapper mapper = new XmlMapper();
		JsonNode node = mapper.readTree(xml.getBytes());

		ObjectMapper jsonMapper = CustomObjectMapper.get();

		return jsonMapper.writeValueAsString(node);
	}

	/**
	 * XML String을 JSON String으로 변환
	 * 감싸고 있는 노드를 제거
	 * 
	 * @param xml String
	 * @param deleteNode String
	 * @return String
	 */
	public static String xmlToJson(String xml, String deleteNode) throws IOException {
		StringBuilder deleteString = null;
		StringBuilder deleteString2 = null;
		if (xml.contains(deleteNode)) {
			deleteString = new StringBuilder();
			deleteString.append("<").append(deleteNode).append(">");
			deleteString2 = new StringBuilder();
			deleteString2.append("</").append(deleteNode).append(">");
			xml = xml.replaceAll(deleteString.toString(), "").replaceAll(deleteString2.toString(), "");
		}
		
		return StringUtil.xmlToJson(xml);
	}
	
	/**
	 * 전달받은 전화번호 사이에 하이픈(-)을 추가 한다.
	 * <p>
	 * ex)
	 * REQ : 01012341234
	 * RES : 010-1234-1234
	 * </p>
	 * @param phoneNumber
	 * @return
	 */
	public static String convertStrToCellNum(String phoneNumber) {
		final String REGEX = "(^02|^0505|^1[\\d]{3}|^0[\\d]{2})([\\d]+)?([\\d]{4})$";
		phoneNumber = phoneNumber.replaceFirst(REGEX, "$1-$2-$3").replace("--", "-");
		
		return phoneNumber;
	}
	
	/**
	 * 전달받은 전화번호 사이에 하이픈(-)을 제거 한다.
	 * <p>
	 * ex)
	 * REQ : 010-1234-1234
	 * RES : 01012341234
	 * </p>
	 * @param phoneNumber
	 * @return
	 */
	public static String convertCellNumToStr(String phoneNumber) {
		String[] cellNum = phoneNumber.split("-");
		
		StringBuilder builder = new StringBuilder();
		for (String str : cellNum) {
			builder.append(str);
		}
		
		return builder.toString();
	}
	
	/**
	 * 전달받은 전화번호의 정규식을 체크한다.
	 * <p>
	 * ex)
	 * REQ : 01012341234, 010-1234-1234
	 * RES : true, true
	 * </p>
	 * <p>
	 * REQ : 010121234, 010-12-1234
	 * RES : false, false
	 * </p>
	 * @param phoneNumber
	 * @return
	 */
	public static boolean formatPhoneNumberValid(String phoneNumber) {
		phoneNumber = convertCellNumToStr(phoneNumber);
		
		final String REGEX = "(^0[\\d]{2})([\\d]{3,4})([\\d]{4})$";
		return Pattern.matches(REGEX, phoneNumber);
	}

}
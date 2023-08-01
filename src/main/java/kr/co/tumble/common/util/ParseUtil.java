package kr.co.tumble.common.util;



import kr.co.tumble.common.constant.TumbleConstants;
import kr.co.tumble.common.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * ParseUtil
 */
@Slf4j
public class ParseUtil {

	private static final String CLASS = "class";

	private ParseUtil() {}

	public static Map<String, String> parseStringToMap(final String str) throws Exception {
		return parseStringToMap(str, TumbleConstants.AND, TumbleConstants.EQUAL);
	}

	public static Map<String, String> parseStringToMap(final String str, final String and) throws Exception {
		return parseStringToMap(str, and, TumbleConstants.EQUAL);
	}

	public static Map<String, String> parseStringToMap(final String str, final String and, final String equals) throws Exception {
		final Map<String, String> hash = new HashMap<>();
		final StringTokenizer token = new StringTokenizer(str, and);
		String temp = "";

		while (token.hasMoreElements()) {
			try {
				temp = token.nextToken();
				hash.put(temp.substring(0, temp.indexOf(equals)), temp.substring(temp.indexOf(equals) + 1));
			} catch (final IndexOutOfBoundsException indexoutofboundsexception) {
				log.error("parseStringToMap indexoutofboundsexception : {}", indexoutofboundsexception);
			}
		}

		return hash;
	}

	public static Map<String, String> parseStringToMapUrlDecode(final String str) throws Exception {
		return parseStringToMapUrlDecode(str, TumbleConstants.AND, TumbleConstants.EQUAL);
	}

	public static Map<String, String> parseStringToMapUrlDecode(final String str, final String and) throws Exception {
		return parseStringToMapUrlDecode(str, and, TumbleConstants.EQUAL);
	}

	public static Map<String, String> parseStringToMapUrlDecode(final String str, final String and, final String equals) throws Exception {
		final Map<String, String> hash = new HashMap<>();
		final StringTokenizer token = new StringTokenizer(str, and);
		String temp = "";

		while (token.hasMoreElements()) {
			try {
				temp = token.nextToken();
				hash.put(temp.substring(0, temp.indexOf(equals)),
						URLDecoder.decode(temp.substring(temp.indexOf(equals) + 1), "UTF-8"));
			} catch (final IndexOutOfBoundsException indexoutofboundsexception) {
				log.error("parseStringToMapUrlDecode indexoutofboundsexception : {}", indexoutofboundsexception);
			}
		}

		return hash;
	}

	public static void parseStringToBean(final Object obj, final String str) throws Exception {
		parseStringToBean(obj, str, TumbleConstants.AND, TumbleConstants.EQUAL);
	}

	public static void parseStringToBean(final Object obj, final String str, final String and) throws Exception {
		parseStringToBean(obj, str, and, TumbleConstants.EQUAL);
	}

	public static void parseStringToBean(final Object obj, final String str, final String and, final String equals)
			throws Exception {
		final Map<String, String> hash = new HashMap<>();
		final StringTokenizer token = new StringTokenizer(str, and);
		String temp = "";

		while (token.hasMoreElements()) {
			try {
				temp = token.nextToken();
				hash.put(temp.substring(0, temp.indexOf(equals)), temp.substring(temp.indexOf(equals) + 1));
			} catch (final IndexOutOfBoundsException indexoutofboundsexception) {
				log.error("parseStringToBean indexoutofboundsexception : {}", indexoutofboundsexception);
			}
		}

		populate(obj, hash);
	}

	public static String parseBeanToString(final Object obj) {
		return parseBeanToString(obj, TumbleConstants.AND, TumbleConstants.EQUAL);
	}

	public static String parseBeanToString(final Object obj, final String and) {
		return parseBeanToString(obj, and, TumbleConstants.EQUAL);
	}

	public static String parseBeanToString(final Object obj, final String and, final String equals) {
		try {
			return parseMapToString(beanToMap(obj), and, equals);
		} catch (Exception e) {
			throw new CommonException(e);
		}
	}

	public static String parseMapToString(final Map<String, String> parameters) {
		return parseMapToString(parameters, TumbleConstants.AND, TumbleConstants.EQUAL);
	}

	public static String parseMapToString(final Map<String, String> parameters, final String and) {
		return parseMapToString(parameters, and, TumbleConstants.AND);
	}

	public static String parseMapToString(final Map<String, String> parameters, final String and, final String equals) {
		final StringBuilder serializeString = new StringBuilder();
		final Map<String, String>  sortedParamMap = new TreeMap<>();
		sortedParamMap.putAll(parameters);

		for (final Iterator<?> pairs = sortedParamMap.entrySet().iterator(); pairs.hasNext();) {
			final Map.Entry<String, String>  pair = (Map.Entry) pairs.next();
			if (!CLASS.equals(pair.getKey())) {
				serializeString.append(pair.getKey());
				serializeString.append(equals);
				serializeString.append(StringUtils.defaultString(pair.getValue()));
				if (pairs.hasNext()) {
					serializeString.append(and);
				}
			}
		}

		return serializeString.toString();
	}

	public static String parseBeanToStringByUrlEncode(final Object obj) {
		return parseBeanToStringByUrlEncode(obj, TumbleConstants.AND, TumbleConstants.EQUAL);
	}

	public static String parseBeanToStringByUrlEncode(final Object obj, final String and) {
		return parseBeanToStringByUrlEncode(obj, and, TumbleConstants.EQUAL);
	}

	public static String parseBeanToStringByUrlEncode(final Object obj, final String and, final String equals) {
		try {
			return parseMapToStringByUrlEncode(beanToMap(obj), and, equals);
		} catch (Exception e) {
			throw new CommonException(e);
		}
	}

	public static String parseMapToStringByUrlEncode(final Map<String, String>  parameters) {
		return parseMapToStringByUrlEncode(parameters, TumbleConstants.AND, TumbleConstants.EQUAL);
	}

	public static String parseMapToStringByUrlEncode(final Map<String, String>  parameters, final String and) {
		return parseMapToStringByUrlEncode(parameters, and, TumbleConstants.EQUAL);
	}

	@SuppressWarnings("unchecked")
	public static String parseMapToStringByUrlEncode(final Map<String, String> parameters, final String and, final String equals) {
		final StringBuilder serializeString = new StringBuilder("");
		final Map<String, String>  sortedParamMap = new TreeMap<> ();
		sortedParamMap.putAll(parameters);

		for (final Iterator<?> pairs = sortedParamMap.entrySet().iterator(); pairs.hasNext();) {
			final Map.Entry<String, String>  pair = (Map.Entry<String, String>) pairs.next();
			if (!CLASS.equals(pair.getKey())) {
				serializeString.append(pair.getKey());
				serializeString.append(equals);
				try {
					serializeString.append(URLEncoder.encode(pair.getValue(), StandardCharsets.UTF_8));
				} catch (final Exception exception) {
					log.debug("ParseUtil parseMapToStringByUrlEncode : {}", exception);
				}

				if (pairs.hasNext()) {
					serializeString.append(and);
				}
			}
		}

		return serializeString.toString();
	}

	public static String removeEmpty(final String parameters) throws Exception {
		return removeEmpty(parameters, TumbleConstants.AND, TumbleConstants.EQUAL);
	}

	public static String removeEmpty(final String parameters, final String and) throws Exception {
		return removeEmpty(parameters, and, TumbleConstants.EQUAL);
	}

	public static String removeEmpty(String parameters, final String and, final String equals) throws Exception {
		final Map<String, String> hash = new HashMap<>();
		final StringTokenizer token = new StringTokenizer(parameters, and);
		String temp = "";
		while (token.hasMoreElements()) {
			try {
				temp = token.nextToken();
				if (!"".equals(StringUtils.defaultString(temp.substring(temp.indexOf(equals) + 1)))) {
					hash.put(temp.substring(0, temp.indexOf(equals)), temp.substring(temp.indexOf(equals) + 1));
				}
			}
			catch (final IndexOutOfBoundsException indexoutofboundsexception) {
				log.error("removeEmpty indexOutOfBoundsException: {}", indexoutofboundsexception);
			}
		}
		parameters = parseMapToString(hash, and, equals);
		return parameters;
	}

	public static Map<String, String> removeEmpty(final Map<String, String> parameters) {
		final Set<?> set = parameters.keySet();
		for (final Iterator<?> iterator = set.iterator(); iterator.hasNext();) {
			final String key = (String) iterator.next();
			if ("".equals(StringUtils.defaultString(parameters.get(key)))) {
				parameters.remove(key);
			}
		}

		return parameters;
	}

	public static void populate(final Object bean, final Map<String, ? extends Object> properties) throws IllegalAccessException, InvocationTargetException {
		BeanUtilsBean.getInstance().populate(bean, properties);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> beanToMap(final Object obj) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (obj instanceof Map) {
			return (Map<String, String>) obj;
		} else {
			final Map<String, String> map = BeanUtils.describe(obj);
			map.remove(CLASS);
			return map;
		}
	}
}

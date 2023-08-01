package kr.co.tumble.common.util;


import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * ObjectUtil Class
 */
public class ObjectUtil {

	private ObjectUtil() {}

	/**
	 * Object Null 체크여부
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		
		if (obj instanceof Optional) {
			return !((Optional<?>) obj).isPresent();
		}
		
		if (obj instanceof CharSequence charSequence) {
			return charSequence.length() == 0;
		}
		
		if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		}
		
		if (obj instanceof Collection) {
			return ((Collection<?>) obj).isEmpty();
		}
		
		if (obj instanceof Map) {
			return ((Map<?, ?>) obj).isEmpty();
		}
		
		return false;
	}
	
	/**
	 * Object Not Null 체크여부
	 */
	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}
}
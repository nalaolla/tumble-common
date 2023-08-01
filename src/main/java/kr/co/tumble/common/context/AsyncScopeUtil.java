package kr.co.tumble.common.context;

import java.util.HashMap;
import java.util.Map;

/**
 * AsyncScopeUtil
 */
public class AsyncScopeUtil {

    private AsyncScopeUtil() {}

    private static final Map<String, Object> attributeMap = new HashMap<>();

    public static Object getAttribute() {
        return attributeMap.get(Long.toString(Thread.currentThread().getId()));
    }

    public static void setAttribute(Object attribute) {
        attributeMap.put(Long.toString(Thread.currentThread().getId()), attribute);
    }

    public static void removeAttribute() {
        attributeMap.remove(Long.toString(Thread.currentThread().getId()));
    }

}
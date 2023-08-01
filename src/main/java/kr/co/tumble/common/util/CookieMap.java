package kr.co.tumble.common.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CookieMap
 * Cookie값을 LinkedHashMap으로 설정
 */
public class CookieMap implements Serializable {

    private static final long serialVersionUID = -5287518129831622248L;

    private LinkedHashMap<String, String> inMap = new LinkedHashMap<>();

    public String get(String key) {
        return inMap.get(key);
    }
    public void put(String key, String value) {
        inMap.put(key, value);
    }
    public void remove(String key) {
        inMap.remove(key);
    }
    public Map<String, String> getMap() {
        return inMap;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CookieMap [ ");
        if (inMap != null) {
            int i = 0;
            for (Iterator<?> iterator = inMap.entrySet().iterator(); iterator.hasNext(); i++) { // && i < inMap.size()
                if (i > 0) builder.append(", ");
                builder.append(iterator.next());
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
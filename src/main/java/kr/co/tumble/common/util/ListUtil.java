package kr.co.tumble.common.util;

import java.util.Collections;
import java.util.List;

/**
 * ListUtil Class
 */
public class ListUtil {

    private ListUtil() {}

    public static <T> List<T> emptyIfNull(final List<T> list) {
        return list == null ? Collections.<T>emptyList() : list;
    }

}

package kr.co.tumble.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.tumble.common.exception.FrameworkException;
import kr.co.tumble.common.messageconverter.CustomObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * EntityUtils
 */
@Component
public class EntityUtils {

	public static final String INSERT_LIST = "insertList";
	public static final String UPDATE_LIST = "updateList";
	public static final String DELETE_LLIST = "deleteList";

	private EntityUtils() {}

	public static <T> T fromJSONObject(String jsonString, Class<T> clazz) {
		ObjectMapper mapper = CustomObjectMapper.get();

		try {
			return mapper.readValue(jsonString, clazz);
		} catch (Exception e) {
			throw new FrameworkException(e);
		}
	}

	@SuppressWarnings("unchecked")
    public static <T> T fromBase64(String base64) {
		byte[] bytes = Base64.decodeBase64(base64);

		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				ObjectInputStream ois = new ObjectInputStream(bais)) {
			return (T) ois.readObject();
		} catch (Exception e) {
			throw new FrameworkException(e);
		}
	}

    public static boolean nullOrEmpty(List<?> list){
        return list == null || list.isEmpty();
    }

    public static boolean notNullAndNotEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }
}

package kr.co.tumble.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.tumble.common.messageconverter.CustomObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * JsonUtils Class
 */
@Slf4j
public class JsonUtils {

	private JsonUtils() {}

	public static String string(Object value) {
		try {
			return objectMapper().writeValueAsString(value);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static String toJsonStrIgnoreError(Object obj) {
		String jsonStr = null;
		
        try {
            jsonStr = objectMapper().writeValueAsString(obj);
        } catch (Exception e) {
			log.debug("toJsonStrIgnoreError jsonStr = {}", e);
        }
        
        return jsonStr;
	}
	
	public static <T> T object(String json, Class<T> valueType) {
		try {
			return objectMapper().readValue(json, valueType);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> T object(String json, TypeReference<T> valueType) {
		try {
			return objectMapper().readValue(json, valueType);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static ObjectMapper objectMapper() {
		return Instance.objectMapper;
	}
	
	@Component
	static class Instance {
		private static ObjectMapper objectMapper = CustomObjectMapper.get();
		
		@Autowired
		void init(ObjectMapper objectMapper) {
			setObjectMapper(objectMapper);
		}

		private static void setObjectMapper(ObjectMapper objectMapper) {
			if (objectMapper == null) {
				Instance.objectMapper = objectMapper;
			}
		}
	}

}
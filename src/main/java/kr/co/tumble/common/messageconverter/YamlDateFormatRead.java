package kr.co.tumble.common.messageconverter;

import kr.co.tumble.common.properties.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * YamlDateFormatRead
 */
@PropertySource(
	value = {"classpath:config/dateformat.yml"}
	, ignoreResourceNotFound = true
	, factory = YamlPropertySourceFactory.class
)
@Configuration
@ConfigurationProperties(prefix = "dateformat", ignoreInvalidFields = true)
@Setter
@Getter
public class YamlDateFormatRead {

	private Map<String, Object> localTime = new HashMap<>();
	private Map<String, Object> localDate = new HashMap<>();
	private Map<String, Object> localDateTime = new HashMap<>();
    private List<String> patterns = new ArrayList<>();

	private static final String SERIALIZE = "serialize";
	private static final String DESERIALIZE = "deserialize";

    public Object getSerialize(Class<?> dateClass) {
    	if (dateClass.equals(LocalDateTime.class)) {
    		return localDateTime.getOrDefault(SERIALIZE, null);
    	} else if (dateClass.equals(LocalDate.class)) {
    		return localDate.getOrDefault(SERIALIZE, null);
    	} else if (dateClass.equals(LocalTime.class)) {
    		return localTime.getOrDefault(SERIALIZE, null);
    	} else {
    		return null;
    	}
    }

    public Object getDeserialize(Class<?> dateClass) {
    	if (dateClass.equals(LocalDateTime.class)) {
    		return localDateTime.getOrDefault(DESERIALIZE, null);
    	} else if (dateClass.equals(LocalDate.class)) {
    		return localDate.getOrDefault(DESERIALIZE, null);
    	} else if (dateClass.equals(LocalTime.class)) {
    		return localTime.getOrDefault(DESERIALIZE, null);
    	} else {
    		return null;
    	}
    }

    @SuppressWarnings({ "unused", "unchecked" })
	public List<String> getPatterns(String langCd, Map<String, Object> map) {
    	patterns = new ArrayList<>();

    	List<String> langCds = new ArrayList<>(Arrays.asList(langCd, "base"));
    	for (String langCode : langCds) {
    		Object obejct = map.get(langCode);
    		if (ObjectUtils.isNotEmpty(obejct) && obejct.getClass().equals(java.util.LinkedHashMap.class)) {
    			patterns.addAll(((Map<String, String>) map.get(langCode)).values());
    		} else if (ObjectUtils.isNotEmpty(obejct) && map.get(langCode).getClass().equals(String.class)) {
    			patterns.add((String) map.get(langCode));
    		}
    	}
		return patterns;
	}

}
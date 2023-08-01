package kr.co.tumble.common.messageconverter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Jackson LocalTime Serializer
 */
@Slf4j
public class JacksonLocalTimeSerializer extends StdSerializer<LocalTime> {

	protected static YamlDateFormatRead read;

	protected static String langCd;

    public JacksonLocalTimeSerializer() {
        this(null);
    }

    protected JacksonLocalTimeSerializer(Class<LocalTime> t) {
        super(t);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void serialize(LocalTime localTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
    	Boolean success = false;

    	Map<String, Object> localTimeMap = (Map<String, Object>) read.getSerialize(LocalTime.class);
    	if (localTimeMap != null) {
    		List<String> patterns = read.getPatterns(langCd, localTimeMap);
    		if (patterns != null && !patterns.isEmpty()) {
		    	for (String pattern : patterns) {
		    		success = this.writeLocalTimeToString(jsonGenerator, localTime, pattern);
		    		if (Boolean.TRUE.equals(success)) {
						break;
		    		}
				}
    		}
    	}
        if (Boolean.FALSE.equals(success)) {
    		for (DateTimeFormatter dateTimeFormatter : DATE_FORMAT_LIST) {
    			success = this.writeLocalTimeToString(jsonGenerator, localTime, dateTimeFormatter);
    			if (Boolean.TRUE.equals(success)) {
    				break;
        		}
    		}
    	}
        if (Boolean.FALSE.equals(success)) {
        	log.error("[CustomObjectMapper] JacksonLocalTimeSerializer : {}", localTime);
        }
    }

	private Boolean writeLocalTimeToString(JsonGenerator jsonGenerator, LocalTime localTime, String pattern) {
    	try {
    		return this.writeLocalTimeToString(jsonGenerator, localTime, DateTimeFormatter.ofPattern(pattern));
    	} catch (Exception ex) {
    		return false;
    	}
    }

	private Boolean writeLocalTimeToString(JsonGenerator jsonGenerator, LocalTime localTime, DateTimeFormatter dateTimeFormatter) {
    	try {
    		jsonGenerator.writeString(dateTimeFormatter.format(localTime));
        } catch (Exception ex) {
        	log.debug("JacksonLocalTimeSerializer writeLocalTimeToString : {}", ex);
        	return false;
        }
    	return true;
    }

    private static final List<DateTimeFormatter> DATE_FORMAT_LIST = List.of(
		DateTimeFormatter.ofPattern("HH:mm:ss")
    );

}
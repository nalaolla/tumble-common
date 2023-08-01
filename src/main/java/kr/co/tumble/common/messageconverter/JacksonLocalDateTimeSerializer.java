package kr.co.tumble.common.messageconverter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Jackson LocalDateTime Serializer
 */
@Slf4j
public class JacksonLocalDateTimeSerializer extends StdSerializer<LocalDateTime> {

	protected static YamlDateFormatRead read;

	protected static String langCd;

    public JacksonLocalDateTimeSerializer() {
        this(null);
    }

    protected JacksonLocalDateTimeSerializer(Class<LocalDateTime> t) {
        super(t);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
    	Boolean success = false;

		Map<String, Object> localDateTimeMap = (Map<String, Object>) read.getSerialize(LocalDateTime.class);
		if (localDateTimeMap != null) {
			List<String> patterns = read.getPatterns(langCd, localDateTimeMap);
			if (patterns != null && !patterns.isEmpty()) {
				for (String pattern : patterns) {
					success = this.writeLocalDateTimeToString(jsonGenerator, localDateTime, pattern);
					if (Boolean.TRUE.equals(success)) {
						break;
					}
				}
			}
		}
    	if (Boolean.FALSE.equals(success)) {
    		for (DateTimeFormatter dateTimeFormatter : DATE_FORMAT_LIST) {
    			success = this.writeLocalDateTimeToString(jsonGenerator, localDateTime, dateTimeFormatter);
    			if (Boolean.TRUE.equals(success)) {
    				break;
        		}
    		}
    	}
    	if (Boolean.FALSE.equals(success)) {
        	log.error("[CustomObjectMapper] JacksonLocalDateTimeSerializer : {}", localDateTime);
        }
    }

	private Boolean writeLocalDateTimeToString(JsonGenerator jsonGenerator, LocalDateTime localDateTime, String pattern) {
    	try {
    		return this.writeLocalDateTimeToString(jsonGenerator, localDateTime, DateTimeFormatter.ofPattern(pattern));
    	} catch (Exception ex) {
    		return false;
    	}
    }

    private Boolean writeLocalDateTimeToString(JsonGenerator jsonGenerator, LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
    	try {
    		jsonGenerator.writeString(dateTimeFormatter.format(localDateTime));
        } catch (Exception ex) {
        	ex.printStackTrace();
        	return false;
        }
    	return true;
    }

    private static final List<DateTimeFormatter> DATE_FORMAT_LIST = List.of(
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    );

}
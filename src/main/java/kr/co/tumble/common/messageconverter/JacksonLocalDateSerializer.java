package kr.co.tumble.common.messageconverter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Jackson LocalDate Serializer
 */
@Slf4j
public class JacksonLocalDateSerializer extends StdSerializer<LocalDate> {

	protected static YamlDateFormatRead read;

	protected static String langCd;

    public JacksonLocalDateSerializer() {
        this(null);
    }

    protected JacksonLocalDateSerializer(Class<LocalDate> t) {
        super(t);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
    	Boolean success = false;

    	Map<String, Object> localDateMap = (Map<String, Object>) read.getSerialize(LocalDate.class);
    	if (localDateMap != null) {
    		List<String> patterns = read.getPatterns(langCd, localDateMap);
    		if (patterns != null && !patterns.isEmpty()) {
		    	for (String pattern : patterns) {
		    		success = this.writeLocalDateToString(jsonGenerator, localDate, pattern);
		    		if(Boolean.TRUE.equals(success)) {
						break;
		    		}
				}
    		}
    	}
    	if (Boolean.FALSE.equals(success)) {
    		for (DateTimeFormatter dateTimeFormatter : DATE_FORMAT_LIST) {
    			success = this.writeLocalDateToString(jsonGenerator, localDate, dateTimeFormatter);
    			if (Boolean.TRUE.equals(success)) {
    				break;
        		}
    		}
    	}
    	if (Boolean.FALSE.equals(success)) {
        	log.error("[CustomObjectMapper] JacksonLocalDateSerializer : {}", localDate);
        }
    }

	private Boolean writeLocalDateToString(JsonGenerator jsonGenerator, LocalDate localDate, String pattern) {
    	try {
    		return this.writeLocalDateToString(jsonGenerator, localDate, DateTimeFormatter.ofPattern(pattern));
    	} catch (Exception ex) {
    		return false;
    	}
    }

	private Boolean writeLocalDateToString(JsonGenerator jsonGenerator, LocalDate localDate, DateTimeFormatter dateTimeFormatter) {
    	try {
    		jsonGenerator.writeString(dateTimeFormatter.format(localDate));
        } catch (Exception ex) {
			log.error("[JacksonLocalDateSerializer] writeLocalDateToString : {}", ex);
        	return false;
        }
    	return true;
    }

    private static final List<DateTimeFormatter> DATE_FORMAT_LIST = List.of(
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    );

}
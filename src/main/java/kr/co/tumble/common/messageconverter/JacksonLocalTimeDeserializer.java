package kr.co.tumble.common.messageconverter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Jackson LocalTime Deserializer
 */
@JsonComponent
@Slf4j
public class JacksonLocalTimeDeserializer extends StdDeserializer<LocalTime> {

    protected static YamlDateFormatRead read;

    protected static String langCd;

    public JacksonLocalTimeDeserializer() {
        this(null);
    }

    protected JacksonLocalTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    @SuppressWarnings("unchecked")
    @Override
    public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getText();

        LocalTime localTime = null;
        if (StringUtils.isNotBlank(value)) {
        	for (DateTimeFormatter dateTimeFormatter : DATE_FORMAT_LIST) {
        		localTime = getLocalTime(value, dateTimeFormatter);
                if (localTime != null) {
                    break;
                }
            }

			Map<String, Object> localTimeMap = (Map<String, Object>) read.getDeserialize(LocalTime.class);
        	if (localTime == null && localTimeMap != null) {
        		List<String> patterns = read.getPatterns(langCd, localTimeMap);
        		if (patterns != null && !patterns.isEmpty()) {
        			for (String pattern : patterns) {
        				localTime = this.getLocalTime(value, DateTimeFormatter.ofPattern(pattern));
        				if (localTime != null) {
        					break;
        				}
        			}
        		}
        	}
        }

        if(localTime == null) {
        	log.error("[CustomObjectMapper] JacksonLocalTimeDeserializer : {}", value);
        }
        return localTime;
    }

    private LocalTime getLocalTime(String value, DateTimeFormatter dateTimeFormatter) {
        LocalTime localTime = null;
        try {
            localTime = LocalTime.parse(value, dateTimeFormatter);
        } catch (Exception ex) {
            localTime = null;
        }
        return localTime;
    }

	private static final List<DateTimeFormatter> DATE_FORMAT_LIST = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_INSTANT,
            DateTimeFormatter.RFC_1123_DATE_TIME,
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ISO_OFFSET_DATE,
            DateTimeFormatter.ISO_DATE,
            DateTimeFormatter.ISO_ORDINAL_DATE,
            DateTimeFormatter.BASIC_ISO_DATE
    );
}
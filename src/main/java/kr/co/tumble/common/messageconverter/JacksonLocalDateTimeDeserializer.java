package kr.co.tumble.common.messageconverter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Jackson LocalDateTime Deserializer
 */
@Slf4j
@JsonComponent
public class JacksonLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    protected static YamlDateFormatRead read;

	protected static String langCd;

    public JacksonLocalDateTimeDeserializer() {
        this(null);
    }

    protected JacksonLocalDateTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    @SuppressWarnings("unchecked")
	@Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        String value = jsonParser.getText();

        LocalDateTime localDateTime = null;
        if (StringUtils.isNotBlank(value)) {
        	for (DateTimeFormatter dateTimeFormatter : DATE_FORMAT_LIST) {
                localDateTime = getLocalDateTime(value, dateTimeFormatter);
                if (localDateTime != null) {
                    break;
                }
            }

			Map<String, Object> localDateTimeMap = (Map<String, Object>) read.getDeserialize(LocalDateTime.class);
        	if (localDateTime == null && localDateTimeMap != null) {
        		List<String> patterns = read.getPatterns(langCd, localDateTimeMap);
        		if (patterns != null && !patterns.isEmpty()) {
        			for (String pattern : patterns) {
        				localDateTime = this.getLocalDateTime(value, DateTimeFormatter.ofPattern(pattern));
        				if (localDateTime != null) {
        					break;
        				}
        			}
        		}
        	}
        }

        if (localDateTime == null) {
        	log.warn("[CustomObjectMapper] JacksonLocalDateTimeDeserializer : {}", value);
        }
        return localDateTime;
    }

    private LocalDateTime getLocalDateTime(String value, DateTimeFormatter dateTimeFormatter) {
        LocalDateTime localDateTime = null;
        try {
            localDateTime = LocalDateTime.parse(value, dateTimeFormatter);
        } catch (Exception ex) {
            localDateTime = null;
        }
        return localDateTime;
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
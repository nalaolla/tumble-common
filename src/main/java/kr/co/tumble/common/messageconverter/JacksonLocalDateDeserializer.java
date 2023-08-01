package kr.co.tumble.common.messageconverter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Jackson LocalDate Deserializer
 */
@JsonComponent
@Slf4j
public class JacksonLocalDateDeserializer extends StdDeserializer<LocalDate> {

    protected static YamlDateFormatRead read;

    protected static String langCd;

    public JacksonLocalDateDeserializer() {
        this(null);
    }

    protected JacksonLocalDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @SuppressWarnings("unchecked")
	@Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getText();

        LocalDate localDate = null;
        if (StringUtils.isNotBlank(value)) {
        	for (DateTimeFormatter dateTimeFormatter : DATE_FORMAT_LIST) {
        		localDate = getLocalDate(value, dateTimeFormatter);
                if (localDate != null) {
                    break;
                }
            }

			Map<String, Object> localDateMap = (Map<String, Object>) read.getDeserialize(LocalDate.class);
        	if (localDate == null && localDateMap != null) {
        		List<String> patterns = read.getPatterns(langCd, localDateMap);
        		if (patterns != null && !patterns.isEmpty()) {
        			for (String pattern : patterns) {
        				localDate = this.getLocalDate(value, DateTimeFormatter.ofPattern(pattern));
        				if (localDate != null) {
        					break;
        				}
        			}
        		}
        	}
        }

        if(localDate == null) {
        	log.error("[CustomObjectMapper] JacksonLocalDateDeserializer : {}", value);
        }
        return localDate;
    }

    private LocalDate getLocalDate(String value, DateTimeFormatter dateTimeFormatter) {
        LocalDate localDate = null;

        try {
            localDate = LocalDate.parse(value, dateTimeFormatter);
        } catch (Exception ex) {
            localDate = null;
        }
        return localDate;
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
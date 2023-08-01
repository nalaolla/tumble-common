package kr.co.tumble.common.messageconverter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Jackson Formatter LocalDateTime Deserializer
 */
@JsonComponent
public class FormatterLocalDateTimeDeserializer implements Converter<String, LocalDateTime> {

	protected static YamlDateFormatRead read;

	protected static String langCd;

	@SuppressWarnings("unchecked")
	@Override
	public LocalDateTime convert(String text) {
		String value = text;
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
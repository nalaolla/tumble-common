package kr.co.tumble.common.messageconverter;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import kr.co.tumble.common.context.CookieContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * CustomObjectMapper Class
 */
@Component
@RequiredArgsConstructor
@ComponentScan(basePackageClasses = YamlDateFormatRead.class)
public class CustomObjectMapper {

    private final YamlDateFormatRead read;
    private static YamlDateFormatRead yamlDateFormatRead = null;

    @PostConstruct
    void init() {
        setYamlDateFormatRead(this.read);
    }

    private static void setYamlDateFormatRead(YamlDateFormatRead read) {
        if (yamlDateFormatRead == null) {
            yamlDateFormatRead = read;
        }
    }

    public static YamlDateFormatRead getYamlDateFormatRead() {
        return yamlDateFormatRead;
    }

    public static ObjectMapper get() {
        return CustomObjectMapper.get(CustomObjectMapper.getYamlDateFormatRead());
    }

    @SuppressWarnings("static-access")
    @Autowired
    public static ObjectMapper get(YamlDateFormatRead read){

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());

        SimpleModule simpleModule = new SimpleModule();

        // Serializer
        JacksonLocalDateSerializer jacksonLocalDateSerializer = new JacksonLocalDateSerializer();
        jacksonLocalDateSerializer.read = read;
        jacksonLocalDateSerializer.langCd = CookieContextHolder.getLangCd();
        simpleModule.addSerializer(LocalDate.class, jacksonLocalDateSerializer);

        JacksonLocalTimeSerializer jacksonLocalTimeSerializer = new JacksonLocalTimeSerializer();
        jacksonLocalTimeSerializer.read = read;
        jacksonLocalTimeSerializer.langCd = CookieContextHolder.getLangCd();
        simpleModule.addSerializer(LocalTime.class, jacksonLocalTimeSerializer);

        JacksonLocalDateTimeSerializer jacksonLocalDateTimeSerializer = new JacksonLocalDateTimeSerializer();
        jacksonLocalDateTimeSerializer.read = read;
        jacksonLocalDateTimeSerializer.langCd = CookieContextHolder.getLangCd();
        simpleModule.addSerializer(LocalDateTime.class, jacksonLocalDateTimeSerializer);

        // Deserializer
        JacksonLocalDateDeserializer jacksonLocalDateDeserializer = new JacksonLocalDateDeserializer();
        jacksonLocalDateDeserializer.read = read;
        jacksonLocalDateDeserializer.langCd = CookieContextHolder.getLangCd();
        simpleModule.addDeserializer(LocalDate.class, jacksonLocalDateDeserializer);

        JacksonLocalTimeDeserializer jacksonLocalTimeDeserializer = new JacksonLocalTimeDeserializer();
        jacksonLocalTimeDeserializer.read = read;
        jacksonLocalTimeDeserializer.langCd = CookieContextHolder.getLangCd();
        simpleModule.addDeserializer(LocalTime.class, jacksonLocalTimeDeserializer);

        JacksonLocalDateTimeDeserializer jacksonLocalDateTimeDeserializer = new JacksonLocalDateTimeDeserializer();
        jacksonLocalDateTimeDeserializer.read = read;
        jacksonLocalDateTimeDeserializer.langCd = CookieContextHolder.getLangCd();
        simpleModule.addDeserializer(LocalDateTime.class, jacksonLocalDateTimeDeserializer);

        objectMapper.registerModule(simpleModule);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature());
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);

        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategies.LowerCamelCaseStrategy());

        return objectMapper;
    }

}
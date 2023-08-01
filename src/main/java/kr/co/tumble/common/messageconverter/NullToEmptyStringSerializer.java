package kr.co.tumble.common.messageconverter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Jackson의 경우 기본적으로 Json Data 매핑시 필드는 설정되었지만
 * 값이 없을 경우 null로 표현되기 때문에 null일 경우 빈공백("")으로 표시해주도록 설정
 */
public class NullToEmptyStringSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString("");
    }
}
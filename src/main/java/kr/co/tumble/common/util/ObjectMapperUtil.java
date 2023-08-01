package kr.co.tumble.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import kr.co.tumble.common.constant.TumbleConstants;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;

/**
 * ObjectMapperUtil Class
 */
@Slf4j
public class ObjectMapperUtil {

    private ObjectMapperUtil() {}

    private static final SimpleDateFormat[] DATE_FORMATS = {
            TumbleConstants.SIMPLE_DATE_FORMAT_YYYYMMDDHHMISS_WITH_DASH_DELIM.get(),
            TumbleConstants.SIMPLE_DATE_FORMAT_YYYYMMDDHHMISS_WITH_DELIM.get(),
            TumbleConstants.SIMPLE_DATE_FORMAT_YYYYMMDD_WITH_DELIM.get(),
            TumbleConstants.SIMPLE_DATE_FORMAT_YYYYMMDD_WITH_DASH_DELIM.get(),
            TumbleConstants.SIMPLE_DATE_FORMAT_YYYYMMDDHHMISS.get(),
            TumbleConstants.SIMPLE_DATE_FORMAT_YYYYMMDD.get()
    };

    public static <T> T treeToValue(ObjectMapper objectMapper, JsonNode node, Class<T> clazz) {
        try {
            return objectMapper.treeToValue(node, clazz);
        } catch (Exception e) {
            log.debug(e.getMessage() ,e);
        }

        T t = null;
        for (SimpleDateFormat dateFormat : DATE_FORMATS) {
            try {
                objectMapper.setDateFormat(dateFormat);
                t = objectMapper.treeToValue(node, clazz);

                break;
            } catch (Exception e) {
                log.debug(e.getMessage() ,e);
            }
        }

        objectMapper.setDateFormat(new StdDateFormat());
        return t;
    }

}
package kr.co.tumble.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.tumble.common.annotation.EmptyToNull;
import kr.co.tumble.common.entity.BaseCommonEntity;
import kr.co.tumble.common.messageconverter.CustomObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * ReflectionUtil Class
 */
@Slf4j
public class ReflectionUtil {

    private ReflectionUtil() {}

    public static Field[] getDeclaredFieldsAll(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = type;

        while (currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }

        return fields.toArray(new Field[] {});
    }

    public static Field getDeclaredField(Class<?> type, String fieldName) {
        Class<?> currentType = type;
        Field resultField = null;

        while (!Object.class.equals(currentType)) {
            try {
                resultField = currentType.getDeclaredField(fieldName);
                break;
            } catch (Exception e) {
                log.trace(e.getMessage(), e);
                currentType = currentType.getSuperclass();
            }
        }

        return resultField;
    }

    /**
     * 해당 필드에 @EmptyToNull 선언
     * Entity Field empty String => Null 변환
     * @param targetObj
     * @param <T>
     */
    public static <T> void convertAnnotationEmptyToNull(T targetObj) {
        if (targetObj == null) {
            return;
        }

        ReflectionUtils.doWithFields(targetObj.getClass(),
                field -> {
                    Annotation annotation = field.getAnnotation(EmptyToNull.class);
                    Object value;
                    	try {
							value = BeanUtils.getProperty(targetObj, field.getName());
						} catch (InvocationTargetException | NoSuchMethodException e) {
							value = null;
						}
                    
                    if (value == null) {
                        return;
                    }

                    if (annotation != null && field.getType() == String.class && value.toString().trim().equals("")) {
                        try {
                            BeanUtils.setProperty(targetObj, field.getName(), null);
                        } catch (InvocationTargetException e) {
                            log.info("", e);
                        }
                    }

                    if (BaseCommonEntity.class.isAssignableFrom(field.getType())) {
                        convertAnnotationEmptyToNull(value);
                    }

                    if (field.getType() == List.class) {
                        ((List<?>) value).iterator().forEachRemaining(ReflectionUtil::convertAnnotationEmptyToNull);
                    }
                });
    }

    public static Map<String, Object> convertToMap(Object obj) throws IllegalArgumentException, SecurityException {
        ObjectMapper objectMapper = CustomObjectMapper.get();

    	if (obj == null) {
            return Collections.emptyMap();
        }

        @SuppressWarnings("unchecked")
		Map<String, Object> convertMap = objectMapper.convertValue(obj, Map.class);

        return convertMap;
    }

    public static <T> T convertToValueObject(Map<String, Object> map, Class<T> type) {
        ObjectMapper objectMapper = CustomObjectMapper.get();
    	
    	if (type == null) {
    		throw new NullPointerException("can't find Class.");
    	}
    	
		T instance = objectMapper.convertValue(map, type);

    	return instance;
    }

    public static List<Map<String, Object>> convertToMaps(List<?> list)
            throws IllegalArgumentException, SecurityException {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> convertList = new ArrayList<>();

        for (Object obj : list) {
            convertList.add(ReflectionUtil.convertToMap(obj));
        }
        return convertList;
    }

    public static <T> List<T> convertToValueObjects(List<Map<String, Object>> list, Class<T> type) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> convertList = new ArrayList<>();

        for (Map<String, Object> map : list) {
            convertList.add(ReflectionUtil.convertToValueObject(map, type));
        }
        return convertList;
    }
    
    public static <T> T converToValueObject(Class<?> type, Map<String, ?> map) {
        ObjectMapper objectMapper = CustomObjectMapper.get();
    	
    	if (type == null) {
    		throw new NullPointerException("can't find Class.");
    	}
    	
        @SuppressWarnings("unchecked")
		T instance = (T) objectMapper.convertValue(map, type);

    	return instance;
    }

}
package kr.co.tumble.common.annotation;

import java.lang.annotation.*;

/**
 * GlobalResponseBodyAdvice 용 커스텀 어노테이션
 * ResponseBodyAdvice 사용하지 않음
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisableResponseBodyAdvice {

}
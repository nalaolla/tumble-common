package kr.co.tumble.common.annotation;

import java.lang.annotation.*;

/**
 * EmptyToNull 커스텀 어노테이션
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EmptyToNull {

}
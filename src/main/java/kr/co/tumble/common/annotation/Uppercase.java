package kr.co.tumble.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Uppercase 커스텀 어노테이션
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
public @interface Uppercase {

}
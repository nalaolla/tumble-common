package kr.co.tumble.common.annotation;

import java.lang.annotation.*;

/**
 * Excel 커스텀 어노테이션
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Excel {

    String filedName() default "";
    int sort() default Integer.MAX_VALUE;
    boolean visible() default true;

}

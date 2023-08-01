package kr.co.tumble.common.annotation;


import kr.co.tumble.common.util.masking.AbstractMasker;
import kr.co.tumble.common.util.masking.Masker;

import java.lang.annotation.*;

/**
 * MaskingFormat 커스텀 어노테이션
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MaskingFormat {

    String regexPattern() default "^(.*)";
    String replacePattern() default "$1";
    Class<? extends Masker> maskerClass() default AbstractMasker.class;

}
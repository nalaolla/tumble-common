package kr.co.tumble.common.annotation;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * ExcelDownLoad 커스텀 어노테이션
 */
@Transactional
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelDownLoad {

}

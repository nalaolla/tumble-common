package kr.co.tumble.common.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * CheckDateValidator Class
 */
public class CheckDateValidator implements ConstraintValidator<CheckDateFormat, String> {

    private String pattern;

    @Override
    public void initialize(CheckDateFormat constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        if ( object == null ) {
            return true;
        }

		DateFormat dateFormat = new SimpleDateFormat(pattern);
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(object);
			return true;
		} catch (java.text.ParseException e) {
			return false;
		}
    }
}
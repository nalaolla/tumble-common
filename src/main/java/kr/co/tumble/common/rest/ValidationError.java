package kr.co.tumble.common.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.validation.FieldError;

/**
 * ValidationError 객체
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class ValidationError {

	@Schema(example = "field")
	private String field;
	@Schema(example = "field message")
	private String message;

	@Builder
	public ValidationError(String field, String message) {
		this.field = field;
		this.message = message;
	}

	public static ValidationError of(final FieldError fieldError) {
		return ValidationError.builder()
				.field(fieldError.getField())
				.message(fieldError.getDefaultMessage())
				.build();
	}

}
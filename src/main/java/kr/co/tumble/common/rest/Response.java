package kr.co.tumble.common.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 공통 Response 객체
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class Response<T> {

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime timestamp = LocalDateTime.now();

	@Schema(example = "0000")
	private String code = "0000";

	@Schema(example = "result message")
	private String message = "";

	@Schema(example = "process check")
	private Boolean isProcess;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T payload;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Boolean error = null;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<ValidationError> errors = new ArrayList<>();

	@Builder
	public Response(String code, String message, T payload, Boolean error, List<ValidationError> errors, Boolean isProcess) {
		if (code != null) {
			this.code = code;
		}

		this.message = message;
		this.payload = payload;
		this.error = error;
		this.isProcess = isProcess;

		if (errors != null && !errors.isEmpty()) {
			this.errors = errors;
		}
	}

	public Response<Object> setErrorResponse(String code, String message, Boolean isProcess) {
		return Response.builder()
				.code(code)
				.message(message)
				.isProcess(isProcess)
				.error(true)
				.build();
	}

}

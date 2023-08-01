package kr.co.tumble.common.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ErrorCode Class
 */
@Getter
@Setter
@NoArgsConstructor
public class ErrorCode {

	private String code;
	private String message;
	private int httpStatus;
	private Boolean isProcess;

	@Builder
	public ErrorCode(String code, String message, int httpStatus, boolean isProcess) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
		this.isProcess = isProcess;
	}
}

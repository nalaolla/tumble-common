package kr.co.tumble.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * CommonAppError
 */
@Getter
@AllArgsConstructor
public enum CommonAppError implements AppError {

	// --------- Common Code
	// OK 200
	SUCCESS("0000", "common.message.success", "common.message.success"),
	// INTERNAL_SERVER_ERROR 500
	FAIL("9999", "common.message.fail", "common.message.fail"),
	// INTERNAL_SERVER_ERROR 500
	UNKNOWN("9000", "common.error.unknown", "common.error.unknown"),

	// BAD_REQUEST 9400
	EMPTY_PARAMETER("9001", "common.error.emptyParameter", "common.error.emptyParameter"),
	INVALID_PARAMETER("9002", "common.error.invalidParameter", "common.error.invalidParameter"),
	INSERT_PARAMETER("9003", "common.error.insertParameter", "common.error.insertParameter"),
	DUPLICATE_DATA("9004", "common.error.duplicateData", "common.error.duplicateData"),
	INVALID_FILE("9005", "common.error.invalidFile", "common.error.invalidFile"),
	UPLOAD_FAIL("9006", "common.error.uploadFail", "common.error.uploadFail"),
	VALIDATION_EXCEPTION("9007", "common.error.validationParameter", "common.error.validationParameter"),
	BINDING_ERROR("9008", "common.error.bindingError", "common.error.bindingError"),
	BINDING_ERROR_NOT_NULL("9009", "common.error.bindingErrorNotNull", "common.error.bindingErrorNotNull"),

	// UNAUTHORIZED 9401
	REQUIRED_LOGIN("9100", "common.error.requiredLogin", "common.error.requiredLogin"),
	NEED_LOGIN("9101", "common.error.needLogin", "common.error.needLogin"), // 로그인 필요
	FAIL_DELETE_TOKEN("9102", "common.error.failDeleteToken", "common.error.failDeleteToken"),

	// FORBIDDEN 9403
	NOT_AUTHORIZED("9300", "common.error.notAuthorized", "common.error.notAuthorized"),
	DISPLAY_LIMIT("9301", "common.error.displayLimit", "common.error.displayLimit"),
	INVALID_TOKEN("9302", "common.error.invalidToken", "common.error.invalidToken"),
	FAIL_TOKEN("9303", "common.error.failToken", "common.error.failToken"),

	// NOT_FOUND 9404
	DATA_NOT_FOUND("9400", "common.error.dataNotFound", "common.error.dataNotFound");
	// Common Code ---------

	private final String code;
	private final String messageKey;
	private final String boMessageKey;

}

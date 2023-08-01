package kr.co.tumble.common.exception;

/**
 * AppError interface
 */
public interface AppError {
	String getCode();
	String getMessageKey();
	String getBoMessageKey();
	default boolean getIsProcess() {
		return false;
	}
}

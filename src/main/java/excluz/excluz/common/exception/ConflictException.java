package excluz.excluz.common.exception;

import excluz.excluz.common.exception.error.ErrorCode;

public class ConflictException extends CustomRuntimeException {
	public ConflictException(ErrorCode errorCode) {
		super(errorCode);
	}
}

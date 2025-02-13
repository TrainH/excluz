package excluz.excluz.common.exception;

import excluz.excluz.common.exception.error.ErrorCode;

public class ForbiddenException extends CustomRuntimeException {
	public ForbiddenException(ErrorCode errorCode) {
		super(errorCode);
	}
}

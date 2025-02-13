package excluz.excluz.common.exception;

import excluz.excluz.common.exception.error.ErrorCode;

public class UnauthorizedException extends CustomRuntimeException {
	public UnauthorizedException(ErrorCode errorCode) {
		super(errorCode);
	}
}

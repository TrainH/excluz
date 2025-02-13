package excluz.excluz.common.exception;

import excluz.excluz.common.exception.error.ErrorCode;

public class BadRequestException extends CustomRuntimeException {
	public BadRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}

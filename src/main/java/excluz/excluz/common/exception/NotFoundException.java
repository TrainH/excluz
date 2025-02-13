package excluz.excluz.common.exception;

import excluz.excluz.common.exception.error.ErrorCode;

public class NotFoundException extends CustomRuntimeException{
	public NotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}

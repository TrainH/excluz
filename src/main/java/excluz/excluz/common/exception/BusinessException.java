package excluz.excluz.common.exception;

import excluz.excluz.common.exception.error.ErrorCode;

public class BusinessException extends CustomRuntimeException {
	public BusinessException(ErrorCode errorCode) {
		super(errorCode);
	}
}

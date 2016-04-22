package com.baidu.dsp.common.exception;

import com.baidu.dsp.common.constant.ErrorCode;
import com.baidu.dsp.common.constant.ModuleCode;
import com.baidu.dsp.common.exception.base.RuntimeGlobalException;

/**
 * 验证错误
 *
 */
public class ValidationException extends RuntimeGlobalException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String exceptionMessage) {
        super(ErrorCode.DEFAULT_ERROR, exceptionMessage);
    }

    @Override
    public ModuleCode getModuleCode() {
        return ModuleCode.VALIDATION;
    }

    public ValidationException(String exceptionMessage, Throwable throwable) {

        super(ErrorCode.DEFAULT_ERROR, exceptionMessage, throwable);
    }

}

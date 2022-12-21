package com.sh.loginpratice.rest_security_jwt_exception.exception;

import com.sh.loginpratice.rest_security_jwt_exception.exception.type.BaseExceptionType;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException{
    private final BaseExceptionType baseExceptionType;

    public BizException(BaseExceptionType baseExceptionType){
        super(baseExceptionType.getMessage());
        this.baseExceptionType = baseExceptionType;
    }

}

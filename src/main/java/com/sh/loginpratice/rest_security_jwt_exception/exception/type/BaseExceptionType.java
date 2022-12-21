package com.sh.loginpratice.rest_security_jwt_exception.exception.type;

import org.springframework.http.HttpStatus;

public interface BaseExceptionType {
    String getErrorCode();
    String getMessage();
    HttpStatus getHttpStatus();
}
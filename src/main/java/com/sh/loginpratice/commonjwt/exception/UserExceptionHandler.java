package com.sh.loginpratice.commonjwt.exception;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class UserExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> makeCookie(Exception e){
        log.error("jwt token expired", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED); // 401 에러!!! -> 권한 없음 refresh 토큰 발급 필요

    }

}

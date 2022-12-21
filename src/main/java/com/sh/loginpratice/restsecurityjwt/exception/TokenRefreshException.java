package com.sh.loginpratice.restsecurityjwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // 403에러, 리프레쉬 토큰 or 권한이 없을 때
public class TokenRefreshException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public TokenRefreshException(String token, String message) {
        super(String.format("Failed for [%s]: %s", token, message));
    }
}

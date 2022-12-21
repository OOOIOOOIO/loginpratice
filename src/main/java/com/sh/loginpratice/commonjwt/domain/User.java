package com.sh.loginpratice.commonjwt.domain;

import lombok.*;

@Getter
@NoArgsConstructor // 기본 생성자, requestbody 필요
@AllArgsConstructor // setter 대신 @Builder와 사용
@Builder
public class User {
    private String email;
    private String password;
    private String authToken; // 사용자 인증 정보 토큰
    private String refreshToken; // authToken 갱신을 위한 토큰


}

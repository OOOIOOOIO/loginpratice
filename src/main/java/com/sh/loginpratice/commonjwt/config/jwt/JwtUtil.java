package com.sh.loginpratice.commonjwt.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * application.properties에 선언된 속성들을 사용하며
 * 토큰을 만드는 부분
 * 토큰의 검증 및 내용 확인 부분으로 구성된다.
 *
 */
@Slf4j
@Component
public class JwtUtil {
    // application.properties 에 사용된 값 주입, 비밀번호에 덧붙이는 값, signature에서 사용
    @Value("${jwt.salt}")
    private String salt;

    @Value("${jwt.expireMin}")
    private Long expireMin;

    /**
     * 토큰 생성
     * @param email
     * @return
     */
    public String createAuthToken(String email) {
        return create(email, "authToken", expireMin);
    }

    /**
     * 로그인 성공 시 사용자 정보를 기반으로 JWTToken을 생성하여 반환한다.
     * @param email
     * @param subject
     * @param expireMin
     * @return
     */
    public String create(String email, String subject, long expireMin) {
        // 토큰 생성
        final JwtBuilder builder = Jwts.builder();

        // Header 설정
        builder.setHeaderParam("typ", "JWT");
        builder.setHeaderParam("alg", "HS256");

        // Payload 설정
        builder.setSubject(subject) // 제목 설정
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expireMin)); // 유효기간 설정, expireMin = 2

        // 담고 싶은 정보 설정
        if (email != null) {
            builder.claim("user", email);
        }

        // signature 설정, 암호화
//        builder.signWith(SignatureAlgorithm.HS256, salt.getBytes());
        Key key = Keys.hmacShaKeyFor("qwertyuiopasdfghjklzxcvbnmqwerty".getBytes());
        builder.signWith(key, SignatureAlgorithm.HS256);

        // 직렬화 처리로 마무리
        final String jwt = builder.compact();
        log.info("토큰 발행 : {}", jwt);

        return jwt;
    }

    /**
     * JWT 토큰을 분석해서 필요한 정보를 반환한다.
     * 토큰에 문제가 있다면 Runtime Exception을 던진다.
     * @param jwt
     * @return
     */
    public Map<String, Object> checkAndGetClaims(String jwt) {
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(salt.getBytes())
                .parseClaimsJws(jwt);
        log.trace("claims : {}", claims);

        return claims.getBody();
    }

}

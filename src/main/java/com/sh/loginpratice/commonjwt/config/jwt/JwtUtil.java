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
    // application.yml 에 사용된 값 주입, 비밀번호에 덧붙이는 값, signature에서 사용
    @Value("${jwt.secret}")
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

        // Payload 설정(등록된 클레임)
        builder.setSubject(subject) // sub : 제목 설정(authToken)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expireMin)); // exp : 유효기간 설정, expireMin = 2( 1000 * 60 * 2 == (1000(1초) * 60(1분)) * 2 => 2분)

        // Payload 담고 싶은 정보 설정(비공개 클레임)
        if (email != null) {
            builder.claim("user", email);
        }

        // signature 설정, 암호화
        Key key = Keys.hmacShaKeyFor(salt.getBytes());
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
     * @Throws
     * parseClaimsJws Throws:
     *      UnsupportedJwtException – if the claimsJws argument does not represent an Claims JWS
     *      MalformedJwtException – if the claimsJws string is not a valid JWS
     *      SignatureException – if the claimsJws JWS signature validation fails
     *      ExpiredJwtException – if the specified JWT is a Claims JWT and the Claims has an expiration time before the time this method is invoked.
     *      IllegalArgumentException – if the claimsJws string is null or empty or only whitespace
     * @return
     *
     */
    public Map<String, Object> checkAndGetClaims(String jwt) {

        // JWS란 서명된 JWT이다. ~~~.~~~.~~~ 형태를 JWS라고 한다!
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(salt.getBytes()) // signature를 secrete key로 설정했는지, publickey로 설정했는지 확인! 나는 secret key로 설정
                .build()
                .parseClaimsJws(jwt); // 여기서 Runtime Exception이 던져진다.

        log.trace("claims : {}", claims);

        return claims.getBody();
    }

    /**
     * Refresh Token 생성
     * 인증을 위한 정보는 유지하지 않고 유효기간을 auth-token의 5배로 잡았다.
     * @return
     */
    public String createRefreshToken(){
        return create(null, "refreshToken", expireMin * 5);
    }

}

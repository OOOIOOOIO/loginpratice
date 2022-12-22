package com.sh.loginpratice.websecurityjwt.config.jwt;

import com.sh.loginpratice.websecurityjwt.domain.User;
import com.sh.loginpratice.websecurityjwt.service.security.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expireMin}")
    private Long expireMin;
    @Value("${jwt.cookieName}")
    private String jwtCookie;
    @Value("${jwt.refreshCookieName}")
    private String jwtRefreshCookie;
    

    /**
     * 쿠키에 jwt 넣어서 생성하기
     * @param userPrincipal
     * @return
     */
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());

//        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt)
//                .path("/api") // Cookie 헤더를 전송하기 위하여 요청되는 URL 내에 반드시 존재해야 하는 URL 경로
//                .maxAge(24 * 60 * 60)
//                .httpOnly(true) //  Cross-site 스크립팅 공격을 방지하기 위한 옵션
//                .build();
//
//        return cookie;
        return generateCookie(jwtCookie, jwt, "/api");
    }

    /**
     * jwt 쿠키 생성
     * @param user
     * @return
     */
    public ResponseCookie generateJwtCookie(User user) {
        String jwt = generateTokenFromUsername(user.getUsername());

        return generateCookie(jwtCookie, jwt, "/api");
    }

    /**
     * 리프레쉬 jwt 쿠키 생성
     * @param refreshToken
     * @return
     */
    public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
        return generateCookie(jwtRefreshCookie, refreshToken, "/api/auth/refreshtoken");
    }

    /**
     * 쿠키에서 jwt 가져오기
     * @param request
     * @return
     */
    public String getJwtFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtCookie);
    }

    /**
     * 리프레쉬 쿠키에서 jwt 조회
     * @param request
     * @return
     */
    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtRefreshCookie);
    }

    /**
     * 쿠키 삭제
     * @return
     */
    public ResponseCookie getCleanJwtCookie() {
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).path("/api").build();
        return cookie;
    }

    /**
     * 리프레쉬 쿠키 삭제
     * @return
     */
    public ResponseCookie getCleanJwtRefreshCookie() {
        ResponseCookie cookie = ResponseCookie.from(jwtRefreshCookie, null).path("/api/auth/refreshtoken").build();
        return cookie;
    }



    /**
     * JWT 헤더 이름 조회
     * 여기서는 username을 헤더이름으로 했네
     * @param token
     * @return
     */
    public String getUserNameFromJwtToken(String token) {
//        return Jwts.parser().setSigningKey(salt).parseClaimsJws(token).getBody().getSubject();
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes()) // signature를 secrete key로 설정했는지, publickey로 설정했는지 확인! 나는 secret key로 설정
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     *  JWT 토큰 검사
     * @param authToken
     * @return
     */
    public boolean validateJwtToken(String authToken) {
        try {
//            Jwts.parser().setSigningKey(salt).parseClaimsJws(authToken);
            Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes()) // signature를 secrete key로 설정했는지, publickey로 설정했는지 확인! 나는 secret key로 설정
                    .build()
                    .parseClaimsJws(authToken);  // 여기서 Runtime Exception이 던져진다.

            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    /**
     * JWT 토큰 생성
     * subject에 username 넣음
     * @param username
     * @return
     */
    public String generateTokenFromUsername(String username) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject(username) // 요게 포인트
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expireMin))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 쿠키 생성
     * @param name
     * @param value
     * @param path
     * @return
     */
    private ResponseCookie generateCookie(String name, String value, String path) {
        ResponseCookie cookie = ResponseCookie
                .from(name, value)
                .path(path)
                .maxAge(24 * 60 * 60)
                .httpOnly(true)
                .build();

        return cookie;
    }

    /**
     * 이름으로 쿠키값 조회
     * @param request
     * @param name
     * @return
     */
    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }
}
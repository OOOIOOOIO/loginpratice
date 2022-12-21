package com.sh.loginpratice.commonjwt.interceptor;

import com.sh.loginpratice.commonjwt.config.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String authToken = request.getHeader("jwt-auth-token"); // 헤더에서 token 꺼내기

        log.info("경로 : {}, 토큰 : {}", request.getServletPath(), authToken);

        if (authToken != null) {
            jwtUtil.checkAndGetClaims(authToken); // 토큰 확인
            return true;
        }
        else{
            throw new RuntimeException("인증 토큰이 없습니다.");
        }
    }
}

package com.sh.loginpratice.commonjwt.service;

import com.sh.loginpratice.commonjwt.config.jwt.JwtUtil;
import com.sh.loginpratice.commonjwt.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtUtil jwtUtil;

    /**
     * repository를 통해 원래 DB에 저장, 여기선 그냥 Map에 저장
     */
    Map<String, String> refreshTokens = new HashMap<>();

    /**
     * 로그인 시 토큰 발행, UserDTO에 담아서 return
     * @param email
     * @param password
     * @return
     */
    public User signIn(String email, String password) {
        // repository에서 가져와야할 user 정보
        String testEmail = "polite159@gmail.com";
        String testPassword = "1234";

        // 로그인 성공
        if(email.equals(testEmail) && password.equals(testPassword)){
            String authToken = jwtUtil.createAuthToken(testEmail);

            // refresh token DB에 저장
            saveRefreshToken(email, authToken);

            return User.builder()
                    .email(email)
                    .authToken(authToken)
                    .build();
        }
        else{
            throw new RuntimeException("로그인 실패!");
        }
    }

    /**
     * 사용자 Refresh Token 저장
     * @param email
     * @param refreshToken
     */
    private void saveRefreshToken(String email, String refreshToken) {

        // 원래 repository를 통해 DB에 저장! UserDTO에 필드 있음
        refreshTokens.put(email, refreshToken);
    }

    /**
     * 사용자 refresh Token 가져오기
     * @param email
     * @return
     */
    public String getRefreshToken(String email) {
        
        // 원래 repository를 통해 DB에서 가져옴
        return refreshTokens.get(email);
    }


    /**
     * logout 시 refresh token 삭제!
     * @param email
     */
    public void removeRefreshToken(String email) {
        refreshTokens.remove(email);
    }


    /**
     * Test용
     * @return
     */
    public String getServerInfo(){
        return String.format("현재 시각 : %s", new Date());
    }
    
}

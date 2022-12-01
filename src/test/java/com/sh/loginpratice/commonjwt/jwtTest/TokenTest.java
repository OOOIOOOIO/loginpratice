package com.sh.loginpratice.commonjwt.jwtTest;

import com.sh.loginpratice.commonjwt.config.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class TokenTest {
    @Autowired
    JwtUtil jwtUtil;

    @Test
    public void tokenGenerateTest() throws Exception{
        //given
        String email = "polite159@gmail.com";
        //when
        String token = jwtUtil.createAuthToken(email);
        //then
        assertNotNull(token);
        System.out.println(token);
        /*
        1차
        eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhdXRoVG9rZW4iLCJleHAiOjE2Njk4NzEyNjAsInVzZXIiOiJwb2xpdGUxNTlAZ21haWwuY29tIn0.uZjOYQaMOudpMLahVvb2kpsuoTri3v6-Nxr3ifXmk-E

        2차
        eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhdXRoVG9rZW4iLCJleHAiOjE2Njk4NzE0MTksInVzZXIiOiJwb2xpdGUxNTlAZ21haWwuY29tIn0.LQbo8VHInuOSjvZC0b8Vtw0cddY4zMueEqj-OugqXac

        확인 => header, payload는 같지만 signature는 바뀐다.
         */
    }

    @Test
    public void checkTokenTest() throws Exception{
        //given
        String email = "polite159@gmail.com";
        //when
        String token = jwtUtil.createAuthToken(email);
        Map<String, Object> payload = jwtUtil.checkAndGetClaims(token);

        //then
        assertEquals(payload.get("sub"), "authToken");
        assertEquals(payload.get("user"), email);
    }

    /**
     * 잘못된 토큰 정보를 넘겼을 경우 MalformedJwtException 발생!
     * @throws Exception
     */
    @Test
    public void wrongTokenTest() throws Exception{
        //given
        String fakeToken = "fakeToken";
        //when-then
        assertThrows(MalformedJwtException.class, () -> jwtUtil.checkAndGetClaims(fakeToken));
    }

    /**
     * 형식은 적합하지만 유효기간이 지난 토큰을 사용할 경우 ExpiredJwtException 발생!
     * 이는 이후 RefreshToken을 생성하는 근거가 된다.
     * @throws Exception
     */
    @Test
    public void expiredTest() throws Exception{
        //given
        String fakeToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhdXRoVG9rZW4iLCJleHAiOjE2Njk4NzEyNjAsInVzZXIiOiJwb2xpdGUxNTlAZ21haWwuY29tIn0.uZjOYQaMOudpMLahVvb2kpsuoTri3v6-Nxr3ifXmk-E";
        //when-then
        assertThrows(ExpiredJwtException.class, () -> jwtUtil.checkAndGetClaims(fakeToken));
    }
}

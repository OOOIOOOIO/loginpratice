package com.sh.loginpratice.commonjwt.jwtTest;

import com.sh.loginpratice.commonjwt.config.jwt.JwtUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}

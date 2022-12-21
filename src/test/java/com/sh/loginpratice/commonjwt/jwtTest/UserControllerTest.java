package com.sh.loginpratice.commonjwt.jwtTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sh.loginpratice.commonjwt.config.jwt.JwtUtil;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mock;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void loginTest() throws Exception{
        //given
        // json body 만들기
        Map<String, String> map = new HashMap<>();
        map.put("email", "polite159@gmail.com");
        map.put("password", "1234");
        String content = new ObjectMapper().writeValueAsString(map);

        String url = "/users/login";

        //when-then
        mock.perform(MockMvcRequestBuilders.post(url)
                .contentType("application/json")
                .content(content))
                .andExpect(status().isAccepted()) // MockHttpServletResponse가 202를 반환한다.
                .andExpect(jsonPath("$.sub", CoreMatchers.equalTo("authToken")))
                .andExpect(jsonPath("$.user", CoreMatchers.equalTo(map.get("email"))));
    }

    @Test
    public void getInfoSuccessTest() throws Exception{
        //given
        String token = jwtUtil.createAuthToken("polite159@gmail.com");

        //when-then
        mock.perform(MockMvcRequestBuilders.get("/users/info")
                        .header("jwt-auth-token", token))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.info", CoreMatchers.startsWithIgnoringCase("현재")));

    }


}

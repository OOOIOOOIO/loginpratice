package com.sh.loginpratice.commonjwt.controller;

import com.sh.loginpratice.commonjwt.config.jwt.JwtUtil;
import com.sh.loginpratice.commonjwt.domain.User;
import com.sh.loginpratice.commonjwt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 로그인시 토큰 정보 리턴
     * @param user(email, password)
     * @return
     */
    @PostMapping(value = "/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        User loginUser = userService.signIn(user.getEmail(), user.getPassword());

        Map<String, Object> result = new HashMap<>();

        result.put("jwt-auth-token", loginUser.getAuthToken());

        Map<String, Object> info = jwtUtil.checkAndGetClaims(loginUser.getAuthToken()); // 토큰 정보(sub, exp, user 포함)

        result.putAll(info);

        return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
    }

    /**
     * 토큰이 만료될 시 확인 후 토큰 생성후 토큰 정보 리턴
     * @param user(email, refreshToken)
     * @return
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();

        // refresh token이 valid(유효)한지 점검, 아니라면 Runtime Exception이 터짐
        jwtUtil.checkAndGetClaims(user.getRefreshToken());

        // DB에 저장된 refresh token의 정보가 전달된 토큰의 정보와 같은지 판단.
        if (user.getRefreshToken().equals(userService.getRefreshToken(user.getEmail()))) {

            String authToken = jwtUtil.createAuthToken(user.getEmail()); // 토큰 생성(signature만 다르다!)
            result.put("jwt-auth-token", authToken);

            Map<String, Object> info = jwtUtil.checkAndGetClaims(authToken); // 토큰 정보(sub, exp, user 포함)

            result.putAll(info);
        }

        return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
    }

    @GetMapping("/logout")
    public ResponseEntity logout(@RequestParam("email") String email){
        log.info("logout : {}", email);
        userService.removeRefreshToken(email); // refresh token 삭제

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/info")

    public ResponseEntity<Map<String, Object>> getInfo() {

        Map<String, Object> result = new HashMap<>();
        result.put("info", userService.getServerInfo());

        return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
    }


}

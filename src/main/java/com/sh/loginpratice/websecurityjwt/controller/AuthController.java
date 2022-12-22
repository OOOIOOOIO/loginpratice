package com.sh.loginpratice.websecurityjwt.controller;

import com.sh.loginpratice.websecurityjwt.config.jwt.JwtUtils;
import com.sh.loginpratice.websecurityjwt.controller.dto.request.LoginRequestDto;
import com.sh.loginpratice.websecurityjwt.controller.dto.request.SignUpRequestDto;
import com.sh.loginpratice.websecurityjwt.controller.dto.response.MessageResponseDto;
import com.sh.loginpratice.websecurityjwt.controller.dto.response.UserInfoResponseDto;
import com.sh.loginpratice.websecurityjwt.domain.ERole;
import com.sh.loginpratice.websecurityjwt.domain.RefreshToken;
import com.sh.loginpratice.websecurityjwt.domain.Role;
import com.sh.loginpratice.websecurityjwt.domain.User;
import com.sh.loginpratice.websecurityjwt.exception.TokenRefreshException;
import com.sh.loginpratice.websecurityjwt.repository.RoleRepository;
import com.sh.loginpratice.websecurityjwt.repository.UserRepository;
import com.sh.loginpratice.websecurityjwt.service.RefreshTokenService;
import com.sh.loginpratice.websecurityjwt.service.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@CrossOrigin(origins = "*", maxAge = 3600) // 60분
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    /**
     * 로그인
     *
     * 인증 { 사용자 이름, 비밀번호 }
     * 인증 개체를 사용하여 SecurityContext 업데이트
     * JWT 생성
     * 인증 개체에서 UserDetails 가져오기
     * 응답에는 JWT 및 UserDetails 데이터가 포함됩니다.
     * UsernamePasswordAuthenticationToken은 로그인 요청에서 {username, password}를 가져오고 AuthenticationManager는 이를 사용하여 로그인 계정을 인증합니다.
     *
     * AuthenticationManager has a DaoAuthenticationProvider (with help of UserDetailsService & PasswordEncoder) to validate UsernamePasswordAuthenticationToken object. If successful, AuthenticationManager returns a fully populated Authentication object (including granted authorities).
     *
     *
     *
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        // 인증
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));

        // SecurityContext에 올림
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 유저 정보 가져오기
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // jwt 쿠키 생성
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // refreshToken db 저장
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUserId());

        //refreshToken 쿠키 생성
        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

        // body에 유저 정보 반환 및 쿠키 생성
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new UserInfoResponseDto(userDetails.getUserId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles));

    }


    /**
     * 회원가입(role 필수)
     *
     * 기존 사용자 이름/이메일 확인
     * 새 사용자 생성(역할을 지정하지 않은 경우 ROLE_USER 사용)
     * UserRepository를 사용하여 사용자를 데이터베이스에 저장
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {

        // 유효성 검사
        if (userRepository.existsByUsername(signUpRequestDto.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto("ERROR : USERNAME IS ALREADY TAKEN"));
        }
        if (userRepository.existsByEmail(signUpRequestDto.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto("Error: Email is already in use!"));
        }

        // 유저 생성
        User user = new User(signUpRequestDto.getUsername(),
                signUpRequestDto.getEmail(),
                encoder.encode(signUpRequestDto.getPassword()));

        Set<String> strRoles = signUpRequestDto.getRole();

        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("ERROR : ROLE IS NOT FOUND"));

            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponseDto("USER REGISTERED SUCCESSFULLY!"));

    }

    /**
     * 로그아웃
     * 쿠키 삭제(null로 덮어 씌우기)
     */
    @PostMapping("signout")
    public ResponseEntity<?> logoutUser(){

        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principle.toString() != "anonymousUser") {
            Long userId = ((UserDetailsImpl) principle).getUserId();
            refreshTokenService.deleteByUserId(userId);
        }

        ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new MessageResponseDto("You've been signed out!"));
    }

    /**
     * 리프레쉬 토큰 발급
     */
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

        if ((refreshToken != null) && (refreshToken.length() > 0)) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);

                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .body(new MessageResponseDto("Token is refreshed successfully!"));
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken, "Refresh token is not in database!"));
        }

        return ResponseEntity.badRequest().body(new MessageResponseDto("Refresh Token is empty!"));
    }
}

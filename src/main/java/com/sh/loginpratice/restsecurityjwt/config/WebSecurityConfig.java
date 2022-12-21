package com.sh.loginpratice.restsecurityjwt.config;

import com.sh.loginpratice.restsecurityjwt.config.jwt.AuthEntryPointJwt;
import com.sh.loginpratice.restsecurityjwt.config.jwt.AuthTokenFilter;
import com.sh.loginpratice.restsecurityjwt.service.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * WebSecurityConfig 클래스는 보안 구현의 핵심 클래스이다. cors, crsf, 세션 관리, 보호 자원에 대한 규칙을 구성한다.
 * 또한 Spring Security의 기본 구성을 확장하고 커스터마이징을 할 수 있다.
 *
 * - @EnableWebSecurity는 Spring이 클래스를 찾아 글로벌 웹 보안에 자동으로 적용할 수 있도록 합니다.
 *
 * – @EnableGlobalMethodSecurity는 메소드에 대한 AOP 보안을 제공합니다.
 * @PreAuthorize, @PostAuthorize를 활성화하고 JSR-250도 지원합니다.
 * 메소드 보안 표현식의 구성에서 더 많은 매개변수를 찾을 수 있습니다.
 *
 * – WebSecurityConfigurerAdapter 인터페이스에서 configure(HttpSecurity http) 메서드를 재정의합니다.
 * Spring Security에 CORS 및 CSRF 구성 방법, 모든 사용자의 인증 여부, 필터(AuthTokenFilter) 및 작동(UsernamePasswordAuthenticationFilter 전 필터),
 * 예외 처리기 선택(AuthEntryPointJwt)을 알려줍니다. .
 *
 * – Spring Security는 사용자 세부 정보를 로드하여 인증 및 권한 부여를 수행합니다.
 * 따라서 구현해야 하는 UserDetailsService 인터페이스가 있습니다.
 *
 * – UserDetailsService의 구현은 AuthenticationManagerBuilder.userDetailsService() 메서드를 통해
 * DaoAuthenticationProvider를 구성하는 데 사용됩니다.
 *
 * – DaoAuthenticationProvider를 위한 PasswordEncoder도 필요합니다. 지정하지 않으면 일반 텍스트를 사용합니다
 *
 *
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {  // extends WebSecurityConfigurerAdapte, Spring 2.7.0부터 Deprecated 됨(현재 프로젝트 2.7.6)

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authenticationJwtTokenFilter;


    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
//                .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile",).permitAll()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/test/**").permitAll()
                .anyRequest().authenticated();

        http.authenticationProvider(authenticationProvider());

//        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }


}

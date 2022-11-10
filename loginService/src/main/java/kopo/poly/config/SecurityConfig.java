package kopo.poly.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    // JWT 검증을 위한 필터
    // 초기 Spring Filter를 Spring에 제어가 불가능했지만, 현재 제어 가능함
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info(this.getClass().getName() + ".PasswordEncoder Start!");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info(this.getClass().getName() + ".filterChain Start!");

        // POST 방식 전송을 위해 csrf 막기
        http.csrf().disable();

        http
                .formLogin(login -> login // 로그인 페이지 설정
                        .loginPage("/jwt/loginForm")
                        .loginProcessingUrl("/jwt/loginProc")
                        .usernameParameter("user_id") // 로그인 ID로 사용할 html의 input객체의 name 값
                        .passwordParameter("password") // 로그인 패스워드로 사용할 html의 input객체의 name 값

                        // 로그인 성공
                        .successForwardUrl("/jwt/loginSuccess") // Web MVC, Controller 사용할 때 적용 / 로그인 성공 URL
                        .failureForwardUrl("/jwt/loginFail") // Web MVC, Controller 사용할 때 적용 / 로그인 실패 URL

                )
                .logout(logout -> logout // 로그 아웃 처리
                        .logoutSuccessUrl("/")
                )

                // 세션 사용하지 않도록 설정함
                .sessionManagement(ss -> ss.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}


package kopo.poly.config;

import kopo.poly.filter.JwtAuthenticationFilter;
import kopo.poly.handler.AccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
public class SecurityConfig {

    // Access Denied 처리
    private final AccessDeniedHandler accessDeniedHandler;

    // JWT 검증을 위한 필터
    // 초기 Spring Filter를 Spring에 제어가 불가능했지만, 현재 제어 가능함
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {

        log.info(this.getClass().getName() + ".filterChain Start!");

        // POST 방식 전송을 위해 csrf 막기
        http.csrf().disable();

        // 인증정보가 없다면(로그인하지 않지 않음) 로그인 화면 이동
        http.formLogin().loginPage("/jwt/loginForm");

        // 인증정보는 있지만, 권한이 없는 경우, 에러 처리
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler);

        // stateless방식의 애플리케이션이 되도록 설정
        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        http.authorizeExchange(authz -> authz // 페이지 접속 권한 설정
                        // USER 권한
                        .pathMatchers("/notice/**").hasAnyAuthority("ROLE_USER")

                        // USER 권한
                        .pathMatchers("/user/**").hasAnyAuthority("ROLE_USER")

                        .pathMatchers("/jwt/**").permitAll()

//                        .anyExchange().authenticated() // 그외 나머지 url 요청은 인증된 사용자만 가능
                        .anyExchange().permitAll() // 그 외 나머지 url 요청은 인증 받지 않아도 접속 가능함
        );

        // Spring Cloud Security의 필터들이 실행되기 전에 JWT 검증 필터 실행
        http.addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.HTTP_BASIC);

        log.info(this.getClass().getName() + ".filterChain End!");

        return http.build();
    }
}


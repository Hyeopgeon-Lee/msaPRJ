package kopo.poly.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class RoutConfig {

    /**
     * Gateway로 접근되는 모든 요청에 대해 필터 처리
     * Order은 필더의 순서이며, 순서에 따라 실행 순서가 결정됨
     */
    @Bean
    @Order(-1)
    public GlobalFilter first() {
        return (exchange, chain) -> {
            log.info("First pre filter");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("First post filter");
            }));
        };
    }

    /**
     * Gateway로 접근되는 모든 요청에 대해 필터 처리
     * Order은 필더의 순서이며, 순서에 따라 실행 순서가 결정됨
     */
    @Bean
    @Order(0)
    public GlobalFilter second() {
        return (exchange, chain) -> {
            log.info("Second pre filter");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Second post filter");
            }));
        };
    }

    /**
     * Gateway로 접근되는 모든 요청에 대해 필터 처리
     * Order은 필더의 순서이며, 순서에 따라 실행 순서가 결정됨
     */
    @Bean
    @Order(1)
    public GlobalFilter third() {
        return (exchange, chain) -> {
            log.info("Third pre filter");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Third post filter");
            }));
        };
    }


    /**
     * Gateway로 접근되는 모든 요청에 대해 URL 요청 분리하기
     */
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/notice/**") // 공지사항
                        // 라우터 등록
                        .filters(
                                // URL별 독립적으로 저장 항목을 추가할 경우 정의함
                                f -> f.addRequestHeader("notice-request", "From API Gateway!!")
                                        .addResponseHeader("notice-response", "From API Gateway!!")

                        )
                        .uri("http://localhost:12000") // 연결될 서버 주소

                ).route(r -> r.path("/user/**") // 회원정보 확인
                        // 라우터 등록
                        .filters(
                                // URL별 독립적으로 저장 항목을 추가할 경우 정의함
                                f -> f.addRequestHeader("user-request", "From API Gateway!!")
                                        .addResponseHeader("user-response", "From API Gateway!!")
                        )
                        .uri("http://localhost:11000") // 연결될 서버 주소

                ).route(r -> r.path("/jwt/**") // 로그인, 회원가입 => 로그인이 필요하지 않는 서비스를 별로 URL로 분리
                        //라우터 등록 = > JWT 토큰 발급 및 로그인 처리 수행하는 서비스
                        .filters(
                                // URL별 독립적으로 저장 항목을 추가할 경우 정의함
                                f -> f.addRequestHeader("jwt-request", "From API Gateway!!")
                                        .addResponseHeader("jwt-response", "From API Gateway!!")
                        )
                        .uri("http://localhost:11000") // 연결될 서버 주소
                )
                .build();
    }
}


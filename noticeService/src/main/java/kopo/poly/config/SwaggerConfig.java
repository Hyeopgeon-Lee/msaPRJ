package kopo.poly.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    /**
     * Swagger 문서 설명 내용
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Notice Service")
                .description("Notice Service Description!!")
                .contact(new Contact("Prof. Hyeopgeon Lee",
                        "https://www.kopo.ac.kr/kangseo/content.do?menu=1547",
                        "hglee67@kopo.ac.kr"))
                .license("한국폴리텍대학 서울강서캠퍼스 빅데이터과 학생들은 모두 자유롭게 사용 가능")
                .version("1.0")
                .build();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30) // openApi 3.0 버전 사용
                .useDefaultResponseMessages(false) // 기본 응답 코드 노출 제외
                .select()
                .apis(RequestHandlerSelectors.basePackage("kopo.poly.controller")) // API 정의한 Controller 패키지
                .paths(PathSelectors.any()) // Controller의 모든 API를 문서화하기
                .build() // 빌드
                .apiInfo(apiInfo()); // 문서 정보 저장하기

    }


}

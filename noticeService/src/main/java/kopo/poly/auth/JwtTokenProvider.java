package kopo.poly.auth;

import io.jsonwebtoken.Jwts;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.token.access.name}")
    private String accessTokenName;

    @Value("${jwt.token.refresh.name}")
    private String refreshTokenName;

    /**
     * JWT 토큰(Access Token, Refresh Token)에서 회원 정보 추출
     *
     * @param token 토큰
     * @return 회원 아이디(ex. hglee67)
     */
    public String getUserId(String token) {

        log.info(this.getClass().getName() + ".getUserId Start!");

        String userId = CmmUtil.nvl(Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token).getBody().getSubject());
        log.info("userId : " + userId);

        log.info(this.getClass().getName() + ".getUserId End!");

        return userId;
    }

    /**
     * JWT 토큰(Access Token, Refresh Token)에서 회원 정보 추출
     *
     * @param token 토큰
     * @return 회원 아이디(ex. hglee67)
     */
    public String getUserRoles(String token) {

        log.info(this.getClass().getName() + ".getUserRoles Start!");
        String roles = CmmUtil.nvl((String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("roles"));

        log.info("roles : " + roles);

        log.info(this.getClass().getName() + ".getUserRoles End!");

        return roles;
    }

    /**
     * 쿠기에 저장된 JWT 토큰(Access Token, Refresh Token) 가져오기
     *
     * @param request   request 정보
     * @param tokenType token 유형
     * @return 쿠기에 저장된 토큰 값
     */
    public String resolveToken(HttpServletRequest request, JwtTokenType tokenType) {

        log.info(this.getClass().getName() + ".resolveToken Start!");

        String tokenName = "";

        if (tokenType == JwtTokenType.ACCESS_TOKEN) { // Access Token이라면
            tokenName = accessTokenName;

        } else if (tokenType == JwtTokenType.REFRESH_TOKEN) { // Refresh Token이라면
            tokenName = refreshTokenName;

        }

        String token = "";

        // Cookie에 저장된 데이터 모두 가져오기
        Cookie[] cookies = request.getCookies();

        if (cookies != null) { // Cookie가 존재하면, Cookie에서 토큰 값 가져오기
            for (Cookie key : request.getCookies()) {
                if (key.getName().equals(tokenName)) {
                    token = CmmUtil.nvl(key.getValue());
                    break;
                }
            }
        }

        log.info(this.getClass().getName() + ".resolveToken End!");
        return token;
    }

}

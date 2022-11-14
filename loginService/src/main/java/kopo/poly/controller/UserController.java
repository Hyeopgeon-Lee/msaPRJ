package kopo.poly.controller;

import kopo.poly.auth.JwtTokenProvider;
import kopo.poly.auth.JwtTokenType;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Controller
public class UserController {

    // JWT 객체
    private final JwtTokenProvider jwtTokenProvider;

    // 회원 서비스
    private final IUserInfoService userInfoService;

    /**
     * 본인 회원정보조회
     */
    @RequestMapping(value = "getUserInfo")
    public String getUserInfo(HttpServletRequest request, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".getUserInfo Start!");

        // Access Token 가져오기
        String token = CmmUtil.nvl(jwtTokenProvider.resolveToken(request, JwtTokenType.ACCESS_TOKEN));

        log.info("token : " + token);

        // Access Token에 저장된 회원아이디 가져오기
        String userId = CmmUtil.nvl(jwtTokenProvider.getUserId(token));

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        UserInfoDTO rDTO = userInfoService.getUserInfo(pDTO);

        if (rDTO == null) {
            rDTO = new UserInfoDTO();

        }

        // JSP에 값 전달하기
        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".getUserInfo End!");

        return "/user/userInfo";

    }

}


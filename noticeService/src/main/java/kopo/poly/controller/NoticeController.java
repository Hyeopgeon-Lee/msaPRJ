package kopo.poly.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kopo.poly.auth.JwtTokenProvider;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.service.INoticeService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/*
 * Controller 선언해야만 Spring 프레임워크에서 Controller인지 인식 가능
 * 자바 서블릿 역할 수행
 * */
@Tag(name = "공지사항", description = "공지사항 구현을 위한 API")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/notice")
@Controller
public class NoticeController {

    private final JwtTokenProvider jwtTokenProvider;

    // 공지사항 서비스
    private final INoticeService noticeService;

    @Operation(summary = "공지사항 인덱스 화면", description = "공지사항 처음 들어갈때 접속",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!")
            }
    )
    @GetMapping(value = "index")
    public String Index() {
        return "/index";

    }

    @Operation(summary = "공지사항 리스트 화면", description = "공지사항 리스트 화면 보여주기",
            parameters = {
                    @Parameter(name = "model", description = "JSP에 값을 전달하기 위한 객체")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!")
            }
    )
    @GetMapping(value = "noticeList")
    public String noticeList(ModelMap model) {

        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".noticeList start!");

        // 공지사항 리스트 가져오기
        List<NoticeDTO> rList = noticeService.getNoticeList();

        if (rList == null) {
            rList = new ArrayList<NoticeDTO>();

        }

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rList", rList);

        // 변수 초기화(메모리 효율화 시키기 위해 사용함)
        rList = null;

        // 로그 찍기(추후 찍은 로그를 통해 이 함수 호출이 끝났는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".noticeList end!");

        // 함수 처리가 끝나고 보여줄 JSP 파일명(/WEB-INF/view/notice/NoticeList.jsp)
        return "/notice/NoticeList";
    }

    @Operation(
            summary = "공지사항 상세보기", description = "공지사항 리스트에서 제목을 클릭하여 상세보기 접속",
            parameters = {
                    @Parameter(name = "model", description = "JSP에 값을 전달하기 위한 객체"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!")
            }
    )
    @GetMapping(value = "noticeInfo")
    public String noticeInfo(HttpServletRequest request, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".noticeInfo Start!");

        /*
         * 게시판 글 등록되기 위해 사용되는 form객체의 하위 input 객체 등을 받아오기 위해 사용함
         */
        String nSeq = CmmUtil.nvl(request.getParameter("nSeq")); // 공지글번호(PK)

        /*
         * ####################################################################################
         * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
         * ####################################################################################
         */
        log.info("nSeq : " + nSeq);

        /*
         * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
         */
        NoticeDTO pDTO = new NoticeDTO();
        pDTO.setNoticeSeq(Long.parseLong(nSeq));

        // 공지사항 상세정보 가져오기
        NoticeDTO rDTO = noticeService.getNoticeInfo(pDTO, true);

        if (rDTO == null) {
            rDTO = new NoticeDTO();

        }

        // AccessToken에서 회원아이디 가져오기
        String token = CmmUtil.nvl(jwtTokenProvider.resolveToken(request));

        log.info("token : " + token);

        String user_id = CmmUtil.nvl(jwtTokenProvider.getUserId(token));

        log.info("user_id : " + user_id);

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);
        model.addAttribute("token_user_id", user_id);


        log.info(this.getClass().getName() + ".noticeInfo End!");

        return "/notice/NoticeInfo";
    }

    @Operation(summary = "공지사항 수정하는 화면 이동", description = "공지사항 수정하는 화면 이동",
            parameters = {
                    @Parameter(name = "model", description = "JSP에 값을 전달하기 위한 객체"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!")
            }
    )
    @GetMapping(value = "noticeEditInfo")
    public String noticeEditInfo(HttpServletRequest request, ModelMap model) {

        log.info(this.getClass().getName() + ".noticeEditInfo Start!");

        String msg = "";

        try {

            String nSeq = CmmUtil.nvl(request.getParameter("nSeq")); // 공지글번호(PK)

            log.info("nSeq : " + nSeq);

            NoticeDTO pDTO = new NoticeDTO();

            pDTO.setNoticeSeq(Long.parseLong(nSeq));

            NoticeDTO rDTO = noticeService.getNoticeInfo(pDTO, false);
            /*
             * ###################################################################################
             * 공지사항 수정정보 가져오기(상세보기 쿼리와 동일하여, 같은 서비스 쿼리 사용함)
             * ###################################################################################
             */

            if (rDTO == null) {
                rDTO = new NoticeDTO();

            }

            // AccessToken에서 회원아이디 가져오기
            String token = CmmUtil.nvl(jwtTokenProvider.resolveToken(request));

            log.info("token : " + token);

            String user_id = CmmUtil.nvl(jwtTokenProvider.getUserId(token));

            log.info("user_id : " + user_id);

            // 조회된 리스트 결과값 넣어주기
            model.addAttribute("rDTO", rDTO);
            model.addAttribute("token_user_id", user_id);

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            log.info(this.getClass().getName() + ".NoticeUpdate end!");

            // 결과 메시지 전달하기
            model.addAttribute("msg", msg);

        }

        log.info(this.getClass().getName() + ".noticeEditInfo end!");

        return "/notice/NoticeEditInfo";
    }

    @Operation(summary = "공지사항 수정하기", description = "DB에 공지사항 상세 데이터를 수정하기 위한 요청",
            parameters = {
                    @Parameter(name = "model", description = "JSP에 값을 전달하기 위한 객체"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!")
            }
    )
    @PostMapping(value = "noticeUpdate")
    public String NoticeUpdate(HttpServletRequest request, ModelMap model) {

        log.info(this.getClass().getName() + ".noticeUpdate Start!");

        String msg = "";

        try {
            // JWT Access Token 가져오기
            String token = CmmUtil.nvl(jwtTokenProvider.resolveToken(request));

            String user_id = CmmUtil.nvl(jwtTokenProvider.getUserId(token)); // 토큰에서 추출한 회원아이디
            String nSeq = CmmUtil.nvl(request.getParameter("nSeq")); // 글번호(PK)
            String title = CmmUtil.nvl(request.getParameter("title")); // 제목
            String noticeYn = CmmUtil.nvl(request.getParameter("noticeYn")); // 공지글 여부
            String contents = CmmUtil.nvl(request.getParameter("contents")); // 내용

            log.info("user_id : " + user_id);
            log.info("nSeq : " + nSeq);
            log.info("title : " + title);
            log.info("noticeYn : " + noticeYn);
            log.info("contents : " + contents);

            NoticeDTO pDTO = new NoticeDTO();

            pDTO.setUserId(user_id);
            pDTO.setNoticeSeq(Long.parseLong(nSeq));
            pDTO.setTitle(title);
            pDTO.setNoticeYn(noticeYn);
            pDTO.setContents(contents);

            // 게시글 수정하기 DB
            noticeService.updateNoticeInfo(pDTO);

            msg = "수정되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            log.info(this.getClass().getName() + ".noticeUpdate End!");

            // 결과 메시지 전달하기
            model.addAttribute("msg", msg);

        }

        return "/notice/MsgToList";
    }

    @Operation(summary = "공지사항 삭제하기", description = "DB에 공지사항 상세 데이터를 삭제하기 위한 요청",
            parameters = {
                    @Parameter(name = "model", description = "JSP에 값을 전달하기 위한 객체"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!")
            }
    )
    @GetMapping(value = "noticeDelete")
    public String noticeDelete(HttpServletRequest request, ModelMap model) {

        log.info(this.getClass().getName() + ".noticeDelete Start!");

        String msg = "";

        try {

            String nSeq = CmmUtil.nvl(request.getParameter("nSeq")); // 글번호(PK)

            log.info("nSeq : " + nSeq);

            NoticeDTO pDTO = new NoticeDTO();

            pDTO.setNoticeSeq(Long.parseLong(nSeq));

            // 게시글 삭제하기 DB
            noticeService.deleteNoticeInfo(pDTO);

            msg = "삭제되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            log.info(this.getClass().getName() + ".noticeDelete End!");

            // 결과 메시지 전달하기
            model.addAttribute("msg", msg);

        }

        return "/notice/MsgToList";
    }

    @Operation(summary = "공지사항 등록화면", description = "공지사항 글을 작성하기 위한 화면",
            parameters = {
                    @Parameter(name = "model", description = "JSP에 값을 전달하기 위한 객체"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!")
            }
    )
    @GetMapping(value = "noticeReg")
    public String noticeReg() {

        log.info(this.getClass().getName() + ".noticeReg Start!");

        log.info(this.getClass().getName() + ".noticeReg End!");

        return "/notice/NoticeReg";
    }

    @Operation(summary = "공지사항 등록하기", description = "DB에 공지사항 등록하기 위한 요청",
            parameters = {
                    @Parameter(name = "model", description = "JSP에 값을 전달하기 위한 객체"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!")
            }
    )
    @PostMapping(value = "noticeInsert")
    public String noticeInsert(HttpServletRequest request, ModelMap model) {

        log.info(this.getClass().getName() + ".noticeInsert Start!");

        String msg = "";

        try {
            /*
             * 게시판 글 등록되기 위해 사용되는 form객체의 하위 input 객체 등을 받아오기 위해 사용함
             */
            // JWT Access Token 가져오기
            String token = CmmUtil.nvl(jwtTokenProvider.resolveToken(request));

            String user_id = CmmUtil.nvl(jwtTokenProvider.getUserId(token)); // 토큰에서 추출한 회원아이디
            String title = CmmUtil.nvl(request.getParameter("title")); // 제목
            String noticeYn = CmmUtil.nvl(request.getParameter("noticeYn")); // 공지글 여부
            String contents = CmmUtil.nvl(request.getParameter("contents")); // 내용

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("user_id : " + user_id);
            log.info("title : " + title);
            log.info("noticeYn : " + noticeYn);
            log.info("contents : " + contents);

            NoticeDTO pDTO = new NoticeDTO();

            pDTO.setUserId(user_id);
            pDTO.setTitle(title);
            pDTO.setNoticeYn(noticeYn);
            pDTO.setContents(contents);

            /*
             * 게시글 등록하기위한 비즈니스 로직을 호출
             */
            noticeService.InsertNoticeInfo(pDTO);

            // 저장이 완료되면 사용자에게 보여줄 메시지
            msg = "등록되었습니다.";


        } catch (Exception e) {

            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            log.info(this.getClass().getName() + ".noticeInsert End!");

            // 결과 메시지 전달하기
            model.addAttribute("msg", msg);

        }

        return "/notice/MsgToList";
    }

}

package kopo.poly.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "공지사항 정보 전달을 위한 DTO ", description = "공지사항 정보 전달을 위한 DTO")
@Getter
@Setter
public class NoticeDTO {

    @Schema(description = "DB의 PK, 순번")
    private Long noticeSeq;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "공지글 여부", allowableValues = {"Y", "N"})
    private String noticeYn;

    @Schema(description = "글 내용")
    private String contents;

    @Schema(description = "작성자")
    private String userId;

    @Schema(description = "조회수")
    private String readCnt;

    @Schema(description = "등록자 아이디")
    private String regId;

    @Schema(description = "등록일")
    private String regDt;

    @Schema(description = "수정자 아이디")
    private String chgId;

    @Schema(description = "수정일")
    private String chgDt;

    @Schema(description = "등록자명")
    private String userName;

}

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="kopo.poly.dto.UserInfoDTO" %>
<%@ page import="kopo.poly.util.CmmUtil" %>
<%
    UserInfoDTO rDTO = (UserInfoDTO) request.getAttribute("rDTO");

    if (rDTO == null) {
        rDTO = new UserInfoDTO();

    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원정보 보기</title>
    <link rel="stylesheet" href="/css/table.css"/>
</head>
<body>
<h2>회원정보 보기</h2>
<hr/>
<br/>
<div class="divTable minimalistBlack">
    <div class="divTableBody">
        <div class="divTableRow">
            <div class="divTableCell">아이디
            </div>
            <div class="divTableCell"><%=CmmUtil.nvl(rDTO.getUserId())%>
            </div>
        </div>
        <div class="divTableRow">
            <div class="divTableCell">이름
            </div>
            <div class="divTableCell"><%=CmmUtil.nvl(rDTO.getUserName())%>
            </div>
        </div>
        <div class="divTableRow">
            <div class="divTableCell">이메일
            </div>
            <div class="divTableCell"><%=CmmUtil.nvl(rDTO.getEmail())%>
            </div>
        </div>
        <div class="divTableRow">
            <div class="divTableCell">주소
            </div>
            <div class="divTableCell"><%=CmmUtil.nvl(rDTO.getAddr1())%>
            </div>
        </div>
        <div class="divTableRow">
            <div class="divTableCell">상세 주소
            </div>
            <div class="divTableCell"><%=CmmUtil.nvl(rDTO.getAddr2())%>
            </div>
        </div>
    </div>
</div>
</body>
</html>
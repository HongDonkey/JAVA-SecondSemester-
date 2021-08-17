<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<meta name="viewport" content="user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, width=device-width"/>
<script src="https://developers.kakao.com/sdk/js/kakao.min.js"></script>
<script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
<script src="/css/bootstrap/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="/css/bootstrap/css/bootstrap.min.css">


<style type="text/css">
input{
	width: 100%;
    height: 51px;
    margin-bottom: 10px;
    padding-left: 70px;
    display: block;
    font-size: 14px;
    font-weight: 700;
    text-align: left;
    border-radius: 5px;
}
button{
    width: 100%;
    height: 51px;
    margin-bottom: 10px;
    padding-left: 70px;
    display: block;
    font-size: 14px;
    font-weight: 700;
    text-align: left;
    border-radius: 5px;
}
button.btnF{
    color: #fff;
    background: #3b5997 url(/images/egovframework/example/bg_sns.png) 0px 0px no-repeat;
    height: 50px;
}
button.btnN{
    color: #fff;
    background: #2db400 url(/images/egovframework/example/bg_sns.png) 0px -51px no-repeat;
    height: 50px;
}
button.btnC{
    color: #000;
    background: #ffeb00 url(/images/egovframework/example/bg_sns.png) 0px -102px no-repeat;
    height: 50px;
}


}
</style>

<script>

	
	
	function loginChk(){
		var loginPw = $("#password").val();
		
		if(loginPw == ''){
			alert("비밀번호를 입력해주세요.");
			$("#password").focus();
			return false;
		}
			
	}
	
	function toMain(){
		location.href = "<c:url value='/mainList.do'/>";
	}
	
</script>

<div class="container">
	<div class="panel panel-default">
		<h3>기존 회원 로그인</h3>
		<div class="panel-heading">
			<div class="panel-body">
				<div class="infoWrap">
					<h4>${user_id }님 환영합니다.</h4>
					<form id="loginForm" name="loginForm" method="post" action="/login.do">
						<dl class="ip">
						
							<dt>비밀번호</dt>
							<dd>
								<input type="hidden" id="user_id" name="user_id" value = ${user_id }>
								<input type="password" placeholder="비밀번호를 입력하세요." id="password"
									name="password">
							</dd>
						</dl>
						<button type="submit" class="btnLog" onclick="return loginChk();">로그인</button>
					</form>
						<button type="button" class="btn btn-default" onclick="toMain();">메인으로 돌아가기</button>
					
						<div class="menuBox ntc">
							<ul>
								<li>한국폴리텍대학 스마트금융과</li>
							</ul>
						</div>
					</div>
				</div>

		</div>
	</div>
</div>
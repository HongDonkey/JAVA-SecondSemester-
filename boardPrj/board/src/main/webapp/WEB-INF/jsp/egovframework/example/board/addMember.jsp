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

function toMain(){
	location.href = "<c:url value='/mainList.do'/>";
}


function validator(){

	var newId = $("#user_id").val();
	var newNm = $("#user_name").val();
	var pw1 = $("#password").val();
	var pw2 = $("#password2").val();
	
 	if( newId == '' || newNm == '' || pw1 == ''){
		alert("필수항목을 기입해주세요.")
		return;
	}
	
	if( pw1 != pw2 ){
		alert("비밀번호가 틀립니다.")
		return;
	}
	
	document.SubmitForm.action = "<c:url value='memberAdd.do'/>";
	document.SubmitForm.submit();
}
	
</script>

<div class="container">
	<div class="panel panel-default">
		<h3>&nbsp;회원 가입</h3>
		<div class="panel-heading">
			<div class="panel-body">
				<div class="infoWrap">
					<h4>환영합니다.</h4>
					<div class = "form-group">
					<form id="SubmitForm" name="SubmitForm" method="post" action="">
						<dl class="ip">
							<dt>아이디</dt>
							<dd>
								<input type="text" class = "form-control" placeholder="아이디를 입력하세요." id="user_id"
									name="user_id">
							</dd>
						</dl>
						<dl class="ip">
							<dt>비밀번호</dt>
							<dd>
								<input type="password" class = "form-control" placeholder="비밀번호를 입력하세요." id="password"
									name="password">
							</dd>
						</dl>
						<dl class="ip">
							<dt>비밀번호 확인</dt>
							<dd>
								<input type="password" class = "form-control" placeholder="비밀번호를 입력하세요." id="password2"
									name="password2">
							</dd>
						</dl>
						<dl class="ip">
							<dt>닉네임</dt>
							<dd>
								<input type="text" class = "form-control" placeholder="닉네임을 입력하세요." id="user_name"
									name="user_name">
							</dd>
						</dl>
					</form>
						<button type="button" class="btn btn default" onclick="validator();">회원가입</button>
						<button type="button" class="btnLog" onclick="toMain();">메인으로 돌아가기</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
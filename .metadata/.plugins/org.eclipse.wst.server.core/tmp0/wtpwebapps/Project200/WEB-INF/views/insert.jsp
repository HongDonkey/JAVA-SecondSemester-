<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script type="text/javascript">
	$(document).on('click', '#s', function(event){
		var name = $("#name").val();
		var score = $("#score").val();
		if (name && score) {
			$.ajax({
	            url:'insert_api',
	            data: {"name": name, "score": score},
	            success:function(data){
	                alert(data['message']);
	            }
	        });
		} else {
			alert("데이터를 입력해주세요.");
		}
	});
</script>
</head>
<body>
	<input type="text" id="name" placeholder="이름" />
	<input type="number" id="score" placeholder="점수" />
	<button id="s">전송</button>
</body>
</html>
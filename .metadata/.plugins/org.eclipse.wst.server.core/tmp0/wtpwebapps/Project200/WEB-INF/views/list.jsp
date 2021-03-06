<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script type="text/javascript">
	$(function(){
		$.ajax({
            url:'hello_api',
            success:function(data){
                $('#name').text(data['name']);
            }
        });
		$.ajax({
            url:'list_api',
            success:function(data){
                for (var i =0; i< data.length; i++) {
                	var tr_string = '<tr>' + '<td>' + data[i]['idx'] + '</td>'
                	+ '<td>' + data[i]['name'] + '</td>'
                	+ '<td>' + data[i]['score'] + '</td>' 
                	+ '<td><button class="update">수정하기</button></td>'
                	+ '</tr>';
                	$('#tb').append(tr_string);
                }
            }
        });
	});
	$(document).on('click', '.update', function(event){
		var tr_element = $(this).parents('tr');
		var idx = tr_element.find('td').eq(0).text();
		if ($(this).text() == '수정완료'){
			var name = tr_element.find('td').eq(1).find('input').val();
			var score = tr_element.find('td').eq(2).find('input').val();
			$.ajax({
	            url:'update_api',
	            data : {'idx' : idx, 'name' : name, 'score' : score},
	            success:function(data){
	               location.reload();
	            }
	        });
		} else {
			
			var idx = tr_element.find('td').eq(0).text();
			var name = tr_element.find('td').eq(1).text();
			var score = tr_element.find('td').eq(2).text();
			tr_element.find('td').eq(1).html('<input type="text" value="' + name + '"/>');
			tr_element.find('td').eq(2).html('<input type="number" value="' + score +'"/>');
			$(this).text('수정완료');
			
		}
		
		
	});
</script>
</head>
<body>
	<p>Hello <span id="name"></span></p>
	<a href="i">데이터 입력</a>
	
	<table>
	<thead><tr><th>idx</th><th>이름</th><th>점수</th></tr></thead>
	<tbody id="tb"></tbody>
	</table>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>write</title>
<link href="/img/favicon.ico" rel="shortcut icon" type="image/x-icon">
<link href="/css/common.css" rel="stylesheet" /> 
<style>
table { width:100%;  }
  td {
     padding:5px 10px;
     text-align : center;
     &:nth-of-type(1) {
	     background: black;
	     color : white;
	     border:1px solid white;
	 } 
  }
  tr:last-child > td {
      background: white;
      border : 1px solid black; 
  }
    
  input[type="text"], input[type=number], input[type=password]  {
     width : 100%;
  }
  input[type=submit], input[type=button] {
     width : 100px;
  }
  input[name=userid] {
     width : 65%;
  }
  
  textarea {
  	width  : 100%;
  	height : 300px;
  }
  
  #table1 {  margin-bottom : 150px; }
  
  #btnAddFile {
  	width: 300px;
  }
  
</style>
</head>
<body>
	<main>
		<%@include file="/WEB-INF/include/menuspdspaging.jsp" %>
		<h2 class="h2"><b id="mname"></b>자료실 새 글 쓰기</h2>
		<form action="/Pds/Write" method="post"
			enctype="multipart/form-data" >
			
		 <input type="hidden" name="menu_id" value="${ map.menu_id }" />
		 <!-- 입력을 받지 않고 nowpage 값을 넘겨주기 위함. -->
		 <input type="hidden" name="nowpage" value="${ map.nowpage }" /> 
		  <table id="table1">
		    <tr>
		      <td><span class="red">*</span>제목</td>
		      <td>
		      	<input type="text" name="title"  />
		      </td>
		    </tr>
   		    <tr>
		      <td><span class="red">*</span>작성자</td>
		      <td><input type="text" name="writer" 
		             value="${sessionScope.login.userid}"/></td>	
		    </tr>
		    <tr>
		      <td>내용</td>
		      <td><textarea name="content"></textarea></td>
		    </tr>
		    <tr>
		      <td>파일</td>
		      <td>
		      	<div id=tdfile>
			      <input type="button" id="btnAddFile" value="파일추가(최대 100MB)" /><br>	
		      	  <input type="file" name="upfile" class="upfile" multiple /><br>		      	
		      	</div>
		      </td>
		    </tr>
		    <tr>
		      <td colspan="2">
		      	<input type="submit" value="추가" />
		      	<input type="button" value="목록" id="goList" />
		      </td>
		    </tr>
		  </table>
		</form>
		
	</main>
	<!-- JavaScript 코딩 : client validation -->
	<script>
		// 메뉴제목 출력
		const mnameEl = document.querySelector('#mname');
		let menunameEl = document.querySelector('.menu .active');
		mnameEl.innerHTML = menunameEl.innerHTML;
		
		// 목록으로 이동
		const goListEl = document.querySelector('#goList');
		goListEl.onclick = function() {
			location.href='/Pds/List?menu_id=${map.menu_id}&nowpage=${map.nowpage}';
		};
		
		// 파일 입력창 추가
		const btnAddFileEl = document.querySelector('#btnAddFile');
		const tdfileEl = document.querySelector('#tdfile');
		
		let tag = '<input type="file" name="upfile" class="upfile" multiple/><br>';
		let html = tdfileEl.innerHTML;
		
		// js 에서 실행할 때 새로 추가된 버튼은 이벤트가 한번만 작동
		// 해결 : 이벤트를 부모 Element 에 설정
		tdfileEl.addEventListener('click', function(e) {
			console.dir(e.target) //#btnAddFile 과 .upfile 구분
			if(e.target.id == 'btnAddFile') {
				html +=  tag
				tdfileEl.innerHTML = html				
			}
		}) 
		// 입력항목 체크
	</script>
</body>
</html>























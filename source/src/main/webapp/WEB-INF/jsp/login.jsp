<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>ログイン</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Zen+Maru+Gothic&display=swap" rel="stylesheet">
<link rel="stylesheet" href="css/login.css">
</head>
<body>
<div class="header">

<!-- サイドバー --> 
<%-- <jsp:include page="sidebar.jspf" flush="true|false" /> --%>
<!-- <img src="/b4/img/sidebar.png" id="toggleBtn"> -->
<!-- <link rel="stylesheet" href="/b4/css/sidebar.css"> -->
<!-- <script src="/b4/js/sidebar.js"> </script> -->
<!-- ここまで必要なjspに書き込む --> 

<h1>ここる
<img src="/b4/img/mascot.png" alt="ペンギンのここるイラスト" id="mascot">
</h1>
</div>
<form method="POST" action="/b4/LoginServlet" id="form">
<div><label>ユーザーニックネーム<br>
    <input type="text" name="userNickname" id="userNickname"></label>
</div>
<div class="password-wrapper"><label>パスワード<br>
    <input type="password" name="password" id="password">
	<img src="img/eye.png" id="togglePassword" class="toggle-eye"></label>
</div>
<%
String errorMessage = (String) request.getAttribute("errorMessage");
%>
<%if(errorMessage != null) {%>
<p class="login-error">
	<%= errorMessage %>
</p>
<%}%>
<div><input type="submit" id="login" name="submit" value="ログイン"></div>
<div><input type="submit" id="regist" name="submit" value="新規登録"></div>
</form>
	<script src="/b4/js/login.js"> </script>
	

</body>
</html>
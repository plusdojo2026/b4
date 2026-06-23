<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>ログイン</title>
<link rel="stylesheet" href="css/login.css">
</head>
<body>
<div class="header">
<h1>ここる
<img src="/b4/img/mascot.png" alt="ペンギンのここるイラスト" id="mascot">
</h1>
</div>
<form method="POST" action="/b4/LoginServlet" id="form">
<div><label>ユーザーニックネーム<br>
    <input type="text" name="userNickname" id="userNickname"></label>
</div>
<div><label>パスワード<br>
    <input type="password" name="password" id="password"></label>
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
</body>
</html>
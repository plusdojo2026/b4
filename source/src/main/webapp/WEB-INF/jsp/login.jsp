<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ログイン</title>
</head>
<body>
<div>家事提案アプリ</div>
<div>ここる</div>
<div><img src="/b4/img/mascot.png" alt="ペンギンのここるイラスト" id="mascot"></div>

<form method="POST" action="/b4/LoginServlet" id="form">
<div><label>ユーザーニックネーム<br>
    <input type="text" name="name" id="name"></label>
</div>
<div><label>パスワード<br>
    <input type="password" name="pw" id="pw"></label>
</div>
<%
String errorMessage = (String) request.getAttribute("errorMessage");
%>
<%if(errorMessage != null) {%>
<p class="login-error">
	<%= errorMessage %>
</p>
<%}%>
<div><input type="submit" id="regist" name="submit" value="新規登録"></div>
<div><input type="submit" id="login" name="submit" value="ログイン"></div>
</form>
</body>
</html>
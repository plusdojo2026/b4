<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>ログアウト</title>
	<!-- cssは新規登録のものを利用中 -->
	<link rel="stylesheet" href="/b4/css/userreg.css">
</head>

<body>
<header>
<div class="header">
	<h1>ここる
	<img src="/b4/img/mascot.png" alt="ペンギンのここるイラスト" id="mascot">
	</h1>
</div>
</header>

<body>

<p class="logout">ゆっくり休んでね♪</p>
<form method="POST" action="/b4/LogoutServlet" id="form">
<input type="submit" id="login" name="login" value="ログイン">
</form>
</body>
</html>
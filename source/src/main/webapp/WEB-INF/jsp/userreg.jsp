<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>新規登録</title>
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

<!-- メインここから -->
<main>

<form id="userreg" method="POST" action="/b4/UserRegServlet">

	<div>ユーザーニックネーム</div>
		<input type="text" id="name" name="user_nickname">
		
	<div class="password-wrapper">パスワード</div>
		<input type="password" id="password" name="password">
		<img src="img/eye.png" id="togglePassword" class="toggle-eye">
		
	<div class="password-wrapper">パスワード確認</div>
		<input type="password" id="password2" name="password2">
		<img src="img/eye.png" id="togglePassword2" class="toggle-eye">
		
	<p id="msg"> </p>
	
	
	<div>メールアドレス(任意)</div>
		<input type="text" id= "mail" name="mail_address">
	
<div><input type="submit" id="regist" name="regist" value="登録"></div>

</form>

</main>

<script src="/b4/js/userreg.js"> </script>

</body>

</html>
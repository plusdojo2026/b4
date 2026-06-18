<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<title>新規登録</title>
	<link rel="stylesheet" href="/b4/css/login.css">
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
		
	<div>パスワード</div>
		<input type="password" id="pw" name="password">
		
	<div>確認のため再度入力してください</div>
		<input type="password" id="pw2" name="pw2">
		
	<p id="msg"> </p>
	
	<div>メールアドレス(任意)</div>
		<input type="text" id= "mail" name="mail_address">
	
<div><input type="submit" name="regist" value="登録"></div>

</form>

</main>

</body>

<script>
'use strict'

document.getElementById('userreg').onsubmit = function(event){
	if(window.confirm('登録します。よろしいですか？') === false){
		event.preventDefault();
	}
    let name = document.getElementById('userreg').name.value;
    let pw = document.getElementById('userreg').pw.value;
    let pw2 = document.getElementById('userreg').pw2.value;
    if (name === ''){
        document.getElementById('msg').textContent = 'ユーザーニックネームを入力してください。';
        event.preventDefault();
    } else if (pw === '' || pw2 === ''){
    	document.getElementById('msg').textContent = 'パスワードを入力してください。';
        event.preventDefault();
    } else if (pw !== pw2){
    	document.getElementById('msg').textContent = 'パスワードが一致しません。';
        event.preventDefault();
    }
}
</script>
</html>
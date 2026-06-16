<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>新規登録</title>
</head>
<body>
<header>
	<h1>ここる</h1>
	<img src="/b4/img/mascot.png" alt="ペンギンのここるイラスト" id="mascot">
	<!-- cssのリンク -->
	<link rel="stylesheet" href="/css/userreg.css">
	
</header>

<!-- メインここから -->
<main>

<form id="userreg" method="POST" action="/b4/UserRegServlet">

	<div>ユーザーニックネーム</div>
		<input type="text" id="name" name="name">
		
	<div>パスワード</div>
		<input type="text" id="pw" name="pw">
		
	<div>確認のため再度入力してください</div>
		<input type="text" id="pw2" name="pw2">
		
	<p id="msg"></p>
	
	<div>メールアドレス(任意)</div>
		<input type="text">
	
</form>
	
<div><input type="button" name="regist" value="登録"></div>

</main>

</body>

<script>
'use strict'

document.getElementById('userreg').onsubmit = function(event){
    let id = document.getElementById('userreg').id.value;
    let pw = document.getElementById('userreg').pw.value;
    if (id === ''){
        document.getElementById('msg').textContent = 'ユーザーニックネームを入力してください。';
        event.preventDefault();
    } else if (pw === '' || pw2 === ''){
    	document.getElementById('msg').textContent = 'パスワードを入力してください。';
        event.preventDefault();
    }
}
</script>
</html>
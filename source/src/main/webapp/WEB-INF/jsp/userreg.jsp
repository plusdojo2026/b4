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
	<div>image</div>

<!-- メインここから -->

<form id="userreg" method="POST" action="/b4/UserRegServlet">

	<div>ユーザー登録ID</div>
		<input type="text">
		
	<div>パスワード</div>
		<input type="text" id="id" name="id">
		
	<div>確認のため再度入力してください</div>
		<input type="text" id="pw" name="pw">
		
	<p id="msg"></p>
	
	<div>メールアドレス(任意)</div>
		<input type="text">
	
</form>
	
<div><input type="button" name="regist" value="登録"></div>

</body>

<script>
'use strict'

document.getElementById('userreg').onsubmit = function(event){
    let id = document.getElementById('userreg').id.value;
    let pw = document.getElementById('userreg').pw.value;
    if (id === ''){
        document.getElementById('msg').textContent = 'ユーザー登録IDを入力してください。';
        event.preventDefault();
    } else if (pw === ''){
    	document.getElementById('msg').textContent = 'パスワードを入力してください。';
        event.preventDefault();
    }
}
</script>
</html>
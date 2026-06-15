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

<form method="POST" action="/webapp/LoginServlet" id="form">
<div>ユーザーニックネーム<br>
    <input type="text" name="name" id="name">
</div>
<div>パスワード<br>
    <input type="password" name="pw" id="pw">
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
<script>
'use strict';
/* id="error_message"にエラーメッセージを挿入 */
let formObj = document.getElementById('form'); 	/* id="form"の内容をformObjに */
let errorMessageObj = document.getElementById('error_message');	/* id="error_message"の内容をerrorMessageObjに */
/* idとpwどちらかが空白ならエラーメッセージ表示 */
formObj.onsubmit = function(event) {
    let name = document.getElementById('name').value;
    let pw = document.getElementById('pw').value;
    if(name === '' || pw === '') {
        errorMessageObj.textContent = 'ユーザーIDとパスワードを入力してください';
        event.preventDefault();
    }
};
/* リセットでエラーメッセージ消去 */
formObj.onreset = function() {
    errorMessageObj.textContent = null;
};
</script>
</body>
</html>
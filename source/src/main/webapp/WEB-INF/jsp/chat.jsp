<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta charset="UTF-8">
<title>家事提案</title>
<link rel="stylesheet" href="css/chat.css">
</head>
<body>
<header class="header">
	<div class="title">
		<h1>ここる</h1>
		<img src="images/logout.png" class="logout-icon">
	</div>
</header>
<c:forEach var="msg" items="${messageList}">
    <div class="${msg.sender}">
        ${msg.text}
    </div>
</c:forEach>
</body>
</html>
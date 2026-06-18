<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>テスト</title>
</head>
<body>
<img src="${Iconlist.icon_path}" alt="アイコン" id="icon">
<c:if test="${empty Iconlist}">
	<p>指定された条件に一致するデータはありません。</p>
	</c:if>
</body>
</html>
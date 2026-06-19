<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>リマインダー</title>
</head>
<body>
<c:out value="${Garbage.garbage_day}">
</c:out>
<c:out value="${Garbage.garbage_name}">
</c:out>
</body>
</html>
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
<c:forEach var="e" items="${GarbageList}" >
<div>${e.garbage_day}</div>
<div>${e.garbage_name}</div>
</c:forEach>
</body>
</html>
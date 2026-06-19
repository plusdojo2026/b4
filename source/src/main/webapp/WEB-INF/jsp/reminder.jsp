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
<div class="day">${e.garbage_day}</div>
<div>${e.garbage_name}</div>
</c:forEach>

<script>
'use strict';
/* 現在年月日（曜日）を表示 */
function showDate() {
    const today = new Date();
    const week = ["日", "月", "火", "水", "木", "金", "土"];
    const day = week[today.getDay()];
}
showDate();

if (document.getElementByClassName("day") != day) {
	document.getElementByClassName("day").style.display ="none";
}
</script>
</body>
</html>
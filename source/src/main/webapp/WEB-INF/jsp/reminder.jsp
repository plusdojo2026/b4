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
<div id="${e.garbage_day}">
<div>今日は${e.garbage_day} ${e.garbage_name}</div>

</div>
</c:forEach>

<script>
'use strict';
/* 現在年月日（曜日）を表示 */
let day;
function showDate() {
    const today = new Date();
    const week = ["日", "月", "火", "水", "木", "金", "土"];
    day = week[today.getDay()];
}
showDate();

if (e.garbage_day !== day) {
	document.getElementById('${e.garbage_day}').style.display ="none";
}
</script>
</body>
</html>
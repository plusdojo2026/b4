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
<div id="dayname">
<div>${e.garbageDay} ${e.garbageName}</div>
</div>
</c:forEach>

<form id="input" method="POST" action="/b4/ReminderServlet">
  <label>TODO: <input type= "text" id="todo" name="todoName"></label><br>
  <label>期日: <input type="date" name="todoDate"></label><br>
  <input type=submit id="submit" name="submit" value="登録">
</form>

<c:forEach var="t" items="${TodoList}" >
<table>
	<tr>
		<td>TODO${t.todoName}</td>
		<td>期日${t.todoDate}</td>
	</tr>
</table>
</c:forEach>
<script>
'use strict';
/* 現在年月日（曜日）を表示 */
   const week = ["日", "月", "火", "水", "木", "金", "土"];
    today = week[new Date().getDay()];
    
console.log(today);
for(let i = 0; i < week.length; i++)  {
	let div = document.getElementById("dayname");

    if (div == null) {
        continue;
    }
    
	if (e.garbageDay == today) {
        div.style.display = "block";
	}
	else (e.garbageDay !== today) {
		div.style.display ="none";
	}
}


</script>
</body>
</html>
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
<div>${e.garbage_day} ${e.garbage_name}</div>
</div>
</c:forEach>

<form id="input" method="POST" action="/b4/ReminderServlet">
  <label>TODO: <input type= "text" id="todo" name="todoName"></label><br>
  <label>期日: <input type="date" name="todoDate"></label><br>
  <input type=submit id="submit" name="submit" value="登録">
</form>


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
    
	if (e.garbage_day == today) {
        div.style.display = "block";
	}
	else (e.garbage_day !== today) {
		div.style.display ="none";
	}
}


</script>
</body>
</html>
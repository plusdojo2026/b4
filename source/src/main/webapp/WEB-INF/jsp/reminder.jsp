<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>リマインダー</title>
<link rel="stylesheet" href="css/reminder.css">
</head>
<body>
<header class="header">
	<div class="title">
		<h1>ここる</h1>
		<a href="/b4/LogoutServlet" id="logout"><img src="/b4/img/logout.png" class="logout-icon" ></a>
	</div>
</header>
<main>
	<c:forEach var="e" items="${GarbageList}" >
	<table id="garbage">
		<tr>
			<td>${e.garbageDay}</td>
			<td>${e.garbageName}</td>
		</tr>
	</table>
	</c:forEach>
	
	<form id="input" method="POST" action="/b4/ReminderServlet">
	  <label>やること: <input type= "text" name="todoName"></label><br>
	  <label>期限: <input type="date" name="todoDate"></label><br>
	  <input type="submit" id="submit" name="submit" value="登録">
		<table id="todo">
		<thead>
	    <tr>
	    	<th></th>
	     	<th>やること</th>
	     	<th>期限</th>
	     	<th></th>
	    </tr>
	    </thead>
		<c:forEach var="t" items="${TodoList}" >
	    <tbody>
		<tr>
	<!-- idの情報を見えないようにして表示する -->
			<td>
			<input type="hidden" name="id" value="${t.id}"> 
			</td>
			<td class="todo" data-label="やること">${t.todoName}</td>
			<td class="todo" data-label="期限">${t.todoDate}</td>
			<td>
			<input type="submit" id="submit" name="submit" value="削除">
			</td>
		</tr>
		</tbody>
		</c:forEach>
		</table>
	</form>
</main>
<script>
'use strict';
/* 現在年月日（曜日）を表示 */
   const week = ["日", "月", "火", "水", "木", "金", "土"];
    today = week[new Date().getDay()];
    
console.log(today);
for(let i = 0; i < week.length; i++)  {
	let div = document.getElementById("garbage");

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
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>リマインダー</title>
<link rel="stylesheet" href="css/reminder.css">
</head>
<body>
<jsp:include page="sidebar.jspf" flush="true|false" />

<header class="header">

	<div class="title">
	
	<!-- サイドバー -->
<%-- <jsp:include page="sidebar.jspf" flush="true|false" /> --%>
<img src="/b4/img/sidebar.png" id="toggleBtn">
<link rel="stylesheet" href="/b4/css/sidebar.css">
<script src="/b4/js/sidebar.js"> </script>

		<h1>ここる</h1>
		<a href="/b4/LogoutServlet" id="logout"><img src="/b4/img/logout.png" class="logout-icon" ></a>
	</div>
</header>
<main>
	<div id="garbage">
	<c:forEach var="e" items="${GarbageList}" >
	<table>
		<tr>
			<td>${e.garbageDay}</td>
			<td>${e.garbageName}</td>
		</tr>
	</table>
	</c:forEach>
	</div>
	
	<form id="input" method="POST" action="/b4/ReminderServlet">
	  <label>やること: <input type= "text" name="todoName"></label><br>
	  <label>期限: <input type="date" name="todoDate"></label><br>
	  <button type="submit" name="id" value="input">
         登録
	  </button>
	</form>
	
	<form id="delete" method="POST" action="/b4/ReminderServlet">
		<table id="todo">
		    <tr>
		     	<th>やること</th>
		     	<th>期限</th>
		     	<th></th>
		    </tr>
		    
		   <c:forEach var="t" items="${TodoList}" >
			<tr>
				<td class="todo" data-label="やること">${t.todoName}</td>
				<td class="todo" id="todoDate" data-label="期限">${t.todoDate}</td>
				<td>
				<button type="submit" name="id" value="${t.id}">
                    削除
				</button>
				<!-- <input type="text" name="id" value="${t.id}"> 
				<input type="submit" id="submit" name="submit" value="削除">
				 -->
				</td>				
			</tr>
			</c:forEach>
		</table>
	</form>
</main>
<script>
'use strict';
/* 現在年月日（曜日）を表示 */
   /*const week = ["日", "月", "火", "水", "木", "金", "土"];
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
*/
</script>
</body>
</html>
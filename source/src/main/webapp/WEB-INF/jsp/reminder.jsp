<%@ page language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="ja">
<head>
	<meta charset="UTF-8">

	<meta name="viewport"
		content="width=device-width, initial-scale=1">

	<title>リマインダー</title>

	<link rel="preconnect"
		href="https://fonts.googleapis.com">

	<link rel="preconnect"
		href="https://fonts.gstatic.com"
		crossorigin>

	<link
		href="https://fonts.googleapis.com/css2?family=Zen+Maru+Gothic&display=swap"
		rel="stylesheet">

	<link rel="stylesheet"
		href="${pageContext.request.contextPath}/css/reminder.css">

	<link rel="stylesheet"
		href="${pageContext.request.contextPath}/css/sidebar.css">

	<script
		src="${pageContext.request.contextPath}/js/sidebar.js"
		defer>
	</script>
</head>

<body data-context-path="${pageContext.request.contextPath}">

	<!-- サイドバー -->
	<jsp:include
		page="/WEB-INF/jsp/sidebar.jsp"
		flush="true" />

	<header class="header">

		<div class="title">

			<img
				src="${pageContext.request.contextPath}/img/sidebar.png"
				id="toggleBtn"
				alt="メニュー">

			<h1>ここる</h1>

			<a
				href="${pageContext.request.contextPath}/LogoutServlet"
				id="logout">

				<img
					src="${pageContext.request.contextPath}/img/logout.png"
					class="logout-icon"
					alt="ログアウト">
			</a>

		</div>

	</header>

	<main>

		<div id="garbage">

			<c:forEach
				var="e"
				items="${GarbageList}">

				<table>
					<tr>
						<td>${e.garbageDay}</td>
						<td>${e.garbageName}</td>
					</tr>
				</table>

			</c:forEach>

		</div>

		<form
			id="input"
			method="post"
			action="${pageContext.request.contextPath}/ReminderServlet">

			<label>
				やること:
				<input
					type="text"
					name="todoName">
			</label>

			<br>

			<label>
				期限:
				<input
					type="date"
					name="todoDate">
			</label>

			<br>

			<button
				type="submit"
				name="id"
				value="input">
				登録
			</button>

		</form>

		<form
			id="delete"
			method="post"
			action="${pageContext.request.contextPath}/ReminderServlet">

			<table id="todo">

				<tr>
					<th>やること</th>
					<th>期限</th>
					<th></th>
				</tr>

				<c:forEach
					var="t"
					items="${TodoList}">

					<tr>

						<td
							class="todo"
							data-label="やること">
							${t.todoName}
						</td>

						<td
							class="todo"
							data-label="期限">
							${t.todoDate}
						</td>

						<td>
							<button
								type="submit"
								name="id"
								value="${t.id}">
								削除
							</button>
						</td>

					</tr>

				</c:forEach>

			</table>

		</form>

	</main>

</body>
</html>
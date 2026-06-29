<%@ page language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ja">
<head>
	<meta charset="UTF-8">

	<meta name="viewport"
		content="width=device-width, initial-scale=1">

	<title>ユーザー設定</title>

	<link rel="preconnect"
		href="https://fonts.googleapis.com">

	<link rel="preconnect"
		href="https://fonts.gstatic.com"
		crossorigin>

	<link
		href="https://fonts.googleapis.com/css2?family=Zen+Maru+Gothic&display=swap"
		rel="stylesheet">

	<link rel="stylesheet"
		href="${pageContext.request.contextPath}/css/setting.css">

	<link rel="stylesheet"
		href="${pageContext.request.contextPath}/css/sidebar.css">

	<script
		src="${pageContext.request.contextPath}/js/setting.js"
		defer>
	</script>

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

			<h1 class="header-title">
				ここる
			</h1>

			<a
				href="${pageContext.request.contextPath}/LogoutServlet"
				id="logout"
				class="header-logout">

				<img
					src="${pageContext.request.contextPath}/img/logout.png"
					class="logout-icon"
					alt="ログアウト">
			</a>

		</div>

	</header>

	<div class="container">

		<div class="icon-container">

			<div
				class="icon-circle"
				onclick="openModal()">

				<img
					id="displayIcon"
					src="${pageContext.request.contextPath}/img/icon1.png"
					alt="ユーザーアイコン">

			</div>

		</div>

		<div class="setting-section">

			<h3>プロフィール</h3>

			<form
				action="${pageContext.request.contextPath}/SettingServlet"
				method="post">

				<input
					type="hidden"
					name="iconName"
					id="hiddenIconName"
					value="1">

				<div class="form-group">

					<label>名前：</label>

					<input
						type="text"
						name="userName"
						value="${user.userName}"
						placeholder="名前を入力してください">

				</div>

				<div class="form-group">

					<label>子どもの人数：</label>

					<input
						type="number"
						name="childCount"
						value="${user.childCount}">

				</div>

				<div class="btn-container">

					<button
						type="submit"
						name="action"
						value="profile"
						class="submit-btn">
						登録
					</button>

				</div>

			</form>

		</div>

		<div class="setting-section">

			<h3>ゴミ捨て設定</h3>

			<form
				action="${pageContext.request.contextPath}/SettingServlet"
				method="post">

				<div class="form-group">

					<label>ゴミ分類名：</label>

					<select name="garbage_name">

						<option
							value="可燃ゴミ"
							${user.garbageName == '可燃ゴミ' ? 'selected' : ''}>
							可燃ゴミ
						</option>

						<option
							value="不燃ゴミ"
							${user.garbageName == '不燃ゴミ' ? 'selected' : ''}>
							不燃ゴミ
						</option>

						<option
							value="資源ゴミ"
							${user.garbageName == '資源ゴミ' ? 'selected' : ''}>
							資源ゴミ
						</option>

						<option
							value="ビン、缶、ペットボトル"
							${user.garbageName == 'ビン、缶、ペットボトル'
								? 'selected'
								: ''}>
							ビン、缶、ペットボトル
						</option>

						<option
							value="その他"
							${user.garbageName == 'その他' ? 'selected' : ''}>
							その他
						</option>

					</select>

				</div>

				<div class="form-group">

					<label>曜日：</label>

					<select name="garbage_day">
						<option value="月">月曜日</option>
						<option value="火">火曜日</option>
						<option value="水">水曜日</option>
						<option value="木">木曜日</option>
						<option value="金">金曜日</option>
						<option value="土">土曜日</option>
						<option value="日">日曜日</option>
					</select>

				</div>

				<div class="btn-container">

					<button
						type="submit"
						name="action"
						value="garbage"
						class="submit-btn">
						登録
					</button>

				</div>

			</form>

		</div>

<%
	if (request.getAttribute("message")
			!= null) {
%>

		<p class="success-message">
			<%= request.getAttribute("message") %>
		</p>

<%
	}
%>

	</div>

	<div
		id="modal"
		class="modal-overlay">

		<p>アイコンを選んでください</p>

		<div class="modal-icons">

			<label class="icon-label">
				<input
					type="radio"
					name="tempIcon"
					value="2"
					onclick="selectIcon(2, 'cat.png')">

				<img
					src="${pageContext.request.contextPath}/img/cat.png"
					width="40"
					height="40"
					alt="猫">
			</label>

			<label class="icon-label">
				<input
					type="radio"
					name="tempIcon"
					value="7"
					onclick="selectIcon(7, 'dinosaur.png')">

				<img
					src="${pageContext.request.contextPath}/img/dinosaur.png"
					width="40"
					height="40"
					alt="恐竜">
			</label>

			<label class="icon-label">
				<input
					type="radio"
					name="tempIcon"
					value="1"
					onclick="selectIcon(1, 'dog.png')">

				<img
					src="${pageContext.request.contextPath}/img/dog.png"
					width="40"
					height="40"
					alt="犬">
			</label>

			<label class="icon-label">
				<input
					type="radio"
					name="tempIcon"
					value="6"
					onclick="selectIcon(6, 'dolphin.png')">

				<img
					src="${pageContext.request.contextPath}/img/dolphin.png"
					width="40"
					height="40"
					alt="イルカ">
			</label>

			<label class="icon-label">
				<input
					type="radio"
					name="tempIcon"
					value="4"
					onclick="selectIcon(4, 'gorilla.png')">

				<img
					src="${pageContext.request.contextPath}/img/gorilla.png"
					width="40"
					height="40"
					alt="ゴリラ">
			</label>

			<label class="icon-label">
				<input
					type="radio"
					name="tempIcon"
					value="3"
					onclick="selectIcon(3, 'rabbit.png')">

				<img
					src="${pageContext.request.contextPath}/img/rabbit.png"
					width="40"
					height="40"
					alt="ウサギ">
			</label>

			<label class="icon-label">
				<input
					type="radio"
					name="tempIcon"
					value="5"
					onclick="selectIcon(5, 'whiteBear.png')">

				<img
					src="${pageContext.request.contextPath}/img/whiteBear.png"
					width="40"
					height="40"
					alt="シロクマ">
			</label>

		</div>

		<br>

		<button
			type="button"
			onclick="closeModal()">
			閉じる
		</button>

	</div>

</body>
</html>
<%@ page language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="dto.Activity" %>

<%!
	/**
	 * HTMLに出力する文字列をエスケープ
	 */
	private String escapeHtml(
			String value) {

		if (value == null) {
			return "";
		}

		return value
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}
%>

<%
	/*
	 * ChatServletから渡された
	 * 分類済み家事一覧を取得
	 */
	@SuppressWarnings("unchecked")
	Map<String, List<Activity>> reportActivityMap =
			(Map<String, List<Activity>>)
			request.getAttribute(
					"reportActivityMap");
%>

<!DOCTYPE html>
<html lang="ja">
<head>
	<meta charset="UTF-8">

	<meta name="viewport"
		content="width=device-width, initial-scale=1">

	<title>家事提案</title>

	<link rel="preconnect"
		href="https://fonts.googleapis.com">

	<link rel="preconnect"
		href="https://fonts.gstatic.com"
		crossorigin>

	<link
		href="https://fonts.googleapis.com/css2?family=Zen+Maru+Gothic&display=swap"
		rel="stylesheet">

	<link rel="stylesheet"
		href="${pageContext.request.contextPath}/css/chat.css">

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

	<!-- ユーザーアイコン -->
	<img
		src="${Iconlist.icon_path}"
		id="icon"
		data-fallback="${pageContext.request.contextPath}/img/penguin.png"
		alt="ユーザーアイコン">

	<!-- チャット表示領域 -->
	<div id="chatArea"></div>

	<!-- 実施済み家事の報告画面 -->
	<div
		id="modal"
		class="hidden"
		role="dialog"
		aria-modal="true"
		aria-labelledby="reportModalTitle">

		<div class="modal-content">

			<h3 id="reportModalTitle">
				何をやったか教えて！
			</h3>

			<div class="check-area">

<%
	if (reportActivityMap == null
			|| reportActivityMap.isEmpty()) {
%>

				<p>家事一覧を取得できませんでした</p>

<%
	} else {

		boolean hasAnyActivity = false;

		for (Map.Entry<String, List<Activity>>
				entry : reportActivityMap.entrySet()) {

			String groupName =
					entry.getKey();

			List<Activity> groupActivityList =
					entry.getValue();

			if (groupActivityList == null
					|| groupActivityList.isEmpty()) {

				continue;
			}

			hasAnyActivity = true;
%>

				<details>
					<summary>
						<%= escapeHtml(groupName) %>
					</summary>

<%
			for (Activity activity
					: groupActivityList) {

				if (activity == null) {
					continue;
				}

				String activityName =
						activity.getActivityName();
%>

					<label>
						<input
							type="checkbox"
							value="<%= activity.getId() %>"
							data-name="<%= escapeHtml(activityName) %>">

						<span>
							<%= escapeHtml(activityName) %>
						</span>
					</label>

<%
			}
%>

				</details>

<%
		}

		if (!hasAnyActivity) {
%>

				<p>報告できる家事がありません</p>

<%
		}
	}
%>

			</div>

			<div id="close">

				<button
					type="button"
					onclick="reportHw()">
					報告する
				</button>

				<button
					type="button"
					onclick="closeModal()">
					閉じる
				</button>

			</div>
		</div>
	</div>

	<!-- 時間指定画面 -->
	<div
		id="timeModal"
		class="hidden"
		role="dialog"
		aria-modal="true"
		aria-labelledby="timeModalTitle">

		<div class="modal-content">

			<h3 id="timeModalTitle">
				何分くらい頑張る？
			</h3>

			<select id="timeSelect">
				<option value="10">10分</option>
				<option value="15">15分</option>
				<option value="30">30分</option>
				<option value="45">45分</option>
				<option value="60">60分</option>
			</select>

			<div id="decide">

				<button
					type="button"
					onclick="decideTime()">
					決定
				</button>

				<button
					type="button"
					onclick="closeTimeModal()">
					閉じる
				</button>

			</div>
		</div>
	</div>

	<script
		src="${pageContext.request.contextPath}/js/chat.js">
	</script>

</body>
</html>
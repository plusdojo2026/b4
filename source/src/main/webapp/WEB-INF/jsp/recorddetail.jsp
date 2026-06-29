<%@ page language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="dto.RecordHistoryDto" %>

<%!
	/**
	 * 対象文字列にいずれかのキーワードが含まれるか判定
	 */
	private boolean containsAny(
			String target,
			String... keywords) {

		if (target == null
				|| keywords == null) {

			return false;
		}

		for (String keyword : keywords) {

			if (keyword != null
					&& target.contains(keyword)) {

				return true;
			}
		}

		return false;
	}

	/**
	 * 活動名が指定した表示グループに属するか判定
	 */
	private boolean matchesGroup(
			String groupName,
			String activityName) {

		if (groupName == null
				|| activityName == null) {

			return false;
		}

		boolean cleaning =
				containsAny(
						activityName,
						"掃除",
						"拭く",
						"流す",
						"ワイパー");

		boolean laundry =
				containsAny(
						activityName,
						"洗濯",
						"干す",
						"取り込む",
						"畳む",
						"たたむ");

		boolean cooking =
				containsAny(
						activityName,
						"食器",
						"キッチン",
						"コンロ",
						"料理",
						"調理",
						"食事");

		if ("掃除".equals(groupName)) {
			return cleaning;
		}

		if ("洗濯".equals(groupName)) {
			return laundry;
		}

		if ("料理".equals(groupName)) {
			return cooking;
		}

		if ("片付け".equals(groupName)) {
			return !cleaning
					&& !laundry
					&& !cooking;
		}

		return false;
	}

	/**
	 * 指定グループの家事が指定曜日に記録されているか判定
	 */
	private boolean hasRecord(
			List<RecordHistoryDto> recordList,
			String groupName,
			int dayIndex) {

		if (recordList == null
				|| recordList.isEmpty()) {

			return false;
		}

		for (RecordHistoryDto record
				: recordList) {

			if (record == null) {
				continue;
			}

			if (record.getActivityName() == null
					|| record.getCreatedAt() == null) {

				continue;
			}

			if (!"HOUSEWORK".equals(
					record.getCategory())) {

				continue;
			}

			if (!matchesGroup(
					groupName,
					record.getActivityName())) {

				continue;
			}

			int recordDayIndex =
					getDayIndex(
							record.getCreatedAt());

			if (recordDayIndex == dayIndex) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 月曜日始まりの曜日番号を取得
	 */
	private int getDayIndex(
			LocalDateTime dateTime) {

		return dateTime
				.getDayOfWeek()
				.getValue() - 1;
	}

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
	List<RecordHistoryDto> recordList =
			new ArrayList<RecordHistoryDto>();

	Object recordListObj =
			request.getAttribute(
					"recordList");

	if (recordListObj instanceof List<?>) {

		@SuppressWarnings("unchecked")
		List<RecordHistoryDto> castedRecordList =
				(List<RecordHistoryDto>)
				recordListObj;

		recordList =
				castedRecordList;
	}

	String periodLabel =
			(String) request.getAttribute(
					"periodLabel");

	Integer previousWeekOffset =
			(Integer) request.getAttribute(
					"previousWeekOffset");

	Integer nextWeekOffset =
			(Integer) request.getAttribute(
					"nextWeekOffset");

	if (periodLabel == null
			|| periodLabel.isEmpty()) {

		periodLabel = "";
	}

	if (previousWeekOffset == null) {
		previousWeekOffset = -1;
	}

	if (nextWeekOffset == null) {
		nextWeekOffset = 1;
	}

	String[] activityNames = {
			"掃除",
			"洗濯",
			"料理",
			"片付け"
	};

	String[] dayLabels = {
			"月",
			"火",
			"水",
			"木",
			"金",
			"土",
			"日"
	};
%>

<!DOCTYPE html>
<html lang="ja">
<head>
	<meta charset="UTF-8">

	<meta name="viewport"
		content="width=device-width, initial-scale=1.0">

	<title>記録一覧</title>

	<link rel="preconnect"
		href="https://fonts.googleapis.com">

	<link rel="preconnect"
		href="https://fonts.gstatic.com"
		crossorigin>

	<link
		href="https://fonts.googleapis.com/css2?family=Zen+Maru+Gothic&display=swap"
		rel="stylesheet">

	<link rel="stylesheet"
		href="${pageContext.request.contextPath}/css/sidebar.css">

	<link rel="stylesheet"
		href="${pageContext.request.contextPath}/css/recorddetail.css?v=20260629-12">

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

	<div class="page-frame">

		<header class="record-header">

			<div class="record-header-inner">

				<button
					type="button"
					id="toggleBtn"
					class="record-menu-button"
					aria-label="サイドバーを開く">

					<span class="record-menu-viewport">

						<img
							src="${pageContext.request.contextPath}/img/sidebar.png"
							class="record-menu-icon"
							alt="">

					</span>

				</button>

				<h1 class="record-page-title">
					記録一覧
				</h1>

				<a
					href="${pageContext.request.contextPath}/LogoutServlet"
					class="record-logout-button"
					aria-label="ログアウト">

					<span class="record-logout-viewport">

						<img
							src="${pageContext.request.contextPath}/img/logout.png"
							class="record-logout-icon"
							alt="">

					</span>

				</a>

			</div>

		</header>

		<main class="record-main">

			<section class="record-panel">

				<div class="record-panel-title">

					<span>
						家事ごとの記録
					</span>

					<span>
						<%= escapeHtml(periodLabel) %>
					</span>

				</div>

				<div class="record-grid">

					<div class="grid-empty"></div>

<%
	for (String dayLabel
			: dayLabels) {
%>

					<div class="day-label">
						<%= escapeHtml(dayLabel) %>
					</div>

<%
	}
%>

<%
	for (String activityName
			: activityNames) {
%>

					<div class="activity-label">
						<%= escapeHtml(activityName) %>
					</div>

<%
		for (int dayIndex = 0;
				dayIndex < 7;
				dayIndex++) {

			boolean done =
					hasRecord(
							recordList,
							activityName,
							dayIndex);
%>

					<div class="record-cell<%= done ? " done" : "" %>">

<%
			if (done) {
%>

						<img
							src="${pageContext.request.contextPath}/img/penguin.png"
							alt="記録あり">

<%
			}
%>

					</div>

<%
		}
	}
%>

				</div>

			</section>

			<div class="week-buttons">

				<form
					action="${pageContext.request.contextPath}/RecordDetailServlet"
					method="get"
					class="week-form">

					<input
						type="hidden"
						name="weekOffset"
						value="<%= previousWeekOffset.intValue() %>">

					<button
						type="submit"
						class="week-button">
						前週
					</button>

				</form>

				<form
					action="${pageContext.request.contextPath}/RecordDetailServlet"
					method="get"
					class="week-form">

					<input
						type="hidden"
						name="weekOffset"
						value="<%= nextWeekOffset.intValue() %>">

					<button
						type="submit"
						class="week-button">
						翌週
					</button>

				</form>

			</div>

			<div class="home-link-wrap">

				<a
					href="${pageContext.request.contextPath}/ChatServlet"
					class="home-link">
					ホームへ戻る
				</a>

			</div>

		</main>

	</div>

</body>
</html>
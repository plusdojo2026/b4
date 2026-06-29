<%@ page contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="dto.RecordHistoryDto" %>

<%!
	/**
	 * 指定した活動名が指定した曜日に記録されているか判定
	 */
	private boolean hasRecord(
			List<RecordHistoryDto> recordList,
			String activityName,
			int dayIndex) {

		if (recordList == null
				|| recordList.isEmpty()) {

			return false;
		}

		for (RecordHistoryDto record : recordList) {

			if (record == null) {
				continue;
			}

			if (record.getActivityName() == null
					|| record.getCreatedAt() == null) {

				continue;
			}

			if (!activityName.equals(
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
	 * LocalDateTimeから月曜日始まりの曜日番号を取得
	 *
	 * 月 = 0
	 * 火 = 1
	 * 水 = 2
	 * 木 = 3
	 * 金 = 4
	 * 土 = 5
	 * 日 = 6
	 */
	private int getDayIndex(
			LocalDateTime dateTime) {

		int dayOfWeekValue =
				dateTime
						.getDayOfWeek()
						.getValue();

		return dayOfWeekValue - 1;
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
	// コンテキストパスを取得
	String contextPath =
			request.getContextPath();

	// 記録一覧を取得
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

	// 表示期間を取得
	String periodLabel =
			(String) request.getAttribute(
					"periodLabel");

	// 前週と翌週のオフセットを取得
	Integer previousWeekOffset =
			(Integer) request.getAttribute(
					"previousWeekOffset");

	Integer nextWeekOffset =
			(Integer) request.getAttribute(
					"nextWeekOffset");

	// null対策
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

	// 画面に表示する活動名
	String[] activityNames = {
			"掃除",
			"洗濯",
			"料理",
			"片付け"
	};

	// 曜日ラベル
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

	<!-- バージョンを付けて古いCSSキャッシュを回避 -->
	<link rel="stylesheet"
		href="<%= contextPath %>/css/recorddetail.css?v=20260629-2">
</head>

<body>

<div class="page-frame">

	<header class="record-header">

		<h1 class="header-title">
			記録一覧
		</h1>

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
									src="<%= contextPath %>/img/penguin.png"
									alt="記録あり">

							<%
								}
							%>

						</div>

					<%
						}
					%>

				<%
					}
				%>

			</div>

		</section>

		<div class="week-buttons">

			<a
				href="<%= contextPath %>/record?weekOffset=<%= previousWeekOffset %>"
				class="week-button">

				前週

			</a>

			<a
				href="<%= contextPath %>/record?weekOffset=<%= nextWeekOffset %>"
				class="week-button">

				翌週

			</a>

		</div>

		<div class="home-link-wrap">

			<a
				href="<%= contextPath %>/ChatServlet"
				class="home-link">

				ホームへ戻る

			</a>

		</div>

	</main>

</div>

</body>
</html>
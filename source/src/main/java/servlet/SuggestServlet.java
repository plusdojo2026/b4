package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.ActivityDao;
import dao.ActivityHistoryDao;
import dto.Activity;
import dto.LoginUser;
import dto.RecordHistoryDto;

/**
 * 活動を提案するServlet
 *
 * 主な処理：
 * ・時間指定またはおまかせで提案を開始する
 * ・残り時間をHttpSessionで管理する
 * ・実施済み活動や条件に合わない活動を候補から除外する
 * ・各種補正ポイントを計算して優先順位を決める
 * ・活動完了後に残り時間を減らして次の候補を返す
 *
 */
@WebServlet("/SuggestServlet")
public class SuggestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/** 時間指定モードで最後に確保する準備時間 */
	private static final int PREPARE_MINUTES = 5;

	/** 提案候補から除外することを表すスコア */
	private static final int EXCLUDED_SCORE = -9999;

	/** HttpSessionに保存する属性名 */
	private static final String SESSION_MODE = "suggestionMode";
	private static final String SESSION_REMAINING_MINUTES = "suggestionRemainingMinutes";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		setJsonResponse(response);
		writeError(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POSTでアクセスしてください。");
	}

	/**
	 * JavaScriptから送信されたactionに応じて処理を分岐
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		setJsonResponse(response);

		HttpSession session = request.getSession(false);

		if (session == null) {
			writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "ログイン情報がありません。");
			return;
		}

		LoginUser loginUser = (LoginUser) session.getAttribute("idnamepw");

		if (loginUser == null) {
			writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "ログイン情報がありません。");
			return;
		}

		String action = request.getParameter("action");

		if (action == null || action.isEmpty()) {
			writeError(response, HttpServletResponse.SC_BAD_REQUEST, "actionが指定されていません。");
			return;
		}

		try {
			int userId = loginUser.getUserId();

			if ("start".equals(action)) {
				startSuggestion(request, response, session, userId);
			} else if ("complete".equals(action)) {
				completeSuggestion(request, response, session, userId);
			} else if ("refresh".equals(action)) {
				refreshSuggestion(response, session, userId);
			} else {
				writeError(response, HttpServletResponse.SC_BAD_REQUEST, "不正なactionです。");
			}

		} catch (NumberFormatException e) {
			writeError(response, HttpServletResponse.SC_BAD_REQUEST, "数値の指定が正しくありません。");

		} catch (Exception e) {
			e.printStackTrace();
			writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"提案処理中にエラーが発生しました。");
		}
	}

	/**
	 * 提案を開始
	 *
	 * TIME：
	 * 指定時間をセッションへ保存
	 *
	 * AUTO：
	 * 残り時間を使用せずに提案
	 */
	private void startSuggestion(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, int userId) throws IOException {

		String mode = request.getParameter("mode");

		if ("TIME".equals(mode)) {
			String timeText = request.getParameter("time");

			if (timeText == null || timeText.isEmpty()) {
				writeError(response, HttpServletResponse.SC_BAD_REQUEST, "時間が指定されていません。");
				return;
			}

			int selectedMinutes = Integer.parseInt(timeText);

			// 現在の画面仕様では30分、45分、60分のみ受け付ける。
			if (selectedMinutes != 30 && selectedMinutes != 45 && selectedMinutes != 60) {
				writeError(response, HttpServletResponse.SC_BAD_REQUEST,
						"時間は30分、45分、60分から選択してください。");
				return;
			}

			session.setAttribute(SESSION_MODE, "TIME");
			session.setAttribute(SESSION_REMAINING_MINUTES, selectedMinutes);

		} else if ("AUTO".equals(mode)) {
			session.setAttribute(SESSION_MODE, "AUTO");
			session.removeAttribute(SESSION_REMAINING_MINUTES);

		} else {
			writeError(response, HttpServletResponse.SC_BAD_REQUEST,
					"modeにはTIMEまたはAUTOを指定してください。");
			return;
		}

		writeCurrentSuggestions(response, session, userId);
	}

	/**
	 * 活動完了後に残り時間を更新し、次の提案を返す
	 *
	 * ReportServletで活動履歴を登録した後に、この処理を呼び出す
	 */
	private void completeSuggestion(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, int userId) throws IOException {

		String mode = (String) session.getAttribute(SESSION_MODE);

		if (mode == null) {
			writeError(response, HttpServletResponse.SC_BAD_REQUEST, "提案が開始されていません。");
			return;
		}

		String activityIdText = request.getParameter("activityId");

		if (activityIdText == null || activityIdText.isEmpty()) {
			writeError(response, HttpServletResponse.SC_BAD_REQUEST,
					"activityIdが指定されていません。");
			return;
		}

		int activityId = Integer.parseInt(activityIdText);

		if (activityId <= 0) {
			writeError(response, HttpServletResponse.SC_BAD_REQUEST, "activityIdが不正です。");
			return;
		}

		ActivityDao activityDao = new ActivityDao();
		List<Activity> activityList = activityDao.selectAll();
		Activity completedActivity = findActivityById(activityList, activityId);

		if (completedActivity == null) {
			writeError(response, HttpServletResponse.SC_NOT_FOUND,
					"指定された活動が存在しません。");
			return;
		}

		// TIMEモードの場合だけ、完了した活動の所要時間を残り時間から引く
		if ("TIME".equals(mode)) {
			Integer remainingMinutes =
					(Integer) session.getAttribute(SESSION_REMAINING_MINUTES);

			if (remainingMinutes == null) {
				writeError(response, HttpServletResponse.SC_BAD_REQUEST,
						"残り時間が取得できません。");
				return;
			}

			remainingMinutes -= completedActivity.getRequiredTime();

			if (remainingMinutes < 0) {
				remainingMinutes = 0;
			}

			session.setAttribute(SESSION_REMAINING_MINUTES, remainingMinutes);
		}

		writeCurrentSuggestions(response, session, userId);
	}

	/**
	 * 残り時間を変更せず、現在の条件で提案候補を再取得
	 */
	private void refreshSuggestion(HttpServletResponse response, HttpSession session,
			int userId) throws IOException {

		String mode = (String) session.getAttribute(SESSION_MODE);

		if (mode == null) {
			writeError(response, HttpServletResponse.SC_BAD_REQUEST, "提案が開始されていません。");
			return;
		}

		writeCurrentSuggestions(response, session, userId);
	}

	/**
	 * 現在のモードと残り時間から提案候補を作成し、JSONで返す
	 */
	private void writeCurrentSuggestions(HttpServletResponse response, HttpSession session,
			int userId) throws IOException {

		String mode = (String) session.getAttribute(SESSION_MODE);
		Integer remainingMinutes = null;

		if ("TIME".equals(mode)) {
			remainingMinutes =
					(Integer) session.getAttribute(SESSION_REMAINING_MINUTES);

			if (remainingMinutes == null) {
				writeError(response, HttpServletResponse.SC_BAD_REQUEST,
						"残り時間が取得できません。");
				return;
			}

			// 残り時間が準備時間以下なら、活動を提案せず終了
			if (remainingMinutes <= PREPARE_MINUTES) {
				writeSuggestionResponse(response, "PREPARE", mode, remainingMinutes,
						"家事はここまでにして、次の用事の準備をしましょう。",
						new ArrayList<ScoredActivity>());
				return;
			}
		}

		List<ScoredActivity> suggestions =
				createSuggestions(userId, mode, remainingMinutes);

		if (suggestions.isEmpty()) {
			if ("TIME".equals(mode)) {
				writeSuggestionResponse(response, "PREPARE", mode, remainingMinutes,
						"残り時間でできる活動がないため、用事の準備をしましょう。",
						suggestions);
			} else {
				writeSuggestionResponse(response, "FINISH", mode, null,
						"今日の提案はここまでです。無理せず休みましょう。",
						suggestions);
			}

			return;
		}

		writeSuggestionResponse(response, "CONTINUE", mode, remainingMinutes, null,
				suggestions);
	}

	/**
	 * 全活動を取得し、除外判定とスコア計算を行う
	 */
	private List<ScoredActivity> createSuggestions(int userId, String mode,
			Integer remainingMinutes) {

		ActivityDao activityDao = new ActivityDao();
		ActivityHistoryDao historyDao = new ActivityHistoryDao();

		List<Activity> activityList = activityDao.selectAll();

		if (activityList == null) {
			activityList = new ArrayList<>();
		}

		LocalDateTime now = LocalDateTime.now();
		LocalDate today = now.toLocalDate();
		LocalDateTime startAt = today.atStartOfDay();
		LocalDateTime endAt = startAt.plusDays(1);

		// 今日実施した活動を取得する。
		List<RecordHistoryDto> todayHistoryList =
				historyDao.findRecordHistoryList(userId, startAt, endAt);

		if (todayHistoryList == null) {
			todayHistoryList = new ArrayList<>();
		}

		Set<Integer> doneTodayActivityIds = new HashSet<>();
		int houseworkCount = 0;
		int childCount = 0;

		for (RecordHistoryDto history : todayHistoryList) {
			doneTodayActivityIds.add(history.getActivityId());

			if ("HOUSEWORK".equals(history.getCategory())) {
				houseworkCount++;
			}

			if ("CHILD".equals(history.getCategory())) {
				childCount++;
			}
		}

		/*
		 * 全活動の最終実施日時を1回のSQLで取得
		 *
		 * 活動ごとにfindLastExecutedAtを呼ぶとSQLの実行回数が増えるため、
		 * SuggestServletではMapを使用
		 */
		Map<Integer, LocalDateTime> lastExecutedAtMap =
				historyDao.findLastExecutedAtMap(userId);

		/*
		 * ユーザーが最後に実施した活動を取得
		 *
		 * 直前が子供と一緒にできない家事だった場合の補正に使用
		 */
		RecordHistoryDto latestHistory = historyDao.findLatestHistory(userId);
		Activity latestActivity = null;

		if (latestHistory != null) {
			latestActivity =
					findActivityById(activityList, latestHistory.getActivityId());
		}

		int overworkLevel = calculateOverworkLevel(houseworkCount);
		LocalTime currentTime = now.toLocalTime();
		List<ScoredActivity> scoredList = new ArrayList<>();

		for (Activity activity : activityList) {
			int score = calculateScore(activity, activityList, doneTodayActivityIds,
					lastExecutedAtMap, latestActivity, overworkLevel, childCount,
					now, currentTime, mode, remainingMinutes);

			if (score == EXCLUDED_SCORE) {
				continue;
			}

			ScoredActivity scoredActivity = new ScoredActivity();
			scoredActivity.activity = activity;
			scoredActivity.score = score;
			scoredActivity.lastExecutedAt =
					lastExecutedAtMap.get(activity.getId());

			scoredList.add(scoredActivity);
		}

		/*
		 * 優先順位：
		 * 1. 合計スコアが高い順
		 * 2. 最終実施日時が古い順
		 * 3. 所要時間が短い順
		 * 4. CHILDカテゴリを優先
		 * 5. activityIdが小さい順
		 */
		scoredList.sort(new Comparator<ScoredActivity>() {
			@Override
			public int compare(ScoredActivity first, ScoredActivity second) {
				int scoreCompare = Integer.compare(second.score, first.score);

				if (scoreCompare != 0) {
					return scoreCompare;
				}

				int lastExecutedCompare =
						compareLastExecutedAt(first.lastExecutedAt, second.lastExecutedAt);

				if (lastExecutedCompare != 0) {
					return lastExecutedCompare;
				}

				int timeCompare = Integer.compare(
						first.activity.getRequiredTime(),
						second.activity.getRequiredTime());

				if (timeCompare != 0) {
					return timeCompare;
				}

				boolean firstChild =
						"CHILD".equals(first.activity.getCategory());
				boolean secondChild =
						"CHILD".equals(second.activity.getCategory());

				if (firstChild != secondChild) {
					return firstChild ? -1 : 1;
				}

				return Integer.compare(first.activity.getId(), second.activity.getId());
			}
		});

		return scoredList;
	}

	/**
	 * 活動1件の除外判定とスコア計算を行う
	 */
	private int calculateScore(Activity activity, List<Activity> activityList,
			Set<Integer> doneTodayActivityIds,
			Map<Integer, LocalDateTime> lastExecutedAtMap,
			Activity latestActivity, int overworkLevel, int childCount,
			LocalDateTime now, LocalTime currentTime,
			String mode, Integer remainingMinutes) {

		// 今日実施済みの活動は重複提案しない。
		if (doneTodayActivityIds.contains(activity.getId())) {
			return EXCLUDED_SCORE;
		}

		/*
		 * FINISHカテゴリは21時以降または5時未満だけ提案
		 * 提案可能時間では非常に高いスコアにして最優先に
		 */
		if ("FINISH".equals(activity.getCategory())) {
			if (isFinishTime(currentTime)) {
				return 9999;
			}

			return EXCLUDED_SCORE;
		}

		/*
		 * TIMEモードでは最後の5分を準備時間として残す
		 * 準備時間を除いた残り時間より長い活動は除外
		 */
		if ("TIME".equals(mode)) {
			if (remainingMinutes == null) {
				return EXCLUDED_SCORE;
			}

			int activityAvailableMinutes =
					remainingMinutes - PREPARE_MINUTES;

			if (activityAvailableMinutes <= 0
					|| activity.getRequiredTime() > activityAvailableMinutes) {

				return EXCLUDED_SCORE;
			}
		}

		/*
		 * 騒音がある活動は17時以上20時未満だけ提案
		 */
		if (Boolean.TRUE.equals(activity.getIsNoise())
				&& !isNoiseAllowedTime(currentTime)) {

			return EXCLUDED_SCORE;
		}

		/*
		 * 家事フローの順番と待ち時間を確認
		 *
		 * flow_groupがない活動、またはflow_stepが1以下の活動は
		 * 前提活動なしで提案可能
		 */
		if (!isFlowAvailable(activity, activityList, lastExecutedAtMap, now)) {
			return EXCLUDED_SCORE;
		}

		int score = activity.getBasePoint();

		// 今日の家事件数による頑張りすぎ防止補正。
		score += calculateOverworkPoint(activity, overworkLevel);

		// 今日まだ子供時間を取っていない場合の補正
		score += calculateChildPoint(activity, childCount);

		// 直前が子供と一緒にできない家事だった場合の補正
		score += calculateLatestActivityPoint(activity, latestActivity);

		// 最終実施日が古い活動を優先する補正
		LocalDateTime lastExecutedAt =
				lastExecutedAtMap.get(activity.getId());

		score += calculateLastExecutedPoint(
				lastExecutedAt,
				now.toLocalDate());

		// 現在時刻に適した活動を優先する補正。
		score += calculateTimeZonePoint(activity, currentTime);

		/*
		 * ゴミ出し補正は他担当のGarbageDao完成後に追加
		 *
		 * 予定：
		 * ・ゴミ出し当日朝5時～8時：「ゴミを出す」+60
		 * ・ゴミ出し前日20時以降：「ゴミをまとめる」+60
		 * ・対象日でないゴミ活動は除外
		 */

		return score;
	}

	/**
	 * 今日の家事件数から頑張りすぎレベルを判定
	 *
	 * 0：通常
	 * 1：家事5件以上
	 * 2：家事7件以上
	 */
	private int calculateOverworkLevel(int houseworkCount) {
		if (houseworkCount >= 7) {
			return 2;
		}

		if (houseworkCount >= 5) {
			return 1;
		}

		return 0;
	}

	/**
	 * 頑張りすぎレベルに応じた補正ポイントを返す
	 */
	private int calculateOverworkPoint(Activity activity, int overworkLevel) {
		String category = activity.getCategory();

		if (overworkLevel == 1) {
			if ("HOUSEWORK".equals(category)) {
				return -10;
			}

			if ("CHILD".equals(category) || "REST".equals(category)) {
				return 20;
			}
		}

		if (overworkLevel == 2) {
			if ("HOUSEWORK".equals(category)) {
				return -20;
			}

			if ("CHILD".equals(category)) {
				return 30;
			}

			if ("REST".equals(category)) {
				return 40;
			}
		}

		return 0;
	}

	/**
	 * 今日まだ子供時間を実施していない場合の補正
	 */
	private int calculateChildPoint(Activity activity, int childCount) {
		if (childCount > 0) {
			return 0;
		}

		String category = activity.getCategory();

		if ("CHILD".equals(category)) {
			return 30;
		}

		if ("HOUSEWORK".equals(category)) {
			if (Boolean.TRUE.equals(activity.getIsCanWithChild())) {
				return 10;
			}

			return -10;
		}

		return 0;
	}

	/**
	 * 直前の活動による補正
	 *
	 * 直前が「子供と一緒にできない家事」の場合：
	 * ・CHILD：+15
	 * ・子供と一緒にできるHOUSEWORK：+20
	 * ・REST：+20
	 */
	private int calculateLatestActivityPoint(Activity activity, Activity latestActivity) {
		if (latestActivity == null) {
			return 0;
		}

		boolean latestIsHousework = "HOUSEWORK".equals(latestActivity.getCategory());

		boolean latestCanWithChild = Boolean.TRUE.equals(latestActivity.getIsCanWithChild());

		if (!latestIsHousework || latestCanWithChild) {
			return 0;
		}

		String category = activity.getCategory();

		if ("CHILD".equals(category)) {
			return 15;
		}

		if ("HOUSEWORK".equals(category)
				&& Boolean.TRUE.equals(activity.getIsCanWithChild())) {

			return 20;
		}

		if ("REST".equals(category)) {
			return 20;
		}

		return 0;
	}

	/**
	 * 最終実施日による補正。
	 *
	 * 今日実施済み：別処理で除外
	 * 昨日実施：0
	 * 2～3日前：+10
	 * 4～6日前：+15
	 * 7日以上前：+20
	 * 一度も実施していない：+10
	 */
	private int calculateLastExecutedPoint(LocalDateTime lastExecutedAt,
			LocalDate today) {

		if (lastExecutedAt == null) {
			return 10;
		}

		long elapsedDays = ChronoUnit.DAYS.between(
				lastExecutedAt.toLocalDate(),
				today);

		if (elapsedDays <= 1) {
			return 0;
		}

		if (elapsedDays <= 3) {
			return 10;
		}

		if (elapsedDays <= 6) {
			return 15;
		}

		return 20;
	}

	/**
	 * 時間帯に応じた補正ポイントを返す
	 */
	private int calculateTimeZonePoint(Activity activity, LocalTime currentTime) {
		String category = activity.getCategory();

		// 昼：10時以上15時未満
		if (isBetween(currentTime, LocalTime.of(10, 0), LocalTime.of(15, 0))) {
			if ("HOUSEWORK".equals(category) || "REST".equals(category)) {
				return 10;
			}
		}

		// 夕方：15時以上18時未満
		if (isBetween(currentTime, LocalTime.of(15, 0), LocalTime.of(18, 0))) {
			if ("CHILD".equals(category)) {
				return 10;
			}

			if ("HOUSEWORK".equals(category)
					&& activity.getRequiredTime() <= 10) {

				return 10;
			}
		}

		// 夜：18時以上21時未満
		if (isBetween(currentTime, LocalTime.of(18, 0), LocalTime.of(21, 0))) {
			if ("CHILD".equals(category)) {
				return 20;
			}

			if ("HOUSEWORK".equals(category)
					&& !Boolean.TRUE.equals(activity.getIsNoise())) {

				return 20;
			}
		}

		// 21時以上または5時未満
		if (isFinishTime(currentTime)) {
			if ("REST".equals(category) || "CHILD".equals(category)) {
				return 15;
			}
		}

		return 0;
	}

	/**
	 * 家事フローの順番と待ち時間を判定する。
	 *
	 * flow_stepが2以上の場合、1つ前のstepが実施済みである必要がある。
	 * また、現在のstepを以前実施している場合は、前のstepがその後に
	 * 実施されていなければ、新しいフローが始まっていないと判定する
	 */
	private boolean isFlowAvailable(Activity activity, List<Activity> activityList,
			Map<Integer, LocalDateTime> lastExecutedAtMap, LocalDateTime now) {

		String flowGroup = activity.getFlowGroup();
		Integer flowStep = activity.getFlowStep();

		if (flowGroup == null || flowGroup.isBlank()
				|| flowStep == null || flowStep <= 1) {

			return true;
		}

		Activity previousActivity =
				findFlowActivity(activityList, flowGroup, flowStep - 1);

		if (previousActivity == null) {
			return false;
		}

		LocalDateTime previousExecutedAt =
				lastExecutedAtMap.get(previousActivity.getId());

		if (previousExecutedAt == null) {
			return false;
		}

		/*
		 * 現在のstepを以前実施済みの場合、
		 * 前のstepが現在のstepより後に行われている必要がある
		 *
		 * 古いフローの履歴だけで後続活動が
		 * 繰り返し提案されることを防止する
		 */
		LocalDateTime currentExecutedAt =
				lastExecutedAtMap.get(activity.getId());

		if (currentExecutedAt != null
				&& !previousExecutedAt.isAfter(currentExecutedAt)) {

			return false;
		}

		/*
		 * wait_minutesは、前のstepを実施してから現在のstepを提案できるまでに必要な待ち時間
		 */
		Integer waitMinutes = activity.getWaitMinutes();

		if (waitMinutes != null && waitMinutes > 0) {
			LocalDateTime availableAt =
					previousExecutedAt.plusMinutes(waitMinutes);

			if (now.isBefore(availableAt)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 同じflow_group内から、指定されたflow_stepの活動を取得
	 */
	private Activity findFlowActivity(List<Activity> activityList,
			String flowGroup, int flowStep) {

		if (activityList == null || flowGroup == null) {
			return null;
		}

		for (Activity activity : activityList) {
			if (!flowGroup.equals(activity.getFlowGroup())) {
				continue;
			}

			Integer activityFlowStep = activity.getFlowStep();

			if (activityFlowStep != null && activityFlowStep == flowStep) {
				return activity;
			}
		}

		return null;
	}

	/**
	 * 最終実施日時を比較
	 *
	 * 一度も実施していない活動を最も古いものとして先にする
	 */
	private int compareLastExecutedAt(LocalDateTime first, LocalDateTime second) {
		if (first == null && second == null) {
			return 0;
		}

		if (first == null) {
			return -1;
		}

		if (second == null) {
			return 1;
		}

		return first.compareTo(second);
	}

	/**
	 * 騒音活動を提案できる時間か判定
	 */
	private boolean isNoiseAllowedTime(LocalTime currentTime) {
		return isBetween(currentTime, LocalTime.of(17, 0), LocalTime.of(20, 0));
	}

	/**
	 * 「今日はここまで」を提案する時間か判定
	 */
	private boolean isFinishTime(LocalTime currentTime) {
		return !currentTime.isBefore(LocalTime.of(21, 0))
				|| currentTime.isBefore(LocalTime.of(5, 0));
	}

	/**
	 * valueがstart以上、end未満か判定
	 */
	private boolean isBetween(LocalTime value, LocalTime start, LocalTime end) {
		return !value.isBefore(start) && value.isBefore(end);
	}

	/**
	 * 活動IDからActivityを検索
	 */
	private Activity findActivityById(List<Activity> activityList, int activityId) {
		if (activityList == null) {
			return null;
		}

		for (Activity activity : activityList) {
			if (activity.getId() == activityId) {
				return activity;
			}
		}

		return null;
	}

	/**
	 * 提案結果をJSONで返す
	 */
	private void writeSuggestionResponse(HttpServletResponse response,
			String status, String mode, Integer remainingMinutes,
			String message, List<ScoredActivity> suggestions) throws IOException {

		StringBuilder json = new StringBuilder();

		json.append("{");
		json.append("\"status\":\"").append(escapeJson(status)).append("\",");
		json.append("\"mode\":\"").append(escapeJson(mode)).append("\",");
		json.append("\"remainingMinutes\":");

		if (remainingMinutes == null) {
			json.append("null");
		} else {
			json.append(remainingMinutes);
		}

		json.append(",");
		json.append("\"message\":");

		if (message == null) {
			json.append("null");
		} else {
			json.append("\"").append(escapeJson(message)).append("\"");
		}

		json.append(",");
		json.append("\"suggestions\":[");

		for (int i = 0; i < suggestions.size(); i++) {
			ScoredActivity scored = suggestions.get(i);
			Activity activity = scored.activity;

			if (i > 0) {
				json.append(",");
			}

			json.append("{");
			json.append("\"activityId\":").append(activity.getId()).append(",");
			json.append("\"category\":\"")
					.append(escapeJson(activity.getCategory())).append("\",");
			json.append("\"title\":\"")
					.append(escapeJson(activity.getActivityName())).append("\",");
			json.append("\"requiredTime\":")
					.append(activity.getRequiredTime()).append(",");
			json.append("\"message\":\"")
					.append(escapeJson(createActivityMessage(activity))).append("\"");
			json.append("}");
		}

		json.append("]");
		json.append("}");

		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(json.toString());
	}

	/**
	 * 活動カテゴリに応じて表示メッセージを作成
	 */
	private String createActivityMessage(Activity activity) {
		String category = activity.getCategory();

		if ("REST".equals(category)) {
			return activity.getRequiredTime() + "分だけ休憩して、"
					+ activity.getActivityName() + "をしてみましょう。";
		}

		if ("CHILD".equals(category)) {
			return activity.getRequiredTime() + "分だけ、"
					+ activity.getActivityName() + "をしてみましょう。";
		}

		return activity.getRequiredTime() + "分で、"
				+ activity.getActivityName() + "をやってみましょう。";
	}

	/**
	 * JSON内で使用する文字列をエスケープ
	 */
	private String escapeJson(String value) {
		if (value == null) {
			return "";
		}

		return value.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\r", "\\r")
				.replace("\n", "\\n")
				.replace("\t", "\\t");
	}

	/**
	 * JSONレスポンスの文字コードとContent-Typeを設定
	 */
	private void setJsonResponse(HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
	}

	/**
	 * エラー内容をJSONで返す
	 */
	private void writeError(HttpServletResponse response, int status,
			String message) throws IOException {

		response.setStatus(status);

		String json = "{"
				+ "\"success\":false,"
				+ "\"message\":\"" + escapeJson(message) + "\""
				+ "}";

		response.getWriter().write(json);
	}

	/**
	 * 活動、計算後スコア、最終実施日時を保持する内部クラス
	 */
	private static class ScoredActivity {
		private Activity activity;
		private int score;
		private LocalDateTime lastExecutedAt;
	}
}
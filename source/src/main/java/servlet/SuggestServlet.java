package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
import dao.GarbageDao;
import dto.Activity;
import dto.LoginUser;
import dto.RecordHistoryDto;

/**
 * 活動を提案するServlet
 */
@WebServlet("/SuggestServlet")
public class SuggestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/** 最後に確保する準備時間 */
	private static final int PREPARE_MINUTES = 5;

	/** 候補から除外するスコア */
	private static final int EXCLUDED_SCORE = -9999;

	/** ゴミ活動の補正ポイント */
	private static final int GARBAGE_PRIORITY_POINT = 60;

	/** ゴミをまとめる活動名 */
	private static final String GARBAGE_COLLECT_NAME =
			"ゴミをまとめる";

	/** ゴミを出す活動名 */
	private static final String GARBAGE_TAKE_OUT_NAME =
			"ゴミを出す";

	/** 提案モードを保存するセッション属性名 */
	private static final String SESSION_MODE =
			"suggestionMode";

	/** 残り時間を保存するセッション属性名 */
	private static final String SESSION_REMAINING_MINUTES =
			"suggestionRemainingMinutes";

	/** 直前に画面へ返した活動ID */
	private static final String SESSION_LAST_SUGGESTED_ACTIVITY_ID =
			"lastSuggestedActivityId";

	/** 直前に画面へ返したカテゴリ */
	private static final String SESSION_LAST_SUGGESTED_CATEGORY =
			"lastSuggestedCategory";

	/** 同じカテゴリが先頭提案になった連続回数 */
	private static final String SESSION_SAME_CATEGORY_COUNT =
			"sameSuggestedCategoryCount";

	/** CHILDカテゴリを連続提案するときの減点 */
	private static final int CHILD_REPEAT_PENALTY = 25;

	/** 同一活動を連続提案するときの減点 */
	private static final int SAME_ACTIVITY_REPEAT_PENALTY = 50;

	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		setJsonResponse(response);

		writeError(
				response,
				HttpServletResponse.SC_METHOD_NOT_ALLOWED,
				"POSTでアクセスしてください。");
	}

	/**
	 * actionに応じて処理を分岐
	 */
	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		setJsonResponse(response);

		HttpSession session =
				request.getSession(false);

		if (session == null) {

			writeError(
					response,
					HttpServletResponse.SC_UNAUTHORIZED,
					"ログイン情報がありません。");
			return;
		}

		LoginUser loginUser =
				(LoginUser) session.getAttribute(
						"idnamepw");

		if (loginUser == null) {

			writeError(
					response,
					HttpServletResponse.SC_UNAUTHORIZED,
					"ログイン情報がありません。");
			return;
		}

		String action =
				request.getParameter("action");

		if (action == null || action.isEmpty()) {

			writeError(
					response,
					HttpServletResponse.SC_BAD_REQUEST,
					"actionが指定されていません。");
			return;
		}

		try {

			int userId =
					loginUser.getUserId();

			if ("start".equals(action)) {

				startSuggestion(
						request,
						response,
						session,
						userId);

			} else if ("complete".equals(action)) {

				completeSuggestion(
						request,
						response,
						session,
						userId);

			} else if ("refresh".equals(action)) {

				refreshSuggestion(
						response,
						session,
						userId);

			} else {

				writeError(
						response,
						HttpServletResponse.SC_BAD_REQUEST,
						"不正なactionです。");
			}

		} catch (NumberFormatException e) {

			writeError(
					response,
					HttpServletResponse.SC_BAD_REQUEST,
					"数値の指定が正しくありません。");

		} catch (Exception e) {

			e.printStackTrace();

			writeError(
					response,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"提案処理中にエラーが発生しました。");
		}
	}

	/**
	 * 提案を開始
	 */
	private void startSuggestion(
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session,
			int userId)
			throws IOException {

		String mode =
				request.getParameter("mode");

		if ("TIME".equals(mode)) {

			String timeText =
					request.getParameter("time");

			if (timeText == null
					|| timeText.isEmpty()) {

				writeError(
						response,
						HttpServletResponse.SC_BAD_REQUEST,
						"時間が指定されていません。");
				return;
			}

			int selectedMinutes =
					Integer.parseInt(timeText);

			if (selectedMinutes != 10
					&& selectedMinutes != 15
					&& selectedMinutes != 30
					&& selectedMinutes != 45
					&& selectedMinutes != 60) {

				writeError(
						response,
						HttpServletResponse.SC_BAD_REQUEST,
						"時間は10分、15分、30分、45分、60分から選択してください。");
				return;
			}

			session.setAttribute(
					SESSION_MODE,
					"TIME");

			session.setAttribute(
					SESSION_REMAINING_MINUTES,
					selectedMinutes);

		} else if ("AUTO".equals(mode)) {

			session.setAttribute(
					SESSION_MODE,
					"AUTO");

			session.removeAttribute(
					SESSION_REMAINING_MINUTES);

		} else {

			writeError(
					response,
					HttpServletResponse.SC_BAD_REQUEST,
					"modeにはTIMEまたはAUTOを指定してください。");
			return;
		}

		/*
		 * 新しく提案を開始した場合は、
		 * 前回の連続提案情報をリセットする。
		 */
		resetSuggestionHistory(session);

		writeCurrentSuggestions(
				response,
				session,
				userId);
	}

	/**
	 * 活動完了後に残り時間を更新
	 */
	private void completeSuggestion(
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session,
			int userId)
			throws IOException {

		String mode =
				(String) session.getAttribute(
						SESSION_MODE);

		if (mode == null) {

			writeError(
					response,
					HttpServletResponse.SC_BAD_REQUEST,
					"提案が開始されていません。");
			return;
		}

		String activityIdText =
				request.getParameter("activityId");

		if (activityIdText == null
				|| activityIdText.isEmpty()) {

			writeError(
					response,
					HttpServletResponse.SC_BAD_REQUEST,
					"activityIdが指定されていません。");
			return;
		}

		int activityId =
				Integer.parseInt(activityIdText);

		if (activityId <= 0) {

			writeError(
					response,
					HttpServletResponse.SC_BAD_REQUEST,
					"activityIdが不正です。");
			return;
		}

		ActivityDao activityDao =
				new ActivityDao();

		List<Activity> activityList =
				activityDao.selectAll();

		Activity completedActivity =
				findActivityById(
						activityList,
						activityId);

		if (completedActivity == null) {

			writeError(
					response,
					HttpServletResponse.SC_NOT_FOUND,
					"指定された活動が存在しません。");
			return;
		}

		if ("TIME".equals(mode)) {

			Integer remainingMinutes =
					(Integer) session.getAttribute(
							SESSION_REMAINING_MINUTES);

			if (remainingMinutes == null) {

				writeError(
						response,
						HttpServletResponse.SC_BAD_REQUEST,
						"残り時間が取得できません。");
				return;
			}

			remainingMinutes -=
					completedActivity.getRequiredTime();

			if (remainingMinutes < 0) {
				remainingMinutes = 0;
			}

			session.setAttribute(
					SESSION_REMAINING_MINUTES,
					remainingMinutes);
		}

		writeCurrentSuggestions(
				response,
				session,
				userId);
	}

	/**
	 * 現在の条件で候補を再取得
	 */
	private void refreshSuggestion(
			HttpServletResponse response,
			HttpSession session,
			int userId)
			throws IOException {

		String mode =
				(String) session.getAttribute(
						SESSION_MODE);

		if (mode == null) {

			writeError(
					response,
					HttpServletResponse.SC_BAD_REQUEST,
					"提案が開始されていません。");
			return;
		}

		writeCurrentSuggestions(
				response,
				session,
				userId);
	}

	/**
	 * 現在の提案をJSONで返す
	 */
	private void writeCurrentSuggestions(
			HttpServletResponse response,
			HttpSession session,
			int userId)
			throws IOException {

		String mode =
				(String) session.getAttribute(
						SESSION_MODE);

		Integer remainingMinutes = null;

		if ("TIME".equals(mode)) {

			remainingMinutes =
					(Integer) session.getAttribute(
							SESSION_REMAINING_MINUTES);

			if (remainingMinutes == null) {

				writeError(
						response,
						HttpServletResponse.SC_BAD_REQUEST,
						"残り時間が取得できません。");
				return;
			}

			if (remainingMinutes
					<= PREPARE_MINUTES) {

				writeSuggestionResponse(
						response,
						"PREPARE",
						mode,
						remainingMinutes,
						"残り時間でできる活動がないため、用事の準備をしましょう。",
						new ArrayList<ScoredActivity>());
				return;
			}
		}

		List<ScoredActivity> suggestions =
				createSuggestions(
						userId,
						mode,
						remainingMinutes,
						session);

		if (suggestions.isEmpty()) {

			if ("TIME".equals(mode)) {

				writeSuggestionResponse(
						response,
						"NO_SUGGESTION",
						mode,
						remainingMinutes,
						"現在の条件で提案できる活動がありません。",
						suggestions);

			} else {

				writeSuggestionResponse(
						response,
						"FINISH",
						mode,
						null,
						"今日の提案はここまでです。無理せず休みましょう。",
						suggestions);
			}

			return;
		}

		ScoredActivity firstSuggestion =
				suggestions.get(0);

		if ("FINISH".equals(
				firstSuggestion.activity.getCategory())) {

			writeSuggestionResponse(
					response,
					"FINISH",
					mode,
					remainingMinutes,
					"今日はここまでで大丈夫です。無理せず休みましょう。",
					new ArrayList<ScoredActivity>());
			return;
		}

		/*
		 * 実際に画面へ返す先頭候補を記録する。
		 * 次回の再提案時に同カテゴリを減点する。
		 */
		rememberSuggestion(
				session,
				firstSuggestion);

		writeSuggestionResponse(
				response,
				"CONTINUE",
				mode,
				remainingMinutes,
				null,
				suggestions);
	}

	/**
	 * 提案候補を作成
	 */
	private List<ScoredActivity> createSuggestions(
			int userId,
			String mode,
			Integer remainingMinutes,
			HttpSession session) {

		ActivityDao activityDao =
				new ActivityDao();

		ActivityHistoryDao historyDao =
				new ActivityHistoryDao();

		GarbageDao garbageDao =
				new GarbageDao();

		List<Activity> activityList =
				activityDao.selectAll();

		if (activityList == null) {
			activityList =
					new ArrayList<>();
		}

		LocalDateTime now =
				LocalDateTime.now();

		LocalDate today =
				now.toLocalDate();

		LocalDate tomorrow =
				today.plusDays(1);

		LocalDateTime startAt =
				today.atStartOfDay();

		LocalDateTime endAt =
				startAt.plusDays(1);

		List<String> todayGarbageNames =
				garbageDao.selectGarbageNamesByDate(
						userId,
						today);

		List<String> tomorrowGarbageNames =
				garbageDao.selectGarbageNamesByDate(
						userId,
						tomorrow);

		if (todayGarbageNames == null) {
			todayGarbageNames =
					new ArrayList<>();
		}

		if (tomorrowGarbageNames == null) {
			tomorrowGarbageNames =
					new ArrayList<>();
		}

		boolean garbageDayToday =
				!todayGarbageNames.isEmpty();

		boolean garbageDayTomorrow =
				!tomorrowGarbageNames.isEmpty();

		List<RecordHistoryDto> todayHistoryList =
				historyDao.findRecordHistoryList(
						userId,
						startAt,
						endAt);

		if (todayHistoryList == null) {
			todayHistoryList =
					new ArrayList<>();
		}

		Set<Integer> doneTodayActivityIds =
				new HashSet<>();

		int houseworkCount = 0;
		int childCount = 0;

		boolean garbageActivityDoneToday =
				false;

		for (RecordHistoryDto history
				: todayHistoryList) {

			doneTodayActivityIds.add(
					history.getActivityId());

			if ("HOUSEWORK".equals(
					history.getCategory())) {

				houseworkCount++;
			}

			if ("CHILD".equals(
					history.getCategory())) {

				childCount++;
			}

			Activity doneActivity =
					findActivityById(
							activityList,
							history.getActivityId());

			if (doneActivity != null
					&& isGarbageActivity(
							doneActivity)) {

				garbageActivityDoneToday =
						true;
			}
		}

		Map<Integer, LocalDateTime> lastExecutedAtMap =
				historyDao.findLastExecutedAtMap(
						userId);

		if (lastExecutedAtMap == null) {
			lastExecutedAtMap =
					new HashMap<>();
		}

		Integer lastSuggestedActivityId =
				getSessionInteger(
						session,
						SESSION_LAST_SUGGESTED_ACTIVITY_ID);

		String lastSuggestedCategory =
				(String) session.getAttribute(
						SESSION_LAST_SUGGESTED_CATEGORY);

		Integer sameCategoryCountValue =
				getSessionInteger(
						session,
						SESSION_SAME_CATEGORY_COUNT);

		int sameCategoryCount =
				sameCategoryCountValue == null
						? 0
						: sameCategoryCountValue;

		RecordHistoryDto latestHistory =
				historyDao.findLatestHistory(
						userId);

		Activity latestActivity = null;

		if (latestHistory != null) {

			latestActivity =
					findActivityById(
							activityList,
							latestHistory.getActivityId());
		}

		int overworkLevel =
				calculateOverworkLevel(
						houseworkCount);

		LocalTime currentTime =
				now.toLocalTime();

		List<ScoredActivity> scoredList =
				new ArrayList<>();

		for (Activity activity : activityList) {

			int score =
					calculateScore(
							activity,
							activityList,
							doneTodayActivityIds,
							lastExecutedAtMap,
							latestActivity,
							overworkLevel,
							childCount,
							garbageActivityDoneToday,
							garbageDayToday,
							garbageDayTomorrow,
							lastSuggestedActivityId,
							lastSuggestedCategory,
							sameCategoryCount,
							now,
							currentTime,
							mode,
							remainingMinutes);

			if (score == EXCLUDED_SCORE) {
				continue;
			}

			ScoredActivity scoredActivity =
					new ScoredActivity();

			scoredActivity.activity =
					activity;

			scoredActivity.score =
					score;

			scoredActivity.lastExecutedAt =
					lastExecutedAtMap.get(
							activity.getId());

			scoredActivity.garbageNames =
					getGarbageNamesForActivity(
							activity,
							todayGarbageNames,
							tomorrowGarbageNames);

			scoredList.add(
					scoredActivity);
		}

		scoredList.sort(
				new Comparator<ScoredActivity>() {

					@Override
					public int compare(
							ScoredActivity first,
							ScoredActivity second) {

						int scoreCompare =
								Integer.compare(
										second.score,
										first.score);

						if (scoreCompare != 0) {
							return scoreCompare;
						}

						int lastExecutedCompare =
								compareLastExecutedAt(
										first.lastExecutedAt,
										second.lastExecutedAt);

						if (lastExecutedCompare != 0) {
							return lastExecutedCompare;
						}

						int timeCompare =
								Integer.compare(
										first.activity
												.getRequiredTime(),
										second.activity
												.getRequiredTime());

						if (timeCompare != 0) {
							return timeCompare;
						}

						/*
						 * 同点時にCHILDを固定優先しない。
						 */
						return Integer.compare(
								first.activity.getId(),
								second.activity.getId());
					}
				});

		/*
		 * JSON内の候補順でもCHILDが連続しないようにする。
		 */
		return diversifyChildSuggestions(
				scoredList);
	}

	/**
	 * 活動1件のスコアを計算
	 */
	private int calculateScore(
			Activity activity,
			List<Activity> activityList,
			Set<Integer> doneTodayActivityIds,
			Map<Integer, LocalDateTime> lastExecutedAtMap,
			Activity latestActivity,
			int overworkLevel,
			int childCount,
			boolean garbageActivityDoneToday,
			boolean garbageDayToday,
			boolean garbageDayTomorrow,
			Integer lastSuggestedActivityId,
			String lastSuggestedCategory,
			int sameCategoryCount,
			LocalDateTime now,
			LocalTime currentTime,
			String mode,
			Integer remainingMinutes) {

		/*
		 * 今日実施済みなら除外する。
		 */
		if (doneTodayActivityIds.contains(
				activity.getId())) {

			return EXCLUDED_SCORE;
		}

		/*
		 * FINISHは21時以降または5時未満だけ有効。
		 */
		if ("FINISH".equals(
				activity.getCategory())) {

			if (isFinishTime(currentTime)) {
				return 9999;
			}

			return EXCLUDED_SCORE;
		}

		int garbagePoint =
				calculateGarbagePoint(
						activity,
						garbageActivityDoneToday,
						garbageDayToday,
						garbageDayTomorrow,
						currentTime);

		if (garbagePoint == EXCLUDED_SCORE) {
			return EXCLUDED_SCORE;
		}

		if ("TIME".equals(mode)) {

			if (remainingMinutes == null) {
				return EXCLUDED_SCORE;
			}

			int activityAvailableMinutes =
					remainingMinutes
					- PREPARE_MINUTES;

			if (activityAvailableMinutes <= 0
					|| activity.getRequiredTime()
					> activityAvailableMinutes) {

				return EXCLUDED_SCORE;
			}
		}

		if (Boolean.TRUE.equals(
				activity.getIsNoise())
				&& !isNoiseAllowedTime(
						currentTime)) {

			return EXCLUDED_SCORE;
		}

		if (!isFlowAvailable(
				activity,
				activityList,
				lastExecutedAtMap,
				now)) {

			return EXCLUDED_SCORE;
		}

		/*
		 * スコアは毎回基本ポイントから作り直す。
		 * 前回の計算値は保持しない。
		 */
		int score =
				activity.getBasePoint();

		score += garbagePoint;

		score += calculateOverworkPoint(
				activity,
				overworkLevel);

		score += calculateChildPoint(
				activity,
				childCount);

		score += calculateLatestActivityPoint(
				activity,
				latestActivity);

		score += calculateSuggestedRepeatPoint(
				activity,
				lastSuggestedActivityId,
				lastSuggestedCategory,
				sameCategoryCount);

		LocalDateTime lastExecutedAt =
				lastExecutedAtMap.get(
						activity.getId());

		score += calculateLastExecutedPoint(
				lastExecutedAt,
				now.toLocalDate());

		score += calculateTimeZonePoint(
				activity,
				currentTime);

		return score;
	}

	/**
	 * ゴミ活動の補正を計算
	 */
	private int calculateGarbagePoint(
			Activity activity,
			boolean garbageActivityDoneToday,
			boolean garbageDayToday,
			boolean garbageDayTomorrow,
			LocalTime currentTime) {

		if (!isGarbageActivity(activity)) {
			return 0;
		}

		if (garbageActivityDoneToday) {
			return EXCLUDED_SCORE;
		}

		String activityName =
				activity.getActivityName();

		if (GARBAGE_TAKE_OUT_NAME.equals(
				activityName)) {

			if (!garbageDayToday
					|| !isGarbageTakeOutTime(
							currentTime)) {

				return EXCLUDED_SCORE;
			}

			return GARBAGE_PRIORITY_POINT;
		}

		if (GARBAGE_COLLECT_NAME.equals(
				activityName)) {

			if (!garbageDayTomorrow
					|| !isGarbageCollectTime(
							currentTime)) {

				return EXCLUDED_SCORE;
			}

			return GARBAGE_PRIORITY_POINT;
		}

		return EXCLUDED_SCORE;
	}

	/**
	 * ゴミ関連活動か判定
	 */
	private boolean isGarbageActivity(
			Activity activity) {

		if (activity == null) {
			return false;
		}

		if (activity.getHasGarbage()) {
			return true;
		}

		String activityName =
				activity.getActivityName();

		return GARBAGE_COLLECT_NAME.equals(
				activityName)
				|| GARBAGE_TAKE_OUT_NAME.equals(
						activityName);
	}

	/**
	 * 活動に対応するゴミ分類名を取得
	 */
	private List<String> getGarbageNamesForActivity(
			Activity activity,
			List<String> todayGarbageNames,
			List<String> tomorrowGarbageNames) {

		if (activity == null) {
			return new ArrayList<>();
		}

		String activityName =
				activity.getActivityName();

		if (GARBAGE_TAKE_OUT_NAME.equals(
				activityName)) {

			return new ArrayList<>(
					todayGarbageNames);
		}

		if (GARBAGE_COLLECT_NAME.equals(
				activityName)) {

			return new ArrayList<>(
					tomorrowGarbageNames);
		}

		return new ArrayList<>();
	}

	/**
	 * ゴミを出せる時間か判定
	 */
	private boolean isGarbageTakeOutTime(
			LocalTime currentTime) {

		return isBetween(
				currentTime,
				LocalTime.of(5, 0),
				LocalTime.of(8, 0));
	}

	/**
	 * ゴミをまとめられる時間か判定
	 */
	private boolean isGarbageCollectTime(
			LocalTime currentTime) {

		return !currentTime.isBefore(
				LocalTime.of(20, 0));
	}

	/**
	 * 頑張りすぎレベルを判定
	 */
	private int calculateOverworkLevel(
			int houseworkCount) {

		if (houseworkCount >= 7) {
			return 2;
		}

		if (houseworkCount >= 5) {
			return 1;
		}

		return 0;
	}

	/**
	 * 頑張りすぎ補正を計算
	 */
	private int calculateOverworkPoint(
			Activity activity,
			int overworkLevel) {

		String category =
				activity.getCategory();

		if (overworkLevel == 1) {

			if ("HOUSEWORK".equals(category)) {
				return -5;
			}

			if ("CHILD".equals(category)
					|| "REST".equals(category)) {

				return 10;
			}
		}

		if (overworkLevel == 2) {

			if ("HOUSEWORK".equals(category)) {
				return -10;
			}

			if ("CHILD".equals(category)) {
				return 15;
			}

			if ("REST".equals(category)) {
				return 20;
			}
		}

		return 0;
	}

	/**
	 * 子供時間補正を計算
	 */
	private int calculateChildPoint(
			Activity activity,
			int childCount) {

		/*
		 * 今日すでに子供時間を実施している場合は、
		 * 子供時間補正を付けない。
		 */
		if (childCount > 0) {
			return 0;
		}

		String category =
				activity.getCategory();

		/*
		 * 今日まだ子供時間がない場合だけ少し優先する。
		 */
		if ("CHILD".equals(category)) {
			return 10;
		}

		/*
		 * 子供と一緒にできる家事も少し優先する。
		 */
		if ("HOUSEWORK".equals(category)
				&& Boolean.TRUE.equals(
						activity.getIsCanWithChild())) {

			return 5;
		}

		return 0;
	}

	/**
	 * 直前に完了した活動による補正を計算
	 */
	private int calculateLatestActivityPoint(
			Activity activity,
			Activity latestActivity) {

		if (latestActivity == null) {
			return 0;
		}

		String currentCategory =
				activity.getCategory();

		String latestCategory =
				latestActivity.getCategory();

		/*
		 * 子供時間を完了した直後は、
		 * 次の子供時間を減点する。
		 */
		if ("CHILD".equals(latestCategory)
				&& "CHILD".equals(currentCategory)) {

			return -20;
		}

		/*
		 * 休憩も連続しすぎないよう減点する。
		 */
		if ("REST".equals(latestCategory)
				&& "REST".equals(currentCategory)) {

			return -15;
		}

		boolean latestIsHousework =
				"HOUSEWORK".equals(
						latestCategory);

		boolean latestCanWithChild =
				Boolean.TRUE.equals(
						latestActivity
								.getIsCanWithChild());

		/*
		 * 直前が子供と一緒にできない家事の場合だけ、
		 * 次の子供時間・休憩などを少し優先する。
		 */
		if (!latestIsHousework
				|| latestCanWithChild) {

			return 0;
		}

		if ("CHILD".equals(currentCategory)) {
			return 5;
		}

		if ("HOUSEWORK".equals(currentCategory)
				&& Boolean.TRUE.equals(
						activity.getIsCanWithChild())) {

			return 10;
		}

		if ("REST".equals(currentCategory)) {
			return 5;
		}

		return 0;
	}

	/**
	 * 直前に画面へ表示した提案との重複補正を計算
	 */
	private int calculateSuggestedRepeatPoint(
			Activity activity,
			Integer lastSuggestedActivityId,
			String lastSuggestedCategory,
			int sameCategoryCount) {

		int point = 0;

		/*
		 * 再提案で同じ活動が再び先頭に来ることを防ぐ。
		 */
		if (lastSuggestedActivityId != null
				&& activity.getId()
					== lastSuggestedActivityId) {

			point -= SAME_ACTIVITY_REPEAT_PENALTY;
		}

		String category =
				activity.getCategory();

		/*
		 * 直前の提案がCHILDだった場合、
		 * 次のCHILD候補を減点する。
		 */
		if ("CHILD".equals(lastSuggestedCategory)
				&& "CHILD".equals(category)) {

			int additionalPenalty =
					Math.min(
							Math.max(
									sameCategoryCount - 1,
									0) * 10,
							20);

			point -= CHILD_REPEAT_PENALTY
					+ additionalPenalty;
		}

		/*
		 * 休憩も連続しすぎないよう軽く減点する。
		 */
		if ("REST".equals(lastSuggestedCategory)
				&& "REST".equals(category)) {

			point -= 15;
		}

		return point;
	}

	/**
	 * CHILD候補が候補一覧内で連続しないように並べ替える
	 */
	private List<ScoredActivity> diversifyChildSuggestions(
			List<ScoredActivity> sortedList) {

		List<ScoredActivity> remaining =
				new ArrayList<>(sortedList);

		List<ScoredActivity> result =
				new ArrayList<>();

		String previousCategory = null;

		while (!remaining.isEmpty()) {

			int selectedIndex = 0;

			ScoredActivity highest =
					remaining.get(0);

			/*
			 * 直前がCHILDで、次の最高点もCHILDの場合、
			 * HOUSEWORKまたはRESTがあれば間に挟む。
			 */
			if ("CHILD".equals(previousCategory)
					&& "CHILD".equals(
							highest.activity.getCategory())) {

				int nonChildIndex =
						findFirstNonChildIndex(
								remaining);

				if (nonChildIndex >= 0) {
					selectedIndex =
							nonChildIndex;
				}
			}

			ScoredActivity selected =
					remaining.remove(
							selectedIndex);

			result.add(selected);

			previousCategory =
					selected.activity.getCategory();
		}

		return result;
	}

	/**
	 * CHILD以外の候補位置を取得
	 */
	private int findFirstNonChildIndex(
			List<ScoredActivity> list) {

		for (int i = 0;
				i < list.size();
				i++) {

			String category =
					list.get(i)
							.activity
							.getCategory();

			if (!"CHILD".equals(category)
					&& !"FINISH".equals(category)) {

				return i;
			}
		}

		return -1;
	}

	/**
	 * 新しく提案を開始した場合に連続提案情報をリセット
	 */
	private void resetSuggestionHistory(
			HttpSession session) {

		session.removeAttribute(
				SESSION_LAST_SUGGESTED_ACTIVITY_ID);

		session.removeAttribute(
				SESSION_LAST_SUGGESTED_CATEGORY);

		session.removeAttribute(
				SESSION_SAME_CATEGORY_COUNT);
	}

	/**
	 * 今回画面へ返した先頭候補をセッションへ保存
	 */
	private void rememberSuggestion(
			HttpSession session,
			ScoredActivity suggestion) {

		if (suggestion == null
				|| suggestion.activity == null) {

			return;
		}

		String currentCategory =
				suggestion.activity.getCategory();

		String previousCategory =
				(String) session.getAttribute(
						SESSION_LAST_SUGGESTED_CATEGORY);

		Integer previousCountValue =
				getSessionInteger(
						session,
						SESSION_SAME_CATEGORY_COUNT);

		int previousCount =
				previousCountValue == null
						? 0
						: previousCountValue;

		int currentCount =
				currentCategory != null
				&& currentCategory.equals(
						previousCategory)
						? previousCount + 1
						: 1;

		session.setAttribute(
				SESSION_LAST_SUGGESTED_ACTIVITY_ID,
				suggestion.activity.getId());

		session.setAttribute(
				SESSION_LAST_SUGGESTED_CATEGORY,
				currentCategory);

		session.setAttribute(
				SESSION_SAME_CATEGORY_COUNT,
				currentCount);
	}

	/**
	 * セッション属性をIntegerとして安全に取得
	 */
	private Integer getSessionInteger(
			HttpSession session,
			String attributeName) {

		Object value =
				session.getAttribute(
						attributeName);

		if (value instanceof Integer) {
			return (Integer) value;
		}

		return null;
	}

	/**
	 * 最終実施日補正を計算
	 */
	private int calculateLastExecutedPoint(
			LocalDateTime lastExecutedAt,
			LocalDate today) {

		if (lastExecutedAt == null) {
			return 10;
		}

		long elapsedDays =
				ChronoUnit.DAYS.between(
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
	 * 時間帯補正を計算
	 */
	private int calculateTimeZonePoint(
			Activity activity,
			LocalTime currentTime) {

		String category =
				activity.getCategory();

		/*
		 * 昼：10時以上15時未満
		 */
		if (isBetween(
				currentTime,
				LocalTime.of(10, 0),
				LocalTime.of(15, 0))) {

			if ("HOUSEWORK".equals(category)
					|| "REST".equals(category)) {

				return 10;
			}
		}

		/*
		 * 夕方：15時以上18時未満
		 */
		if (isBetween(
				currentTime,
				LocalTime.of(15, 0),
				LocalTime.of(18, 0))) {

			if ("CHILD".equals(category)) {
				return 5;
			}

			if ("HOUSEWORK".equals(category)
					&& activity.getRequiredTime()
						<= 10) {

				return 10;
			}
		}

		/*
		 * 夜：18時以上21時未満
		 */
		if (isBetween(
				currentTime,
				LocalTime.of(18, 0),
				LocalTime.of(21, 0))) {

			if ("CHILD".equals(category)) {
				return 10;
			}

			if ("HOUSEWORK".equals(category)
					&& !Boolean.TRUE.equals(
							activity.getIsNoise())) {

				return 20;
			}
		}

		/*
		 * 21時以上または5時未満
		 */
		if (isFinishTime(currentTime)) {

			if ("REST".equals(category)
					|| "CHILD".equals(category)) {

				return 10;
			}
		}

		return 0;
	}

	/**
	 * 家事フローを判定
	 */
	private boolean isFlowAvailable(
			Activity activity,
			List<Activity> activityList,
			Map<Integer, LocalDateTime> lastExecutedAtMap,
			LocalDateTime now) {

		String flowGroup =
				activity.getFlowGroup();

		Integer flowStep =
				activity.getFlowStep();

		if (flowGroup == null
				|| flowGroup.isBlank()
				|| flowStep == null
				|| flowStep <= 1) {

			return true;
		}

		Activity previousActivity =
				findFlowActivity(
						activityList,
						flowGroup,
						flowStep - 1);

		if (previousActivity == null) {
			return false;
		}

		LocalDateTime previousExecutedAt =
				lastExecutedAtMap.get(
						previousActivity.getId());

		if (previousExecutedAt == null) {
			return false;
		}

		LocalDateTime currentExecutedAt =
				lastExecutedAtMap.get(
						activity.getId());

		if (currentExecutedAt != null
				&& !previousExecutedAt.isAfter(
						currentExecutedAt)) {

			return false;
		}

		int waitMinutes =
				activity.getWaitMinutes();

		if (waitMinutes > 0) {

			LocalDateTime availableAt =
					previousExecutedAt.plusMinutes(
							waitMinutes);

			if (now.isBefore(availableAt)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 同じフローの活動を検索
	 */
	private Activity findFlowActivity(
			List<Activity> activityList,
			String flowGroup,
			int flowStep) {

		if (activityList == null
				|| flowGroup == null) {

			return null;
		}

		for (Activity activity : activityList) {

			if (!flowGroup.equals(
					activity.getFlowGroup())) {

				continue;
			}

			Integer activityFlowStep =
					activity.getFlowStep();

			if (activityFlowStep != null
					&& activityFlowStep
						== flowStep) {

				return activity;
			}
		}

		return null;
	}

	/**
	 * 最終実施日時を比較
	 */
	private int compareLastExecutedAt(
			LocalDateTime first,
			LocalDateTime second) {

		if (first == null
				&& second == null) {

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
	 * 騒音活動の時間を判定
	 */
	private boolean isNoiseAllowedTime(
			LocalTime currentTime) {

		return isBetween(
				currentTime,
				LocalTime.of(8, 0),
				LocalTime.of(20, 0));
	}

	/**
	 * FINISHの時間を判定
	 */
	private boolean isFinishTime(
			LocalTime currentTime) {

		return !currentTime.isBefore(
				LocalTime.of(21, 0))
				|| currentTime.isBefore(
						LocalTime.of(5, 0));
	}

	/**
	 * 時刻範囲を判定
	 */
	private boolean isBetween(
			LocalTime value,
			LocalTime start,
			LocalTime end) {

		return !value.isBefore(start)
				&& value.isBefore(end);
	}

	/**
	 * 活動IDから検索
	 */
	private Activity findActivityById(
			List<Activity> activityList,
			int activityId) {

		if (activityList == null) {
			return null;
		}

		for (Activity activity : activityList) {

			if (activity.getId()
					== activityId) {

				return activity;
			}
		}

		return null;
	}

	/**
	 * 提案結果をJSONで返す
	 */
	private void writeSuggestionResponse(
			HttpServletResponse response,
			String status,
			String mode,
			Integer remainingMinutes,
			String message,
			List<ScoredActivity> suggestions)
			throws IOException {

		StringBuilder json =
				new StringBuilder();

		json.append("{");

		json.append("\"status\":\"")
				.append(escapeJson(status))
				.append("\",");

		json.append("\"mode\":\"")
				.append(escapeJson(mode))
				.append("\",");

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

			json.append("\"")
					.append(escapeJson(message))
					.append("\"");
		}

		json.append(",");
		json.append("\"suggestions\":[");

		for (int i = 0;
				i < suggestions.size();
				i++) {

			ScoredActivity scored =
					suggestions.get(i);

			Activity activity =
					scored.activity;

			if (i > 0) {
				json.append(",");
			}

			json.append("{");

			json.append("\"activityId\":")
					.append(activity.getId())
					.append(",");

			json.append("\"category\":\"")
					.append(
							escapeJson(
									activity.getCategory()))
					.append("\",");

			json.append("\"title\":\"")
					.append(
							escapeJson(
									activity.getActivityName()))
					.append("\",");

			json.append("\"requiredTime\":")
					.append(
							activity.getRequiredTime())
					.append(",");

			json.append("\"message\":\"")
					.append(
							escapeJson(
									createActivityMessage(
											scored)))
					.append("\"");

			json.append("}");
		}

		json.append("]");
		json.append("}");

		response.setStatus(
				HttpServletResponse.SC_OK);

		response.getWriter().write(
				json.toString());
	}

	/**
	 * 活動メッセージを作成
	 */
	private String createActivityMessage(
			ScoredActivity scored) {

		Activity activity =
				scored.activity;

		String activityName =
				activity.getActivityName();

		String garbageNames =
				String.join(
						"、",
						scored.garbageNames);

		if (GARBAGE_TAKE_OUT_NAME.equals(
				activityName)
				&& !garbageNames.isBlank()) {

			return "今日は"
					+ garbageNames
					+ "の日です。"
					+ activity.getRequiredTime()
					+ "分で、ゴミを出してみましょう。";
		}

		if (GARBAGE_COLLECT_NAME.equals(
				activityName)
				&& !garbageNames.isBlank()) {

			return "明日は"
					+ garbageNames
					+ "の日です。"
					+ activity.getRequiredTime()
					+ "分で、ゴミをまとめてみましょう。";
		}

		String category =
				activity.getCategory();

		if ("REST".equals(category)) {

			return activity.getRequiredTime()
					+ "分だけ休憩して、"
					+ activityName
					+ "をしてみましょう。";
		}

		if ("CHILD".equals(category)) {

			return activity.getRequiredTime()
					+ "分だけ、"
					+ activityName
					+ "をしてみましょう。";
		}

		return activity.getRequiredTime()
				+ "分で、"
				+ activityName
				+ "をやってみましょう。";
	}

	/**
	 * JSON文字列をエスケープ
	 */
	private String escapeJson(
			String value) {

		if (value == null) {
			return "";
		}

		return value
				.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\r", "\\r")
				.replace("\n", "\\n")
				.replace("\t", "\\t");
	}

	/**
	 * JSONレスポンスを設定
	 */
	private void setJsonResponse(
			HttpServletResponse response) {

		response.setContentType(
				"application/json; charset=UTF-8");

		response.setCharacterEncoding(
				"UTF-8");
	}

	/**
	 * エラーをJSONで返す
	 */
	private void writeError(
			HttpServletResponse response,
			int status,
			String message)
			throws IOException {

		response.setStatus(status);

		String json =
				"{"
				+ "\"success\":false,"
				+ "\"message\":\""
				+ escapeJson(message)
				+ "\""
				+ "}";

		response.getWriter().write(json);
	}

	/**
	 * 活動とスコアを保持
	 */
	private static class ScoredActivity {

		private Activity activity;

		private int score;

		private LocalDateTime lastExecutedAt;

		private List<String> garbageNames =
				new ArrayList<>();
	}
}
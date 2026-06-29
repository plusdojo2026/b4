package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.ActivityDao;
import dao.IconDao;
import dto.Activity;
import dto.Icon;
import dto.LoginUser;

/**
 * チャット画面の初期表示を行うServlet
 */
@WebServlet("/ChatServlet")
public class ChatServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/** 最初の報告画面に表示する分類名 */
	private static final String REPORT_GROUP_DISH = "食器類";
	private static final String REPORT_GROUP_CLEANING = "掃除類";
	private static final String REPORT_GROUP_TIDYING = "片付け類";
	private static final String REPORT_GROUP_GARBAGE ="ゴミまとめ類";
	private static final String REPORT_GROUP_OTHER ="その他";

	/**
	 * ログインユーザーの情報を取得してチャット画面を表示
	 */
	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");

		// 既存のセッションを取得
		HttpSession session = request.getSession(false);

		// 未ログインの場合はログイン画面へ移動
		if (session == null) {
			response.sendRedirect(
					request.getContextPath()
					+ "/LoginServlet");
			return;
		}

		// ログインユーザーを取得
		LoginUser loginUser =(LoginUser) session.getAttribute("idnamepw");

		// ログイン情報がない場合はログイン画面へ移動
		if (loginUser == null) {
			response.sendRedirect(
					request.getContextPath()
					+ "/LoginServlet");
			return;
		}

		int userId = loginUser.getUserId();

		// ユーザーのアイコンを取得
		IconDao iconDao = new IconDao();

		Icon icon = iconDao.select(userId);

		// 全活動を取得
		ActivityDao activityDao = new ActivityDao();

		List<Activity> activityList = activityDao.selectAll();

		if (activityList == null) {
			activityList = new ArrayList<>();
		}

		/*
		 * 最初の報告用にHOUSEWORKだけを分類
		 */
		Map<String, List<Activity>>
				reportActivityMap =createReportActivityMap(activityList);

		// JSPへアイコン情報を渡す
		request.setAttribute("Iconlist",icon);

		// JSPへ分類済みの家事一覧を渡す
		request.setAttribute("reportActivityMap",reportActivityMap);

		// チャット画面へ移動
		RequestDispatcher dispatcher =request.getRequestDispatcher("/WEB-INF/jsp/chat.jsp");

		dispatcher.forward(request, response);
	}

	/**
	 * 最初の報告用の家事一覧を作成
	 */
	private Map<String, List<Activity>>
			createReportActivityMap(List<Activity> activityList) {

		Map<String, List<Activity>>
				reportActivityMap = new LinkedHashMap<>();

		/*
		 * LinkedHashMapを使用して
		 * JSPでの表示順を固定
		 */
		reportActivityMap.put(
				REPORT_GROUP_DISH,
				new ArrayList<>());

		reportActivityMap.put(
				REPORT_GROUP_CLEANING,
				new ArrayList<>());

		reportActivityMap.put(
				REPORT_GROUP_TIDYING,
				new ArrayList<>());

		reportActivityMap.put(
				REPORT_GROUP_GARBAGE,
				new ArrayList<>());

		reportActivityMap.put(
				REPORT_GROUP_OTHER,
				new ArrayList<>());

		if (activityList == null) {
			return reportActivityMap;
		}

		for (Activity activity : activityList) {

			/*
			 * 最初の報告ではHOUSEWORKだけを表示
			 */
			if (!"HOUSEWORK".equals(activity.getCategory())) {

				continue;
			}

			String reportGroup =determineReportGroup(activity);

			reportActivityMap
					.get(reportGroup)
					.add(activity);
		}

		return reportActivityMap;
	}

	/**
	 * 活動を報告画面用カテゴリへ分類
	 */
	private String determineReportGroup(
			Activity activity) {

		if (activity == null) {
			return REPORT_GROUP_OTHER;
		}

		String activityName =activity.getActivityName();

		if (activityName == null) {
			activityName = "";
		}

		/*
		 * ゴミ活動はhas_garbagesを優先
		 */
		if (activity.getHasGarbage() || activityName.contains("ゴミ")) {

			return REPORT_GROUP_GARBAGE;
		}

		/*
		 * その他として指定された2活動
		 */
		if ("布団・寝具を整える".equals(activityName) || "明日の持ち物を確認する".equals(activityName)) {

			return REPORT_GROUP_OTHER;
		}

		/*
		 * 食器関連
		 */
		if (containsAny(
				activityName,
				"食器",
				"洗い物",
				"皿",
				"コップ")) {

			return REPORT_GROUP_DISH;
		}

		/*
		 * 掃除関連
		 */
		if (containsAny(
				activityName,
				"掃除",
				"拭く",
				"流す",
				"掃除機",
				"ワイパー",
				"粘着ローラー")) {

			return REPORT_GROUP_CLEANING;
		}

		/*
		 * 片付け関連
		 */
		if (containsAny(
				activityName,
				"片付",
				"整理",
				"整頓",
				"戻す",
				"しまう",
				"洗濯",
				"たたむ",
				"取り込む",
				"干す")) {

			return REPORT_GROUP_TIDYING;
		}

		/*
		 * 分類条件に当てはまらない家事は
		 * 表示漏れを防ぐためその他へ入れる
		 */
		return REPORT_GROUP_OTHER;
	}

	/**
	 * 対象文字列にいずれかのキーワードが含まれるか判定
	 */
	private boolean containsAny(
			String target,
			String... keywords) {

		if (target == null || keywords == null) {

			return false;
		}

		for (String keyword : keywords) {
			if (keyword != null && target.contains(keyword)) {

				return true;
			}
		}

		return false;
	}
}
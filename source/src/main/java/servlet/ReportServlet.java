package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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

/**
 * 活動履歴を登録するServlet
 *
 * JavaScriptからの受信
 * application/x-www-form-urlencoded
 *
 * JavaScriptへの返却
 * JSON
 */
@WebServlet("/ReportServlet")
public class ReportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		setJsonResponse(response);

		writeJson(
				response,
				HttpServletResponse.SC_METHOD_NOT_ALLOWED,
				false,
				"POSTでアクセスしてください",
				0);
	}

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		setJsonResponse(response);

		/*
		 * ChatServletで作られたセッションを取得
		 *
		 * falseを指定するとセッションがない場合に
		 * 新しいセッションを作成しない
		 */
		HttpSession session =
				request.getSession(false);

		if (session == null) {
			writeJson(
					response,
					HttpServletResponse.SC_UNAUTHORIZED,
					false,
					"ログイン情報がありません",
					0);
			return;
		}

		// ログインユーザーを取得
		LoginUser loginUser =(LoginUser) session.getAttribute("idnamepw");

		if (loginUser == null) {
			writeJson(
					response,
					HttpServletResponse.SC_UNAUTHORIZED,
					false,
					"ログイン情報がありません",
					0);
			return;
		}

		String action = request.getParameter("action");

		if (action == null || action.isEmpty()) {

			writeJson(
					response,
					HttpServletResponse.SC_BAD_REQUEST,
					false,
					"actionが指定されていません",
					0);
			return;
		}

		int userId = loginUser.getUserId();

		try {
			if ("checkActivity".equals(action)) {

				/*
				 * 最初にユーザーが報告した家事を登録
				 */
				registerCheckedActivities(
						request,
						response,
						userId);

			} else if ("complete".equals(action)) {

				/*
				 * 提案された活動の完了を登録
				 */
				registerCompletedActivity(
						request,
						response,
						userId);

			} else {
				writeJson(
						response,
						HttpServletResponse.SC_BAD_REQUEST,
						false,
						"不正なactionです",
						0);
			}

		} catch (NumberFormatException e) {
			writeJson(
					response,
					HttpServletResponse.SC_BAD_REQUEST,
					false,
					"activityIdは数値で指定してください",
					0);

		} catch (Exception e) {
			e.printStackTrace();

			writeJson(
					response,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					false,
					"活動履歴の登録に失敗しました",
					0);
		}
	}

	/**
	 * 最初に選択された家事を登録
	 *
	 * 最初の報告ではHOUSEWORKだけを受け付ける
	 */
	private void registerCheckedActivities(
			HttpServletRequest request,
			HttpServletResponse response,
			int userId)
			throws IOException {

		String[] activityIdTexts = request.getParameterValues("activityId");

		if (activityIdTexts == null || activityIdTexts.length == 0) {

			writeJson(
					response,
					HttpServletResponse.SC_BAD_REQUEST,
					false,
					"家事が選択されていません",
					0);
			return;
		}

		/*
		 * 同じ活動IDが複数送られた場合に
		 * 二重登録しないようSetを使用
		 */
		Set<Integer> activityIdSet = new LinkedHashSet<>();

		for (String activityIdText : activityIdTexts) {

			if (activityIdText == null || activityIdText.isEmpty()) {

				continue;
			}

			int activityId =Integer.parseInt(activityIdText);

			if (activityId <= 0) {
				writeJson(
						response,
						HttpServletResponse.SC_BAD_REQUEST,
						false,
						"活動IDが不正です",
						0);
				return;
			}

			activityIdSet.add(activityId);
		}

		if (activityIdSet.isEmpty()) {
			writeJson(
					response,
					HttpServletResponse.SC_BAD_REQUEST,
					false,
					"家事が選択されていません",
					0);
			return;
		}

		ActivityDao activityDao = new ActivityDao();

		List<Integer> validActivityIds = new ArrayList<>();

		/*
		 * 登録前に全IDを確認
		 *
		 * HOUSEWORK以外が含まれていた場合は
		 * 履歴登録を開始しない
		 */
		for (Integer activityId : activityIdSet) {

			Activity activity =activityDao.findById(activityId);

			if (activity == null) {
				writeJson(
						response,
						HttpServletResponse.SC_BAD_REQUEST,
						false,
						"存在しない活動が指定されています",
						0);
				return;
			}

			if (!"HOUSEWORK".equals(activity.getCategory())) {

				writeJson(
						response,
						HttpServletResponse.SC_BAD_REQUEST,
						false,
						"最初の報告では家事だけを選択できます",
						0);
				return;
			}

			validActivityIds.add(activityId);
		}

		ActivityHistoryDao historyDao =new ActivityHistoryDao();

		int insertCount = 0;

		for (Integer activityId: validActivityIds) {

			int historyId =historyDao.create(
							userId,
							activityId);

			if (historyId > 0) {
				insertCount++;
			}
		}

		if (insertCount == 0) {
			writeJson(
					response,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					false,
					"活動履歴を登録できませんでした",
					0);
			return;
		}

		if (insertCount != validActivityIds.size()) {

			writeJson(
					response,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					false,
					"一部の活動履歴を登録できませんでした",
					insertCount);
			return;
		}

		writeJson(
				response,
				HttpServletResponse.SC_OK,
				true,
				insertCount
				+ "件の家事を記録しました",
				insertCount);
	}

	/**
	 * 提案後に完了した活動を1件登録
	 *
	 * HOUSEWORK、CHILD、RESTを記録できる
	 */
	private void registerCompletedActivity(
			HttpServletRequest request,
			HttpServletResponse response,
			int userId)
			throws IOException {

		String activityIdText =request.getParameter("activityId");

		if (activityIdText == null || activityIdText.isEmpty()) {

			writeJson(
					response,
					HttpServletResponse.SC_BAD_REQUEST,
					false,
					"activityIdが指定されていません",
					0);
			return;
		}

		int activityId =Integer.parseInt(activityIdText);

		if (activityId <= 0) {
			writeJson(
					response,
					HttpServletResponse.SC_BAD_REQUEST,
					false,
					"activityIdが不正です",
					0);
			return;
		}

		ActivityDao activityDao = new ActivityDao();

		Activity activity = activityDao.findById(activityId);

		if (activity == null) {
			writeJson(
					response,
					HttpServletResponse.SC_NOT_FOUND,
					false,
					"指定された活動が存在しません",
					0);
			return;
		}

		/*
		 * FINISHは通常の活動履歴として登録しない
		 */
		if ("FINISH".equals(activity.getCategory())) {

			writeJson(
					response,
					HttpServletResponse.SC_BAD_REQUEST,
					false,
					"終了メッセージは活動として登録できません",
					0);
			return;
		}

		ActivityHistoryDao historyDao =new ActivityHistoryDao();

		int historyId =historyDao.create(
						userId,
						activityId);

		if (historyId == 0) {
			writeJson(
					response,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					false,
					"活動履歴を登録できませんでした",
					0);
			return;
		}

		writeJson(
				response,
				HttpServletResponse.SC_OK,
				true,
				"活動の完了を記録しました",
				1);
	}

	/**
	 * JSON形式のレスポンスを設定
	 */
	private void setJsonResponse(HttpServletResponse response) {

		response.setContentType("application/json; charset=UTF-8");

		response.setCharacterEncoding("UTF-8");
	}

	/**
	 * JSONをJavaScriptへ返す
	 */
	private void writeJson(
			HttpServletResponse response,
			int status,
			boolean success,
			String message,
			int recordedCount)
			throws IOException {

		response.setStatus(status);

		String json =
				"{"
				+ "\"success\":"
				+ success
				+ ","
				+ "\"message\":\""
				+ escapeJson(message)
				+ "\","
				+ "\"recordedCount\":"
				+ recordedCount
				+ "}";

		response.getWriter().write(json);
	}

	/**
	 * JSON文字列をエスケープ
	 */
	private String escapeJson(String value) {

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
}
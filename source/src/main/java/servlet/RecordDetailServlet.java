package servlet;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.ActivityHistoryDao;
import dto.LoginUser;
import dto.RecordHistoryDto;

/**
 * 記録一覧画面を表示するServlet
 */
@WebServlet("/RecordDetailServlet")
public class RecordDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * 記録一覧を表示
	 */
	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		response.setContentType(
				"text/html; charset=UTF-8");

		response.setCharacterEncoding(
				"UTF-8");

		// 既存のセッションを取得
		HttpSession session =
				request.getSession(false);

		// 未ログインの場合はログイン画面へ移動
		if (session == null) {
			response.sendRedirect(
					request.getContextPath()
					+ "/LoginServlet");
			return;
		}

		// ログインユーザーを取得
		LoginUser loginUser =
				(LoginUser) session.getAttribute(
						"idnamepw");

		// ログイン情報がない場合はログイン画面へ移動
		if (loginUser == null) {
			response.sendRedirect(
					request.getContextPath()
					+ "/LoginServlet");
			return;
		}

		int userId =
				loginUser.getUserId();

		// 表示する週の位置を取得
		int weekOffset =
				parseWeekOffset(
						request.getParameter(
								"weekOffset"));

		// 今週の月曜日を取得
		LocalDate currentWeekMonday =
				LocalDate.now()
						.with(
								TemporalAdjusters
										.previousOrSame(
												DayOfWeek.MONDAY));

		// 表示対象週の月曜日を取得
		LocalDate weekStartDate =
				currentWeekMonday.plusWeeks(
						weekOffset);

		// 表示対象週の日曜日を取得
		LocalDate weekEndDate =
				weekStartDate.plusDays(6);

		// DB検索用の日時を作成
		LocalDateTime startAt =
				weekStartDate.atStartOfDay();

		LocalDateTime endAt =
				weekStartDate
						.plusDays(7)
						.atStartOfDay();

		// 指定週の履歴を取得
		ActivityHistoryDao historyDao =
				new ActivityHistoryDao();

		List<RecordHistoryDto> recordList =
				historyDao.findRecordHistoryList(
						userId,
						startAt,
						endAt);

		// 表示期間を作成
		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern(
						"yyyy/MM/dd");

		String periodLabel =
				weekStartDate.format(formatter)
				+ " ～ "
				+ weekEndDate.format(formatter);

		// JSPへ渡す
		request.setAttribute(
				"recordList",
				recordList);

		request.setAttribute(
				"periodLabel",
				periodLabel);

		request.setAttribute(
				"currentWeekOffset",
				weekOffset);

		request.setAttribute(
				"previousWeekOffset",
				weekOffset - 1);

		request.setAttribute(
				"nextWeekOffset",
				weekOffset + 1);

		// JSPへフォワード
		RequestDispatcher dispatcher =
				request.getRequestDispatcher(
						"/WEB-INF/jsp/recorddetail.jsp");

		dispatcher.forward(
				request,
				response);
	}

	/**
	 * weekOffsetを数値へ変換
	 */
	private int parseWeekOffset(
			String weekOffsetText) {

		if (weekOffsetText == null
				|| weekOffsetText.isBlank()) {

			return 0;
		}

		try {
			return Integer.parseInt(
					weekOffsetText);

		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
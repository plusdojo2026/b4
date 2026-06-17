package servlet;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

@WebServlet("/record")
public class RecordDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//記録一覧画面を表示する
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		try {
			// セッション取得
			HttpSession session = request.getSession(false);

			// 未ログインの場合はログイン画面へ戻す
			if (session == null || session.getAttribute("name") == null) {
				response.sendRedirect(request.getContextPath() + "/LoginServlet");
				return;
			}

			// ログインユーザー取得
			LoginUser loginUser = (LoginUser) session.getAttribute("name");

			// ユーザーID取得
			int userId = loginUser.getId();

			// ユーザーIDが取得できない場合はログイン画面へ戻す
			if (userId <= 0) {
				session.invalidate();
				response.sendRedirect(request.getContextPath() + "/LoginServlet");
				return;
			}

			// 週の移動量を取得
			// 0 : 今週
			// -1 : 前週
			// 1 : 翌週
			String weekOffsetParam = request.getParameter("weekOffset");

			int weekOffset = 0;

			if (weekOffsetParam != null && !weekOffsetParam.isBlank()) {
				try {
					weekOffset = Integer.parseInt(weekOffsetParam);
				} catch (NumberFormatException e) {
					weekOffset = 0;
				}
			}

			// 今週の月曜日を取得
			LocalDate today = LocalDate.now();
			LocalDate thisMonday = today.with(DayOfWeek.MONDAY);

			// 表示対象週の開始日と終了日
			LocalDate startDate = thisMonday.plusWeeks(weekOffset);
			LocalDate endDate = startDate.plusDays(6);

			// DB検索用日時
			LocalDateTime startAt = startDate.atStartOfDay();
			LocalDateTime endAt = endDate.plusDays(1).atStartOfDay();

			// 記録一覧取得
			ActivityHistoryDao activityHistoryDao = new ActivityHistoryDao();

			List<RecordHistoryDto> recordList = activityHistoryDao.findRecordHistoryList(userId, startAt, endAt);

			// 件数集計
			int totalCount = recordList.size();
			int houseworkCount = 0;
			int childCount = 0;
			int restCount = 0;

			for (RecordHistoryDto record : recordList) {
				String category = record.getCategory();

				if ("housework".equals(category)) {
					houseworkCount++;
				} else if ("child".equals(category)) {
					childCount++;
				} else if ("rest".equals(category)) {
					restCount++;
				}
			}

			// グラフ表示用割合
			int houseworkRatio = calculateRatio(houseworkCount, totalCount);
			int childRatio = calculateRatio(childCount, totalCount);
			int restRatio = calculateRatio(restCount, totalCount);

			// 画面表示用の日付範囲
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

			String periodLabel = startDate.format(formatter) + " ～ " + endDate.format(formatter);

			// 前週・翌週リンク用
			int previousWeekOffset = weekOffset - 1;
			int nextWeekOffset = weekOffset + 1;

			// JSPへ渡す値
			request.setAttribute("recordList", recordList);

			request.setAttribute("periodLabel", periodLabel);
			request.setAttribute("weekOffset", weekOffset);
			request.setAttribute("previousWeekOffset", previousWeekOffset);
			request.setAttribute("nextWeekOffset", nextWeekOffset);

			request.setAttribute("totalCount", totalCount);
			request.setAttribute("houseworkCount", houseworkCount);
			request.setAttribute("childCount", childCount);
			request.setAttribute("restCount", restCount);

			request.setAttribute("houseworkRatio", houseworkRatio);
			request.setAttribute("childRatio", childRatio);
			request.setAttribute("restRatio", restRatio);

			// 記録一覧画面へフォワード
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/recorddetail.jsp");

			dispatcher.forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	//グラフ表示用の割合を計算する
	private int calculateRatio(int count, int totalCount) {

		if (totalCount == 0) {
			return 0;
		}

		return Math.round((float) count / totalCount * 100);
	}
}
package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.ActivityHistoryDao;
import dto.LoginUser;

/**
 * 活動履歴を登録するServlet
 *
 * 役割;
 * 1. 最初の「何か家事やった？」で報告された活動を登録
 * 2. 提案後に「終わったよ」を押した活動を登録
 *
 * 
 */
@WebServlet("/ReportServlet")
public class ReportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.getWriter().write("POSTでアクセスしてください。");
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        /*
         * ChatServletで作成されたセッションを取得
         * getSession(false)にすると、セッションがない場合に新規作成しない。
         */
        HttpSession session = request.getSession(false);

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("ログイン情報がありません。");
            return;
        }

        /*
         * ChatServletでは idnamepw という名前でLoginUserを保存している。
         */
        LoginUser loginUser = (LoginUser) session.getAttribute("idnamepw");

        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("ログイン情報がありません。");
            return;
        }

        int userId = loginUser.getUserId();

        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("actionが指定されていません。");
            return;
        }

        try {
            if ("checkActivity".equals(action)) {

                /*
                 * 最初の報告
                 * 複数のactivityIdを受け取る
                 */
                registerCheckedActivities(request, response, userId);

            } else if ("complete".equals(action)) {

                /*
                 * 提案された活動の完了
                 * activityIdは1件だけ
                 */
                registerCompletedActivity(request, response, userId);

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("不正なactionです。");
            }

        } catch (NumberFormatException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("activityIdは数値で指定してください。");

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("活動履歴の登録に失敗しました。");
        }
    }

    /**
     * 最初の報告で選ばれた活動を登録
     *
     * activityId=1&activityId=2 のように
     * 複数件送られてくる想定
     */
    private void registerCheckedActivities(
            HttpServletRequest request,
            HttpServletResponse response,
            int userId)
            throws IOException {

        String[] activityIdTexts = request.getParameterValues("activityId");

        if (activityIdTexts == null|| activityIdTexts.length == 0) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("活動が選択されていません。");
            return;
        }

        ActivityHistoryDao historyDao = new ActivityHistoryDao();

        int insertCount = 0;

        for (String activityIdText : activityIdTexts) {

            if (activityIdText == null || activityIdText.isEmpty()) {
                continue;
            }

            int activityId = Integer.parseInt(activityIdText);

            if (activityId <= 0) {
                continue;
            }

            boolean result = historyDao.insert(userId, activityId);

            if (result) {
                insertCount++;
            }
        }

        if (insertCount == 0) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
            response.getWriter().write("活動履歴を登録できませんでした。");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(insertCount + "件の活動を記録しました。");
    }

    /**
     * 提案された活動の完了を登録
     */
    private void registerCompletedActivity(
            HttpServletRequest request,
            HttpServletResponse response,
            int userId)
            throws IOException {

        String activityIdText = request.getParameter("activityId");

        if (activityIdText == null || activityIdText.isEmpty()) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("activityIdが指定されていません。");
            return;
        }

        int activityId = Integer.parseInt(activityIdText);

        if (activityId <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("activityIdが不正です。");
            return;
        }

        ActivityHistoryDao historyDao = new ActivityHistoryDao();

        boolean result = historyDao.insert(userId, activityId);

        if (!result) {
        	//エラー500 サーバーエラーを通知
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("活動履歴を登録できませんでした。");
            return;
        }
        
        int historyId = historyDao.create(userId, activityId);

        if (historyId == 0) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("活動履歴を登録できませんでした。");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("活動の完了を記録しました。");
    }
}
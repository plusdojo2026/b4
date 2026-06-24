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
 * 活動履歴を登録するServlet。
 *
 * JavaScriptからの受信：
 * application/x-www-form-urlencoded
 *
 * JavaScriptへの返却：
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
                "POSTでアクセスしてください。",
                0
        );
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        setJsonResponse(response);

        /*
         * ChatServletで作られたセッションを取得する。
         * falseを指定すると、セッションがない場合に新しく作成しない。
         */
        HttpSession session = request.getSession(false);

        if (session == null) {
            writeJson(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    false,
                    "ログイン情報がありません。",
                    0
            );
            return;
        }

        /*
         * ChatServletで保存したLoginUserを取得する。
         */
        LoginUser loginUser = (LoginUser) session.getAttribute("idnamepw");

        if (loginUser == null) {
            writeJson(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    false,
                    "ログイン情報がありません。",
                    0
            );
            return;
        }

        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            writeJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    false,
                    "actionが指定されていません。",
                    0
            );
            return;
        }

        int userId = loginUser.getUserId();

        try {
            if ("checkActivity".equals(action)) {

                /*
                 * 最初にユーザーが報告した、
                 * 複数の活動を登録する。
                 */
                registerCheckedActivities(
                        request,
                        response,
                        userId
                );

            } else if ("complete".equals(action)) {

                /*
                 * 提案された活動の完了を1件登録する。
                 */
                registerCompletedActivity(
                        request,
                        response,
                        userId
                );

            } else {
                writeJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "不正なactionです。",
                        0
                );
            }

        } catch (NumberFormatException e) {

            writeJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    false,
                    "activityIdは数値で指定してください。",
                    0
            );

        } catch (Exception e) {

            e.printStackTrace();

            writeJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "活動履歴の登録に失敗しました。",
                    0
            );
        }
    }

    /**
     * 最初に選択された複数の活動を登録する。
     */
    private void registerCheckedActivities(
            HttpServletRequest request,
            HttpServletResponse response,
            int userId)
            throws IOException {

        /*
         * activityIdが複数送られてくるため、
         * getParameterValues()を使用する。
         */
        String[] activityIdTexts = request.getParameterValues("activityId");

        if (activityIdTexts == null || activityIdTexts.length == 0) {

            writeJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    false,
                    "活動が選択されていません。",
                    0
            );
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

            /*
             * create()だけを呼ぶ。
             * insert()とcreate()を両方呼ぶと二重登録になる。
             */
            int historyId = historyDao.create(
                            userId,
                            activityId
                    		);

            if (historyId > 0) {
                insertCount++;
            }
        }

        if (insertCount == 0) {
            writeJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "活動履歴を登録できませんでした。",
                    0
            );
            return;
        }

        writeJson(
                response,
                HttpServletResponse.SC_OK,
                true,
                insertCount + "件の活動を記録しました。",
                insertCount
        );
    }

    /**
     * 提案後に完了した活動を1件登録する。
     */
    private void registerCompletedActivity(
            HttpServletRequest request,
            HttpServletResponse response,
            int userId)
            throws IOException {

        String activityIdText = request.getParameter("activityId");

        if (activityIdText == null || activityIdText.isEmpty()) {

            writeJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    false,
                    "activityIdが指定されていません。",
                    0
            );
            return;
        }

        int activityId = Integer.parseInt(activityIdText);

        if (activityId <= 0) {
            writeJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    false,
                    "activityIdが不正です。",
                    0
            );
            return;
        }

        ActivityHistoryDao historyDao = new ActivityHistoryDao();

        int historyId = historyDao.create(
                        userId,
                        activityId
                		);

        if (historyId == 0) {
            writeJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "活動履歴を登録できませんでした。",
                    0
            );
            return;
        }

        writeJson(
                response,
                HttpServletResponse.SC_OK,
                true,
                "活動の完了を記録しました。",
                1
        );
    }

    /**
     * JSON形式のレスポンスを設定する。
     */
    private void setJsonResponse(
            HttpServletResponse response) {

        response.setContentType("application/json; charset=UTF-8");

        response.setCharacterEncoding("UTF-8");
    }

    /**
     * JSONをJavaScriptへ返す。
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
     * JSON文字列内で問題になる文字を変換する。
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

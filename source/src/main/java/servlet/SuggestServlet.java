package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
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
import dto.RecordHistoryDto;

/**
 * 活動を提案するServlet。
 *
 * 処理内容：
 * ・時間指定またはおまかせで提案を開始する
 * ・残り時間をHttpSessionで管理する
 * ・活動候補をポイント順に並べて返す
 * ・完了後に残り時間を減らして再提案する
 *
 * 活動履歴の登録はReportServlet
 */
@WebServlet("/SuggestServlet")
public class SuggestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 最後に用事の準備時間として残す時間
     */
    private static final int PREPARE_MINUTES = 5;

    /**
     * 提案対象外を表すスコア
     */
    private static final int EXCLUDED_SCORE = -9999;

    /**
     * HttpSessionへ保存する属性名
     */
    private static final String SESSION_MODE = "suggestionMode";
    private static final String SESSION_REMAINING_MINUTES = "suggestionRemainingMinutes";


    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        setJsonResponse(response);
        writeError(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POSTでアクセスしてください。");
    }

    /**
     * 提案処理
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        setJsonResponse(response);

        HttpSession session = request.getSession(false);

        if (session == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,"ログイン情報がありません。");
            return;
        }

        LoginUser loginUser = (LoginUser) session.getAttribute("idnamepw");

        if (loginUser == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,"ログイン情報がありません。");
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
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "提案処理中にエラーが発生しました。");
        }
    }

    //提案を開始
    private void startSuggestion(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session,
            int userId)
            throws IOException {

        String mode =request.getParameter("mode");

        if ("TIME".equals(mode)) {

            String timeText = request.getParameter("time");

            if (timeText == null || timeText.isEmpty()) {
                writeError(response, HttpServletResponse.SC_BAD_REQUEST, "時間が指定されていません。");
                return;
            }

            int selectedMinutes = Integer.parseInt(timeText);

            if (selectedMinutes != 10 && selectedMinutes != 15 && selectedMinutes != 30 && selectedMinutes != 45 && selectedMinutes != 60) {
                writeError(response, HttpServletResponse.SC_BAD_REQUEST, "時間は10分、15分、30分、45分、60分から選択してください。");
                return;
            }

            session.setAttribute(SESSION_MODE,"TIME");
            session.setAttribute(SESSION_REMAINING_MINUTES,selectedMinutes);

        } else if ("AUTO".equals(mode)) {

            session.setAttribute(SESSION_MODE,"AUTO");
            session.removeAttribute(SESSION_REMAINING_MINUTES);

        } else {

            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "modeにはTIMEまたはAUTOを指定してください。");
            return;
        }

        writeCurrentSuggestions(response, session, userId);
    }

    /**
     * 活動完了後の処理
     *
     * ReportServletで履歴登録した後に呼び出す
     */
    private void completeSuggestion(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session,
            int userId)
            throws IOException {

        String mode = (String) session.getAttribute(SESSION_MODE);

        if (mode == null) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST,"提案が開始されていません。");
            return;
        }

        String activityIdText =request.getParameter("activityId");

        if (activityIdText == null || activityIdText.isEmpty()) {

            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "activityIdが指定されていません。");
            return;
        }

        int activityId = Integer.parseInt(activityIdText);

        ActivityDao activityDao = new ActivityDao();
        List<Activity> activityList = activityDao.selectAll();
        Activity completedActivity =findActivityById(activityList, activityId);

        if (completedActivity == null) {
            writeError(response, HttpServletResponse.SC_NOT_FOUND,"指定された活動が存在しません。");
            return;
        }

        /*
         * TIMEモード(時間指定)のときだけ残り時間を減らす
         */
        if ("TIME".equals(mode)) {

            Integer remainingMinutes =(Integer) session.getAttribute(SESSION_REMAINING_MINUTES);

            if (remainingMinutes == null) {
                writeError(response, HttpServletResponse.SC_BAD_REQUEST, "残り時間が取得できません。");
                return;
            }

            remainingMinutes -= completedActivity.getRequiredTime();

            if (remainingMinutes < 0) {
                remainingMinutes = 0;
            }

            session.setAttribute(SESSION_REMAINING_MINUTES,remainingMinutes);
        }

        /*
         * 更新された残り時間と履歴を使用して、
         * 候補を再計算
         */
        writeCurrentSuggestions(response, session, userId);
    }

    /**
     * 残り時間を減らさずに候補を再取得
     */
    private void refreshSuggestion(
            HttpServletResponse response,
            HttpSession session,
            int userId)
            throws IOException {

        String mode =(String) session.getAttribute(SESSION_MODE);

        if (mode == null) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "提案が開始されていません。");
            return;
        }

        writeCurrentSuggestions(response, session, userId);
    }

    /**
     * 現在のセッション状態から候補を作成して返す
     */
    private void writeCurrentSuggestions(
            HttpServletResponse response,
            HttpSession session,
            int userId)
            throws IOException {

        String mode =(String) session.getAttribute(SESSION_MODE);
        Integer remainingMinutes = null;

        if ("TIME".equals(mode)) {

            remainingMinutes =(Integer) session.getAttribute(SESSION_REMAINING_MINUTES);

            if (remainingMinutes == null) {
                writeError(response, HttpServletResponse.SC_BAD_REQUEST, "残り時間が取得できません。");
                return;
            }

            /*
             * 残り時間が5分以下なら提案を終了
             */
            if (remainingMinutes <= PREPARE_MINUTES) {

                writeSuggestionResponse(
                        response,
                        "PREPARE",
                        mode,
                        remainingMinutes,
                        "家事はここまでにして、次の用事の準備をしましょう。",
                        new ArrayList<ScoredActivity>()
                );
                return;
            }
        }

        List<ScoredActivity> suggestions =
                createSuggestions(userId, mode, remainingMinutes);

        /*
         * 提案できる活動がない場合
         */
        if (suggestions.isEmpty()) {

            if ("TIME".equals(mode)) {

                writeSuggestionResponse(
                        response,
                        "PREPARE",
                        mode,
                        remainingMinutes,
                        "残り時間でできる活動がないため、用事の準備をしましょう。",
                        suggestions
                );

            } else {

                writeSuggestionResponse(
                        response,
                        "FINISH",
                        mode,
                        null,
                        "今日の提案はここまでです。無理せず休みましょう。",
                        suggestions
                );
            }

            return;
        }

        writeSuggestionResponse(
                response,
                "CONTINUE",
                mode,
                remainingMinutes,
                null,
                suggestions
        );
    }

    /**
     * 活動候補を取得し、スコアを計算
     */
    private List<ScoredActivity> createSuggestions(
            int userId,
            String mode,
            Integer remainingMinutes) {

        ActivityDao activityDao = new ActivityDao();
        ActivityHistoryDao historyDao = new ActivityHistoryDao();

        List<Activity> activityList = activityDao.selectAll();

        if (activityList == null) {
            activityList = new ArrayList<>();
        }

        /*
         * 今日の開始時刻と終了時刻
         */
        LocalDate today = LocalDate.now();
        LocalDateTime startAt = today.atStartOfDay();
        LocalDateTime endAt = startAt.plusDays(1);

        List<RecordHistoryDto> todayHistoryList =historyDao.findRecordHistoryList(userId, startAt, endAt);
        Set<Integer> doneTodayActivityIds = new HashSet<>();

        int houseworkCount = 0;
        int childCount = 0;

        for (RecordHistoryDto history: todayHistoryList) {

            doneTodayActivityIds.add(history.getActivityId());

            if ("HOUSEWORK".equals(history.getCategory())) {
                houseworkCount++;
            }

            if ("CHILD".equals(history.getCategory())) {
                childCount++;
            }
        }

        int overworkLevel = calculateOverworkLevel(houseworkCount);

        LocalTime currentTime = LocalTime.now();

        List<ScoredActivity> scoredList = new ArrayList<>();

        for (Activity activity : activityList) {

            int score = calculateScore(
                            activity,
                            doneTodayActivityIds,
                            overworkLevel,
                            childCount,
                            currentTime,
                            mode,
                            remainingMinutes
            				);

            if (score == EXCLUDED_SCORE) {
                continue;
            }

            ScoredActivity scoredActivity = new ScoredActivity();

            scoredActivity.activity = activity;
            scoredActivity.score = score;

            scoredList.add(scoredActivity);
        }

        /*
         * スコアが高い順
         * 同点の場合は所要時間が短い順
         * さらに同点の場合はCHILDを優先
         */
        scoredList.sort(new Comparator<ScoredActivity>() {

                    @Override
                    public int compare(
                            ScoredActivity first,
                            ScoredActivity second) {

                        int scoreCompare = Integer.compare(second.score, first.score);

                        if (scoreCompare != 0) {
                            return scoreCompare;
                        }

                        int timeCompare = Integer.compare(first.activity.getRequiredTime(), second.activity.getRequiredTime());

                        if (timeCompare != 0) {
                            return timeCompare;
                        }

                        boolean firstChild = "CHILD".equals(first.activity.getCategory());

                        boolean secondChild = "CHILD".equals(second.activity.getCategory());

                        if (firstChild != secondChild) {
                            return firstChild ? -1 : 1;
                        }

                        return Integer.compare(first.activity.getId(), second.activity.getId());
                    }
                }
        );

        return scoredList;
    }

    /**
     * 活動1件のスコアを計算
     */
    private int calculateScore(
            Activity activity,
            Set<Integer> doneTodayActivityIds,
            int overworkLevel,
            int childCount,
            LocalTime currentTime,
            String mode,
            Integer remainingMinutes) {

        /*
         * 今日実施済みなら除外
         */
        if (doneTodayActivityIds.contains(activity.getId())) {

            return EXCLUDED_SCORE;
        }

        /*
         * FINISHは21時以降だけ提案
         */
        if ("FINISH".equals(
                activity.getCategory())) {

            if (isFinishTime(currentTime)) {
                return 9999;
            }

            return EXCLUDED_SCORE;
        }

        /*
         * TIMEモードでは、最後の5分を準備時間として確保
         */
        if ("TIME".equals(mode)) {

            int activityAvailableMinutes = remainingMinutes - PREPARE_MINUTES;

            if (activityAvailableMinutes <= 0) {
                return EXCLUDED_SCORE;
            }

            if (activity.getRequiredTime()
                    > activityAvailableMinutes) {

                return EXCLUDED_SCORE;
            }
        }

        /*
         * 騒音活動は9時以上18時未満だけ許可
         */
        if (activity.getIsNoise() && !isNoiseAllowedTime(currentTime)) {

            return EXCLUDED_SCORE;
        }

        int score = activity.getBasePoint();

        /*
         * 頑張りすぎ防止補正
         */
        score += calculateOverworkPoint(activity, overworkLevel);

        /*
         * 子供時間補正
         */
        score += calculateChildPoint(activity, childCount);

        /*
         * 時間帯補正
         */
        score += calculateTimeZonePoint(activity, currentTime);

        /*
         * ゴミの日補正と家事フロー補正は、
         * GarbageDaoとフロー管理が完成後に追加
         */

        return score;
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
     * 頑張りすぎ防止ポイント
     */
    private int calculateOverworkPoint(Activity activity, int overworkLevel) {

        String category = activity.getCategory();

        if (overworkLevel == 1) {

            if ("HOUSEWORK".equals(category)) {
                return -10;
            }

            if ("CHILD".equals(category)) {
                return 20;
            }

            if ("REST".equals(category)) {
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
     * 今日の子供時間補正
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

            if (activity.getIsCanWithChild()) {
                return 10;
            }

            return -10;
        }

        return 0;
    }

    /**
     * 時間帯補正
     */
    private int calculateTimeZonePoint(
            Activity activity,
            LocalTime currentTime) {

        String category = activity.getCategory();

        /*
         * 昼：10時以上15時未満
         */
        if (isBetween(currentTime, LocalTime.of(10, 0), LocalTime.of(15, 0))) {
        	
            if ("HOUSEWORK".equals(category) || "REST".equals(category)) {
                return 10;
            }
        }

        /*
         * 夕方：15時以上18時未満
         */
        if (isBetween(currentTime, LocalTime.of(15, 0), LocalTime.of(18, 0))) {

            if ("CHILD".equals(category)) {
                return 10;
            }

            if ("HOUSEWORK".equals(category) && activity.getRequiredTime() <= 10) {

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
                return 20;
            }

            if ("HOUSEWORK".equals(category) && !activity.getIsNoise()) {

                return 20;
            }
        }

        /*
         * 21時以上または5時未満
         */
        if (isFinishTime(currentTime)) {

            if ("REST".equals(category) || "CHILD".equals(category)) {

                return 15;
            }
        }

        return 0;
    }

    /**
     * 騒音活動を提案できる時間か判定
     */
    private boolean isNoiseAllowedTime(
            LocalTime currentTime) {

        return isBetween(currentTime, LocalTime.of(9, 0), LocalTime.of(18, 0)
        );
    }

    /**
     * FINISHを表示する時間か判定
     */
    private boolean isFinishTime(LocalTime currentTime) {

        return !currentTime.isBefore(LocalTime.of(21, 0))|| currentTime.isBefore(LocalTime.of(5, 0));
    }

    /**
     * start以上、end未満か判定
     */
    private boolean isBetween(
            LocalTime value,
            LocalTime start,
            LocalTime end) {

        return !value.isBefore(start) && value.isBefore(end);
    }

    /**
     * IDから活動を検索する。
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
     * 提案結果をJSONで返却
     */
    private void writeSuggestionResponse(
            HttpServletResponse response,
            String status,
            String mode,
            Integer remainingMinutes,
            String message,
            List<ScoredActivity> suggestions)
            throws IOException {

        StringBuilder json = new StringBuilder();

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

        for (int i = 0; i < suggestions.size(); i++) {
        	
            ScoredActivity scored = suggestions.get(i);

            Activity activity = scored.activity;

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
                                    activity.getCategory()
                            )
                    )
                    .append("\",");

            json.append("\"title\":\"")
                    .append(
                            escapeJson(
                                    activity.getActivityName()
                            )
                    )
                    .append("\",");

            json.append("\"requiredTime\":")
                    .append(
                            activity.getRequiredTime()
                    )
                    .append(",");

            json.append("\"message\":\"")
                    .append(
                            escapeJson(
                                    createActivityMessage(
                                            activity
                                    )
                            )
                    )
                    .append("\"");

            json.append("}");
        }

        json.append("]");
        json.append("}");

        response.setStatus(HttpServletResponse.SC_OK);

        response.getWriter().write(json.toString());
    }

    /**
     * 活動ごとの表示メッセージを作る
     */
    private String createActivityMessage(Activity activity) {

        String category = activity.getCategory();

        if ("REST".equals(category)) {

            return activity.getRequiredTime()
                    + "分だけ休憩して、"
                    + activity.getActivityName()
                    + "をしてみましょう。";
        }

        if ("CHILD".equals(category)) {

            return activity.getRequiredTime()
                    + "分だけ、"
                    + activity.getActivityName()
                    + "をしてみましょう。";
        }

        return activity.getRequiredTime()
                + "分で、"
                + activity.getActivityName()
                + "をやってみましょう。";
    }

    /**
     * JSONで使用する文字列をエスケープ
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

    /**
     * JSONレスポンスを設定
     */
    private void setJsonResponse(
            HttpServletResponse response) {

        response.setContentType("application/json; charset=UTF-8");

        response.setCharacterEncoding("UTF-8");
    }

    /**
     * エラーをJSONで返却
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
     * 活動と計算後スコアを保持する内部クラス
     */
    private static class ScoredActivity {

        private Activity activity;
        private int score;
    }
}
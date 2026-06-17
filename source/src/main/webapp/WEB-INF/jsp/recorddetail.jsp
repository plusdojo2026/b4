<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="dto.RecordHistoryDto" %>

<%!
    /**
     * 指定した活動名が、指定した曜日に記録されているか判定するメソッド
     *
     * recordList には、RecordDetailServlet から渡された
     * 1週間分の記録データが入る想定
     *
     * activityName は、画面の左側に表示する活動名
     * dayIndex は、画面上の曜日位置
     *
     * 今回は画面を「月〜日」の順番で表示するため、
     * dayIndex は以下の対応にしている。
     *
     * 月 = 0
     * 火 = 1
     * 水 = 2
     * 木 = 3
     * 金 = 4
     * 土 = 5
     * 日 = 6
     */
    private boolean hasRecord(
            List<RecordHistoryDto> recordList,
            String activityName,
            int dayIndex
    ) {
        // 記録一覧が空の場合は、記録なしとして扱う
        if (recordList == null || recordList.isEmpty()) {
            return false;
        }

        // 記録一覧を1件ずつ確認する
        for (RecordHistoryDto record : recordList) {

            // 念のためnullチェック
            if (record == null) {
                continue;
            }

            // 活動名または記録日時がない場合は判定できないためスキップ
            if (record.getActivityName() == null || record.getCreatedAt() == null) {
                continue;
            }

            // 画面側の活動名とDBから取得した活動名が一致しない場合はスキップ
            //
            // 完全一致で判定
            // →そのため、DBの activities.activity_name と
            // activityNames 配列の文字列は一致させる必要がある
            if (!activityName.equals(record.getActivityName())) {
                continue;
            }

            // 記録日時から曜日位置を取得する
            int recordDayIndex = getDayIndex(record.getCreatedAt());

            // 活動名と曜日が一致すれば、そのセルにペンギンを表示する
            if (recordDayIndex == dayIndex) {
                return true;
            }
        }

        return false;
    }

    /**
     * LocalDateTime から画面表示用の曜日番号を取得する。
     *
     * Javaの DayOfWeek#getValue() は以下の値を返す。
     *
     * 月 = 1
     * 火 = 2
     * 水 = 3
     * 木 = 4
     * 金 = 5
     * 土 = 6
     * 日 = 7
     *
     * この画面では「月〜日」の順で表示するため、
     * 以下に変換する。
     *
     * 月 = 0
     * 火 = 1
     * 水 = 2
     * 木 = 3
     * 金 = 4
     * 土 = 5
     * 日 = 6
     */
    private int getDayIndex(LocalDateTime dateTime) {
        int dayOfWeekValue = dateTime.getDayOfWeek().getValue();

        return dayOfWeekValue - 1;
    }

    /**
     * HTMLに出力する文字列を安全に表示するためのメソッド
     *
     * DBの活動名や画面表示文字列に、
     * < や > などが含まれていた場合にHTMLとして解釈されないよう念のため
     */
    private String escapeHtml(String value) {
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
    /*
     * コンテキストパスを取得
     *
     * CSS、画像、Servletリンクのパスに使用
     */
    String contextPath = request.getContextPath();

    /*
     * RecordDetailServlet から渡された記録一覧を取得
     *
     * request.getAttribute() の戻り値は Object 型のため、
     * List<RecordHistoryDto> にキャストする必要あり。
     *
     * 直接キャストすると警告が出る
     * → 一度 Object として受け取り、List かどうか確認
     */
    List<RecordHistoryDto> recordList = new ArrayList<RecordHistoryDto>();

    Object recordListObj = request.getAttribute("recordList");

    if (recordListObj instanceof List<?>) {
        @SuppressWarnings("unchecked")
        List<RecordHistoryDto> castedRecordList =
                (List<RecordHistoryDto>) recordListObj;

        recordList = castedRecordList;
    }

    /*
     * 表示期間
     *
     * 例；
     * 2026/06/15 ～ 2026/06/21
     *
     * RecordDetailServlet 側で作成して request にセット
     */
    String periodLabel = (String) request.getAttribute("periodLabel");

    /*
     * 前週・翌週ボタン用の値
     *
     * /record?weekOffset=-1
     * /record?weekOffset=0
     * /record?weekOffset=1
     *
     * のように、表示する週を切り替えるために使う
     */
    Integer previousWeekOffset =
            (Integer) request.getAttribute("previousWeekOffset");

    Integer nextWeekOffset =
            (Integer) request.getAttribute("nextWeekOffset");

    /*
     * null対策
     *
     * Servlet側で値がセットされなかった場合でも、
     * JSPがエラーにならないように初期値を入れておく
     */
    if (periodLabel == null || periodLabel.isEmpty()) {
        periodLabel = "";
    }

    if (previousWeekOffset == null) {
        previousWeekOffset = -1;
    }

    if (nextWeekOffset == null) {
        nextWeekOffset = 1;
    }

    /*
     * 画面に表示する活動名
     *
     * 固定配列で管理
     * DB設計・activities テーブルの activity_name と順次合わせたい
     */
    String[] activityNames = {
            "掃除",
            "洗濯",
            "料理",
            "片付け"
    };

    /*
     * 曜日ラベル
     *
     * RecordDetailServlet 側では、
     * 表示対象週を「月曜開始〜日曜終了」で作っているため、
     * JSP側も「月〜日」で表示
     */
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
    <title>記録一覧</title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

	<!-- CSS -->
    <link rel="stylesheet" href="<%= contextPath %>/css/recorddetail.css">
</head>

<body>

<div class="page-frame">

	<!-- ヘッダー -->
    <header class="record-header">

        <!-- サイドバー -->
        <a href="#"
           class="header-icon sidebar-button"
           aria-label="サイドバーを開く">
            <span></span>
        </a>

        <h1 class="header-title">記録一覧</h1>

        <!--右上の戻るボタン。 現時点ではチャット画面へ戻す。-->
        <a href="<%= contextPath %>/ChatServlet"
           class="header-icon exit-button"
           aria-label="ホームへ戻る">
            <span></span>
        </a>

    </header>

    <main class="record-main">

        <!--記録一覧のメインパネル
        家事名 × 曜日 の表形式で、記録があるセルにペンギンを表示する。-->
        <section class="record-panel">

            <div class="record-panel-title">
                <span>家事ごとの記録</span>
                <span><%= escapeHtml(periodLabel) %></span>
            </div>

            <div class="record-grid">

                <!-- 左上の空セル。活動名列と曜日列の交差部分 -->
                <div class="grid-empty"></div>

                <!-- 曜日ヘッダーを表示 -->
                <%
                    for (String dayLabel : dayLabels) {
                %>
                    <div class="day-label">
                        <%= escapeHtml(dayLabel) %>
                    </div>
                <%
                    }
                %>

                <!-- 活動名ごとに行を作成する -->
                <%
                    for (String activityName : activityNames) {
                %>

                    <!-- 左端に活動名を表示する -->
                    <div class="activity-label">
                        <%= escapeHtml(activityName) %>
                    </div>

                    <!-- 月〜日までの7セルを作成する-->
                    <%
                        for (int dayIndex = 0; dayIndex < 7; dayIndex++) {

                            /*
                             * その活動が、その曜日に記録されているか判定
                             * true の場合、セルにdoneクラスを付け、ペンギンを表示
                             */
                            boolean done = hasRecord(recordList, activityName, dayIndex);
                    %>

                        <div class="record-cell<%= done ? " done" : "" %>">

                            <%
                                if (done) {
                            %>
                                <!--記録がある場合だけペンギンアイコンを表示する-->
                                <img src="<%= contextPath %>/img/penguin.png" alt="記録あり">
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

        <!--前週・翌週の切替ボタン
         RecordDetailServlet が weekOffset を受け取り、対象週を切り替える-->
        <div class="week-buttons">

            <a href="<%= contextPath %>/record?weekOffset=<%= previousWeekOffset %>"
               class="week-button">
                前週
            </a>

            <a href="<%= contextPath %>/record?weekOffset=<%= nextWeekOffset %>"
               class="week-button">
                翌週
            </a>

        </div>

        <!--チャット画面へ戻るリンク
        記録一覧は参照画面なので、登録処理は行わない-->
        <div class="home-link-wrap">
            <a href="<%= contextPath %>/ChatServlet"
               class="home-link">
                ホームへ戻る
            </a>
        </div>

    </main>

</div>

</body>
</html>
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dto.RecordHistoryDto;

/**
 * 活動履歴テーブルを操作するDAO
 *
 * 使用するテーブル：
 * ・activity_histories
 * ・activities
 */
public class ActivityHistoryDao {

	//DB接続
    private static final String JDBC_URL =
            "jdbc:mysql://localhost:3306/b4"
            + "?characterEncoding=UTF-8"
            + "&useSSL=false"
            + "&serverTimezone=Asia/Tokyo";

    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    /**
     * DBへ接続する。
     *
     * @return DB接続
     * @throws Exception DB接続に失敗した場合
     */
    private Connection getConnection() throws Exception {

        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(
                JDBC_URL,
                DB_USER,
                DB_PASSWORD
        );
    }

    /**
     * 活動履歴を1件登録する。
     *
     * ReportServletから使用する。
     *
     * @param userId ユーザーID
     * @param activityId 活動ID
     * @return 登録されたactivity_histories.id
     *         登録失敗時は0
     */
    public int create(int userId, int activityId) {

        if (userId <= 0 || activityId <= 0) {
            return 0;
        }

        String sql =
                "INSERT INTO activity_histories ("
                + "user_id, "
                + "activity_id, "
                + "c_at, "
                + "u_at"
                + ") VALUES ("
                + "?, "
                + "?, "
                + "CURRENT_TIMESTAMP, "
                + "CURRENT_TIMESTAMP"
                + ")";

        try (
            Connection conn = getConnection();

            PreparedStatement pstmt = conn.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    )
        ) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, activityId);

            int result = pstmt.executeUpdate();

            if (result != 1) {
                return 0;
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 活動履歴を1件登録
     *
     * ReportServlet側でbooleanとして判定したい場合に使用
     *
     * @param userId ユーザーID
     * @param activityId 活動ID
     * @return 登録成功ならtrue
     */
    public boolean insert(int userId, int activityId) {

        int createdId = create(userId, activityId);

        return createdId > 0;
    }

    /**
     * 複数の活動履歴を登録
     *
     * 最初の「やったものを教えて」で
     * 複数の活動が選択された場合に使用
     *
     * @param userId ユーザーID
     * @param activityIds 活動ID一覧
     * @return 登録できた件数
     */
    public int createAll(
            int userId,
            List<Integer> activityIds) {

        if (userId <= 0 || activityIds == null || activityIds.isEmpty()) {

            return 0;
        }

        int successCount = 0;

        for (Integer activityId : activityIds) {

            if (activityId == null || activityId <= 0) {
                continue;
            }

            int createdId = create(userId, activityId);

            if (createdId > 0) {
                successCount++;
            }
        }

        return successCount;
    }

    /**
     * 指定期間の活動履歴一覧を取得
     *
     * RecordDetailServletなどから使用
     *
     * startAt以上、endAt未満の履歴を取得
     *
     * @param userId ユーザーID
     * @param startAt 開始日時
     * @param endAt 終了日時
     * @return 活動履歴一覧
     */
    public List<RecordHistoryDto> findRecordHistoryList(
            int userId,
            LocalDateTime startAt,
            LocalDateTime endAt) {

        List<RecordHistoryDto> recordList = new ArrayList<>();

        if (userId <= 0 || startAt == null || endAt == null || !startAt.isBefore(endAt)) {

            return recordList;
        }

        String sql =
                "SELECT "
                + "ah.id AS history_id, "
                + "a.id AS activity_id, "
                + "a.activity_name AS activity_name, "
                + "a.category AS category, "
                + "ah.c_at AS created_at "
                + "FROM activity_histories ah "
                + "INNER JOIN activities a "
                + "ON ah.activity_id = a.id "
                + "WHERE ah.user_id = ? "
                + "AND ah.c_at >= ? "
                + "AND ah.c_at < ? "
                + "ORDER BY ah.c_at DESC, ah.id DESC";

        try (
            Connection conn = getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, userId);
            pstmt.setTimestamp(2,Timestamp.valueOf(startAt));
            pstmt.setTimestamp(3,Timestamp.valueOf(endAt));

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    RecordHistoryDto dto = new RecordHistoryDto();

                    dto.setId(rs.getInt("history_id"));

                    dto.setActivityId(rs.getInt("activity_id"));
                    dto.setActivityName(rs.getString("activity_name"));
                    dto.setCategory(rs.getString("category"));

                    Timestamp createdAt = rs.getTimestamp("created_at");

                    if (createdAt != null) {
                        dto.setCreatedAt(createdAt.toLocalDateTime());
                    }

                    recordList.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordList;
    }
}

package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dto.RecordHistoryDto;

public class ActivityHistoryDao {

    //チャット画面で報告された活動を記録する
    public int create(int userId, int activityId) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // JDBCドライバを読み込む
            Class.forName("com.mysql.cj.jdbc.Driver");

            // DB接続
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/b4?"
                            + "&useSSL=false&"
                            + "&serverTimezone=Asia/Tokyo",
                    "root",
                    "password"
            );

            // SQL文の作成
            String sql =
                    "INSERT INTO activity_history ( " +
                    "user.id, " +
                    "activity.id, " +
                    "c_at, " +
                    "u_at " +
                    ") VALUES ( " +
                    "?, " +
                    "?, " +
                    "CURRENT_TIMESTAMP, " +
                    "CURRENT_TIMESTAMP " +
                    ")";

            // SQL準備
            pstmt = conn.prepareStatement(
                    sql,
                    PreparedStatement.RETURN_GENERATED_KEYS
            );

            // ?に値を入れる
            pstmt.setInt(1, userId);
            pstmt.setInt(2, activityId);

            // SQL実行
            int result = pstmt.executeUpdate();

            // 登録失敗
            if (result != 1) {
                return 0;
            }

            // 登録したIDを取得
            rs = pstmt.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            // ResultSet解放
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // PreparedStatement解放
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Connection解放
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    //記録一覧画面に表示する活動履歴を取得する
    public List<RecordHistoryDto> findRecordHistoryList(
            int userId,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {

        List<RecordHistoryDto> recordList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // JDBCドライバを読み込む
            Class.forName("com.mysql.cj.jdbc.Driver");

            // DB接続
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/business_card_db?"
                            + "characterEncoding=utf8"
                            + "&useSSL=false"
                            + "&serverTimezone=Asia/Tokyo",
                    "root",
                    "password"
            );

            // SQL文の作成
            String sql =
                    "SELECT " +
                    "ah.id AS id, " +
                    "a.id AS id, " +
                    "a.activity_name AS activity_name, " +
                    "a.category AS category, " +
                    "ah.c_at AS created_at " +
                    "FROM activity_history ah " +
                    "INNER JOIN activities a " +
                    "ON ah.activity_id = a.id " +
                    "WHERE ah.user_id = ? " +
                    "AND ah.c_at >= ? " +
                    "AND ah.c_at < ? " +
                    "ORDER BY ah.c_at DESC, ah.id DESC";

            // SQL準備
            pstmt = conn.prepareStatement(sql);

            // ?に値を入れる
            pstmt.setInt(1, userId);
            pstmt.setTimestamp(2, Timestamp.valueOf(startAt));
            pstmt.setTimestamp(3, Timestamp.valueOf(endAt));

            // SQL実行
            rs = pstmt.executeQuery();

            // 取得結果をDTOに詰める
            while (rs.next()) {

                RecordHistoryDto dto = new RecordHistoryDto();

                dto.setActivityHistoryId(rs.getInt("history_id"));
                dto.setActivityId(rs.getInt("activity_id"));
                dto.setActivityName(rs.getString("activity_name"));
                dto.setCategory(rs.getString("category"));

                Timestamp createdAt = rs.getTimestamp("created_at");

                if (createdAt != null) {
                    dto.setCreatedAt(createdAt.toLocalDateTime());
                }

                recordList.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            // ResultSet解放
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // PreparedStatement解放
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Connection解放
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return recordList;
    }
}
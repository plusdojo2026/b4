package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.Activity;

/**
 * activitiesテーブルを操作するDAO。
 *
 * 主な用途：
 * ・活動一覧を取得する
 * ・活動IDを指定して1件取得する
 */
public class ActivityDao {

    /*
     * DB接続情報。
     * ActivityHistoryDaoなど、ほかのDAOと同じ内容に統一する。
     */
    private static final String JDBC_URL =
            "jdbc:mysql://localhost:3306/b4"
            + "?characterEncoding=UTF-8"
            + "&useSSL=false"
            + "&serverTimezone=Asia/Tokyo";

    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    /**
     * DB接続を取得する。
     *
     * @return Connection
     * @throws SQLException DB接続に失敗した場合
     * @throws ClassNotFoundException JDBCドライバがない場合
     */
    private Connection getConnection()
            throws SQLException, ClassNotFoundException {

        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * activitiesテーブルの全活動を取得する。
     *
     * SuggestServletで提案候補を作るときに使用する。
     *
     * @return 全活動の一覧
     */
    public List<Activity> selectAll() {

        List<Activity> activityList = new ArrayList<>();

        String sql =
                "SELECT "
                + "id, "
                + "category, "
                + "activity_name, "
                + "required_time, "
                + "base_point, "
                + "can_with_child, "
                + "is_noise, "
                + "is_garbage, "
                + "flow_group, "
                + "flow_step, "
                + "garbage_action_type, "
                + "wait_minutes "
                + "FROM activities "
                + "ORDER BY id ASC";

        try (
            Connection conn = getConnection();

            PreparedStatement pstmt =conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery()
        ) {

            while (rs.next()) {

                Activity activity = createActivityFromResultSet(rs);

                activityList.add(activity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return activityList;
    }

    /**
     * IDを指定して活動を1件取得する。
     *
     * 活動完了後にrequired_timeを取得するときなどに使用する。
     *
     * @param activityId activities.id
     * @return 活動。存在しない場合はnull
     */
    public Activity findById(int activityId) {

        if (activityId <= 0) {
            return null;
        }

        String sql =
                "SELECT "
                + "id, "
                + "category, "
                + "activity_name, "
                + "required_time, "
                + "base_point, "
                + "can_with_child, "
                + "is_noise, "
                + "is_garbage, "
                + "flow_group, "
                + "flow_step, "
                + "garbage_action_type, "
                + "wait_minutes "
                + "FROM activities "
                + "WHERE id = ?";

        try (
            Connection conn = getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, activityId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return createActivityFromResultSet(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 指定時間以内で完了できる活動を取得する。
     *
     * 現段階ではSuggestServlet側でも時間判定するため、
     * 必須ではないが、テストや簡単な取得に使用できる。
     *
     * @param maxMinutes 最大所要時間
     * @return 指定時間以内の活動一覧
     */
    public List<Activity> selectByMaxRequiredTime(
            int maxMinutes) {

        List<Activity> activityList =
                new ArrayList<>();

        if (maxMinutes < 0) {
            return activityList;
        }

        String sql =
                "SELECT "
                + "id, "
                + "category, "
                + "activity_name, "
                + "required_time, "
                + "base_point, "
                + "can_with_child, "
                + "is_noise, "
                + "is_garbage, "
                + "flow_group, "
                + "flow_step, "
                + "garbage_action_type, "
                + "wait_minutes "
                + "FROM activities "
                + "WHERE required_time <= ? "
                + "ORDER BY id ASC";

        try (
            Connection conn = getConnection();

            PreparedStatement pstmt =
                    conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, maxMinutes);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    Activity activity =
                            createActivityFromResultSet(rs);

                    activityList.add(activity);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return activityList;
    }

    /**
     * ResultSetの現在行をActivityに変換する。
     *
     * selectAll()とfindById()で同じ変換処理を
     * 重複して書かないための共通メソッド。
     *
     * @param rs activitiesテーブルの取得結果
     * @return Activity
     * @throws SQLException ResultSetの読み取りに失敗した場合
     */
    private Activity createActivityFromResultSet(
            ResultSet rs)
            throws SQLException {

        Activity activity = new Activity();

        activity.setId(rs.getInt("id"));
        activity.setCategory(rs.getString("category"));
        activity.setActivityName(rs.getString("activity_name"));
        activity.setRequiredTime(rs.getInt("required_time"));
        activity.setBasePoint(rs.getInt("base_point"));
        activity.setIsCanWithChild(rs.getBoolean("can_with_child"));
        activity.setIsNoise(rs.getBoolean("is_noise"));
        activity.setIsGarbage(rs.getBoolean("is_garbage"));
        activity.setFlowGroup(rs.getString("flow_group"));
        activity.setFlowStep(getNullableInteger(rs, "flow_step"));
        activity.setGarbageActionType(rs.getString("garbage_action_type"));

        /*
         * wait_minutesがNULLの場合は0として扱う。
         */
        int waitMinutes = rs.getInt("wait_minutes");

        if (rs.wasNull()) {
            waitMinutes = 0;
        }

        activity.setWaitMinutes(
                waitMinutes
        );

        return activity;
    }

    /**
     * NULLの可能性があるint列をIntegerとして取得する。
     *
     * ResultSet#getInt()だけでは、
     * NULLと数値0を区別できないため使用する。
     *
     * @param rs ResultSet
     * @param columnName 列名
     * @return 数値。DBがNULLの場合はnull
     * @throws SQLException 読み取りに失敗した場合
     */
    private Integer getNullableInteger(
            ResultSet rs,
            String columnName)
            throws SQLException {

        int value = rs.getInt(columnName);

        if (rs.wasNull()) {
            return null;
        }

        return value;
    }
}

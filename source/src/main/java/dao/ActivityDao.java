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
 * activitiesテーブルを操作するDAO
 */
public class ActivityDao {

	/*
	 * DB接続情報
	 */
	private static final String JDBC_URL =
			"jdbc:mysql://localhost:3306/b4"
			+ "?characterEncoding=UTF-8"
			+ "&useSSL=false"
			+ "&allowPublicKeyRetrieval=true"
			+ "&serverTimezone=Asia/Tokyo";

	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "password";

	/**
	 * DB接続を取得
	 */
	private Connection getConnection()
			throws SQLException, ClassNotFoundException {

		Class.forName(
				"com.mysql.cj.jdbc.Driver");

		return DriverManager.getConnection(
				JDBC_URL,
				DB_USER,
				DB_PASSWORD);
	}

	/**
	 * 全活動を取得
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
				+ "has_garbages, "
				+ "flow_group, "
				+ "flow_step, "
				+ "wait_minutes "
				+ "FROM activities "
				+ "ORDER BY id ASC";

		try (
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery()
		) {

			while (rs.next()) {
				Activity activity =createActivityFromResultSet(rs);

				activityList.add(activity);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return activityList;
	}

	/**
	 * 活動IDを指定して取得
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
				+ "has_garbages, "
				+ "flow_group, "
				+ "flow_step, "
				+ "wait_minutes "
				+ "FROM activities "
				+ "WHERE id = ?";

		try (
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)
		) {

			pstmt.setInt(1, activityId);

			try (
				ResultSet rs = pstmt.executeQuery()
			) {

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
	 * 指定時間以内の活動を取得
	 */
	public List<Activity> selectByMaxRequiredTime(int maxMinutes) {

		List<Activity> activityList =new ArrayList<>();

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
				+ "has_garbages, "
				+ "flow_group, "
				+ "flow_step, "
				+ "wait_minutes "
				+ "FROM activities "
				+ "WHERE required_time <= ? "
				+ "ORDER BY id ASC";

		try (
			Connection conn = getConnection();
			PreparedStatement pstmt =conn.prepareStatement(sql)
		) {

			pstmt.setInt(1, maxMinutes);

			try (
				ResultSet rs = pstmt.executeQuery()
			) {

				while (rs.next()) {
					Activity activity =createActivityFromResultSet(rs);

					activityList.add(activity);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return activityList;
	}

	/**
	 * ResultSetからActivityを作成
	 */
	private Activity createActivityFromResultSet(ResultSet rs)
			throws SQLException {

		Activity activity = new Activity();

		activity.setId(rs.getInt("id"));
		activity.setCategory(rs.getString("category"));
		activity.setActivityName(rs.getString("activity_name"));
		activity.setRequiredTime(rs.getInt("required_time"));
		activity.setBasePoint(rs.getInt("base_point"));
		activity.setIsCanWithChild(rs.getBoolean("can_with_child"));
		activity.setIsNoise(rs.getBoolean("is_noise"));
		activity.setHasGarbage(rs.getBoolean("has_garbages"));
		activity.setFlowGroup(rs.getString("flow_group"));
		activity.setFlowStep(getNullableInteger(rs,"flow_step"));
		int waitMinutes =rs.getInt("wait_minutes");

		if (rs.wasNull()) {
			waitMinutes = 0;
		}

		activity.setWaitMinutes(
				waitMinutes);

		return activity;
	}

	/**
	 * NULL可能な数値を取得
	 */
	private Integer getNullableInteger(
			ResultSet rs,
			String columnName)
			throws SQLException {

		int value =
				rs.getInt(columnName);

		if (rs.wasNull()) {
			return null;
		}

		return value;
	}
}
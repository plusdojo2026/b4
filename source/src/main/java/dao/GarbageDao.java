package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import dto.Garbage;

/**
 * garbagesテーブルを操作するDAO
 */
public class GarbageDao {

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
	private Connection getConnection() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");

		return DriverManager.getConnection(
				JDBC_URL,
				DB_USER,
				DB_PASSWORD);
	}

	/**
	 * ゴミ情報を登録
	 */
	public boolean insert(Garbage garbage) {
		if (garbage == null
				|| garbage.getUserId() <= 0
				|| garbage.getGarbageDay() == null
				|| garbage.getGarbageDay().isBlank()
				|| garbage.getGarbageName() == null
				|| garbage.getGarbageName().isBlank()) {

			return false;
		}

		String sql =
				"INSERT INTO garbages "
				+ "(user_id, garbage_day, garbage_name) "
				+ "VALUES (?, ?, ?)";

		try (
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)
		) {

			pstmt.setInt(1, garbage.getUserId());
			pstmt.setString(2, garbage.getGarbageDay());
			pstmt.setString(3, garbage.getGarbageName());

			return pstmt.executeUpdate() == 1;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 指定ユーザーのゴミ情報を取得
	 */
	public List<Garbage> select(int userId) {
		return selectByUserId(userId);
	}

	/**
	 * 指定ユーザーのゴミ情報を取得
	 */
	public List<Garbage> selectByUserId(int userId) {
		List<Garbage> garbageList = new ArrayList<>();

		if (userId <= 0) {
			return garbageList;
		}

		String sql =
				"SELECT "
				+ "id, "
				+ "user_id, "
				+ "garbage_day, "
				+ "garbage_name, "
				+ "c_at, "
				+ "u_at "
				+ "FROM garbages "
				+ "WHERE user_id = ? "
				+ "ORDER BY id ASC";

		try (
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)
		) {

			pstmt.setInt(1, userId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					garbageList.add(
							createGarbage(rs));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return garbageList;
	}

	/**
	 * 指定日のゴミ情報を取得
	 */
	public List<Garbage> selectByDate(
			int userId,
			LocalDate targetDate) {

		List<Garbage> resultList = new ArrayList<>();

		if (userId <= 0 || targetDate == null) {
			return resultList;
		}

		List<Garbage> garbageList = selectByUserId(userId);

		DayOfWeek targetDay = targetDate.getDayOfWeek();

		for (Garbage garbage : garbageList) {
			if (containsDayOfWeek(
					garbage.getGarbageDay(),
					targetDay)) {

				resultList.add(garbage);
			}
		}

		return resultList;
	}

	/**
	 * 指定日がゴミの日か判定
	 */
	public boolean isGarbageDay(int userId, LocalDate targetDate) {

		return !selectByDate(userId, targetDate).isEmpty();
	}

	/**
	 * 指定日の翌日がゴミの日か判定
	 */
	public boolean isDayBeforeGarbageDay(int userId, LocalDate targetDate) {

		if (targetDate == null) {
			return false;
		}

		return isGarbageDay(userId, targetDate.plusDays(1));
	}

	/**
	 * 指定日のゴミ分類名を取得
	 */
	public List<String> selectGarbageNamesByDate(int userId, LocalDate targetDate) {

		Set<String> garbageNameSet =new LinkedHashSet<>();

		List<Garbage> garbageList =
				selectByDate(userId, targetDate);

		for (Garbage garbage : garbageList) {
			String garbageName = garbage.getGarbageName();

			if (garbageName != null && !garbageName.isBlank()) {

				garbageNameSet.add(garbageName.trim());
			}
		}

		return new ArrayList<>(garbageNameSet);
	}

	/**
	 * ResultSetからGarbageを作成
	 */
	private Garbage createGarbage(ResultSet rs) throws Exception {

		Garbage garbage = new Garbage();

		garbage.setId(rs.getInt("id"));
		garbage.setUserId(rs.getInt("user_id"));
		garbage.setGarbageDay(rs.getString("garbage_day"));
		garbage.setGarbageName(rs.getString("garbage_name"));

		Timestamp cAt =rs.getTimestamp("c_at");

		if (cAt != null) {
			garbage.setCAt(cAt.toLocalDateTime());
		}

		Timestamp uAt =rs.getTimestamp("u_at");

		if (uAt != null) {
			garbage.setUAt(
					uAt.toLocalDateTime());
		}

		return garbage;
	}

	/**
	 * 文字列に指定曜日が含まれるか判定
	 */
	private boolean containsDayOfWeek(
			String garbageDay,
			DayOfWeek targetDay) {

		if (garbageDay == null
				|| garbageDay.isBlank()
				|| targetDay == null) {

			return false;
		}

		String normalized =
				garbageDay
				.replace("毎週", "")
				.replace("曜日", "")
				.replace("曜", "")
				.replace("／", "/")
				.trim();

		String[] dayTexts =normalized.split(
						"[,、/・\\s]+");

		for (String dayText : dayTexts) {
			DayOfWeek convertedDay =convertToDayOfWeek(dayText);

			if (convertedDay == targetDay) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 曜日文字列をDayOfWeekへ変換
	 */
	private DayOfWeek convertToDayOfWeek(
			String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		String normalized =
				value.trim()
				.toUpperCase(Locale.ROOT);

		switch (normalized) {
			case "月":
			case "MON":
			case "MONDAY":
			case "1":
				return DayOfWeek.MONDAY;

			case "火":
			case "TUE":
			case "TUES":
			case "TUESDAY":
			case "2":
				return DayOfWeek.TUESDAY;

			case "水":
			case "WED":
			case "WEDNESDAY":
			case "3":
				return DayOfWeek.WEDNESDAY;

			case "木":
			case "THU":
			case "THUR":
			case "THURS":
			case "THURSDAY":
			case "4":
				return DayOfWeek.THURSDAY;

			case "金":
			case "FRI":
			case "FRIDAY":
			case "5":
				return DayOfWeek.FRIDAY;

			case "土":
			case "SAT":
			case "SATURDAY":
			case "6":
				return DayOfWeek.SATURDAY;

			case "日":
			case "SUN":
			case "SUNDAY":
			case "0":
			case "7":
				return DayOfWeek.SUNDAY;

			default:
				return null;
		}
	}
}
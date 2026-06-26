package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dto.RecordHistoryDto;

/**
 * 活動履歴テーブルを操作するDAO。
 *
 * 使用するテーブル：
 * ・activity_histories
 * ・activities
 *
 * 主な役割：
 * ・活動履歴の登録
 * ・期間を指定した履歴一覧の取得
 * ・直前に実施した活動の取得
 * ・活動ごとの最終実施日時の取得
 */
public class ActivityHistoryDao {

	// DB接続情報
	private static final String JDBC_URL = 
			// サーバー環境
			/*"jdbc:mysql://localhost:3306/b4?"
			+ "useSSL=false&allowPublicKeyRetrieval=true"
			+ "&serverTimezone=Asia/Tokyo"
			+ "&connectTimeout=30000";*/
			// ローカル環境
			"jdbc:mysql://localhost:3306/b4"
			+ "?characterEncoding=UTF-8"
			+ "&useSSL=false"
			+ "&serverTimezone=Asia/Tokyo";

	private static final String DB_USER = "root"/*"b4"*/;
	private static final String DB_PASSWORD = "password"/*"6vvRyvdGp4t4Cr3C"*/;

	/**
	 * DBへ接続する。
	 *
	 * @return DB接続
	 * @throws Exception JDBCドライバーの読込またはDB接続に失敗した場合
	 */
	private Connection getConnection() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
	}

	/**
	 * 活動履歴を1件登録する。
	 *
	 * ReportServletから使用する。
	 *
	 * @param userId ユーザーID
	 * @param activityId 活動ID
	 * @return 登録されたactivity_histories.id。登録失敗時は0
	 */
	public int create(int userId, int activityId) {
		if (userId <= 0 || activityId <= 0) {
			return 0;
		}

		String sql = "INSERT INTO activity_histories ("
				+ "user_id, activity_id, c_at, u_at"
				+ ") VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

		try (
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
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
			System.err.println("ActivityHistoryDao#createでエラーが発生しました。");
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 活動履歴を1件登録する。
	 *
	 * ReportServlet側でbooleanとして判定したい場合に使用する。
	 *
	 * @param userId ユーザーID
	 * @param activityId 活動ID
	 * @return 登録成功ならtrue
	 */
	public boolean insert(int userId, int activityId) {
		return create(userId, activityId) > 0;
	}

	/**
	 * 複数の活動履歴を登録する。
	 *
	 * 最初の「やったものを教えて」で複数の活動が選択された場合に使用する。
	 *
	 * @param userId ユーザーID
	 * @param activityIds 活動ID一覧
	 * @return 登録できた件数
	 */
	public int createAll(int userId, List<Integer> activityIds) {
		if (userId <= 0 || activityIds == null || activityIds.isEmpty()) {
			return 0;
		}

		int successCount = 0;

		for (Integer activityId : activityIds) {
			if (activityId == null || activityId <= 0) {
				continue;
			}

			if (create(userId, activityId) > 0) {
				successCount++;
			}
		}

		return successCount;
	}

	/**
	 * 指定期間の活動履歴一覧を取得する。
	 *
	 * RecordDetailServletやSuggestServletから使用する。
	 * startAt以上、endAt未満の履歴を新しい順で取得する。
	 *
	 * @param userId ユーザーID
	 * @param startAt 開始日時
	 * @param endAt 終了日時
	 * @return 活動履歴一覧。取得できない場合は空のList
	 */
	public List<RecordHistoryDto> findRecordHistoryList(int userId, LocalDateTime startAt, LocalDateTime endAt) {
		List<RecordHistoryDto> recordList = new ArrayList<>();

		if (userId <= 0 || startAt == null || endAt == null || !startAt.isBefore(endAt)) {
			return recordList;
		}

		String sql = "SELECT "
				+ "ah.id AS history_id, "
				+ "a.id AS activity_id, "
				+ "a.activity_name AS activity_name, "
				+ "a.category AS category, "
				+ "ah.c_at AS created_at "
				+ "FROM activity_histories ah "
				+ "INNER JOIN activities a ON ah.activity_id = a.id "
				+ "WHERE ah.user_id = ? "
				+ "AND ah.c_at >= ? "
				+ "AND ah.c_at < ? "
				+ "ORDER BY ah.c_at DESC, ah.id DESC";

		try (
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)
		) {

			pstmt.setInt(1, userId);
			pstmt.setTimestamp(2, Timestamp.valueOf(startAt));
			pstmt.setTimestamp(3, Timestamp.valueOf(endAt));

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					recordList.add(createRecordHistoryDto(rs));
				}
			}

		} catch (Exception e) {
			System.err.println("ActivityHistoryDao#findRecordHistoryListでエラーが発生しました。");
			e.printStackTrace();
		}

		return recordList;
	}

	/**
	 * ユーザーが最後に実施した活動履歴を1件取得する。
	 *
	 * SuggestServletで「直前に行った活動」による補正を行うときに使用する。
	 *
	 * 例：
	 * ・直前が子供と一緒にできない家事なら、CHILDやRESTを優先する
	 * ・直前の活動カテゴリーを確認する
	 *
	 * @param userId ユーザーID
	 * @return 最後の活動履歴。履歴がない場合はnull
	 */
	public RecordHistoryDto findLatestHistory(int userId) {
		if (userId <= 0) {
			return null;
		}

		String sql = "SELECT "
				+ "ah.id AS history_id, "
				+ "a.id AS activity_id, "
				+ "a.activity_name AS activity_name, "
				+ "a.category AS category, "
				+ "ah.c_at AS created_at "
				+ "FROM activity_histories ah "
				+ "INNER JOIN activities a ON ah.activity_id = a.id "
				+ "WHERE ah.user_id = ? "
				+ "ORDER BY ah.c_at DESC, ah.id DESC "
				+ "LIMIT 1";

		try (
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)
		) {

			pstmt.setInt(1, userId);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return createRecordHistoryDto(rs);
				}
			}

		} catch (Exception e) {
			System.err.println("ActivityHistoryDao#findLatestHistoryでエラーが発生しました。");
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 指定した活動を最後に実施した日時を取得
	 *
	 * 特定の活動1件だけを確認したい場合に使用
	 * 活動を一度も実施していない場合はnullを返す
	 *
	 * SuggestServletで全活動を採点するときに、このメソッドを活動件数分呼ぶと
	 * SQLの実行回数が多くなるため、その場合はfindLastExecutedAtMapを使用
	 *
	 * @param userId ユーザーID
	 * @param activityId 活動ID
	 * @return 最終実施日時。未実施または取得失敗時はnull
	 */
	public LocalDateTime findLastExecutedAt(int userId, int activityId) {
		if (userId <= 0 || activityId <= 0) {
			return null;
		}

		String sql = "SELECT MAX(c_at) AS last_executed_at "
				+ "FROM activity_histories "
				+ "WHERE user_id = ? AND activity_id = ?";

		try (
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)
		) {

			pstmt.setInt(1, userId);
			pstmt.setInt(2, activityId);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					Timestamp lastExecutedAt = rs.getTimestamp("last_executed_at");

					if (lastExecutedAt != null) {
						return lastExecutedAt.toLocalDateTime();
					}
				}
			}

		} catch (Exception e) {
			System.err.println("ActivityHistoryDao#findLastExecutedAtでエラーが発生しました。");
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * ユーザーが実施した活動について、活動IDごとの最終実施日時を取得
	 *
	 * Map形式：
	 * ・キー：activity_id
	 * ・値：その活動を最後に実施した日時
	 *
	 * SuggestServletで全活動の「最後に実施してから何日経過したか」を判定するときに使用
	 *
	 * 例：
	 * activityId=1 → 2026-06-20 10:30
	 * activityId=2 → 2026-06-23 18:00
	 *
	 * 履歴が一度もない活動はMapに含まれない
	 *
	 * @param userId ユーザーID
	 * @return 活動IDと最終実施日時のMap 取得できない場合は空のMap
	 */
	public Map<Integer, LocalDateTime> findLastExecutedAtMap(int userId) {
		Map<Integer, LocalDateTime> lastExecutedAtMap = new HashMap<>();

		if (userId <= 0) {
			return lastExecutedAtMap;
		}

		String sql = "SELECT "
				+ "activity_id, "
				+ "MAX(c_at) AS last_executed_at "
				+ "FROM activity_histories "
				+ "WHERE user_id = ? "
				+ "GROUP BY activity_id";

		try (
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)
		) {

			pstmt.setInt(1, userId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					int activityId = rs.getInt("activity_id");
					Timestamp lastExecutedAt = rs.getTimestamp("last_executed_at");

					if (lastExecutedAt != null) {
						lastExecutedAtMap.put(activityId, lastExecutedAt.toLocalDateTime());
					}
				}
			}

		} catch (Exception e) {
			System.err.println("ActivityHistoryDao#findLastExecutedAtMapでエラーが発生しました。");
			e.printStackTrace();
		}

		return lastExecutedAtMap;
	}

	/**
	 *
	 * findRecordHistoryListとfindLatestHistoryで同じ変換処理を
	 * 重複して書かないための共通メソッド
	 *
	 * SQLでは以下の別名が必要：
	 * ・history_id
	 * ・activity_id
	 * ・activity_name
	 * ・category
	 * ・created_at
	 *
	 * @param rs SQL実行結果
	 * @return 活動履歴DTO
	 * @throws Exception ResultSetの読込に失敗した場合
	 */
	private RecordHistoryDto createRecordHistoryDto(ResultSet rs) throws Exception {
		RecordHistoryDto dto = new RecordHistoryDto();

		dto.setId(rs.getInt("history_id"));
		dto.setActivityId(rs.getInt("activity_id"));
		dto.setActivityName(rs.getString("activity_name"));
		dto.setCategory(rs.getString("category"));

		Timestamp createdAt = rs.getTimestamp("created_at");

		if (createdAt != null) {
			dto.setCreatedAt(createdAt.toLocalDateTime());
		}	

		return dto;
	}
}

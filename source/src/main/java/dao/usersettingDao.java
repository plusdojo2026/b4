package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dto.UserSetting;

public class usersettingDao {
    private final String URL = "jdbc:mysql://localhost/your_db_name";
    private final String USER = "your_user";
    private final String PASS = "your_password";

    // 保存用
    public void save(UserSetting user) {
        String sql = "UPDATE user_profiles SET icon_name=?, user_name=?, child_count=?, garbage_name=?, garbage_day=? WHERE id=1";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getIconName());
            ps.setString(2, user.getUserName());
            ps.setInt(3, user.getChildCount());
            ps.setString(4, user.getGarbageName());
            ps.setString(5, user.getGarbageDay());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                insertNewRecord(user);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 新規追加
    private void insertNewRecord(UserSetting user) {
        String sql = "INSERT INTO user_profiles (id, icon_name, user_name, child_count, garbage_name, garbage_day) VALUES (1, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getIconName());
            ps.setString(2, user.getUserName());
            ps.setInt(3, user.getChildCount());
            ps.setString(4, user.getGarbageName());
            ps.setString(5, user.getGarbageDay());
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    // 取得用
    public UserSetting find() {
        UserSetting user = new UserSetting();
                String sql = "SELECT * FROM user_profiles WHERE id=1";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                user.setIconName(rs.getString("icon_name"));
                user.setUserName(rs.getString("user_name"));
                user.setChildCount(rs.getInt("child_count"));
                user.setGarbageName(rs.getString("garbage_name"));
                user.setGarbageDay(rs.getString("garbage_day"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return user;
    }
    
    
    //アイコン追加用メソッド
    public boolean update(int icon_id,int user_id) {
		Connection conn = null;
		boolean result = false;

		try {
			// JDBCドライバを読み込む
			Class.forName("com.mysql.cj.jdbc.Driver");

			// データベースに接続する
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b4?"
			        + "characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true", // ★末尾に「&allowPublicKeyRetrieval=true」を追記
			        "root", "Aoisql");

			// SQL文を準備する
			String sql = "UPDATE users SET icon_id=? WHERE user_id=?";
			PreparedStatement pStmt = conn.prepareStatement(sql);

			// SQL文を完成させる
			if (icon_id != 0) {
				pStmt.setInt(1, icon_id);
			} else {
				pStmt.setInt(1, 0);
			}
			if (user_id != 0) {
				pStmt.setInt(2, user_id);
			} else {
				pStmt.setInt(2, 0);
			}
			


			// SQL文を実行する
			if (pStmt.executeUpdate() == 1) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			// データベースを切断
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		// 結果を返す
		return result;
	}
    
    
    
}
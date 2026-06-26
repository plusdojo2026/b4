package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class usersettingDao {
    private final String URL = "jdbc:mysql://localhost:3306/b4?characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true";
    private final String USER = "your_user";
    private final String PASS = "your_password";

    // ユーザー設定がすでに存在するか確認
    public boolean userSearch(int user_id) {
        Connection conn = null;
        boolean settingResult = false;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);

            String sql = "SELECT count(*) FROM user_settings WHERE user_id=?";
            PreparedStatement pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, user_id);
            
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("count(*)") >= 1) {
                    settingResult = true;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return settingResult;
    }

    // 初回登録用
    public boolean insertSetting(int user_id, int icon_id) {
        Connection conn = null;
        boolean result = false;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);

            String sql = "INSERT INTO user_settings (user_id, icon_id) VALUES (?, ?)";
            PreparedStatement pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, user_id);
            pStmt.setInt(2, icon_id);

            if (pStmt.executeUpdate() == 1) {
                result = true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return result;
    }

    // 更新用
    public boolean updateSetting(int user_id, int icon_id) {
        Connection conn = null;
        boolean result = false;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);

            String sql = "UPDATE user_settings SET icon_id=? WHERE user_id=?";
            PreparedStatement pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, icon_id);
            pStmt.setInt(2, user_id);

            if (pStmt.executeUpdate() == 1) {
                result = true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return result;
    }

    public boolean update(int icon_id, int user_id) {
        return updateSetting(user_id, icon_id);
    }
}
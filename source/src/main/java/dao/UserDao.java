package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dto.User;

public class UserDao {
	// 引数で指定されたidpwでログイン成功ならtrueを返す
		public boolean isLoginOK(User idpw) {
			Connection conn = null;
			boolean loginResult = false;
			
			try {
				// JDBCドライバを読み込む
				Class.forName("com.mysql.cj.jdbc.Driver");

				// データベースに接続する
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/webapp1?"
						+ "characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true",
						"root", "password");
			
				// SELECT文を準備する
				String sql = "SELECT count(*) FROM User WHERE id=? AND pw=?";
				PreparedStatement pStmt = conn.prepareStatement(sql);
				pStmt.setString(1, idpw.getUser_nickname());
				pStmt.setString(2, idpw.getPassword());
				
				// SELECT文を実行し、結果表を取得する
				ResultSet rs = pStmt.executeQuery();

				// ユーザーIDとパスワードが一致するユーザーがいれば結果をtrueにする
				rs.next();
				if (rs.getInt("count(*)") == 1) {
					loginResult = true;
				}
			// エラー対応
			} catch (SQLException e) {
				e.printStackTrace();
				loginResult = false;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				loginResult = false;
			} finally {
				// データベースを切断
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
						loginResult = false;
					}
				}
			}
			// 結果を返す
			return loginResult;
		}
}


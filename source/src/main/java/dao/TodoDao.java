package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import dto.Todo;

public class TodoDao {
	// 引数gbで指定されたレコードを登録し、成功したらtrueを返す
		public boolean insert(Todo td) {
			Connection conn = null;
			boolean result = false;
			
			try {
				// JDBCドライバを読み込む
				Class.forName("com.mysql.cj.jdbc.Driver");

				// データベースに接続する
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b4?"
						+ "characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true",
						"root", "password");
				
				// SQL文を準備する
				String sql = "INSERT INTO todos (todo_name,todo_date,user_id) VALUES (?,?,?)";
				PreparedStatement pStmt = conn.prepareStatement(sql);

				
				// SQL文を完成させる
				if (td.getTodo_name() != null) {
					pStmt.setString(1, td.getTodo_name());
				} else {
					pStmt.setString(1,"");
				}
				if (td.getTodo_date() != null) {
					pStmt.setString(2, td.getTodo_date());
				} else {
					pStmt.setString(2,"");
				}
				if (td.getUser_id() != 0) {
					pStmt.setInt(3, td.getUser_id());
				} else {
					pStmt.setInt(3,0);
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

package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.Todo;

public class TodoDao {
	// 引数tdで指定されたレコードを登録し、成功したらtrueを返す
		public boolean insert(Todo td) {
			Connection conn = null;
			boolean result = false;
			
			try {
				// JDBCドライバを読み込む
				Class.forName("com.mysql.cj.jdbc.Driver");

				// データベースに接続する
				// サーバー環境
				/*conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b4?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo&connectTimeout=30000",
						"b4", "6vvRyvdGp4t4Cr3C");*/
				// ローカル環境
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b4?"
						+ "characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B9&rewriteBatchedStatements=true",
						"root", "password");
				
				// SQL文を準備する
				String sql = "INSERT INTO todos (todo_name,todo_date,user_id) VALUES (?,?,?)";
				PreparedStatement pStmt = conn.prepareStatement(sql);

				
				// SQL文を完成させる
				if (td.getTodoName() != null) {
					pStmt.setString(1, td.getTodoName());
				} else {
					pStmt.setString(1,"");
				}
				if (td.getTodoDate() != null) {
					pStmt.setString(2, td.getTodoDate());
				} else {
					pStmt.setString(2,"");
				}
				if (td.getUserId() != 0) {
					pStmt.setInt(3, td.getUserId());
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
		
		public List<Todo> select(int userId) {
			Connection conn = null; 	// データベースに接続していない
			List<Todo> TodoList = new ArrayList<Todo>();
			// Garbage gab = new Garbage();	   // 入れ物gabを作っておく

			try {
				// JDBCドライバを読み込む
				Class.forName("com.mysql.cj.jdbc.Driver");

				// データベースに接続する
				// サーバー環境
				/*conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b4?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo&connectTimeout=30000",
						"b4", "6vvRyvdGp4t4Cr3C");*/
				// ローカル環境
				conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/b4?"
						+ "characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true",
						"root", "password");

				// SQL文を準備する
				String sql = "SELECT id,todo_name,todo_date FROM todos WHERE user_id = 1 ORDER BY todo_date ASC";		// idの値でデータを指定
				PreparedStatement pStmt = conn.prepareStatement(sql);

				/* テスト中はコメントアウト
				 * // SQL文を完成させる
				if (user_id != 0) {							// 入力された文字で検索
					pStmt.setInt(1, user_id);
				} else {										// nullだったらすべてあいまい検索
					pStmt.setString(1, "%");
				}*/
				
				// SQL文を実行し、結果表を取得する
				ResultSet rs = pStmt.executeQuery();

				// 結果表をコレクションにコピーする
				while (rs.next()) {
					Todo tod = new Todo(
							rs.getInt("id"),
							rs.getInt(userId),
							rs.getString("todo_name"),
							rs.getString("todo_date")
							);
					TodoList.add(tod);
				}
			// エラー対応	
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
			return TodoList;
		}
		
		// 引数idで指定された番号のレコードを削除し、成功したらtrueを返す
		public boolean delete(int id) {
			Connection conn = null;
			boolean result = false;

			try {
				// JDBCドライバを読み込む
				Class.forName("com.mysql.cj.jdbc.Driver");

				// データベースに接続する
				// サーバー環境
				/*conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b4?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo&connectTimeout=30000",
						"b4", "6vvRyvdGp4t4Cr3C");*/
				// ローカル環境
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b4?"
						+ "characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true",
						"root", "password");

				// SQL文を準備する
				String sql = "DELETE FROM todos WHERE id=?";		// idの値でデータを指定
				PreparedStatement pStmt = conn.prepareStatement(sql);

				// SQL文を完成させる
				pStmt.setInt(1, id);
				
				// SQL文を実行する
				if (pStmt.executeUpdate() == 1) {
					result = true;
				}
			// エラー対応
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

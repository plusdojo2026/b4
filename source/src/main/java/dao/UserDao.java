package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dto.LoginUser;
import dto.User;

public class UserDao {
	// 引数で指定されたnamepwでログイン成功ならtrueを返す
		public boolean isLoginOK(User namepassword) {
			Connection conn = null;
			boolean loginResult = false;
			
			try {
				// JDBCドライバを読み込む
				Class.forName("com.mysql.cj.jdbc.Driver");

				// データベースに接続する
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b4?"
						+ "characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true",
						"root", "password");
			
				// SELECT文を準備する
				String sql = "SELECT count(*) FROM users WHERE user_nickname=? AND password=?";
				PreparedStatement pStmt = conn.prepareStatement(sql);
				// pStmt.setInt(1, namepw.getId());
				pStmt.setString(1, namepassword.getUser_nickname());
				pStmt.setString(2, namepassword.getPassword());
				
				// SELECT文を実行し、結果表を取得する
				ResultSet rs = pStmt.executeQuery();

				// ユーザーニックネームとパスワードが一致するユーザーがいれば結果をtrueにする
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
		
		public LoginUser findLoginUser(String name, String password) {
			Connection conn = null;        // データベースに接続していない
			LoginUser ls = new LoginUser();	   // 入れ物を作っておく

			try {
				// JDBCドライバを読み込む
				Class.forName("com.mysql.cj.jdbc.Driver");

				// データベースに接続する
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b4?"
						+ "characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true",
						"root", "password");

				// SQL文を準備する
				String sql = "SELECT id, user_nickname, password FROM users WHERE user_nickname = ?";		// idの値でデータを指定
				PreparedStatement pStmt = conn.prepareStatement(sql);

				// SQL文を完成させる
				if (name != null) {							// 入力された文字で検索
					pStmt.setString(1, name);
				}
				
				// SQL文を実行し、結果表を取得する
				ResultSet rs = pStmt.executeQuery();

				// 結果表をコレクションにコピーする
				rs.next();
				ls.setUserName(name);
				ls.setUserId(rs.getInt("id"));
				ls.setPassword(rs.getString("password"));
				
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
			return ls;
		}
		
	// 引数cardで指定されたレコードを登録し、成功したらtrueを返す
		public boolean insert(User idpw) {
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
				String sql = "INSERT INTO Users (user_nickname,password,mail_address) VALUES (?, ?, ?)";
				PreparedStatement pStmt = conn.prepareStatement(sql);

				// SQL文を完成させる
				if (idpw.getUser_nickname() != null) {
					pStmt.setString(1, idpw.getUser_nickname());
				} else {
					pStmt.setString(1, "");
				}
				if (idpw.getPassword() != null) {
					pStmt.setString(2, idpw.getPassword());
				} else {
					pStmt.setString(2, "");
				}
				if (idpw.getMail_address() != null) {
					pStmt.setString(3, idpw.getMail_address());
				} else {
					pStmt.setString(3, "");
				}
//				if (idpw.getC_at() != null) {
//					pStmt.setString(4, idpw.getC_at());
//				} else {
//					pStmt.setString(4, "");
//				}
//				if (idpw.getU_at() != null) {
//					pStmt.setString(5, idpw.getU_at());
//				} else {
//					pStmt.setString(5, "");
//				}
				
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


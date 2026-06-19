package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dto.Garbage;

public class GarbageDao {
	// 引数gbで指定されたレコードを登録し、成功したらtrueを返す
	public boolean insert(Garbage gb) {
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
			String sql = "INSERT INTO garbages (user_id,garbage_day,garbage_name) VALUES (0, ?, ?)";
			PreparedStatement pStmt = conn.prepareStatement(sql);

			// SQL文を完成させる
			if (gb.getUser_id() != 0) {
				pStmt.setInt(1, gb.getUser_id());
			}else {
				pStmt.setString(1,"");
			}
			if (gb.getGarbage_day() != null) {
				pStmt.setString(2, gb.getGarbage_day());
			} else {
				pStmt.setString(2,"");
			}
			if (gb.getGarbage_name() != null) {
				pStmt.setString(3, gb.getGarbage_name());
			} else {
				pStmt.setString(3,"");
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
	
	public Garbage select(int id) {
		Connection conn = null;        // データベースに接続していない
		Garbage gab = new Garbage();	   // 入れ物gabを作っておく

		try {
			// JDBCドライバを読み込む
			Class.forName("com.mysql.cj.jdbc.Driver");

			// データベースに接続する
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b4?"
					+ "characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true",
					"root", "password");

			// SQL文を準備する
			String sql = "SELECT garbage_day, garbage_name FROM garbages WHERE user_id = 1";		// idの値でデータを指定
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
			rs.next();
			gab.setGarbage_day(rs.getString("garbage_day"));
			rs.next();
			gab.setGarbage_name(rs.getString("garbage_name"));
			
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
		return gab;
	}
}

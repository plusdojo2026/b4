package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dto.Icon;

public class IconDao {
	/*// 引数icで指定されたレコードを更新し、成功したらtrueを返す
		public boolean update(Usersetting ic) {
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
				String sql = "UPDATE user_settings SET icon_id WHERE id=?";
				PreparedStatement pStmt = conn.prepareStatement(sql);
				
				// SQL文を完成させる
				if (ic.getIcon_id() != null) {				// 文字入力があったら?に入れる
					pStmt.setInt(1, ic.getIcon_id());
				} else {									// 入力がなければ空白で入れる
					pStmt.setInt(1, 1);
				}
				pStmt.setInt(2, ic.getId());
				
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
		}*/
		
		public Icon select(int icon_id) {
			Connection conn = null;        // データベースに接続していない
			Icon ic = new Icon();	   // 入れ物icを作っておく

			try {
				// JDBCドライバを読み込む
				Class.forName("com.mysql.cj.jdbc.Driver");

				// データベースに接続する
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b4?"
						+ "characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true",
						"root", "password");

				// SQL文を準備する
				String sql = "SELECT * FROM icons WHERE id = ?";		// idの値でデータを指定
				PreparedStatement pStmt = conn.prepareStatement(sql);

				// SQL文を完成させる
				if (icon_id != 0) {							// 入力された文字で検索
					pStmt.setInt(1, icon_id);
				} else {										// nullだったらすべてあいまい検索
					pStmt.setString(1, "%");
				}
				
				// SQL文を実行し、結果表を取得する
				ResultSet rs = pStmt.executeQuery();

				// 結果表をコレクションにコピーする
				rs.next();
				ic.setId(icon_id);
				ic.setIcon_path(rs.getString("icon_path"));
				
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
			return ic;
		}
		
}


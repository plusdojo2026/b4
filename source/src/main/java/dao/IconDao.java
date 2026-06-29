package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dto.Icon;

public class IconDao {
		public Icon select(int icon_id) {
			Connection conn = null;        // データベースに接続していない
			Icon ic = new Icon();	   // 入れ物icを作っておく

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
				String sql = "SELECT icon_path FROM user_settings INNER JOIN icons ON icon_id = icons.id WHERE user_id = 1";		// idの値でデータを指定
				PreparedStatement pStmt = conn.prepareStatement(sql);

				// テスト中はコメントアウト
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


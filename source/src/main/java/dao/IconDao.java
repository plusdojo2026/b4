package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dto.Icon;

public class IconDao {
	public int insert(Icon icon) {
		Connection conn = null;
		
		try {
			// JDBCドライバを読み込む
			Class.forName("com.mysql.cj.jdbc.Driver");

			// データベースに接続する
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/b4?"
					+ "characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true",
					"root", "password");
			
			// SELECT文を準備する
			String sql = "SELECT count(*) FROM Icon WHERE id=?";
			PreparedStatement pStmt = conn.prepareStatement(sql);
			pStmt.setInt(1, icon.getId());
			pStmt.setString(2, icon.getIcon_name());
			pStmt.setString(3, icon.getIcon_path());
			
			// SELECT文を実行し、結果表を取得する
			ResultSet rs = pStmt.executeQuery();
			
	return id;	
	}
}

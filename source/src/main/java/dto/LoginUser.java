package dto;

import java.io.Serializable;

public class LoginUser implements Serializable {
	private static final long serialVersionUID = 1L;
	private int userId;		  // id
	private String userNickname; // ログイン時のニックネーム
	private String password;

	// ゲッタ・セッタ
	public String getUserNickname() {					
		return userNickname;
	}
	public void setUserNickname(String userNickname) {		
		this.userNickname = userNickname;
	}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	// デフォルトのコンストラクタ
	public LoginUser() {
		super();
		this.userId = 0;
		this.userNickname = "";
		this.password = "";
	}
	// コンストラクタ
	public LoginUser(int userId, String userNickname, String password) {
		this.userId = userId;
		this.userNickname = userNickname;
		this.password = password;
	}
}

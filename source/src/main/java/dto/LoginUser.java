package dto;

import java.io.Serializable;

public class LoginUser implements Serializable {
	private int id;		  // id
	private String name; // ログイン時のニックネーム
	private String password;

	// ゲッタ・セッタ
	public String getName() {					
		return name;
	}
	public void setUserName(String name) {		
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public void setUserId(int id) {
		this.id = id;
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
		this.id = 0;
		this.name = "";
		this.password = "";
	}
	// コンストラクタ
	public LoginUser(int id, String name, String password) {
		this.id = id;
		this.name = name;
		this.password = password;
	}
}

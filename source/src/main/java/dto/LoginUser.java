package dto;

import java.io.Serializable;

public class LoginUser implements Serializable {
	private int id;		  // id
	private String name; // ログイン時のニックネーム

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

	// デフォルトのコンストラクタ
	public LoginUser() {
		super();
		this.id = 0;
		this.name = "";
	}
	// コンストラクタ
	public LoginUser(int id, String name) {
		this.id = id;
		this.name = name;
	}
}

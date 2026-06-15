package dto;

import java.io.Serializable;

public class LoginUser implements Serializable {
	private String name; // ログイン時のニックネーム

	public String getName() {					// ニックネームのゲッタ
		return name;
	}

	public void setUserName(String name) {		// ニックネームのセッタ
		this.name = name;
	}
	// デフォルトのコンストラクタ
	public LoginUser() {
		this(null);
	}
	// コンストラクタ
	public LoginUser(String name) {
		this.name = name;
	}
}

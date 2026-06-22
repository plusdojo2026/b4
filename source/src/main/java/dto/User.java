package dto;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;					/* id */
	private String userNickname;	/* ユーザーニックネーム */
	private String password;		/* パスワード */
	private String mailAddress;	/* メールアドレス */
	private String cAt;			/* created_at */
	private String uAt;			/* update_at */
	
	// ゲッタ・セッタ
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUserNickname() {
		return userNickname;
	}
	public void setUserNickname(String userNickname) {
		this.userNickname = userNickname;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getMailAddress() {
		return mailAddress;
	}
	public void setMail_address(String mailAddress) {
		this.mailAddress = mailAddress;
	}
	
	public String getCAt() {
		return cAt;
	}
	public void setCAt(String cAt) {
		this.cAt = cAt;
	}
	
	public String getUAt() {
		return uAt;
	}
	public void setUAt(String uAt) {
		this.uAt = uAt;
	}
	// コンストラクタ
	public User(int id, String userNickname, String password, String mailAddress, String cAt, String uAt) {
		super();
		this.id = id;
		this.userNickname = userNickname;
		this.password = password;
		this.mailAddress = mailAddress;
		this.cAt = cAt;
		this.uAt = uAt;
	}
	// デフォルトのコンストラクタ
	public User() {
		super();
		this.id = 0;
		this.userNickname ="";
		this.password ="";
		this.mailAddress ="";
		this.cAt ="";
		this.uAt ="";
	}
	
}

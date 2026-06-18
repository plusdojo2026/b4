package dto;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;					/* id */
	private String user_nickname;	/* ユーザーニックネーム */
	private String password;		/* パスワード */
	private String mail_address;	/* メールアドレス */
	private String c_at;			/* created_at */
	private String u_at;			/* update_at */
	
	// ゲッタ・セッタ
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUser_nickname() {
		return user_nickname;
	}
	public void setUser_nickname(String user_nickname) {
		this.user_nickname = user_nickname;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getMail_address() {
		return mail_address;
	}
	public void setMail_address(String mail_address) {
		this.mail_address = mail_address;
	}
	
	public String getC_at() {
		return c_at;
	}
	public void setC_at(String c_at) {
		this.c_at = c_at;
	}
	
	public String getU_at() {
		return u_at;
	}
	public void setU_at(String u_at) {
		this.u_at = u_at;
	}
	// コンストラクタ
	public User(int id, String user_nickname, String password, String mail_address, String c_at, String u_at) {
		super();
		this.id = id;
		this.user_nickname = user_nickname;
		this.password = password;
		this.mail_address = mail_address;
		this.c_at = c_at;
		this.u_at = u_at;
	}
	// デフォルトのコンストラクタ
	public User() {
		super();
		this.id = 0;
		this.user_nickname ="";
		this.password ="";
		this.mail_address ="";
		this.c_at ="";
		this.u_at ="";
	}
	
}

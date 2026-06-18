package dto;

import java.io.Serializable;

public class Garbage implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;					// ID
	private String user_id;			// ユーザーID
	private String garbage_day;		// ゴミ捨て曜日
	private String garbage_name;	// ゴミ分類名
	
	// ゲッタ・セッタ
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
	public String getGarbage_day() {
		return garbage_day;
	}
	public void setGarbage_day(String garbage_day) {
		this.garbage_day = garbage_day;
	}
	
	public String getGarbage_name() {
		return garbage_name;
	}
	public void setGarbage_name(String garbage_name) {
		this.garbage_name = garbage_name;
	}
	
	// コンストラクタ
	public Garbage(int id, String user_id, String garbage_day, String garbage_name) {
		this.id = id;
		this.user_id = user_id;
		this.garbage_day = garbage_day;
		this.garbage_name = garbage_name;
	}
	// デフォルトのコンストラクタ
	public Garbage() {
		this.id = 0;
		this.user_id = "";
		this.garbage_day = "";
		this.garbage_name = "";
	}
}

package dto;

import java.io.Serializable;

public class Garbage implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;					// ID
	private int userId;			// ユーザーID
	private String garbageDay;		// ゴミ捨て曜日
	private String garbageName;	// ゴミ分類名
	
	// ゲッタ・セッタ
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public String getGarbageDay() {
		return garbageDay;
	}
	public void setGarbageDay(String garbageDay) {
		this.garbageDay = garbageDay;
	}
	
	public String getGarbageName() {
		return garbageName;
	}
	public void setGarbageName(String garbageName) {
		this.garbageName = garbageName;
	}
	
	// コンストラクタ
	public Garbage(int id, int userId, String garbageDay, String garbageName) {
		this.id = id;
		this.userId = userId;
		this.garbageDay = garbageDay;
		this.garbageName = garbageName;
	}
	public Garbage(String garbageDay, String garbageName) {
		this.garbageDay = garbageDay;
		this.garbageName = garbageName;
	}
	// デフォルトのコンストラクタ
	public Garbage() {
		this.id = 0;
		this.userId = 0;
		this.garbageDay = "";
		this.garbageName = "";
	}

}

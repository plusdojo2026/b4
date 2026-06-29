package dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * garbagesテーブルの1件分のデータを保持するDTO
 *
 * 項目：
 * ・ユーザーID
 * ・ゴミ収集曜日
 * ・ゴミ分類名
 * ・登録日時
 * ・更新日時
 */
public class Garbage implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;						// ID
	private int userId;					// ユーザーID
	private String garbageDay;			// ゴミ収集曜日
	private String garbageName;			// ゴミ分類名
	private LocalDateTime cAt;			// 登録日時
	private LocalDateTime uAt;			// 更新日時

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

	public LocalDateTime getCAt() {
		return cAt;
	}

	public void setCAt(LocalDateTime cAt) {
		this.cAt = cAt;
	}

	public LocalDateTime getUAt() {
		return uAt;
	}

	public void setUAt(LocalDateTime uAt) {
		this.uAt = uAt;
	}

	/**
	 * 全項目を指定するコンストラクタ
	 *
	 * GarbageDaoでSELECT結果をまとめて設定する場合に使用
	 */
	public Garbage(int id, int userId, String garbageDay, String garbageName,
			LocalDateTime cAt, LocalDateTime uAt) {

		this.id = id;
		this.userId = userId;
		this.garbageDay = garbageDay;
		this.garbageName = garbageName;
		this.cAt = cAt;
		this.uAt = uAt;
	}

	/**
	 * ID、ユーザーID、曜日、ゴミ分類名を指定するコンストラクタ
	 *
	 */
	public Garbage(int id, int userId, String garbageDay, String garbageName) {
		this.id = id;
		this.userId = userId;
		this.garbageDay = garbageDay;
		this.garbageName = garbageName;
	}

	/**
	 * 新規登録時に使用するコンストラクタ
	 */
	public Garbage(int userId, String garbageDay, String garbageName) {
		this.userId = userId;
		this.garbageDay = garbageDay;
		this.garbageName = garbageName;
	}

	/**
	 * 曜日とゴミ分類名だけを指定するコンストラクタ
	 *
	 * 画面表示用など、ユーザーIDが不要な場合に使用
	 */
	public Garbage(String garbageDay, String garbageName) {
		this.garbageDay = garbageDay;
		this.garbageName = garbageName;
	}

	/**
	 * デフォルトコンストラクタ。
	 *
	 * DAOでインスタンスを作成した後、
	 * setterで各項目を設定する場合に使用する。
	 */
	public Garbage() {
		this.id = 0;
		this.userId = 0;
		this.garbageDay = "";
		this.garbageName = "";
		this.cAt = null;
		this.uAt = null;
	}
}
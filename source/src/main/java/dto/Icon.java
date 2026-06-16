package dto;

import java.io.Serializable;

public class Icon implements Serializable {
	private int id;				/* id */
	private String icon_name;	/* アイコン名 */
	private String icon_path;	/* 画像パス */
	private String c_at;		/* created_at */
	private String u_at;		/* update_at */
	
	// ゲッタ・セッタ
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getIcon_name() {
		return icon_name;
	}
	public void setIcon_name(String icon_name) {
		this.icon_name = icon_name;
	}
	
	public String getIcon_path() {
		return icon_path;
	}
	public void setIcon_path(String icon_path) {
		this.icon_path = icon_path;
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
	public Icon(int id, String icon_name, String icon_path, String c_at, String u_at) {
		this.id = id;
		this.icon_name = icon_name;
		this.icon_path = icon_path;
		this.c_at = c_at;
		this.u_at = u_at;
	}
	
	// デフォルトのコンストラクタ
	public Icon() {
		this.id = 0;
		this.icon_name = "";
		this.icon_path = "";
		this.c_at = "";
		this.u_at = "";
	}
}

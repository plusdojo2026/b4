package dto;

import java.io.Serializable;

public class Activity implements Serializable {
	
	private int id; //id
	private String category; //カテゴリー
	private String activity_name; //活動名
	private int required_time; //所要時間
	private int base_point; //基本ポイント
	private String c_at; //作成日
	private String u_at; //更新日
	
	//getter/setter
	//id
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	//カテゴリー
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	//活動名
	public String getActivity_name() {
		return activity_name;
	}
	public void setActivity_name(String activity_name) {
		this.activity_name = activity_name;
	}
	
	//所要時間
	public int getRequired_time() {
		return required_time;
	}
	public void setRequired_time(int required_time) {
		this.required_time = required_time;
	}
	
	//基本ポイント
	public int getBase_point() {
		return base_point;
	}
	public void setBase_point(int base_point) {
		this.base_point = base_point;
	}
	
	//作成日
	public String getC_at() {
		return c_at;
	}
	public void setC_at(String c_at) {
		this.c_at = c_at;
	}
	
	//更新日
	public String getU_at() {
		return u_at;
	}
	public void setU_at(String u_at) {
		this.u_at = u_at;
	}
	
	
	//コンストラクタ
	public Activity(int id, String category, String activity_name, int required_time, int base_point, String c_at,
			String u_at) {
		super();
		this.id = id;
		this.category = category;
		this.activity_name = activity_name;
		this.required_time = required_time;
		this.base_point = base_point;
		this.c_at = c_at;
		this.u_at = u_at;
	}
	
	//デフォルトコンストラクタ
	public Activity() {
		super();
		this.id = 0;
		this.category = "";
		this.activity_name = "";
		this.required_time = 0;
		this.base_point = 0;
		this.c_at = "";
		this.u_at = "";
	}

}

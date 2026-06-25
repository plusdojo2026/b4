package dto;

import java.io.Serializable;

public class Activity implements Serializable {
	
	private int id; //id
	private String category; //カテゴリー
	private String activityName; //活動名
	private int requiredTime; //所要時間
	private int basePoint; //基本ポイント
	private boolean isCanWithChild; //子供とできるか判定
	private boolean isNoise; //騒音か(掃除機など)
	private boolean hasGarbage; //ゴミの有無
	private String flowGroup; //家事の流れ
	private Integer flowStep; //家事の順番
	private String garbageActionType; //なんのゴミか
	private int waitMinutes; //家事待ち時間
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
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	
	//所要時間
	public int getRequiredTime() {
		return requiredTime;
	}
	public void setRequiredTime(int requiredTime) {
		this.requiredTime = requiredTime;
	}
	
	//基本ポイント
	public int getBasePoint() {
		return basePoint;
	}
	public void setBasePoint(int basePoint) {
		this.basePoint = basePoint;
	}
	
	//子供とできるか判定
	public boolean getIsCanWithChild() {
		return isCanWithChild;
	}
	public void setIsCanWithChild(boolean isCanWithChild) {
		this.isCanWithChild = isCanWithChild;
	}
	
	//騒音か
	public boolean getIsNoise() {
		return isNoise;
	}
	public void setIsNoise(boolean isNoise) {
		this.isNoise = isNoise;
	}
	
	//ゴミの有無
	public boolean getHasGarbage() {
		return hasGarbage;
	}
	public void setHasGarbage(boolean hasGarbage) {
		this.hasGarbage = hasGarbage;
	}
	
	//家事の流れ
	public String getFlowGroup() {
		return flowGroup;
	}
	public void setFlowGroup(String flowGroup) {
		this.flowGroup = flowGroup;
	}
	
	//家事の順番
	public Integer getFlowStep() {
		return flowStep;
	}
	public void setFlowStep(Integer flowStep) {
		this.flowStep = flowStep;
	}
	
	//なんのゴミか
	public String getGarbageActionType() {
		return garbageActionType;
	}
	public void setGarbageActionType(String garbageActionType) {
		this.garbageActionType = garbageActionType;
	}
	
	//家事の待ち時間
	public int getWaitMinutes() {
		return waitMinutes;
	}
	public void setWaitMinutes(int waitMinutes) {
		this.waitMinutes = waitMinutes;
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
	public Activity(
			int id, 
			String category, 
			String activityName, 
			int requiredTime, 
			int basePoint, 
			boolean isCanWithChild, 
			boolean isNoise, 
			boolean hasGarbage, 
			String flowGroup,
			Integer flowStep, 
			String garbageActionType, 
			int waitMinutes, 
			String c_at, 
			String u_at) {
		
		super();
		this.id = id;
		this.category = category;
		this.activityName = activityName;
		this.requiredTime = requiredTime;
		this.basePoint = basePoint;
		this.isCanWithChild = isCanWithChild;
		this.isNoise = isNoise;
		this.hasGarbage = hasGarbage;
		this.flowGroup = flowGroup;
		this.flowStep = flowStep;
		this.garbageActionType = garbageActionType;
		this.waitMinutes = waitMinutes;
		this.c_at = c_at;
		this.u_at = u_at;
	}
	
	//デフォルトコンストラクタ
	public Activity() {
		super();
		this.id = 0;
		this.category = "";
		this.activityName = "";
		this.requiredTime = 0;
		this.basePoint = 0;
		this.isCanWithChild = false;
		this.isNoise = false;
		this.hasGarbage = false;
		this.flowGroup = "";
		this.flowStep = 0;
		this.garbageActionType = "";
		this.waitMinutes = 0;
		this.c_at = "";
		this.u_at = "";
	}

}

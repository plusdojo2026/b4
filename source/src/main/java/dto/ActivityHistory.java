package dto;

import java.time.LocalDateTime;

public class ActivityHistory{
	
	private int activityHistoryId; //活動履歴ID
	private int userId; //ユーザーID
	private int activityId; //活動ID
	private LocalDateTime createdAt; //作成日
	private LocalDateTime updatedAt; //更新日
	
	public ActivityHistory() {	
	}
	
	public ActivityHistory(
			int activityHistoryId,
			int userId,
			int activityId,
			LocalDateTime createdAt,
			LocalDateTime updatedAt
			) {
		this.activityHistoryId = activityHistoryId;
		this.userId = userId;
		this.activityId = activityId;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	//活動履歴IDのgetter
	public int getActivityHistoryId() {
		return activityHistoryId;
	}
	//活動履歴IDのsetter
	public void setActivityHistoryId(int activityHistoryId) {
		this.activityHistoryId = activityHistoryId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getActivityId() {
		return activityId;
	}
	
	public void setActivityId(int activityId) {
		this.activityId = activityId ;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
}
package dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RecordHistoryDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id; //
	private int activityId; //activities のID
	private String activityName; //活動名
	private String category; //活動カテゴリ (housework, child, rest)
	private LocalDateTime createdAt; //やった日時

	//デフォルトコンストラクタ
	public RecordHistoryDto() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	} 

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
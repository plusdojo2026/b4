package dto;

import java.io.Serializable;

public class Todo implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;					// ID
	private int userId;			// ユーザーID
	private String todoName;		// TODO名
	private String todoDate;	// TODO期日
	
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
	
	public String getTodoName() {
		return todoName;
	}
	public void setTodoName(String todoName) {
		this.todoName = todoName;
	}
	
	public String getTodoDate() {
		return todoDate;
	}
	public void setTodoDate(String todoDate) {
		this.todoDate = todoDate;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	// コンストラクタ
	public Todo(int id, int userId, String todoName, String todoDate) {
		this.id = id;
		this.userId = userId;
		this.todoName = todoName;
		this.todoDate = todoDate;
	}
	
	public Todo(int userId, String todoName, String todoDate) {
		this.userId = userId;
		this.todoName = todoName;
		this.todoDate = todoDate;
	}
	
	
	// デフォルトのコンストラクタ
	public Todo() {
		this.id = 0;
		this.userId = 0;
		this.todoName = "";
		this.todoDate = "";
	}
}

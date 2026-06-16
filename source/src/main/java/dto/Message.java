package dto;

import java.io.Serializable;

public class Message implements Serializable{
	private String sender;  	
	private String text;
	
    //コンストラクタ
	public Message(String sender, String text) {
		super();
		this.sender = sender;
		this.text = text;
	}
	
    //ゲッタとセッタ
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}

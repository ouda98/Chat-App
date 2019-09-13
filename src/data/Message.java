package data;

import java.io.Serializable;

public class Message implements Serializable{

	private static final long serialVersionUID = 1L;
	private String  from;
	private String to;
	private String content;
	private int ttl;
	private int type;
	
	/*type:
	 * 0 -> fetch message
	 * 1 -> send message
	 * 2 -> getMemberList
	 * 3 -> join
	 * 4 -> empty
	 * 5 -> getMemberList from other Server
	 */
	
	public Message(int type) {
		this.from = "";
		this.to = "";
		this.content = "";
		this.ttl = 2;
		this.type = type; 
	}
	
	public Message(String from, String to, int type) {
		this.from = from;
		this.to = to;
		this.content = "";
		this.ttl = 2;
		this.type = type; 
	}
	
	public Message(String content, int type) {
		this.from = "";
		this.to = "";
		this.content = content;
		this.ttl = 2;
		this.type = type; 
	}
	
	public Message(String from, String to, String content, int ttl, int type) {
		this.from = from;
		this.to = to;
		this.content = content;
		this.ttl = ttl;
		this.type = type;
	}

	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getTo() {
		return to;
	}
	
	public void setTo(String to) {
		this.to = to;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getTtl() {
		return ttl;
	}
	
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "Message [from=" + from + ", to=" + to + ", content=" + content + ", ttl=" + ttl + ", type=" + type + "]";
	}
	
}

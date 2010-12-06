package gov.nysenate.billbuzz.model;


public class Comment extends DisqusObject {

//	private String ip_address;
//	private String is_anonymous;
	private String status;
	private String has_been_moderated;
	private String message;
	private String created_at;
	private String username;
	private String display_name;
	private String email;
	private String id;
	private String threadInfo;
	
	public Comment() {
		super.id = id;
	}
	public String getMessage() {
		return message;
	}
	public String getCreatedAt() {
		return created_at;
	}
	public String getUsername() {
		return username;
	}
	public String getDisplayName() {
		return display_name;
	}
	public String getEmail() {
		return email;
	}
	public String getStatus() {
		return status;
	}
	public String getHasBeenModerated() {
		return has_been_moderated;
	}
	public String getThreadInfo() {
		return threadInfo;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setDisplayName(String display_name) {
		this.display_name = display_name;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public void setId(String id) {
		this.id = id;
	}
	public void setThreadInfo(String threadInfo) {
		this.threadInfo = threadInfo;
	}
}
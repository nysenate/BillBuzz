package gov.nysenate.billbuzz.model;

import gov.nysenate.billbuzz.disqus.models.BaseObject;

import java.util.Date;



public class Comment extends BaseObject {

//	private String ip_address;
//	private String is_anonymous;
	private String status;
	private String has_been_moderated;
	private String message;
	private Date created_at;
	private String username;
	private String display_name;
	private String email;
	private String threadInfo;

	public String getMessage() {
		return message;
	}
	public Date getCreatedAt() {
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

	public void setThreadInfo(String threadInfo) {
		this.threadInfo = threadInfo;
	}
}
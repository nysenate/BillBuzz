package gov.nysenate.billbuzz.model.persist;

import gov.nysenate.billbuzz.ObjectHelper;
import gov.nysenate.billbuzz.generated.articles.Comment;

import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class DisqusComment {

	private String email;

	@Column(jdbcType="LONGVARCHAR")
	private String message;

	private String name;

	private String url;

	private String ip;

	private Date date;

	private int points;

	public DisqusComment() {

	}

	public DisqusComment(Comment c) {
		ip = c.getIpAddress();

		date = ObjectHelper.dateFromString(c.getDate());

		points = c.getPoints();

		email = c.getEmail();

		message = c.getMessage();

		name = c.getName();

		url = c.getUrl();
	}

	public String getEmail() {
		return email;
	}
	public String getMessage() {
		return message;
	}
	public String getName() {
		return name;
	}
	public String getUrl() {
		return url;
	}
	public String getIp() {
		return ip;
	}
	public Date getDate() {
		return date;
	}
	public int getPoints() {
		return points;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public void setPoints(int points) {
		this.points = points;
	}
}

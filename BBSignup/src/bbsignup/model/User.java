package bbsignup.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.*;

@PersistenceCapable
public class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Persistent
	@Column(name="first_name")
	private String firstName;
	
	@Persistent
	@Column(name="last_name")
	private String lastName;
	
	@Persistent
	@PrimaryKey
	@Column(name="email")
	private String email;
	
	@Persistent
	@Column(name="auth")
	private String auth;
	
	@Persistent
	@Column(name="other_data")
	private boolean otherData;
	
	
	@Persistent(defaultFetchGroup="true")
	@Column(name="subs")
	private List<String> subscriptions;
	
	public User() {
		subscriptions = new ArrayList<String>();
	}
	
	public User(String firstName, String lastName, String email, String auth, boolean otherData) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.auth = auth;
		subscriptions = new ArrayList<String>();
		this.otherData = otherData;
	}
	
	
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getEmail() {
		return email;
	}
	public String getAuth() {
		return auth;
	}
	public List<String> getSubscriptions() {
		return subscriptions;
	}
	public boolean getOtherData() {
		return otherData;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public void setSubscriptions(List<String> subscriptions) {
		this.subscriptions = subscriptions;
	}
	public void setOtherData(boolean otherData) {
		this.otherData = otherData;
	}
	
	public void addSubscription(String s) {
		subscriptions.add(s);
	}
}

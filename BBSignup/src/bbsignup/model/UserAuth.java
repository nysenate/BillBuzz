package bbsignup.model;

import java.io.IOException;
import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import org.jasypt.util.text.BasicTextEncryptor;

import bbsignup.src.Controller;

@PersistenceCapable
public class UserAuth {
	@PrimaryKey
	String dateTime;
	String email;
	String hash;
	
	public static void main(String[] args) throws IOException {	
		String str = "";
		Controller c = new Controller();
		UserAuth ua = c.getUserAuth("williams@nysenate.gov");
		
		System.out.println(ua.getHash());
		System.out.println(ua.isHashCorrect(str));
	}
	
	public UserAuth() {
		
	}
	
	public UserAuth(String email) {
		this.email = email;
		this.dateTime = Long.toString(new Date().getTime());
		setHash();
	}

	public String getDateTime() {
		return dateTime;
	}

	public String getEmail() {
		return email;
	}
	
	public String getHash() {
		return hash;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setHash() {
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(dateTime + email);
		this.hash = textEncryptor.encrypt(
				dateTime + email).replaceAll("=|&|\\?|\\+|/|\\p{Cntrl}","");
	}
	
	public boolean isHashCorrect(String hash) {
		if(this.hash.equals(hash)) {
			return true;
		}
		return false;
		/*BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(dateTime + email);
		if(textEncryptor.decrypt(hash).equals(dateTime+email)) {
			return true;
		}
		return false;*/
		/*BasicPasswordEncryptor pe = new BasicPasswordEncryptor();
		if(hash.equals(pe.encryptPassword(dateTime + email))) {
			return true;
		}
		return false;*/
	}
	
	
}

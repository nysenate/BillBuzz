package gov.nysenate.billbuzz.model;

import gov.nysenate.billbuzz.src.Controller;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import org.jasypt.util.text.BasicTextEncryptor;

@PersistenceCapable
public class UserAuth {
	@PrimaryKey
	String dateTime;
	String email;
	String hash;

	public static void main(String[] args) {
		System.out.println(new Controller().getUser("williams@nysenate.gov").getOtherData());
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

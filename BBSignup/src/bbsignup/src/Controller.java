package bbsignup.src;

import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import bbsignup.model.*;

public class Controller {
	private static final String SMTP_HOST_NAME = Resource.get("hostname");

	private static final String SMTP_PORT = Resource.get("port");
	
	private static final String SMTP_ACCOUNT_USER = Resource.get("user");
	private static final String SMTP_ACCOUNT_PASS = Resource.get("pass");
	
	public String WEBLINK = "http://billbuzz.nysenate.gov/";
	
	public Controller() {
	}
	
	@SuppressWarnings("unchecked")
	public List<Senator> getSenators() {
		return (List<Senator>) PMF.getDetachedObjects(Senator.class);
		
	}
	
	public String tryDelete(String email) {
		
		User u = (User) PMF.getDetachedObject(User.class, "email", email);
		
		if(u != null) {
			UserAuth ua = this.getUserAuth(email);
			if(ua == null) {
				ua = new UserAuth(email);
			}
			
			String message = "Hello!<br/><br/>To remove yourself from BillBuzz please click the following link:";
			
			message += "<br/><br/>" + WEBLINK + "delete.jsp?email=" + email + "&key=" + ua.getHash();
			
			message += "<br/><br/>If you have any questions please contact us at <a href=\"mailto:billbuzz@nysenate.gov\">billbuzz@nysenate.gov</a> or reply to this email.";
			
			try {
				sendMail(email,"Finalize your BillBuzz cancellation", message, "billbuzz@nysenate.gov", "BillBuzz");
				PMF.persistObject(ua);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
			
		}
		return "errno4";
	}
	
	public boolean deleteUser(String email, String key) {
		UserAuth ua = getUserAuth(email);
		
		if(ua.isHashCorrect(key)) {	
			PMF.deleteObjects(new Class[] {User.class,UserAuth.class}, 
					new String[] {"email","email"}, 
					new String[] {email,email});

			return true;	
		}
		return false;
	}
	
	public User getUser(String email) {
		return (User) PMF.getDetachedObject(User.class, "email", email);
	}
	
	public UserAuth getUserAuth(String email) {
		return (UserAuth) PMF.getDetachedObject(UserAuth.class, "email", email);
	}
	
	
	public void newUserEmail(String email, String hash) {
		String message = "Hello!<br/><br/>Thanks for signing up for BillBuzz, to finalize your subscription please click the following link:";
		
		message += "<br/><br/>" + WEBLINK + "authenticate.jsp?email=" + email + "&key=" + hash;
		
		message += "<br/><br/>If you have any questions please contact us at <a href=\"mailto:billbuzz@nysenate.gov\">billbuzz@nysenate.gov</a> or reply to this email.";
		
		try {
			sendMail(email,"Finalize your BillBuzz subscription", message, "billbuzz@nysenate.gov", "BillBuzz");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public String updateEmail(String email) {
		
		User u = getUser(email);
		
		if(u == null) {
			return "errno5";
		}
		
		UserAuth ua = getUserAuth(email);
		if(ua == null) {
			ua = new UserAuth(email);	
		}
		
		String message = "Hello!<br/><br/>It looks like you're trying to update your subscription, to do so please click the link below and follow the instructions:";
		
		message += "<br/><br/>" + WEBLINK + "update.jsp?uemail=" + email + "&key=" + ua.getHash();
		
		message += "<br/><br/>If you have any questions please contact us at <a href=\"mailto:billbuzz@nysenate.gov\">billbuzz@nysenate.gov</a> or reply to this email.";
		
		try {
			sendMail(email,"Update your BillBuzz subscription", message, "billbuzz@nysenate.gov", "BillBuzz");
			PMF.deleteObjectById(UserAuth.class, "email", email);
			PMF.persistObject(ua);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean authenticateUser(String email, String key){
		UserAuth ua = getUserAuth(email);
				
		if(ua != null && ua.isHashCorrect(key)) {
			
			PersistenceManager pm = PMF.getPersistenceManager();
			Transaction tx = pm.currentTransaction();
			
			User u = null;
			
			try {
				tx.begin();
				u = (User)PMF.getObject(pm, User.class, "email", email);
				
				if(u != null) {					
					u.setAuth("y");
				}
				tx.commit();
			}
			finally {
				if(tx.isActive()) {
					tx.rollback();
				}
				pm.close();
				
				PMF.deleteObjectById(UserAuth.class, "email", email);
			}
			
			if(u != null) {
				return true;
			}			
		}
		return false;
	}

	public void sendMail(String to, String subject, String message, String from, String fromDisplay) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.starttls.enable","false");
		props.put("mail.smtp.socketFactory.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.put("mail.smtp.ssl.enable","false");

		Session session = Session.getDefaultInstance(props,	new javax.mail.Authenticator() {
										protected PasswordAuthentication getPasswordAuthentication() {
											return new PasswordAuthentication(SMTP_ACCOUNT_USER, SMTP_ACCOUNT_PASS);}});
		session.setDebug(false);
		Message msg = new MimeMessage(session);
		InternetAddress addressFrom = new InternetAddress(from);
		addressFrom.setPersonal(fromDisplay);
		msg.setFrom(addressFrom);
	
		
		StringTokenizer st = new StringTokenizer (to,",");
		
		InternetAddress[] rcps = new InternetAddress[st.countTokens()];
		int idx = 0;
		
		while (st.hasMoreTokens())
		{
			InternetAddress addressTo = new InternetAddress(st.nextToken());
			rcps[idx++] = addressTo;
			
		}
		
		msg.setRecipients(Message.RecipientType.TO,rcps);
		
		msg.setSubject(subject);
		msg.setContent(message, "text/html");
		Transport.send(msg);
	}	
	
}

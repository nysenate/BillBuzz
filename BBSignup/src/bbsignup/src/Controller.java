package bbsignup.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import bbsignup.model.Senator;
import bbsignup.model.User;

public class Controller {
	private static final String SMTP_HOST_NAME = Resource.get("hostname");

	private static final String SMTP_PORT = Resource.get("port");
	
	private static final String SMTP_ACCOUNT_USER = Resource.get("user");
	private static final String SMTP_ACCOUNT_PASS = Resource.get("pass");

	
	public static final String SEN_FILE = "senators";
	
	public String WEBLINK = "http://billbuzz.nysenate.gov/";
	
	public static void main(String[] args) throws Exception {
		Controller c =  new Controller();
				
		
//		c.persistSen();
		
		
	}
	
	
	
	public Controller() throws IOException {
//		Properties props = new Properties();
//		FileInputStream fis = new FileInputStream("bbs.props");
//		
//		props.load(fis);
//		fis.close();
//		
//		WEBLINK = props.getProperty("billbuzzsignup.weblink");
//		
//		System.out.println(props.getProperty("billbuzzsignup.weblink"));
	}
	
	public List<Senator> getSenators() {
		return (List<Senator>) PMF.getDetachedObjects(Senator.class);
		
	}
	
	
	
	public void persistSen() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(new File(SEN_FILE)));
		
		
		String in = null;
		
		while((in = br.readLine()) != null) {
						
			String name = in.split("#")[0];
			String party = in.split("#")[2];
			String url = in.split("#")[3];
			
			PMF.persistObject(new Senator(name, url, party));
			
		}
		
		br.close();
		
	}
	
	public String tryDelete(String email) {
		
		User u = (User) PMF.getDetachedObject(User.class, "email", email);
		
		if(u != null) {
			
			String message = "Hello!<br/><br/>To remove yourself from BillBuzz please click the following link:";
			
			message += "<br/><br/>" + WEBLINK + "delete.jsp?email=" + email + "&key=" + keyFromEmail(email);
			
			message += "<br/><br/>If you have any questions please contact us at <a href=\"mailto:billbuzz@nysenate.gov\">billbuzz@nysenate.gov</a> or reply to this email.";
			
			try {
				sendMail(email,"Finalize your BillBuzz cancellation", message, "billbuzz@nysenate.gov", "BillBuzz");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
			
		}
		return "errno4";
	}
	
	public boolean deleteUser(String email, String key) {
		
		
		if(keyFromEmail(email).equals(key)) {
			PersistenceManager pm = PMF.getPersistenceManager();
			Transaction tx = pm.currentTransaction();
			
			User u = null;
			
			try {
				tx.begin();
				
				u = (User)PMF.getObject(pm, User.class, "email", email);
				
				pm.deletePersistent(u);
				
				tx.commit();
			}
			finally {
				if(tx.isActive()) {
					tx.rollback();
				}
				pm.close();
			}
			
			if(u == null) {
				return false;
			}
			
			return true;	
		}
		
		return false;
		
	}
	
	public List<String> getSubFromArrays(String[] r, String[] d, List<Senator> senators) {
		List<String> subs = new ArrayList<String>();

		
		if(((r != null) ? r.length : 0) + ((d != null) ? d.length : 0) == senators.size()) {
			subs.add("all");
		}
		else {
			List<String> tr = new ArrayList<String>();
			List<String> td = new ArrayList<String>();
			tr = combineArray(tr,r);
			td = combineArray(td,d);
						
			if(tr.size() == countParty(senators,"r")) {
				tr.clear();
				tr.add("rep");
			}
			
			if(td.size() == countParty(senators,"d")) {
				td.clear();
				td.add("dem");
			}
			
			subs.addAll(tr);
			subs.addAll(td);
			
		}
		return subs;
		
	}
	
	
	public User getUser(String first, String last, String e, String[] r, String[] d, List<Senator> senators, String auth) {
		List<String> subs = getSubFromArrays(r,d,senators);
		
		User u = new User(first,last,e, auth);
		u.setSubscriptions(subs);
		
		return u;
	}
	
	
	public User validUpdate(String email, String key) {
		if(keyFromEmail(email).equals(key)) {
			return getUser(email);
		}
		return null;
	}
	

	
	public User getUser(String email) {
		return (User) PMF.getDetachedObject(User.class, "email", email);
	}
	
	
	public String getErrors(String first, String last, String e1, String e2, String[] r, String[] d, List<Senator> senators, boolean update) {
		String ret = "";
		
		if(update != true) {
			User u2 = getUser(e1);
			
			if(u2 != null) {
				return "errno2";
			}
		}		
		
		if(first == null || first.equals("")) {
			ret += "<LI>First name must be included<br/>";
		}
		if(update != true) {
			if(e1 == null || e2 == null || !e1.equals(e2) || e1.equals("")) {
				ret += "<LI>Email addresses do not match<br/>";
			}
		}		
		try {
			String emailTo = e1.split("@")[1];
			if(!emailTo.matches("[\\.]*(nysenate.gov|senate.state.ny.us)$")) {
				return "errno3";
			}
		}
		catch (Exception e) {
			ret +="<LI>Invalid email address<br/>";
		}
		if((r == null && d== null)) {
			ret += "<LI>No senator selection has been made</br>";
		}
		
		return ret;
	}
	
	
	
	public String handle(String first, String last, String e1, String e2, String[] r, String[] d, List<Senator> senators, boolean update) {
		
		String ret = getErrors(first,last,e1,e2,r,d,senators,update);
		
		if(!ret.equals("")) {
			return ret;
		}
				
		//add new unauthorized user
		
		if(update == false) {
			PMF.persistObject(getUser(first,last,e1,r,d,senators,"n"));
		}
		else {
			
			if(deleteUser(e2, keyFromEmail(e2))) {
				PMF.persistObject(getUser(first,last,e1,r,d,senators,"y"));	
			}			
			else {
				ret="updatefail";
			}
		}
				
		return "";
	}
	
	public void newUserEmail(String email) {
		String message = "Hello!<br/><br/>Thanks for signing up for BillBuzz, to finalize your subscription please click the following link:";
		
		message += "<br/><br/>" + WEBLINK + "authenticate.jsp?email=" + email + "&key=" + keyFromEmail(email);
		
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
		
		String message = "Hello!<br/><br/>It looks like you're trying to update your subscription, to do so please click the link below and follow the instructions:";
		
		message += "<br/><br/>" + WEBLINK + "update.jsp?uemail=" + email + "&key=" + keyFromEmail(email);
		
		message += "<br/><br/>If you have any questions please contact us at <a href=\"mailto:billbuzz@nysenate.gov\">billbuzz@nysenate.gov</a> or reply to this email.";
		
		try {
			sendMail(email,"Update your BillBuzz subscription", message, "billbuzz@nysenate.gov", "BillBuzz");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public List<String> combineArray(List<String> lst, String[] os) {
		
		if(os == null) {
			return lst;
		}
		
		for(String o:os) {
			lst.add(o);
		}
		
		return lst;
		
	}
	
	public int countParty(List<Senator> senators, String party) {
		int count = 0;
		
		for(Senator s:senators) {
			if(s.getParty().equals(party)) {
				count++;
			}
			
		}
		
		return count;
		
	}
	
	public boolean authenticateUser(String email, String key){
		
		if(keyFromEmail(email).equals(key)) {
			
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
			}

			
			if(u != null) {
				return true;
			}			
			
		}
	
		
		return false;
		
	}
	
	public byte[] shiftByte(int shift, byte[] data, boolean dir) {				
		
		int len = data.length;
		
		shift = shift%len;
		
		
		if(dir == true) {
			
			shift = len - shift;
			
		}
		
		byte[] nd = new byte[len];
		
		for(int i = shift; i < len; i++) {
			nd[i-shift] = data[i];
		}
		
		for(int i = 0; i < shift; i++) {
			nd[len-(shift-i)] = data[i];
		}		
		
		return nd;
	}
	
	

	
	
	public String keyFromEmail(String email) {
		byte[] data = base64ToByte(email);
		
		data = shiftByte(email.length(),data,false);
		
		String ret = "";
		
		for(Byte b:data) {
			ret+=b;
		}
		
		return ret;
	}
	
	
		
	public byte[] base64ToByte(String data) {
		BASE64Decoder dec = new BASE64Decoder();
		try {
			return dec.decodeBuffer(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String byteToBase64(byte[] data) {
		BASE64Encoder enc = new BASE64Encoder();
		return enc.encode(data);
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

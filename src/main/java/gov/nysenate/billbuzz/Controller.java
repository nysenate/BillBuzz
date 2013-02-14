package gov.nysenate.billbuzz;

import gov.nysenate.billbuzz.model.persist.LastUse;
import gov.nysenate.billbuzz.model.persist.Senator;
import gov.nysenate.billbuzz.model.persist.Unmoderated;
import gov.nysenate.billbuzz.model.persist.User;
import gov.nysenate.billbuzz.model.persist.UserAuth;
import gov.nysenate.billbuzz.service.PMF;
import gov.nysenate.billbuzz.util.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
@SuppressWarnings("unchecked")
public class Controller {

    public String WEBLINK = "http://billbuzz.nysenate.gov/";

	public static void saveUnmoderated(List<String> lst)
	{
		PMF.saveUnmoderated(lst);
	}

	public static void setLastUse(String lastUse)
	{
		PMF.setLastUse(lastUse);
	}

	public static List<Senator> getSenators()
	{
		return (List<Senator>) PMF.getDetachedObjects(Senator.class);
	}

	public static List<User> getUsers()
	{
		return (List<User>) PMF.getDetachedObjects(User.class);
	}

	public static String formatDate(int i) {

		return ((i<10) ? "0" + i : "" + i);

	}

	public static String getLastUse() {

		String ret = null;

		List<LastUse> lst = (List<LastUse>) PMF.getDetachedObjects(LastUse.class);

		Calendar c = Calendar.getInstance();

		String date = (c.get(Calendar.YEAR) + "-"
				+ formatDate(c.get(Calendar.MONTH)+1) + "-"
				+ formatDate(c.get(Calendar.DAY_OF_MONTH)) + "T"
				+ formatDate(c.get(Calendar.HOUR_OF_DAY)) + ":"
				+ formatDate(c.get(Calendar.MINUTE)));

		if(lst.size() == 0) {

			ret = date;
		}
		else {

			LastUse lu = lst.iterator().next();

			ret = lu.getLastUse();
		}



		setLastUse(date);

		return ret;
	}


	public static List<String> getUnmoderated() {
		Unmoderated u = null;

		List<Unmoderated> lst = (List<Unmoderated>)PMF.getDetachedObjects(Unmoderated.class);

		if(lst.size() == 0) {
			u = new Unmoderated();
		}
		else {
			u = lst.iterator().next();
		}

		return u.getUnmoderated();
	}

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String in = "";

        System.out.print("> ");
        while(!(in = br.readLine()).equals("exit")) {

            if(in.startsWith("add user")) {
                Pattern p = Pattern.compile("add user (.+?) (.+?) (.+?) (true|false)$");
                Matcher m = p.matcher(in);
                if(m.find()) {
                    User u = new User(m.group(1), m.group(2), m.group(3),
                            "y",m.group(4).equals("true")?true:false);
                    u.addSubscription("all");
                    PMF.persistObject(u);
                    System.out.println("User " + m.group(3) + " added succesfully");
                }
                else {
                    System.out.println("proper format is: add user <nfame> <lfame> <email>" +
                            "<otherdata(true or false)>");
                }
            }
            else if(in.startsWith("delete user")) {
                Pattern p = Pattern.compile("delete user (.+)$");
                Matcher m = p.matcher(in);
                if(m.find()) {
                    PMF.deleteObjectById(User.class, "email", m.group(1));
                    System.out.println("User " + m.group(1) + " deleted");
                }
                else {
                    System.out.println("proper format is: add user <nfame> <lfame> <email>");
                }
            }
            else if(in.startsWith("set other")) {
                PersistenceManager pm = PMF.getPersistenceManager();
                Transaction tx = pm.currentTransaction();

                try {
                    tx.begin();

                    List<User> users = (List<User>)PMF.getObjects(pm, User.class);
                    for(User user:users) {
                        user.setOtherData(true);
                    }

                    tx.commit();
                }
                finally {
                    if(tx.isActive()){
                        tx.rollback();
                    }
                    pm.close();
                }
            }
            System.out.print("> ");
        }
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

        if(ua != null && ua.getHash() != null && ua.isHashCorrect(key)) {
            gov.nysenate.billbuzz.service.PMF.deleteObjects(new Class[] {User.class,UserAuth.class},
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
        String message = "Hello!<br/><br/>Thanks for signing up for BillBuzz.  To finalize your subscription please click the following link:";

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
        props.put("mail.smtp.host", Config.getValue("hostname"));
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.port", Config.getValue("port"));
        props.put("mail.smtp.starttls.enable","false");
        props.put("mail.smtp.socketFactory.port", Config.getValue("port"));
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.ssl.enable","false");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                                        protected PasswordAuthentication getPasswordAuthentication() {
                                            return new PasswordAuthentication(Config.getValue("user"), Config.getValue("pass"));}});
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

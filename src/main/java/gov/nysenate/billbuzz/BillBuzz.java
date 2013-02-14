package gov.nysenate.billbuzz;

import gov.nysenate.billbuzz.model.BBSenator;
import gov.nysenate.billbuzz.model.BillInfo;
import gov.nysenate.billbuzz.model.Comment;
import gov.nysenate.billbuzz.model.ThreadDescription;
import gov.nysenate.billbuzz.model.persist.User;
import gov.nysenate.billbuzz.util.Config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class BillBuzz {

	private static Logger logger = Logger.getLogger(BillBuzz.class);
	private static String _billRegExp = "[BJRbjr][\\d]*[a-zA-Z]*\\-\\d{4}";
	private static final String SMTP_HOST_NAME = "webmail.senate.state.ny.us";

	private static final String SMTP_PORT = "587";

	private static final String SMTP_ACCOUNT_USER = Config.getValue("user");
	private static final String SMTP_ACCOUNT_PASS = Config.getValue("pass");


	Map<String,gov.nysenate.billbuzz.model.persist.Senator> _sep;

	//bills without a senator sponsor
	List<ThreadDescription> _otherThreads;

	/**
	 * Builds the Senator list then iterates through each senator sending out emails as it goes along
	 * @param date "YYYY-MM-DDTXX:XX" only  posts after this date will be found
	 * @throws Exception
	 * @throws Exception if a file cannot be opened or there is a network issue
	 */
	public BillBuzz(String date) throws Exception{
		runBillBuzz(date);
	}
	public BillBuzz() throws Exception{
		runBillBuzz(Controller.getLastUse());
	}

	public void runBillBuzz(String date) throws Exception {
		logger.info("running billbuzz for date:" + date.toString());
		_otherThreads = new ArrayList<ThreadDescription>();
		_sep = senatorMap();

		List<BBSenator> senators = getSenatorUpdates(date);
		Map<String,BBSenator> m = mapping(senators);
		sendToSubscribers(m);
	}


	public void sendToSubscribers(Map<String,BBSenator> m) throws Exception {
		List<User> users = Controller.getUsers();

		for(User u:users) {
			logger.info("compiling message for user:" + u.getEmail());

			String name = u.getFirstName();
			String email = u.getEmail();

			List<String> subs = u.getSubscriptions();

			List<BBSenator> senators = new ArrayList<BBSenator>();

			Collection<BBSenator> col = m.values();
			if(subs.contains("all")) {
				senators.addAll(col);
			}
			else {
				for(String s:subs) {
					BBSenator temp = null;
					if((temp = m.get(s)) != null) {
						senators.add(temp);
					}
				}
			}

			SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d");

			if(!senators.isEmpty() || (u.getOtherData() && !_otherThreads.isEmpty())) {
				sendMail(email,"BillBuzz for " + sdf.format(new Date()),generateHTMLSubscriptionMessage(senators,name,u.getOtherData()),
						"billbuzz@nysenate.gov","Open Legislation");
			}
		}
	}


	public Map<String,BBSenator> mapping(List<BBSenator> senators) {

		Map<String,BBSenator> m = new HashMap<String,BBSenator>();

		for(BBSenator s:senators) {

			m.put(s.getName().toUpperCase(), s);

		}

		return m;
	}




	/**
	 * creates a map of senators from database
	 */
	public Map<String,gov.nysenate.billbuzz.model.persist.Senator> senatorMap() throws Exception {
		logger.info("creating senator map");
		Map<String,gov.nysenate.billbuzz.model.persist.Senator> m = new HashMap<String,gov.nysenate.billbuzz.model.persist.Senator>();

		List<gov.nysenate.billbuzz.model.persist.Senator> lst = Controller.getSenators();


		for(gov.nysenate.billbuzz.model.persist.Senator sen:lst) {

			String n = sen.getOpenLegName().toUpperCase();

			m.put(n, sen);
		}

		return m;

	}

	/**
	 * This is used to format the date correctly for both the Disqus API and lastuse document
	 * If a number is less than 10 it appends a '0' before it.  i.e. '8' will become '08'
	 * @param i is a number
	 * @return a string based on the number
	 */
	public String formatDate(int i) {
		return ((i<10) ? "0" + i : "" + i);
	}
	/**
	 * Uses JavaMail to send email
	 * @param to who is being emailed
	 * @param subject subject of the email
	 * @param message the message contents
	 * @param from who is sending the email
	 * @throws Exception
	 */
	public void sendMail(String to, String subject, String message, String from, String fromDisplay) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.starttls.enable","false");
		props.put("mail.smtp.socketFactory.port", SMTP_PORT);
		//props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.put("mail.smtp.socketFactory.fallback", "false");
	//	props.put("mail.smtps.ssl.protocols","TLSv1");
		props.put("mail.smtp.ssl.enable","false");
	//	props.put("mail.smtp.ssl.protocols","TLSv1");

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

	/**
	 * This function builds the Senator objects based on what is returned from the Disqus class
	 * 		in to the following form
	 * |--->Senator 1:
	 * |		|->Thread 1
	 * |		|->Thread 2
	 * |--->Senator 2:
	 * |		|->Thread 1
	 * |--->etc
	 * It dumps all threads associated with a senator and dumps them in to a senator object, it
	 * 		also verifies that the thread/post should be posted
	 * @param date "YYYY-MM-DDTXX:XX" only  posts after this date will be found
	 * @returns a list of Senator objects with their associated threads and information
	 * @throws Exception if it can't connect to the network
	 */
	public List<BBSenator> getSenatorUpdates(String date) throws Exception{
		logger.info("getting list of updated threads");

		ThreadMod tm = new ThreadMod();
		List<ThreadDescription> td = tm.getApprovedThreads(date);

		List<BBSenator> sen = new ArrayList<BBSenator>();

		for(ThreadDescription thread : td) {
			BillInfo bill = thread.getBill();

			//if it's the correct bill format
			if(!bill.getSenateId().matches(_billRegExp)) {
				logger.info("reading updates for billno: " + bill.getBillId() + " and threadid: " + thread.getID());

				List<String> toWho = getToWho(thread);

				if(toWho.isEmpty()) {
					_otherThreads.add(thread);
				}

				thread.setURL(correctURL(bill.getSenateId()));

				//add thread for everyone associated with it
				for(String oLName : toWho) {

					if(sen.isEmpty()) {
						BBSenator s = new BBSenator(oLName);
						s.addThread(thread);

						if(_sep.get(s.getName()) != null) {
							logger.info("creating senator: " + s.getName() +", adding thread: " + thread.getID());
							sen.add(s);
						}
						else {
							logger.info("senator " + s.getName() + " does not exist in _sep map");
						}

					} else {

						Iterator<BBSenator> senItr = sen.iterator();

						BBSenator s = null;

						while (senItr.hasNext()) {
							BBSenator curSen = senItr.next();
							//if the senator already exists
							if (curSen.getName().compareTo(oLName) == 0) {
								logger.info("adding threadid: " + thread.getID() + " to senator: " + curSen.getName());
								curSen.addThread(thread);
								break;
							}
							//if the senator wasn't found in the list this adds the new senators info
							if (!senItr.hasNext()) {
								s = new BBSenator(oLName);
								s.addThread(thread);
							}
						}
						//if a new senator is being added

						if(s != null && _sep.get(s.getName()) != null) {
							logger.info("creating senator: " + s.getName() + ", adding thread: " + thread.getID());
							sen.add(s);
						}
						else {
							logger.info("skipping billno: " + bill.getBillId() + ", senator: "
									+ oLName + ", threadid: " + thread.getID());
						}
					}
				}
			}
			else {
				logger.info("skipping billno: " + bill.getBillId());
			}
		}
		return sen;
	}

	/**
	 * This function compiles the list of Senators that are included in a Thread
	 * @param td is a ThreadDescription
	 * @returns a List<String> of senators' email address' who are associated with the Thread
	 */
	public List<String> getToWho(ThreadDescription td) {
		logger.info("getting senators for threadid: " + td.getID());
		List<String> ret = new ArrayList<String>();
		BillInfo b = td.getBill();
		//String oLName = .getOpenLegName();//getParty(b.getSponsor());
		//if the given senator has a listed email address
		if(_sep.get(b.getSponsor()) != null) {
			logger.info("adding senator " + _sep.get(b.getSponsor()).getOpenLegName().toUpperCase() + " to thread");
			ret.add(_sep.get(b.getSponsor()).getOpenLegName().toUpperCase());
		}
		//if there is a same as thread
		if(td.getSameAsThread() != null) {
			List<String> cosp = td.getSameAsThread().getBill().getCosponsors();
			//read through people associated with assembly thread
			for(String s:cosp) {
				//if there is a valid email and it isn't in the list
				gov.nysenate.billbuzz.model.persist.Senator perSen = _sep.get(s);
				if(perSen != null && !ret.contains(perSen.getOpenLegName().toUpperCase())) {
					logger.info("adding senator " + perSen.getOpenLegName().toUpperCase() + "to thread (from sameas)");
					ret.add(perSen.getOpenLegName().toUpperCase());
				}
			}
		}

		return ret;
	}


	/**
	 *
	 * This function ensures that the Senator will be given the correct URL
	 * @param billID is the billid of a particular bill
	 * @returns the correct URL
	 */
	public String correctURL(String billID) {
		return "http://open.nysenate.gov/legislation/bill/"+billID;
	}


	/**
	 * Structure of output:
	 * <greeting message>
	 * <billID1>:<url><billSummary1></url>
	 *
	 * 		<author1>: <comment1>
	 * 		<author2>: <comment2>
	 *
	 * <billID2>:<url><billSummary2></url>
	 * 		<author>: <comment1>
	 *
	 * etc...
	 * @param sen is a Senator Object
	 * @returns a String of the compiled message based on any new comments from Bills a senator is s
	 * 			sponsoring
	 */
	/*
	public String generatePTMessage(Senator sen) {
		Iterator<ThreadDescription> threadItr = sen.getThreads().iterator();
		String message = GENERAL_MSG
			+ sen.getName()
			+ ":<p>";
		while(threadItr.hasNext()) {
			ThreadDescription td = threadItr.next();
			BillInfo b = td.getBill();
			message = message.concat("<u><big>"
					+ b.getSenateId()
					+ ": "
					+ (((b.getTitle().length() > 100)
							? b.getTitle().substring(0,80) + "..." : b.getTitle()))
					+ "</big></u>");
			Iterator<Comment> comItr = td.getComments().iterator();
			while(comItr.hasNext()) {
				Comment com = comItr.next();
				message = message.concat(
						com.getCreatedAt()
						+ "<hr/>"
						+ ((com.getEmail().compareTo("") == 0)
								? com.getUsername() : com.getEmail() + "<br>" +com.getUsername())
						+ " says: <br/><br/>"
						+ com.getMessage()
						+  "<br/>");
			}
			message = message.concat("<br/>View more comments for "
					+ b.getSenateId()
					+ ": "
					+ td.getURL()
					+ "#discuss");

			//		+ "</table><p>");
		}
		return message;
	}	*/



	public String generateHTMLSubscriptionMessage(List<BBSenator> senators, String name, boolean otherThreadsTog) {

		String sUrl = "http://open.nysenate.gov/legislation/sponsor/";


		String message = "Greetings, " + name + ". Here's the latest buzz on your subscribed legislation"
			+ ":<hr/>";




		for(BBSenator sen:senators) {

			message +="<h3><b><a href=\"" + sUrl + sen.getName() + "\">Legislation from Senator " + sen.getName() + "</a></b><h3/>";
			Iterator<ThreadDescription> threadItr = sen.getThreads().iterator();
			while(threadItr.hasNext()) {
				ThreadDescription td = threadItr.next();
				BillInfo b = td.getBill();
				message = message.concat("<b>Comments on: <a href=\""
						+ td.getURL()
						+ "\">"
						+ b.getSenateId()
						+ ": "
						+ (((b.getTitle().length() > 100)
								? b.getTitle().substring(0,80) + "..." : b.getTitle()))
						+ "</a></b><br/><br/>");
				//add text describing comments
				message = message.concat(getCommentText(td));
				//if there is a sameAs thread associated with the thread print that as well
				if(td.getSameAsThread() != null) {
					message = message.concat(getCommentText(td.getSameAsThread()));
				}
				message += "<br/>";
			}
		}

		if(otherThreadsTog && _otherThreads != null && !_otherThreads.isEmpty()) {
			message += "<h3><b>Other Legislation</b></h3>";
			for(ThreadDescription td:_otherThreads) {
				BillInfo b = td.getBill();
				message = message.concat("<b>Comments on: <a href=\""
						+ td.getURL()
						+ "\">"
						+ b.getSenateId()
						+ ": "
						+ (((b.getTitle().length() > 100)
								? b.getTitle().substring(0,80) + "..." : b.getTitle()))
						+ "</a></b><br/><br/>");
				//add text describing comments
				message = message.concat(getCommentText(td));
				//if there is a sameAs thread associated with the thread print that as well
				if(td.getSameAsThread() != null) {
					message = message.concat(getCommentText(td.getSameAsThread()));
				}
				message += "<br/>";
			}
		}

		message +="<br/>Please visit the <a href=\"http://billbuzz.nysenate.gov\">BillBuzz homepage</a> to modify or delete your subscription.<br/>";

		return message;
	}










	public String generateHTMLMessage(BBSenator sen) {
		Iterator<ThreadDescription> threadItr = sen.getThreads().iterator();
		String message = "Good morning. Here's the latest buzz on legislation sponsored by Senator "
			+ sen.getName()
			+ ":<hr/><br/>";
		while(threadItr.hasNext()) {
			ThreadDescription td = threadItr.next();
			BillInfo b = td.getBill();
			message = message.concat("<b>Comments on: <a href=\""
					+ td.getURL()
					+ "\">"
					+ b.getSenateId()
					+ ": "
					+ (((b.getTitle().length() > 100)
							? b.getTitle().substring(0,80) + "..." : b.getTitle()))
					+ "</a></b><br/><br/>");
			//add text describing comments
			message = message.concat(getCommentText(td));
			//if there is a sameAs thread associated with the thread print that as well
			if(td.getSameAsThread() != null) {
				message = message.concat(getCommentText(td.getSameAsThread()));
			}
		}
		return message;
	}


	/**
	 * @param td is a ThreadDescription
	 * @returns a String of HTML containing the information from td
	 */
	public String getCommentText(ThreadDescription td) {
		String ret = "";
		BillInfo b = td.getBill();
		for(Comment c:td.getComments()) {
			ret = ret.concat(
					"<b>On " + c.getCreatedAt() + ": " + c.getUsername()
					+ " said:</b><br/><pre>"
					+ breakUp(c.getMessage(), 80)
					+  "</pre><hr/>");
		}
		ret = ret.concat("<a href=\""
				+ td.getURL()
				+ "#discuss\">View and respond to all comments for "
				+ b.getSenateId()
				+ "</a><hr/>");
		return ret;
	}

	public String breakUp(String string, int size) {

		String[] strings = string.split("\n");


		String ret = "";

		for(int count = 0; count < strings.length; count++) {

			if(strings[count].length() > size) {

				StringBuilder sb = new StringBuilder(strings[count]);

				int i = 0;

				while((i = sb.indexOf(" ", i+size)) != -1) {

					sb.insert(i+1, "\n");
				}

				strings[count] = sb.toString();
			}

		}

		for(String s:strings) {
			ret += s + "\n";
		}



		return ret;

	}
}















package gov.nysenate.billbuzz.util;

import gov.nysenate.billbuzz.model.BillBuzzApproval;
import gov.nysenate.billbuzz.model.BillBuzzConfirmation;
import gov.nysenate.billbuzz.model.BillBuzzSenator;
import gov.nysenate.billbuzz.model.BillBuzzThread;
import gov.nysenate.billbuzz.model.BillBuzzUser;
import gov.nysenate.util.Config;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;


public class Mailer
{
  private static Logger logger = Logger.getLogger(Mailer.class);

  public static void sendConfirmation(String templateName, String subject,
                                      BillBuzzUser user,
                                      BillBuzzConfirmation confirmation)
                     throws EmailException, MessagingException
  {
    VelocityContext vc = new VelocityContext();
    vc.put("user", user);
    vc.put("confirmation", confirmation);
    Mailer.send(templateName, subject, user, vc);
  } // sendConfirmation()


  public static void sendDigest(String templateName, String subject,
                                BillBuzzUser user,
                                Map<BillBuzzSenator, Map<BillBuzzThread, Set<BillBuzzApproval>>> userApprovals)
                     throws EmailException, MessagingException
  {
    VelocityContext vc = new VelocityContext();
    vc.put("dateFormat", new SimpleDateFormat("MMMM dd yyyy 'at' hh:mm a"));
    vc.put("user", user);
    vc.put("userApprovals", userApprovals);
    Mailer.send(templateName, subject, user, vc);
  } // sendDigest()


  private static void send(String templateName, String subject,
                           BillBuzzUser user,
                           VelocityContext context)
                      throws EmailException, MessagingException
  {
    logger.info("Sending "+templateName+" email to "+user.getEmail());

    Config config = Application.getConfig();
    String siteUrl = config.getValue("mailer.site_url");
    String fromAddress = config.getValue("mailer.from.address");
    context.put("siteUrl", siteUrl);
    context.put("fromAddress", fromAddress);

    // Generate text message
    StringWriter textWriter = new StringWriter();
    Velocity.mergeTemplate(templateName+".txt", "UTF-8", context, textWriter);

    // Generate HTML message
    StringWriter htmlWriter = new StringWriter();
    Velocity.mergeTemplate(templateName+".html", "UTF-8", context, htmlWriter);

    // Create the text part of the email
    MimeBodyPart textMsg = new MimeBodyPart();
    textMsg.setContent(textWriter.toString(), "text/plain");
    textMsg.setDisposition("inline");

    // Create the html part of the email
    MimeBodyPart htmlMsg = new MimeBodyPart();
    htmlMsg.setContent(htmlWriter.toString(), "text/html");
    htmlMsg.setDisposition("inline");

    // Populate the email content
    MimeMultipart content = new MimeMultipart("alternative");
    content.addBodyPart(textMsg);
    content.addBodyPart(htmlMsg);

    // Send off the mail!
    MultiPartEmail email = new MultiPartEmail();
    email.setHostName(config.getValue("mailer.hostname"));
    email.setSmtpPort(Integer.parseInt(config.getValue("mailer.port")));
    email.setAuthenticator(new DefaultAuthenticator(config.getValue("mailer.username"), config.getValue("mailer.password")));
    email.setSubject(subject);
    email.addTo(user.getEmail(), user.getFirstName()+" "+user.getLastName());
    email.setFrom(config.getValue("mailer.from.address"), config.getValue("mailer.from.name"));
    email.setCharset("UTF-8");
    email.setContent(content);

    // Email requests should timeout
    Properties props = email.getMailSession().getProperties();
    props.setProperty("mail.smtp.connectiontimeout", config.getValue("mailer.connection_timeout"));
    props.setProperty("mail.smtp.timeout", config.getValue("mailer.read_timeout"));

    try {
      email.send();
    }
    catch (Exception e1) {
      // Try again! If we fail again just give up.
      logger.error("Error sending email to "+user.getEmail(), e1);
      try {
        logger.info("Trying again...");
        email.send();
      }
      catch (Exception e2) {
        logger.error("Error sending email to "+user.getEmail(), e1);
        logger.info("Trying again...");
        email.send();
      }
    }
  }
}

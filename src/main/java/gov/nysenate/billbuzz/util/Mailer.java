package gov.nysenate.billbuzz.util;

import gov.nysenate.billbuzz.model.BillBuzzUser;
import gov.nysenate.util.Config;

import java.io.StringWriter;
import java.util.Properties;

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

    public static void send(String templateName, String subject, BillBuzzUser user, VelocityContext context) throws EmailException, MessagingException
    {
        logger.info("Sending "+templateName+" email to "+user.getEmail());

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
        htmlMsg.setContent(htmlWriter.toString(), "text/html;");
        htmlMsg.setDisposition("inline");

        // Populate the email content
        MimeMultipart content = new MimeMultipart("alternative");
        content.addBodyPart(textMsg);
        content.addBodyPart(htmlMsg);

        // Send off the mail!
        Config config = Application.getConfig();
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
        props.setProperty("mail.smtp.connectiontimeout", "10000");
        props.setProperty("mail.smtp.timeout", "10000");

        try {
            email.send();
        }
        catch (Exception e) {
            // Try again! If we fail again just give up.
            email.send();
        }
    }
}

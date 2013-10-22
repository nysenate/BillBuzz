package gov.nysenate.billbuzz.util;

import gov.nysenate.billbuzz.model.BillBuzzUser;
import gov.nysenate.util.Config;

import java.io.StringWriter;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class Mailer
{
    public static void send(String templateName, BillBuzzUser user, VelocityContext context) throws EmailException, MessagingException
    {
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
        email.setSubject("Test BillBuzz email");
        email.addTo(user.getEmail(), user.getFirstName()+" "+user.getLastName());
        email.setFrom(config.getValue("mailer.from.address"), config.getValue("mailer.from.name"));
        email.setCharset("UTF-8");
        email.setContent(content);
        email.send();
    }
}

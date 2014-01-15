package gov.nysenate.billbuzz.controller;

import gov.nysenate.billbuzz.model.BillBuzzConfirmation;
import gov.nysenate.billbuzz.model.BillBuzzUser;
import gov.nysenate.billbuzz.util.BillBuzzDAO;
import gov.nysenate.billbuzz.util.FormProcessor;
import gov.nysenate.billbuzz.util.Mailer;

import java.io.IOException;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

/**
 * Handles /BillBuzz/update/(request)? requests.
 *
 * @author GraylinKim
 *
 */
@SuppressWarnings("serial")
public class UpdateRequest extends HttpServlet
{
    private final Logger logger = Logger.getLogger(UpdateRequest.class);

    /**
     * Serve up the simple request form.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setAttribute("message", "instruction");
        request.getRequestDispatcher("/WEB-INF/pages/update_request.jsp").forward(request, response);
    }

    /**
     * Create confirmation code and send email with valid link to the update form
     *
     * @throws IOException
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.info("Processing update request...");
        try {
            String message = "";
            BillBuzzUser user = FormProcessor.processRequestForm(request);
            if (user == null) {
                message = "invalid_email";
                logger.info("Returning to the user for missing or invalid user email.");
            }
            else {
                message = "success";
                logger.info("Mailing user "+user.getFirstName()+" <"+user.getEmail()+"> an update link.");
                BillBuzzConfirmation confirmation = new BillBuzzDAO().getOrCreateConfirmation("update", user.getId(), true, null);

                VelocityContext context = new VelocityContext();
                context.put("user", user);
                context.put("request", request);
                context.put("confirmation", confirmation);
                Mailer.send("update_request", "BillBuzz Subscription Update Request", user, context);
            }

            request.setAttribute("user", user);
            request.setAttribute("message", message);
            request.getRequestDispatcher("/WEB-INF/pages/update_request.jsp").forward(request, response);
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
        catch (EmailException e) {
            logger.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
        catch (MessagingException e) {
            logger.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
    }
}

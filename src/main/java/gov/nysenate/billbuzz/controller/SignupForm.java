package gov.nysenate.billbuzz.controller;

import gov.nysenate.billbuzz.model.BillBuzzConfirmation;
import gov.nysenate.billbuzz.model.BillBuzzSubscription;
import gov.nysenate.billbuzz.model.BillBuzzUser;
import gov.nysenate.billbuzz.util.BillBuzzDAO;
import gov.nysenate.billbuzz.util.FormProcessor;
import gov.nysenate.billbuzz.util.Mailer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;


/**
 * Handles BillBuzz/(signup/(form)?)? requests.
 *
 * SignUp requests are posted to the server while form loads and confirmations
 * use get requests.
 *
 * @author Graylin Kim
 * @author Ken Zalewski <zalewski@nysenate.gov>
 *
 */
@SuppressWarnings("serial")
public class SignupForm extends HttpServlet
{
  private final Logger logger = Logger.getLogger(SignupForm.class);

  /**
   * Render the signup form.
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
                 throws ServletException, IOException
  {
    try {
      BillBuzzDAO dao = new BillBuzzDAO();
      request.setAttribute("message", "instruction");
      request.setAttribute("senators", dao.getSessionSenators(dao.getSessionYear()));
      request.setAttribute("subscriptions", FormProcessor.getSubscriptionMap(new ArrayList<BillBuzzSubscription>()));
      request.getRequestDispatcher("/WEB-INF/pages/signup_form.jsp").forward(request, response);
    }
    catch (SQLException e) {
      logger.error(e.getMessage(), e);
      throw new ServletException(e.getMessage(), e);
    }
  }


  /**
   * Process the sign-up form and send confirmation email
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
                 throws ServletException, IOException
  {
    logger.info("Processing subscription form...");
    try {
      String message = "";
      Date now = new Date();
      BillBuzzDAO dao = new BillBuzzDAO();
      BillBuzzUser user = FormProcessor.processSubscriptionForm(request, now);
      List<BillBuzzSubscription> subscriptions = new ArrayList<BillBuzzSubscription>();
      if (user == null) {
        logger.info("Returning to the user for missing user info.");
        message = "missing_userinfo";
        subscriptions = FormProcessor.getSubscriptions(request, 0L, now);
      }
      else if (user.getSubscriptions().isEmpty()) {
        logger.info("Returning to the user for missing subscription info.");
        message = "missing_subscription";
      }
      else {
        subscriptions = user.getSubscriptions();
        message = "success";
        BillBuzzConfirmation confirmation = dao.getOrCreateConfirmation("signup", user.getId(), true, now);
        String templateName = "signup_confirmation";
        String subject = "BillBuzz Signup Confirmation";

        if (user.getConfirmedAt() == null) {
          logger.info("Processing signup for: "+user.getFirstName()+" <"+user.getEmail()+">");
          // Save subscriptions
          dao.replaceSubscriptions(subscriptions, user.getId());
        }
        else {
          logger.info("Mailing confirmed user "+user.getFirstName()+" <"+user.getEmail()+"> a link to their update form.");
          message = "update_required";
          templateName = "update_confirmation";
          subject = "BillBuzz Update Request";
        }
        Mailer.sendConfirmation(templateName, subject, user, confirmation);
      }

      request.setAttribute("user", user);
      request.setAttribute("message", message);
      request.setAttribute("subscriptions", FormProcessor.getSubscriptionMap(subscriptions));
      request.setAttribute("senators", dao.getSessionSenators(dao.getSessionYear()));
      request.getRequestDispatcher("/WEB-INF/pages/signup_form.jsp").forward(request, response);
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

package gov.nysenate.billbuzz.controller;

import gov.nysenate.billbuzz.model.BillBuzzConfirmation;
import gov.nysenate.billbuzz.model.BillBuzzSubscription;
import gov.nysenate.billbuzz.util.Application;
import gov.nysenate.billbuzz.util.BillBuzzDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

/**
 * Handles /BillBuzz/signup/confirm requests.
 *
 * @author Graylin
 *
 */
@SuppressWarnings("serial")
public class SignupConfirmation extends HttpServlet
{
    private final Logger logger = Logger.getLogger(SignupConfirmation.class);

    /**
     * Verify confirmation email and update the user account accordingly
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.info("Signup Confirmation Request: "+request.getQueryString());
        try {
            String message = "";
            BillBuzzDAO dao = new BillBuzzDAO();
            BillBuzzConfirmation confirmation = null;
            List<BillBuzzSubscription> subscriptions = new ArrayList<BillBuzzSubscription>();

            String confirmationCode = request.getParameter("key");
            if (confirmationCode == null) {
                message = "invalid";
                logger.info("Missing required 'key' parameter for confirmation.");
            }
            else {
                confirmation = dao.loadConfirmation("signup", confirmationCode);
                if (confirmation == null || confirmation.isExpired()) {
                    message = "invalid";
                    logger.info("Confirmation code '"+confirmationCode+"' is invalid.");
                }
                else {
                    subscriptions = dao.loadSubscriptions(confirmation.getUserId());
                    QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());

                    if (confirmation.isUsed()) {
                        if (confirmation.getUser().isActivated()) {
                            message = "activated";
                            logger.info("User "+confirmation.getUser().getEmail()+" is already activated");
                        }
                        else {
                            message = "reactivate";
                            logger.info("Reactivating user "+confirmation.getUser().getEmail());
                            runner.update("UPDATE billbuzz_user SET activated = True WHERE id = ?", confirmation.getUserId());
                        }
                    }
                    else {
                        message = "success";
                        Date now = new Date();
                        logger.info("Activating user "+confirmation.getUser().getEmail());
                        runner.update("UPDATE billbuzz_user SET activated = True, confirmedAt = ? WHERE id = ?", now, confirmation.getUserId());
                        confirmation.setUsedAt(now);
                        dao.saveConfirmation(confirmation);
                    }
                }
            }

            request.setAttribute("message", message);
            request.setAttribute("confirmation", confirmation);
            request.setAttribute("subscriptions", subscriptions);
            request.getRequestDispatcher("/WEB-INF/pages/signup_confirmation.jsp").forward(request, response);
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
    }
}

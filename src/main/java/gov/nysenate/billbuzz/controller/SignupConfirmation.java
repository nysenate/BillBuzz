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

/**
 * Handles /BillBuzz/signup/confirm requests.
 *
 * @author Graylin
 *
 */
@SuppressWarnings("serial")
public class SignupConfirmation extends HttpServlet
{
    /**
     * Verify confirmation email and update the user account accordingly
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try {
            String message = "";
            BillBuzzDAO dao = new BillBuzzDAO();
            BillBuzzConfirmation confirmation = null;
            List<BillBuzzSubscription> subscriptions = new ArrayList<BillBuzzSubscription>();

            String confirmationCode = request.getParameter("key");
            if (confirmationCode == null) {
                message = "invalid";
            }
            else {
                confirmation = dao.loadConfirmation("signup", confirmationCode);
                if (confirmation == null || confirmation.isExpired()) {
                    message = "invalid";
                }
                else {
                    subscriptions = dao.loadSubscriptions(confirmation.getUserId());
                    QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());

                    if (confirmation.isUsed()) {
                        if (confirmation.getUser().isActivated()) {
                            message = "activated";
                        }
                        else {
                            message = "reactivate";
                            runner.update("UPDATE billbuzz_user SET activated = True WHERE id = ?", confirmation.getUserId());
                        }
                    }
                    else {
                        message = "success";
                        Date now = new Date();
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
            throw new ServletException(e.getMessage(), e);
        }
    }
}

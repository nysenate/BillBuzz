package gov.nysenate.billbuzz.controller;

import gov.nysenate.billbuzz.model.BillBuzzConfirmation;
import gov.nysenate.billbuzz.util.Application;
import gov.nysenate.billbuzz.util.BillBuzzDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

/**
 * Handles /BillBuzz/unsubscribe/confirm requests
 *
 * @author GraylinKim
 *
 */
@SuppressWarnings("serial")
public class UnsubscribeConfirmation extends HttpServlet
{
    private final Logger logger = Logger.getLogger(UnsubscribeConfirmation.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try {
            String message = "";
            BillBuzzDAO dao = new BillBuzzDAO();
            BillBuzzConfirmation confirmation = null;

            String confirmationCode = request.getParameter("key");
            if (confirmationCode == null) {
                message = "invalid";
            }
            else {
                QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());
                confirmation = dao.loadConfirmation("unsubscribe", confirmationCode);
                if (confirmation == null || confirmation.isExpired()) {
                    message = "invalid";
                }
                else {
                    if (confirmation.getUser().isActivated()) {
                        message = "success";
                        runner.update("UPDATE billbuzz_user SET activated = False WHERE id = ?", confirmation.getUserId());
                        confirmation.setUsedAt(new Date());
                        dao.saveConfirmation(confirmation);
                    }
                    else {
                        message = "inactive";
                    }
                }
            }
            request.setAttribute("message", message);
            request.setAttribute("confirmation", confirmation);
            request.getRequestDispatcher("/WEB-INF/pages/unsubscribe_confirmation.jsp").forward(request, response);
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
    }
}

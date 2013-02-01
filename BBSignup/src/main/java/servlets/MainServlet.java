package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bbsignup.model.Senator;
import bbsignup.model.User;
import bbsignup.model.UserAuth;
import bbsignup.src.Controller;
import bbsignup.src.PMF;

/**
 * Servlet implementation class MainServlet
 */
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// will store selected subscriptions
		List<String> subs = new ArrayList<String>();

		HttpSession session = request.getSession();
		Object o = SenatorContext.getSenators(this.getServletContext());

		String fname = null;
		String lname = null;
		String email = null;
		String otherData = null;

		// if update exists in the session then the user already xists */
		String update = (String)session.getAttribute("update");

		if(o != null) {
			fname = request.getParameter("firstname");
			lname = request.getParameter("lastname");
			email = request.getParameter("email1");
			otherData = request.getParameter("otherData");

			/* this is a session variable stored from index.jsp, verifies
			 * that the user is following proper workflow */
			@SuppressWarnings("unchecked")
			List<Senator> senators = (List<Senator>)o;

			// if the button to select all senators has been selected
			if(request.getParameter("cb_all") != null) {
				subs.add("all");
			}
			else {
				for(Senator senator:senators) {
					String[] params = request.getParameterValues(senator.getOpenLegName());

					if(params != null) {
						//it has been selected
						subs.add(senator.getOpenLegName());
					}
				}
			}

			//they didn't select anything, return back to index.jsp
			if(subs.size() == 0) {
				session.setAttribute("fn", fname);
				session.setAttribute("ln", lname);
				session.setAttribute("e", email);
				session.setAttribute("subs", subs);
				response.sendRedirect("index.jsp");
				return;
			}

			//they selected everything, change to 'all'
			if(subs.size() == 62) {
				subs.clear();
				subs.add("all");
			}

			Controller c = new Controller();
			String message = null;
			User u = new User(fname, lname, email, "n", (otherData.equals("yes") ? true:false));
			u.setSubscriptions(subs);

			if(update == null) {
				User cur = (User)PMF.getDetachedObject(User.class, "email", email);

				if(cur == null) {
					UserAuth ua = new UserAuth(email);

					c.newUserEmail(email, ua.getHash());
					PMF.persistObject(u, ua);
					message = "<div class=\"good\" style=\"width:500px;\">" +
						"Thanks for signing up, you should receive an email shortly to verify your request." +
						"<br/><br/>" +
						"Redirecting to nysenate.gov in 10 seconds..." +
						"<meta http-equiv=\"Refresh\" content=\"10; URL=http://www.nysenate.gov\">" +
						"</div>";
				}
				else {
					message = "<div class=\"bad\" style=\"width:500px;\">" +
						"It appears that email address already has an account!  Please " +
						"<a href=\"update.jsp\">click here</a> to update." +
						"<br/><br/>" +
						"</div>";
				}


			}
			else {
				String oldEmail = (String) session.getAttribute("oldemail");
				u.setAuth("y");

				PMF.deleteObjects(new Class[] {UserAuth.class, User.class},
						new String[] {"email","email"},
						new String[] {oldEmail,oldEmail});
				PMF.persistObject(u);

				message = "<div class=\"good\" style=\"width:500px;\">" +
					"Your account has been successfully updated!  Thanks for using BillBuzz." +
					"<br/><br/>" +
					"Redirecting to nysenate.gov in 10 seconds..." +
					"<meta http-equiv=\"Refresh\" content=\"10; URL=http://www.nysenate.gov\">" +
					"</div>";

			}
			session.setAttribute("subs", null);
			session.setAttribute("fn", null);
			session.setAttribute("ln", null);
			session.setAttribute("e", null);
			session.setAttribute("oldemail", null);
			session.setAttribute("update", null);

			session.setAttribute("message", message);
			response.sendRedirect("message.jsp");
		}
		else {
			response.sendRedirect("index.jsp");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

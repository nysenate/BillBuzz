package gov.nysenate.billbuzz.servlet;

import gov.nysenate.billbuzz.Controller;
import gov.nysenate.billbuzz.model.persist.Senator;
import gov.nysenate.billbuzz.model.persist.User;
import gov.nysenate.billbuzz.model.persist.UserAuth;
import gov.nysenate.billbuzz.service.PMF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fname = request.getParameter("firstname");
		String lname = request.getParameter("lastname");
		String email = request.getParameter("email1");
		String otherData = request.getParameter("otherData");

        List<String> subs = new ArrayList<String>();
		// if the button to select all senators has been selected
		if(request.getParameter("cb_all") != null) {
			subs.add("all");
		}
		else {
		    List<Senator> senators = (List<Senator>)request.getAttribute("senators");

		    // Check to see if the box for each senator has been checked
		    Map<String, String> parameters = request.getParameterMap();
			for(Senator senator:senators) {
			    if (parameters.containsKey(senator.getOpenLegName())) {
			        subs.add(senator.getOpenLegName());
			    }
			}

		    // if they selected everything, change to 'all'
	        if(subs.size() == senators.size()) {
	            subs.clear();
	            subs.add("all");
	        }
		}

	    // if update exists in the session then the user already exists
        HttpSession session = request.getSession();
        String update = (String)session.getAttribute("update");

		//they didn't select anything, return back to index.jsp
		// TODO: can we tell them to make sure some boxes are selected?
		if(subs.size() == 0) {
			session.setAttribute("fn", fname);
			session.setAttribute("ln", lname);
			session.setAttribute("e", email);
			session.setAttribute("subs", subs);
			response.sendRedirect("index.jsp");
			return;
		}

		Controller c = new Controller();
		String message = null;
		User user = new User(fname, lname, email, "n", (otherData.equals("yes") ? true:false));
		user.setSubscriptions(subs);

		if(update == null) {
			User cur = (User)PMF.getDetachedObject(User.class, "email", email);

			if(cur == null) {
				UserAuth userauth = new UserAuth(email);

				c.newUserEmail(email, userauth.getHash());
				PMF.persistObject(user, userauth);
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
			user.setAuth("y");

			PMF.deleteObjectById(User.class, "email", oldEmail);
			PMF.deleteObjectById(UserAuth.class, "email", oldEmail);
			PMF.persistObject(user);

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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}

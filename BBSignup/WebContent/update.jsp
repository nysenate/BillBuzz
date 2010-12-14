<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.util.*,bbsignup.src.*,bbsignup.model.*,javax.jdo.*"
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<link rel="stylesheet" type="text/css" media="screen" href="style.css"/> 
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>BillBuzz!  Update Your Subscription</title>
</head>
<body>
<center>
<br/>
<%
	Controller c = new Controller();
	String uemail = (String)request.getParameter("uemail");
	String key = (String)request.getParameter("key");
	
	if(uemail != null && key != null) {
		
		User u = null;
		UserAuth ua = null;
		
		if((u = c.getUser(uemail)) != null && (ua = c.getUserAuth(uemail)) != null &&
				ua.isHashCorrect(key)) {
			session.setAttribute("update", "update");
			session.setAttribute("fn",u.getFirstName());
			session.setAttribute("ln",u.getLastName());
			session.setAttribute("e",u.getEmail());
			session.setAttribute("subs", u.getSubscriptions());
			session.setAttribute("oldemail", uemail);
			%>
				<jsp:forward page="index.jsp" />
			<%
		}
		else {
			session.setAttribute("error","errno6");
			%>
				<jsp:forward page="notice.jsp" />
			<%
		}
		
	}

	else if(uemail != null) {
		String val = new Controller().updateEmail(uemail);
		if(val == null) {
			%>
				<div class="good" style="width:500px;">
				
					Thank you for using BillBuzz, you will receive an email shortly<br/>
					with a link allowing you to update your subscription.
				
				</div>
			<%
		}
		else {
			session.setAttribute("error",val);
			%>
				<jsp:forward page="notice.jsp" />
			<%
		}
		
	}
	else {		
		%>
		<h2 style="left:-115px">Update your BillBuzz subscription</h2>
		<div class="good" style="width:500px;">
		<form name="update" method="post" action="">
			<table>
				<tr>
					<td colspan = 2>
						NOTE: You will receive an email that will allow you to update your settings.<br/>
					</td>
				</tr>
				<tr>
					<td>
						Email 
					</td>
					<td>
						<input type="text" name="uemail"></input>
					</td>
				</tr>
				<tr>
					<td>
						
					</td>
					<td>
						<input type="submit" name="updatesubmit" value="Update"></input>
					</td>
				</tr>
			</table>	
		</form>
		</div>		
		<%		
	}
%>


<%@ include file="footer.jsp" %>
</center>
</body>
</html>
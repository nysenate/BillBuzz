<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.util.*,bbsignup.src.*,bbsignup.model.*,javax.jdo.*"
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<link rel="stylesheet" type="text/css" media="screen" href="style.css"/> 

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>BillBuzz!  Processing request...</title>
</head>
<body>
<center>
<br/>
<%
	Object o = session.getAttribute("senators");
	if(o != null) {
				
		if(o instanceof List<?>) {
			
			String update = (String)session.getAttribute("update");
			String updateemail = (String)session.getAttribute("updateemail");
			
			List<Senator> list = (List<Senator>)o;
			
			Controller c = new Controller();
			
			
			String[] r = request.getParameterValues("r");
			String[] d = request.getParameterValues("d");
						
			String s = c.handle(request.getParameter("firstname"),request.getParameter("lastname"),request.getParameter("email1"),
					((updateemail != null) ? updateemail : request.getParameter("email2")),r,d,list,
					((update != null && update.equals("true")) ? true : false));
				
			if(s.equals("")) {
				if(update != null && update.equals("true")) {
					session.setAttribute("error",null);
					session.setAttribute("update",null);
					session.setAttribute("updateemail",null);
					%>
						<div class="good" style="width:500px;">
							Your account has been successfully updated!  Thanks for using BillBuzz.
							<br/><br/>
							Redirecting to nysenate.gov in 10 seconds...
							<meta http-equiv="Refresh" content="10; URL=http://www.nysenate.gov">
						</div>
					<%
				}
				else {						
					//success
					//send auth email
					c.newUserEmail(request.getParameter("email1"));
					%>
						<div class="good" style="width:500px;">
							Thanks for signing up, you should receive an email shortly to verify your request.
							<br/><br/>
							Redirecting to nysenate.gov in 10 seconds...
							<meta http-equiv="Refresh" content="10; URL=http://www.nysenate.gov">
						</div>
					<%
				}
				
			}
			else { 
				
				session.setAttribute("error", s);
				session.setAttribute("fn",request.getParameter("firstname"));
				session.setAttribute("ln",request.getParameter("lastname"));
				session.setAttribute("e1",request.getParameter("email1"));
				session.setAttribute("e2",request.getParameter("email2"));
				session.setAttribute("subs", c.getSubFromArrays(r,d,list));
				%>					
					<jsp:forward page="notice.jsp" />					
				<%
			}	
		}				
	}		
	else {
		%>
			
			<jsp:forward page="index.jsp" />
		
		<%
	}
%>
<%@ include file="footer.jsp" %>
</center>
</body>
</html>
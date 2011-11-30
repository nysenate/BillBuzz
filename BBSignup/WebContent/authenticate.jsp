<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.util.*,bbsignup.src.*,bbsignup.model.*,javax.jdo.*"
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<link rel="stylesheet" type="text/css" media="screen" href="style.css"/> 

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>BillBuzz!  Authentication</title>
</head>
<body>
<center>
<br/>

<%
	String email = request.getParameter("email");
	String key = request.getParameter("key");
	
	Controller c = new Controller();
	
	if(key != null && email != null) {
		if(c.authenticateUser(email,key) == true) {
			%>
				<div class="good" style="width:500px;">
					Success!  Thanks for signing up, you'll start receiving BillBuzz updates within the next 24 hours.
					<br/><br/>
					Redirecting to nysenate.gov in 10 seconds...
					<meta http-equiv="Refresh" content="10; URL=http://www.nysenate.gov">
				</div>	
			<%
		}
		else {
			%>
				<div class="bad" style="width:500px;">
					There was an error authenticating the email address <%=email %>
					<br/><br/>
					Please <a href="mailto:billbuzz@nysenate.gov">contact us</a> if you signed up and are having trouble.
				</div>
			<%
		}
		
	}
	else if(email != null) {
		%>
			<div class="bad" style="width:500px;">
				There was an error authenticating the email address <%=email %>
				<br/><br/>
				Please <a href="mailto:billbuzz@nysenate.gov">contact us</a> if you signed up and are having trouble.
			</div>
		<%
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
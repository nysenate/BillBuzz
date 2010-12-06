<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.util.*,bbsignup.src.*,bbsignup.model.*,javax.jdo.*"
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<link rel="stylesheet" type="text/css" media="screen" href="style.css"/> 

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>BillBuzz!  Notice</title>
</head>
<body>
<center>
<%

	String s = (String)session.getAttribute("error");

	if(s.equals("errno2")) {
		//email already exists
		session.setAttribute("error", "<LI>Email address "+ session.getAttribute("e1") + " already registered");
		%>
			<div class="bad" style="width:500px;">
			Sorry, it appears the email address <%=session.getAttribute("e1")%> has already been registered.
			<br/><br/>
			If you think this is a mistake please <a href="mailto:billbuzz@nysenate.gov">contact us</a> or <a href="index.jsp">try again</a>.
			</div>
		<%
		session.setAttribute("e1", null);
		session.setAttribute("e2", null);
	}
	else if(s.equals("errno3")) {
		//email not in senate range
		session.setAttribute("error", "<LI>Please use @senate email address");
		session.setAttribute("e1", null);
		session.setAttribute("e2", null);
		
		%>
			<div class="bad" style="width:500px;">
				Sorry, this service is currently only available to emails @senate.state.ny.us or @nysenate.gov.
				<br/><br/>
				If you think this is a mistake please <a href="mailto:billbuzz@nysenate.gov">contact us</a> or <a href="index.jsp">try again</a>.
			</div>
		<%
	}
	else if(s.equals("errno4")) {
		//user trying to delete sub, not in system
		session.setAttribute("error", null);
		%>
			<div class="bad" style="width:500px;">
				Sorry, it appears we can't find that email in our system.
				<br/><br/>
				If you think this is a mistake please <a href="mailto:billbuzz@nysenate.gov">contact us</a> or <a href="unsubscribe.jsp">try again</a>.
			</div>
		<%
	}
	else if(s.equals("errno5")) {
		session.setAttribute("error", null);
		%>
			<div class="bad" style="width:500px;">
				Sorry, it appears we can't find that email in our system.
				<br/><br/>
				If you think this is a mistake please <a href="mailto:billbuzz@nysenate.gov">contact us</a> or <a href="update.jsp">try again</a>.
			</div>
		<%
	}
	else if(s.equals("errno6")) {
		session.setAttribute("error", null);
		%>
			<div class="bad" style="width:500px;">
				Sorry, that request is invalid.
				<br/><br/>
				If you think this is a mistake please <a href="mailto:billbuzz@nysenate.gov">contact us</a> or <a href="update.jsp">try again</a>.
			</div>
		<%
	}
	else {
		//bad input
		
		%>
		
			<jsp:forward page="index.jsp" />
	
		<%
		
	
	}
%>
<%@ include file="footer.jsp" %>
</center>
</body>
</html>
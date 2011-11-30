<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.util.*,bbsignup.src.*,bbsignup.model.*,javax.jdo.*"
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<link rel="stylesheet" type="text/css" media="screen" href="style.css"/> 
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<center>
<br/>
<%
	String email = (String)request.getParameter("email");
	String key = (String)request.getParameter("key");
	
	if(email != null && key != null) {
		if(new Controller().deleteUser(email,key)) {
			%>
				<div class="good" style="width:500px;">
					Thank you for using BillBuzz.  Your subscription has been successfully deleted.
					<br/><br/>
					Redirecting to nysenate.gov in 10 seconds...
					<meta http-equiv="Refresh" content="10; URL=http://www.nysenate.gov">
				</div>	
			<%
		}
		
		
	}
	else {
		%>
		<div class="bad" style="width:500px;">
			There was an error authenticating your request.
			<br/><br/>
			Please <a href="mailto:billbuzz@nysenate.gov">contact us</a> if you are having trouble.
		</div>
	<%
	}

	

%>

<%@ include file="footer.jsp" %>
</center>
</body>
</html>
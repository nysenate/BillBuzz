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
	String message = (String)session.getAttribute("message");
	if(message != null) {
		out.write(message);
		session.setAttribute("message", null);
	}
	else {
		response.sendRedirect("index.jsp");
	}


%>


<%@ include file="footer.jsp" %>
</center>
</body>
</html>
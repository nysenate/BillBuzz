<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.util.*,bbsignup.src.*,bbsignup.model.*,javax.jdo.*"
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<link rel="stylesheet" type="text/css" media="screen" href="style.css"/> 

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>BillBuzz!  Unsubscribe</title>
</head>
<body>
<center>
<br/>
<%
	String uemail = (String)request.getParameter("uemail");

	if(uemail != null) {
		
		String e = new Controller().tryDelete(uemail);
		
		if(e == null) {
			%>
				<div class="good" style="width:500px;">
				
					Thank you for using BillBuzz, you will receive an email shortly<br/>
					to verify the cancellation of your subscription.
				
				</div>
			<%
		}
		else {
			
			session.setAttribute("error",e);
			
			%>
				<jsp:forward page="notice.jsp"/>
			<%
			
			
		}
		
	}
	else {
		%>
			<h2 style="left:-150px">Unsubscribe from BillBuzz</h2>
			
			<div class="bad" style="width:500px;">
			<form name="unsub" method="post" action="">
				<table>
					<tr>
						<td colspan=2>
							NOTE: This will remove your BillBuzz subscription.  After you submit your<br/>
							 email address you will receive an email confirming this with a clickable link.
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
							<input type="submit" name="unsubscribe" value="Unsubscribe"></input>
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
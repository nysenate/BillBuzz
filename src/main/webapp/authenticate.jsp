<%@ page language="java" import="java.util.*,gov.nysenate.billbuzz.Controller,gov.nysenate.billbuzz.model.*,javax.jdo.*"%>
<jsp:include page="header.jsp" />
<div id="main">
<center>

<%
	String email = request.getParameter("email");
	String key = request.getParameter("key");
	
	Controller c = new Controller();
	
	if(key != null && email != null) {
		if(c.authenticateUser(email,key) == true) {
			%>
				<div class="good" style="width:500px;">
					Success!  Thanks for signing up.  You'll start receiving BillBuzz updates within the next 24 hours.
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
			<jsp:forward page="/" />
		<%
		
	}

%>
</center>
</div>
<%@ include file="footer.jsp"%>
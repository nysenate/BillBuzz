<%@ page language="java" import="java.util.*,gov.nysenate.billbuzz.src.*,gov.nysenate.billbuzz.model.*,javax.jdo.*"
    %>

<jsp:include page="header.jsp" />
<div id="main">
<center>
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
		else {
			%>
			<div class="bad" style="width:500px;">
				There was an error authenticating your request.
				<br/><br/>
				Please <a href="mailto:billbuzz@nysenate.gov">contact us</a> if you are having trouble.
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

</center>
</div>
<%@ include file="footer.jsp"%>
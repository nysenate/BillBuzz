<%@ page language="java" import="java.util.*,gov.nysenate.billbuzz.model.*" %>
<jsp:include page="/WEB-INF/parts/header.jsp">
    <jsp:param value="unsubscribe" name="page"/>
</jsp:include>
<%
String email = request.getParameter("email");
String message = (String)request.getAttribute("message");
if (message.equals("instruction")) { %>
<div class="bb_main instruction">
Enter your email below and we'll send you a link that can be used to cancel your subscription.
</div>
<% } else if (message.equals("invalid_email")) { %>
<div class="bb_main error">
This email was not found in our system.
</div>
<% } else if (message.equals("inactive_user")) { %>
<div class="bb_main error">
You account has already been deactivated.
</div>
<% } else if (message.equals("success")) { %>
<div class="bb_main success">
We've sent you an email. Just click the link in the email confirm cancellation of your subscription.
</div>
<% } %>
<br/>
<div class="bb_main">
<div style="width:500px;">
<form name="unsub" method="post" action="">
	<table>
		<tr>
			<td colspan = 2 align=center>
				Please enter your email address:
			</td>
		</tr>
		<tr>
			<td colspan = 2 align=center>
				<input type="text" name="email" <%=email != null ? "value="+email : ""%>></input>
				<input type="submit" name="unsubscribe" value="Unsubscribe"></input>
			</td>
		</tr>
	</table>
</form>
</div>
</div>
<%@ include file="/WEB-INF/parts/footer.jsp"%>
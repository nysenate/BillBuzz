<%@ page language="java" import="java.util.*,gov.nysenate.billbuzz.src.*,gov.nysenate.billbuzz.model.*,javax.jdo.*"
    %>
<jsp:include page="header.jsp" />
<div id="main">
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
		
			<jsp:forward page="/" />
	
		<%
		
	
	}
%>
</center>
</div>
<%@ include file="footer.jsp"%>
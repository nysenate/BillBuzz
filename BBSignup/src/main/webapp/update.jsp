<%@ page language="java" import="java.util.*,bbsignup.src.*,bbsignup.model.*,javax.jdo.*"
    %>
<jsp:include page="header.jsp" />
<div id="main">
<body>
<center>
<%
	Controller c = new Controller();
	String uemail = (String)request.getParameter("uemail");
	String key = (String)request.getParameter("key");
	
	if(uemail != null && key != null) {
		
		User u = null;
		UserAuth ua = null;
		
		if((u = c.getUser(uemail)) != null && (ua = c.getUserAuth(uemail)) != null &&
				ua.isHashCorrect(key)) {
			session.setAttribute("update", "update");
			session.setAttribute("fn",u.getFirstName());
			session.setAttribute("ln",u.getLastName());
			session.setAttribute("e",u.getEmail());
			session.setAttribute("subs", u.getSubscriptions());
			session.setAttribute("oldemail", uemail);
			session.setAttribute("otherData",u.getOtherData()?"yes":"no");
			%>
				<jsp:forward page="/" />
			<%
		}
		else {
			session.setAttribute("error","errno6");
			%>
				<jsp:forward page="notice.jsp" />
			<%
		}
		
	}

	else if(uemail != null) {
		String val = new Controller().updateEmail(uemail);
		if(val == null) {
			%>
				<div class="good" style="width:500px;">
				
					Thank you for using BillBuzz, you will receive an email shortly<br/>
					with a link allowing you to update your subscription.
				
				</div>
			<%
		}
		else {
			session.setAttribute("error",val);
			%>
				<jsp:forward page="notice.jsp" />
			<%
		}
		
	}
	else {		
		%>
		<h2 style="left:-115px">Update your BillBuzz subscription</h2>
		<div class="good" style="width:500px;">
		<form name="update" method="post" action="">
			<table>
				<tr>
					<td colspan = 2 align=center>
						Please enter your email address:
					</td>
				</tr>
				<tr>
					
					<td colspan=2 align=center>
						<input type="text" name="uemail"></input>
						<input type="submit" name="updatesubmit" value="Update"></input>
					</td>
				</tr>
				<tr>
					<td colspan = 2>
						NOTE: You will receive an email that will allow you to update your settings.<br/>
					</td>
				</tr>
			</table>	
		</form>
		</div>		
		<%		
	}
%>
</div>

</center>
<%@ include file="footer.jsp"%>
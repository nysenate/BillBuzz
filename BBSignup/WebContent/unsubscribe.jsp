<%@ page language="java" 
    import="java.util.*,bbsignup.src.*,bbsignup.model.*,javax.jdo.*"
    %>
<jsp:include page="header.jsp" />
<div id="main">
<center>
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
</div>
</center>
<%@ include file="nfooter.jsp"%>
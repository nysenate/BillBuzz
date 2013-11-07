<%@ page contentType="text/html" pageEncoding="UTF-8" language="java" import="java.util.*,gov.nysenate.billbuzz.model.*"%>
<jsp:include page="/WEB-INF/parts/header.jsp">
<jsp:param value="signup" name="page"/>
</jsp:include>
<%
String email = request.getParameter("email");
String email2 = request.getParameter("email2");
String lastName = request.getParameter("lastName");
String firstName = request.getParameter("firstName");
String message = (String)request.getAttribute("message");
BillBuzzUser user = (BillBuzzUser)request.getAttribute("user");

@SuppressWarnings("unchecked")
List<BillBuzzSenator> senators = (List<BillBuzzSenator>)request.getAttribute("senators");

@SuppressWarnings("unchecked")
Map<String, Set<String>> subscriptions = (Map<String, Set<String>>)request.getAttribute("subscriptions");
%>
<% if (message.equals("instruction")) { %>
<div class="bb_main instruction">
To sign up simply fill out the form below and select parties or individuals that you would like to receive updates for!
</div>
<% } else if (message.equals("missing_userinfo")) { 
    if (lastName == null || firstName == null || email == null) { %>
        <div class="bb_main error">
        Email, first name, and last name are required when registering an account with BillBuzz.
        </div><%
    } else { %>
        <div class="bb_main error">
        You must confirm your email address to continue with registration.
        </div><%
    }
} else if (message.equals("missing_subscription")) { %>
<div class="bb_main error">
At least one subscription is required to activate a BillBuzz account.
</div>
<% } else if (message.equals("success")) { %>
<div class="bb_main success">
We've sent you a email with a link to confirm your subscription to the parties and senators checked below.
</div>
<% } else if (message.equals("update_required")) { %>
<div class="bb_main error">
It seems like you already have an account with us. We've sent you a link that can be used to update or reactivate your account.
</div>
<% } %>
<br/>
<div class="bb_main">
<form id="subscriptionForm" method="POST" action="<%=request.getContextPath()%>/signup/form">
<br>
	<table>
		<tr>
			<td>First name</td>
			<td><input type="text" name="firstName" value="<%=((firstName == null) ? "" : firstName)%>"></input></td>
		</tr>
		<tr>
			<td>Last name</td>
			<td><input type="text" name="lastName" value="<%=((lastName == null) ? "" : lastName)%>"></input></td>
		</tr>
		<tr>
			<td>Email</td>
			<td><input type="text" name="email" value="<%=((email == null) ? "" : email)%>"></input></td>
		</tr>
		<tr>
			<td>Confirm email</td>
			<td><input type="text" name="email2" value="<%=((email2 == null) ? "" : email2)%>"></input></td>
		</tr>
	</table>
    <br />
	<table>
		<tr>
			<td><input type="checkbox" name="all" value="all" <%=subscriptions.get("all").contains("all") ? "checked=\"yes\"" : ""%>></input></td>
			<td>All</td>
			<td><input type="checkbox" name="parties" value="D" <%=subscriptions.get("all").contains("all") || subscriptions.get("party").contains("D") ? "checked=\"yes\"" : ""%>></input></td>
			<td>Democratic</td>
			<td><input type="checkbox" name="parties" value="R" <%=subscriptions.get("all").contains("all") || subscriptions.get("party").contains("R") ? "checked=\"yes\"" : ""%>></input></td>
			<td>Republican</td>
		</tr>
		<tr>
			<td><input type="checkbox" name="parties" value="IP" <%=subscriptions.get("all").contains("all") || subscriptions.get("party").contains("IP") ? "checked=\"yes\"" : ""%>></input></td>
			<td>Independence</td>
			<td><input type="checkbox" name="parties" value="C" <%=subscriptions.get("all").contains("all") || subscriptions.get("party").contains("C") ? "checked=\"yes\"" : ""%>></input></td>
			<td>Conservative</td>
			<td><input type="checkbox" name="parties" value="WF" <%=subscriptions.get("all").contains("all") || subscriptions.get("party").contains("WF") ? "checked=\"yes\"" : ""%>></input></td>
			<td>Working Families</td>
		</tr>
	</table>
    <br/>
    <div style="position:relative;right:-7px;">
		<table>
			<tr>
			<% if (senators != null) {
			    int i = 0;
				for (BillBuzzSenator senator : senators) {
				    if (i % 4 == 0 && i != 0) {
			%></tr>
			<tr><%
		            }
                    i++;
                    
                    String partyClasses = "";
                    boolean checked = subscriptions.get("all").contains("all") || subscriptions.get("sponsor").contains(senator.getShortName());
                    for (BillBuzzParty party : senator.getParties()) {
                        if (subscriptions.get("party").contains(party.getId())) {
                            checked = true;
                        }
                        partyClasses += " "+party.getId();
                    }
                    %><td>
					   <div class="senator">
					       <input type="checkbox" name="senators" class="<%=partyClasses%>" value="<%=senator.getShortName()%>" <%=checked ? "checked=\"yes\"" : "" %>></input>
					   </div>
			        </td>
			        <td><%=senator.getName()%></td>
			        <%
					}
			    }
				%>
			</tr>
		
			<%%>
			<tr></tr>
			<tr>
				<td colspan=8>
					<p>Would you like to receive updates for bills without an individual sponsor?
                    <select name="other">
                        <option <%=subscriptions.get("other").contains("other")?"SELECTED":"" %>>Yes</option>
                        <option <%=!subscriptions.get("other").contains("other")?"SELECTED":"" %>>No</option>
                    </select>
					<br/><i>This primarily refers to the budget bill and bills introduced collectively by the rules committee.</i>
					</p>
				</td>
			</tr>
		</table>

		<div style="position:right;right:250px;">
		    <input type="button" name="clear" value="Clear Selection"></input>
			<input type="submit" id="process" name="submit" value="Sign up"></input>
		</div>
		<p></p>
	</div>
</form>
</div>
<%@ include file="/WEB-INF/parts/footer.jsp"%>

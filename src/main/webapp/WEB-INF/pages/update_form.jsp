<%@ page contentType="text/html" pageEncoding="UTF-8" language="java" import="java.util.*,gov.nysenate.billbuzz.model.*"%>
<jsp:include page="/WEB-INF/parts/header.jsp">
<jsp:param value="update" name="page"/>
</jsp:include>
<%
String email = request.getParameter("email");
String email2 = request.getParameter("email2");
String lastName = request.getParameter("lastName");
String firstName = request.getParameter("firstName");
String message = (String)request.getAttribute("message");
BillBuzzConfirmation confirmation = (BillBuzzConfirmation)request.getAttribute("confirmation");
BillBuzzUser user = (BillBuzzUser)request.getAttribute("user");

@SuppressWarnings("unchecked")
List<BillBuzzSenator> senators = (List<BillBuzzSenator>)request.getAttribute("senators");

@SuppressWarnings("unchecked")
Map<String, Set<String>> subscriptions = (Map<String, Set<String>>)request.getAttribute("subscriptions");

if (user != null) {
    if (firstName == null) { firstName = user.getFirstName(); }
    if (lastName == null) { lastName = user.getLastName(); }
    if (email == null) {
        email = user.getEmail();
        email2 = user.getEmail();
    }
}
%>
<% if (message.equals("instruction")) { %>
<div class="bb_main instruction">
The form below has been filled with your current subscription preferences. Just modify them as necessary and submit them
to update your account.
</div>
<% } else if (message.equals("missing_userinfo")) { 
    if (firstName == null || email == null) { %>
        <div class="bb_main error">
        Email and first name are required when registering an account with BillBuzz.
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
That's it, your subscription has been updated to reflect the preferences checked displayed below.
</div> 
<% } %>
<br/>
<div class="bb_main">
<form id="subscriptionForm" method="POST" action="<%=request.getContextPath()%>/update/form">
<input type="hidden" name="key" value="<%=(confirmation == null ? "" : confirmation.getCode())%>" />
<br>
    <table>
        <tr>
            <td>* First name</td>
            <td><input type="text" name="firstName" value="<%=((firstName == null) ? "" : firstName)%>"></input></td>
        </tr>
        <tr>
            <td>&nbsp;&nbsp;Last name</td>
            <td><input type="text" name="lastName" value="<%=((lastName == null) ? "" : lastName)%>"></input></td>
        </tr>
        <tr>
            <td>* Email</td>
            <td><input type="text" name="email" value="<%=((email == null) ? "" : email)%>"></input></td>
        </tr>
        <tr>
            <td>* Email (confirm)</td>
            <td><input type="text" name="email2" value="<%=((email2 == null) ? "" : email2)%>"></input></td>
        </tr>
    </table>
    <br/>
    <table>
        <tr>
            <td><input type="checkbox" name="all" value="all" <%=subscriptions.get("all").contains("all") ? "checked=\"yes\"" : ""%>></input></td>
            <td>Subscribe to all comments on senate legislation.</td>
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
                    %><td>
                       <div class="senator">
                           <input type="checkbox" name="senators" value="<%=senator.getShortName()%>" <%=subscriptions.get("all").contains("all") || subscriptions.get("sponsor").contains(senator.getShortName()) ? "checked=\"yes\"" : "" %>></input>
                       </div>
                    </td>
                    <td><%=senator.getName()%></td>
                    <%
                    }
                }
                %>
            </tr>
        </table>
        <p></p>
        <table>
            <tr>
                <td><input type="checkbox" name="other" value="other" <%=subscriptions.get("all").contains("all") || subscriptions.get("other").contains("other") ? "checked=\"yes\"" : ""%>></input></td>
                <td>Receive updates for budget bills and legislation from the Rules committee?</td>
            </tr>
        </table>
        <p></p>
        <div style="position:right;right:250px;">
            <input type="button" name="clear" value="Clear Selection"></input>
            <input type="submit" id="process" name="submit" value="Update Subscription"></input>
        </div>
        <p></p>
    </div>
</form>
</div>
<%@ include file="/WEB-INF/parts/footer.jsp"%>

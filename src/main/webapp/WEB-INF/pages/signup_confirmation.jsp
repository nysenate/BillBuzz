<%@ page contentType="text/html" pageEncoding="UTF-8" language="java" import="java.util.*,gov.nysenate.billbuzz.model.*"%>
<jsp:include page="/WEB-INF/pages/header.jsp">
<jsp:param value="signup" name="page"/>
</jsp:include>
<div class="bb_main">
<%
String message = (String)request.getAttribute("message");
if (message.equals("success")) { %>
Success!  Thanks for signing up.
<% } else if (message.equals("invalid")) { %>
Sorry, your confirmation key seems to be invalid. Please try signing up again or email billbuzz@nysenate.gov for support.
<% } else if (message.equals("activated")) { %>
You've already activated your account! Everything is good to go.
<% } else if (message.equals("reactivate")) { %>
You just reactivated your account. 
<% } %>
</div>
<%@ include file="/WEB-INF/pages/footer.jsp"%>
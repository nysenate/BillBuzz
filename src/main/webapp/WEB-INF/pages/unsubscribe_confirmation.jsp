<%@ page contentType="text/html" pageEncoding="UTF-8" language="java" import="java.util.*,gov.nysenate.billbuzz.model.*"%>
<jsp:include page="/WEB-INF/pages/header.jsp">
<jsp:param value="unsubscribe" name="page"/>
</jsp:include>
<div class="bb_main">
<%
String message = (String)request.getAttribute("message");
if (message.equals("success")) { %>
You've been unsubscribed from BillBuzz and should not receive any more email.
<% } else if (message.equals("invalid")) { %>
Sorry, your confirmation key seems to be invalid. Please try signing up again or email billbuzz@nysenate.gov for support.
<% } else if (message.equals("inactive")) { %>
You're account has already been unsubscribed.
<% } %>
</div>
<%@ include file="/WEB-INF/pages/footer.jsp"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" language="java" import="java.util.*,gov.nysenate.billbuzz.model.*"%>
<jsp:include page="/WEB-INF/pages/header.jsp">
<jsp:param value="unsubscribe" name="page"/>
</jsp:include>
<%
String message = (String)request.getAttribute("message");
if (message.equals("success")) { %>
<div class="bb_main success">
You've been unsubscribed from BillBuzz and should not receive any more email.
</div>
<% } else if (message.equals("invalid")) { %>
<div class="bb_main error">
Sorry, your confirmation key seems to be invalid. Please try signing up again or email billbuzz@nysenate.gov for support.
</div>
<% } else if (message.equals("inactive")) { %>
<div class="bb_main error">
You're account has already been unsubscribed.
</div>
<% } %>
<%@ include file="/WEB-INF/pages/footer.jsp"%>
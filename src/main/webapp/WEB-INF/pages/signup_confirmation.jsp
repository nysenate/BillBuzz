<%@ page contentType="text/html" pageEncoding="UTF-8" language="java" import="java.util.*,gov.nysenate.billbuzz.model.*"%>
<jsp:include page="/WEB-INF/parts/header.jsp">
<jsp:param value="signup" name="page"/>
</jsp:include>
<%
String message = (String)request.getAttribute("message");
if (message.equals("success")) { %>
<div class="bb_main success">
Success!  Thanks for signing up.
</div>
<% } else if (message.equals("invalid")) { %>
<div class="bb_main error">
Sorry, your confirmation key seems to be invalid. Please try signing up again or email billbuzz@nysenate.gov for support.
</div>
<% } else if (message.equals("activated")) { %>
<div class="bb_main success">
You've already activated your account! Everything is good to go.
</div>
<% } else if (message.equals("reactivate")) { %>
<div class="bb_main success">
You just reactivated your account.
</div> 
<% } %>
<%@ include file="/WEB-INF/parts/footer.jsp"%>
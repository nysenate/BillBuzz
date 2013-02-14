<%@ page language="java" %>
<jsp:include page="header.jsp" />
<div id="main">
<center>

<%
	String message = (String)session.getAttribute("message");
	if(message != null) {
		out.write(message);
		session.setAttribute("message", null);
	}
	else {
		response.sendRedirect("index.jsp");
	}


%>

</center>
</div>
<%@ include file="footer.jsp"%>
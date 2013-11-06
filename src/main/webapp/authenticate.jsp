<%@ page language="java"%>
<%-- This page preserves backwards compatibility of old subscription confirmation links. --%>
<%
String key = request.getParameter("key");
response.sendRedirect(request.getContextPath()+"/signup/confirmation?key="+key);
%>
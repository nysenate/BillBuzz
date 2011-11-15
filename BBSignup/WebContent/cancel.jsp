<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%
	session.setAttribute("subs", null);
	session.setAttribute("fn", null);
	session.setAttribute("ln", null);
	session.setAttribute("e", null);
	session.setAttribute("oldemail", null);
	session.setAttribute("update", null);
	
%>

<jsp:forward page="/" />
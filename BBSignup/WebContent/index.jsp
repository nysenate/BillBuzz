<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.util.*,bbsignup.src.*,bbsignup.model.*,javax.jdo.*"
    %>    
<%
	Controller c = new Controller();
	List<Senator> list = c.getSenators();
	session.setAttribute("senators",list);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<link rel="stylesheet" type="text/css" media="screen" href="style.css"/> 

<script type="text/javascript">	
	function check(field, param) {
		for(i = 0; i < field.length; i++) {
			field[i].checked=true;
		}
	}	
	function uncheck(field) {
		for(i = 0; i < field.length; i++) {
			field[i].checked=false;
		}
	}
	function doCheck(field,t1,t2) {
		if(document.senators.control[t1].checked == true) {
			check(field);
		}
		else {
			if(document.senators.control[t2].checked !=true) {
				uncheck(field);
			}		
		}
	}
</script>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sign up for BillBuzz!</title>
</head>
<body>

<center>
<%
	String serr = (String)session.getAttribute("error");
	String fn = (String)session.getAttribute("fn");
	String ln = (String)session.getAttribute("ln");
	String e1 = (String)session.getAttribute("e1");
	String e2 = (String)session.getAttribute("e2");
	List<String> subs = (List<String>)session.getAttribute("subs");
	if(serr != null) {
		
		if(serr.equals("update") && fn != null) {
			%>
				<div class="good" style="font-size:85%;width:40%;">
				<table>
					<tr>
						<td>
							Changes will be saved when you click Submit.
						</td>
					</tr>
				</table>
				</div>
			<%
			session.setAttribute("update","true");
		}
		else {			
			%>
				<div class="bad" style="font-size:85%;width:40%;">
				<table>
					<tr>
						<td>
							It appears there was an error with your request, please review the following items:<br/>
							<ul><%=serr %></ul>
						</td>
					</tr>
				</table>
				</div>
			<%
			
		}		
		
		
		
		session.setAttribute("fn",null);
		session.setAttribute("ln",null);
		session.setAttribute("e1",null);
		session.setAttribute("e2",null);
		session.setAttribute("subs",null);
		session.setAttribute("error", null);
		
	}
	
%>

<form name="senators" method="post" action="process.jsp">


<h2>What is BillBuzz?</h2>

<div class="main">
<table>
	<tr>
		<td>
		<div style="width:650px;">BillBuzz is a new service available to the Senate that allows Senators and Senate staff to easily
		see what constituents are saying about legislation.<p>
		
		It scans the Senate's <a href="http://open.nysenate.gov/legislation">OpenLegislation</a> website for comments left by
		visitors on bills. Subscribers to BillBuzz are sent daily emails that pull in recent comments made
		on a Senator's sponsored legislation. Senators and staff can sign up to receive the latest 'buzz'
		around one or more Senator's sponsored legislation. <p>
		
		To sign up simply fill out the form below!
		</div>
		</td>
	</tr>
</table>
</div>

<h2 style="left:-385px">Sign Up</h2>
<p></p>
<div class="main">
<table>
	<tr>
		<td><br></br>First name</td>
		<td><br></br><input type="text" name="firstname" value="<%=((fn==null) ? "":fn)%>"></input></td>
	</tr>
	<tr>
		<td>Last name</td>
		<td><input type="text" name="lastname" value="<%=((ln==null) ? "":ln)%>"></input></td>
	</tr>
	<tr>
		<td>Email</td>
		<td><input type="text" name="email1" value="<%=((e1==null) ? "":e1)%>"></input></td>
	</tr>
	<tr>
		<td>Confirm email</td>
		<td><input type="text" name="email2" value="<%=((e2==null) ? "":e2)%>"></input></td>
	</tr>
</table>

<br/><br/>
<table>
	<tr>
		<td><input type="checkbox" value="all" name="control" onClick="doCheck(document.senators.d,0,1);doCheck(document.senators.r,0,2);"></input></td>
		<td>All</td>
		<td><input type="checkbox" value ="d" name="control" onClick="doCheck(document.senators.d,1,0)"></input></td>
		<td>Democrats</td>
		<td><input type="checkbox" value="r" name="control" onClick="doCheck(document.senators.r,2,0)"></input></td>
		<td>Republicans</td>
	</tr>
</table>
<br/>
<table cellpadding=3>
<%
	
	%>
		<tr>
	
	<%
	int i = 0;
	boolean tog = false;
	for(Senator s:list) {
		if(i%4 == 0 && i != 0) {
			%> </tr><tr><%
		}
		%>
		<td>
			<%
				if(subs != null) {
					if(subs.contains("all"))
						tog = true;
					else if(subs.contains("dem") && s.getParty().equals("d"))
						tog = true;
					else if(subs.contains("rep") && s.getParty().equals("r"))
						tog = true;
					else if(subs.contains(s.getName()))
						tog = true;
				}	
			%>
			<input type="checkbox" name="<%=s.getParty()%>" value="<%=s.getName()%>" <%=((tog == true)? "checked=\"yes\"":"")%>></input>
			<%
				tog = false;
			%>
		</td>
		
		<td>
			<a href="<%=s.getUrl()%>"><%=s.getName()%></a> (<%=s.getParty().toUpperCase()%>)
		</td>
		
		<%
		
		i++;
	}


	%>
	</tr>

	<%

%>
	<tr></tr>
	<tr></tr>
	<tr>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td><input type="button" name="clear" value="Clear Selection" onClick="uncheck(document.senators.r);uncheck(document.senators.d);uncheck(document.senators.control);"></input></td>
		<td></td>
		<td><input type="submit" name="submit" value="Submit"></input></td>
	</tr>

</table>
</div>
</form>

<%@ include file="footer.jsp" %>


</center>
</body>
</html>
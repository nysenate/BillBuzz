<%@ page contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" language="java" import="gov.nysenate.billbuzz.servlets.SenatorContext,java.util.*,gov.nysenate.billbuzz.src.*,gov.nysenate.billbuzz.model.Senator"%>
	<jsp:include page="header.jsp" />
<%
    List<Senator> list = (List<Senator>)SenatorContext.getSenators(this.getServletContext());;
%>
<center>
<div id="main">
<%
    String update = (String) session.getAttribute("update");
	String fn = (String) session.getAttribute("fn");
	String ln = (String) session.getAttribute("ln");
	String e1 = (String) session.getAttribute("e");
	String otherData = (String) session.getAttribute("otherData");
	List<String> subs = (List<String>) session.getAttribute("subs");
	
	if(otherData == null) {
		otherData ="";
	}
	
	if (update != null) {

		if (update.equals("update")) {
%>
<div class="good" style="font-size: 85%; width: 40%;">
<table>
	<tr>
		<td>Changes will be saved when you click Submit.</td>
	</tr>
	<tr>
		<td align=center><a href="cancel.jsp">Click here to cancel your update</a></td>
	</tr>
</table>
</div>
<%
    session.setAttribute("update", "update");
		} 
	}
	else {
		session.setAttribute("fn", null);
		session.setAttribute("ln", null);
		session.setAttribute("e", null);
		session.setAttribute("update", null);
		session.setAttribute("subs", null);
	}
	if(subs != null && subs.size() == 0) {
%>
		<div class="bad" style="font-size: 85%; width: 40%;">
		<table>
			<tr>
				<td>You didn't choose any Senators!<br />
				</td>
			</tr>
		</table>
		</div>
		<%
		    }
		%>

<form id="inputForm" name="senators" method="post"
	action="subscribe">
<h2>What is BillBuzz?</h2>

<div class="bb_main">
<table>
	<tr>
		<td>
		<div style="width: 650px;">BillBuzz is a new service that allows
		Senators, Senate staff and constituents to easily see what is being
		said about legislation.
		<p>BillBuzz scans the Senate's <a
			href="http://open.nysenate.gov/legislation">OpenLegislation</a>
		website for comments left by visitors on bills. Subscribers to
		BillBuzz are sent daily emails that pull in recent comments made on a
		Senator's sponsored legislation. You can sign up to receive the latest
		'buzz' around one or more Senator's sponsored legislation.
		<p>To sign up simply fill out the form below and select parties or
		individuals that you would like to receive updates for!
		</div>
		</td>
	</tr>
</table>
</div>
	<div id ="error" class="bad" style="font-size: 85%; width: 40%;display:none;"></div>

<h2>Sign Up</h2>
<p></p>
<div class="bb_main"><br>
<table>
	<tr>
		<td>First name</td>
		<td><input type="text" name="firstname"
			value="<%=((fn == null) ? "" : fn)%>"></input></td>
	</tr>
	<tr>
		<td>Last name</td>
		<td><input type="text" name="lastname"
			value="<%=((ln == null) ? "" : ln)%>"></input></td>
	</tr>
	<tr>
		<td>Email</td>
		<td><input type="text" name="email1"
			value="<%=((e1 == null) ? "" : e1)%>"></input></td>
	</tr>
	<tr>
		<td>Confirm email</td>
		<td><input type="text" name="email2"
			value="<%=((e1 == null) ? "" : e1)%>"></input></td>
	</tr>
</table>

<br />
<table>
	<tr>
		<td><input id="cb_all" name="cb_all" type="checkbox"></input></td>
		<td>All</td>
		<td><input class="cb_" party="D" type="checkbox"></input></td>
		<td>Democratic</td>
		<td><input class="cb_" party="R" type="checkbox"></input></td>
		<td>Republican</td>
	</tr>
	<tr>
		<td><input class="cb_" party="IP" type="checkbox"></input></td>
		<td>Independence</td>
		<td><input class="cb_" party="C" type="checkbox"></input></td>
		<td>Conservative</td>
		<td><input class="cb_" party="WF" type="checkbox"></input></td>
		<td>Working Families</td>
		

	</tr>
</table>
<br />
<div style="position:relative;right:-7px;">
<table cellpadding=3>
	<%%>
	<tr>

		<%
		    int i = 0;
					boolean tog = false;
					for (Senator s : list) {
						if (i % 4 == 0 && i != 0) {
		%>
	</tr>
	<tr>
		<%
			}
		%>

		<td>
		<%
			if (subs != null) {
					if (subs.contains("all"))
						tog = true;
					else {
						if(subs.contains(s.getOpenLegName())) {
							tog = true;
						}
						else {
							tog = false;
						}
					}
				}
		%>
		<div class="senator"><input class="sen_" type="checkbox"
			name="<%=s.getOpenLegName()%>"
			<%=((tog == true) ? "checked=\"yes\"" : "")%>></input></div>
		<%
			tog = false;
		%>
		</td>

		<td><a target="_blank" href="<%=s.getUrl()%>"><%=s.getName()%></a>
		<div class="party" style="font-size: 75%;" pl="(<%=s.getParty().toUpperCase()%>)"></div>
		</td>

		<%
			i++;
			}
		%>
	</tr>

	<%%>
	<tr></tr>
	<tr>
		<td colspan=8>
			<p>Would you like to receive updates for data where the sponsor can't be determined?
				<select name="otherData">
					<option <%=otherData.equals("yes")?"SELECTED":"" %>>Yes</option>
					<option <%=otherData.equals("no")?"SELECTED":"" %>>No</option>
				</select>
			</p>
		</td>
		<td></td>
		<td></td>
	</tr>
</table>

<div style="position:right;right:250px;"><input type="button" name="clear" value="Clear Selection"
			onClick="clearAll()"></input>
		<input type="submit" id="process" name="submit" value="Sign up"></input></td><div>
</div>
</div>
</form>

</div>
</center>
<%@ include file="footer.jsp"%>

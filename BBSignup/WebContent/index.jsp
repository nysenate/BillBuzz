<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="servlets.SenatorContext,java.util.*,bbsignup.src.*,bbsignup.model.Senator,javax.jdo.*"%>
<%
	
	List<Senator> list = (List<Senator>)SenatorContext.getSenators(this.getServletContext());;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<link rel="stylesheet" type="text/css" media="screen" href="style.css" />
<script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.js"></script>
<script type="text/javascript">

function addError(message, msg) {
	if(message == "") {
		return msg;
	}
	else {
		return message += "<br>" + msg;
	}
}
	$(document).ready(
		function() {
			/*reset = function() {
				$('.cb_').each(function() {
					doCheck(this, $(this).attr('party'));
				});
			};*/

			doCheck = function(curSelector, party) {
				if (!$('#cb_all').is(':checked')) {
					$('.senator').each(function(index) {
						var parties = $($('.party').get(index)).html();

						var check = $(this).children("INPUT[type='checkbox']");

						var re = new RegExp('(\\(|\\- )' + party
								+ '(\\)| \\-)');
						if (parties.match(re)) {
							$(check).attr('checked',
									$(curSelector).is(':checked'));
						}
					});
				}
			};

			clearAll = function() {
				$("INPUT[type='checkbox']").attr('checked', false);
			};

			$('#cb_all').change(
					function() {
						$("INPUT[type='checkbox']").attr('checked',
							$('#cb_all').is(':checked'));
					});

			$('.cb_').change(function() {
				doCheck(this, $(this).attr('party'));
				//reset();
			});

			$('#process').click(function(event) {
				
				
				message = "";
				e1 = document.forms.senators.email1.value;
				e2 = document.forms.senators.email2.value;
				fn = document.forms.senators.firstname.value;
				ln = document.forms.senators.lastname.value;
				
				if(!fn){
					message = addError(message, "Enter your first name");
				}
				if(!ln) {
					message = addError(message, "Enter your last name");
				}
				if(e1 == null || e2 == null || e1 != e2) {
					message = addError(message, "Your email addresses must match!");
				}
				else {
					if(!e1.match(/.*?@.*?\..*?/)) {
						message = addError(message, "Enter a valid email address");
					}
				}
				
				if(message != "") {
					$("#error").html(message);
					$("#error").css({'display' : 'inherit'});
					$('html,body').animate({
						scrollTop:$("#error").offset().top}, 500);
					return false;
				}
				else {
					return true;

				}
				
			});

		});

</script>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sign up for BillBuzz!</title>
</head>
<body>

<center>
<%
	String update = (String) session.getAttribute("update");
	String fn = (String) session.getAttribute("fn");
	String ln = (String) session.getAttribute("ln");
	String e1 = (String) session.getAttribute("e");
	List<String> subs = (List<String>) session.getAttribute("subs");
	if (update != null) {

		if (update.equals("update")) {
%>
<div class="good" style="font-size: 85%; width: 40%;">
<table>
	<tr>
		<td>Changes will be saved when you click Submit.</td>
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

<div class="main">
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

<h2 style="left: -385px">Sign Up</h2>
<p></p>
<div class="main"><br>
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
		<td>Democrat</td>

		<td><input class="cb_" party="R" type="checkbox"></input></td>
		<td>Republican</td>

		<td><input class="cb_" party="C" type="checkbox"></input></td>
		<td>Conservative</td>
	</tr>
	<td><input class="cb_" party="WF" type="checkbox"></input></td>
	<td>Working Family</td>

	<td><input class="cb_" party="IND" type="checkbox"></input></td>
	<td>Independent</td>

	<td><input class="cb_" party="IP" type="checkbox"></input></td>
	<td>Independence Party</td>

	<td><input class="cb_" party="I" type="checkbox"></input></td>
	<td>Independent Party</td>
	</tr>
</table>
<br />
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
		<div class="senator"><input type="checkbox"
			name="<%=s.getOpenLegName()%>"
			<%=((tog == true) ? "checked=\"yes\"" : "")%>></input></div>
		<%
			tog = false;
		%>
		</td>

		<td><a target="_blank" href="<%=s.getUrl()%>"><%=s.getName()%></a>
		<div class="party" style="font-size: 75%;">(<%=s.getParty().toUpperCase()%>)</div>
		</td>

		<%
			i++;
			}
		%>
	</tr>

	<%%>
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
		<td><input type="button" name="clear" value="Clear Selection"
			onClick="clearAll()"></input></td>
		<td></td>
		<td><input type="submit" id="process" name="submit"></input></td>
	</tr>

</table>
</div>
</form>

<%@ include file="footer.jsp"%></center>
</body>
</html>
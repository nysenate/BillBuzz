<%@ page language="java" import="java.util.*" %>
<jsp:include page="/WEB-INF/pages/header.jsp">
    <jsp:param value="update" name="page"/>
</jsp:include>
<center>
<div class="good" style="width:500px;">
	<form name="update" method="post" action="">
		<table>
			<tr>
				<td colspan = 2 align=center>
					Please enter your email address:
				</td>
			</tr>
			<tr>
				<td colspan=2 align=center>
					<input type="text" name="uemail"></input>
					<input type="submit" name="updatesubmit" value="Update"></input>
				</td>
			</tr>
			<tr>
				<td colspan = 2>
					NOTE: You will receive an email that will allow you to update your settings.<br/>
				</td>
			</tr>
		</table>
	</form>
	</div>
</div>
</center>
<%@ include file="/WEB-INF/pages/footer.jsp"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" language="java" import="java.util.*,gov.nysenate.billbuzz.model.*"%>
<jsp:include page="/WEB-INF/parts/header.jsp">
<jsp:param value="error" name="page"/>
</jsp:include>
<div class="bbmain error" style="text-align:left; padding:10px 40px; border: 1px solid #555;">
    We are sorry but there has been an issue processing your request. Site administrators have been notified and will look into the problem.
    Try again later or email support at <a href="mailto:billbuzz@nysenate.gov">billbuzz@nysenate.gov</a> for assistance.
</div>
<%@ include file="/WEB-INF/parts/footer.jsp"%>
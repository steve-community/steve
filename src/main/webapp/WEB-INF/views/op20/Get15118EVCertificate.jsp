<%@ include file="../00-header.jsp" %>
<%@ include file="../00-op-bind-errors.jsp" %>

<div class="content">
<jsp:include page="00-menu.jsp">
    <jsp:param name="menuItem" value="Get15118EVCertificate"/>
</jsp:include>
<div class="op20-content">
<%@ include file="../op-forms/ocpp20/Get15118EVCertificateForm.jsp" %>
</div></div>
<%@ include file="../00-footer.jsp" %>

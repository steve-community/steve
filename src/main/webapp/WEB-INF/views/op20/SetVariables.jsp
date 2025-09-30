<%@ include file="../00-header.jsp" %>
<%@ include file="../00-op-bind-errors.jsp" %>

<div class="content">
<jsp:include page="00-menu.jsp">
    <jsp:param name="menuItem" value="SetVariables"/>
</jsp:include>
<div class="op20-content">
<%@ include file="../op-forms/ocpp20/SetVariablesForm.jsp" %>
</div></div>
<%@ include file="../00-footer.jsp" %>

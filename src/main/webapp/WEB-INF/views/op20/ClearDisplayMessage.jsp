<%--
    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2025 SteVe Community Team
    All Rights Reserved.
--%>
<%@ include file="../00-header.jsp" %>
<%@ include file="../00-op-bind-errors.jsp" %>
<div class="content">
<jsp:include page="00-menu.jsp">
    <jsp:param name="menuItem" value="ClearDisplayMessage"/>
</jsp:include>
<div class="op20-content">
<%@ include file="../op-forms/ocpp20/ClearDisplayMessageForm.jsp" %>
</div></div>
<%@ include file="../00-footer.jsp" %>
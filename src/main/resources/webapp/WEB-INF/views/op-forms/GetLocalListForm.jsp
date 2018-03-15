<form:form action="${ctxPath}/manager/operations/${opVersion}/GetLocalListVersion" modelAttribute="params">
    <section><span>Charge Points with OCPP ${opVersion}</span></section>
    <%@ include file="../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <center><i>No parameters required.</i></center><div class="submit-button"><input type="submit" value="Perform"></div>
</form:form>
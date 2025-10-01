<form:form action="${ctxPath}/manager/operations/v2.0/ClearVariableMonitoring" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Monitoring IDs (comma-separated):</td>
            <td><form:input path="monitoringIds" placeholder="1,2,3"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

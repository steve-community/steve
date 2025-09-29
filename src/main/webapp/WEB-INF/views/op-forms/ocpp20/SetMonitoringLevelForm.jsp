<form:form action="${ctxPath}/manager/operations/v2.0/SetMonitoringLevel" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Severity Level:</td>
            <td><form:input path="severity" placeholder="0-9 (0=Danger, 9=Debug)"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

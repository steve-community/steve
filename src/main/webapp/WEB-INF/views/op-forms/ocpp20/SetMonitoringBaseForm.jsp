<form:form action="${ctxPath}/manager/operations/v2.0/SetMonitoringBase" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Monitoring Base:</td>
            <td><form:input path="monitoringBase" placeholder="All|FactoryDefault|HardWiredOnly"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

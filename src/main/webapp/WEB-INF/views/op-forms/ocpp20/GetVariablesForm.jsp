<form:form action="${ctxPath}/manager/operations/v2.0/GetVariables" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Component Name:</td>
            <td><form:input path="componentName" placeholder="ChargingStation"/></td>
        </tr>
        <tr>
            <td>Variable Name:</td>
            <td><form:input path="variableName" placeholder="AvailabilityState"/></td>
        </tr>
        <tr>
            <td>Attribute Type (optional):</td>
            <td><form:input path="attributeType" placeholder="Actual"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

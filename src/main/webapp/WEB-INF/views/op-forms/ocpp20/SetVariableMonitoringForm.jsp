<form:form action="${ctxPath}/manager/operations/v2.0/SetVariableMonitoring" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Component Name:</td>
            <td><form:input path="setMonitoringData[0].component.name" placeholder="e.g., ChargingStation"/></td>
        </tr>
        <tr>
            <td>Component Instance (optional):</td>
            <td><form:input path="setMonitoringData[0].component.instance" placeholder="e.g., 1"/></td>
        </tr>
        <tr>
            <td>EVSE ID (optional):</td>
            <td><form:input path="setMonitoringData[0].component.evse.id" placeholder="e.g., 1"/></td>
        </tr>
        <tr>
            <td>Variable Name:</td>
            <td><form:input path="setMonitoringData[0].variable.name" placeholder="e.g., Available"/></td>
        </tr>
        <tr>
            <td>Variable Instance (optional):</td>
            <td><form:input path="setMonitoringData[0].variable.instance" placeholder="e.g., 1"/></td>
        </tr>
        <tr>
            <td>Monitor Type:</td>
            <td><form:input path="setMonitoringData[0].type" placeholder="UpperThreshold|LowerThreshold|Delta|Periodic|PeriodicClockAligned"/></td>
        </tr>
        <tr>
            <td>Value:</td>
            <td><form:input path="setMonitoringData[0].value" placeholder="Monitor value"/></td>
        </tr>
        <tr>
            <td>Severity:</td>
            <td><form:input path="setMonitoringData[0].severity" placeholder="0-9"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

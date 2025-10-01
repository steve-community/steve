<form:form action="${ctxPath}/manager/operations/v2.0/SetVariableMonitoring" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Monitoring Entry</span></section>
    <table class="userInput">
        <tr>
            <td>Monitor ID (optional):</td>
            <td><form:input path="setMonitoringData[0].id" type="number" min="0" placeholder="Existing monitor ID"/></td>
        </tr>
        <tr>
            <td>Active During Transaction:</td>
            <td><form:checkbox path="setMonitoringData[0].transaction"/></td>
        </tr>
        <tr>
            <td>Monitor Type (required):</td>
            <td><form:select path="setMonitoringData[0].type">
                <form:option value="">- Select monitor type -</form:option>
                <form:option value="UpperThreshold">Upper Threshold</form:option>
                <form:option value="LowerThreshold">Lower Threshold</form:option>
                <form:option value="Delta">Delta</form:option>
                <form:option value="Periodic">Periodic</form:option>
                <form:option value="PeriodicClockAligned">Periodic Clock Aligned</form:option>
            </form:select></td>
        </tr>
        <tr>
            <td>Value (required):</td>
            <td><form:input path="setMonitoringData[0].value" type="number" step="0.01" placeholder="Threshold / interval"/></td>
        </tr>
        <tr>
            <td>Severity (0-9):</td>
            <td><form:input path="setMonitoringData[0].severity" type="number" min="0" max="9" placeholder="5"/></td>
        </tr>
    </table>

    <section><span>Component</span></section>
    <table class="userInput">
        <tr>
            <td>Name (required):</td>
            <td><form:input path="setMonitoringData[0].component.name" placeholder="Component name"/></td>
        </tr>
        <tr>
            <td>Instance:</td>
            <td><form:input path="setMonitoringData[0].component.instance" placeholder="Component instance"/></td>
        </tr>
        <tr>
            <td>EVSE ID:</td>
            <td><form:input path="setMonitoringData[0].component.evseId" type="number" min="1" placeholder="Optional EVSE"/></td>
        </tr>
        <tr>
            <td>Connector ID:</td>
            <td><form:input path="setMonitoringData[0].component.connectorId" type="number" min="1" placeholder="Optional connector"/></td>
        </tr>
    </table>

    <section><span>Variable</span></section>
    <table class="userInput">
        <tr>
            <td>Name (required):</td>
            <td><form:input path="setMonitoringData[0].variable.name" placeholder="Variable name"/></td>
        </tr>
        <tr>
            <td>Instance:</td>
            <td><form:input path="setMonitoringData[0].variable.instance" placeholder="Variable instance"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

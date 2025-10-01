<form:form action="${ctxPath}/manager/operations/v2.0/GetMonitoringReport" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Request ID (required):</td>
            <td><form:input path="requestId" type="number" min="0" placeholder="Unique identifier"/></td>
        </tr>
        <tr>
            <td>Monitoring Criteria:</td>
            <td><form:select path="monitoringCriteria" multiple="true" size="3">
                <form:option value="ThresholdMonitoring">Threshold Monitoring</form:option>
                <form:option value="DeltaMonitoring">Delta Monitoring</form:option>
                <form:option value="PeriodicMonitoring">Periodic Monitoring</form:option>
            </form:select>
            <div class="hint">Hold Ctrl/Cmd to select multiple entries.</div></td>
        </tr>
    </table>

    <section><span>Component Filter (optional)</span></section>
    <table class="userInput">
        <tr>
            <td>Component Name:</td>
            <td><form:input path="componentVariable[0].component.name" placeholder="Component name"/></td>
        </tr>
        <tr>
            <td>Component Instance:</td>
            <td><form:input path="componentVariable[0].component.instance" placeholder="Instance"/></td>
        </tr>
        <tr>
            <td>EVSE ID:</td>
            <td><form:input path="componentVariable[0].component.evseId" type="number" min="1" placeholder="Optional"/></td>
        </tr>
        <tr>
            <td>Connector ID:</td>
            <td><form:input path="componentVariable[0].component.connectorId" type="number" min="1" placeholder="Optional"/></td>
        </tr>
        <tr>
            <td>Variable Name:</td>
            <td><form:input path="componentVariable[0].variable.name" placeholder="Variable name"/></td>
        </tr>
        <tr>
            <td>Variable Instance:</td>
            <td><form:input path="componentVariable[0].variable.instance" placeholder="Variable instance"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

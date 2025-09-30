<form:form action="${ctxPath}/manager/operations/v2.0/GetMonitoringReport" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Request ID (required):</td>
            <td><form:input path="requestId" placeholder="Unique identifier for this request"/></td>
        </tr>
        <tr>
            <td>Monitoring Criteria:</td>
            <td><form:select path="monitoringCriteria">
                <form:option value="">- Please choose a monitoring criteria -</form:option>
                <form:option value="ThresholdMonitoring">Threshold Monitoring</form:option>
                <form:option value="DeltaMonitoring">Delta Monitoring</form:option>
                <form:option value="PeriodicMonitoring">Periodic Monitoring</form:option>
            </form:select></td>
        </tr>
        <tr>
            <td>Component Variables:</td>
            <td><form:textarea path="componentVariable" placeholder="Optional: Specify component variables (JSON array)" rows="3"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

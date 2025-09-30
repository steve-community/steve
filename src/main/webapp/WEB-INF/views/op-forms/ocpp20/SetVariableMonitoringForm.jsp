<form:form action="${ctxPath}/manager/operations/v2.0/SetVariableMonitoring" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Set Monitoring Data (required):</td>
            <td><form:textarea path="setMonitoringData" placeholder="Set monitoring data configuration (JSON array)" rows="6"/></td>
        </tr>
        <tr>
            <td>Monitor Type:</td>
            <td><form:select path="monitorType">
                <form:option value="">- Please choose a monitor type -</form:option>
                <form:option value="UpperThreshold">Upper Threshold</form:option>
                <form:option value="LowerThreshold">Lower Threshold</form:option>
                <form:option value="Delta">Delta</form:option>
                <form:option value="Periodic">Periodic</form:option>
                <form:option value="PeriodicClockAligned">Periodic Clock Aligned</form:option>
            </form:select></td>
        </tr>
        <tr>
            <td>Monitor Value:</td>
            <td><form:input path="monitorValue" type="number" step="0.01" placeholder="Threshold or interval value"/></td>
        </tr>
        <tr>
            <td>Component:</td>
            <td><form:input path="component" placeholder="Component name"/></td>
        </tr>
        <tr>
            <td>Variable:</td>
            <td><form:input path="variable" placeholder="Variable name"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

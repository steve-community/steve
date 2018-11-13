<form:form action="${ctxPath}/manager/operations/${opVersion}/GetCompositeSchedule" modelAttribute="params">
    <section><span>Charge Points with OCPP ${opVersion}</span></section>
    <%@ include file="../00-cp-single.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Connector ID (integer):</td>
            <td><form:input path="connectorId" placeholder="0 = charge point as a whole"/></td>
        </tr>
        <tr><td>Duration (in seconds):</td><td><form:input path="durationInSeconds"/></td></tr>
        <tr><td>Charging Rate Unit:</td>
            <td>
                <form:select path="chargingRateUnit">
                    <option value="" selected>-- Empty --</option>
                    <form:options items="${chargingRateUnit}"/>
                </form:select>
            </td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>
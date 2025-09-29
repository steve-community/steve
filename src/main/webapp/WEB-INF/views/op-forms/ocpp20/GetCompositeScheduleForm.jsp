<form:form modelAttribute="params">
    <section><span>Charge Points</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>

    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Duration (seconds):</td>
            <td><form:input path="duration" placeholder="numeric value"/></td>
        </tr>
        <tr>
            <td>EVSE ID:</td>
            <td><form:input path="evseId" placeholder="numeric value"/></td>
        </tr>
        <tr>
            <td>Charging Rate Unit:</td>
            <td>
                <form:select path="chargingRateUnit">
                    <form:option value="" label="-- Select One --"/>
                    <form:option value="A" label="Amperes"/>
                    <form:option value="W" label="Watts"/>
                </form:select>
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <div class="submit-button"><input type="submit" value="Perform"></div>
            </td>
        </tr>
    </table>
</form:form>

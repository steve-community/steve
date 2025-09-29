<form:form action="${ctxPath}/manager/operations/v2.0/GetDisplayMessages" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Request ID:</td>
            <td><form:input path="requestId" placeholder="Enter request ID"/></td>
        </tr>
        <tr>
            <td>Message IDs (optional, comma-separated):</td>
            <td><form:input path="id" placeholder="1,2,3 (leave empty for all)"/></td>
        </tr>
        <tr>
            <td>Priority (optional):</td>
            <td><form:input path="priority" placeholder="AlwaysFront|InFront|NormalCycle"/></td>
        </tr>
        <tr>
            <td>State (optional):</td>
            <td><form:input path="state" placeholder="Charging|Faulted|Idle|Unavailable"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

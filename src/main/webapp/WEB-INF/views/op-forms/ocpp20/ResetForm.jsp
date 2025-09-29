<form:form action="${ctxPath}/manager/operations/v2.0/Reset" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Reset Type:</td>
            <td><form:input path="resetType" placeholder="Immediate|OnIdle"/></td>
        </tr>
        <tr>
            <td>EVSE ID (optional):</td>
            <td><form:input path="evseId" placeholder="1"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

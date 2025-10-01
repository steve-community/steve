<form:form action="${ctxPath}/manager/operations/v2.0/SendLocalList" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Version Number:</td>
            <td><form:input path="versionNumber" placeholder="Enter version number"/></td>
        </tr>
        <tr>
            <td>Update Type:</td>
            <td><form:input path="updateType" placeholder="Differential|Full"/></td>
        </tr>
        <tr>
            <td>Local Authorization List:</td>
            <td><form:textarea path="authorizationList" placeholder="Enter authorization list data (optional)"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>
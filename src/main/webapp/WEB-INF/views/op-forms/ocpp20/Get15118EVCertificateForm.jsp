<form:form action="${ctxPath}/manager/operations/v2.0/Get15118EVCertificate" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>15118 Schema Version:</td>
            <td><form:input path="iso15118SchemaVersion" placeholder="e.g., urn:iso:15118:2:2013:MsgDef"/></td>
        </tr>
        <tr>
            <td>Action:</td>
            <td><form:input path="action" placeholder="Install|Update"/></td>
        </tr>
        <tr>
            <td>eMA ID:</td>
            <td><form:input path="exiRequest" placeholder="Enter eMA ID"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

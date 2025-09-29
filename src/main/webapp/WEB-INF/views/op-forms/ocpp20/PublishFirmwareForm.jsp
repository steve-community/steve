<form:form action="${ctxPath}/manager/operations/v2.0/PublishFirmware" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Location:</td>
            <td><form:input path="location" placeholder="URL of firmware file"/></td>
        </tr>
        <tr>
            <td>MD5 Checksum:</td>
            <td><form:input path="checksum" placeholder="MD5 checksum of firmware"/></td>
        </tr>
        <tr>
            <td>Request ID:</td>
            <td><form:input path="requestId" placeholder="Enter request ID"/></td>
        </tr>
        <tr>
            <td>Retries (optional):</td>
            <td><form:input path="retries" placeholder="3"/></td>
        </tr>
        <tr>
            <td>Retry Interval (optional, seconds):</td>
            <td><form:input path="retryInterval" placeholder="60"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

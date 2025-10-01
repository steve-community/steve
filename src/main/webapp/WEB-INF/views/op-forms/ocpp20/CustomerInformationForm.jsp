<form:form action="${ctxPath}/manager/operations/v2.0/CustomerInformation" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Request ID:</td>
            <td><form:input path="requestId" placeholder="Enter request ID"/></td>
        </tr>
        <tr>
            <td>Report Base:</td>
            <td><form:radiobutton path="report" value="true"/> Yes
                <form:radiobutton path="report" value="false"/> No</td>
        </tr>
        <tr>
            <td>Clear Cache:</td>
            <td><form:radiobutton path="clear" value="true"/> Yes
                <form:radiobutton path="clear" value="false"/> No</td>
        </tr>
        <tr>
            <td>ID Token (optional):</td>
            <td><form:input path="idToken" placeholder="Enter customer ID token"/></td>
        </tr>
        <tr>
            <td>ID Token Type (optional):</td>
            <td><form:input path="idTokenType" placeholder="Central|eMAID|ISO14443|ISO15693|KeyCode|Local|MacAddress|NoAuthorization"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

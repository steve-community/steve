<form:form action="${ctxPath}/manager/operations/v2.0/ReserveNow" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Reservation ID:</td>
            <td><form:input path="id" placeholder="Enter reservation ID"/></td>
        </tr>
        <tr>
            <td>Expiry Date/Time:</td>
            <td><form:input path="expiryDateTime" cssClass="dateTimePicker" placeholder="Select expiry date/time"/></td>
        </tr>
        <tr>
            <td>ID Token:</td>
            <td><form:input path="idToken" placeholder="Enter ID token"/></td>
        </tr>
        <tr>
            <td>ID Token Type:</td>
            <td><form:input path="idTokenType" placeholder="Central|eMAID|ISO14443|ISO15693|KeyCode|Local|MacAddress|NoAuthorization"/></td>
        </tr>
        <tr>
            <td>EVSE ID (optional):</td>
            <td><form:input path="evseId" placeholder="1"/></td>
        </tr>
        <tr>
            <td>Group ID Token (optional):</td>
            <td><form:input path="groupIdToken" placeholder="Enter group ID token"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>
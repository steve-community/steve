<form:form action="${ctxPath}/manager/operations/v2.0/SetDisplayMessage" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Message ID:</td>
            <td><form:input path="message.id" placeholder="Enter message ID"/></td>
        </tr>
        <tr>
            <td>Priority:</td>
            <td><form:input path="message.priority" placeholder="AlwaysFront|InFront|NormalCycle"/></td>
        </tr>
        <tr>
            <td>Message Text:</td>
            <td><form:textarea path="message.message.content" rows="3" cols="60" placeholder="Enter message text"/></td>
        </tr>
        <tr>
            <td>Message Format (optional):</td>
            <td><form:input path="message.message.format" placeholder="ASCII|HTML|URI|UTF8"/></td>
        </tr>
        <tr>
            <td>Language (optional):</td>
            <td><form:input path="message.message.language" placeholder="en|de|fr|es"/></td>
        </tr>
        <tr>
            <td>State (optional):</td>
            <td><form:input path="message.state" placeholder="Charging|Faulted|Idle|Unavailable"/></td>
        </tr>
        <tr>
            <td>Start Date/Time (optional):</td>
            <td><form:input path="message.startDateTime" cssClass="dateTimePicker" placeholder="Select start date/time"/></td>
        </tr>
        <tr>
            <td>End Date/Time (optional):</td>
            <td><form:input path="message.endDateTime" cssClass="dateTimePicker" placeholder="Select end date/time"/></td>
        </tr>
        <tr>
            <td>Transaction ID (optional):</td>
            <td><form:input path="message.transactionId" placeholder="Enter transaction ID"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

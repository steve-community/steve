<form:form action="${ctxPath}/manager/operations/v2.0/SetDisplayMessage" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Message Details</span></section>
    <table class="userInput">
        <tr>
            <td>Message ID (required):</td>
            <td><form:input path="message.id" type="number" min="1" placeholder="Numeric message identifier"/></td>
        </tr>
        <tr>
            <td>Priority (required):</td>
            <td><form:select path="message.priority">
                <form:option value="">- Select a priority -</form:option>
                <form:option value="AlwaysFront">Always Front</form:option>
                <form:option value="InFront">In Front</form:option>
                <form:option value="NormalCycle">Normal Cycle</form:option>
            </form:select></td>
        </tr>
        <tr>
            <td>State (optional):</td>
            <td><form:select path="message.state">
                <form:option value="">- Any state -</form:option>
                <form:option value="Charging">Charging</form:option>
                <form:option value="Faulted">Faulted</form:option>
                <form:option value="Idle">Idle</form:option>
                <form:option value="Unavailable">Unavailable</form:option>
            </form:select></td>
        </tr>
        <tr>
            <td>Start Date/Time:</td>
            <td><form:input path="message.startDateTime" placeholder="2025-03-01T09:00:00Z"/></td>
        </tr>
        <tr>
            <td>End Date/Time:</td>
            <td><form:input path="message.endDateTime" placeholder="2025-03-01T17:00:00Z"/></td>
        </tr>
        <tr>
            <td>Transaction ID:</td>
            <td><form:input path="message.transactionId" maxlength="36" placeholder="Optional transaction ID"/></td>
        </tr>
    </table>

    <section><span>Message Content</span></section>
    <table class="userInput">
        <tr>
            <td>Format (required):</td>
            <td><form:select path="message.message.format">
                <form:option value="">- Select format -</form:option>
                <form:option value="ASCII">ASCII</form:option>
                <form:option value="HTML">HTML</form:option>
                <form:option value="URI">URI</form:option>
                <form:option value="UTF8">UTF-8</form:option>
            </form:select></td>
        </tr>
        <tr>
            <td>Language (optional):</td>
            <td><form:input path="message.message.language" placeholder="en-US" maxlength="8"/></td>
        </tr>
        <tr>
            <td>Content (required):</td>
            <td><form:textarea path="message.message.content" rows="4" placeholder="Message to display"/></td>
        </tr>
    </table>

    <section><span>Display Target</span></section>
    <table class="userInput">
        <tr>
            <td>Display Name:</td>
            <td><form:input path="message.display.name" placeholder="Optional display name"/></td>
        </tr>
        <tr>
            <td>EVSE ID:</td>
            <td><form:input path="message.display.evseId" type="number" min="1" placeholder="Optional EVSE"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

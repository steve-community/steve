<form:form action="${ctxPath}/manager/operations/v2.0/SetDisplayMessage" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Message (required):</td>
            <td><form:textarea path="message" placeholder="Display message content (JSON object)" rows="4"/></td>
        </tr>
        <tr>
            <td>Message ID:</td>
            <td><form:input path="messageId" placeholder="Unique identifier for this message"/></td>
        </tr>
        <tr>
            <td>Message Priority:</td>
            <td><form:select path="messagePriority">
                <form:option value="">- Please choose a priority -</form:option>
                <form:option value="AlwaysFront">Always Front</form:option>
                <form:option value="InFront">In Front</form:option>
                <form:option value="NormalCycle">Normal Cycle</form:option>
            </form:select></td>
        </tr>
        <tr>
            <td>Message State:</td>
            <td><form:select path="messageState">
                <form:option value="">- Please choose a state -</form:option>
                <form:option value="Charging">Charging</form:option>
                <form:option value="Faulted">Faulted</form:option>
                <form:option value="Idle">Idle</form:option>
                <form:option value="Unavailable">Unavailable</form:option>
            </form:select></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

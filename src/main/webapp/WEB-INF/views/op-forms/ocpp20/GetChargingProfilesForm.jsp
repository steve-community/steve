<form:form modelAttribute="params">
    <section><span>Charge Points</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>

    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Request ID:</td>
            <td><form:input path="requestId" placeholder="numeric value"/></td>
        </tr>
        <tr>
            <td>EVSE ID:</td>
            <td><form:input path="evseId" placeholder="numeric value (optional, 0 for grid connection)"/></td>
        </tr>
        <tr>
            <td></td>
            <td>
                <div class="submit-button"><input type="submit" value="Perform"></div>
            </td>
        </tr>
    </table>
</form:form>

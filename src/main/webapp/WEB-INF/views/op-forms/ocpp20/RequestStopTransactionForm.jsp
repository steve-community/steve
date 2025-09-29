<form:form action="${ctxPath}/manager/operations/v2.0/RequestStopTransaction" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Transaction ID:</td>
            <td><form:input path="transactionId" placeholder="transaction-id-123"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

<form:form action="${ctxPath}/manager/operations/v2.0/GetTransactionStatus" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Transaction ID (optional):</td>
            <td><form:input path="transactionId" placeholder="Enter transaction ID (leave empty for ongoing transaction)"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

<form:form action="${ctxPath}/manager/operations/v2.0/CostUpdated" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td>Total Cost (required):</td>
            <td><form:input path="totalCost" type="number" step="0.01" min="0" placeholder="Current total cost"/></td>
        </tr>
        <tr>
            <td>Transaction ID (required):</td>
            <td><form:input path="transactionId" maxlength="36" placeholder="Transaction identifier"/></td>
        </tr>
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

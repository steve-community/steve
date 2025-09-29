<form:form action="${ctxPath}/manager/operations/v2.0/ClearCache" modelAttribute="params">
    <section><span>Charge Points (OCPP 2.0)</span></section>
    <%@ include file="../../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <p><i>This command clears the authorization cache. No additional parameters needed.</i></p>
    <table class="userInput">
        <tr><td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td></tr>
    </table>
</form:form>

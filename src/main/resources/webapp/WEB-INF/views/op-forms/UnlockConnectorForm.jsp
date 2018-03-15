<form:form action="${ctxPath}/manager/operations/${opVersion}/UnlockConnector" modelAttribute="params">
    <section><span>Charge Points with OCPP ${opVersion}</span></section>
    <%@ include file="../00-cp-single.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr><td>Connector ID:</td>
            <td><form:select path="connectorId" disabled="true" /></td>
        </tr>
    </table>
    <div class="submit-button"><input type="submit" value="Perform"></div>
</form:form>
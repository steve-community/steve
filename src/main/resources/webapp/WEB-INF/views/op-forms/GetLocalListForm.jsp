<form:form action="${ctxPath}/manager/operations/${opVersion}/GetLocalListVersion" modelAttribute="params">
    <section><span>Charge Points with OCPP ${opVersion}</span></section>
    <%@ include file="../00-cp-multiple.jsp" %>
    <section><span>Parameters</span></section>
    <table class="userInput">
        <tr>
            <td></td><td><i>No parameters required.</i></td>
        </tr>
        <tr>
            <td></td><td><div class="submit-button"><input type="submit" value="Perform"></div></td>
        </tr>
    </table>
</form:form>